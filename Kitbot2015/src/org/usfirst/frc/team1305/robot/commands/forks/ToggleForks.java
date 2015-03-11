package org.usfirst.frc.team1305.robot.commands.forks;

import org.usfirst.frc.team1305.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Opens/Closes forks.
 */
public class ToggleForks extends Command {

    public ToggleForks() {
    	requires(Robot.forks);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//sets Claw toggle function from claw subsystem to command
    	Robot.forks.ToggleForks();
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
