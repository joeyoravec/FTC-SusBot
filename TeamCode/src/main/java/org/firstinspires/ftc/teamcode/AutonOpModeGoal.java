package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

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

