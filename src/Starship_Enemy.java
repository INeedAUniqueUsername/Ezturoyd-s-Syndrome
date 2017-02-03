import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;

public class Starship_Enemy extends Starship {
	
	ArrayList<Space_Object> targets = new ArrayList<Space_Object>();
	
	final String ACT_THRUST = "Thrust";
	final String ACT_BRAKE = "Brake";
	final String ACT_TURN_CCW = "CCW";
	final String ACT_TURN_CW = "CW";
	final String ACT_FIRE = "Fire";
	
	double aim = 0;
	
	public void draw(Graphics g)
	{
		drawStarship(g);
		
		Space_Object target = getTargetPrimary();
		if(exists(target))
		{
			Point2D.Double pos = getPos();
			Point2D.Double pos_target = target.getPos();
			Point2D.Double pos_solution = polarOffset(getPos(), aim, getDistanceBetween(target));
			drawArrow(g, pos, pos_solution);
			drawArrow(g, pos, pos_target);
			drawArrow(g, pos_target, pos_solution);
		}
			
	}
	
	public void update()
	{
		updateSpaceship();
		
		
		removeDeadTargets();
		ArrayList<Space_Object> objectsTooClose = new ArrayList<Space_Object>();
		Space_Object target_primary = targets.size() > 0 ? targets.get(0) : null;
		for(Space_Object o : world.getStarships())
		{
			if(!o.equals(this))
			{
				if(getDistanceBetween(o) < getMinSeparationFromOthers())
				{
					objectsTooClose.add(o);
				}
				
			}
		}
		
		int objectsTooCloseCount = objectsTooClose.size();
		if(objectsTooCloseCount > 0)
		{
			double angle_destination = 0;
			for(Space_Object o : objectsTooClose)
			{
				angle_destination += getAngleFrom(o);
			}
			angle_destination /= objectsTooCloseCount;
			turnDirection(calcTurnDirection(angle_destination));
			thrust();
			System.out.println("Too close to " + objectsTooClose);
			System.out.println("Destination Angle: " + angle_destination);
		} else if(exists(target_primary))
		{
			attackObject(target_primary);
		}
		//thrust();
	}
	public void removeDeadTargets()
	{
		Iterator<Space_Object> o_i = targets.iterator();
		while(o_i.hasNext())
		{
			Space_Object o = o_i.next();
			if(!world.isAlive(o))
			{
				targets.remove(o);
			}
		}
	}
	public void setTargetPrimary(Space_Object target_new)
	{
		targets.add(0, target_new);
	}
	public void onAttacked(Space_Object attacker)
	{
		setTargetPrimary(attacker);
	}
	public double getVelTowards(Space_Object object)
	{
		double angle_towards_object = getAngleTowards(object);
		return object.getVelRadial(angle_towards_object) - getVelRadial(angle_towards_object);
	}
	public void attackObject(Space_Object target)
	{;
		//To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the screen.
		double target_x_center = target.getPosX();
		double target_y_center = target.getPosY();
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
		//double angle_to_target = getAngleTowardsPos(target_x_focus, target_y_focus);
		double angle_to_target = calcFireSolution(getPos(), new Point2D.Double(target_x_focus, target_y_focus), new Point2D.Double(target.getVelX() - getVelX(), target.getVelY() - getVelY()));
		double aim = angle_to_target;
		double faceAngleDiff = calcFutureAngleDifference(angle_to_target);
		if(faceAngleDiff > getMaxAngleDifference())
		{
			action_rotation = calcTurnDirection(angle_to_target);
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
			action_thrusting = ACT_BRAKE;
			printToWorld("Status: Brake");
		}
		else if(velAngleDiff > 60)
		{
			printToWorld("Status: Nothing");
		}
		else
		{
			action_thrusting = ACT_THRUST;
			printToWorld("Status: Thrust");
		}
		
		double distance_to_target = target_distance_focus;
		double velDiff = getVelRadial(angle_to_target) - target.getVelRadial(angle_to_target);
		if(distance_to_target > getMaxSeparationFromTarget())
		{
			action_thrusting = ACT_THRUST;
			printToWorld("Status (Distance): Far");
		} else if(distance_to_target < getMinSeparationFromTarget()) {
			action_rotation = calcTurnDirection(getAngleFrom(target));
			if(faceAngleDiff > 90)
			{
				action_thrusting = ACT_THRUST;
			}
		} else {
			action_thrusting = ACT_BRAKE;
			printToWorld("Status (Distance): Close");
		}
		if(faceAngleDiff < 0.5)
		{
			action_weapon = ACT_FIRE;
		}
		printToWorld("Angle to Target: " + angle_to_target);
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
		switch(action_thrusting) {
		case	ACT_THRUST:		thrust();			break;
		case	ACT_BRAKE:		brake();			break;
		}
		switch(action_rotation) {
		case	ACT_TURN_CCW:	turnCCW();			break;
		case	ACT_TURN_CW:	turnCW();			break;
		}
		switch(action_weapon) {
		case	ACT_FIRE:		setFiring(true);	break;
		}
		
	}
	/*
	public double calcFireSolution(Space_Object target)
	{
		Weapon weapon_primary = getWeaponPrimary();
		double weapon_speed = weapon_primary.getProjectileSpeed();
		
		//Here is our initial estimate. If the target is moving, then by the time the shot reaches the target's original position, the target will be somnewhere else
		double time_to_hit_estimate = getDistanceBetween(target) / weapon_speed;
		Point2D.Double pos_target_future = target.calcFuturePos(time_to_hit_estimate);
		double angle_to_hit_estimate = getAngleTowardsPos(pos_target_future);
		
		//System.out.println("Try 0");
		//System.out.println("Time to Hit: " + time_to_hit_estimate);
		
		//Calculate the time to hit the target at its new position, and then calculate where the target will be relative to its original position after the new estimated time.
		boolean active = true;
		double time_to_hit_old = 0;
		for(int i = 1; i < 10; i++)
		{
			double time_to_hit = getDistanceBetweenPos(pos_target_future) / weapon_speed;
			pos_target_future = target.calcFuturePos(time_to_hit);
			
			//System.out.println("Try " + i);
			//System.out.println("Time to Hit: " + time_to_hit);
			
			if(Math.abs(time_to_hit - time_to_hit_old) < 1)
			{
				active = false;
				break;
			}
			time_to_hit_old = time_to_hit;
		}
		
		/*
		 * double time_to_hit = 0;
		 * double 
		 *//*
		
		double angle_to_hit = getAngleTowardsPos(pos_target_future);
		if(angle_to_hit_estimate != angle_to_hit)
		{
			System.out.println("Angle (Original): " + angle_to_hit_estimate);
			System.out.println("Angle (Actual): " + angle_to_hit);
			
		}
		return angle_to_hit;
	}
*/
	
