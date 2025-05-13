package Tools;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.configuration.annotations.DigitalIoDeviceType;

@Disabled
@Config
@TeleOp(name = "PID tuning")
public class PIDtuning extends OpMode {
    private PIDController controller;

    public static double p = 0, i = 0, d = 0;

    public static int target = 0;

    private DcMotor right, left;
    @Override
    public void init() {
        controller = new PIDController(p, i, d);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        right = hardwareMap.get(DcMotor.class, "liftR");
        left = hardwareMap.get(DcMotor.class, "liftL");
        right.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        controller.setPID(p, i, d);
        int liftPos = right.getCurrentPosition();
        double pid = controller.calculate(liftPos, target);

        right.setPower(pid);
        left.setPower(pid);

        telemetry.addData("pos ", liftPos);
        telemetry.addData("target ", target);
        telemetry.update();
    }
}