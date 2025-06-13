package OpModes;


import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.pedropathing.localization.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import Controllers.ActionsController;
import Controllers.ExtendController;
import Controllers.IntakeController;
import Controllers.OuttakeController;

@Config
@TeleOp(name = "Main Teleop",group = "Competition")
public class AlohaTeleop extends LinearOpMode {
    Motor Lfront = null;
    Motor Rfront = null;
    Motor Rback = null;
    Motor Lback = null;

    GoBildaPinpointDriver odo = null;


    private ActionsController actionsController;
    private OuttakeController outtakeController;

    private GamepadEx driver1;
    private GamepadEx driver2;

    private boolean isIntakeTaken = false;
    private boolean isExtended = false;
    private boolean isIntakeOpen = false;

    private boolean isBusket = false;

    private double extendLenght = 0f;
    public static double extendSpeed = 0.006;

    @Override
    public void runOpMode(){
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

        Lfront.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        Rfront.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        Lback.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        Rback.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        double kP = 0.05;
        double kI = 0.005;
        double kD = 0.001;

        Lfront.setVeloCoefficients(kP, kI, kD);
        Rfront.setVeloCoefficients(kP, kI, kD);
        Lback .setVeloCoefficients(kP, kI, kD);
        Rback .setVeloCoefficients(kP, kI, kD);

        MecanumDrive drive = new MecanumDrive(
                Lfront,
                Rfront,
                Lback,
                Rback
        );


        odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        odo.recalibrateIMU();
        odo.resetPosAndIMU();

        driver1 = new GamepadEx(gamepad1);
        driver2 = new GamepadEx(gamepad2);
        actionsController = new ActionsController(hardwareMap);
        outtakeController = new OuttakeController();


        outtakeController.initialize(hardwareMap,
                "OuttakeClaw",
                "ClawRotate",
                "OuttakeArmLeft",
                "OuttakeArmRight",
                true);
        telemetry.addData("Status, ","Initialized");
        waitForStart();
        while (opModeIsActive()){
            if (isExtended){
                drive.driveFieldCentric(
                        driver1.getLeftX(),
                        driver1.getLeftY(),
                        driver1.getRightX() /4,
                        Math.toDegrees(odo.getHeading()),// gyro value passed in here must be in degrees
                        false
                );
            }
            else{
                drive.driveFieldCentric(
                        driver1.getLeftX(),
                        driver1.getLeftY(),
                        driver1.getRightX(),
                        Math.toDegrees(odo.getHeading()),// gyro value passed in here must be in degrees
                        false
                );
            }

            driver1.readButtons();
            driver2.readButtons();

            if (driver2.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){ //TAKE THIS SHIT
                actionsController.toTakeSpecimen();
            }

            if (driver2.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
                actionsController.toPushSpecimen();
            }

            if (driver1.wasJustPressed(GamepadKeys.Button.X)){
                if (!isExtended){
                    actionsController.toIntakeAim();
                    extendLenght = ExtendController.Positions.EXTEND_MAX.getPos();
                    isExtended = true;
                    actionsController.setLiftToTransfer();

                }else{
                    isExtended = false;
                    actionsController.setIntakeToStandard();
                    extendLenght = 0;
                }
            }else if (driver1.wasJustPressed(GamepadKeys.Button.A) && isExtended){
                if(!isIntakeTaken){
                    actionsController.toIntakeTake();
                    isIntakeTaken = true;
                }else{
                    actionsController.setIntakeClaw(true);
                    isIntakeTaken = false;
                }
            }

            if (driver2.wasJustPressed(GamepadKeys.Button.Y)){

                isExtended = false;
                actionsController.setTransferNBusket();
                extendLenght = 0;

            }

            if (gamepad1.left_trigger > 0 && isExtended && extendLenght > 0){
                extendLenght -= extendSpeed;
            }
            if(gamepad1.right_trigger > 0 && isExtended && extendLenght < ExtendController.Positions.EXTEND_MAX.getPos()){
                extendLenght += extendSpeed;
            }



            if (driver1.wasJustPressed(GamepadKeys.Button.B)){
                isIntakeOpen = !isIntakeOpen;
                actionsController.setClaws(isIntakeOpen);
            }



            if (driver1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
                actionsController.clawRotate(true);
            }else if (driver1.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
                actionsController.clawRotate(false);
            }



            actionsController.setExtendTarget(extendLenght);

            telemetry.addData("Status", "Running");
            telemetry.addData("isTaken",isIntakeTaken);
            telemetry.addData("Action Busy", actionsController.isBusy() ? "YES" : "NO");
            telemetry.addData("extendlength",extendLenght);
            telemetry.update();

            actionsController.update();

            odo.update(GoBildaPinpointDriver.readData.ONLY_UPDATE_HEADING);


        }
    }

}
