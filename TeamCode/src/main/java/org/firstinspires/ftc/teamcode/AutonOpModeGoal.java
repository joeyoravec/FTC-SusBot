package org.firstinspires.ftc.teamcode;

public class AutonOpModeGoal extends susBotOpMode {

    protected AutonOpModeGoal(AllianceColor color) {
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

