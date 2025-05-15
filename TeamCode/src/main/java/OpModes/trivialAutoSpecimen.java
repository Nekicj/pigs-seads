package OpModes;

import static java.lang.Math.abs;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import Controllers.Outtake.DaddyController;
import com.qualcomm.robotcore.util.ElapsedTime;



import auto.constants.FConstants;
import auto.constants.LConstants;

@Autonomous(name = "SpecimenAutoBest", group = "Autobotz")
public class trivialAutoSpecimen extends OpMode {

    private Follower follower;
    private DaddyController OutController;
    private Timer pathTimer, opmodeTimer;
    private int pathState;
    private ElapsedTime stageTimer = new ElapsedTime();
    private int stage4 = 0;
    private boolean opened = false;
    private boolean hasTakenAtSpec1 = false;

    private DelayedAction clawCloseAndGo = new DelayedAction();
    private DelayedAction clawCloseAndGo4 = new DelayedAction();
    private DelayedAction clawCloseAndGo5 = new DelayedAction();
    private DelayedAction clawCloseAndGo6 = new DelayedAction();

    ElapsedTime actionTimer = new ElapsedTime();

    private final Pose startPose = new Pose(7.8, 56.06, Math.toRadians(0));  // Starting position
    private final Pose Spec1 = new Pose(36.6, 72, 0);
    private final Pose CPto1 = new Pose(14.7, 34);
    private final Pose Samp1 = new Pose(64, 26.1, 0);
    private final Pose toHP = new Pose(22, 20.4, 0);
    private final Pose Samp2 = new Pose(64, 13.7, 0);
    private final Pose CPto2 = new Pose(50.1, 31.6);
    private final Pose Samp3 = new Pose(62,7.2, 0);
    private final Pose CPto3 = new Pose(47.4, 23.8);
    private final Pose toHP2 = new Pose(21.2, 8.2, 0);
    private final Pose CPtoSpace = new Pose(19.8, 59.6);
    private final Pose toSpace = new Pose(36.6, 72.2, 0);

    private final Pose PickPos = new Pose(9.1, 31.27, 0);
    private final Pose Spec2 = new Pose(7.8+12.91206598205746, 56.06+39.76907356578898, 0.022974112383540985);

    private Path Score, Park;
    private PathChain Grab1, Grab2, Grab3, Clip2, Clip3, Clip4, Human3, PickUp;

    public class DelayedAction {
        private ElapsedTime timer = new ElapsedTime();
        private boolean started = false;
        private boolean finished = false;

        public void run(double delaySeconds, Runnable action) {
            if (finished) return;

            if (!started) {
                timer.reset();
                started = true;
            }

            if (timer.seconds() >= delaySeconds) {
                action.run();
                finished = true;
            }
        }

        public void reset() {
            started = false;
            finished = false;
        }

        public boolean isFinished() {
            return finished;
        }
    }

