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


import auto.constants.FConstants;
import auto.constants.LConstants;

@Autonomous(name = "SpecimenAutoBest", group = "Autobotz")
public class trivialAutoSpecimen extends OpMode {

    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    private boolean opened = false;




    private final Pose startPose = new Pose(7.8, 56.06, Math.toRadians(0));  // Starting position
    private final Pose Spec1 = new Pose(36.6, 72, 0);
    private final Pose CPto1 = new Pose(14.7, 34);
    private final Pose Samp1 = new Pose(72.4, 26.1, 0);
    private final Pose toHP = new Pose(22, 20.4, 0);
    private final Pose Samp2 = new Pose(66.3, 13.7, 0);
    private final Pose CPto2 = new Pose(50.1, 31.6, 0);
    private final Pose Samp3 = new Pose(62, 6.9, 0);
    private final Pose CPto3 = new Pose(47.4, 23.8, 0);
    private final Pose toHP2 = new Pose(17.7, 8.2, 0);
    private final Pose PickPos = new Pose(7.8+12.91206598205746, 56.06+39.76907356578898, 0.022974112383540985);
    private final Pose Spec2 = new Pose(7.8+12.91206598205746, 56.06+39.76907356578898, 0.022974112383540985);

    private Path Score, Park;
    private PathChain Grab1, Grab2, Grab3, SampToHP2, toSamp3, SampToHP3, Human3, PickUp;

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
                    follower.followPath(Grab3,true);
                    setPathState(4);
                }
                break;
//            case 4:
//                if(!follower.isBusy()) {
//                    follower.followPath(SampToHP2,true);
//                    setPathState(5);
//                }
//                break;
//            case 5:
//                if(!follower.isBusy()) {
//                    follower.followPath(toSamp3,true);
//                    setPathState(6);
//                }
//                break;
//            case 6:
//                if(!follower.isBusy()) {
//                    follower.followPath(SampToHP3,true);
//                    setPathState(7);
//                }
//                break;

//
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
        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }


    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        actionTimer = new Timer();


        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();




    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop() {}

    @Override
    public void start() {

        opmodeTimer.resetTimer();
        setPathState(0);
    }
    @Override
    public void stop() {
    }

}
