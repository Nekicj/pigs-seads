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

//import Controllers.ActionsController;
//import Controllers.LiftController;
//import Controllers.OuttakeController;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;



import auto.constants.FConstants;
import auto.constants.LConstants;

@Autonomous(name = "SpecimenAutoSnake", group = "Competition")
public class SpecimenAutoSnake extends OpMode {

    private Follower follower;
//    private OuttakeController outtakeController;
//    private ActionsController actionsController;
    private Timer pathTimer, opmodeTimer;
    private DelayedPoseAction takeAtSpec1 = new DelayedPoseAction();
    private DelayedPoseAction takeAtSpace6 = new DelayedPoseAction();
    private DelayedPoseAction takeAtSpace7 = new DelayedPoseAction();
    private int pathState;
    private ElapsedTime stageTimer = new ElapsedTime();
    private int stage4 = 0;

    private ElapsedTime stageTimer5 = new ElapsedTime();
    private int stage5 = 0;

    private ElapsedTime stageTimer6 = new ElapsedTime();
    private int stage6 = 0;
    private boolean opened = false;
    private boolean hasTakenAtSpec1 = false;
    private Servo IntakeDownServo = null;
    private DelayedAction clawCloseAndGo = new DelayedAction();
    private DelayedAction clawCloseAndGo4 = new DelayedAction();
    private DelayedAction clawCloseAndGo5 = new DelayedAction();
    private DelayedAction clawCloseAndGo6 = new DelayedAction();


    ElapsedTime actionTimer = new ElapsedTime();

    private final Pose startPose = new Pose(9.15, 55.8, Math.toRadians(0));  // Starting position
    private final Pose Spec1 = new Pose(37.5, 68.09, 0);
    private final Pose CPto1 = new Pose(14.28, 38.8);
    private final Pose Samp1 = new Pose(63.63, 28.57, 0);

    private final Pose toHP = new Pose(21.2, 21.65, 0);
    private final Pose Samp2 = new Pose(60.27, 14.95, 0);
    private final Pose CPto2 = new Pose(58.7, 27.46);
    private final Pose Samp3 = new Pose(57.15,7.59, 0);
    private final Pose CPto3 = new Pose(57.15, 18.97);
    private final Pose toHP2 = new Pose(19.2, 7.59, 0);
    private final Pose CPtoSpace = new Pose(16.91, 60.7);
    private final Pose toSpace = new Pose(38.75, 67.08, 0);
    private final Pose pickyzone = new Pose(18, 33.7, 0);

    private final Pose PickPos = new Pose(9.6, 33.7, 0);
    private final Pose PickPos2 = new Pose(11.6, 33.7, 0);

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

    public class DelayedPoseAction {
        private boolean started = false;
        private boolean done = false;
        private ElapsedTime timer = new ElapsedTime();

        public void run(Pose currentPose, Pose targetPose, double tolerance, double delaySeconds, Runnable action) {
            if (done) return;

            double dx = Math.abs(currentPose.getX() - targetPose.getX());
            double dy = Math.abs(currentPose.getY() - targetPose.getY());

            if (dx <= tolerance && dy <= tolerance) {
                if (!started) {
                    timer.reset();
                    started = true;
                }

                if (timer.seconds() >= delaySeconds) {
                    action.run();
                    done = true;
                }
            }
        }

