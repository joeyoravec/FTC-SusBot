package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class susBotOpMode extends LinearOpMode {

    public enum AllianceColor {
        RED,
        BLUE,
        NONE
    }

    susBotOpMode(AllianceColor color)
    {
        this.color = color;

//        this.frontLeftDrive = hardwareMap.dcMotor.get("front_left_drive");
//        this.frontRightDrive = hardwareMap.dcMotor.get("front_right_drive");
//        this.backLeftDrive = hardwareMap.dcMotor.get("back_left_drive");
//        this.backRightDrive = hardwareMap.dcMotor.get("back_right_drive");
    }

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