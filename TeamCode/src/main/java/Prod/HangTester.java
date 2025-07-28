package Prod;


import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import Controllers.ActionsController;
import Controllers.BaseController;
import Controllers.ExtendController;

@Config
@TeleOp(name = "Hang",group = "Test")
public class HangTester extends LinearOpMode {
    private ActionsController actionsController;

    private GamepadEx driver1;

    private boolean isUp = false;
    public static double liftChangeSpeed = 3000;


    @Override
    public void runOpMode(){
        driver1 = new GamepadEx(gamepad1);



        actionsController = new ActionsController(hardwareMap);


        telemetry.addData("Status, ","Initialized");
        waitForStart();

        while (opModeIsActive()){
            driver1.readButtons();

            if (gamepad1.left_trigger > 0){
                actionsController.liftManual(false,liftChangeSpeed);}
            else if (gamepad1.right_trigger > 0){
                actionsController.liftManual(true,liftChangeSpeed);}

//            if (driver1.wasJustPressed(GamepadKeys.Button.B)) {
//                isUp = !isUp;
//                actionsController.setUpGear(isUp);
//            }


            actionsController.update(gamepad2.back);

            telemetry.addData("Status", "Running");
            telemetry.addData("Action Busy", actionsController.isBusy() ? "YES" : "NO");

            telemetry.update();
        }
    }

}
