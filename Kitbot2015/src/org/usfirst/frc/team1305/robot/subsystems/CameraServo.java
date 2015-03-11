package org.usfirst.frc.team1305.robot.subsystems;

import org.usfirst.frc.team1305.robot.commands.cameraServo.MoveCameraCommand;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class CameraServo extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.


	public CameraServo(){

	}

	private void updateSmartDashboard()
    {
    }
	// Called just before this Command runs the first time
    protected void initialize() {
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new MoveCameraCommand());
    }

    public void MoveCamera (){

    	updateSmartDashboard();
    }


}

