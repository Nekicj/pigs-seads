package Controllers;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@Config
public class IntakeController {
    private Servo intakeClaw = null;
    private Servo clawRotate = null;
    private Servo rightIntakeArm = null;
    private Servo leftIntakeArm = null;
    private Servo intakeKrutilka = null;

    public static double INTAKE_ARM_TRANSFER_A = 0.44; // 0.4
    public static double INTAKE_KRUTILKA_TRANSFER_A = 0.38;  //0.35

    public static double rotateIntakeSpeed = 0.006;
    public static double intakeRotatePos = 0.46;

    private double clawRotateCounter = 4;

    private static final double[] CLAW_ROTATE_POSITIONS = {
            Servos.INTAKE_CLAW_ROTATE_1.getPos(),
            Servos.INTAKE_CLAW_ROTATE_2.getPos(),
            Servos.INTAKE_CLAW_ROTATE_3.getPos(),
            Servos.INTAKE_CLAW_ROTATE_4.getPos(),
            Servos.INTAKE_CLAW_ROTATE_5.getPos(),
            Servos.INTAKE_CLAW_ROTATE_6.getPos(),
            Servos.INTAKE_CLAW_ROTATE_7.getPos()
    };

    public static enum Servos{
        CLAW_OPEN(0.545),
        CLAW_CLOSE(0.498),

        INTAKE_ARM_AIM(0.22),

        INTAKE_ARM_TAKE(0.15),

        INTAKE_ARM_TRANSFER(0.64),

        INTAKE_ARM_LOW(0.3),

        INTAKE_KRUTILKA_MIDDLE(0.475),

        INTAKE_CLAW_ROTATE_1(1),
        INTAKE_CLAW_ROTATE_2(0.82),
        INTAKE_CLAW_ROTATE_3(0.7),
        INTAKE_CLAW_ROTATE_4(0.5), //MIDDLE
        INTAKE_CLAW_ROTATE_5(0.3),
        INTAKE_CLAW_ROTATE_6(0.16),
        INTAKE_CLAW_ROTATE_7(0),

        INTAKE_ARM_TEHNOZ(0.18),
        INTAKE_KRUTILKA_TEHNOZ(0.2),
        INTAKE_CLAW_ROTATE_TEHNOZ(0.16);


        private final double position;
        Servos(double pos) {this.position = pos;}


        public double getPos() {return position;}

    }

    public void initialize(HardwareMap hardwareMap,String clawServoName,String clawRotateName,String RightintakeArmName,String LeftintakeArmName,String intakeKrutilkaName){
        initialize(hardwareMap,clawServoName, clawRotateName,RightintakeArmName,LeftintakeArmName,intakeKrutilkaName,false);
    }

    public void initialize(HardwareMap hardwareMap,String clawServoName, String clawRotateName,String RightintakeArmName,String LeftintakeArmName,String intakeKrutilkaName,boolean isClawOpen){
        intakeClaw = hardwareMap.get(Servo.class,clawServoName);
        clawRotate = hardwareMap.get(Servo.class,clawRotateName);
        rightIntakeArm = hardwareMap.get(Servo.class,RightintakeArmName);
        leftIntakeArm =  hardwareMap.get(Servo.class,LeftintakeArmName);
        intakeKrutilka = hardwareMap.get(Servo.class,intakeKrutilkaName);

        leftIntakeArm.setDirection(Servo.Direction.FORWARD);
        rightIntakeArm.setDirection(Servo.Direction.REVERSE);

        clawRotate.setPosition(Servos.INTAKE_CLAW_ROTATE_4.getPos());


        if (!isClawOpen) intakeClaw.setPosition(Servos.CLAW_CLOSE.getPos());

    }

    private void safeSetPosition(Servo servo, double position) {
        if (servo != null) {
            position = Range.clip(position, 0, 1);
            servo.setPosition(position);
        }
    }

    public void setTehnoZ(){
        safeSetPosition(leftIntakeArm,Servos.INTAKE_ARM_TEHNOZ.getPos());

        safeSetPosition(intakeKrutilka,Servos.INTAKE_KRUTILKA_TEHNOZ.getPos());
        safeSetPosition(clawRotate,Servos.INTAKE_CLAW_ROTATE_TEHNOZ.getPos());
    }

    public void setIntakeAim(){
        safeSetPosition(leftIntakeArm,Servos.INTAKE_ARM_AIM.getPos());
        safeSetPosition(rightIntakeArm,Servos.INTAKE_ARM_AIM.getPos());

        safeSetPosition(intakeKrutilka,Servos.INTAKE_KRUTILKA_MIDDLE.getPos());}


    public void setIntakeTake(){
        safeSetPosition(leftIntakeArm,Servos.INTAKE_ARM_TAKE.getPos());}

    public void setIntakeToTransfer(){
        safeSetPosition(leftIntakeArm,Servos.INTAKE_ARM_TRANSFER.getPos());
        safeSetPosition(rightIntakeArm,Servos.INTAKE_ARM_TRANSFER.getPos());
        safeSetPosition(intakeKrutilka,Servos.INTAKE_KRUTILKA_MIDDLE.getPos());}

    public void setIntakeToLow(){
        safeSetPosition(leftIntakeArm,Servos.INTAKE_ARM_LOW.getPos());
        safeSetPosition(rightIntakeArm,Servos.INTAKE_ARM_LOW.getPos());
        safeSetPosition(intakeKrutilka,Servos.INTAKE_KRUTILKA_MIDDLE.getPos());}

    public void intakeRotateControl(double left_trigger,double right_trigger){
        intakeRotatePos += left_trigger * rotateIntakeSpeed;
        intakeRotatePos -= right_trigger* rotateIntakeSpeed;

        intakeRotatePos = Math.max(0, Math.min(1, intakeRotatePos));

        safeSetPosition(intakeKrutilka,intakeRotatePos);
    }

    public void setRotateClaw(double rotate) {
        clawRotateCounter = Math.max(1, Math.min(7, rotate));
        updateRotateClaw();
    }

    public void rotateClaw(boolean up) {
        clawRotateCounter = up
                ? Math.max(1, clawRotateCounter - 1)
                : Math.min(7, clawRotateCounter + 1);

        updateRotateClaw();
    }

    public void updateRotateClaw() {
        if (clawRotateCounter >= 1 && clawRotateCounter <= 7) {
            int index = (int) clawRotateCounter - 1;
            safeSetPosition(clawRotate,CLAW_ROTATE_POSITIONS[index]);
        }
    }

    public void setClawOpen(){safeSetPosition(intakeClaw,Servos.CLAW_OPEN.getPos());}
    public void setClawClose(){safeSetPosition(intakeClaw,Servos.CLAW_CLOSE.getPos());}
    public void setIntakeClawPosition(double position){safeSetPosition(intakeClaw,position);}
    public void setClawRotatePosition(double position){safeSetPosition(clawRotate,position);}
    public double getIntakeRotatePos(){return intakeRotatePos;}
    public void setIntakeRotatePos(double position){safeSetPosition(rightIntakeArm,position);}

}
