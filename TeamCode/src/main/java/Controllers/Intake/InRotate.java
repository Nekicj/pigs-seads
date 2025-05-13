package Controllers.Intake;

import com.qualcomm.robotcore.hardware.Servo;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition;


public class InRotate extends Subsystem {
    // BOILERPLATE
    public static final InRotate INSTANCE = new InRotate();
    private InRotate() { }

    // USER CODE
    public Servo rotate;

    public String name = "inTake";

    /// POSITIONS

    public double posParallel = 0;
    public double posHypotenuse = 0.5;
    public double posPerpendecular = 1;

    /// POSITIONS

    public Command toPosition(double pos) {
        return new ServoToPosition(
                rotate,
                pos,
                this);
    }

    public Command toParallel() {
        return new ServoToPosition(
                rotate,
                posParallel,
                this
        );
    }

    public Command toHypotenuse() {
        return new ServoToPosition(
                rotate,
                posHypotenuse,
                this
        );
    }

    public Command toPerpendecular() {
        return new ServoToPosition(
                rotate,
                posPerpendecular,
                this
        );
    }

    @Override
    public void initialize() {
        rotate = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, name);
    }
}
