package org.usfirst.frc.team3453.robot;

//import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;

//import com.ni.vision.NIVision;
//import com.ni.vision.NIVision.Image;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	RobotDrive myRobot;
	Talon frontLeft, frontRight, rearLeft, rearRight;
	Talon topCarriage, bottomCarriage;
	Joystick carriageStick, driveStick;
	int autoLoopCounter;
	double operatorReading;
	double tVal;
	double topCarriageSpeed;
	double bottomCarriageSpeed;
	double topCarriageSpeedUpMod, topCarriageSpeedDownMod;
	double bottomCarriageSpeedUpMod, bottomCarriageSpeedDownMod;
	boolean isTrigger;
	boolean followMe;
	boolean topCarriageSpeedLock;
	boolean bottomCarriageSpeedLock;
	boolean topCarriageSpeedReset;
	boolean bottomCarriageSpeedReset;

	DigitalInput topCarriageLimit;
	DigitalInput topCarriageMagnet;
	DigitalInput midCarriageLimit;
	DigitalInput midCarriageMagnet;
	DigitalInput bottomCarriageLimit;
	DigitalInput bottomCarriageMagnet;
	
	DigitalInput reverseInput;
	double magnetMod;
	int neg;
	int topUpLimit, topDownLimit;
	boolean magnetOverRide;
	
	// NI vision stuff
//    int session;
//    Image frame;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	frontLeft = new Talon(1);
    	frontRight = new Talon(3);
    	rearLeft = new Talon (2);
    	rearRight = new Talon(4);
    	topCarriage = new Talon(6);
    	bottomCarriage = new Talon(5);
    	
    	
    	myRobot = new RobotDrive(frontLeft,rearLeft,frontRight,rearRight);
    	driveStick = new Joystick(0);
    	carriageStick = new Joystick(1);
    	
    	topCarriageSpeed = 0;
    	bottomCarriageSpeed = 0;
    	topCarriageSpeedLock = false;
    	bottomCarriageSpeedLock = false;
    	topCarriageSpeedReset = false;
    	bottomCarriageSpeedReset = false;
    	
    	topCarriageSpeedUpMod = topCarriageSpeedDownMod = 1;
    	bottomCarriageSpeedUpMod = bottomCarriageSpeedDownMod = 1;
    	
    	topCarriageLimit = new DigitalInput(0);
    	topCarriageMagnet = new DigitalInput(1);
    	midCarriageLimit = new DigitalInput(2);
    	midCarriageMagnet = new DigitalInput(3);
    	bottomCarriageLimit = new DigitalInput(4);
    	bottomCarriageMagnet = new DigitalInput(5);
    	
    	reverseInput = new DigitalInput(9);
    	
    	neg = 1;
    	topUpLimit = 1;
    	topDownLimit = 1;
    	
    	magnetMod = 0.3;
    	followMe = false;
    	
    	// NI Vision init
