package Prod;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name="Transfer Servos",group="Prod")
public class TransferServos extends LinearOpMode {

    private Servo leftservoIntakeArm = null;
    private Servo rightservoIntakeArm = null;

    private Servo servoArmLeft = null;
    private Servo servoArmRight = null;

    private Servo servoOuttakeRotate = null;

    private Servo intakeRotate = null;



    public static double servoIntakeArmPose = 0.5;

    public static double servoOuttakeArmPose = 0.5;
    public static double servoOuttakeRotatePose = 0.5;


    GamepadEx driver1 = null;



    @Override
    public void runOpMode() throws InterruptedException {

        leftservoIntakeArm = hardwareMap.get(Servo.class,"LeftIntakeArm");
        rightservoIntakeArm = hardwareMap.get(Servo.class,"RightIntakeArm");

        leftservoIntakeArm.setDirection(Servo.Direction.FORWARD);
        rightservoIntakeArm.setDirection(Servo.Direction.REVERSE);

        servoArmLeft = hardwareMap.get(Servo.class,"OuttakeArmLeft");
        servoArmRight = hardwareMap.get(Servo.class,"OuttakeArmRight");

        servoOuttakeRotate = hardwareMap.get(Servo.class,"ClawRotate");

        intakeRotate = hardwareMap.get(Servo.class,"IntakeClawRotate");


        servoArmLeft.setDirection(Servo.Direction.REVERSE);
        servoArmRight.setDirection(Servo.Direction.FORWARD);

        driver1 = new GamepadEx(gamepad1);


        waitForStart();
        while (opModeIsActive()){
            intakeRotate.setPosition(0.5);
            if (driver1.wasJustPressed(GamepadKeys.Button.B)){
                rightservoIntakeArm.setPosition(servoIntakeArmPose);

                leftservoIntakeArm.setPosition(servoIntakeArmPose);
            }

            if (driver1.wasJustPressed(GamepadKeys.Button.X)){
                servoArmLeft.setPosition(servoOuttakeArmPose);
                servoArmRight.setPosition(servoOuttakeArmPose);
            }
            if (driver1.wasJustPressed(GamepadKeys.Button.Y)){
                servoOuttakeRotate.setPosition(servoOuttakeRotatePose);
            }

            driver1.readButtons();
        }

    }
}
