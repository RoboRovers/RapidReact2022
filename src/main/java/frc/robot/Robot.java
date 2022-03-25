/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.*;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.w3c.dom.html.HTMLButtonElement;

import java.net.InterfaceAddress;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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

  VictorSPX leftDriveMotor1 = new VictorSPX(10);
  
  VictorSPX rightDriveMotor1 = new VictorSPX(12);
  VictorSPX leftDriveMotor2 = new VictorSPX(11);
  VictorSPX rightDriveMotor2 = new VictorSPX(13);

  //PWMVictorSPX LeftDriveMotor = new PWMVictorSPX(0); 
  //PWMVictorSPX RightDriveMotor = new PWMVictorSPX(1); 
  //DifferentialDrive RobotDrive = new DifferentialDrive(LeftDriveMotor,RightDriveMotor);
  VictorSPX intake = new VictorSPX(6);
  VictorSPX LauncherFront = new VictorSPX(4);
  VictorSPX LauncherBack = new VictorSPX(5);
  VictorSPX Bumper1 = new VictorSPX(3);
  //PWMVictorSPX IntakeRecliner = new PWMVictorSPX(7);
  CANSparkMax Climber = new CANSparkMax(1, MotorType.kBrushless);
  //PWMVictorSPX Climber = new PWMVictorSPX(9);
  Encoder leftEncoder = new Encoder(0, 1);
  Encoder rightEncoder = new Encoder(2, 3);
  ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
  boolean bumperRunning = false;
  Joystick Joystick1 = new Joystick(0);
  //VictorSPX Tilter = new VictorSPX()
  XboxController xboxController1 = new XboxController(1);
  Timer timer = new Timer();

  int reverseConstant = 1;
  Integer DoNothingAuto = 0;
  Integer OnlyLeaveTarmac = 1;
  Integer OnlyShootBall = 2;
  Integer ShootAndLeaveTarmac = 3;
  Integer Shoot2Balls = 4;
  Integer Shoot3Balls = 5;
  Integer Shoot4Balls = 6;
  Integer BallA = 1;
  Integer BallB = 2;
  Integer BallC = 3;
  SendableChooser<Integer> AutoChooser = new SendableChooser<>();
  SendableChooser<Integer> BallChooser = new SendableChooser<>();
  final double DISTANCEMULTIPLIER = 1.5707963268/360.0;
  final double LAUNCHERSPEEDHIGH = 0.9;
  final double LAUNCHERSPEEDLOW = 0.5;
  final double BUMPERSPEED = -0.9;
  final double RAMPSECONDS = 0.5;
  final double INTAKESPEED = -0.75;
  final double AUTODRIVESPEED = -0.4;
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
    AutoChooser.addOption("Shoot 3 balls", Shoot3Balls);
    BallChooser.setDefaultOption("A", BallA);
    BallChooser.addOption("B", BallB);
    BallChooser.addOption("C", BallC);
    SmartDashboard.putData("Auto Choice", AutoChooser);
    SmartDashboard.putData("Ball Choice", BallChooser);
    
    leftDriveMotor1.configOpenloopRamp(RAMPSECONDS);
    leftDriveMotor2.configOpenloopRamp(RAMPSECONDS);
    rightDriveMotor1.configOpenloopRamp(RAMPSECONDS);
    rightDriveMotor2.configOpenloopRamp(RAMPSECONDS);
    
  }
  
  
  @Override
  public void teleopInit() {
      //LauncherBack.set(ControlMode.PercentOutput,0.5);
    //CameraServer.startAutomaticCapture();

    timer.reset();
    timer.start();
    leftEncoder.reset();
    intake.set(ControlMode.PercentOutput,0);
    rightEncoder.reset();
    SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    SmartDashboard.putNumber("leftEncoderDistance", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get());
  }
  private void ArcadeDrive(double Speed, double Rotation) {
      leftDriveMotor1.set(ControlMode.PercentOutput, -1 * Speed, DemandType.ArbitraryFeedForward, Rotation);
      rightDriveMotor1.set(ControlMode.PercentOutput, Speed, DemandType.ArbitraryFeedForward, Rotation);
      leftDriveMotor2.set(ControlMode.PercentOutput, -1 * Speed, DemandType.ArbitraryFeedForward, Rotation);
      rightDriveMotor2.set(ControlMode.PercentOutput, Speed, DemandType.ArbitraryFeedForward, Rotation);
  }
  @Override
  public void teleopPeriodic() {
    if (colorSensor.getProximity() == 2047) {
        SmartDashboard.putBoolean("Bumper Down?", true);
    }
    else {
        SmartDashboard.putBoolean("Bumper Down?", false);
    }
    
    //SmartDashboard.putNumber("timer", timer.get());
    SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    SmartDashboard.putNumber("leftEncoderDistance", -1*(leftEncoder.get()*DISTANCEMULTIPLIER));
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get()*DISTANCEMULTIPLIER);
    SmartDashboard.putNumber("Color Sensor Proximity", colorSensor.getProximity());
    
    //ArcadeDrive(0.9, 0);
    //ArcadeDrive(reverseConstant * -(Joystick1.getY()), reverseConstant * Joystick1.getX());
    ArcadeDrive(Joystick1.getY(), Joystick1.getX());
    //SmartDashboard.putNumber("Controller Y", -Joystick1.getY());
    //SmartDashboard.putNumber("Controller X", Joystick1.getX());
    
    if (Joystick1.getRawButtonPressed(2)) {
        intake.set(ControlMode.PercentOutput,-INTAKESPEED);
    }
    if (Joystick1.getRawButtonReleased(2)) {
        intake.set(ControlMode.PercentOutput,0);
    }
    if (Joystick1.getRawButtonPressed(1)) {
        intake.set(ControlMode.PercentOutput, INTAKESPEED);
    }
    if (Joystick1.getRawButtonReleased(1)) {
        intake.set(ControlMode.PercentOutput,0);
    }
    if (Joystick1.getRawButtonPressed(11)) {
        reverseConstant *= -1;
    }
    if (Joystick1.getRawButtonReleased(11)) {
        
    }
    if (Joystick1.getRawButtonPressed(12)) {
        //IntakeRecliner.set(ControlMode.PercentOutput,-0.4);
    }
    if (Joystick1.getRawButtonReleased(12)) {
        //IntakeRecliner.set(ControlMode.PercentOutput,0);
    }
    if (xboxController1.getRawButtonPressed(5)) {
        LauncherFront.set(ControlMode.PercentOutput,LAUNCHERSPEEDLOW);
        LauncherBack.set(ControlMode.PercentOutput,-LAUNCHERSPEEDLOW);
    }
    if (xboxController1.getRawButtonReleased(5)) {
        LauncherFront.set(ControlMode.PercentOutput,0);
        LauncherBack.set(ControlMode.PercentOutput,0);
    }
    if (xboxController1.getRawButtonPressed(7)) {
        LauncherFront.set(ControlMode.PercentOutput,LAUNCHERSPEEDHIGH);
        LauncherBack.set(ControlMode.PercentOutput,-LAUNCHERSPEEDHIGH);
    }
    if (xboxController1.getRawButtonReleased(7)) {
        LauncherFront.set(ControlMode.PercentOutput,0);
        LauncherBack.set(ControlMode.PercentOutput,0);
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
        Bumper1.set(ControlMode.PercentOutput, BUMPERSPEED);
        
    }
    if (xboxController1.getRawButtonReleased(8)) {
        Bumper1.set(ControlMode.PercentOutput, 0);
    }
    if (xboxController1.getRawButtonPressed(6)) {
      Bumper1.set(ControlMode.PercentOutput, 0.35);
    }
    if (xboxController1.getRawButtonReleased(6)) {
        Bumper1.set(ControlMode.PercentOutput, 0);
    }
    /*
    if (xboxController1.getRawButtonPressed(6)) {
        Bumper1.set(ControlMode.PercentOutput, 0.35);
      }
      if (xboxController1.getRawButtonReleased(6)) {
          Bumper1.set(ControlMode.PercentOutput, 0);
      }
      */
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
  boolean TurningToBall2 = false;
  boolean TaxingToBall2 = false;
  boolean TurningToHub2 = false;
  boolean TaxingToTarmac2 = false;
  double ballAngleValue = 0;
  int AutoNum = 0;
    
  int ballsShot = 0;
  @Override
  public void autonomousInit() {
    
    leftDriveMotor1.setNeutralMode(NeutralMode.Brake);
    rightDriveMotor1.setNeutralMode(NeutralMode.Brake);
    leftDriveMotor2.setNeutralMode(NeutralMode.Brake);
    rightDriveMotor2.setNeutralMode(NeutralMode.Brake);
    leftEncoder.reset();
    rightEncoder.reset();
    timer.stop();
    timer.reset();

    AutoNum = AutoChooser.getSelected();
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
            ShootingBall = true;
            //DroppingIntake = true;
            DoingAuto = true;
            TurningToBall = true;
            TurningToHub = true;
            TaxingToBall = true;
            TurningToHub = true;
            break;
        case 5:
            DroppingIntake = true;
            DoingAuto = true;
            TurningToHub = true;
            TaxingToBall = true;
            TurningToHub = true;
            TurningToBall2 = true;
            TaxingToBall2 = true;
            TaxingToBall2 = true;
            TurningToHub2 = true;
            TaxingToTarmac2 = true;
            break;
        
    }
      
    ballsShot = 0;
    int BallNum = BallChooser.getSelected();
    switch (BallNum) {
        case 1:
            SmartDashboard.putString("Ball Chosen", "A");
            ballAngleValue = -2;
            break;
        case 2:
        SmartDashboard.putString("Ball Chosen", "B");
            ballAngleValue = -150;
            TurningToTarmac = false;
            TurningToHub = true;
            //TaxingToHub = true;
            break;
        case 3:
        SmartDashboard.putString("Ball Chosen", "C");
            ballAngleValue = 2;
            break;
    }
    
  }

  @Override
  public void autonomousPeriodic() {
    //SmartDashboard.putNumber("leftEncoderValue", leftEncoder.get());
    SmartDashboard.putNumber("rightEncoderValue", rightEncoder.get());
    //SmartDashboard.putNumber("leftEncoderDistance", -1*(leftEncoder.get()*DISTANCEMULTIPLIER));
    SmartDashboard.putNumber("rightEncoderDistance", rightEncoder.get()*DISTANCEMULTIPLIER);
    SmartDashboard.putNumber("timer", timer.get());
    SmartDashboard.putNumber("AutoNum", AutoNum);
    if (AutoNum == 0) {

    }
    else if  (AutoNum < 4) {
        if (ShootingBall) {
            SmartDashboard.putString("Current Task", "Shooting");
            if (timer.get() == 0) {
                timer.start();
                //Bumper1.set(ControlMode.PercentOutput, -0.75);
                LauncherFront.set(ControlMode.PercentOutput,LAUNCHERSPEEDHIGH);
                LauncherBack.set(ControlMode.PercentOutput,-LAUNCHERSPEEDHIGH);
                
                
            }
            else if (timer.get() > 1 && timer.get() < 1.5) {
                Bumper1.set(ControlMode.PercentOutput, -0.75);
            }
            else if (timer.get() > 2.5) {
                LauncherFront.set(ControlMode.PercentOutput,0);
                LauncherBack.set(ControlMode.PercentOutput,0);
                Bumper1.set(ControlMode.PercentOutput, 0);
                timer.stop();
                timer.reset();
                
                
                
                ShootingBall = false;
                
                
                if (colorSensor.getProximity() != 2047) {
                    //FixingBumper = true;
                }
            }
        }
        else if (FixingBumper) {
            SmartDashboard.putString("Current Task", "fixing bumper");
            if (colorSensor.getProximity() == 2047) {
                FixingBumper = false;
            }
            else if (colorSensor.getProximity() > 100 && colorSensor.getProximity() < 200) {
                Bumper1.set(ControlMode.PercentOutput, 0);
            }
            else if (colorSensor.getProximity() < 100) {
                Bumper1.set(ControlMode.PercentOutput, 0.1);
            }
            
        }
        else if (LeavingTarmac) {
            LauncherBack.set(ControlMode.PercentOutput, -LAUNCHERSPEEDHIGH);
            LauncherFront.set(ControlMode.PercentOutput, LAUNCHERSPEEDHIGH);
            intake.set(ControlMode.PercentOutput, INTAKESPEED);
            /*
            SmartDashboard.putString("Current Task", "leaving tarmac");
            if (timer.get() == 0) {
                timer.start();
                ArcadeDrive(AUTODRIVESPEED, 0);
            }
            else if (timer.get() < 2) {
                ArcadeDrive(AUTODRIVESPEED, 0);
            }
            else {
                timer.stop();
                timer.reset();
                ArcadeDrive(0, 0);
                LeavingTarmac = false;
            }
            */
        }
    }
    else if (AutoNum == 4) {
        if (ShootingBall) {
            SmartDashboard.putString("Current Task", "Shooting");
            if (timer.get() == 0) {
                timer.start();
                //Bumper1.set(ControlMode.PercentOutput, -0.75);
                LauncherFront.set(ControlMode.PercentOutput,LAUNCHERSPEEDHIGH);
                LauncherBack.set(ControlMode.PercentOutput,-LAUNCHERSPEEDHIGH);
                
                
            }
            else if (timer.get() > 0 && timer.get() < 1) {
                LauncherFront.set(ControlMode.PercentOutput,LAUNCHERSPEEDHIGH);
                LauncherBack.set(ControlMode.PercentOutput,-LAUNCHERSPEEDHIGH);
            }
            else if (timer.get() > 1 && timer.get() < 2) {
                Bumper1.set(ControlMode.PercentOutput, -0.75);
            }
            else {
                LauncherFront.set(ControlMode.PercentOutput,0);
                LauncherBack.set(ControlMode.PercentOutput,0);
                Bumper1.set(ControlMode.PercentOutput, 0);
                timer.stop();
                timer.reset();
                
                
                
                ShootingBall = false;
                
                
                if (colorSensor.getProximity() != 2047) {
                    //FixingBumper = true;
                }
            }
        } 
        else if (FixingBumper) {
            SmartDashboard.putString("Current Task", "fixing bumper");
            if (colorSensor.getProximity() == 2047) {
                FixingBumper = false;
            }
            else if (colorSensor.getProximity() > 100 && colorSensor.getProximity() < 200) {
                Bumper1.set(ControlMode.PercentOutput, 0);
            }
            else if (colorSensor.getProximity() < 100) {
                Bumper1.set(ControlMode.PercentOutput, 0.1);
            }
        }
        else if (DroppingIntake) {
            if (timer.get() == 0) {
                timer.start();

            }
            else if (timer.get() < 0.5) {
                ArcadeDrive(0.1, 0);
            }
            else if (timer.get() > 1.5) {
                DroppingIntake = false;
            }
        }
        else if (TurningToBall) {
            if (ballAngleValue > 0 && rightEncoder.get() < ballAngleValue) {
                ArcadeDrive(0, -0.4);
            }
            else if (ballAngleValue < 0 && rightEncoder.get() > ballAngleValue) {
                ArcadeDrive(0, 0.4);
            }
            else {
                TurningToBall = false;
                leftEncoder.reset();
                rightEncoder.reset();
            }
        }
        else if (TaxingToBall) {
            SmartDashboard.putString("Current Task", "Taxing to Ball");
            if (rightEncoder.get()*DISTANCEMULTIPLIER < 3.5) {
                timer.start();
                intake.set(ControlMode.PercentOutput,INTAKESPEED);
                ArcadeDrive(AUTODRIVESPEED, 0);
            }

            else {
                timer.stop();
                timer.reset();
                intake.set(ControlMode.PercentOutput,0);
                ArcadeDrive(0, 0);
                TaxingToBall = false;
            }
        }
        else if (TaxingToTarmac) {
            SmartDashboard.putString("Current Task", "Taxing to tarmac");
            if (timer.get() == 0) {
                timer.start();
                ArcadeDrive(-AUTODRIVESPEED, 0);
                
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER > 0) {
                ArcadeDrive(-AUTODRIVESPEED, 0);
            }
            else {
                ArcadeDrive(0, 0);
                timer.stop();
                timer.reset();
                TaxingToTarmac = false;
                rightEncoder.reset();
                leftEncoder.reset();
            }
        }
        else if (TurningToHub) {
            double hubAngleValue = -ballAngleValue;
            SmartDashboard.putString("Current Task", "Turning");
            if (timer.get() == 0) {
                timer.start();
                leftEncoder.reset();
                rightEncoder.reset();
                

                
            }
            else if (hubAngleValue < 0 && rightEncoder.get()*DISTANCEMULTIPLIER > hubAngleValue) {
                ArcadeDrive(0, -0.4);
            }
            else if (hubAngleValue > 0 && rightEncoder.getDistance()*DISTANCEMULTIPLIER < hubAngleValue) {
                ArcadeDrive(0, 0.4);
            }
            else {
                ArcadeDrive(0, 0);
                
                timer.stop();
                timer.reset();
                TurningToHub = false;
                ShootingBall = true;
            }
        }

    }
    else if (AutoNum == 5) {
        if (ShootingBall) {
            SmartDashboard.putString("Current Task", "Shooting");
            if (timer.get() == 0) {
                timer.start();
                //Bumper1.set(ControlMode.PercentOutput, -0.75);
                LauncherFront.set(ControlMode.PercentOutput,0.8);
                LauncherBack.set(ControlMode.PercentOutput,-0.8);
                
                
            }
            else if (timer.get() > 1 && timer.get() < 1.5) {
                Bumper1.set(ControlMode.PercentOutput, -0.75);
            }
            else if (timer.get() > 3) {
                LauncherFront.set(ControlMode.PercentOutput,0);
                LauncherBack.set(ControlMode.PercentOutput,0);
                Bumper1.set(ControlMode.PercentOutput, 0);
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
                Bumper1.set(ControlMode.PercentOutput, 0);
            }
            else if (colorSensor.getProximity() < 100) {
                Bumper1.set(ControlMode.PercentOutput, 0.1);
            }
        }
        else if (DroppingIntake) {
            if (timer.get() == 0) {
                timer.start();

            }
            else if (timer.get() < 0.5) {
                ArcadeDrive(0.1, 0);
            }
            else if (timer.get() > 1.5) {
                DroppingIntake = false;
            }
        }
        
        else if (TaxingToBall) {
            SmartDashboard.putString("Current Task", "Taxing to Ball");
            if (rightEncoder.get()*DISTANCEMULTIPLIER < 3.5) {
                timer.start();
                intake.set(ControlMode.PercentOutput,INTAKESPEED);
                ArcadeDrive(AUTODRIVESPEED, 0);
            }

            else {
                timer.stop();
                timer.reset();
                intake.set(ControlMode.PercentOutput,0);
                ArcadeDrive(0, 0);
                TaxingToBall = false;
            }
        }
        else if (TaxingToTarmac) {
            SmartDashboard.putString("Current Task", "Taxing to tarmac");
            if (timer.get() == 0) {
                timer.start();
                ArcadeDrive(-AUTODRIVESPEED, 0);
                
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER > 0) {
                ArcadeDrive(-AUTODRIVESPEED, 0);
            }
            else {
                ArcadeDrive(0, 0);
                timer.stop();
                timer.reset();
                TaxingToTarmac = false;
            }
        }
        else if (TurningToHub) {
            SmartDashboard.putString("Current Task", "Turning");
            if (timer.get() == 0) {
                timer.start();
                leftEncoder.reset();
                rightEncoder.reset();
                
                
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER > -50) {
                ArcadeDrive(0, 0.4);
            }

            else {
                ArcadeDrive(0, 0);
                
                timer.stop();
                timer.reset();
                TurningToHub = false;
                ShootingBall = true;
            }
        }
        else if (TurningToBall2) {
            SmartDashboard.putString("Current Task", "Turning");
            if (timer.get() == 0) {
                timer.start();
                leftEncoder.reset();
                rightEncoder.reset();
                
                
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER > -300) {
                ArcadeDrive(0, 0.4);
            }

            else {
                ArcadeDrive(0, 0);
                leftEncoder.reset();
                rightEncoder.reset();
                timer.stop();
                timer.reset();
                TurningToHub = false;
                ShootingBall = true;
            }

        }
        else if (TaxingToBall2) {
            SmartDashboard.putString("Current Task", "Taxing to Ball");
            if (rightEncoder.get()*DISTANCEMULTIPLIER < 7) {
                timer.start();
                intake.set(ControlMode.PercentOutput,INTAKESPEED);
                ArcadeDrive(AUTODRIVESPEED, 0);
            }

            else {
                timer.stop();
                timer.reset();
                intake.set(ControlMode.PercentOutput,0);
                ArcadeDrive(0, 0);
                TaxingToBall = false;
                TaxingToTarmac = true;
            }
        }
        else if (TurningToHub2) {
            SmartDashboard.putString("Current Task", "Turning");
            if (timer.get() == 0) {
                timer.start();
                leftEncoder.reset();
                rightEncoder.reset();
                
                
            }
            else if (rightEncoder.get()*DISTANCEMULTIPLIER > 300) {
                ArcadeDrive(0, -0.4);
            }

            else {
                ArcadeDrive(0, 0);
                
                timer.stop();
                timer.reset();
                TurningToHub = false;
                ShootingBall = true;
            }
        }

    }
  }
  
}