        public void reset() {
            started = false;
            done = false;
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
                .addPath(new BezierLine(new Point(toHP2), new Point(pickyzone)))
                .setLinearHeadingInterpolation(toHP2.getHeading(), pickyzone.getHeading())
                .addPath(new BezierLine(new Point(pickyzone), new Point(PickPos)))
                .setLinearHeadingInterpolation(pickyzone.getHeading(), PickPos.getHeading())
                .build();
        Clip2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(PickPos), new Point(CPtoSpace),new Point(toSpace)))
                .setLinearHeadingInterpolation(PickPos.getHeading(), toSpace.getHeading())
                .addPath(new BezierLine(new Point(toSpace), new Point(pickyzone)))
                .setLinearHeadingInterpolation(toSpace.getHeading(), pickyzone.getHeading())
                .addPath(new BezierLine(new Point(pickyzone), new Point(PickPos2)))
                .setLinearHeadingInterpolation(pickyzone.getHeading(), PickPos2.getHeading())
                .build();
        Clip3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(PickPos2), new Point(CPtoSpace),new Point(toSpace)))
                .setLinearHeadingInterpolation(PickPos2.getHeading(), toSpace.getHeading())
                .addPath(new BezierLine(new Point(toSpace), new Point(pickyzone)))
                .setLinearHeadingInterpolation(toSpace.getHeading(), pickyzone.getHeading())
                .addPath(new BezierLine(new Point(pickyzone), new Point(PickPos2)))
                .setLinearHeadingInterpolation(pickyzone.getHeading(), PickPos2.getHeading())
                .build();
        Clip4 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(PickPos2), new Point(CPtoSpace),new Point(toSpace)))
                .setLinearHeadingInterpolation(PickPos2.getHeading(), toSpace.getHeading())
                .addPath(new BezierLine(new Point(toSpace), new Point(pickyzone)))
                .setLinearHeadingInterpolation(toSpace.getHeading(), pickyzone.getHeading())
                .addPath(new BezierLine(new Point(pickyzone), new Point(PickPos2)))
                .setLinearHeadingInterpolation(pickyzone.getHeading(), PickPos2.getHeading())
                .build();

    }



    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(Score);
                setPathState(1);
                break;
            case 1:
                if(!follower.isBusy()) {
                    //OutController.OuttakeTake();
                   // actionsController.toTakeSpecimen();
                    follower.followPath(Grab1,true);
                    setPathState(2);
                }
                break;
            case 2:
                if(!follower.isBusy()) {
                    follower.followPath(Grab2,true);
                   // follower.setMaxPower(0.8);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    follower.followPath(Grab3,true);
                   // actionsController.toTakeSpecimen();
                    //follower.setMaxPower(6);
                    setPathState(4);

                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    if (stage4 == 0) {
                        stageTimer.reset();
                        stage4 = 1;
                    }

                    if (stage4 == 1 && stageTimer.seconds() >= 0.5) {
                       // outtakeController.setClawClose();
                        stage4 = 2;
                        stageTimer.reset();
                    }

                    if (stage4 == 2 && stageTimer.seconds() >= 0.5) {
                        follower.followPath(Clip2, true);
                       // outtakeController.setOuttakeToPush();
                       // outtakeController.setClawRotateToPush();
                       // actionsController.setLiftTarget(LiftController.Position.SPECIMEN_PUSH.getPos());
                        hasTakenAtSpec1 = false;
                        setPathState(5);
                        stage4 = 0;
                    }
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    if (stage5 == 0) {
                        stageTimer5.reset();
                        stage5 = 1;
                    }

                    if (stage5 == 1 && stageTimer5.seconds() >= 0.5) {
                       // outtakeController.setClawClose();
                        stage5 = 2;
                        stageTimer5.reset();
                    }

                    if (stage5 == 2 && stageTimer5.seconds() >= 0.5) {
                        follower.followPath(Clip3, true);
                        //outtakeController.setOuttakeToPush();
                        //outtakeController.setClawRotateToPush();
                        //actionsController.setLiftTarget(LiftController.Position.SPECIMEN_PUSH.getPos());
                        hasTakenAtSpec1 = false;
                        setPathState(6);
                        stage5 = 0;
                    }
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    if (stage6 == 0) {
                        stageTimer6.reset();
                        stage6 = 1;
                    }

                    if (stage6 == 1 && stageTimer6.seconds() >= 0.5) {
                        //outtakeController.setClawClose();
                        stage6 = 2;
                        stageTimer6.reset();
                    }

                    if (stage6 == 2 && stageTimer6.seconds() >= 0.5) {
                        //outtakeController.setOuttakeToPush();
                        //outtakeController.setClawRotateToPush();
                        //actionsController.setLiftTarget(LiftController.Position.SPECIMEN_PUSH.getPos());
                        follower.followPath(Clip4, true);
                        hasTakenAtSpec1 = false;
                        setPathState(7);
                        stage6 = 0;
                    }
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
        Pose pose = follower.getPose();
//        actionsController.update();

//        if (pathState == 5) {
//            takeAtSpec1.run(pose, Spec1, 2.0, 1.0, () -> {
//                OutController.OuttakeTake();
//            });
//        }
//
//        if (pathState == 6) {
//            takeAtSpace6.run(pose, toSpace, 2.0, 1.0, () -> {
//                OutController.OuttakeTake();
//            });
//        }
//
//        if (pathState == 7) {
//            takeAtSpace7.run(pose, toSpace, 2.0, 1.0, () -> {
//                OutController.OuttakeTake();
//            });
//        }

        if (!hasTakenAtSpec1 && pathState == 5) {
            Pose currentPose = follower.getPose();
            if (Math.abs(currentPose.getX() - toSpace.getX()) < 2 &&
                    Math.abs(currentPose.getY() - toSpace.getY()) < 2) {
                //outtakeController.setClawOpen();
                //actionsController.toTakeSpecimen();

                hasTakenAtSpec1 = true;
            }
        }
        if (!hasTakenAtSpec1 && pathState == 6) {
            Pose currentPose = follower.getPose();
            if (Math.abs(currentPose.getX() - toSpace.getX()) < 2 &&
                    Math.abs(currentPose.getY() - toSpace.getY()) < 2) {
                //outtakeController.setClawOpen();
                //actionsController.toTakeSpecimen();
                hasTakenAtSpec1 = true;
            }
        }
        if (!hasTakenAtSpec1 && pathState == 7) {
            Pose currentPose = follower.getPose();
            if (Math.abs(currentPose.getX() - toSpace.getX()) < 2 &&
                    Math.abs(currentPose.getY() - toSpace.getY()) < 2) {
               // outtakeController.setClawOpen();
                //actionsController.toTakeSpecimen();
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
    }


    @Override
    public void init() {
//        outtakeController = new OuttakeController();
//
//        outtakeController.initialize(hardwareMap,
//                "OuttakeClaw",
//                "ClawRotate",
//                "OuttakeArmLeft",
//                "OuttakeArmRight",
//                false);


        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
//        actionsController = new ActionsController(hardwareMap);
//        actionsController.setExtendTarget(0);
//        actionsController.setLiftToTransfer();
//        outtakeController.setClawRotateToPush();



        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();


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
