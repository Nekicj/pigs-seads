package Prod;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name="Kuz Servos",group="Prod")
public class KuzServos extends LinearOpMode {
    private Servo KuzLeft = null;
    private Servo KuzRight = null;


    public static double servoKusPose= 0.5;

    GamepadEx driver1 = null;


    @Override
    public void runOpMode() throws InterruptedException {

        KuzLeft = hardwareMap.get(Servo.class,"Lkuz");
        KuzRight = hardwareMap.get(Servo.class,"RKus");

        KuzLeft.setDirection(Servo.Direction.FORWARD);
        KuzRight.setDirection(Servo.Direction.REVERSE);


        driver1 = new GamepadEx(gamepad1);


        waitForStart();
        while (opModeIsActive()){
            if (driver1.wasJustPressed(GamepadKeys.Button.B)){
                KuzLeft.setPosition(servoKusPose);
                KuzRight.setPosition(servoKusPose);
            }


            driver1.readButtons();
        }

    }
}
