package Controllers;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import OpModes.AlohaTeleop;

@Config
public class ActionsController {
    //TIME VARIABLES ===============================================================================

    //  00Intake00
    public static double INTAKE_TIME_DOWN = 0.06;
    public static double INTAKE_TIME_TAKE = 0.06;

    //  00Transfer
    public static double TRANSFER_TO_TAKE_SAMPLE = 0.2;
    public static double TRANSFER_TO_BUSKET = 0.15;


    //==============================================================================================


    private final OuttakeController outtakeController;
    private final LiftController liftController;
    private final ExtendController extendController;

    private final IntakeController intakeController;

    private final CommandScheduler outtakeScheduler = new CommandScheduler();
    private final CommandScheduler intakeScheduler = new CommandScheduler();
    private final CommandScheduler transferSchedule = new CommandScheduler();

    public ActionsController(HardwareMap hardwareMap){
        outtakeController = new OuttakeController();
        liftController = new LiftController();
        intakeController = new IntakeController();
        extendController = new ExtendController();


        liftController.initialize(hardwareMap);
        outtakeController.initialize(hardwareMap,
                "OuttakeClaw",
                "ClawRotate",
                "OuttakeArmLeft",
                "OuttakeArmRight",
                false);
        intakeController.initialize(hardwareMap,
                "IntakeClaw",
                "IntakeClawRotate",
                "IntakeRotate",
                "IntakeArm",
                "IntakeKrutilka");

        extendController.initialize(hardwareMap);
    }

    public void update(){
        liftController.update();
        outtakeScheduler.update();
        intakeScheduler.update();
        transferSchedule.update();
    }

    public void toTakeSpecimen(){

        outtakeScheduler.clearQueue();
        outtakeScheduler.setAutoReset(false);

        outtakeScheduler.scheduleCommand(()->  liftController.setTargetPosition(LiftController.Position.SPECIMEN_TAKE.getPos()));
        outtakeScheduler.scheduleCommand(outtakeController::setOuttakeToTake);
        outtakeScheduler.scheduleCommand(outtakeController::setClawRotateToTake);
        outtakeScheduler.scheduleCommand(outtakeController::setClawOpen);

        outtakeScheduler.start();

    }



    public void toPushSpecimen(){
        outtakeScheduler.clearQueue();
        outtakeScheduler.setAutoReset(false);

        outtakeScheduler.scheduleCommand(() -> liftController.setTargetPosition(LiftController.Position.SPECIMEN_PUSH.getPos()));
        outtakeScheduler.scheduleCommand(outtakeController::setOuttakeToPush);
        outtakeScheduler.scheduleCommand(outtakeController::setClawRotateToPush);

        outtakeScheduler.start();
    }

    public void setLiftTarget(double target){
        liftController.setTargetPosition(target);
    }

    public void setClawOpenNToTakeSpecimen(){
        outtakeScheduler.clearQueue();
        outtakeScheduler.setAutoReset(false);

        outtakeScheduler.scheduleCommand(outtakeController::setClawOpen);

        outtakeScheduler.scheduleDelay(0.7);
        outtakeScheduler.scheduleCommand(outtakeController::setClawOpen);

        outtakeScheduler.scheduleCommand(outtakeController::setOuttakeToTake);
        outtakeScheduler.scheduleCommand(outtakeController::setClawRotateToTake);
        outtakeScheduler.scheduleCommand(()->liftController.setTargetPosition(LiftController.Position.SPECIMEN_TAKE.getPos()));

        outtakeScheduler.start();
    }

    public void toIntakeAim(){
        intakeScheduler.clearQueue();
        intakeScheduler.setAutoReset(false);

        intakeScheduler.scheduleCommand(intakeController::setIntakeAim);
        intakeScheduler.scheduleCommand(outtakeController::setOuttakeToTransfer);
        intakeScheduler.scheduleCommand(outtakeController::setClawOpen);
        intakeScheduler.scheduleCommand(() -> liftController.setTargetPosition(0));


        intakeScheduler.start();
    }

    public void toIntakeTake(){
        intakeScheduler.clearQueue();
        intakeScheduler.setAutoReset(false);

        intakeScheduler.scheduleCommand(intakeController::setIntakeTake);

        intakeScheduler.scheduleDelay(INTAKE_TIME_DOWN);
        intakeScheduler.scheduleCommand(intakeController::setIntakeTake);

        intakeScheduler.scheduleCommand(intakeController::setClawClose);

        intakeScheduler.scheduleDelay(INTAKE_TIME_TAKE);
        intakeScheduler.scheduleCommand(intakeController::setClawClose);

        intakeScheduler.scheduleCommand(intakeController::setIntakeAim);

        intakeScheduler.start();
    }

