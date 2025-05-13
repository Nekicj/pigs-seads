package OpModes;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup;
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup;
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay;
import com.rowanmcalpin.nextftc.pedro.FollowPath;
import com.rowanmcalpin.nextftc.pedro.PedroOpMode;

import Controllers.Outtake.Lifts;
import Controllers.Outtake.Outtake;
import Controllers.Outtake.OuttakeArm;
import auto.constants.FConstants;
import auto.constants.LConstants;

@Config
@Autonomous(name = "0 + 5?")
public class FiveSpecimenAuto extends PedroOpMode {

    public FiveSpecimenAuto() {
        super (Lifts.INSTANCE, Outtake.INSTANCE, OuttakeArm.INSTANCE);
    }

    public static double poses[][] = {
            {6, 53.5}, // start pose (0)
            {20, 55}, // score preload (1)
            {-10, 20}, // control l1 (2)
            {134, 23}, // control l2 (3)
            {15, 23}, // take 1 (4)
            {110, 14.6}, // control l2 t2 (5)
            {15, 10.6}, // take 2 (6)
            {0, 0}, // control l3 t3 (7)
            {0, 0}, // take 3 (8)
            {0, 0}, // score 1 (9)
            {0, 0}, // pricel wall 2 (10)
            {0, 0}, // grab 2 (11)
            {0, 0}, // score 2 (12)
            {0, 0}, // pricel wall 3 (13)
            {0, 0}, // grab 3 (14)
            {0, 0}, // score 3 (15)
            {0, 0}, // pricel wall 4 (16)
            {0, 0}, // grab 4 (17)
            {0, 0}, // score 4 (18)
            {0, 0} // park (19)
    };

    private final Pose startPose = new Pose(poses[0][0], poses[0][1], 0);
    private final Pose scorePreload = new Pose(poses[1][0], poses[1][1], 0);
    private final Pose control1t1 = new Pose(poses[2][0], poses[2][1], 0);
    private final Pose control1t2 = new Pose(poses[3][0], poses[3][1], 0);
    private final Pose take1 = new Pose(poses[4][0], poses[4][1], 0);
    private final Pose control2 = new Pose(poses[5][0], poses[5][1], 0);
    private final Pose take2 = new Pose(poses[6][0], poses[6][1], 0);
    private final Pose control3 = new Pose(poses[7][0], poses[7][1], 0);
    private final Pose take3 = new Pose(poses[8][0], poses[8][1], 0);
    private final Pose score1 = new Pose(poses[9][0], poses[9][1], 0);
    private final Pose pricel2 = new Pose(poses[10][0], poses[10][1], 0);
    private final Pose grab2 = new Pose(poses[11][0], poses[11][1], 0);
    private final Pose score2 = new Pose(poses[12][0], poses[12][1], 0);
    private final Pose pricel3 = new Pose(poses[13][0], poses[13][1], 0);
    private final Pose grab3 = new Pose(poses[14][0], poses[14][1], 0);
    private final Pose score3 = new Pose(poses[15][0], poses[15][1], 0);
    private final Pose pricel4 = new Pose(poses[16][0], poses[16][1], 0);
    private final Pose grab4 = new Pose(poses[17][0], poses[17][1], 0);
    private final Pose score4 = new Pose(poses[18][0], poses[18][1], 0);
    private final Pose park = new Pose(poses[19][0], poses[19][1], 0);


    private PathChain starting, taking, scor1, pricl2, scor2, pricl3, scor3, pricl4, scor4, parking;

