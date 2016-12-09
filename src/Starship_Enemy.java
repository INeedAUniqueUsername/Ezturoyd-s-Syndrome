import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

public class Starship_Enemy extends Starship {
	
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
		return object.getVelRadial(angle_towards_object) - getVelRadial(angle_towards_object);
	}
	public void faceTarget()
	{
		String action_thrusting = "";
		String action_rotation = "";
		String action_strafing = "";
		final String act_thrust = "Thrust";
		final String act_brake = "Brake";
		final String act_turn_ccw = "CCW";
		final String act_turn_cw = "CW";
		double angle_to_target = getAngleTowards(target);
		double r_decel_time = Math.abs(vel_r/ROTATION_DECEL);
		//double angle_to_target_future = angle_to_target + target.getVelR() * r_decel_time;
		//Let's relearn AP Physics I!
		double pos_r_future =
				pos_r
				+ vel_r * r_decel_time
				+ ((vel_r > 0) ? -1 : 1) * (1/2) * ROTATION_DECEL * Math.pow(r_decel_time, 2)
				;	//Make sure that the deceleration value has the opposite sign of the rotation speed
		double faceAngleDiffCCW = modRangeDegrees(angle_to_target - pos_r_future);
		double faceAngleDiffCW = modRangeDegrees(pos_r_future - angle_to_target);
		double faceAngleDiff = min(faceAngleDiffCCW, faceAngleDiffCW);
		if(faceAngleDiff > getMaxAngleDifference())
		{
			if(faceAngleDiffCW < faceAngleDiffCCW)
			{
				action_rotation = act_turn_cw;
				printToWorld("Status (Facing): CW");
			}
			else if(faceAngleDiffCCW < faceAngleDiffCW)
			{
				action_rotation = act_turn_ccw;
				printToWorld("Status (Facing): CCW");
			}
			else
			{
				if(Math.random() > .5) {
					action_rotation = act_turn_ccw;
				} else {
					action_rotation = act_turn_ccw;
				}
				printToWorld("Status (Facing): Random");
			}
		}
		else
		{
			printToWorld("Status (Facing): Aligned");
		}
		double velAngle = getVelAngle();
		double velAngleDiffCCW = modRangeDegrees(angle_to_target - velAngle);
		double velAngleDiffCW = modRangeDegrees(velAngle - angle_to_target);
		double velAngleDiff = min(velAngleDiffCCW, velAngleDiffCW);
		if(velAngleDiff > 120)
		{
			action_thrusting = act_brake;
			printToWorld("Status: Brake");
		}
		else if(velAngleDiff > 60)
		{
			printToWorld("Status: Nothing");	
		}
		else
		{
			action_thrusting = act_thrust;
			printToWorld("Status: Thrust");
		}
		
		double distance_to_target = getDistanceBetween(target);
		double velDiff = getVelRadial(angle_to_target) - target.getVelRadial(angle_to_target);
		if(distance_to_target > getMinSeparation())
		{
			action_thrusting = act_thrust;
			printToWorld("Status (Distance): Far");
		}
		else
		{
			action_thrusting = act_brake;
			printToWorld("Status (Distance): Close");
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
		switch(action_thrusting)
		{
		case act_thrust: thrust();
			break;
		case act_brake: brake();
			break;
		}
		switch(action_rotation)
		{
		case act_turn_ccw: turnCCW();
			break;
		case act_turn_cw: turnCW();
			break;
		}
		
	}
	public double getMaxAngleDifference()
	{
		return 10;
	}
	public double getMinSeparation()
	{
		return 300;
	}
}
