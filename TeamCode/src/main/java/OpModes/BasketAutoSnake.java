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

import Controllers.ActionsController;
import Controllers.OuttakeController;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;



import auto.constants.FConstants;
import auto.constants.LConstants;

@Autonomous(name = "BasketAutoSnake", group = "Competition")
public class BasketAutoSnake extends OpMode {

    private Follower follower;
    private ElapsedTime stateTimer = new ElapsedTime();
    private OuttakeController outtakeController;
    private ActionsController actionsController;
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

    private final Pose startPose = new Pose(9.24, 96.40, Math.toRadians(0));  // Starting position
    private final Pose Backet = new Pose(12.7, 130.8, -45);
    private final Pose Pick1 = new Pose(23.4, 120.33,0);
    private final Pose Pick2 = new Pose(23.4, 130.6, 0);
    private final Pose Pick3 = new Pose(23.4, 130.6, 25);
    private final Pose Park = new Pose(65.1, 95.1,90);
    private final Pose ParkCP = new Pose(67.2, 132.6);

    private Path Score, Parking;
    private PathChain Take1, Samp1, Take2, Samp2, Take3, Samp3;

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
        Score = new Path(new BezierLine(new Point(startPose), new Point(Backet)));
        Score.setLinearHeadingInterpolation(0, Backet.getHeading());


        Take1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Backet), new Point(Pick1)))
                .setLinearHeadingInterpolation(Backet.getHeading(), Pick1.getHeading())
                .build();

        Samp1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Pick1), new Point(Backet)))
                .setLinearHeadingInterpolation(Pick1.getHeading(), Backet.getHeading())
                .build();

        Take2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Backet), new Point(Pick2)))
                .setLinearHeadingInterpolation(Backet.getHeading(), Pick2.getHeading())
                .build();

        Samp2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Pick2), new Point(Backet)))
                .setLinearHeadingInterpolation(Pick2.getHeading(), Backet.getHeading())
                .build();

        Take3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Backet), new Point(Pick3)))
                .setLinearHeadingInterpolation(Backet.getHeading(), Pick3.getHeading())
                .build();

        Samp3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(Pick3), new Point(Backet)))
                .setLinearHeadingInterpolation(Pick3.getHeading(), Backet.getHeading())
                .build();

        Parking = new Path(new BezierCurve(new Point(Backet), new Point(ParkCP), new Point(Park)));
        Parking.setLinearHeadingInterpolation(Backet.getHeading(), Park.getHeading());

    }



    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(Score);
                follower.setMaxPower(0.8);
                actionsController.setOuttakeToBasket();
                stateTimer.reset();
                setPathState(1);
                break;

            case 1:
                if (!follower.isBusy() && stateTimer.seconds() >= 1.5) {
                    follower.followPath(Take1, true);
                    setPathState(2);
                }
                break;
            case 2:
                if(!follower.isBusy()) {
                    follower.followPath(Samp1,true);
                    setPathState(2);
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    follower.followPath(Take2,true);
                    setPathState(3);
                }
                break;
            case 4:
                if(!follower.isBusy()) {
                    follower.followPath(Samp2,true);
                    setPathState(5);
                }
                break;
            case 5:
                if(!follower.isBusy()) {
                    follower.followPath(Take3,true);
                    setPathState(6);
                }
                break;
            case 6:
                if(!follower.isBusy()) {
                    follower.followPath(Samp3,true);
                    setPathState(7);
                }
                break;
            case 7:
                if(!follower.isBusy()) {
                    follower.followPath(Parking,true);
                    setPathState(8);
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
        actionsController.update(false);

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
