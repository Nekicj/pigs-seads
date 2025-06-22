package Controllers;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class ExtendController {
    private Servo extendArmLeft = null;
    private Servo extendArmRight = null;


    public static enum Positions{
        EXTEND_MAX(0.3);




        private final double position;
        Positions(double pos) {this.position = pos;}


        public double getPos() {return position;}

    }


    public void initialize(HardwareMap hardwareMap){
        extendArmLeft = hardwareMap.get(Servo.class,"Lkuz");
        extendArmRight = hardwareMap.get(Servo.class,"RKus");

        extendArmLeft.setDirection(Servo.Direction.FORWARD);
        extendArmRight.setDirection(Servo.Direction.REVERSE);

    }

    public void setTargetPosition(double target){
        if(target <=Positions.EXTEND_MAX.getPos()){
            extendArmRight.setPosition(target);
            extendArmLeft.setPosition(target);
        }
        else{

            extendArmRight.setPosition(Positions.EXTEND_MAX.getPos());
            extendArmLeft.setPosition(Positions.EXTEND_MAX.getPos());
        }
    }
}
