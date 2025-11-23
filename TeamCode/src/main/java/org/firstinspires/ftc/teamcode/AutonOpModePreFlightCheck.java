package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Pre-Flight Check auto", group="Testing")
public class AutonOpModePreFlightCheck extends susBotOpMode {
    public AutonOpModePreFlightCheck() {
        super(AllianceColor.NONE);
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