	public double calcFireSolution(Point2D.Double pos_source, Point2D.Double pos_target, Point2D.Double vel_diff)
	{
		Weapon weapon_primary = getWeaponPrimary();
		double weapon_speed = weapon_primary.getProjectileSpeed();
		
		//Here is our initial estimate. If the target is moving, then by the time the shot reaches the target's original position, the target will be somnewhere else
		double time_to_hit_estimate = getDistanceBetweenPos(pos_target) / weapon_speed;
		Point2D.Double pos_target_future = calcFuturePos(pos_target, vel_diff, time_to_hit_estimate);
		double angle_to_hit_estimate = getAngleTowardsPos(pos_target_future);
		
		//System.out.println("Try 0");
		//System.out.println("Time to Hit: " + time_to_hit_estimate);
		
		//Calculate the time to hit the target at its new position, and then calculate where the target will be relative to its original position after the new estimated time.
		boolean active = true;
		double time_to_hit_old = 0;
		for(int i = 1; i < 10; i++)
		{
			double time_to_hit = getDistanceBetweenPos(pos_target_future) / weapon_speed;
			pos_target_future = calcFuturePos(pos_target, vel_diff, time_to_hit);
			
			//System.out.println("Try " + i);
			//System.out.println("Time to Hit: " + time_to_hit);
			
			if(Math.abs(time_to_hit - time_to_hit_old) < 1)
			{
				active = false;
				break;
			}
			time_to_hit_old = time_to_hit;
		}
		
		double angle_to_hit = getAngleTowardsPos(pos_target_future);
		if(angle_to_hit_estimate != angle_to_hit)
		{
			System.out.println("Angle (Original): " + angle_to_hit_estimate);
			System.out.println("Angle (Actual): " + angle_to_hit);
			
		}
		return angle_to_hit;
	}
	public Point2D.Double calcFuturePos(Point2D.Double origin, Point2D.Double vel, double time)
	{
		double angle = arctanDegrees(vel.getY(), vel.getX());
		double speed = Math.sqrt(Math.pow(vel.getX(), 2) + Math.pow(vel.getY(), 2));
		return polarOffset(origin, angle, speed * time);
	}
	public double calcFutureAngle()
	{
		double r_decel_time = Math.abs(vel_r/ROTATION_DECEL);
		//double angle_to_target_future = angle_to_target + target.getVelR() * r_decel_time;
		//Let's relearn AP Physics I!
		return    pos_r
				+ vel_r * r_decel_time
				+ ((vel_r > 0) ? -1 : 1) * (1/2) * ROTATION_DECEL * Math.pow(r_decel_time, 2)
				;	//Make sure that the deceleration value has the opposite sign of the rotation speed
	}
	public String calcTurnDirection(double target_angle)
	{
		double pos_r_future = calcFutureAngle();
		double faceAngleDiffCCW = modRangeDegrees(target_angle - pos_r_future);
		double faceAngleDiffCW = modRangeDegrees(pos_r_future - target_angle);
		double faceAngleDiff = min(faceAngleDiffCCW, faceAngleDiffCW);
		if(faceAngleDiff > getMaxAngleDifference())
		{
			if(faceAngleDiffCW < faceAngleDiffCCW)
			{
				printToWorld("Status (Facing): CW");
				return ACT_TURN_CW;
			}
			else if(faceAngleDiffCCW < faceAngleDiffCW)
			{
				printToWorld("Status (Facing): CCW");
				return ACT_TURN_CCW;
			}
			else
			{
				printToWorld("Status (Facing): Random");
				if(Math.random() > .5) {
					return ACT_TURN_CW;
				} else {
					return ACT_TURN_CCW;
				}
			}
		}
		else
		{
			printToWorld("Status (Facing): Aligned");
			return "";
		}
	}
	public double calcFutureAngleDifference(double angle_target)
	{
		double r_decel_time = Math.abs(vel_r/ROTATION_DECEL);
		//double angle_to_target_future = angle_to_target + target.getVelR() * r_decel_time;
		//Let's relearn AP Physics I!
		double pos_r_future =
				pos_r
				+ vel_r * r_decel_time
				+ ((vel_r > 0) ? -1 : 1) * (1/2) * ROTATION_DECEL * Math.pow(r_decel_time, 2)
				;	//Make sure that the deceleration value has the opposite sign of the rotation speed
		double faceAngleDiffCCW = modRangeDegrees(angle_target - pos_r_future);
		double faceAngleDiffCW = modRangeDegrees(pos_r_future - angle_target);
		return min(faceAngleDiffCCW, faceAngleDiffCW);
	}
	public void turnDirection(String direction)
	{
		switch(direction)
		{
		case	ACT_TURN_CCW:	turnCCW();			break;
		case	ACT_TURN_CW:	turnCW();			break;
		}
	}
	public Space_Object getTargetPrimary()
	{
		return targets.size() > 0 ? targets.get(0) : null;
	}
	public double getMaxAngleDifference()
	{
		return 3;
	}
	public double getMinSeparationFromAttackers()
	{
		return 300;
	}
	public double getMaxSeparationFromTarget()
	{
		return 300;
	}
	public double getMinSeparationFromTarget()
	{
		return 200;
	}
	public double getMinSeparationFromOthers()
	{
		return 100;
	}
}
