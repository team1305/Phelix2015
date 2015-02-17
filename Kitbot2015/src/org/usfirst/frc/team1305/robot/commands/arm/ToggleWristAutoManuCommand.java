package org.usfirst.frc.team1305.robot.commands.arm;

import org.usfirst.frc.team1305.robot.OI;
import org.usfirst.frc.team1305.robot.Robot;



import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ToggleWristAutoManuCommand extends Command {

	
    public ToggleWristAutoManuCommand() {
    	requires(Robot.arm);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.arm.toggleWristAutoManu();;
    }
    
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    	
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	
    }
}
