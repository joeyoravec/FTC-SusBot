package org.firstinspires.ftc.teamcode;

import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class susBotOpMode extends LinearOpMode {

    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();
    private static final Style robotLook = new Style(
            "", "#3F51B5", 0.75
    );
    private static final Style targetLook = new Style(
            "", "#B5A33F", 0.75
    );
    public static final double ROBOT_RADIUS = 9; // woah

    private Position cameraPosition = new Position(DistanceUnit.INCH,
            0, 0, 0, 0);

    // Camera is defined pointing at the ceiling
    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
            // yaw: this is opposite the docs; no idea why it must rotate when facing forward
            -90,
            // pitch: this matches the docs, must rotate down
            -90,
            // do not roll the camera
            0,
            0);

    // Position of Blue Goal April Tag
    Pose blueGoalAprilTagPose = new Pose(
            -58.3727,
            -55.6425,
            Math.toRadians(54),
            FTCCoordinates.INSTANCE
    ).getAsCoordinateSystem(PedroCoordinates.INSTANCE);

    // Position of the Red Goal April Tag
    Pose redGoalAprilTagPose = new Pose(
            -58.3727,
            55.6425,
            Math.toRadians(-54),
            FTCCoordinates.INSTANCE
    ).getAsCoordinateSystem(PedroCoordinates.INSTANCE);

    // 47 inches facing the blue goal
    Pose blueGoalLaunchingPose = new Pose(
            -58.3727 + 27.62591,
            -55.6425 + 38.0238,
            Math.toRadians(234),
            FTCCoordinates.INSTANCE
    ).getAsCoordinateSystem(PedroCoordinates.INSTANCE);

    // 47 inches facing the red goal
    Pose redGoalLaunchingPose = new Pose(
            -58.3727 + 27.62591,
            55.6425 - 38.0238,
            Math.toRadians(126),
            FTCCoordinates.INSTANCE
    ).getAsCoordinateSystem(PedroCoordinates.INSTANCE);

    // 47 inches facing the blue goal
    Pose startingPose_pedro = new Pose(
            0,
            0,
            Math.toRadians(180),
            FTCCoordinates.INSTANCE
    ).getAsCoordinateSystem(PedroCoordinates.INSTANCE);


    public enum AllianceColor {
        RED,
        BLUE,
        NONE
    }

    susBotOpMode(AllianceColor color)
    {
        this.color = color;
        this.motif = Motif.fromId(1);
    }

    public void initOpMode() {
        this.frontLeftDrive = hardwareMap.dcMotor.get("front_left_drive");
        this.frontRightDrive = hardwareMap.dcMotor.get("front_right_drive");
        this.backLeftDrive = hardwareMap.dcMotor.get("back_left_drive");
        this.backRightDrive = hardwareMap.dcMotor.get("back_right_drive");

        // Create the AprilTag processor and assign it to a variable.
        myAprilTagProcessor = new AprilTagProcessor.Builder()
                .setCameraPose(cameraPosition, cameraOrientation)
                .build();

        // Create a VisionPortal, with the specified webcam name and AprilTag processor, and assign it to a variable.
        myVisionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), myAprilTagProcessor);

        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
    }

    public enum Motif {
        UNKNOWN(1, "UNKNOWN"),
        GPP(21, "GPP"),
        PGP(22, "PGP"),
        PPG(23, "PPG");

        private final int id;
        private final String pattern;

        Motif(int id, String pattern) { this.id = id; this.pattern = pattern; }
        public int id() { return id; }
        public String pattern() { return pattern; }

        // Static method to construct an enum from an int
        public static Motif fromId(int id) {
            for (Motif status : Motif.values()) {
                if (status.id() == id) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No enum constant with id " + id);
        }
    }

    public void tryDetectMotifAprilTag() {
        // Get a list containing the latest detections, which may be stale.
        List<AprilTagDetection> myAprilTagDetections = myAprilTagProcessor.getDetections();

        // Define a list of Motif IDs we're looking for
        List<Integer> desiredMotifAprilTagIds = Arrays.asList(21, 22, 23);

        AprilTagDetection myMotifAprilTagDetection = myAprilTagDetections.stream()
                .filter(d -> desiredMotifAprilTagIds.contains(d.id) )
                .findFirst()
                .orElse(null);

        if (myMotifAprilTagDetection != null)
        {
            motif = Motif.fromId(myMotifAprilTagDetection.id);
        }
    }

    public void tryDetectGoalAprilTag() {
        // Get a list containing the latest detections, which may be stale.
        List<AprilTagDetection> myAprilTagDetections = myAprilTagProcessor.getDetections();

        // Determine which Goal ID we're looking for
        int desiredGoalAprilTagId;
        if (color == AllianceColor.BLUE)
        {
            desiredGoalAprilTagId = 20;
        }
        else if (color == AllianceColor.RED)
        {
            desiredGoalAprilTagId = 24;
        }
        else
        {
            // if it's unknown assume blue
            desiredGoalAprilTagId = 20;
        }

        myGoalAprilTagDetection = myAprilTagDetections.stream()
                .filter(d -> d.id == desiredGoalAprilTagId )
                .findFirst()
                .orElse(null);
    }

    public void send_telemetry() {
        // Show what is the current "known" motif
        telemetry.addData("Alliance", color.toString());

        // Show what is the current "known" motif
        telemetry.addData("getMaxPowerScaling", follower.getMaxPowerScaling());

        // Show the drive motor encoder positions
        telemetry.addData("front_left_drive", frontLeftDrive.getCurrentPosition());
        telemetry.addData("front_right_drive", frontRightDrive.getCurrentPosition());
        telemetry.addData("back_left_drive", backLeftDrive.getCurrentPosition());
        telemetry.addData("back_right_drive", backRightDrive.getCurrentPosition());

        // Show what is the current "known" motif0
        telemetry.addData("motif", motif.pattern());

        Locale usLocale = new Locale("en", "US");

        Pose robotPose_pedro = follower.getPose();
        telemetry.addLine("\n==== Pedro Coordinates");
        telemetry.addLine(String.format(usLocale, "XYH %6.1f %6.1f %6.1f",
                robotPose_pedro.getX(),
                robotPose_pedro.getY(),
                Math.toDegrees(robotPose_pedro.getHeading())));

        Pose robotPose_ftc = robotPose_pedro.getAsCoordinateSystem(FTCCoordinates.INSTANCE);
        telemetry.addLine("\n==== FTC Coordinates");
        telemetry.addLine(String.format(usLocale, "XYH %6.1f %6.1f %6.1f",
                robotPose_ftc.getX(),
                robotPose_ftc.getY(),
                Math.toDegrees(robotPose_ftc.getHeading())));

        drawRobot(robotPose_pedro);
        drawRobot(blueGoalAprilTagPose, targetLook, 4);
        drawRobot(redGoalAprilTagPose, targetLook, 4);
        drawRobot(blueGoalLaunchingPose, targetLook, 4);
        drawRobot(redGoalLaunchingPose, targetLook, 4);
        sendPacket();

        // Show all visible AprilTags
        List<AprilTagDetection> myAprilTagDetections = myAprilTagProcessor.getDetections();
        for (AprilTagDetection myAprilTagDetection : myAprilTagDetections) {
            if (myAprilTagDetection.metadata != null) {

                // Only use tags that don't have Obelisk in them
                telemetry.addLine(String.format(usLocale, "\n==== (ID %d) %s", myAprilTagDetection.id, myAprilTagDetection.metadata.name));
                if (!myAprilTagDetection.metadata.name.contains("Obelisk")) {

                    telemetry.addLine("ftcPose relative to camera:");
                    telemetry.addLine(String.format(usLocale, "XYZ %6.1f %6.1f %6.1f  (inch)",
                            myAprilTagDetection.ftcPose.x,
                            myAprilTagDetection.ftcPose.y,
                            myAprilTagDetection.ftcPose.z));
                    telemetry.addLine(String.format(usLocale, "RBE %6.1f %6.1f %6.1f  (deg)",
                            myAprilTagDetection.ftcPose.range,
                            myAprilTagDetection.ftcPose.bearing,
                            myAprilTagDetection.ftcPose.elevation));

                    telemetry.addLine("robotPose on FTC field:");
                    telemetry.addLine(String.format(usLocale, "XYZ %6.1f %6.1f %6.1f  (inch)",
                            myAprilTagDetection.robotPose.getPosition().x,
                            myAprilTagDetection.robotPose.getPosition().y,
                            myAprilTagDetection.robotPose.getPosition().z));
                    telemetry.addLine(String.format(usLocale, "PRY %6.1f %6.1f %6.1f  (deg)",
                            myAprilTagDetection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
                            myAprilTagDetection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
                            myAprilTagDetection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));

                    telemetry.addLine("robotPose Pedro coordinates:");
                    Pose robotPose_pedro_from_tag = new Pose(
                            myAprilTagDetection.robotPose.getPosition().x,
                            myAprilTagDetection.robotPose.getPosition().y,
                            Math.toRadians(myAprilTagDetection.robotPose.getOrientation().getYaw()),
                            FTCCoordinates.INSTANCE
                    ).getAsCoordinateSystem(PedroCoordinates.INSTANCE);
                    telemetry.addLine(String.format(usLocale, "XYH %6.1f %6.1f %6.1f",
                            robotPose_pedro_from_tag.getX(),
                            robotPose_pedro_from_tag.getY(),
                            Math.toDegrees(robotPose_pedro_from_tag.getHeading())));
                }
            }
        }

        telemetry.update();
    }

    /**
     * This draws a robot at a specified Pose with a specified
     * look. The heading is represented as a line.
     *
     * @param pose  the Pose to draw the robot at
     * @param style the parameters used to draw the robot with
     */
    public static void drawRobot(Pose pose, Style style, double radius) {
        if (pose == null || Double.isNaN(pose.getX()) || Double.isNaN(pose.getY()) || Double.isNaN(pose.getHeading())) {
            return;
        }

        panelsField.setStyle(style);
        panelsField.moveCursor(pose.getX(), pose.getY());
        panelsField.circle(radius);

        Vector v = pose.getHeadingAsUnitVector();
        v.setMagnitude(v.getMagnitude() * radius);
        double x1 = pose.getX() + v.getXComponent() / 4, y1 = pose.getY() + v.getYComponent() / 4;
        double x2 = pose.getX() + v.getXComponent(), y2 = pose.getY() + v.getYComponent();

        panelsField.setStyle(style);
        panelsField.moveCursor(x1, y1);
        panelsField.line(x2, y2);
    }

    /**
     * This draws a robot at a specified Pose. The heading is represented as a line.
     *
     * @param pose the Pose to draw the robot at
     */
    public static void drawRobot(Pose pose) {
        drawRobot(pose, robotLook, ROBOT_RADIUS);
    }

    /**
     * This tries to send the current packet to FTControl Panels.
     */
    public static void sendPacket() {
        panelsField.update();
    }

    protected AprilTagProcessor myAprilTagProcessor;
    protected VisionPortal myVisionPortal;

    protected Motif motif;

    protected Follower follower;


    protected AprilTagDetection myGoalAprilTagDetection;

    protected AllianceColor color;
    protected DcMotor frontLeftDrive;
    protected double frontLeftDrive_power;
    protected DcMotor frontRightDrive;
    protected double frontRightDrive_power;
    protected DcMotor backLeftDrive;
    protected double backLeftDrive_power;
    protected DcMotor backRightDrive;
    protected double backRightDrive_power;
}