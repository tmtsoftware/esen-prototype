package org.tmt.esen.esenprototypehcd;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;


import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Provides client access to HCD's OPC UA server
 */
public class OpcUaHcdClient {
    //    private static final int NAMESPACE = 2;
    private static final int NAMESPACE = 4;
    private static final String NAMESPACE_PREFIX = "MAIN.";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    //private final KeyStoreLoader loader = new KeyStoreLoader();
    private final AtomicLong clientHandles = new AtomicLong(1L);
    private final OpcUaClient client;

    public OpcUaHcdClient() throws Exception {
        client = createClient();
        // synchronous connect
        client.connect().get();
    }

    private OpcUaClient createClient() throws Exception {
        SecurityPolicy securityPolicy = SecurityPolicy.None;

        // For some reason this does not work with TwinCat servers, but if the resolved Server Name is included in
        // the hosts file of the machine running this code, it works.
        EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints("opc.tcp://192.168.1.5:4840").get();
        for (EndpointDescription e: endpoints)
        {
            logger.info("Endpoint URL: {}", e.getEndpointUrl());
        }
        EndpointDescription endpoint = Arrays.stream(endpoints)
                .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

        logger.info("Using endpoint: {} [{}]", endpoint.getEndpointUrl(), securityPolicy);

        //loader.load();

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                .setApplicationUri("urn:eclipse:milo:examples:client")
                //.setCertificate(loader.getClientCertificate())
                //.setKeyPair(loader.getClientKeyPair())
                .setEndpoint(endpoint)
                .setIdentityProvider(new AnonymousProvider())
                .setRequestTimeout(uint(5000))
                .build();

        return new OpcUaClient(config);

    }

    public void subscribe(String name) throws Exception {

        // create a subscription and a monitored item
        UaSubscription subscription = client.getSubscriptionManager().createSubscription(1.0).get();

//        NodeId nodeId = new NodeId(NAMESPACE, Hcd2Namespace.NAMESPACE_PREFIX + name);
        NodeId nodeId = new NodeId(NAMESPACE, NAMESPACE_PREFIX + name);
        System.out.println("Subscribing to {}" + nodeId.toString());

        ReadValueId readValueId = new ReadValueId(nodeId,
                AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);

        // client handle must be unique per item
        UInteger clientHandle = uint(clientHandles.getAndIncrement());

        MonitoringParameters parameters = new MonitoringParameters(
                clientHandle,
                100.0,     // sampling interval
                null,       // filter, null means use default
                uint(10),   // queue size
                true);      // discard oldest

        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                readValueId, MonitoringMode.Reporting, parameters);

        //. added this from example
        //. https://github.com/eclipse/milo/blob/master/milo-examples/client-examples/src/main/java/org/eclipse/milo/examples/client/SubscriptionExample.java
        BiConsumer<UaMonitoredItem, Integer> onItemCreated = (item, id) -> item.setValueConsumer(this::onSubscriptionValue);
        List<UaMonitoredItem> items = subscription
                .createMonitoredItems(TimestampsToReturn.Both, newArrayList(request), onItemCreated).get();

        // do something with the value updates
        //   UaMonitoredItem item = items.get(0);
        //   item.setValueConsumer(valueConsumer);

        for (UaMonitoredItem item : items) {
            if (item.getStatusCode().isGood()) {
                logger.info("item created for nodeId={}", item.getReadValueId().getNodeId());
            } else {
                logger.warn(
                        "failed to create item for nodeId={} (status={})",
                        item.getReadValueId().getNodeId(), item.getStatusCode());
            }
        }

    }

    public void readValue(String nodeName) throws Exception {

        logger.info("IN readValue: NAMESPACE = " + NAMESPACE + ", identifier = " + NAMESPACE_PREFIX + nodeName);

        NodeId nodeId = new NodeId(NAMESPACE, NAMESPACE_PREFIX + nodeName);
        VariableNode node = client.getAddressSpace().createVariableNode(nodeId);

        for (int i=0; i<30; i++) {

            long start = System.currentTimeMillis();

            DataValue value = node.readValue().get();

            Double[] valueArray = (Double[])value.getValue().getValue();

            long end = System.currentTimeMillis();

            //System.out.println("first/last values: " + valueArray[0] + ", " + valueArray[22999]);
            System.out.println("elapsed time for 23000 double precision values = " + (end - start) + "ms");

        }
    }


    // XXX just set the value
    public void setValue(String name, Object value) throws Exception {
        Variant v = new Variant(value);

        // don't write status or timestamps
        DataValue dv = new DataValue(v, null, null);

        List<NodeId> nodeIds = ImmutableList.of(new NodeId(NAMESPACE, NAMESPACE_PREFIX + name));

        // write asynchronously....
        CompletableFuture<List<StatusCode>> f = client.writeValues(nodeIds, ImmutableList.of(dv));

        // ...but block for the results so we write in order
        List<StatusCode> statusCodes = f.get();
        StatusCode status = statusCodes.get(0);

        if (status.isGood()) {
            logger.info("Wrote '{}' to nodeId={}", v, nodeIds.get(0));
        } else {
            logger.error("Write '{}' failed for nodeId={}", v, nodeIds.get(0));
        }
    }


    private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
        System.out.println(
                "subscription value received: " +
                item.getReadValueId().getNodeId() + ", " + value.getValue());
    }
}
