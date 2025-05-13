package OpModes;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.pedropathing.localization.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name="Tamos TeleOp",group="Linear OpMode")
public class TamosTeleop extends LinearOpMode {
    // Intake ------------------------------

    private Servo Intake = null;
    private Servo IntakeRotationServo = null;
    private Servo IntakeDownServo = null;

    public static double IntakeTargetPosition = 0;

    public static double IntakeDownPosition = 0.45;
    public static double IntakeUpPosition = 0.7;

    public static double IntakeClawClose = 0.23;
    public static double IntakeClawOpen = 0.37;

    public static double IntakeRightPosition = 0.7;
    public static double IntakeNormalPosition = 0;




    private boolean isClawing = false;
    private double rotateServoPosition = 0;
    public static double rotateServoSpeed = 0.1;
    private boolean isTook = false;



    // Outtake -----------------------------
    private Servo Outtake = null;
    private Servo ArmL = null;
    private Servo ArmR = null;

    private boolean isOuttakeOpen = false;

    public static double clawOpen = 0.8;
    public static double clawClose = 1;

    public static double armTakeSamplePose = 0;
    public static double armBringSamplePose = 0.9;
    public static double servoPos = 0;

    // Base Motors ---------------------------
    Motor Lfront = null;
    Motor Rfront = null;
    Motor Rback = null;
    Motor Lback = null;

    // Extendo Motor---------------------------

    private Motor extendoMotor = null;
    PIDController extendoPidController = new PIDController(0.01, 0, 0);

    public static double extendoTargetPosition = 0;
    double extendoMaxPosition = 2100;
    public static double extendoTargetChangeSpeed = 7000;

    public static double extendoIntakePos = 1100;

    private boolean isExtend = false;

    // Lift Motors-----------------------------
    PIDController leftLiftPidController = new PIDController(0.01, 0, 0);
    PIDController rightLiftPidController = new PIDController(0.01, 0, 0);

    public static double liftTargetPosition = 0;
    double liftMaxTargetPosition = 3000;
    public static double liftTargetChangeSpeed = 3000;

    private Motor leftLift = null;
    private Motor rightLift = null;

    // Lift variables ------------------------------------------------------------------------------

    public static double liftTakeSamplePose = 0;
    public static double liftBringSamplePose = 1300;



    ElapsedTime elapsedTimer = new ElapsedTime();

    ElapsedTime elapsedIntakeTime = new ElapsedTime();


    GoBildaPinpointDriver odo = null;

    //state variables-------------------------------------------------------------------------------

    int isClawingState = 0;
    int isExtendState = 0;


