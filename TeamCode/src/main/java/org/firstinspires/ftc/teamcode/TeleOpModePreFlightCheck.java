package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp(name="Pre-Flight Check tele", group="Testing")
public class TeleOpModePreFlightCheck extends susBotOpMode {
    public TeleOpModePreFlightCheck() {
        super(AllianceColor.NONE);
    }

    public void clearState() {
        frontLeftDrive_power = 0;
        frontRightDrive_power = 0;
        backLeftDrive_power = 0;
        backRightDrive_power = 0;
    }

    public void set() {
        DcMotor motorToControl = null;
        double intendedPower = 0;

        // Determine what power we would use for the motor
        if (gamepad1.left_trigger > 0) {
            intendedPower = -1 * gamepad1.left_trigger;
        } else if (gamepad1.right_trigger > 0) {
            intendedPower = gamepad1.right_trigger;
        }

        // Determine which, if any motor we want to control
        if (gamepad1.dpad_up && gamepad1.dpad_left) {
            frontLeftDrive_power = intendedPower;
        } else if (gamepad1.dpad_up && gamepad1.dpad_right) {
            frontRightDrive_power = intendedPower;
        } else if (gamepad1.dpad_down && gamepad1.dpad_left) {
            backLeftDrive_power = intendedPower;
        } else if (gamepad1.dpad_down && gamepad1.dpad_right) {
            backRightDrive_power = intendedPower;
        }
    }

    public void update() {
        frontLeftDrive.setPower(frontLeftDrive_power);
        frontRightDrive.setPower(frontRightDrive_power);
        backLeftDrive.setPower(backLeftDrive_power);
        backRightDrive.setPower(backRightDrive_power);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        initOpMode();
        waitForStart();

        while (opModeIsActive()) {
            tryDetectMotifAprilTag();
            tryDetectGoalAprilTag();

            clearState();
            set();
            update();

            send_telemetry();
        }
    }
}