    public void buildPaths() {
        starting = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(scorePreload)))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePreload.getHeading())
                .build();
        taking = follower.pathBuilder()
                .addPath(new BezierCurve(scorePreload, control1t1, control1t2, take1))
                .setConstantHeadingInterpolation(0)
                .addPath(new BezierCurve(take1, control2, take2))
                .setConstantHeadingInterpolation(0)
                .addPath(new BezierCurve(take2, control3, take3))
                .setConstantHeadingInterpolation(0)
                .build();
        scor1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(take3), new Point(score1)))
                .setLinearHeadingInterpolation(take3.getHeading(), score1.getHeading())
                .build();
        pricl2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score1), new Point(pricel2)))
                .setLinearHeadingInterpolation(take3.getHeading(), score1.getHeading())
                .addPath(new BezierLine(new Point(pricel2), new Point(grab2)))
                .setLinearHeadingInterpolation(pricel2.getHeading(), grab2.getHeading())
                .build();
        scor2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grab2), new Point(score2)))
                .setLinearHeadingInterpolation(grab2.getHeading(), score2.getHeading())
                .build();
        pricl3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score2), new Point(pricel3)))
                .setLinearHeadingInterpolation(score2.getHeading(), pricel3.getHeading())
                .addPath(new BezierLine(new Point(pricel3), new Point(grab3)))
                .setLinearHeadingInterpolation(pricel3.getHeading(), grab3.getHeading())
                .build();
        scor3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grab3), new Point(score3)))
                .setLinearHeadingInterpolation(grab3.getHeading(), score3.getHeading())
                .build();
        pricl4 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score3), new Point(pricel4)))
                .setLinearHeadingInterpolation(score3.getHeading(), pricel4.getHeading())
                .addPath(new BezierLine(new Point(pricel4), new Point(grab4)))
                .setLinearHeadingInterpolation(pricel4.getHeading(), grab4.getHeading())
                .build();
        scor4 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grab4), new Point(score4)))
                .setLinearHeadingInterpolation(grab4.getHeading(), score4.getHeading())
                .build();
        parking = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score4), new Point(park)))
                .setLinearHeadingInterpolation(score4.getHeading(), park.getHeading())
                .build();
    }

    public Command auto() {
        return new SequentialGroup (

            /// SCORE PRELOAD
            new ParallelGroup(

                    Outtake.INSTANCE.close(),
                    OuttakeArm.INSTANCE.toChamber(),
                    Lifts.INSTANCE.toChamber(),

                    new SequentialGroup(
                        new Delay(0.3),
                        new FollowPath(starting),
                        new Delay(0.3),
                        Outtake.INSTANCE.open()
                    )
            ),

            /// MOVE SAMPLES
            new ParallelGroup (
                    new FollowPath(taking),
                    OuttakeArm.INSTANCE.toGrab(),
                    Lifts.INSTANCE.toGround()
            ),

            /// SCORE 1st
            new ParallelGroup(
                    new SequentialGroup(
                            new Delay(0.2),
                            new FollowPath(scor1),
                            new Delay(0.3),
                            Outtake.INSTANCE.open()
                    ),
                    Outtake.INSTANCE.close(),
                    OuttakeArm.INSTANCE.toChamber(),
                    Lifts.INSTANCE.toChamber()
            ),

            /// GRAB 2nd
            new ParallelGroup(
                    new FollowPath(pricl2),
                    OuttakeArm.INSTANCE.toGrab(),
                    Lifts.INSTANCE.toGround()
            ),

            /// SCORE 2nd
            new SequentialGroup(
                    Outtake.INSTANCE.close(),
                    new ParallelGroup(
                            OuttakeArm.INSTANCE.toChamber(),
                            Lifts.INSTANCE.toChamber(),
                            new FollowPath(scor2)
                    ),
                    new Delay(0.3),
                    Outtake.INSTANCE.open()
            ),

            /// GRAB 3rd
                new ParallelGroup(
                        new FollowPath(pricl3),
                        OuttakeArm.INSTANCE.toGrab(),
                        Lifts.INSTANCE.toGround()
                ),

            /// SCORE 3rd
            new SequentialGroup(
                    Outtake.INSTANCE.close(),
                    new ParallelGroup(
                            OuttakeArm.INSTANCE.toChamber(),
                            Lifts.INSTANCE.toChamber(),
                            new FollowPath(scor3)
                    ),
                    new Delay(0.3),
                    Outtake.INSTANCE.open()
            ),

            /// GRAB 4th
            new ParallelGroup(
                    new FollowPath(pricl4),
                    OuttakeArm.INSTANCE.toGrab(),
                    Lifts.INSTANCE.toGround()
            ),

            /// SCORE 4th
            new SequentialGroup(
                    Outtake.INSTANCE.close(),
                    new ParallelGroup(
                            OuttakeArm.INSTANCE.toChamber(),
                            Lifts.INSTANCE.toChamber(),
                            new FollowPath(scor4)
                    ),
                    new Delay(0.3),
                    Outtake.INSTANCE.open(),
                    new FollowPath(parking)
            )
        );
    }

    @Override
    public void onUpdate() {
        telemetry.addData("Lift target position", Lifts.INSTANCE.controller.getTarget());
        telemetry.addData("Lift current position", Lifts.INSTANCE.right.getCurrentPosition());
        telemetry.update();
    }

    @Override
    public void onInit() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setMaxPower(0.7);
        follower.setStartingPose(startPose);
        buildPaths();
    }

    @Override
    public void onStartButtonPressed() {
        auto().invoke();
    }
}
