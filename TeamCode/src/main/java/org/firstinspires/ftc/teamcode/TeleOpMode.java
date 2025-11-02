package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class TeleOpMode extends susBotOpMode {

    protected TeleOpMode(AllianceColor color) {
        super(color);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        this.frontLeftDrive = hardwareMap.dcMotor.get("front_left_drive");
        this.frontRightDrive = hardwareMap.dcMotor.get("front_right_drive");
        this.backLeftDrive = hardwareMap.dcMotor.get("back_left_drive");
        this.backRightDrive = hardwareMap.dcMotor.get("back_right_drive");

        //TelemetryManager telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        Follower follower = Constants.createFollower(hardwareMap);
        //follower.setStartingPose(newPose());
        follower.update();

        waitForStart();

        follower.startTeleopDrive();

        while (opModeIsActive()) {
            follower.setTeleOpDrive(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    true // Robot Centric
            );
            follower.update();

            telemetry.addData("frontLeftDrive", frontLeftDrive.getCurrentPosition());
            telemetry.addData("frontRightDrive", frontRightDrive.getCurrentPosition());
            telemetry.addData("backLeftDrive", backLeftDrive.getCurrentPosition());
            telemetry.addData("backRightDrive", backRightDrive.getCurrentPosition());
            telemetry.update();
        }
    }
}

