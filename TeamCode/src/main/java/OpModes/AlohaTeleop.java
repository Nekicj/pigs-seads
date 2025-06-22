package OpModes;


import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import Controllers.ActionsController;
import Controllers.BaseController;
import Controllers.ExtendController;

@Config
@TeleOp(name = "Main Teleop",group = "Competition")
public class AlohaTeleop extends LinearOpMode {
    private ActionsController actionsController;
    private BaseController baseController;

    private GamepadEx driver1;
    private GamepadEx driver2;

    private boolean isIntakeTaken = false;
    private boolean isExtended = false;
    private boolean isIntakeOpen = false;

    public double extendLenght = 0f;
    public static double extendSpeed = 0.003;



    @Override
    public void runOpMode(){
        driver1 = new GamepadEx(gamepad1);
        driver2 = new GamepadEx(gamepad2);

        actionsController = new ActionsController(hardwareMap);
        baseController = new BaseController();
        baseController.initialize(hardwareMap);

        telemetry.addData("Status, ","Initialized");
        waitForStart();

        while (opModeIsActive()){


            if (isExtended){baseController.update(driver1.getLeftX(),driver1.getLeftY(),driver1.getRightX(),2.5,true);}
            else{baseController.update(driver1.getLeftX(),driver1.getLeftY(),driver1.getRightX(),1,true);}

            driver1.readButtons();
            driver2.readButtons();

            if (driver2.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER))     { //TAKE THIS SHIT
                actionsController.toTakeSpecimen();}


            if (driver2.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
                actionsController.toPushSpecimen();}


            if (driver1.wasJustPressed(GamepadKeys.Button.X)){
                if (!isExtended){
                    actionsController.toIntakeAim();
                    extendLenght = ExtendController.Positions.EXTEND_MAX.getPos();
                    isExtended = true;
                    actionsController.setLiftToTransfer();
                }else{
                    isExtended = false;
                    actionsController.setRotateClaw(4);
                    actionsController.setIntakeToStandard();

                    extendLenght = 0;
                }
            }else if (driver1.wasJustPressed(GamepadKeys.Button.A) && isExtended){
                if(!isIntakeTaken){
                    actionsController.toIntakeTake();
                    isIntakeTaken = true;
                    isIntakeOpen = false;
                }else{
                    actionsController.setIntakeClaw(true);
                    isIntakeTaken = false;
                    isIntakeOpen = true;
                }
            }

            if (driver2.wasJustPressed(GamepadKeys.Button.Y) || driver1.wasJustPressed(GamepadKeys.Button.DPAD_UP)){
                isExtended = false;
                actionsController.setTransferNBusket();
                extendLenght = 0;
            }else if(driver2.wasJustPressed(GamepadKeys.Button.A) || driver1.wasJustPressed(GamepadKeys.Button.DPAD_DOWN) ){
                actionsController.setLiftToTransfer();
            }

            if (gamepad1.left_trigger > 0 && isExtended && extendLenght > 0){
                extendLenght -= extendSpeed;}


            if(gamepad1.right_trigger > 0 && isExtended && extendLenght < ExtendController.Positions.EXTEND_MAX.getPos()){
                extendLenght += extendSpeed;}


            if (driver1.wasJustPressed(GamepadKeys.Button.B) || driver1.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON) || driver1.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)){
                isIntakeOpen = !isIntakeOpen;
                actionsController.setClaws(isIntakeOpen);}

            if (driver1.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)){
                actionsController.toTehnoZ();
            }


            if (driver1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
                actionsController.clawRotate(true);
            }else if (driver1.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
                actionsController.clawRotate(false);}


            actionsController.setExtendTarget(extendLenght);
            actionsController.update();

            telemetry.addData("Status", "Running");
            telemetry.addData("isTaken",isIntakeTaken);
            telemetry.addData("Action Busy", actionsController.isBusy() ? "YES" : "NO");
            telemetry.addData("extendlength",extendLenght);

            telemetry.update();
        }
    }

}
