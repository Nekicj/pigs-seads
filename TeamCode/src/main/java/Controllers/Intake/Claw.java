package Controllers.Intake;

import com.qualcomm.robotcore.hardware.Servo;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition;


public class Claw extends Subsystem {

    public static final Claw INSTANCE = new Claw();
    private Claw() { }

    public Servo intake;

    public String name = "inTakeArm";

    /// POSITIONS
    public double posOpen = 0.1;
    public double posClose = 0.51;

    /// COMMANDS
    public Command open() {
        return new ServoToPosition(
                intake,
                posOpen,
                this);
    }

    public Command close() {
        return new ServoToPosition(
                intake,
                posClose,
                this);
    }

    @Override
    public void initialize() {
        intake = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, name);
    }
}