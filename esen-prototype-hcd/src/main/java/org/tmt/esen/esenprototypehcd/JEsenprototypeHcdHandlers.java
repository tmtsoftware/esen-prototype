package org.tmt.esen.esenprototypehcd;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import csw.command.client.messages.TopLevelActorMessage;
import csw.framework.javadsl.JComponentHandlers;
import csw.framework.models.JCswContext;
import csw.location.models.TrackingEvent;
import csw.logging.api.javadsl.ILogger;
import csw.params.commands.CommandResponse;
import csw.params.commands.ControlCommand;
import csw.time.core.models.UTCTime;

import java.util.concurrent.CompletableFuture;

/**
 * Domain specific logic should be written in below handlers.
 * This handlers gets invoked when component receives messages/commands from other component/entity.
 * For example, if one component sends Submit(Setup(args)) command to EsenprototypeHcd,
 * This will be first validated in the supervisor and then forwarded to Component TLA which first invokes validateCommand hook
 * and if validation is successful, then onSubmit hook gets invoked.
 * You can find more information on this here : https://tmtsoftware.github.io/csw/commons/framework.html
 */
public class JEsenprototypeHcdHandlers extends JComponentHandlers {

    private final JCswContext cswCtx;
    private ActorContext<TopLevelActorMessage> ctx;
    private final ILogger log;

    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorAirTemperaturesActor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorSurfaceTemperaturesActor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorStructuralTemperaturesActor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorWindSpeedsActor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorHumiditiesActor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorDewPointsActor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorInclinationsActor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorPressuresActor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> opcMonitorUniaxialAccellerations1Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations2Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations3Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations4Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations5Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations6Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations7Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations8Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations9Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations10Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations11Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations12Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations13Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorUniaxialAccellerations14Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorTriaxialAccellerations1Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorTriaxialAccellerations2Actor;
    private ActorRef<JOpcMonitorActor.OpcMonitorMessage> OpcMonitorTriaxialAccellerations3Actor;


    JEsenprototypeHcdHandlers(ActorContext<TopLevelActorMessage> ctx,JCswContext cswCtx) {
        super(ctx, cswCtx);
        this.cswCtx = cswCtx;
        this.ctx = ctx;
        this.log = cswCtx.loggerFactory().getLogger(getClass());


    }

    @Override
    public CompletableFuture<Void> jInitialize() {
    log.info("Initializing esenprototype HCD...");
    return CompletableFuture.runAsync(() -> {

        // create the monitorActors
        opcMonitorAirTemperaturesActor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.AirTemperatures", cswCtx.loggerFactory()));
        opcMonitorSurfaceTemperaturesActor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.SurfaceTemperatures", cswCtx.loggerFactory()));
        opcMonitorStructuralTemperaturesActor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.StructuralTemperatures", cswCtx.loggerFactory()));
        opcMonitorWindSpeedsActor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.WindSpeeds", cswCtx.loggerFactory()));
        opcMonitorHumiditiesActor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.Humidities", cswCtx.loggerFactory()));
        opcMonitorDewPointsActor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.DewPoints", cswCtx.loggerFactory()));
        opcMonitorInclinationsActor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.Inclinations", cswCtx.loggerFactory()));
        opcMonitorPressuresActor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.Pressures", cswCtx.loggerFactory()));
        opcMonitorUniaxialAccellerations1Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations1", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations2Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations2", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations3Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations3", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations4Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations4", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations5Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations5", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations6Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations6", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations7Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations7", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations8Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations8", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations9Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations9", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations10Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations10", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations11Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations11", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations12Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations12", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations13Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations13", cswCtx.loggerFactory()));
        OpcMonitorUniaxialAccellerations14Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.UniaxialAccellerations14", cswCtx.loggerFactory()));
        OpcMonitorTriaxialAccellerations1Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.TriaxialAccellerations1", cswCtx.loggerFactory()));
        OpcMonitorTriaxialAccellerations2Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.TriaxialAccellerations2", cswCtx.loggerFactory()));
        OpcMonitorTriaxialAccellerations3Actor = ctx.spawnAnonymous(JOpcMonitorActor.behavior(4, "MAIN.TriaxialAccellerations3", cswCtx.loggerFactory()));


        // start the monitorActors
        opcMonitorAirTemperaturesActor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        opcMonitorSurfaceTemperaturesActor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        opcMonitorStructuralTemperaturesActor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        opcMonitorWindSpeedsActor.tell(new JOpcMonitorActor.StartMessage(0.1f));  // wind speeds at 10 Hz
        opcMonitorHumiditiesActor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        opcMonitorDewPointsActor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        opcMonitorInclinationsActor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        opcMonitorPressuresActor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        opcMonitorUniaxialAccellerations1Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations2Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations3Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations4Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations5Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations6Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations7Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations8Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations9Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations10Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations11Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations12Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations13Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorUniaxialAccellerations14Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorTriaxialAccellerations1Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorTriaxialAccellerations2Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        OpcMonitorTriaxialAccellerations3Actor.tell(new JOpcMonitorActor.StartMessage(1.0f));
        });
    }

    @Override
    public CompletableFuture<Void> jOnShutdown() {
        return CompletableFuture.runAsync(() -> {

        });
    }

    @Override
    public void onLocationTrackingEvent(TrackingEvent trackingEvent) {

    }

    @Override
    public CommandResponse.ValidateCommandResponse validateCommand(ControlCommand controlCommand) {
        return new CommandResponse.Accepted(controlCommand.runId());
    }

    @Override
    public CommandResponse.SubmitResponse onSubmit(ControlCommand controlCommand) {
        return new CommandResponse.Completed(controlCommand.runId());
    }

    @Override
    public void onOneway(ControlCommand controlCommand) {

    }

    @Override
    public void onGoOffline() {

    }

    @Override
    public void onGoOnline() {

    }


}




