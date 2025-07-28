package Prod;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name = "ExtendoTester",group = "Test")
public class ExtendLiftTester extends LinearOpMode {
    private Motor extendoLift = null;

    public static double kP = 0.02;
    public static double kD = 0;
    public static double kI = 0.001;
    PIDController leftLiftPidController = new PIDController(kP, kI, kD);



    public static double target = 0;
    public static double liftTargetChangeSpeed = 3000;
    public static double tolerance = 20;
    ElapsedTime elapsedTimer = new ElapsedTime();

    @Override
    public void runOpMode(){
        extendoLift = new Motor(hardwareMap,"ExtendoMotor");

        extendoLift.setInverted(true);

        extendoLift.setRunMode(Motor.RunMode.RawPower);

        extendoLift.resetEncoder();

        elapsedTimer.reset();

        waitForStart();
        while(opModeIsActive()){
            double elapsedTime = elapsedTimer.milliseconds() / 1000.0;
            double liftPower = 0;

            if (gamepad1.right_trigger > 0){
                liftPower = 1;
            }else if(gamepad1.left_trigger> 0){
                liftPower = -1;
            }
            elapsedTimer.reset();


            if (target < 0 )
                target = 0;
            else if (target > 390)
                target = 390;


            target += elapsedTime * liftPower * liftTargetChangeSpeed;

            double leftLiftCurrent = extendoLift.getCurrentPosition();
            double leftLiftPower = leftLiftPidController.calculate(leftLiftCurrent, target);

            //extendoLift.set(leftLiftPower);

            telemetry.addData("position",extendoLift.getCurrentPosition());
            telemetry.update();
        }
    }
}
