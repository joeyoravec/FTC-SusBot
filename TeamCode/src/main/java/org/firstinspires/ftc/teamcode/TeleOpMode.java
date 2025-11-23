package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;
import java.util.Locale;

public class TeleOpMode extends susBotOpMode {
    private Supplier<PathChain> pathChain;
    private boolean automatedDrive = false;

    protected TeleOpMode(AllianceColor color) {
        super(color);
    }

    private void trySetRobotPoseFromCamera() {
        // Show all visible AprilTags
        List<AprilTagDetection> myAprilTagDetections = myAprilTagProcessor.getDetections();
        for (AprilTagDetection myAprilTagDetection : myAprilTagDetections) {
            if (myAprilTagDetection.metadata != null) {
                Locale usLocale = new Locale("en", "US");

                // Only use tags that don't have Obelisk in them
                if (!myAprilTagDetection.metadata.name.contains("Obelisk")) {

                    Pose robotPose_pedro_from_tag = new Pose(
                            myAprilTagDetection.robotPose.getPosition().x,
                            myAprilTagDetection.robotPose.getPosition().y,
                            Math.toRadians(myAprilTagDetection.robotPose.getOrientation().getYaw()),
                            FTCCoordinates.INSTANCE
                    ).getAsCoordinateSystem(PedroCoordinates.INSTANCE);
                    telemetry.addLine(String.format(usLocale, "setting pose XYH %6.1f %6.1f %6.1f",
                            robotPose_pedro_from_tag.getX(),
                            robotPose_pedro_from_tag.getY(),
                            Math.toDegrees(robotPose_pedro_from_tag.getHeading())));

                    follower.setPose(robotPose_pedro_from_tag);

                    Pose robotPose_pedro_stored = follower.getPose();
                    telemetry.addLine(String.format(usLocale, "got pose XYH %6.1f %6.1f %6.1f",
                            robotPose_pedro_stored.getX(),
                            robotPose_pedro_stored.getY(),
                            Math.toDegrees(robotPose_pedro_stored.getHeading())));
                }
            }
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        TelemetryManager telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        initOpMode();

        follower = Constants.createFollower(hardwareMap);

        follower.setStartingPose(startingPose_pedro);

        // Even if you set max power in the mecanum constants, it'll use default
        // globalMaxPower unless you set it on the follower!
        follower.setMaxPower(0.3);

        follower.update();

        pathChain = () -> follower.pathBuilder() //Lazy Curve Generation
                .addPath(new Path(new BezierLine(follower::getPose, blueGoalLaunchingPose)))
                .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, blueGoalLaunchingPose.getHeading(), 0.8))
                .build();

        waitForStart();

        follower.startTeleopDrive();

        while (opModeIsActive()) {
            follower.update();

            tryDetectMotifAprilTag();
            tryDetectGoalAprilTag();
            trySetRobotPoseFromCamera();

            if (!automatedDrive) {
                follower.setTeleOpDrive(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x,
                        -gamepad1.right_stick_x,
                        true // Robot Centric
                );
            }

            // Gamepad A: automated drive to BlueGoalPose
            if (gamepad1.aWasPressed()) {
                follower.followPath(pathChain.get());
                automatedDrive = true;
            }

            // Gamepad B: stop automated drive
            // or stop if automated drive is complete
            if (automatedDrive && (gamepad1.bWasPressed() || !follower.isBusy())) {
                follower.startTeleopDrive();
                automatedDrive = false;
            }

            telemetryM.debug("position", follower.getPose());
            telemetryM.debug("velocity", follower.getVelocity());
            telemetryM.debug("automatedDrive", automatedDrive);
            telemetryM.update();

            send_telemetry();
        }
    }
}