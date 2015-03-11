package org.usfirst.frc.team1305.robot.commands.drivetrain;


import org.usfirst.frc.team1305.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Standard scrub drive for scrubs.
 */
public class Drive extends Command {

    public Drive() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(Drive);
    	requires(Robot.drivetrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if(Robot.drivetrain.isLowGear == false){
    	//sets values from drivestick for arcade drive
    	double XL = Robot.oi.getDriveX()/0.8;
    	double YL = Robot.oi.getDriveY();

    	//apply stick values to the arcadedrive function
    	Robot.drivetrain.arcadeDrive(YL, XL);
    	}else{
    		//sets values from drivestick for arcade drive
        	double XL = Robot.oi.getDriveX();
        	double YL = Robot.oi.getDriveY();

        	//apply stick values to the arcadedrive function
        	Robot.drivetrain.arcadeDrive(YL, XL);
    	}

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

	public static void toggleSmoothing() {

	}
}
