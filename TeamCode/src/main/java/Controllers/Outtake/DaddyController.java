package Controllers.Outtake;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.pedropathing.localization.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class DaddyController {

    private Servo Outtake = null;
    private Servo ArmL = null;
    private Servo ArmR = null;

    public static double clawOpen = 0.8;
    public static double clawClose = 1;

    public static double armTakeSamplePose = 0.07;
    public static double armBringSamplePose = 0.83;


    PIDController leftLiftPidController = new PIDController(0.01, 0, 0);
    PIDController rightLiftPidController = new PIDController(0.01, 0, 0);

    public static double liftTargetPosition = 0;
    double liftMaxTargetPosition = 3000;
    public static double liftTargetChangeSpeed = 3000;

    ElapsedTime elapsedTimer = new ElapsedTime();

    private Motor leftLift = null;
    private Motor rightLift = null;

    // Lift variables ------------------------------------------------------------------------------

    public static double liftTakeSamplePose = 0;
    public static double liftBringSamplePose = 1300;

    public DaddyController(HardwareMap hardwareMap){
        leftLift = new Motor(hardwareMap, "liftL");
        rightLift = new Motor(hardwareMap, "liftR");

        Outtake = hardwareMap.get(Servo.class,"Outtake");
        ArmL = hardwareMap.get(Servo.class, "ServoArmL");
        ArmR = hardwareMap.get(Servo.class, "ServoArmR");

        ArmL.setDirection(Servo.Direction.FORWARD);
        ArmR.setDirection(Servo.Direction.REVERSE);

        leftLift.setInverted(false);
        rightLift.setInverted(true);
        leftLift.setRunMode(Motor.RunMode.RawPower);
        rightLift.setRunMode(Motor.RunMode.RawPower);

        rightLift.resetEncoder();
        leftLift.resetEncoder();

        Outtake.setPosition(clawClose);
        ArmL.setPosition(armTakeSamplePose);
        ArmR.setPosition(armTakeSamplePose);
    }

    public void OuttakeTake() {
        Outtake.setPosition(clawOpen);
        ArmL.setPosition(armTakeSamplePose);
        ArmR.setPosition(armTakeSamplePose);
        liftTargetPosition = liftTakeSamplePose;
    }

    public void OuttakeClip() {
        Outtake.setPosition(clawClose);
        ArmL.setPosition(armBringSamplePose);
        ArmR.setPosition(armBringSamplePose);
        liftTargetPosition = liftBringSamplePose;
    }

    public void ClClaw() {
        Outtake.setPosition(clawClose);
    }

    public void updateScriptedActions() {
        double elapsedTime = elapsedTimer.milliseconds() / 1000.0;
        elapsedTimer.reset();
        double liftPower = 0;
        if (liftTargetPosition < 0)
            liftTargetPosition = 0;
        else if (liftTargetPosition > liftMaxTargetPosition)
            liftTargetPosition = liftMaxTargetPosition;


        liftTargetPosition += elapsedTime * liftPower * liftTargetChangeSpeed;

        double leftLiftCurrent = leftLift.getCurrentPosition();
        double leftLiftPower = leftLiftPidController.calculate(leftLiftCurrent, liftTargetPosition);

        double rightLiftCurrent = rightLift.getCurrentPosition();
        double rightLiftPower = rightLiftPidController.calculate(rightLiftCurrent, liftTargetPosition);

        leftLift.set(leftLiftPower);
        rightLift.set(rightLiftPower);
    }
}

