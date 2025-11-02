package org.firstinspires.ftc.teamcode;

public class AutonOpModeGoal extends susBotOpMode {

    protected AutonOpModeGoal(AllianceColor color) {
        super(color);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        while (opModeIsActive()) {

            // TODO: Insert code for autonomous from this position

            telemetry.addData("frontLeftDrive", frontLeftDrive.getCurrentPosition());
            telemetry.addData("frontRightDrive", frontRightDrive.getCurrentPosition());
            telemetry.addData("backLeftDrive", backLeftDrive.getCurrentPosition());
            telemetry.addData("backRightDrive", backRightDrive.getCurrentPosition());
        }
    }
}

