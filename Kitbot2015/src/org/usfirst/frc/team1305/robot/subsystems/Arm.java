package org.usfirst.frc.team1305.robot.subsystems;

import org.usfirst.frc.team1305.robot.Robot;
import org.usfirst.frc.team1305.robot.RobotMap;
//import org.usfirst.frc.team1305.robot.commands.arm.MoveShoulderCommand;
import org.usfirst.frc.team1305.robot.commands.arm.ArmDefaultCommand;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Controls all arm movement. Holds algorithm for keeping claw horizontal.
 */
public class Arm extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	private AnalogPotentiometer potShoulder = new AnalogPotentiometer(RobotMap.ANALOG_POT_SHOULDER);
	private AnalogPotentiometer potElbow = new AnalogPotentiometer(RobotMap.ANALOG_POT_ELBOW);
	private AnalogPotentiometer potWrist = new AnalogPotentiometer(RobotMap.ANALOG_POT_WRIST);

//	private int newXClawPosition, prevXClawPosition;
//	private int newYClawPosition, prevYClawPosition;
//	private int X_AXIS_MAX = 30, X_AXIS_MIN = 0, Y_AXIS_MIN = -14, Y_AXIS_MAX = 30;
//	private int X_AXIS_FACTOR = 10, Y_AXIS_FACTOR = 10;
//	private double hypot;
//	private double BICEP_LENGTH = 38, FOREARM_LEN = 33;
	private double MIN_SHOULDER_POT       = 0.099; //0.12;
	private int SHOULDER_ANGLE_AT_MIN_POT = 24;
	private double MAX_SHOULDER_POT       = 0.491; //0.495;
	private double SHOULDER_POT_MAX_LIMIT = 0.5;
	private int SHOULDER_ANGLE_AT_MAX_POT = 88;
	private double SHOULDER_YMXB_M 	      = (SHOULDER_ANGLE_AT_MIN_POT - SHOULDER_ANGLE_AT_MAX_POT)/(MIN_SHOULDER_POT - MAX_SHOULDER_POT);
	private double SHOULDER_YMXB_B        = SHOULDER_ANGLE_AT_MAX_POT - (SHOULDER_YMXB_M * MAX_SHOULDER_POT);

	private double MIN_ELBOW_POT = 0.021; //0.1;
	private int ELBOW_ANGLE_AT_MIN_POT = 142;
	private double MAX_ELBOW_POT = 0.357; //0.46;
	private int ELBOW_ANGLE_AT_MAX_POT = 20;
	private double ELBOW_YMXB_M = (ELBOW_ANGLE_AT_MIN_POT - ELBOW_ANGLE_AT_MAX_POT)/(MIN_ELBOW_POT - MAX_ELBOW_POT);
	private double ELBOW_YMXB_B = ELBOW_ANGLE_AT_MAX_POT - (ELBOW_YMXB_M * MAX_ELBOW_POT);

	private double MIN_WRIST_POT = 0.175; //0.192;
	private int WRIST_ANGLE_AT_MIN_POT = 100;
	private double MAX_WRIST_POT = 0.429; //0.498; //0.52;
	private int WRIST_ANGLE_AT_MAX_POT = 234;
	private double WRIST_POT_LIMIT = 0.385;
	//NB - wrist line (y=mx + b) has y is pot reading, not angle like shoulder and elbow
	//because we "calc" target pot reading (rather than "reading" current value and calc'ing angle)
	private double WRIST_YMXB_M = (MIN_WRIST_POT - MAX_WRIST_POT)/(WRIST_ANGLE_AT_MIN_POT - WRIST_ANGLE_AT_MAX_POT);
	private double WRIST_YMXB_B = MAX_WRIST_POT - (WRIST_YMXB_M * WRIST_ANGLE_AT_MAX_POT);

	private double ELBOW_DIR_TO_MOTOR_DIR = 1; // -1 if positive motor causes negative elbow dir
	private double SHOULDER_DIR_TO_MOTOR_DIR = 1; // -1 if positive motor causes negative shoulder dir
	private double WRIST_DIR_TO_MOTOR_DIR = 1; // -1 if positive motor causes negative wrist dir
	private CANTalon shoulderMotor = new CANTalon(RobotMap.CAN_DEVICE_SHOULDER);
	private CANTalon elbowMotor = new CANTalon(RobotMap.CAN_DEVICE_ELBOW);
	private CANTalon wristMotor = new CANTalon(RobotMap.CAN_DEVICE_WRIST);
