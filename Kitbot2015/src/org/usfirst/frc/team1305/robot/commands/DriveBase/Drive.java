
package org.usfirst.frc.team1305.robot.commands.DriveBase;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team1305.robot.Robot;

/**
 *
 */
public class Drive extends Command {

    public Drive() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.Drivetrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double XL = Robot.oi.getDriveXL();
    	double XR = Robot.oi.getDriveXR();
    	double YL = Robot.oi.getDriveYL();
    	double YR = Robot.oi.getDriveYR();
    	//below needs changeing to move turn on one axis
    	Robot.Drivetrain.Teamptank(YL, -YR);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}