    public void buildPaths() {
        Score = new Path(new BezierLine(new Point(startPose), new Point(Spec1)));
        Score.setLinearHeadingInterpolation(0, Samp1.getHeading());


        Grab1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(Spec1), new Point(CPto1), new Point(Samp1)))
                .setLinearHeadingInterpolation(Spec1.getHeading(), Samp1.getHeading())
                .addPath(new BezierLine(new Point(Samp1), new Point(toHP)))
                .setLinearHeadingInterpolation(Samp1.getHeading(), toHP.getHeading())
                .build();
        Grab2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(toHP), new Point(CPto2), new Point(Samp2)))
                .setLinearHeadingInterpolation(toHP.getHeading(), Samp2.getHeading())
                .addPath(new BezierLine(new Point(Samp2), new Point(toHP)))
                .setLinearHeadingInterpolation(Samp2.getHeading(), toHP.getHeading())
                .build();
        Grab3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(toHP), new Point(CPto3), new Point(Samp3)))
                .setLinearHeadingInterpolation(toHP.getHeading(), Samp3.getHeading())
                .addPath(new BezierLine(new Point(Samp3), new Point(toHP2)))
                .setLinearHeadingInterpolation(Samp3.getHeading(), toHP2.getHeading())
                .addPath(new BezierLine(new Point(toHP2), new Point(PickPos)))
                .setLinearHeadingInterpolation(toHP2.getHeading(), PickPos.getHeading())
                .build();
        Clip2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(PickPos), new Point(CPtoSpace),new Point(toSpace)))
                .setLinearHeadingInterpolation(PickPos.getHeading(), toSpace.getHeading())
                .addPath(new BezierLine(new Point(toSpace), new Point(PickPos)))
                .setLinearHeadingInterpolation(toSpace.getHeading(), PickPos.getHeading())
                .build();
        Clip3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(PickPos), new Point(CPtoSpace),new Point(toSpace)))
                .setLinearHeadingInterpolation(PickPos.getHeading(), toSpace.getHeading())
                .addPath(new BezierLine(new Point(toSpace), new Point(PickPos)))
                .setLinearHeadingInterpolation(toSpace.getHeading(), PickPos.getHeading())
                .build();
        Clip4 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(PickPos), new Point(CPtoSpace),new Point(toSpace)))
                .setLinearHeadingInterpolation(PickPos.getHeading(), toSpace.getHeading())
                .addPath(new BezierLine(new Point(toSpace), new Point(PickPos)))
                .setLinearHeadingInterpolation(toSpace.getHeading(), PickPos.getHeading())
                .build();

    }



    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(Score);
                follower.setMaxPower(0.6);
                OutController.OuttakeClip();
                setPathState(1);
                break;
            case 1:
                if(!follower.isBusy()) {
                    follower.setMaxPower(1);
                    OutController.OuttakeTake();
                    follower.followPath(Grab1,true);
                    setPathState(2);
                }
                break;
            case 2:
                if(!follower.isBusy()) {
                    follower.followPath(Grab2,true);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    follower.setMaxPower(0.8);
                    follower.followPath(Grab3,true);
                    setPathState(4);

                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    clawCloseAndGo4.run(0.6, () -> {
                        OutController.ClClaw();
                        follower.setMaxPower(0.67);
                        OutController.OuttakeClip();
                        follower.followPath(Clip2, true);
                        hasTakenAtSpec1 = false;
                        setPathState(5);
                        clawCloseAndGo4.reset();
                    });
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    clawCloseAndGo5.run(0.6, () -> {
                        OutController.ClClaw();
                        follower.setMaxPower(0.67);
                        OutController.OuttakeClip();
                        follower.followPath(Clip3, true);
                        hasTakenAtSpec1 = false;
                        setPathState(6);
                        clawCloseAndGo5.reset();
                    });
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    clawCloseAndGo6.run(0.6, () -> {
                        OutController.ClClaw();
                        follower.setMaxPower(0.67);
                        OutController.OuttakeClip();
                        follower.followPath(Clip4, true);
                        hasTakenAtSpec1 = false;
                        setPathState(7);
                        clawCloseAndGo6.reset();
                    });
                }
                break;
//            case 6:
//                if(!follower.isBusy()) {
//                    follower.followPath(SampToHP3,true);
//                    setPathState(7);
//                }
//                break;


        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {

        follower.update();
        autonomousPathUpdate();

        if (!hasTakenAtSpec1 && pathState == 5) {
            Pose currentPose = follower.getPose();
            if (Math.abs(currentPose.getX() - Spec1.getX()) < 2.0 &&
                    Math.abs(currentPose.getY() - Spec1.getY()) < 2.0) {
                OutController.OuttakeTake();
                hasTakenAtSpec1 = true;
            }
        }
        if (!hasTakenAtSpec1 && pathState == 6) {
            Pose currentPose = follower.getPose();
            if (Math.abs(currentPose.getX() - toSpace.getX()) < 2.0 &&
                    Math.abs(currentPose.getY() - toSpace.getY()) < 2.0) {
                OutController.OuttakeTake();
                hasTakenAtSpec1 = true;
            }
        }
        if (!hasTakenAtSpec1 && pathState == 7) {
            Pose currentPose = follower.getPose();
            if (Math.abs(currentPose.getX() - toSpace.getX()) < 2.0 &&
                    Math.abs(currentPose.getY() - toSpace.getY()) < 2.0) {
                OutController.OuttakeTake();
                hasTakenAtSpec1 = true;
            }
        }


        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("timer", actionTimer.time());
        telemetry.update();
        OutController.updateScriptedActions();
    }


    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();



        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();
        OutController = new DaddyController(hardwareMap);



    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop(

    ) {}

    @Override
    public void start() {

        opmodeTimer.resetTimer();
        setPathState(0);
    }
    @Override
    public void stop() {
    }

}
