import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

public class Starship_Enemy extends Starship {
	
	Space_Object target_object;
	
	public void update()
	{
		updateSpaceship();
		if(exists(target_object))
		{
			faceTarget();
		}
		//thrust();
	}
	public void setTarget(Space_Object target_new)
	{
		target_object = target_new;
	}
	public double getVelTowards(Space_Object object)
	{
		double angle_towards_object = getAngleTowards(object);
		return object.getVelRadial(angle_towards_object) - getVelRadial(angle_towards_object);
	}
	public void faceTarget()
	{
		//To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the screen.
		double target_x_center = target_object.getPosX();
		double target_y_center = target_object.getPosY();
		double target_distance_center = getDistanceBetweenPos(pos_x, pos_y, target_x_center, target_y_center);
		
		double target_x_up = target_x_center;
		double target_y_up = target_y_center - GameWindow.HEIGHT;
		double target_distance_up = getDistanceBetweenPos(pos_x, pos_y, target_x_up, target_y_up);
		
		double target_x_down = target_x_center;
		double target_y_down = target_y_center + GameWindow.HEIGHT;
		double target_distance_down = getDistanceBetweenPos(pos_x, pos_y, target_x_down, target_y_down);
		
		double target_x_right = target_x_center + GameWindow.WIDTH;
		double target_y_right = target_y_center;
		double target_distance_right = getDistanceBetweenPos(pos_x, pos_y, target_x_right, target_y_right);
		
		double target_x_left = target_x_center - GameWindow.WIDTH;
		double target_y_left = target_y_center;
		double target_distance_left = getDistanceBetweenPos(pos_x, pos_y, target_x_left, target_y_left);
		
		double target_x_focus = target_x_center;
		double target_y_focus = target_y_center;
		double target_distance_focus = target_distance_center;
		
		if(target_distance_focus > target_distance_up)
		{
			target_x_focus = target_x_up;
			target_y_focus = target_y_up;
			target_distance_focus = target_distance_up;
		}
		if(target_distance_focus > target_distance_down)
		{
			target_x_focus = target_x_down;
			target_y_focus = target_y_down;
			target_distance_focus = target_distance_down;
		}
		if(target_distance_focus > target_distance_right)
		{
			target_x_focus = target_x_right;
			target_y_focus = target_y_right;
			target_distance_focus = target_distance_right;
		}
		if(target_distance_focus > target_distance_left)
		{
			target_x_focus = target_x_left;
			target_y_focus = target_y_left;
			target_distance_focus = target_distance_left;
		}
		
		String action_thrusting = "Nothing";
		String action_rotation = "Nothing";
		String action_strafing = "Nothing";
		String action_weapon = "Nothing";
		final String act_thrust = "Thrust";
		final String act_brake = "Brake";
		final String act_turn_ccw = "CCW";
		final String act_turn_cw = "CW";
		final String act_fire = "Fire";
		double angle_to_target = getAngleTowardsPos(target_x_focus, target_y_focus);
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
		
		double distance_to_target = target_distance_focus;
		double velDiff = getVelRadial(angle_to_target) - target_object.getVelRadial(angle_to_target);
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
		
		
		
		if(faceAngleDiff + velAngleDiff < 10)
		{
			action_weapon = act_fire;
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
		printToWorld("Weapons: " + action_weapon);
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
		switch(action_weapon)
		{
		case act_fire: setFiring(true);
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
