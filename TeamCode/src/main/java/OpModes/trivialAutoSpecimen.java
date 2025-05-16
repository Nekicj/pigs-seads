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

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;



import auto.constants.FConstants;
import auto.constants.LConstants;

@Autonomous(name = "SpecimenAutoBest", group = "Autobotz")
public class trivialAutoSpecimen extends OpMode {

    private Follower follower;
    private DaddyController OutController;
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
    private final Pose toSpace = new Pose(36.9, 72.2, 0);

    private final Pose PickPos = new Pose(8.8, 31.27, 0);
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
                    if (stage4 == 0) {
                        stageTimer.reset();   // запускаем таймер
                        stage4 = 1;
                    }

                    if (stage4 == 1 && stageTimer.seconds() >= 0.3) {
                        OutController.ClClaw();  // ✅ закрываем клещи через 0.3 сек
                        stage4 = 2;
                        stageTimer.reset();     // перезапускаем таймер для следующего действия
                    }

                    if (stage4 == 2 && stageTimer.seconds() >= 0.3) {
                        follower.setMaxPower(0.7);
                        OutController.OuttakeClip();       // ✅ поднимаем лифт
                        follower.followPath(Clip2, true);  // ✅ едем
                        hasTakenAtSpec1 = false;
                        setPathState(5);
                        stage4 = 0;                         // сброс для повторного использования
                    }
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    if (stage5 == 0) {
                        stageTimer5.reset();
                        stage5 = 1;
                    }

                    if (stage5 == 1 && stageTimer5.seconds() >= 0.3) {
                        OutController.ClClaw();   // закрыть клещи через 0.3 сек
                        stage5 = 2;
                        stageTimer5.reset();
                    }

                    if (stage5 == 2 && stageTimer5.seconds() >= 0.3) {
                        follower.setMaxPower(0.7);
                        OutController.OuttakeClip();
                        follower.followPath(Clip3, true);
                        hasTakenAtSpec1 = false;
                        setPathState(6);
                        stage5 = 0; // сброс
                    }
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    if (stage6 == 0) {
                        stageTimer6.reset();
                        stage6 = 1;
                    }

                    if (stage6 == 1 && stageTimer6.seconds() >= 0.3) {
                        OutController.ClClaw();
                        stage6 = 2;
                        stageTimer6.reset();
                    }

                    if (stage6 == 2 && stageTimer6.seconds() >= 0.3) {
                        follower.setMaxPower(0.7);
                        OutController.OuttakeClip();
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
            if (Math.abs(currentPose.getX() - Spec1.getX()) < 1 &&
                    Math.abs(currentPose.getY() - Spec1.getY()) < 1) {
                OutController.OuttakeTake();
                hasTakenAtSpec1 = true;
            }
        }
        if (!hasTakenAtSpec1 && pathState == 6) {
            Pose currentPose = follower.getPose();
            if (Math.abs(currentPose.getX() - toSpace.getX()) < 1 &&
                    Math.abs(currentPose.getY() - toSpace.getY()) < 1) {
                OutController.OuttakeTake();
                hasTakenAtSpec1 = true;
            }
        }
        if (!hasTakenAtSpec1 && pathState == 7) {
            Pose currentPose = follower.getPose();
            if (Math.abs(currentPose.getX() - toSpace.getX()) < 1 &&
                    Math.abs(currentPose.getY() - toSpace.getY()) < 1) {
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
        IntakeDownServo = hardwareMap.get(Servo.class,"inTakeArm");
        IntakeDownServo.setPosition(1);




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
