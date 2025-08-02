package Prod;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Wheel Direction Test", group = "Test")
public class BaseMotorsDirectionTest extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private final ElapsedTime timer = new ElapsedTime();

    // Цвета для визуального отображения
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    @Override
    public void runOpMode() {
        // Инициализация моторов
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Настройка режимов
        for (DcMotor motor : new DcMotor[]{frontLeft, frontRight, backLeft, backRight}) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        // Ожидание начала
        telemetry.addData("Status", "Initialized - Press PLAY to start test");
        telemetry.update();
        waitForStart();

        // Тест 1: Проверка подключения
        telemetry.addLine("TEST 1: MOTOR CONNECTION CHECK");
        telemetry.addData("Front Left", frontLeft.getConnectionInfo());
        telemetry.addData("Front Right", frontRight.getConnectionInfo());
        telemetry.addData("Back Left", backLeft.getConnectionInfo());
        telemetry.addData("Back Right", backRight.getConnectionInfo());
        telemetry.addLine("All motors should show connection info");
        telemetry.update();
        sleep(3000);

        // Тест 2: Проверка направлений вращения
        telemetry.addLine("\nTEST 2: INDIVIDUAL WHEEL DIRECTION");
        telemetry.addLine("Press corresponding button to test each wheel");
        telemetry.addLine("A: Front Left | B: Front Right | X: Back Left | Y: Back Right");
        telemetry.addLine("Each wheel should rotate FORWARD when pressed");
        telemetry.update();

        while (opModeIsActive() && timer.seconds() < 15) {
            // Управление отдельными колесами
            testWheel(frontLeft, "Front Left", gamepad1.a);
            testWheel(frontRight, "Front Right", gamepad1.b);
            testWheel(backLeft, "Back Left", gamepad1.x);
            testWheel(backRight, "Back Right", gamepad1.y);

            // Тест 3: Синхронное движение
            if (gamepad1.left_bumper) {
                telemetry.addLine("\nTEST 3: ALL WHEELS FORWARD");
                setAllPowers(0.3);
            } else if (gamepad1.right_bumper) {
                telemetry.addLine("\nTEST 4: ALL WHEELS REVERSE");
                setAllPowers(-0.3);
            } else if (gamepad1.left_trigger > 0.5) {
                telemetry.addLine("\nTEST 5: STRAFE LEFT");
                strafeLeft(0.3);
            } else if (gamepad1.right_trigger > 0.5) {
                telemetry.addLine("\nTEST 6: STRAFE RIGHT");
                strafeRight(0.3);
            } else {
                setAllPowers(0);
            }

            telemetry.update();
        }

        // Остановка моторов
        setAllPowers(0);
        telemetry.addData("Status", "Test Complete");
        telemetry.update();
        sleep(2000);
    }

    private void testWheel(DcMotor motor, String name, boolean buttonPressed) {
        double power = buttonPressed ? 0.3 : 0.0;
        motor.setPower(power);

        // Визуальная индикация правильного направления
        String status = (power > 0) ? "FORWARD" : "STOPPED";
        String color = (power > 0) ? GREEN : RESET;
        telemetry.addData(name, color + status + RESET);
    }

    private void setAllPowers(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    private void strafeLeft(double power) {
        frontLeft.setPower(-power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(-power);
    }

    private void strafeRight(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(-power);
        backLeft.setPower(-power);
        backRight.setPower(power);
    }
}