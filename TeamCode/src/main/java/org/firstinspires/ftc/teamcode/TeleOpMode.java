package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class TeleOpMode extends susBotOpMode {

    protected TeleOpMode(AllianceColor color) {
        super(color);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        initOpMode();

        //TelemetryManager telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        Follower follower = Constants.createFollower(hardwareMap);
        //follower.setStartingPose(newPose());
        follower.update();

        waitForStart();

        follower.startTeleopDrive();

        while (opModeIsActive()) {
            tryDetectMotifAprilTag();
            tryDetectGoalAprilTag();

            follower.setTeleOpDrive(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x,
                    -gamepad1.right_stick_x,
                    true // Robot Centric
            );
            follower.update();

            send_telemetry();
        }
    }
}

