package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Pre-Flight Check auto", group="Testing")
public class AutonOpModePreFlightCheck extends susBotOpMode {
    public AutonOpModePreFlightCheck() {
        super(AllianceColor.NONE);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("frontLeftDrive", frontLeftDrive.getCurrentPosition());
            telemetry.addData("frontRightDrive", frontRightDrive.getCurrentPosition());
            telemetry.addData("backLeftDrive", backLeftDrive.getCurrentPosition());
            telemetry.addData("backRightDrive", backRightDrive.getCurrentPosition());
        }
    }
}