    public void toTehnoZ(){
        intakeScheduler.clearQueue();
        intakeScheduler.setAutoReset(false);

        intakeScheduler.scheduleCommand(intakeController::setTehnoZ);

        intakeScheduler.start();
    }

    public void setTransfer(){
        transferSchedule.clearQueue();
        transferSchedule.setAutoReset(false);

        transferSchedule.scheduleCommand(outtakeController::setOuttakeToTransfer);
        transferSchedule.scheduleCommand(outtakeController::setClawOpen);
        transferSchedule.scheduleCommand(() -> liftController.setTargetPosition(0));

        transferSchedule.start();
    }

    public void setTransferNBusket(){
        transferSchedule.clearQueue();
        transferSchedule.setAutoReset(false);

        transferSchedule.scheduleCommand(intakeController::setIntakeToTransfer);
        transferSchedule.scheduleCommand(()->intakeController.setClawRotatePosition(IntakeController.Servos.INTAKE_CLAW_ROTATE_4.getPos()));
        transferSchedule.scheduleCommand(outtakeController::setOuttakeToTransfer);
        transferSchedule.scheduleCommand(outtakeController::setClawOpen);
        transferSchedule.scheduleCommand(() -> liftController.setTargetPosition(0));
        transferSchedule.scheduleCommand(outtakeController::setClawClose);

        transferSchedule.scheduleDelay(TRANSFER_TO_TAKE_SAMPLE);
        transferSchedule.scheduleCommand(intakeController::setClawOpen);

        transferSchedule.scheduleCommand(intakeController::setClawOpen);

        transferSchedule.scheduleDelay(TRANSFER_TO_BUSKET);
        transferSchedule.scheduleCommand(outtakeController::setOuttakeToBasket);

        transferSchedule.scheduleCommand(outtakeController::setOuttakeToBasket);
        transferSchedule.scheduleCommand(()->liftController.setTargetPosition(LiftController.Position.MAX.getPos()));




        transferSchedule.start();
    }

    public void setIntakeToStandard(){
        intakeScheduler.clearQueue();
        intakeScheduler.setAutoReset(false);

        intakeScheduler.scheduleCommand(intakeController::setIntakeToTransfer);
        intakeScheduler.scheduleCommand(()->intakeController.setClawRotatePosition(IntakeController.Servos.INTAKE_CLAW_ROTATE_4.getPos()));


        intakeScheduler.start();
    }
    public void setLiftToTransfer(){
        outtakeScheduler.clearQueue();
        outtakeScheduler.setAutoReset(false);

        outtakeScheduler.scheduleCommand(() -> liftController.setTargetPosition(0));
        outtakeScheduler.scheduleCommand(outtakeController::setOuttakeToTransfer);

        outtakeScheduler.start();


    }
    public void setOuttakeToBasket(){
        outtakeScheduler.clearQueue();
        outtakeScheduler.setAutoReset(false);

        outtakeScheduler.scheduleCommand(() -> liftController.setTargetPosition(LiftController.Position.MAX.getPos()));
        outtakeScheduler.scheduleCommand(outtakeController::setOuttakeToBasket);

        outtakeScheduler.scheduleDelay(2);
        outtakeScheduler.scheduleCommand(outtakeController::setOuttakeToBasket);

        outtakeScheduler.scheduleCommand(outtakeController::setClawOpen);

        outtakeScheduler.start();
    }


    public void setClaws(boolean isIntakeOpen){
        if(isIntakeOpen){
            intakeController.setClawOpen();
            outtakeController.setClawClose();
        }else {
            intakeController.setClawClose();
            outtakeController.setClawOpen();
        }
    }


    public void setIntakeClaw(boolean isOpen){
        if (isOpen){
            intakeController.setClawOpen();
        }else{
            intakeController.setClawClose();
        }
    }

    public void setExtendTarget(double target){
        extendController.setTargetPosition(target);
    }

    public boolean isBusy() {
        return outtakeScheduler.isRunning();
    }

    public void intakeRotateControl(double left_trigger,double right_trigger){
        intakeController.intakeRotateControl(left_trigger,right_trigger);
    }

    public void clawRotate(boolean up){
        intakeController.rotateClaw(up);
    }
    public void setRotateClaw(double rotate){
        intakeController.setRotateClaw(4);
    }

}
