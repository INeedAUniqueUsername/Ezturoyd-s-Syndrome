import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

public class Spaceship_Enemy extends Spaceship {
	
	Space_Object target;
	
	public void update()
	{
		updateSpaceship();
		if(exists(target))
		{
			faceTarget();
		}
		//thrust();
	}
	public void setTarget(Space_Object target_new)
	{
		target = target_new;
	}
	public double getVelTowards(Space_Object object)
	{
		double angle_towards_object = getAngleTowards(object);
		return object.getVelAngled(angle_towards_object) - getVelAngled(angle_towards_object);
	}
	public void faceTarget()
	{
		double angle_to_target = getAngleTowards(target);
		double r_decel_time = Math.abs(vel_r/ROTATION_DECEL);
		//double angle_to_target_future = angle_to_target + target.getVelR() * r_decel_time;
		//Let's relearn AP Physics I!
		double pos_r_future =
				pos_r
				+ vel_r * r_decel_time
				+ ((vel_r > 0) ? -1 : 1) * (1/2) * ROTATION_DECEL * Math.pow(r_decel_time, 2)
				;	//Make sure that the deceleration value has the opposite sign of the rotation speed
		double faceAngleDiffCCW = modRangeDegrees(angle_to_target - pos_r);
		double faceAngleDiffCW = modRangeDegrees(pos_r - angle_to_target);
		double faceAngleDiff = min(faceAngleDiffCCW, faceAngleDiffCW);
		if(faceAngleDiff > getMaxAngleDifference())
		{
			if(faceAngleDiffCW < faceAngleDiffCCW)
			{
				turnCW();
			}
			else if(faceAngleDiffCCW < faceAngleDiffCW)
			{
				turnCCW();
			}
		}
		double velAngle = getVelAngle();
		double velAngleDiffCCW = modRangeDegrees(angle_to_target - velAngle);
		double velAngleDiffCW = modRangeDegrees(velAngle - angle_to_target);
		double velAngleDiff = min(velAngleDiffCCW, velAngleDiffCW);
		if(velAngleDiff > 120)
		{
			brake();
			printToWorld("Status: Brake");
		}
		else if(velAngleDiff > 60)
		{
			printToWorld("Status: Nothing");			
		}
		else
		{
			thrust();
			printToWorld("Status: Thrust");
		}
		printToWorld("Angle to Target: " + angle_to_target);
		printToWorld("Facing Angle Difference CCW: " + faceAngleDiffCCW);
		printToWorld("Facing Angle Difference CW: " + faceAngleDiffCW);
		printToWorld("Facing Angle Difference: " + faceAngleDiff);
		printToWorld("Max Facing Angle Difference: " + getMaxAngleDifference());
		printToWorld("Velocity Angle: " + velAngle);
		printToWorld("Velocity Angle Difference CCW: " + velAngleDiffCCW);
		printToWorld("Velocity Angle Difference CW: " + velAngleDiffCW);
		printToWorld("Velocity Angle Difference: " + velAngleDiff);
		/*
		double velAngle = getVelAngle();
		double decelAngle = velAngle + 180;
		double x_decel = DECEL * cosDegrees(decelAngle);
		double x_decel_time = Math.abs(vel_x/x_decel);
		double pos_x_future =
			pos_x
			+ vel_x * x_decel_time
			+ (vel_x > 0 ? -1 : 1) * (1/2) * x_decel * Math.pow(x_decel_time, 2)
			;	//Make sure that the deceleration value has the opposite sign of the velocity
		
		double y_decel = DECEL * sinDegrees(decelAngle);
		double y_decel_time = Math.abs(vel_y/y_decel);
		double pos_y_future =
			pos_y
			+ vel_y * y_decel_time
			+ (vel_y > 0 ? -1 : 1) * (1/2) * y_decel * Math.pow(y_decel_time, 2);
		*/
	}
	public double getMaxAngleDifference()
	{
		return 90;
	}
}
