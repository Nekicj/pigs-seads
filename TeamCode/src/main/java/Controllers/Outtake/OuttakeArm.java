package Controllers.Outtake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.control.coefficients.PIDCoefficients;
import com.rowanmcalpin.nextftc.core.control.controllers.PIDFController;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.MultipleServosToPosition;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition;

import java.util.List;

public class OuttakeArm extends Subsystem {
    // BOILERPLATE
    public static final OuttakeArm INSTANCE = new OuttakeArm();

    private OuttakeArm() { }

    // USER CODE
    public Servo right;
    public Servo left;

    public String nameRight = "ServoArmR";
    public String nameLeft = "ServoArmL";

    /// POSITIONS
    public double posChamber = 0;
    public double posGrab = 0.84;

    /// COMMANDS
    public Command toChamber() {
        return new MultipleServosToPosition(
                List.of(right, left),
                posChamber,
                this
        );
    }

    public Command toGrab() {
        return new MultipleServosToPosition(
                List.of(right, left),
                posGrab,
                this
        );
    }


    public Command setPosition(double pos) {
        return new MultipleServosToPosition(
                List.of(right, left),
                pos,
                this
        );
    }

    @Override
    public void initialize() {
        right = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, nameRight);
        left = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, nameLeft);

        left.setDirection(Servo.Direction.REVERSE);
    }
}