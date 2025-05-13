package Controllers.Outtake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.utility.InstantCommand;
import com.rowanmcalpin.nextftc.core.control.coefficients.PIDCoefficients;
import com.rowanmcalpin.nextftc.core.control.controllers.PIDFController;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorGroup;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.SetPower;

public class Lifts extends Subsystem {

    public static final Lifts INSTANCE = new Lifts();

    private Lifts() { }

    public MotorEx left;
    public MotorEx right;

    public MotorGroup motor;
    public String nameLeft = "liftL";
    public String nameRight = "liftR";

    /// PID
    public PIDFController controller = new PIDFController(0.01, 0, 0);

    /// POSITIONS
    public double posChamber = 1550;
    public double posBasket = 3150;

    /// HIGHEST 3150

    /// COMMANDS
    public Command toGround() {
        return new RunToPosition(
                motor,
                0,
                controller,
                this
        );
    }

    public Command toChamber() {
        return new RunToPosition(
                motor,
                posChamber,
                controller,
                this);
    }

    public Command toBasket() {
        return new RunToPosition(
                motor,
                posBasket,
                controller,
                this);
    }

    public Command toReset() {
        return new InstantCommand(
                () -> {
                    right.setCurrentPosition(0);
                }
        );
    }

    public Command setMotorPower(double power) {
        return new SetPower(
                motor,
                power,
                this
        );
    }

    @Override
    public Command getDefaultCommand() {
        return new HoldPosition(motor, controller, this);
    }

    @Override
    public void initialize() {
        left = new MotorEx(nameLeft).reverse();
        right = new MotorEx(nameRight);
        motor = new MotorGroup(left, right);
    }
}
