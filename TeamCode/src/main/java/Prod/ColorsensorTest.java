package Prod;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "ColorsensorTest")
public class ColorsensorTest extends OpMode {
    private Servo IntakeClow;

    private RevColorSensorV3 color_sensor;

    @Override
    public void init() {

        IntakeClow = hardwareMap.get(Servo.class, "IntakeClaw");

        color_sensor = hardwareMap.get(RevColorSensorV3.class, "color_sensor");


        // runtime.reset();

    }

    @Override
    public void loop() {



        int red = color_sensor.red();
        int green = color_sensor.green();
        int blue = color_sensor.blue();


        boolean isDefinitelyBlue = blue > 150 && blue > red * 1.5 && blue > green * 1.5;
        if (isDefinitelyBlue) {//оуттейк движение
            IntakeClow.setPosition(0.483);
            gamepad1.setLedColor(1,0,0,50000);
        }
        if (gamepad2.dpad_down){
            IntakeClow.setPosition(0.53);



            telemetry.update();
            telemetry.addData("Red", color_sensor.red());
            telemetry.addData("Blue", color_sensor.blue());
            telemetry.addData("Green", color_sensor.green());

        }
    }
}