package Controllers;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.pedropathing.localization.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class BaseController {
    Motor Lfront = null;
    Motor Rfront = null;
    Motor Rback = null;
    Motor Lback = null;

    GoBildaPinpointDriver odo = null;

    private GamepadEx driver1;

    MecanumDrive drive;


    public void initialize(HardwareMap hardwareMap){
        Lfront = new Motor(hardwareMap,"leftFront",Motor.GoBILDA.RPM_435);
        Rfront = new Motor(hardwareMap,"rightFront",Motor.GoBILDA.RPM_435);
        Lback = new Motor(hardwareMap,"leftBack",Motor.GoBILDA.RPM_435);
        Rback = new Motor(hardwareMap,"rightBack",Motor.GoBILDA.RPM_435);

        Lfront.setInverted(true );
        Lback.setInverted(true);
        Rback.setInverted(true);
        Rfront.setInverted(true);

        Lfront.setRunMode(Motor.RunMode.VelocityControl);
        Rfront.setRunMode(Motor.RunMode.VelocityControl);
        Lback.setRunMode(Motor.RunMode.VelocityControl);
        Rback.setRunMode(Motor.RunMode.VelocityControl);

        drive = new MecanumDrive(
                Lfront,
                Rfront,
                Lback,
                Rback
        );

        odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        odo.recalibrateIMU();
        odo.resetPosAndIMU();

        driver1 = new GamepadEx(gamepad1);
    }

    public void update(double turnCoeff,boolean squareInput){
        odo.update(GoBildaPinpointDriver.readData.ONLY_UPDATE_HEADING);

        drive.driveFieldCentric(
                driver1.getLeftX(),
                driver1.getLeftY(),
                driver1.getRightX() / turnCoeff,
                Math.toDegrees(odo.getHeading()),
                squareInput
        );
    }
}
