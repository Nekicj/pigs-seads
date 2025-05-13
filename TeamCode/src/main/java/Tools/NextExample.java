package Tools;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rowanmcalpin.nextftc.pedro.PedroOpMode;

import Controllers.Outtake.Lifts;

@Disabled
@TeleOp (name = "PID")
public class NextExample extends PedroOpMode {


    public NextExample() {
        super (
                Lifts.INSTANCE
        );
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onStartButtonPressed() {

    }
 
    public void onUpdate() {

    }
}