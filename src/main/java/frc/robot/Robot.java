/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.VideoCamera;
import edu.wpi.first.cscore.VideoSource;
import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.*;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.*;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

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
  CANSparkMax Climber = new CANSparkMax(1, MotorType.kBrushless);
  //PWMVictorSPX Climber = new PWMVictorSPX(9);
  Encoder leftEncoder = new Encoder(0, 1);
  Encoder rightEncoder = new Encoder(2, 3);
  ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
  boolean bumperRunning = false;
  Joystick Joystick1 = new Joystick(0);
  XboxController xboxController1 = new XboxController(1);
  Timer timer = new Timer();

  int reverseConstant = 1;
  Integer DoNothingAuto = 0;
  Integer OnlyLeaveTarmac = 1;
  Integer OnlyShootBall = 2;
  Integer ShootAndLeaveTarmac = 3;
  Integer Shoot2Balls = 4;
  Integer BallA = 1;
  Integer BallB = 2;
  Integer BallC = 3;
  SendableChooser<Integer> AutoChooser = new SendableChooser<>();
  SendableChooser<Integer> BallChooser = new SendableChooser<>();

  
  
  int bumperRunningTicks = 0;
  
  //intake, shooter, transfer
  @Override
  public void robotInit() {
    Climber.setIdleMode(IdleMode.kBrake);
    CameraServer.startAutomaticCapture();
    AutoChooser.setDefaultOption("Do Nothing Option", DoNothingAuto);
    AutoChooser.addOption("Only leave tarmac", OnlyLeaveTarmac);
    AutoChooser.addOption("Only shoot 1st ball", OnlyShootBall);
    AutoChooser.addOption("Shoot 1st ball and leave tarmac", ShootAndLeaveTarmac);
    AutoChooser.addOption("Shoot 2 balls", Shoot2Balls);
    BallChooser.setDefaultOption("A", BallA);
    BallChooser.addOption("B", BallB);
    BallChooser.addOption("C", BallC);
    SmartDashboard.putData("Auto Choice", AutoChooser);
    SmartDashboard.putData("Ball Choice", BallChooser);
  }
  final double DISTANCEMULTIPLIER = 1.5707963268/360.0;
  
  @Override
  public void teleopInit() {
    //CameraServer.startAutomaticCapture();

    timer.reset();
    timer.start();
    leftEncoder.reset();
    intake.set(0);
    rightEncoder.reset();
    SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    SmartDashboard.putNumber("leftEncoderDistance", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get());
  }

  @Override
  public void teleopPeriodic() {
    if (colorSensor.getProximity() == 2047) {
        SmartDashboard.putString("Bumper Position", "Down");
    }
    else {
        SmartDashboard.putString("Bumper Position", "Not Down");
    }
    
    SmartDashboard.putNumber("timer", timer.get());
    SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    SmartDashboard.putNumber("leftEncoderDistance", -1*(leftEncoder.get()*DISTANCEMULTIPLIER));
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get()*DISTANCEMULTIPLIER);
    SmartDashboard.putNumber("Color Sensor Proximity", colorSensor.getProximity());
    
    //RobotDrive.arcadeDrive(0.9, 0);
    RobotDrive.arcadeDrive(reverseConstant * -(Joystick1.getY()), reverseConstant * Joystick1.getX());
    SmartDashboard.putNumber("Controller Y", -Joystick1.getY());
    SmartDashboard.putNumber("Controller X", Joystick1.getX());
    
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
    if (Joystick1.getRawButtonPressed(11)) {
        reverseConstant *= -1;
    }
    if (Joystick1.getRawButtonReleased(11)) {
        
    }
    if (xboxController1.getRawButtonPressed(5)) {
        LauncherFront.set(0.50);
        LauncherBack.set(-0.50);
    }
    if (xboxController1.getRawButtonReleased(5)) {
        LauncherFront.set(0);
        LauncherBack.set(0);
    }
    if (xboxController1.getRawButtonPressed(7)) {
        LauncherFront.set(0.90);
        LauncherBack.set(-0.90);
    }
    if (xboxController1.getRawButtonReleased(7)) {
        LauncherFront.set(0);
        LauncherBack.set(0);
    }
    if (xboxController1.getRawButtonPressed(4)) {
        Climber.set(1);
    }
    if (xboxController1.getRawButtonReleased(4)) {
        Climber.set(0);
    }
    if (xboxController1.getRawButtonPressed(2)) {
        Climber.set(-1);
    }
    if (xboxController1.getRawButtonReleased(2)) {
        Climber.set(0);
    }
    if (xboxController1.getRawButtonPressed(8)) {
        Bumper1.set(-0.9);
    }
    if (xboxController1.getRawButtonReleased(8)) {
        Bumper1.set(0);
    }
    if (xboxController1.getRawButtonPressed(6)) {
      Bumper1.set(0.25);
    }
    if (xboxController1.getRawButtonReleased(6)) {
        Bumper1.set(0);
      }
  }
  
  boolean DoingAuto = false;
  boolean ShootingBall = false;
  boolean TurningToBall = false;
  boolean DroppingIntake = false;
  boolean TaxingToBall = false;
  boolean TurningToTarmac = false;
  boolean TaxingToTarmac = false;
  boolean TurningToHub = false;
  boolean TaxingToHub = false;
  boolean LeavingTarmac = false;
  boolean FixingBumper = false;
  double ballAngleValue = 0;
  
  @Override
  public void autonomousInit() {
    

    leftEncoder.reset();
    rightEncoder.reset();
    timer.stop();
    timer.reset();

    int AutoNum = AutoChooser.getSelected();
    switch (AutoNum) {
        default:
        SmartDashboard.putNumber("autoSelected", 0);
            break;
        case 1:
        SmartDashboard.putNumber("autoSelected", 1);
            DoingAuto = true;
            LeavingTarmac = true;
            break;
        case 2:
        SmartDashboard.putNumber("autoSelected", 2);
            DoingAuto = true;
            ShootingBall = true;
            break;
        case 3:
        SmartDashboard.putNumber("autoSelected", 3);
            DoingAuto = true;
            ShootingBall = true;
            LeavingTarmac = true;
            break;
        case 4:
        SmartDashboard.putNumber("autoSelected", 4);
            DoingAuto = true;
            ShootingBall = true;
            TurningToBall = true;
            TaxingToBall = true;
            TaxingToTarmac = true;
            break;
        
    }
    int BallNum = BallChooser.getSelected();
    switch (BallNum) {
        case 1:
            SmartDashboard.putString("Ball Chosen", "A");
            ballAngleValue = -65;
            break;
        case 2:
        SmartDashboard.putString("Ball Chosen", "B");
            ballAngleValue = -300;
            TurningToHub = true;
            TaxingToHub = true;
            break;
        case 3:
        SmartDashboard.putString("Ball Chosen", "C");
            ballAngleValue = 65;
            break;
    }
    
  }
  
  @Override
  public void teleopExit() {
      timer.stop();
  }
  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    SmartDashboard.putNumber("leftEncoderDistance", -1*(leftEncoder.get()*DISTANCEMULTIPLIER));
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get()*DISTANCEMULTIPLIER);
    SmartDashboard.putNumber("timer", timer.get());
    if (DoingAuto) {
        if (ShootingBall) {
            SmartDashboard.putString("Current Task", "Shooting");
            if (timer.get() == 0) {
                timer.start();
                //Bumper1.set(-0.75);
                LauncherFront.set(0.9);
                LauncherBack.set(-0.9);
                
            }
            else if (timer.get() > 1 && timer.get() < 2) {
                Bumper1.set(-0.75);
            }
            else if (timer.get() > 2 && timer.get() < 3) {
                LauncherFront.set(0);
                LauncherBack.set(0);
                Bumper1.set(0);
                RobotDrive.arcadeDrive(0.4, 0);
            }
            else if (timer.get() > 3 && timer.get() < 4) {
                RobotDrive.arcadeDrive(0, 0);
                
            }
            else if (timer.get() > 4) {
                timer.stop();
                timer.reset();
                ShootingBall = false;
                if (colorSensor.getProximity() != 2047) {
                    FixingBumper = true;
                }
            }
        } 
        else if (FixingBumper) {
            if (colorSensor.getProximity() == 2047) {
                FixingBumper = false;
            }
            else if (colorSensor.getProximity() > 100 && colorSensor.getProximity() < 200) {
                Bumper1.set(0);
            }
            else if (colorSensor.getProximity() < 100) {
                Bumper1.set(0.1);
            }
        }
        else if (TurningToBall) {
            SmartDashboard.putString("Current Task", "Turning");
            if (timer.get() == 0) {
                timer.start();
                leftEncoder.reset();
                rightEncoder.reset();
                //RobotDrive.arcadeDrive(0, -0.4);
                
            }
            else if (ballAngleValue > 0 && rightEncoder.get() < ballAngleValue) {
                RobotDrive.arcadeDrive(0, -0.5);
            }
            else if (ballAngleValue < 0 && rightEncoder.get() > ballAngleValue) {
                RobotDrive.arcadeDrive(0, 0.5);
            }
            else {
                RobotDrive.arcadeDrive(0, 0);
                
                timer.stop();
                timer.reset();
                TurningToBall = false;
            }
            
        }
        else if (TaxingToBall) {
            SmartDashboard.putString("Current Task", "Taxing to Ball");
            if (timer.get() == 0) {
                timer.start();
                RobotDrive.arcadeDrive(0.6, 0);
                intake.set(-0.75);
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER < 7) {
                RobotDrive.arcadeDrive(0.6, 0);
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER < 8) {
                RobotDrive.arcadeDrive(0.3, 0);
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER >= 8) {
                timer.stop();
                timer.reset();
                intake.set(0);
                RobotDrive.arcadeDrive(0, 0);
                TaxingToBall = false;
            }
        }
        else if (TurningToTarmac) {
            SmartDashboard.putString("Current Task", "Turning to hub");
            if (timer.get() == 0) {
                timer.start();
                leftEncoder.reset();
                rightEncoder.reset();
                
                
            }
            else if (timer.get() < 1) {
                leftEncoder.reset();
                rightEncoder.reset();
            }
            else if (ballAngleValue > 0 && rightEncoder.get() > -ballAngleValue) {
                RobotDrive.arcadeDrive(0, 0.5);
            }
            else if (ballAngleValue < 0 && rightEncoder.get() < -ballAngleValue) {
                RobotDrive.arcadeDrive(0, -0.5);
            }
            else if (rightEncoder.get() < -10) {
                RobotDrive.arcadeDrive(0, 0);
                
                timer.stop();
                timer.reset();
                TurningToTarmac = false;
            }
            
        }
        else if (TaxingToTarmac) {
            SmartDashboard.putString("Current Task", "Taxing to Hub");
            if (timer.get() == 0) {
                timer.start();
                RobotDrive.arcadeDrive(-0.6, 0);
                leftEncoder.reset();
                rightEncoder.reset();
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER > -9) {
                RobotDrive.arcadeDrive(-0.6, 0);
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER < -9) {
                RobotDrive.arcadeDrive(0, 0);
                timer.stop();
                timer.reset();
                TaxingToTarmac = false;
                ShootingBall = true;
            }
        }
        else if (TurningToHub) {
            SmartDashboard.putString("Current Task", "Turning");
            if (timer.get() == 0) {
                timer.start();
                leftEncoder.reset();
                rightEncoder.reset();
                
                
            }
            else if (rightEncoder.get() < 300) {
                RobotDrive.arcadeDrive(0, -0.5);
            }
            else {
                RobotDrive.arcadeDrive(0, 0);
                
                timer.stop();
                timer.reset();
                TurningToHub = false;
            }
        }
        else if (TaxingToHub) {
            SmartDashboard.putString("Current Task", "taxing to hub");
            if (timer.get() == 0) {
                leftEncoder.reset();
                rightEncoder.reset();
                timer.start();
            }
            else if (rightEncoder.get() * DISTANCEMULTIPLIER > -0.5) {
                RobotDrive.arcadeDrive(-0.4, 0);
            }
            else {
                timer.stop();
                timer.reset();
                TaxingToHub = false;
            }
        }
        else if (LeavingTarmac) {
            SmartDashboard.putString("Current Task", "Leaving Tarmac");
            if (timer.get() == 0) {
                timer.start();
                RobotDrive.arcadeDrive(0.6, 0);
            }
            else if (leftEncoder.get()*DISTANCEMULTIPLIER < 10) {
                RobotDrive.arcadeDrive(0, 0);
                timer.stop();
                timer.reset();
                LeavingTarmac = false;
            }
        }
    }
    
  }
  
}