package org.firstinspires.ftc.teamcode;

public class AutonOpModeWall extends susBotOpMode {

    protected AutonOpModeWall(AllianceColor color) {
        super(color);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        initOpMode();
        waitForStart();

        while (opModeIsActive()) {
            tryDetectMotifAprilTag();
            tryDetectGoalAprilTag();

            // TODO: Insert code for autonomous from this position

            send_telemetry();
        }
    }
}

