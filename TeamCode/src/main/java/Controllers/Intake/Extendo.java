package Controllers.Intake;

import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.utility.InstantCommand;
import com.rowanmcalpin.nextftc.core.control.coefficients.PIDCoefficients;
import com.rowanmcalpin.nextftc.core.control.controllers.PIDFController;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.SetPower;

public class Extendo extends Subsystem {
    public static final Controllers.Intake.Extendo INSTANCE = new Extendo();
    private Extendo() { }

    public MotorEx motor;
    public String name = "extender";
    public int end = 0;

    /// PID
    public PIDFController controller = new PIDFController(0.005, 0.0, 0.0);

    /// COMMANDS
    public Command toReturn() {
        return new RunToPosition(
                motor,
                0,
                controller,
                this
        );
    }

    public Command setPosition(double pos) {
        return new RunToPosition(
            motor,
            pos,
            controller,
            this
        );
    }

    public Command setMotorPower(double power) {
        return new SetPower(
                motor,
                power,
                this
        );
    }

    public Command toReset() {
        return new InstantCommand(
                () -> {
                    motor.setCurrentPosition(0);
                }
        );
    }

    @Override
    public Command getDefaultCommand() {
        return new HoldPosition(motor, controller, this);
    }

    @Override
    public void initialize() {
        motor = new MotorEx(name);
    }

}