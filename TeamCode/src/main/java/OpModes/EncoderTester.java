//package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Four Encoder Test", group = "Test")
public class EncoderTester extends LinearOpMode {
    @Override
    public void runOpMode() {
        // Подключаем моторы с энкодерами
        DcMotor motor1 = hardwareMap.get(DcMotor.class, "rightBack");
        DcMotor motor2 = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor motor3 = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor motor4 = hardwareMap.get(DcMotor.class, "rightFront");

        // Сбрасываем энкодеры
        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor4.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Включаем энкодеры
        motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Motor 1 Encoder", motor1.getCurrentPosition());
            telemetry.addData("Motor 2 Encoder", motor2.getCurrentPosition());
            telemetry.addData("Motor 3 Encoder", motor3.getCurrentPosition());
            telemetry.addData("Motor 4 Encoder", motor4.getCurrentPosition());
            telemetry.update();
        }
    }
}
