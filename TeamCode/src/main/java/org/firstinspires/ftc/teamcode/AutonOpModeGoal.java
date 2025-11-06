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

    private Pose getRobotPoseFromCamera() {
        //Fill this out to get the robot Pose from the camera's output (apply any filters if you need to using follower.getPose() for fusion)
        //Pedro Pathing has built-in KalmanFilter and LowPassFilter classes you can use for this
        //Use this to convert standard FTC coordinates to standard Pedro Pathing coordinates
        return new Pose(0, 0, 0, FTCCoordinates.INSTANCE).getAsCoordinateSystem(PedroCoordinates.INSTANCE);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        Follower follower;
        boolean following = false;
        final Pose TARGET_LOCATION = new Pose();

        initOpMode();
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose()); //set your starting pose
        waitForStart();

        while (opModeIsActive()) {
            tryDetectMotifAprilTag();
            tryDetectGoalAprilTag();

            follower.update();
            if (!following) {
                follower.followPath(
                        follower.pathBuilder()
                                .addPath(new BezierLine(follower.getPose(), TARGET_LOCATION))
                                .setLinearHeadingInterpolation(follower.getHeading(), TARGET_LOCATION.minus(follower.getPose()).getAsVector().getTheta())
                                .build()
                );
            }

            //This uses the aprilTag to relocalize your robot
            //You can also create a custom AprilTag fusion Localizer for the follower if you want to use this by default for all your autos
            follower.setPose(getRobotPoseFromCamera());

            if (following && !follower.isBusy()) following = false;
            // TODO: Insert code for autonomous from this position

            send_telemetry();
        }
    }
}

