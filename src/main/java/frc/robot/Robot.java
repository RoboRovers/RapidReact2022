/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;

/*
 * Your best friend: https://first.wpi.edu/FRC/roborio/release/docs/java/
 * the link above is the documentation for the classes and library we use
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * TimedRobot: It provides control of the robot program through a collection of init() and periodic() methods,
 * which are called by WPILib during specific robot states (e.g. autonomous or teleoperated).
 * By default, periodic methods are called every 20ms; this can be changed by overriding the getPeriod() method.
 * 
 * Any method with @Overide above it is a function in the class TimedRobot which is what our code inherits from. We override them with
 * the functionality we need from out robot and shouldnt be deleted. Deleting them wont do anything and you can reimplment them later
 * but I recommend not removing them for simplicity. The other methods are ones we defined completly for ourselves and I prefer to put
 * them near the top of the code. These are used to simplify code and make code snippets reusable. An example being the Launcher()
 * method that should set the launcher to the speed you pass in and make sures they spin in oppisite directions. That way we just need
 * to call Luancher when we need it instead of rewriting it everytime we need to set the speed of the launcher. 
 * 
 * Here is the explanation for all the methods we overide from the TimedRobot class:
 * Note: If you want more info on any of these or see the other ones we havent implemented you can find it in the documentation below: 
 * https://first.wpi.edu/FRC/roborio/release/docs/java/
 * 
 * robotInit() is the function run when the robot starts up. Use this for code you need to run when the robot is enabled
 * 
 * teleopInit() is the function run when the robot starts into tele-operated mode (when you control the robot remotly).
 * 
 * teleopPeriodic() is the function run consantly when in tele-operated mode. A very simplified explanation is while the robot is in 
 * tele-operated mode it will do the instructions in this function every 20ms. So this is where stuff like your drive and shooting go
 * 
 * autonomousInit() is the function run when the robot starts into autonomous mode (when the robot moves on its own).
 * 
 * autonomousPeriodic() is the function run consantly when in autonomous mode. A very simplified explanation is while the robot is in 
 * autonomous mode it will do the instructions in this function every 20ms. So this is where stuff like drive and shooting go
 */
public class Robot extends TimedRobot {


  PWMVictorSPX LeftDriveMotor = new PWMVictorSPX(0); 
  PWMVictorSPX RightDriveMotor = new PWMVictorSPX(1); 
  DifferentialDrive RobotDrive = new DifferentialDrive(LeftDriveMotor,RightDriveMotor);
  PWMVictorSPX intake = new PWMVictorSPX(2);
  PWMVictorSPX LauncherFront = new PWMVictorSPX(3);
  PWMVictorSPX LauncherBack = new PWMVictorSPX(4);
  PWMVictorSPX Bumper1 = new PWMVictorSPX(5);
  Encoder leftEncoder = new Encoder(0, 1);
  Encoder rightEncoder = new Encoder(2, 3);
  ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
  boolean bumperRunning = false;
  Joystick Joystick1 = new Joystick(0);
  Timer timer = new Timer();
  int bumperRunningTicks = 0;
  //intake, shooter, transfer
  @Override
  public void robotInit() {
    
  }

