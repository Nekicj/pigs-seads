package Prod;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import Controllers.LiftController;

@Config
@TeleOp(name="LiftseXy",group = "Linear OpMode")
public class LiftsTester extends LinearOpMode {

    private Motor leftLift = null;
    private Motor rightLift = null;

    private double liftTargetPosition = 0;

    PIDController leftLiftPidController = new PIDController(0.0055, 0, 0);
    PIDController rightLiftPidController = new PIDController(0.0055, 0, 0);

    public static double liftTargetChangeSpeed = 2000;
    ElapsedTime elapsedTimer = new ElapsedTime();



    @Override
    public void runOpMode() throws InterruptedException {

        leftLift = new Motor(hardwareMap, "Llift");
        rightLift = new Motor(hardwareMap, "Rlift");


        leftLift.setInverted(true);
        leftLift.setRunMode(Motor.RunMode.RawPower);
        rightLift.setRunMode(Motor.RunMode.RawPower);

        rightLift.resetEncoder();
        leftLift.resetEncoder();
        elapsedTimer.reset();

        waitForStart();

        while (opModeIsActive()){
            double elapsedTime = elapsedTimer.milliseconds() / 1000.0;
            elapsedTimer.reset();

            double liftPower = 0;

            if (gamepad1.left_trigger > 0)
                liftTargetPosition += 50;
            if (gamepad1.right_trigger > 0)
                liftTargetPosition -= 50;


            if (liftTargetPosition < 0)
                liftTargetPosition = 0;
            else if (liftTargetPosition > 2600)
                liftTargetPosition = 2600;

            liftTargetPosition += elapsedTime * liftPower * liftTargetChangeSpeed;

            double leftLiftCurrent = leftLift.getCurrentPosition();
            double leftLiftPower = leftLiftPidController.calculate(leftLiftCurrent, liftTargetPosition);

            double rightLiftCurrent = rightLift.getCurrentPosition();
            double rightLiftPower = rightLiftPidController.calculate(rightLiftCurrent, liftTargetPosition);

//            leftLift.set(leftLiftPower);
//            rightLift.set(rightLiftPower);

            telemetry.addData("target",liftTargetPosition);
            telemetry.addData("Left pose",leftLift.getCurrentPosition());
            telemetry.addData("right pose",rightLift.getCurrentPosition());
            telemetry.update();
        }

    }
}
