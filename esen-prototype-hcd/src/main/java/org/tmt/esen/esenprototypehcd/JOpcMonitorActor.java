package org.tmt.esen.esenprototypehcd;


import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import csw.logging.api.javadsl.ILogger;
import csw.logging.client.javadsl.JLoggerFactory;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.util.List;
import java.util.function.BiConsumer;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;


public class JOpcMonitorActor extends AbstractBehavior<JOpcMonitorActor.OpcMonitorMessage> {


    // add messages here
    interface OpcMonitorMessage {}

    // message to start the monitor
    public static final class StartMessage implements OpcMonitorMessage {
        public final float publishingInterval;

        public StartMessage(float publishingInterval) {
            this.publishingInterval = publishingInterval;
        }
    }
    // message to stop the monitor
    public static final class StopMessage implements OpcMonitorMessage {

    }


    private ActorContext<OpcMonitorMessage> actorContext;
    private JLoggerFactory loggerFactory;
    private ILogger log;
    private int opcNameSpace;
    private String opcNodeIdentifier;
    private float publishingInterval;



    private JOpcMonitorActor(ActorContext<OpcMonitorMessage> actorContext, int opcNameSpace, String opcNodeIdentifier, JLoggerFactory loggerFactory) {
        this.actorContext = actorContext;
        this.loggerFactory = loggerFactory;
        this.log = loggerFactory.getLogger(actorContext, getClass());
        this.opcNameSpace = opcNameSpace;
        this.opcNodeIdentifier = opcNodeIdentifier;
    }

    public static <OpcMonitorMessage> Behavior<OpcMonitorMessage> behavior( int opcNameSpace, String opcNodeIdentifier, JLoggerFactory loggerFactory) {
        return Behaviors.setup(ctx -> {
            return (AbstractBehavior<OpcMonitorMessage>) new JOpcMonitorActor((ActorContext<JOpcMonitorActor.OpcMonitorMessage>) ctx, opcNameSpace, opcNodeIdentifier, loggerFactory);
        });
    }


    @Override
    public Receive<OpcMonitorMessage> createReceive() {

        ReceiveBuilder<OpcMonitorMessage> builder = newReceiveBuilder()

                .onMessage(StartMessage.class,
                        message -> {
                            log.info("StartMessage Received");
                            // TODO change the rate
                            // for the prototype, just start the client monitoring
                            try {
                                OpcUaHcdClient opcUaHcdClient = new OpcUaHcdClient();
                                log.info("created OpcHcdClient");
                                subscribe(opcUaHcdClient, message.publishingInterval);
                                log.info("completed subscription");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return behavior(opcNameSpace, opcNodeIdentifier, loggerFactory);
                        });

        return builder.build();
    }



    public void subscribe(OpcUaHcdClient opcUaHcdClient, float publishingInterval) throws Exception {

        // create a subscription and a monitored item
        UaSubscription subscription = opcUaHcdClient.client.getSubscriptionManager().createSubscription(publishingInterval).get();

//        NodeId nodeId = new NodeId(NAMESPACE, Hcd2Namespace.NAMESPACE_PREFIX + name);
        NodeId nodeId = new NodeId(opcNameSpace, opcNodeIdentifier);
        System.out.println("Subscribing to {}" + nodeId.toString());

        ReadValueId readValueId = new ReadValueId(nodeId,
                AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);

        // client handle must be unique per item
        UInteger clientHandle = uint(opcUaHcdClient.clientHandles.getAndIncrement());

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
                log.info("item created for nodeId={}" + item.getReadValueId().getNodeId());
            } else {
                log.warn("failed to create item for nodeId={} (status={})" +
                        item.getReadValueId().getNodeId() + ", " + item.getStatusCode());
            }
        }

    }

    private void onSubscriptionValue(UaMonitoredItem item, DataValue value) {
        // TODO: here is where the CurrentState Publishing to the Assembly would occur

       log.info("subscription value received: " +
                        item.getReadValueId().getNodeId() + ", " + value.getValue());
    }




}