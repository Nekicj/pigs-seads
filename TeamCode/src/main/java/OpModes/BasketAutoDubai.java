package OpModes;

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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import Controllers.OuttakeController;
import auto.constants.FConstants;
import auto.constants.LConstants;
import Controllers.ActionsController;
import Controllers.BaseController;
import Controllers.ExtendController;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "BasketAutoDubai", group = "Competition")
public class BasketAutoDubai extends OpMode {

    private Follower follower;
    private ActionsController actionsController;
    private Servo clawRotate = null;
    private boolean opened = false;

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
    private ElapsedTime stageTimer7 = new ElapsedTime();
    private int stage7 = 0;
    private boolean hasTakenAtSpec1 = false;
    private Servo IntakeDownServo = null;
    private DelayedAction clawCloseAndGo = new DelayedAction();
    private DelayedAction clawCloseAndGo4 = new DelayedAction();
    private DelayedAction clawCloseAndGo5 = new DelayedAction();
    private DelayedAction clawCloseAndGo6 = new DelayedAction();


    ElapsedTime actionTimer = new ElapsedTime();

    //    private final Pose startPose = new Pose(7.77, 55.77, Math.toRadians(0));  // Starting position
//    private final Pose Spec1 = new Pose(37.4, 68.11, 0);
//    private final Pose way1to1 = new Pose(37.02, 36, 0);
//    private final Pose way2to1 = new Pose(59.19, 36, 0);
//
//    private final Pose way3to1 = new Pose(59.19, 23.77, 0); //2nd use
//    private final Pose tohp1 = new Pose(19.5, 23.77, 0);
//    private final Pose way1to2 = new Pose(59.19, 13.6, 0); //2nd use
//    private final Pose tohp2 = new Pose(19.5,13.94, 0);
//    private final Pose way1to3 = new Pose(59.19, 7.7, 0 );
//    private final Pose tohp3 = new Pose(19.5, 7.9, 0);
    private final Pose startPose = new Pose(8.2, 102.2, Math.toRadians(0));  // Starting position
    private final Pose BasketPos = new Pose(8.1, 136.5, Math.toRadians(-45));
    private final Pose CPtoBas = new Pose(19.64, 111.62);
    private final Pose Samp1 = new Pose(23.44, 120.78, 0);

    private final Pose Samp2 = new Pose(60.27, 14, 0);
    private final Pose Samp3 = new Pose(62.48,8.9, 0);
    private final Pose Park = new Pose(10.2, 33, 0);

    private Path Score;
    private PathChain Bomb1, Bomb2, Bomb3, Bomb4, cars, toSamp1, toSamp2, toSamp3;

    public class DelayedAction {
        private ElapsedTime timer = new ElapsedTime();
        private boolean started = false;
        private boolean opened = false;
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
        Score = new Path(new BezierCurve(new Point(startPose),new Point(CPtoBas), new Point(BasketPos)));
        Score.setLinearHeadingInterpolation(0, BasketPos.getHeading());


        toSamp1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(BasketPos), new Point(Samp1)))
                .setLinearHeadingInterpolation(BasketPos.getHeading(), Samp1.getHeading())
                .build();
        Bomb1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Samp1), new Point(BasketPos)))
                .setLinearHeadingInterpolation(Samp1.getHeading(), BasketPos.getHeading())
                .build();
        toSamp2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(BasketPos), new Point(Samp2)))
                .setLinearHeadingInterpolation(BasketPos.getHeading(), Samp2.getHeading())
                .build();
        Bomb2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Samp2), new Point(BasketPos)))
                .setLinearHeadingInterpolation(Samp2.getHeading(), BasketPos.getHeading())
                .build();
        toSamp3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(BasketPos), new Point(Samp3)))
                .setLinearHeadingInterpolation(BasketPos.getHeading(), Samp3.getHeading())
                .build();

        Bomb3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Samp3), new Point(BasketPos)))
                .setLinearHeadingInterpolation(Samp3.getHeading(), BasketPos.getHeading())
                .build();


    }



    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:

                follower.followPath(Score);
                follower.setMaxPower(0.8);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    if (stage4 == 0) {
                        stageTimer.reset();
                        stage4 = 1;
                    }

                    if (stage4 == 1 && stageTimer.seconds() >= 0.02) {
                        actionsController.setOuttakeToBasketAuto();
                        follower.setMaxPower(0.7);
                        stage4 = 2;
                        stageTimer.reset();
                    }

                    if (stage4 == 2 && stageTimer.seconds() >= 2) {
                        follower.followPath(toSamp1, true);
                        hasTakenAtSpec1 = false;
                        setPathState(2);
                        stage4 = 0;
                    }
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    if (stage4 == 0) {
                        stageTimer.reset();
                        stage4 = 1;
                    }

                    if (stage4 == 1 && stageTimer.seconds() >= 0.02) {
                        actionsController.toIntakeTake();
                        follower.setMaxPower(1);
                        stage4 = 2;
                        stageTimer.reset();
                    }

                    if (stage4 == 2 && stageTimer.seconds() >= 0.06) {
                        follower.followPath(Bomb1, true);

                        hasTakenAtSpec1 = false;
                        setPathState(3);
                        stage4 = 0;
                    }
                }
                break;
//            case 3:
//                if(!follower.isBusy()) {
//                    follower.followPath(toSamp2,true);
//                    follower.setMaxPower(0.8);
//                    setPathState(4);
//
//                }
//                break;
//            case 4:
//                if (!follower.isBusy()) {
//                    if (stage5 == 0) {
//                        stageTimer5.reset();
//                        stage5 = 1;
//                    }
//
//                    if (stage5 == 1 && stageTimer5.seconds() >= 0.02) {
//                        actionsController.setClaws(true);
//                        follower.setMaxPower(1);
//                        stage5 = 2;
//                        stageTimer5.reset();
//                    }
//
//                    if (stage5 == 2 && stageTimer5.seconds() >= 0.06) {
//                        follower.followPath(Bomb2, true);
//                        actionsController.toPushSpecimen();
//                        hasTakenAtSpec1 = false;
//                        setPathState(10);
//                        stage5 = 0;
//                    }
//                }
//                break;
//            case 5:
//                if(!follower.isBusy()) {
//                    follower.followPath(toSamp3,true);
//                    follower.setMaxPower(0.9);
//                    setPathState(6);
//
//                }
//                break;
//            case 6:
//                if (!follower.isBusy()) {
//                    if (stage6 == 0) {
//                        stageTimer.reset();
//                        stage6 = 1;
//                    }
//
//                    if (stage6 == 1 && stageTimer.seconds() >= 0.02) {
//                        actionsController.setClaws(true);
//                        follower.setMaxPower(1);
//                        stage6 = 2;
//                        stageTimer.reset();
//                    }
//
//                    if (stage6 == 2 && stageTimer.seconds() >= 0.06) {
//                        follower.followPath(Bomb3, true);
//                        actionsController.toPushSpecimen();
//                        hasTakenAtSpec1 = false;
//                        setPathState(7);
//                        stage6 = 0;
//                    }
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
        actionsController.update();
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

        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        actionsController = new ActionsController(hardwareMap);
        actionsController.setExtendTarget(0);
        actionsController.setIntakeToStandard();
        clawRotate = hardwareMap.get(Servo.class, "ClawRotate");
        clawRotate.setPosition(OuttakeController.Servos.CLAW_ROTATE_PUSH_SPECIMEN.getPos());





        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();


    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop(

    ) {

    }

    @Override
    public void start() {

        opmodeTimer.resetTimer();
        setPathState(0);
    }
    @Override
    public void stop() {
    }

}