/*        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);

        // the camera name (ex "cam0") can be found through the roborio web interface
        session = NIVision.IMAQdxOpenCamera("cam0",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);
*/
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() {
    	autoLoopCounter = 0;
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	if(autoLoopCounter < 100) //Check if we've completed 100 loops (approximately 2 seconds)
		{
			myRobot.drive(-0.5, 0.0); 	// drive forwards half speed
			autoLoopCounter++;
			} else {
			myRobot.drive(0.0, 0.0); 	// stop robot
		}
    }
    
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit(){
    	// NI Vision teleop init
//        NIVision.IMAQdxStartAcquisition(session);
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        myRobot.arcadeDrive(driveStick, true);
        
        // NI Vision processing
/*        NIVision.IMAQdxGrab(session, frame, 1);
//      NIVision.imaqDrawShapeOnImage(frame, frame, rect,
//              DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);      
        CameraServer.getInstance().setImage(frame);
*/
        
        isTrigger = carriageStick.getTrigger();
        
        //Joystick Axis- Forward= negative, Backwards= positive for attack3 and extreme3DPro
        operatorReading = carriageStick.getY();
        System.out.println("Raw operatorReading: "+operatorReading);
        SmartDashboard.putNumber("Raw operatorReading", operatorReading);
        
/*        tVal = carriageStick.getThrottle();
        if (tVal < -0.15) {
        	followMe = true;
        	SmartDashboard.putBoolean("Follow Me: ", followMe);
        } else {
        	followMe = false;
        	SmartDashboard.putBoolean("Follow Me: ", followMe);
        }
*/        
        topCarriageSpeedLock = carriageStick.getRawButton(6);
        bottomCarriageSpeedLock = carriageStick.getRawButton(7);
        topCarriageSpeedReset = carriageStick.getRawButton(3);
        bottomCarriageSpeedReset = carriageStick.getRawButton(2);
        magnetOverRide = carriageStick.getRawButton(8);
        
        // dio #9 is jumped, reverse the operator joystick input
        // backwards is up, forward is down
        if (reverseInput.get()) {
        	neg = -1;
        } else {
        	neg = 1;
        }
        SmartDashboard.putBoolean("Reverse Joystick ", reverseInput.get());

       
        
        if (topCarriageSpeedReset) {
        	topCarriageSpeed = 0;
        }
        if (bottomCarriageSpeedReset) {
        	bottomCarriageSpeed = 0;
        }
        
        if ((topCarriageLimit.get())) {
        	
        	SmartDashboard.putBoolean("topCarriageLimit: ", topCarriageLimit.get());
        	// top carriage limit switch activated
        	// no top carriage upward movement
        	if ((isTrigger) && ((neg * operatorReading) < 0)) {
        		System.out.println("topCarriageLimit override");
        		System.out.println("topCarriageLimit.operatorReading: "+operatorReading);
        		operatorReading = 0;
        	}
/*        	if ((followMe) && ((neg * operatorReading) < 0)) {
        		topUpLimit = 0;
        	} else {
        		topUpLimit = 1;
        	}
*/        	
        	if (topCarriageSpeed != 0) {
        		topCarriageSpeed = 0;
        	}
        } else {
        	topUpLimit = 1;
        	SmartDashboard.putBoolean("topCarriageLimit: ", topCarriageLimit.get());
        }
        
        if ((midCarriageLimit.get())) {
        	SmartDashboard.putBoolean("midCarriageLimit: ", midCarriageLimit.get());
        	if ((isTrigger) && ((neg * operatorReading) > 0)) {
        		System.out.println("midCarriageLimit.top override");
        		System.out.println("midCarriageLimit.top.operatorReading: "+operatorReading);
        		operatorReading = 0;
        	}
        	if ((!isTrigger) && ((neg * operatorReading) < 0)) {
        		System.out.println("midCarriageLimit.bottom override");
        		System.out.println("midCarriageLimit.bottom.operatorReading: "+operatorReading);
        		operatorReading = 0;
        	}
/*        	if ((followMe) && ((neg * operatorReading) > 0)) {
        		topDownLimit = 0;
        	} else {
        		topDownLimit = 1;
        	}
*/
        	
        	if (topCarriageSpeed != 0) {
        		topCarriageSpeed = 0;
        	}
        	if (bottomCarriageSpeed != 0) {
        		bottomCarriageSpeed = 0;
        	}
        } else {
        	topDownLimit = 1;
        	SmartDashboard.putBoolean("midCarriageLimit: ", midCarriageLimit.get());
        }
        
        if ((bottomCarriageLimit.get())) {
        	
        	SmartDashboard.putBoolean("bottomCarriageLimit: ", bottomCarriageLimit.get());
        	// bottom carriage limit switch activated
        	//  no bottom carriage downward movement
        	if ((!isTrigger) && ((neg * operatorReading) > 0)) {
        		System.out.println("bottomCarriageLimit override");
        		System.out.println("bottomCarriageLimit:operatorReading: "+operatorReading);
        		operatorReading = 0;
        	}
        	if (bottomCarriageSpeed != 0) {
        		bottomCarriageSpeed = 0;
        	}
        	
        } else {
        	SmartDashboard.putBoolean("bottomCarriageLimit: ", bottomCarriageLimit.get());
        }
        
        if (topCarriageMagnet.get()) {
        	SmartDashboard.putBoolean("topCarriageMagnet: ", topCarriageMagnet.get());
        	topCarriageSpeedUpMod = magnetMod;
        } else {
        	SmartDashboard.putBoolean("topCarriageMagnet: ", topCarriageMagnet.get());
        	topCarriageSpeedUpMod = 1;
        }

        if (midCarriageMagnet.get()) {
        	SmartDashboard.putBoolean("midCarriageMagnet: ", midCarriageMagnet.get());
        	topCarriageSpeedDownMod = magnetMod;
        	bottomCarriageSpeedUpMod = magnetMod;
        } else {
        	SmartDashboard.putBoolean("midCarriageMagnet: ", midCarriageMagnet.get());
        	topCarriageSpeedDownMod = 1;
        	bottomCarriageSpeedUpMod = 1;
        }
        
        if (bottomCarriageMagnet.get()) {
        	SmartDashboard.putBoolean("bottomCarriageMagnet: ", bottomCarriageMagnet.get());
        	bottomCarriageSpeedDownMod = magnetMod;
        } else {
        	SmartDashboard.putBoolean("bottomCarriageMagnet: ", bottomCarriageMagnet.get());
        	bottomCarriageSpeedDownMod = 1;
        }
        
        if (magnetOverRide) {
        	topCarriageSpeedUpMod = 1;
        	topCarriageSpeedDownMod = 1;
        	bottomCarriageSpeedUpMod = 1;
        	bottomCarriageSpeedDownMod = 1;
        }
        
        if (isTrigger) {
        	
        	if ((neg * bottomCarriageSpeed) < 0) {
        		bottomCarriage.set(neg * bottomCarriageSpeed * bottomCarriageSpeedUpMod);
        	} else if ((neg * bottomCarriageSpeed) > 0) {
        		bottomCarriage.set(neg * bottomCarriageSpeed * bottomCarriageSpeedDownMod);
        	} else {
        		bottomCarriage.set(0);
        	}        	
        	
        	if (topCarriageSpeedLock) {
        		topCarriageSpeed = operatorReading;
        	}        	
        	if ((neg * topCarriageSpeed) < 0) {
        		topCarriage.set(neg * topCarriageSpeed * topCarriageSpeedUpMod);
        	} else if ((neg * topCarriageSpeed) > 0) {
        		topCarriage.set(neg * topCarriageSpeed * topCarriageSpeedDownMod);
        	} else {
	        	if ((neg * operatorReading) < 0) {
	        		topCarriage.set(neg * operatorReading * topCarriageSpeedUpMod);
	        	} else if ((neg * operatorReading) > 0) {
	        		topCarriage.set(neg * operatorReading * topCarriageSpeedDownMod);
	        	} else {
	        		topCarriage.set(0);
	        	}
        	}
        	        	
        } else {
        	
        	if ((neg * topCarriageSpeed) < 0) {
        		topCarriage.set(neg * topCarriageSpeed * topCarriageSpeedUpMod);
        	} else if ((neg * topCarriageSpeed) > 0) {
        		topCarriage.set(neg * topCarriageSpeed * topCarriageSpeedDownMod);
        	} else {
        		topCarriage.set(0);
        	}        	
        	
        	if (bottomCarriageSpeedLock) {
        		bottomCarriageSpeed = operatorReading;
        	}
        	if ((neg * bottomCarriageSpeed) < 0) {
        		SmartDashboard.putString("Status: ", "auto up");
        		SmartDashboard.putString("Direction: ", "Up");
        		bottomCarriage.set(neg * bottomCarriageSpeed * bottomCarriageSpeedUpMod);
        	} else if ((neg * bottomCarriageSpeed) > 0) {
        		SmartDashboard.putString("Status: ", "auto down");
        		SmartDashboard.putString("Direction: ", "Down");
        		bottomCarriage.set(neg * bottomCarriageSpeed * bottomCarriageSpeedDownMod);
        	} else {
        		SmartDashboard.putString("Status: ", "bottom joystick manual control");
	        	if ((neg * operatorReading) < 0) {
	        		SmartDashboard.putString("Direction: ", "Up");
	        		bottomCarriage.set(neg * operatorReading * bottomCarriageSpeedUpMod);
//	        		if (followMe) {
//	        			topCarriage.set(topUpLimit * neg * operatorReading * topCarriageSpeedUpMod);
//	        		}
	        	} else if ((neg * operatorReading) > 0) {
	        		SmartDashboard.putString("Direction: ", "Down");
	        		bottomCarriage.set(neg * operatorReading * bottomCarriageSpeedDownMod);
//	        		if (followMe) {
//	        			topCarriage.set(topDownLimit * neg * operatorReading * topCarriageSpeedDownMod);
//	        		}
	        	} else {
	        		SmartDashboard.putString("Direction: ", "Stop");
	        		bottomCarriage.set(0);
//	        		if (followMe) {
//	        			topCarriage.set(0);
//	        		}
	        	}
        	}


        	

        }

        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.run();
    }
    
}
