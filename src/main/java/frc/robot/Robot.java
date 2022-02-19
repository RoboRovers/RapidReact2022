

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.*;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.networktables.*;

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

  ADXRS450_Gyro gyro = new ADXRS450_Gyro();
  PWMVictorSPX RightDriveMotor = new PWMVictorSPX(2); 
  PWMVictorSPX LeftDriveMotor = new PWMVictorSPX(1);
  DifferentialDrive RobotDrive = new DifferentialDrive(LeftDriveMotor,RightDriveMotor);

  //PWMVictorSPX Intake = new PWMVictorSPX(2);
  Joystick Joystick1 = new Joystick(0);
  //intake, shooter, transfer
  //private final AnalogInput ultrasonic = new AnalogInput(0);
  Encoder encoder = new Encoder(0, 1);
  NetworkTableEntry encoderEntry;
  @Override
  public void robotInit() {
    //NetworkTableInstance inst = NetworkTableInstance.getDefault();
    //SmartDashboard.putString("test", "hello world");
    Shuffleboard.getTab("gyro").add(gyro);
    //System.out.print("started robot");
  }

  @Override
  public void teleopInit() {
    encoder.reset();
    encoder.setDistancePerPulse(1.0/236.0);
    int encoderRawValue = encoder.getRaw();
    SmartDashboard.putString("Encoder Raw", String.valueOf(encoderRawValue));
    double encoderDistanceValue = encoder.getDistance();
    SmartDashboard.putString("Encoder Distance", String.valueOf(encoderDistanceValue));
    int encoderValue = encoder.get();
    SmartDashboard.putString("Encoder", String.valueOf(encoderValue));
  }

  @Override
  public void teleopPeriodic() {
    RobotDrive.arcadeDrive((Joystick1.getY()), -(Joystick1.getX()));
    /*
    if (Joystick1.getRawButtonPressed(5)) {
      Intake.set(-0.75);
    }
    if (Joystick1.getRawButtonReleased(5)) {
      Intake.set(0);
    }
    double rawValue = ultrasonic.getValue();
    double voltage_scale_factor = 5/RobotController.getVoltage5V();
    double currentDistanceCentimeters = rawValue * voltage_scale_factor * 0.125;
    */
    
    int encoderRawValue = encoder.getRaw();
    SmartDashboard.putString("Encoder Raw", String.valueOf(encoderRawValue));
    double encoderDistanceValue = encoder.getDistance();
    SmartDashboard.putString("Encoder Distance", String.valueOf(encoderDistanceValue));
    int encoderValue = encoder.get();
    SmartDashboard.putString("Encoder", String.valueOf(encoderValue));

    
    
  }
  
  @Override
  public void autonomousInit() {
    
  }

  @Override
  public void autonomousPeriodic() {
    
  }
  
}