  @Override
  public void teleopInit() {
    leftEncoder.reset();
    intake.set(0);
    rightEncoder.reset();
    SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    SmartDashboard.putNumber("leftEncoderDistance", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get());
    SmartDashboard.putNumber("Color Sensor Red reading", 0);
  }

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    SmartDashboard.putNumber("leftEncoderDistance", -1*(leftEncoder.get()/360.0*1.5707963268));
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get()/360.0*1.5707963268);
    SmartDashboard.putNumber("Color Sensor Red reading", colorSensor.getColor().red);
    RobotDrive.arcadeDrive(-(Joystick1.getY()), (Joystick1.getX()));
    
    if (Joystick1.getRawButtonPressed(2)) {
        intake.set(0.75);
    }
    if (Joystick1.getRawButtonReleased(2)) {
        intake.set(0);
    }
    if (Joystick1.getRawButtonPressed(1)) {
        intake.set(-0.75);
    }
    if (Joystick1.getRawButtonReleased(1)) {
        intake.set(0);
    }
    if (Joystick1.getRawButtonPressed(3)) {
        LauncherFront.set(0.80);
        LauncherBack.set(-0.80);
    }
    if (Joystick1.getRawButtonReleased(3)) {
        LauncherFront.set(0);
        LauncherBack.set(0);
    }
    if (Joystick1.getRawButtonPressed(4)) {
        LauncherFront.set(0.50);
        LauncherBack.set(-0.50);
    }
    if (Joystick1.getRawButtonReleased(4)) {
        LauncherFront.set(0);
        LauncherBack.set(0);
    }
    if (Joystick1.getRawButtonPressed(5)) {
        LauncherFront.set(0.90);
        LauncherBack.set(-0.90);
    }
    if (Joystick1.getRawButtonReleased(5)) {
        LauncherFront.set(0);
        LauncherBack.set(0);
    }
    if (Joystick1.getRawButtonPressed(7)) {
        Bumper1.set(-0.9);
    }
    if (Joystick1.getRawButtonReleased(7)) {
        Bumper1.set(0);
    }
    if (Joystick1.getRawButtonPressed(9)) {
        
        if (!bumperRunning) {
            bumperRunning = true;
            bumperRunningTicks = 0;
            Bumper1.set(-0.75);
        }
        else if (timer.get() > 1) {
            
            Bumper1.set(0);
        }
    }
    if (Joystick1.getRawButtonReleased(9)) {
        
        
            
        Bumper1.set(0);
        
        timer.stop();
        timer.reset();
        bumperRunning = false;
    }
    if (Joystick1.getRawButtonPressed(11)) {
        Bumper1.set(-1);
    }
    if (Joystick1.getRawButtonReleased(11)) {
        Bumper1.set(0);
    }
  }
  
  boolean DoingAuto = false;
  boolean ShootingBall = false;
  boolean TurningToBall = false;
  boolean TaxingToBall = false;
  boolean TaxingToHub = false;
  boolean LeavingTarmac = false;
  double ballAngleValue = 0;
  
  
  @Override
  public void autonomousInit() {
    leftEncoder.reset();
    rightEncoder.reset();
    timer.reset();

    int AutoNum = 0; //get from network table.
    int BallNum = 0; //get from network table.
    switch (AutoNum) {
        default:
            break;
        case 1:
            DoingAuto = true;
            LeavingTarmac = true;
            break;
        case 2:
            DoingAuto = true;
            ShootingBall = true;
            break;
        case 3:
            DoingAuto = true;
            ShootingBall = true;
            LeavingTarmac = true;
            break;
        case 4:
            DoingAuto = true;
            ShootingBall = true;
            TurningToBall = true;
            TaxingToBall = true;
            TaxingToHub = true;
            break;
        
    }
    switch (BallNum) {
        case 1:
            ballAngleValue = 0;
            break;
        case 2:
            ballAngleValue = 0;
            break;
        case 3:
            ballAngleValue = 0;
            break;
    }
  }

  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    SmartDashboard.putNumber("leftEncoderDistance", -1*(leftEncoder.get()/360.0*1.5707963268));
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get()/360.0*1.5707963268);
    
    if (DoingAuto) {
        if (ShootingBall) {
            if (timer.get() == 0) {
                timer.start();
                Bumper1.set(-0.75);
                LauncherFront.set(0.85);
                LauncherBack.set(-0.85);
            }
            else if (timer.get() > 1) {
                LauncherFront.set(0);
                LauncherBack.set(0);
                Bumper1.set(0);
                timer.stop();
                timer.reset();
                ShootingBall = false;
            }
        } 
        else if (TurningToBall) {
            if (timer.get() == 0) {
                timer.start();
                LeftDriveMotor.set(-0.4);
            }
            else if (leftEncoder.get()/360.0*1.5707963268 > ballAngleValue) {
                LeftDriveMotor.set(0);
                leftEncoder.reset();
                rightEncoder.reset();
                timer.stop();
                timer.reset();
                TurningToBall = false;
            }
            
        }
        else if (TaxingToBall) {
            if (timer.get() == 0) {
                timer.start();
                RobotDrive.arcadeDrive(0.4, 0);
                intake.set(0.75);
            }
            else if (leftEncoder.get()/360.0*1.5707963268 > 10) {
                timer.stop();
                timer.reset();
                intake.set(0);
                RobotDrive.arcadeDrive(0, 0);
                TaxingToBall = false;
            }
        }
        else if (TaxingToHub) {
            if (timer.get() == 0) {
                timer.start();
                RobotDrive.arcadeDrive(-0.5, 0);

            }
            else if (leftEncoder.get()/360.0*1.5707963268 < -0.5) {
                RobotDrive.arcadeDrive(0, 0);
                timer.stop();
                timer.reset();
                TaxingToHub = false;
                ShootingBall = true;
            }

        }
        else if (LeavingTarmac) {
            if (timer.get() == 0) {
                timer.start();
                RobotDrive.arcadeDrive(0.4, 0);
            }
            else if (leftEncoder.get()/360.0*1.5707963268 < 10) {
                RobotDrive.arcadeDrive(0, 0);
                timer.stop();
                timer.reset();
                LeavingTarmac = false;
            }
        }
    }
 
  }
  
}