    // Times -----------------------------------------------
    public static int TIME_TO_CLOSE = 150;
    public static int TIME_TO_UP = 200;
    @Override
    public void runOpMode() throws InterruptedException {


        // Parts init ----------------------------------------

        GamepadEx driver1 = new GamepadEx(gamepad1);
        GamepadEx driver2 = new GamepadEx(gamepad2);

        Intake = hardwareMap.get(Servo.class, "inTake");
        IntakeDownServo = hardwareMap.get(Servo.class,"inTakeArm");
        IntakeRotationServo = hardwareMap.get(Servo.class,"inTakeRotate");

        leftLift = new Motor(hardwareMap, "liftL");
        rightLift = new Motor(hardwareMap, "liftR");

        Outtake = hardwareMap.get(Servo.class,"Outtake");
        ArmL = hardwareMap.get(Servo.class, "ServoArmL");
        ArmR = hardwareMap.get(Servo.class, "ServoArmR");


        // Extendo ---------------------------------------------------------------------------------
        extendoMotor = new Motor(hardwareMap, "extender",Motor.GoBILDA.RPM_312);

        extendoMotor.setInverted(true);
        extendoMotor.setRunMode(Motor.RunMode.RawPower);
        extendoMotor.resetEncoder();

        Lfront = new Motor(hardwareMap,"rightBack",Motor.GoBILDA.RPM_435);
        Rfront = new Motor(hardwareMap,"leftFront",Motor.GoBILDA.RPM_435);
        Lback = new Motor(hardwareMap,"leftBack",Motor.GoBILDA.RPM_435);
        Rback = new Motor(hardwareMap,"rightFront",Motor.GoBILDA.RPM_435);


        Lfront.setInverted(true);
        Lback.setInverted(true);
        Rback.setInverted(true);
        Rfront.setInverted(true);


        double kP = 0.05;
        double kI = 0.005;
        double kD = 0.001;

        Lfront.setVeloCoefficients(kP, kI, kD);
        Rfront.setVeloCoefficients(kP, kI, kD);
        Lback .setVeloCoefficients(kP, kI, kD);
        Rback .setVeloCoefficients(kP, kI, kD);


        ArmL.setDirection(Servo.Direction.FORWARD);
        ArmR.setDirection(Servo.Direction.REVERSE);

        ArmL.setPosition(0);
        ArmR.setPosition(0);

        //Lfront.setVeloCoefficients();

        MecanumDrive drive = new MecanumDrive(
                Lfront,
                Rfront,
                Lback,
                Rback
        );




        // Parametres ------------------------------

        leftLift.setInverted(false);
        rightLift.setInverted(true);
        leftLift.setRunMode(Motor.RunMode.RawPower);
        rightLift.setRunMode(Motor.RunMode.RawPower);

        rightLift.resetEncoder();
        leftLift.resetEncoder();

        GamepadEx driverOp = new GamepadEx(gamepad1);

        odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        odo.recalibrateIMU();
        odo.resetPosAndIMU();


        IntakeDownServo.setPosition(0.9);

        waitForStart();

        while(opModeIsActive()){

            double elapsedTime = elapsedTimer.milliseconds() / 1000.0;
            elapsedTimer.reset();
            // Intake ----------------------------------------------------------------------------->

            double extendoInputPower = 0;

            if (gamepad1.left_trigger > 0 ) {
                extendoInputPower = -0.2;
            }
            if (gamepad1.right_trigger > 0 ) {
                extendoInputPower = 0.2;
            }


            if (extendoTargetPosition < 0 && !gamepad1.back)
                extendoTargetPosition = 0;
            else if (extendoTargetPosition > extendoMaxPosition)
                extendoTargetPosition = extendoMaxPosition;

            extendoTargetPosition += elapsedTime * extendoInputPower * extendoTargetChangeSpeed;

            double extendoCurrent = extendoMotor.getCurrentPosition();
            double extendoOutputPower = extendoPidController.calculate(extendoCurrent, extendoTargetPosition);

            extendoMotor.set(extendoOutputPower);

            // Lift ------------------------------------------------------------------------------->
            double liftPower = 0;

            if (gamepad2.left_trigger > 0)
                liftPower = -1;
            if (gamepad2.right_trigger > 0)
                liftPower = 1;

            if (liftTargetPosition < 0 && !gamepad2.back)
                liftTargetPosition = 0;
            else if (liftTargetPosition > liftMaxTargetPosition)
                liftTargetPosition = liftMaxTargetPosition;


            liftTargetPosition += elapsedTime * liftPower * liftTargetChangeSpeed;

            double leftLiftCurrent = leftLift.getCurrentPosition();
            double leftLiftPower = leftLiftPidController.calculate(leftLiftCurrent, liftTargetPosition);

            double rightLiftCurrent = rightLift.getCurrentPosition();
            double rightLiftPower = rightLiftPidController.calculate(rightLiftCurrent, liftTargetPosition);

            leftLift.set(leftLiftPower);
            rightLift.set(rightLiftPower);


            odo.update(GoBildaPinpointDriver.readData.ONLY_UPDATE_HEADING);

            if (isExtend){
                drive.driveFieldCentric(
                        driverOp.getLeftX(),
                        driverOp.getLeftY(),
                        driverOp.getRightX() /4,
                        Math.toDegrees(odo.getHeading()),// gyro value passed in here must be in degrees
                        false
                );
            }
            else{
                drive.driveFieldCentric(
                        driverOp.getLeftX(),
                        driverOp.getLeftY(),
                        driverOp.getRightX(),
                        Math.toDegrees(odo.getHeading()),// gyro value passed in here must be in degrees
                        false
                );
            }

            //Binds --------------------------------------------------------------------------------

            if (driver1.wasJustPressed(GamepadKeys.Button.A) && !isClawing && isClawingState == 0 && !isTook){     //bring sample
                isClawing = !isClawing;
                isClawingState = 0;
            }
            else if(driver1.wasJustPressed(GamepadKeys.Button.A) && isTook){
                isTook = !isTook;
                Intake.setPosition(IntakeClawOpen);
            }
            if (driver1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER) && rotateServoPosition > 0){    //turn right
                rotateServoPosition -= rotateServoSpeed;
            }

            else if (driver1.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER) && rotateServoPosition <=1){
                rotateServoPosition += rotateServoSpeed;
            }

