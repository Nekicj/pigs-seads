package OpModes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name="ServoTester",group="Linear OpMode")
public class ServoTest extends LinearOpMode {
    private Servo servo = null;

    public static double servoPose = 0.5;



    @Override
    public void runOpMode() throws InterruptedException {

        servo = hardwareMap.get(Servo.class, "Outtake");


        waitForStart();
        while (opModeIsActive()){
            servo.setPosition(servoPose);
        }

    }
}
