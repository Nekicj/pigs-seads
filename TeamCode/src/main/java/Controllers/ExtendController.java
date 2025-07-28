package Controllers;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public class ExtendController {
    private Motor extendoLift = null;

    public static double kP = 0.02;
    public static double kD = 0;
    public static double kI = 0.001;
    PIDController leftLiftPidController = new PIDController(kP, kI, kD);



    public enum Positions{
        HOME(0),
        EXTENDED(180),
        EXTEND_MAX(390);  //650

        Positions(int pos){
            this.position = pos;
        }
        private int position;

        public double getPos() {
            return position;
        }

    }

    public static double target = 0;
    public static double liftTargetChangeSpeed = 3000;
    public static double tolerance = 20;
    ElapsedTime elapsedTimer = new ElapsedTime();


    public void initialize(HardwareMap hardwareMap){
        extendoLift = new Motor(hardwareMap,"ExtendoMotor");

        extendoLift.setInverted(true);

        extendoLift.setRunMode(Motor.RunMode.RawPower);

        extendoLift.resetEncoder();

        elapsedTimer.reset();

    }

    public  void update(boolean isBack){
        update(0,isBack);
    }
    public void update(double liftPower,boolean isBack) {
        double elapsedTime = elapsedTimer.milliseconds() / 1000.0;
        elapsedTimer.reset();


        if (target < 0 && !isBack)
            target = 0;
        else if (target > Positions.EXTEND_MAX.getPos()){
            target = Positions.EXTEND_MAX.getPos();}


        target += elapsedTime * liftPower * liftTargetChangeSpeed;

        double leftLiftCurrent = extendoLift.getCurrentPosition();
        double leftLiftPower = leftLiftPidController.calculate(leftLiftCurrent, target);

        extendoLift.set(-leftLiftPower);
    }

    public void setTargetPosition(double setTarget){
        target = setTarget;
    }
}