            if (driver1.wasJustPressed(GamepadKeys.Button.X) && !isExtend){
                isExtend = true;
                isExtendState = 0;
            }



            if (driver2.wasJustPressed(GamepadKeys.Button.A)){
                isOuttakeOpen = !isOuttakeOpen;
            }



            // Outtake logics -----------------------------------------------------------------------
            if (isOuttakeOpen){
                Outtake.setPosition(clawOpen);
            }else{
                Outtake.setPosition(clawClose);
            }

            if (driver2.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
                liftTargetPosition = liftTakeSamplePose;
                IntakeTargetPosition = armTakeSamplePose;
            }
            if (driver2.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
                liftTargetPosition = liftBringSamplePose;
                IntakeTargetPosition = armBringSamplePose;
            }


            ArmL.setPosition(IntakeTargetPosition);
            ArmR.setPosition(IntakeTargetPosition);

            // Extend logics ----------------------------------------------------------------------------------

            if (isExtend){
                switch (isExtendState){
                    case 0:
                        extendoTargetPosition = extendoIntakePos;
                        isExtendState = 1;
                        break;
                    case 1:
                        if (driver1.wasJustPressed(GamepadKeys.Button.X)){
                            extendoTargetPosition = 0;
                            isExtend = false;
                            isExtendState = 0;
                            rotateServoPosition = 0;
                        }
                        break;
                }
            }

            // Intake Binds logic --------------------------------------------------------------------------------
            IntakeRotationServo.setPosition(rotateServoPosition);

            if (isClawing){
                switch (isClawingState){
                    case 0:
                        IntakeDownServo.setPosition(IntakeDownPosition);
                        elapsedIntakeTime.reset();
                        isClawingState = 1;
                        break;

                    case 1:
                        if (elapsedIntakeTime.milliseconds() > TIME_TO_CLOSE){
                            Intake.setPosition(IntakeClawClose);
                            elapsedIntakeTime.reset();
                            isClawingState = 2;
                        }
                        break;
                    case 2:
                        if(elapsedIntakeTime.milliseconds() > TIME_TO_UP){
                            IntakeDownServo.setPosition(IntakeUpPosition);
                            elapsedIntakeTime.reset();
                            isClawing = false;
                            isClawingState = 0;
                            isTook = true;
                        }
                        break;
                }
            }


            telemetry.addData("Lift pose",leftLift.getCurrentPosition());
            telemetry.addData("Right pose",rightLift.getCurrentPosition());
            telemetry.addData("Extendo",extendoTargetPosition);
            telemetry.addData("Heading",odo.getHeading());
            telemetry.addData("Lfront",Lfront.getVeloCoefficients());
            telemetry.addData("Rfront",Rfront.getVeloCoefficients());
            telemetry.addData("Lback",Lback.getVeloCoefficients());
            telemetry.addData("Rback",Rback.getVeloCoefficients());
            telemetry.update();

            driver1.readButtons();
            driver2.readButtons();

        }

    }
}
