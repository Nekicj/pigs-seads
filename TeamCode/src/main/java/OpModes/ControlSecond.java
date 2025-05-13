package OpModes;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup;
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup;
import com.rowanmcalpin.nextftc.core.command.utility.InstantCommand;
import com.rowanmcalpin.nextftc.core.command.utility.conditionals.PassiveConditionalCommand;
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay;
import com.rowanmcalpin.nextftc.pedro.PedroOpMode;

import Controllers.Intake.Claw;
import Controllers.Intake.Extendo;
import Controllers.Intake.InRotate;
import Controllers.Intake.IntakeArm;
import Controllers.Outtake.Lifts;
import Controllers.Outtake.Outtake;
import Controllers.Outtake.OuttakeArm;

import auto.constants.FConstants;
import auto.constants.LConstants;


@TeleOp (name = "Base + Intake")
public class ControlSecond extends PedroOpMode {

    private Follower follower;
    private final Pose startPose = new Pose(0,0,0);

    public boolean ArmIsGrabbing = TRUE;
    public boolean ClawIsOpen = TRUE;

    public ControlSecond() {
        super (
                Claw.INSTANCE,
                Lifts.INSTANCE,
                Extendo.INSTANCE,
                IntakeArm.INSTANCE,
                Outtake.INSTANCE,
                OuttakeArm.INSTANCE,
                InRotate.INSTANCE
        );
    }

    @Override
    public void onInit() {

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        IntakeArm.INSTANCE.up();
        Claw.INSTANCE.open();
        InRotate.INSTANCE.toPerpendecular();

        OuttakeArm.INSTANCE.toGrab();
        Outtake.INSTANCE.open();

        Lifts.INSTANCE.toGround();
        Extendo.INSTANCE.toReturn();

    }

    @Override
    public void onStartButtonPressed() {

        /// BASE DRIVER----------------------------------------------------------------------------

        follower.startTeleopDrive();

        // INTAKE ROTATE
        gamepadManager.getGamepad1().getY().setPressedCommand(
                () -> {
                    return InRotate.INSTANCE.toParallel();
                }
        );

        gamepadManager.getGamepad1().getB().setPressedCommand(
                () -> {
                    return InRotate.INSTANCE.toHypotenuse();
                }
        );

        gamepadManager.getGamepad1().getA().setPressedCommand(
                () -> {
                    return InRotate.INSTANCE.toPerpendecular();
                }
        );

        // INTAKE BIND
        gamepadManager.getGamepad1().getRightBumper().setPressedCommand(
                () -> {
                    return new ParallelGroup(
                            IntakeArm.INSTANCE.down(),
                            new SequentialGroup(
                                    new Delay(0.3),
                                    Claw.INSTANCE.close()
                            ),
                            new SequentialGroup(
                                    new Delay(0.5),
                                    IntakeArm.INSTANCE.up()
                            )

                    );
                }
        );

        gamepadManager.getGamepad1().getLeftBumper().setPressedCommand(
                () -> {
                    return Claw.INSTANCE.open();
                }
        );

        // EXTENDO

        gamepadManager.getGamepad1().getRightTrigger().setHeldCommand(
                values -> new PassiveConditionalCommand(
                        () -> Extendo.INSTANCE.motor.getCurrentPosition() > -1750,
                        () -> Extendo.INSTANCE.setMotorPower(-values.doubleValue()),
                        () -> Extendo.INSTANCE.setMotorPower(0)
                )
        );

        gamepadManager.getGamepad1().getLeftTrigger().setHeldCommand(
                values -> {
                    return Extendo.INSTANCE.setMotorPower(values.doubleValue());
                }
        );

        gamepadManager.getGamepad1().getRightTrigger().setReleasedCommand(
                values -> {
                    return Extendo.INSTANCE.setMotorPower(0);
                }
        );

        gamepadManager.getGamepad1().getLeftTrigger().setReleasedCommand(
                values -> {
                    return Extendo.INSTANCE.setMotorPower(0);
                }
        );

        /// BASE DRIVER----------------------------------------------------------------------------




        /// CONTROL DRIVER----------------------------------------------------------------------------

        // OUTTAKE
        gamepadManager.getGamepad2().getRightBumper().setPressedCommand(
                () -> new PassiveConditionalCommand(
                        () -> ClawIsOpen,
                        () -> new ParallelGroup(
                                Outtake.INSTANCE.close(),
                                new InstantCommand(() -> ClawIsOpen = FALSE)
                        ),
                        () -> new ParallelGroup(
                                Outtake.INSTANCE.open(),
                                new InstantCommand(() -> ClawIsOpen = TRUE)
                        )
                )
        );


        // ARM AND LIFT
        gamepadManager.getGamepad2().getLeftBumper().setPressedCommand(
                () -> new PassiveConditionalCommand(
                        () -> ArmIsGrabbing,
                        () -> new ParallelGroup(
                                OuttakeArm.INSTANCE.toChamber(),
                                Lifts.INSTANCE.toChamber(),
                                new InstantCommand(() -> ArmIsGrabbing = FALSE
                                )
                        ),
                        () -> new ParallelGroup(
                                OuttakeArm.INSTANCE.toGrab(),
                                Lifts.INSTANCE.toGround(),
                                new InstantCommand(() -> ArmIsGrabbing = TRUE),
                                new PassiveConditionalCommand(
                                        () -> ClawIsOpen,
                                        () -> new InstantCommand(() -> ClawIsOpen = TRUE),
                                        () -> new ParallelGroup(
                                                Outtake.INSTANCE.open(),
                                                new InstantCommand(() -> ClawIsOpen = TRUE)
                                        )
                                )
                        )
                )
        );

        gamepadManager.getGamepad2().getRightTrigger().setHeldCommand(
                values -> {
                    return Lifts.INSTANCE.setMotorPower(-values.doubleValue());
                }
        );


        gamepadManager.getGamepad2().getLeftTrigger().setHeldCommand(
                values -> {
                    return Lifts.INSTANCE.setMotorPower(values.doubleValue());
                }
        );

        gamepadManager.getGamepad2().getRightTrigger().setReleasedCommand(
                values -> {
                    return Extendo.INSTANCE.setMotorPower(0);
                }
        );

        gamepadManager.getGamepad2().getLeftTrigger().setReleasedCommand(
                values -> {
                    return Extendo.INSTANCE.setMotorPower(0);
                }
        );

        /// CONTROL DRIVER----------------------------------------------------------------------------
    }

    public void onUpdate() {

        telemetry.addData("Extendo current position", Extendo.INSTANCE.motor.getCurrentPosition());
        telemetry.addData("Lift current position", Lifts.INSTANCE.right.getCurrentPosition());

        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, false);
        follower.update();

        telemetry.update();
    }
}