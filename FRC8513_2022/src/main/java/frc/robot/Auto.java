package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto {
    public Robot thisRobot;
    double goalMotorSpeed = 100000;
    double motorDelta = 100000;
    double controllerOutput = 100000;

    public Auto(Robot thisRobotParameter) {
        thisRobot = thisRobotParameter;
    }

    /** This function is run once at the beginning of each autonomous mode. */
    public void autoInit() {
        thisRobot.m_timer.reset();
        thisRobot.m_timer.start();
        thisRobot.autoStartingAngle = thisRobot.currentAngle;
        thisRobot.turnPID.reset();
        SmartDashboard.putNumber("autoStartingAngle", thisRobot.autoStartingAngle);
        thisRobot.leftEncoder.setPosition(0);
        thisRobot.rightEncoder.setPosition(0);
        thisRobot.straightPID.reset();
        thisRobot.distancePID.reset();
        thisRobot.autoStep = 0;

    }

    public void autoPeriodic() {
        switch (thisRobot.autoDashboard) {
            case 0:
                thisRobot.autoAction = 0;
                break;
            case 1:
                driveForward();
                break;
            case 2:
                driveBackwards();
                break;
            case 3:
                turn180();
                break;
            case 4:
                shootReverse();
                break;
            case 5:
                IntakeStraightTurnStraightShoot();
                break;
            case 6:
                IntakeStraightTurnStraightShoot2();
                break;
            default:
                thisRobot.autoDashboard = 0;
        }
        autoActions();
        if (thisRobot.autoActionIsDone) {
            thisRobot.autoStep++;
            thisRobot.autoActionIsDone = false;
        }
        thisRobot.intakeStateController.updateState();
        thisRobot.intakeStateController.updateMotorPower();
        SmartDashboard.putNumber("autoStep", thisRobot.autoStep);
    }

    /** This function is called periodically during autonomous. */
    public void autoActions() {
        switch ((int) thisRobot.autoAction) {
            case 0: // default
                thisRobot.m_myRobot.stopMotor();
                break;
            case 1: // turn 90 degrees to the right with PID
                controllerOutput = thisRobot.turnPID.calculate(thisRobot.currentAngle, thisRobot.autoGoalAngle);
                goalMotorSpeed = MathUtil.clamp(controllerOutput, -1, 1);
                thisRobot.m_myRobot.tankDrive(goalMotorSpeed, -goalMotorSpeed);
                // if error is less than a set value
                if (Math.abs(thisRobot.autoGoalAngle - thisRobot.currentAngle) < thisRobot.autoAngleTHold) {
                    thisRobot.tHoldCounter++;
                    if (thisRobot.tHoldCounter > thisRobot.tHoldCounterTHold) {
                        thisRobot.autoActionIsDone = true;
                    }
                } else {
                    thisRobot.tHoldCounter = 0;
                }
                break;
            case 2: // drive straight to a distance with PID
                double distanceControllerOutput = thisRobot.distancePID.calculate(
                        thisRobot.currentPosition, thisRobot.autoGoalDistance);
                goalMotorSpeed = MathUtil.clamp(distanceControllerOutput, -.6, .6);
                controllerOutput = thisRobot.straightPID
                        .calculate(thisRobot.leftEncoderPosition - thisRobot.rightEncoderPosition, 0);
                motorDelta = MathUtil.clamp(controllerOutput, -.2, .2);
                thisRobot.m_myRobot.tankDrive(goalMotorSpeed + motorDelta, goalMotorSpeed - motorDelta);
                if (Math.abs(thisRobot.autoGoalDistance - thisRobot.currentPosition) < thisRobot.autoDistanceTHold) {
                    thisRobot.tHoldCounter++;
                    if (thisRobot.tHoldCounter > thisRobot.tHoldCounterTHold) {
                        thisRobot.autoActionIsDone = true;
                    }
                } else {
                    thisRobot.tHoldCounter = 0;
                }
                break;
            default:
                thisRobot.m_myRobot.stopMotor();

        }
        SmartDashboard.putNumber("autoGoalError", thisRobot.autoGoalAngle - thisRobot.currentAngle);
        SmartDashboard.putNumber("controllerOutput", controllerOutput);
        SmartDashboard.putNumber("driveStraightMotorDelta", motorDelta);
        SmartDashboard.putNumber("goalMotorSpeed", goalMotorSpeed);
    }

    public void resetSensors() {
        thisRobot.leftEncoder.setPosition(0);
        thisRobot.rightEncoder.setPosition(0);
        thisRobot.ahrs.reset();
    }

    public void driveForward() {
        switch ((int) thisRobot.autoStep) {
            case 0: // drive forward
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 5;
                thisRobot.autoStep++;
                break;
            case 1:
                // waiting for first turn to complete
                break;
            case 2: // stopping
                thisRobot.autoAction = 0;
            default:
                thisRobot.autoAction = 0;
        }
    }

    public void driveBackwards() {
        switch ((int) thisRobot.autoStep) {
            case 0: // drive backward
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = -5;
                thisRobot.autoStep++;
                break;
            case 1:
                // waiting for first turn to complete
                break;
            case 2: // stopping
                thisRobot.autoAction = 0;
            default:
                thisRobot.autoAction = 0;
        }
    }
    public void turn180() {
        switch ((int) thisRobot.autoStep) {
            case 0: // drive backward
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = 180;
                thisRobot.autoStep++;
                break;
            case 1:
                // waiting for first turn to complete
                break;
            case 2: // stopping
                thisRobot.autoAction = 0;
            default:
                thisRobot.autoAction = 0;
        }
    }


    public void backLefttFortyFive() {
        switch ((int) thisRobot.autoStep) {
            case 0: // drive back straight
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = -10;
                thisRobot.autoStep++;
                break;
            case 1:
                // waiting for first turn to complete
                break;
            case 2:// turn left 90
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = -90;
                thisRobot.autoStep++;
                break;
            case 3:
                // waiting for driving straight to complete
                break;
            case 4: // straight
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 5:
                // waiting
                break;
            case 6: // 45 right
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = 45;
                thisRobot.autoStep++;
                break;
            case 7:
                // waiting
                break;
            case 8: // straight
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 9: // stopping
                thisRobot.autoAction = 0;
            default:
                thisRobot.autoAction = 0;

        }
    }

    // right, straight, right, straight, left, straight, left, straight
    public void rsRSlsLS() {
        switch ((int) thisRobot.autoStep) {
            case 0: // turn right 90
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = 90;
                thisRobot.autoStep++;
                break;
            case 1:
                // waiting for first turn to complete
                break;
            case 2:// straigt
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 3:
                // waiting for driving straight to complete
                break;
            case 4: // turn right 90
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = 90;
                thisRobot.autoStep++;
                break;
            case 5:
                // waiting for first turn to complete
                break;
            case 6:// straigt
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 7:
                // waiting for driving straight to complete
                break;
            case 8: // turn left 90
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = -90;
                thisRobot.autoStep++;
                break;
            case 9:
                // waiting
                break;
            case 10: // straight
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 11:
                // waiting
                break;
            case 12: // turn left 90
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = -90;
                thisRobot.autoStep++;
                break;
            case 13:
                // waiting
                break;
            case 14: // straight
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 15: // stopping
                thisRobot.autoAction = 0;
            default:
                thisRobot.autoAction = 0;
        }
    }

    public void turnAroundDriveStraightTurnAroundDriveStraight() {
        switch ((int) thisRobot.autoStep) {
            case 0: // turn 180
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = 180;
                thisRobot.autoStep++;
                break;
            case 1:
                // waiting for first turn to complete
                break;
            case 2:// straight
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 3:
                // waiting for driving straight to complete
                break;
            case 4: // turn 180
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = 180;
                thisRobot.autoStep++;
                break;
            case 5:
                // waiting for first turn to complete
                break;
            case 6:// straight
                thisRobot.autoAction = 2;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 7:
                // waiting for driving straight to complete
                break;
            default:
                thisRobot.autoAction = 0;
        }
    }

    public void intakeDriveStraightShoot() {
        switch ((int) thisRobot.autoStep) {
            case 0: // turn intake on
                thisRobot.intakeStateController.setState(3);
                thisRobot.autoStep++;
            case 1: // setting our current time
                thisRobot.autoStartTime = System.currentTimeMillis();
                thisRobot.autoStep++;
                break;
            case 2: // checking if our elapsed time is greater than out threshold, if it is we move
                    // on to the next case, otherwise, we wait
                if (System.currentTimeMillis() - thisRobot.autoStartTime > 3000) {
                    thisRobot.autoStep++;
                }
                break;
            case 3: // straight
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalDistance = 10;
                thisRobot.autoStep++;
                break;
            case 4:
                // waiting for driving straight to complete
                break;
            case 5: // turn around
                thisRobot.autoAction = 1;
                resetSensors();
                thisRobot.autoGoalAngle = 180;
                thisRobot.autoStep++;
                break;
            case 6:
                // waiting for turning straight to complete
                break;
            case 7: // shoot
                thisRobot.intakeStateController.setState(8);
                break;
            default:
                thisRobot.intakeStateController.setState(3);
        }
    }

public void shootReverse()
{
    switch ((int) thisRobot.autoStep) {
        case 0: //shoot
            thisRobot.intakeStateController.intakeState=8;
            thisRobot.autoStep++;
            break;
        case 1: //setting our current time
            thisRobot.autoStartTime = System.currentTimeMillis();
            thisRobot.autoStep++;
            break;
        case 2: //checking if our elapsed time is greater than out threshold, if it is we move on to the next case, otherwise, we wait
            if(System.currentTimeMillis()-thisRobot.autoStartTime>3000)
            {
                thisRobot.autoStep++;
            }
            break;
        case 3:
            thisRobot.intakeStateController.intakeState=1;
            thisRobot.autoStep++;
        case 4: //drive backward
            thisRobot.autoAction = 2;
            resetSensors();
            thisRobot.autoGoalDistance = -5;
            thisRobot.autoStep++;
            break;
        case 5:
        //wait for robot to finish driving backward
        break;
        case 6:
        thisRobot.autoAction = 0;
        break;
        }
}
public void intakeTurnStraightTurnStraightShoot()
{
    switch ((int) thisRobot.autoStep) {
        case 0: //turn intake on
            thisRobot.intakeStateController.setState(4);
            thisRobot.autoStep++;
            break;
        case 1: //drive straight
        thisRobot.autoAction = 2;
        resetSensors();
        thisRobot.autoGoalDistance = 3;
        thisRobot.autoStep++;
        break;
        case 2: //do nothing 
            break;
        case 3: //turn 180 degrees
            thisRobot.autoAction = 1;
            resetSensors();
            thisRobot.autoGoalAngle = 180;
            thisRobot.autoStep++;
            break;
        case 4: //do nothing 
            break;
        case 5: //drive staright 
        thisRobot.autoAction = 2;
        resetSensors();
        thisRobot.autoGoalDistance = 5;
        thisRobot.autoStep++;
            break;
        case 6: //do nothing
            break;
        case 7: //shoot
        thisRobot.intakeStateController.setState(8);
            thisRobot.autoStep++;
            break;
        case 8: //wait three seconds 
        thisRobot.autoStartTime = System.currentTimeMillis();
        thisRobot.autoStep++;
            break;
        case 9: //wait three seconds 
        if(System.currentTimeMillis()-thisRobot.autoStartTime>3000)
        {
            thisRobot.autoStep++;
        }
        break;
        case 10: //intake stop
        thisRobot.intakeStateController.setState(1);
        thisRobot.autoStep++;
            break;
        
        }
}
public void IntakeStraightTurnStraightShoot()
{
    switch ((int) thisRobot.autoStep) {
        case 0: //turn intake on
            thisRobot.intakeStateController.intakeState=3;
            SmartDashboard.putNumber("case 0", 0);
            thisRobot.autoStep++;
            break;
        case 1: //drive forward
            thisRobot.autoAction = 2;
            resetSensors();
            thisRobot.autoGoalDistance = 2;
            SmartDashboard.putNumber("case 1", 1);
            thisRobot.autoStep++;
            break;
        case 2: //setting our current time
             SmartDashboard.putNumber("case 2", 2);
            /*thisRobot.autoStartTime = System.currentTimeMillis();
            SmartDashboard.putNumber("case 2", 2);
            thisRobot.autoStep++;*/
            break;
        case 3: //checking if our elapsed time is greater than out threshold, if it is we move on to the next case, otherwise, we wait
            SmartDashboard.putNumber("case 3", 3);
            thisRobot.autoStep++;
            /*if(System.currentTimeMillis()-thisRobot.autoStartTime>5000)
            {
                thisRobot.autoStep++;
            }*/
            break;
        case 4: //turn 180 degrees
            thisRobot.autoAction = 1;
            resetSensors();
            thisRobot.autoGoalAngle = 180;
            SmartDashboard.putNumber("case 4", 4);
            thisRobot.autoStep++;
            break;
        case 5: //drive forward
            thisRobot.autoAction = 1;
            resetSensors();
            thisRobot.autoGoalDistance = 2;
            SmartDashboard.putNumber("case 5", 5);
            thisRobot.autoStep++;
            break;
        case 6:
            thisRobot.intakeStateController.intakeState=8;
            SmartDashboard.putNumber("case 6", 6);
            thisRobot.autoStep++;
            break;
        case 7:
            thisRobot.intakeStateController.intakeState=1;
            SmartDashboard.putNumber("case 7", 7);
            thisRobot.autoStep++;
            break;
        case 8:
        //wait for robot to finish driving backward
        SmartDashboard.putNumber("case 8", 8);
        break;
        case 9:
        thisRobot.autoAction = 0;
        SmartDashboard.putNumber("case 9", 9);
        break;
        }
}
public void IntakeStraightTurnStraightShoot2()
{
    switch ((int) thisRobot.autoStep) {
        case 0: //turn intake on
            thisRobot.intakeStateController.intakeState=3;
            SmartDashboard.putNumber("case 0", 0);
            thisRobot.autoStep++;
            break;
        case 1: //drive forward
            thisRobot.autoAction = 2;
            resetSensors();
            thisRobot.autoGoalDistance = 2;
            SmartDashboard.putNumber("case 1", 1);
            thisRobot.autoStep++;
            break;
        case 2: //setting our current time
             SmartDashboard.putNumber("case 2", 2);
            /*thisRobot.autoStartTime = System.currentTimeMillis();
            SmartDashboard.putNumber("case 2", 2);
            thisRobot.autoStep++;*/
            break;
        case 3: //turn 180 degrees
            thisRobot.autoAction = 2;
            resetSensors();
            thisRobot.autoGoalAngle = 180;
            SmartDashboard.putNumber("case 4", 4);
            thisRobot.autoStep++;
            break;
        case 4: //drive forward
            thisRobot.autoAction = 1;
            resetSensors();
            thisRobot.autoGoalDistance = 2;
            SmartDashboard.putNumber("case 5", 5);
            thisRobot.autoStep++;
            break;
        case 5:
            thisRobot.intakeStateController.intakeState=8;
            SmartDashboard.putNumber("case 6", 6);
            thisRobot.autoStep++;
            break;
        case 6:
            thisRobot.intakeStateController.intakeState=1;
            SmartDashboard.putNumber("case 7", 7);
            thisRobot.autoStep++;
            break;
        case 7:
        //wait for robot to finish driving backward
        SmartDashboard.putNumber("case 8", 8);
        break;
        case 8:
        thisRobot.autoAction = 0;
        SmartDashboard.putNumber("case 9", 9);
        break;
        }
}
}