package Controllers.Outtake;

import com.qualcomm.robotcore.hardware.Servo;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition;


public class Outtake extends Subsystem {
    // BOILERPLATE
    public static final Outtake INSTANCE = new Outtake();
    private Outtake() { }

    // USER CODE
    public Servo outtake;
    public String name = "Outtake";

    /// POSITIONS
    public double posOpen = 0.2;
    public double posClose = 0.64;

    /// COMMANDS
    public Command open() {
        return new ServoToPosition(
                outtake,
                posOpen,
                this);
    }

    public Command close() {
        return new ServoToPosition(
                outtake,
                posClose,
                this);
    }

    @Override
    public void initialize() {
        outtake = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, name);
    }
}