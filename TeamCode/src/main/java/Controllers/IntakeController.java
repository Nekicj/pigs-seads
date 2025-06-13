package Controllers;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class IntakeController {
    private Servo intakeClaw = null;
    private Servo clawRotate = null;
    private Servo intakeRotate = null;
    private Servo intakeArm = null;
    private Servo intakeKrutilka = null;

    public static double INTAKE_ARM_TRANSFER_A = 1;
    public static double INTAKE_KRUTILKA_TRANSFER_A = 0.65;

    public static double rotateIntakeSpeed = 0.006;
    public static double intakeRotatePos = 0.46;

    private double clawRotateCounter = 4;

    public static enum Servos{
        CLAW_OPEN(0.4),
        CLAW_CLOSE(0.3),

        INTAKE_ARM_AIM(0.72),
        INTAKE_KRUTILKA_AIM(0.1),

        INTAKE_ARM_TAKE(0.67),
        INTAKE_KRUTILKA_TAKE(0.08),

        INTAKE_ARM_TRANSFER(INTAKE_ARM_TRANSFER_A),
        INTAKE_KRUTILKA_TRANSFER(INTAKE_KRUTILKA_TRANSFER_A),

        INTAKE_ROTATE_MIDDLE(0.46),

        INTAKE_CLAW_ROTATE_1(1),
        INTAKE_CLAW_ROTATE_2(0.82),
        INTAKE_CLAW_ROTATE_3(0.7),
        INTAKE_CLAW_ROTATE_4(0.5),
        INTAKE_CLAW_ROTATE_5(0.3),
        INTAKE_CLAW_ROTATE_6(0.16),
        INTAKE_CLAW_ROTATE_7(0);


        private final double position;
        Servos(double pos) {this.position = pos;}


        public double getPos() {return position;}

    }

    public void initialize(HardwareMap hardwareMap,String clawServoName,String clawRotateName,String intakeRotateName,String intakeArmName,String intakeKrutilkaName){
        initialize(hardwareMap,clawServoName, clawRotateName,intakeRotateName,intakeArmName,intakeKrutilkaName,false);
    }

    public void initialize(HardwareMap hardwareMap,String clawServoName, String clawRotateName,String intakeRotateName,String intakeArmName,String intakeKrutilkaName,boolean isClawOpen){
        intakeClaw = hardwareMap.get(Servo.class,clawServoName);
        clawRotate = hardwareMap.get(Servo.class,clawRotateName);
        intakeRotate = hardwareMap.get(Servo.class,intakeRotateName);
        intakeArm =  hardwareMap.get(Servo.class,intakeArmName);
        intakeKrutilka = hardwareMap.get(Servo.class,intakeKrutilkaName);

        clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_4.getPos());


        if (!isClawOpen) intakeClaw.setPosition(Servos.CLAW_CLOSE.getPos());

    }

    public void setClawOpen(){
        intakeClaw.setPosition(Servos.CLAW_OPEN.getPos());
    }

    public void setClawClose(){
        intakeClaw.setPosition(Servos.CLAW_CLOSE.getPos());
    }

    public void setIntakeAim(){
        intakeArm.setPosition(Servos.INTAKE_ARM_AIM.getPos());
        intakeKrutilka.setPosition(Servos.INTAKE_KRUTILKA_AIM.getPos());
    }

    public void setIntakeTake(){
        intakeArm.setPosition(Servos.INTAKE_ARM_TAKE.getPos());
        intakeKrutilka.setPosition(Servos.INTAKE_KRUTILKA_TAKE.getPos());

    }

    public void setIntakeToTransfer(){
        intakeArm.setPosition(Servos.INTAKE_ARM_TRANSFER.getPos());
        intakeKrutilka.setPosition(Servos.INTAKE_KRUTILKA_TRANSFER.getPos());
        intakeRotate.setPosition(Servos.INTAKE_ROTATE_MIDDLE.getPos());
    }

    public void intakeRotateControl(double left_trigger,double right_trigger){
        intakeRotatePos += left_trigger * rotateIntakeSpeed;
        intakeRotatePos -= right_trigger* rotateIntakeSpeed;

        intakeRotate.setPosition(intakeRotatePos);
    }

    public void setRotateClaw(double rotate){
        clawRotateCounter = rotate;
        updateRotateClaw();
    }
    public void rotateClaw(boolean up){
        if (up && clawRotateCounter > 1){
            clawRotateCounter -= 1;
        }else if (!up && clawRotateCounter < 7){
            clawRotateCounter +=1;
        }

        updateRotateClaw();
    }
    public void updateRotateClaw(){
        if (clawRotateCounter == 1){
            clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_1.getPos());
        }else if(clawRotateCounter == 2){
            clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_2.getPos());
        }else if(clawRotateCounter == 3){
            clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_3.getPos());
        }else if(clawRotateCounter == 4){
            clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_4.getPos());
        }else if(clawRotateCounter == 5){
            clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_5.getPos());
        }else if(clawRotateCounter == 6){
            clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_6.getPos());
        }

        else if(clawRotateCounter == 7){
            clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_7.getPos());
        }

    }

    public void setIntakeClawPosition(double position){
        intakeClaw.setPosition(position);
    }

    public void setClawRotatePosition(double position){
        clawRotate.setPosition(position);
    }


    public double getIntakeRotatePos(){return intakeRotatePos;}
    public void setIntakeRotatePos(double position){intakeRotate.setPosition(position);}

}