//	private double WristAngleToPotRatio = 180;
//	private double ShoulderAngleToPotRatio = 180;
//	private double ElbowAngleToPotRatio = 180;
	private double targetWristPot;
	private double targetWristAngle;
	private boolean isWristAuto = true;
	public static final int ARM_PRESET_EXTENDED = 1;
	public static final int ARM_PRESET_TRANSPORT = 2;
	public static final int ARM_PRESET_MAX_STACK = 3;

	private int currentState = 0;
	private Timer armTimer = new Timer();
	
	public Arm(){

	}

	// Called just before this Command runs the first time
    protected void initialize() {
    	System.out.println("Arm is Initialized");
    	SmartDashboard.putNumber("Shoulder MIN Pot", MIN_SHOULDER_POT);
    	SmartDashboard.putNumber("Shoulder MAX Pot", MAX_SHOULDER_POT);
    	SmartDashboard.putNumber("Elbow MIN Pot", MIN_ELBOW_POT);
    	SmartDashboard.putNumber("Elbow MAX Pot", MAX_ELBOW_POT);
    	SmartDashboard.putNumber("Wrist MIN Pot", MIN_WRIST_POT);
    	SmartDashboard.putNumber("Wrist MAX Pot", MAX_WRIST_POT);
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ArmDefaultCommand());
    }

    private double GetShoulderAngle()
    {
    	//xxxmeasurement of pot to angle produced following formula =178 * G2 - 3.53
    	//measurement of pot to angle produced following formula = =(-178 * C8) + 125.83

    	//updateSmartDashboard("Shoulder Angle Calc", -178 * getShoulderPot() + 130.00); //125.83);
    	updateSmartDashboard("Shoulder Angle Calc", SHOULDER_YMXB_M * getShoulderPot() + SHOULDER_YMXB_B);
    	return (SHOULDER_YMXB_M * potShoulder.get() + SHOULDER_YMXB_B);
    }

    private double GetElbowAngle()
    {
    	//measurement of pot to angle produced following formula =(-274  * G11) + 162

    	//updateSmartDashboard("Elbow Angle Calc", (-274  * getElbowPot()) + 165);
    	updateSmartDashboard("Elbow Angle Calc", ELBOW_YMXB_M * getElbowPot() + ELBOW_YMXB_B);
    	return (ELBOW_YMXB_M * potElbow.get() + ELBOW_YMXB_B);
    	//inverse formula....return -.0036 * getElbowPot() + .5891;
    }

    private double getShoulderPot(){
    	return potShoulder.get();
    }

    private double getElbowPot(){
    	return potElbow.get();
    }

    public double getWristPot(){
    	return potWrist.get();
    }

    private double calcTargetWristPot()
    {
    	targetWristAngle = GetElbowAngle() + GetShoulderAngle(); // - 90;
    	updateSmartDashboard();
    	return ConvertWristAngleToPot(targetWristAngle);
    }

    private double ConvertWristAngleToPot(double wristAngle)
    {
    	//measurement of pot to angle produced following formula = .0019x + .0991
    	//return (0.0019 * wristAngle) + 0.0991; //-  WristAngleToPotRatio;
    	SmartDashboard.putNumber("Wrist M", WRIST_YMXB_M);
    	SmartDashboard.putNumber("Wrist B", WRIST_YMXB_B);
    	SmartDashboard.putNumber("Full Wrist Calc", (WRIST_YMXB_M * getWristPot()) + WRIST_YMXB_B);
    	return (WRIST_YMXB_M * wristAngle) + WRIST_YMXB_B; //-  WristAngleToPotRatio;
    }

    public void MoveShoulder(double yAxis){
    	//min extension 0.12, max extension 0.465

    	if(getShoulderPot() <= MIN_SHOULDER_POT){
    		shoulderMotor.set(-Math.abs(yAxis)/4);
    	}
    	else if(getShoulderPot() >= SHOULDER_POT_MAX_LIMIT){
    		shoulderMotor.set(Math.abs(yAxis)/4);
    	}
    	else{
    		shoulderMotor.set(yAxis);
    	}
    	updateSmartDashboard("Shoulder Joystick", yAxis);
    }

    public void MoveElbow(double yAxis){
    	//min 0.1 max 0.5

    	if(getElbowPot() <= MIN_ELBOW_POT){
    		if(yAxis >= 0){
    			elbowMotor.set(-Math.abs(yAxis));
    		}else{
    			elbowMotor.set(0);
    		}
    	}
    	else if(getElbowPot() >= MAX_ELBOW_POT){
    		if(yAxis <= 0){
    			elbowMotor.set(Math.abs(yAxis));
    		}else{
    			elbowMotor.set(0);
    		}
    	}
    	else{
    		elbowMotor.set(yAxis);
    	}
    	updateSmartDashboard("Elbow Joystick", yAxis);
    	System.out.println("ShoulderPot = " + getShoulderPot());
    	System.out.println("ElbowPot = " + getElbowPot());

    }

    private void updateSmartDashboard()
    {
    	SmartDashboard.putNumber("Shoulder Pot", getShoulderPot());
    	SmartDashboard.putNumber("Elbow Pot", getElbowPot());
    	SmartDashboard.putNumber("Wrist Pot", getWristPot());
    	SmartDashboard.putBoolean("Is Wrist Auto?", isWristAuto);

    	SmartDashboard.putNumber("Target Wrist Angle", targetWristAngle);
    	SmartDashboard.putNumber("Wrist Pot Calc", targetWristPot);
		SmartDashboard.putNumber("Wrist Motor Suggestion", (getWristPot()-targetWristPot)/getWristPot());

		SmartDashboard.putNumber("Shoulder Speed", shoulderMotor.get());
		SmartDashboard.putNumber("Elbow Speed", elbowMotor.get());
		SmartDashboard.putNumber("Extended Wrist Speed", wristMotor.get());
    }

    private void updateSmartDashboard(String itemLabel, double itemValue)
    {
    	SmartDashboard.putNumber(itemLabel, itemValue);
    	updateSmartDashboard();
    }

    private void updateSmartDashboard(String itemLabel, String itemValue)
    {
    	SmartDashboard.putString(itemLabel, itemValue);
    	updateSmartDashboard();
    }

    public void MoveWrist(double yAxis){
    	//if in auto, ignore; otherwise respond
    	if (! isWristAuto)
    	{
    	 	moveWristDirectly(yAxis);
    	}

    }

    private void moveWristDirectly(double yAxis){
    	//min 0.12 max 0.37
    	targetWristPot = calcTargetWristPot();
		//TODO:  put min/max logic back in once
		//potentiometer is fixed
    	if(getWristPot() <= MIN_WRIST_POT){
    		wristMotor.set(Math.abs(yAxis)/4);
    	}
    	else if(getWristPot() >= WRIST_POT_LIMIT){
    		wristMotor.set(-Math.abs(yAxis)/4);
    	}
    	else{
    		wristMotor.set(-yAxis);
    	}
    	updateSmartDashboard();
    }

    public void MoveWristAutomatically()
    {
    	//if wrist is not in auto mode, just ignore
    	if (isWristAuto){
    		targetWristPot = calcTargetWristPot();

        	if(getWristPot() != targetWristPot){
        		wristMotor.set(-(getWristPot()-targetWristPot)*36);
        		//calc fraction it is away, and send as joystick signal
        		//moveWristDirectly(-20 * (getWristPot()-targetWristPot)/getWristPot());
    		}
        	updateSmartDashboard();
    	}

    }

    public void toggleWristAutoManu(){
    	isWristAuto = !isWristAuto;
    }

    private boolean isBetween(double value1, double value2)
    {
    	return value1 == value2;
    }

    public void StopShoulder()
    {
    	shoulderMotor.set(0);
    }

    public void StopElbow()
    {
    	elbowMotor.set(0);
    }

    public void StopWrist()
    {
    	wristMotor.set(0);
    }

    public void MoveArm(int xAxisDir, int yAxisDir)
    {
//    	newXClawPosition = prevXClawPosition + xAxisDir * X_AXIS_FACTOR;
//    	if (newXClawPosition > X_AXIS_MAX) {newXClawPosition = X_AXIS_MAX;}
//    		else if (newXClawPosition < X_AXIS_MIN) {newXClawPosition = X_AXIS_MIN;}
//
//    	newYClawPosition = prevYClawPosition + yAxisDir * Y_AXIS_FACTOR;
//    	if (newYClawPosition > Y_AXIS_MAX) {newXClawPosition = Y_AXIS_MAX;}
//    		else if (newYClawPosition < Y_AXIS_MIN) {newXClawPosition = Y_AXIS_MIN;}
//
//    	hypot = Math.sqrt(newXClawPosition * newXClawPosition + newYClawPosition * newYClawPosition);
//
//    	CalcElbowPot(newXClawPosition, newYClawPosition, hypot);
//    	CalcShoulderPot(newXClawPosition, newYClawPosition, hypot);
    }

    private void CalcElbowPot(int newX, int newY, double hypotenuse)
    {
    	//hypotenuse is opposite elbow, so
    	//SSS theorem says elbow angle = invCos (bicep ^ 2 + forearm ^ 2 - hypot ^ 2) / (2 * bicep *forearm)
    	//double newElbowAngleTarget;
    	//newElbowAngleTarget = Math.acos((BICEP_LENGTH * BICEP_LENGTH + FOREARM_LEN * FOREARM_LEN - hypotenuse * hypotenuse)/
    	//		(2 * BICEP_LENGTH * FOREARM_LEN));
    }

    private void CalcShoulderPot(int newX, int newY, double hypotenuse)
    {
    	//forearm is opposite shoulder, so
    	//SSS theorem says shoulder angle = invCos ((bicep ^ 2 + hypot ^ 2 - forearm ^ 2) / (2 * bicep * hypot))
    	//double newShoulderAngleTarget;
    	//newShoulderAngleTarget = Math.acos((BICEP_LENGTH * BICEP_LENGTH + hypotenuse * hypotenuse - FOREARM_LEN * FOREARM_LEN)/
    	//		(2 * BICEP_LENGTH * hypotenuse));

    }

    public void ArmPresets(int preset){

    	if(preset == ARM_PRESET_EXTENDED){

    		if(getShoulderPot() != 0.185){
    			shoulderMotor.set((getShoulderPot()-0.185)*24);
    		}else{
    			shoulderMotor.set(0);
    		}
    		if(getElbowPot() != 0.02){
    			elbowMotor.set((getElbowPot()-0.02)*24);
    		}else{
    			elbowMotor.set(0);
    		}
//    		if(getWristPot() != 0.42){
//    			wristMotor.set((getWristPot()-0.42)*24);
//    		}
    		//0.22
    		
    		
    	}
    	else if(preset == ARM_PRESET_TRANSPORT){
    		if(getShoulderPot() != 0.500){
    			shoulderMotor.set((getShoulderPot()-0.500)*18);
    		}
    		if(getElbowPot() != 0.306){
    			elbowMotor.set((getElbowPot()-0.306)*24);
    		}
//    		if(getWristPot() != 0.352){
//    			wristMotor.set((getWristPot()-0.352)*24);
//    		}
    	}
    	else if(preset == ARM_PRESET_MAX_STACK){
    		if(getShoulderPot() != 0.505){
    			shoulderMotor.set((getShoulderPot()-0.505)*18);
    		}
    		if(getElbowPot() != 0.02){
    			elbowMotor.set((getElbowPot()-0.02)*24);
    		}
//    		if(getWristPot() != 0.52){
//    			wristMotor.set((getWristPot()-0.52)*24);
//    		}
    	}
    	MoveWristAutomatically();
    	updateSmartDashboard("Preset Is", preset);
    }
    
    public boolean autonomousArmExtend(double duration){
    	MoveWristAutomatically();
    	switch (currentState){
        case 0:
            armTimer.start();

            currentState++;
            break;
        case 1:
            if(armTimer.get()>=1){
            	currentState++;
            }
            Robot.arm.ArmPresets(ARM_PRESET_MAX_STACK);
            break;
        case 2:
        	if (armTimer.get()>= duration+1)
            {
                currentState++;
            }
            Robot.arm.ArmPresets(ARM_PRESET_EXTENDED);
            break;
        case 3:
        	shoulderMotor.set(0);
            elbowMotor.set(0);
            wristMotor.set(0);
            currentState = 0;
            armTimer.stop();
            armTimer.reset();
            break;
    	}
    	if(currentState == 3){
    		currentState = 0;
  			return true;
   		}else{
   			return false;
   		}    
    	
    }
    
    public boolean autonomousArmTransport(double duration){
    	MoveWristAutomatically();
    	switch (currentState){
        case 0:
            currentState++;
            break;
        case 1:
        	if (armTimer.get()>= duration)
            {
                currentState++;
            }
            Robot.arm.ArmPresets(ARM_PRESET_TRANSPORT);
            break;
        case 2:
        	shoulderMotor.set(0);
            elbowMotor.set(0);
            wristMotor.set(0);
            currentState = 0;
            break;
	    }
	    if(currentState == 2){
	    	currentState = 0;
	  		return true;
	   	}else{
	   		return false;
	   	}    
    	
    }
    
    public boolean autonomousArmUp(double duration){
    	MoveWristAutomatically();
    	switch (currentState){
        case 0:
            armTimer.start();
            
            currentState++;
            break;
        case 1:
            if (armTimer.get()>= duration)
            {
                currentState++;
            }
            if(getShoulderPot() != 0.34){
    			shoulderMotor.set((getShoulderPot()-0.34)*24);
    		}
    		if(getElbowPot() != 0.02){
    			elbowMotor.set((getElbowPot()-0.02)*24);
    		}
            break;
        case 2:
            shoulderMotor.set(0);
            elbowMotor.set(0);
            wristMotor.set(0);
            currentState = 0;
            armTimer.stop();
            armTimer.reset();
            break;
    }
    if(currentState == 2){
    	currentState = 0;
  		return true;
   	}else{
   		return false;
   	}    
    	
    }
}

