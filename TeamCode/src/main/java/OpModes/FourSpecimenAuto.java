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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup;
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup;
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay;
import com.rowanmcalpin.nextftc.pedro.FollowPath;
import com.rowanmcalpin.nextftc.pedro.PedroOpMode;

import Controllers.Outtake.Lifts;
import Controllers.Outtake.OuttakeArm;
import Controllers.Outtake.Outtake;
import auto.constants.FConstants;
import auto.constants.LConstants;

@Config
@Disabled
@Autonomous(name = "0 + 4?")
public class FourSpecimenAuto extends PedroOpMode {

    public FourSpecimenAuto() {
        super (Lifts.INSTANCE, Outtake.INSTANCE, OuttakeArm.INSTANCE);
    }
    
    public static double poses[][] = {
    {6, 53.5},
    {35, 66.5},
    {15, 23},
    {-10, 20},
    {134, 23},
    {15, 10.6},
    {110, 14.6},
    {25, 25}, // 7 pricel wall
    {6, 30}, // 8 grab
    {30, 70}, // 9 pricel chamber
    {38, 70}, // 10 score
    {25, 30}, // 11 pricel wall
    {6, 30},  // 12 grab
    {30, 70}, // 13 pricel chamber
    {38, 70}, // 14 score
    {25, 30}, // 15 pricel wall
    {6, 30}, // 16 grab
    {30, 70}, // 17 pricel chamber
    {38, 70}, // 18 score
    {10, 15}, // 19 park
    {5, 15} //hit the wall
};

    public static double howMuchRun = 100;
    private final Pose startPose = new Pose(poses[0][0], poses[0][1], 0);
    private final Pose preloadPose = new Pose(poses[1][0], poses[1][1],0);
    private final Pose take1 = new Pose(poses[2][0], poses[2][1], 90);
    private final Pose control1T1 = new Pose(poses[3][0], poses[3][1],0);
    private final Pose control2T1 = new Pose(poses[4][0], poses[4][1],0);
    private final Pose take2 = new Pose(poses[5][0], poses[5][1],0);
    private final Pose controlT2 = new Pose(poses[6][0], poses[6][1],0);
    private final Pose waitForClip = new Pose(poses[7][0], poses[7][1],0);
    private final Pose grab1 = new Pose(poses[8][0], poses[8][1],0);
    private final Pose pricelChamber1 = new Pose(poses[9][0], poses[9][1],0);
    private final Pose score1 = new Pose(poses[10][0], poses[10][1],0);
    private final Pose pricelWall2 = new Pose(poses[11][0], poses[11][1],0);
    private final Pose grab2 = new Pose(poses[12][0], poses[12][1],0);
    private final Pose pricelChamber2 = new Pose(poses[13][0], poses[13][1],0);
    private final Pose score2 = new Pose(poses[14][0], poses[14][1],0);
    private final Pose park = new Pose(poses[19][0], poses[19][1],0);
    private final Pose hit = new Pose(poses[20][0], poses[20][1],0);
    private PathChain starting, taking, pricelSpecimen1, scoreSpecimen1, pricelSpecimen2, scoreSpecimen2, parking, hitWall;

