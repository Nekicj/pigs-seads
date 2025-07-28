package Prod;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.R;

@Config
@TeleOp(name="Intake Servos",group="Prod")
public class IntakeServos extends LinearOpMode {
    private Servo leftServoArm = null;
    private Servo rightServoArm = null;

    private Servo servoIntakeClaw = null;
    private Servo servoClawRotate = null;

    private Servo servoKrutilka = null;


    public static double servoArmPose = 0.5;
    public static double servoIntakeRotatePose = 0.5;
    public static double servoClawPose = 0.5;
    public static double servoClawRotatePose = 0.5;

    public static double servoKrutilkaPose = 0.5;

    GamepadEx driver1 = null;



    @Override
    public void runOpMode() throws InterruptedException {

        leftServoArm = hardwareMap.get(Servo.class, "LeftIntakeArm");
        rightServoArm = hardwareMap.get(Servo.class, "RightIntakeArm");

        leftServoArm.setDirection(Servo.Direction.FORWARD);
        rightServoArm.setDirection(Servo.Direction.REVERSE);

        servoClawRotate= hardwareMap.get(Servo.class, "IntakeClawRotate");

        servoIntakeClaw = hardwareMap.get(Servo.class,"IntakeClaw");

        servoKrutilka = hardwareMap.get(Servo.class,"IntakeKrutilka");


        driver1 = new GamepadEx(gamepad1);


        waitForStart();
        while (opModeIsActive()){
            if (driver1.wasJustPressed(GamepadKeys.Button.B)){
                leftServoArm.setPosition(servoArmPose);
                rightServoArm.setPosition(servoArmPose);
                servoIntakeClaw.setPosition(servoClawPose);
            }

            if (driver1.wasJustPressed(GamepadKeys.Button.X)){
                servoClawRotate.setPosition(servoClawRotatePose);
            }

            if (driver1.wasJustPressed(GamepadKeys.Button.A)){
                servoKrutilka.setPosition(servoKrutilkaPose);
            }


            driver1.readButtons();
        }

    }
}
