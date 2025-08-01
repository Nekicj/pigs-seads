package Controllers;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.motors.Motor;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class LiftController {
    // HARDWARE ====================================================================================
    private Motor leftLift = null;
    private Motor rightLift = null;

//    private Servo gearSwitcher = null;
//
//    private Servo leftKozel = null;
//    private Servo rightKozel = null;

    public static double kP = 0.0055;
    public static double kD = 0;
    public static double kI = 0;

    PIDController leftLiftPidController = new PIDController(kP, kI, kD);
    PIDController rightLiftPidController = new PIDController(kP, kI , kD);

    // VARIABLES ===================================================================================

    public enum Position{
        HOME(0),
        SPECIMEN_TAKE(0),
        SPECIMEN_PUSH(560),
        MAX(1100),
        HANG_MAX(2130);//650

        Position(int pos){
            this.position = pos;
        }
        private int position;

        public double getPos() {
            return position;
        }

    }

    public enum SwitcherPositions{
        UP_GEAR(0.4),
        DOWN_GEAR(0.6);

        SwitcherPositions(double pos){
            this.position = pos;
        }
        private double position;

        public double getPos() {
            return position;
        }

    }

    public static double target = 0;
    public static double liftTargetChangeSpeed = 3000;
    public static double tolerance = 20;
    ElapsedTime elapsedTimer = new ElapsedTime();



    public void initialize(HardwareMap hardwareMap) {
        leftLift = new Motor(hardwareMap, "Llift");
        rightLift = new Motor(hardwareMap, "Rlift");
//        leftKozel = hardwareMap.get(Servo.class,"leftKozel");
//        rightKozel = hardwareMap.get(Servo.class,"rightKozel");
//        gearSwitcher = hardwareMap.get(Servo.class,"gearSwitcher");
//
//        leftKozel.setDirection(Servo.Direction.F\ORWARD);
//        rightKozel.setDirection(Servo.Direction.REVERSE);

        leftLift.setInverted(true);
        rightLift.setInverted(false);

        leftLift.setRunMode(Motor.RunMode.RawPower);
        rightLift.setRunMode(Motor.RunMode.RawPower);

        leftLift.resetEncoder();
        rightLift.resetEncoder();

        //leftLift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //rightLift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //leftLift.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        //rightLift.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        elapsedTimer.reset();
//        setUpGear(true);
    }


    public  void update(boolean isBack){
        update(0,isBack);
    }

    public void update(double liftPower,boolean isBack) {
        double elapsedTime = elapsedTimer.milliseconds() / 1000.0;
        elapsedTimer.reset();


        if (target < 0 && !isBack)
            target = 0;
        else if (target > Position.MAX.getPos())
            target = Position.MAX.getPos();


        target += elapsedTime * liftPower * liftTargetChangeSpeed;

        double leftLiftCurrent = leftLift.getCurrentPosition();
        double leftLiftPower = leftLiftPidController.calculate(leftLiftCurrent, target);

        double rightLiftCurrent = rightLift.getCurrentPosition();
        double rightLiftPower = rightLiftPidController.calculate(rightLiftCurrent, target);

        leftLift.set(leftLiftPower);
        rightLift.set(rightLiftPower);
    }

//    public void setUpGear(boolean setUp){
//        if (setUp){
//            gearSwitcher.setPosition(SwitcherPositions.UP_GEAR.getPos());
//        }else{
//            gearSwitcher.setPosition(SwitcherPositions.DOWN_GEAR.getPos());
//        }
//    }

//    public void setKozelPower(double power){
//        leftKozel.setPosition(power);
//    }

    public void setTargetPosition(double targetPosition){
        target = targetPosition;
    }

    public double getCurrentPosition(){
        return target;
    }

    public boolean isAtPosition() {
        return Math.abs(leftLift.getCurrentPosition() - target) < tolerance
                && Math.abs(rightLift.getCurrentPosition() - target) < tolerance;
    }


    public void showLogs(Telemetry telemetry) {
        telemetry.addData("L pos", leftLift.getCurrentPosition());
        telemetry.addData("R pos", rightLift.getCurrentPosition());
        telemetry.addData("lift target", target);
    }


}
