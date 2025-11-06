package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Arrays;
import java.util.List;

public abstract class susBotOpMode extends LinearOpMode {

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
        myAprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();

        // Create a VisionPortal, with the specified webcam name and AprilTag processor, and assign it to a variable.
        myVisionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), myAprilTagProcessor);
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

        // Show the drive motor encoder positions
        telemetry.addData("front_left_drive", frontLeftDrive.getCurrentPosition());
        telemetry.addData("front_right_drive", frontRightDrive.getCurrentPosition());
        telemetry.addData("back_left_drive", backLeftDrive.getCurrentPosition());
        telemetry.addData("back_right_drive", backRightDrive.getCurrentPosition());

        // Show all visible AprilTags
        List<AprilTagDetection> myAprilTagDetections = myAprilTagProcessor.getDetections();
        StringBuilder visibleAprilTags = new StringBuilder();
        for (AprilTagDetection myAprilTagDetection : myAprilTagDetections)
        {
            visibleAprilTags.append(myAprilTagDetection.id);
            visibleAprilTags.append(" ");
        }
        telemetry.addData("visibleAprilTags", visibleAprilTags);

        // Show what is the current "known" motif
        telemetry.addData("motif", motif.pattern());

        // Show what is the current "known" goal data
        if (myGoalAprilTagDetection != null) {
            telemetry.addData("range", myGoalAprilTagDetection.ftcPose.range);
            telemetry.addData("bearing", myGoalAprilTagDetection.ftcPose.bearing);
            telemetry.addData("elevation", myGoalAprilTagDetection.ftcPose.elevation);
        }

        telemetry.update();
    }

    private AprilTagProcessor myAprilTagProcessor;
    private VisionPortal myVisionPortal;

    protected Motif motif;

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