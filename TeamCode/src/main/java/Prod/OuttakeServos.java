package Prod;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name="Outtake Servos",group="Prod")
public class OuttakeServos extends LinearOpMode {
    private Servo servoArmLeft = null;
    private Servo servoArmRight = null;

    private Servo servoClaw = null;
    private Servo servoClawRotate = null;


    public static double servoArmPose = 0.5;
    public static double servoClawPose = 0.5;
    public static double servoClawRotatePose = 0.5;

    GamepadEx driver1 = null;



    @Override
    public void runOpMode() throws InterruptedException {

        servoArmLeft = hardwareMap.get(Servo.class, "OuttakeArmLeft");
        servoArmRight= hardwareMap.get(Servo.class, "OuttakeArmRight");

        servoArmLeft.setDirection(Servo.Direction.REVERSE);

        servoArmRight.setDirection(Servo.Direction.FORWARD);

        servoClaw = hardwareMap.get(Servo.class,"OuttakeClaw");
        servoClawRotate = hardwareMap.get(Servo.class,"ClawRotate");

        driver1 = new GamepadEx(gamepad1);


        waitForStart();
        while (opModeIsActive()){
            if (driver1.wasJustPressed(GamepadKeys.Button.B)){
                servoArmLeft.setPosition(servoArmPose);
                servoArmRight.setPosition(servoArmPose);
            }

            if (driver1.wasJustPressed(GamepadKeys.Button.X)){
                servoClaw.setPosition(servoClawPose);
            }

            if (driver1.wasJustPressed(GamepadKeys.Button.Y)){
                servoClawRotate.setPosition(servoClawRotatePose);
            }


            driver1.readButtons();
        }

    }
}