    public void buildPaths() {
        starting = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(preloadPose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), preloadPose.getHeading())
                .build();
        taking = follower.pathBuilder()
                .addPath(new BezierCurve(preloadPose, control1T1, control2T1, take1))
                .setConstantHeadingInterpolation(0)
                .addPath(new BezierCurve(take1, controlT2, take2))
                .setConstantHeadingInterpolation(0)
                .build();
        pricelSpecimen1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(take2), new Point(waitForClip)))
                .setLinearHeadingInterpolation(take2.getHeading(), waitForClip.getHeading())
                .addPath(new BezierLine(new Point(waitForClip), new Point(grab1)))
                .setLinearHeadingInterpolation(waitForClip.getHeading(), grab1.getHeading())
                .build();
        scoreSpecimen1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grab1), new Point(pricelChamber1)))
                .setLinearHeadingInterpolation(grab1.getHeading(), pricelChamber1.getHeading())
                .addPath(new BezierLine(new Point(pricelChamber1), new Point(score1)))
                .setLinearHeadingInterpolation(pricelChamber1.getHeading(), score1.getHeading())
                .build();
        pricelSpecimen2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score1), new Point(pricelWall2)))
                .setLinearHeadingInterpolation(score1.getHeading(), pricelWall2.getHeading())
                .addPath(new BezierLine(new Point(pricelWall2), new Point(grab2)))
                .setLinearHeadingInterpolation(pricelWall2.getHeading(), grab2.getHeading())
                .build();
        scoreSpecimen2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grab2), new Point(pricelChamber2)))
                .setLinearHeadingInterpolation(grab2.getHeading(), pricelChamber2.getHeading())
                .addPath(new BezierLine(new Point(pricelChamber2), new Point(score2)))
                .setLinearHeadingInterpolation(pricelChamber2.getHeading(), score2.getHeading())
                .build();
        parking = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score2), new Point(park)))
                .setLinearHeadingInterpolation(score2.getHeading(), park.getHeading())
                .build();
        hitWall = follower.pathBuilder()
                .addPath(new BezierLine(new Point(park), new Point(hit)))
                .setLinearHeadingInterpolation(park.getHeading(), hit.getHeading())
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

            /// PRICEL 1st SPECIMEN
            new SequentialGroup (
                    new FollowPath(pricelSpecimen1),
                    new Delay(0.5),
                    Outtake.INSTANCE.close()
            ),

            /// SCORE 1st SPECIMEN
            new ParallelGroup(
                    Lifts.INSTANCE.toChamber(),
                    OuttakeArm.INSTANCE.toChamber(),
                    new SequentialGroup(
                        new Delay(0.1),
                        new FollowPath(scoreSpecimen1),
                        new Delay(0.1),
                        Outtake.INSTANCE.open()
                    )
            ),

            /// PRICEL 2nd SPECIMEN
            new SequentialGroup(
                new ParallelGroup (
                        OuttakeArm.INSTANCE.toGrab(),
                        Lifts.INSTANCE.toGround(),
                        new FollowPath(pricelSpecimen2)
                ),
                new Delay(0.3),
                Outtake.INSTANCE.close(),
                new Delay(0.3)
            ),

            /// SCORE 2nd SPECIMEN
            new ParallelGroup(
                    Lifts.INSTANCE.toChamber(),
                    OuttakeArm.INSTANCE.toChamber(),
                    new SequentialGroup(
                        new Delay(0.3),
                        new FollowPath(scoreSpecimen2),
                        new Delay(0.3),
                        Outtake.INSTANCE.open()
                    )
            ),

            /// PRICEL 3rd SPECIMEN
            new SequentialGroup(
                    new ParallelGroup(
                            OuttakeArm.INSTANCE.toGrab(),
                            Lifts.INSTANCE.toGround(),
                            new FollowPath(pricelSpecimen2)
                    ),
                    new Delay(0.3),
                    Outtake.INSTANCE.close(),
                    new Delay(0.3)
            ),

            /// SCORE 3rd SPECIMEN
            new ParallelGroup(

                    Lifts.INSTANCE.toChamber(),
                    OuttakeArm.INSTANCE.toChamber(),
                    new SequentialGroup(
                        new Delay(0.3),
                        new FollowPath(scoreSpecimen2),
                        new Delay(0.3),
                        Outtake.INSTANCE.open()
                    )
            ),

            /// PARK
            new ParallelGroup(
                Lifts.INSTANCE.toGround(),
                OuttakeArm.INSTANCE.toChamber(),
                new FollowPath(parking)
            ),

            new FollowPath(hitWall)
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
        follower.setMaxPower(1);
        follower.setStartingPose(startPose);
        buildPaths();
    }

    @Override
    public void onStartButtonPressed() {
        auto().invoke();
    }
}
