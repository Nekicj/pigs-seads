package Controllers.Intake;

import com.qualcomm.robotcore.hardware.Servo;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition;

public class IntakeArm extends Subsystem {
    // BOILERPLATE
    public static final IntakeArm INSTANCE = new IntakeArm();
    private IntakeArm() { }

    // USER CODE
    public Servo arm;

    public String name = "inTakeRotate";

    public Command down() {
        return new ServoToPosition(
                arm, // SERVO TO MOVE
                0, // POSITION TO MOVE TO
                this); // IMPLEMENTED SUBSYSTEM
    }

    public Command transfer() {
        return new ServoToPosition(
                arm, // SERVO TO MOVE
                1, // POSITION TO MOVE TO
                this); // IMPLEMENTED SUBSYSTEM
    }

    public Command up() {
        return new ServoToPosition(
                arm,
                0.5,
                this);
    }

    @Override
    public void initialize() {
        arm = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, name);

        //left.setDirection(Servo.Direction.REVERSE);
    }
}
