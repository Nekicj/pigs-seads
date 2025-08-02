package Controllers;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.pedropathing.localization.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class BaseController {
    public static double KP = 0.05;
    public static double KI = 0.0001;
    public static double KD = 0.001;

    public static double kS = 0.1;
    public static double kV = 0.02;
    public static double kA = 0.001;

    public static double LBackKP = 0.05;
    public static double LBackKI = 0.0001;
    public static double LBackKD = 0.001;

    public static double LBackkS = 0.1;
    public static double LBackkV = 0.02;
    public static double LBackkA = 0.001;

    Motor Lfront = null;
    Motor Rfront = null;
    Motor Rback = null;
    Motor Lback = null;

    GoBildaPinpointDriver odo = null;


    MecanumDrive drive;


    public void initialize(HardwareMap hardwareMap){
        Lfront = new Motor(hardwareMap,"LFront",Motor.GoBILDA.RPM_435);
        Rfront = new Motor(hardwareMap,"RFront",Motor.GoBILDA.RPM_435);
        Lback = new Motor(hardwareMap,"LBack",Motor.GoBILDA.RPM_435);
        Rback = new Motor(hardwareMap,"RBack",Motor.GoBILDA.RPM_435);

        Lfront.setInverted(true);
        Lback.setInverted(true);
        Rback.setInverted(true);
        Rfront.setInverted(true);

//        Lfront.setRunMode(Motor.RunMode.VelocityControl);
//        Rfront.setRunMode(Motor.RunMode.VelocityControl);
//        Lback.setRunMode(Motor.RunMode.VelocityControl);
//        Rback.setRunMode(Motor.RunMode.VelocityControl);
//
//        Lfront.setFeedforwardCoefficients(kS,kV,kA);
//        Rfront.setFeedforwardCoefficients(kS,kV,kA);
//        Lback.setFeedforwardCoefficients(LBackkS,LBackkV,LBackkA);
//        Rback.setFeedforwardCoefficients(kS,kV,kA);
//
//        Lfront.setVeloCoefficients(KP, KI, KD);
//        Rfront.setVeloCoefficients(KP, KI, KD);
//        Lback.setVeloCoefficients(LBackKP, LBackKI, LBackKD);
//        Rback.setVeloCoefficients(KP, KI, KD);
//
//        Lfront.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
//        Rfront.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
//        Lback.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
//        Rback.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        drive = new MecanumDrive(
                Lfront,
                Rfront,
                Lback,
                Rback
        );



        odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        odo.recalibrateIMU();
        odo.resetPosAndIMU();
    }

    public void update(double leftX,double leftY,double rightX, double turnCoeff,boolean squareInput,boolean isFieldCentric){
        odo.update(GoBildaPinpointDriver.readData.ONLY_UPDATE_HEADING);
        if (isFieldCentric){
            drive.driveFieldCentric(
                    leftX,
                    leftY,
                    rightX /turnCoeff,
                    Math.toDegrees( odo.getHeading()),
                    squareInput
            );
        }else {
            drive.driveRobotCentric(
                    leftX,
                    leftY,
                    rightX /turnCoeff,
                    squareInput
            );
        }

    }
}
