import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;

public class Starship_NPC extends Starship {
	
	ArrayList<Space_Object> targets = new ArrayList<Space_Object>();
	ArrayList<Behavior> behaviors;
	
	public Starship_NPC() {
		behaviors = new ArrayList<Behavior>();
		behaviors.add(new Behavior(this));
		behaviors.add(0, new Behavior_Attack(this));
	}
	
	public void draw(Graphics g)
	{
		drawStarship(g);
		
		Space_Object target = getTargetPrimary();
		/*
		if(exists(target))
		{
			Point2D.Double pos = getPos();
			Point2D.Double pos_target = target.getPos();
			Point2D.Double pos_solution = polarOffset(getPos(), aim, getDistanceBetween(target));
			drawArrow(g, pos, pos_solution);
			drawArrow(g, pos, pos_target);
			drawArrow(g, pos_target, pos_solution);
		}
		*/
			
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
		} else {
			behaviors.get(0).update();
			if(behaviors.get(0).getActive()) {
				behaviors.get(0).updateActions();
			} else {
				removeBehavior(behaviors.get(0));
			}
		}
		//thrust();
	}
	public void addBehavior(Behavior b) {
		behaviors.add(b);
	}
	public void addBehavior(int index, Behavior b) {
		behaviors.add(index, b);
	}
	public void removeBehavior(Behavior b) {
		behaviors.remove(b);
	}
	public void removeDeadTargets()
	{
		Iterator<Space_Object> o_i = targets.iterator();
		while(o_i.hasNext())
		{
			Space_Object o = o_i.next();
			if(!o.getActive())
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
				return Behavior.ACT_TURN_CW;
			}
			else if(faceAngleDiffCCW < faceAngleDiffCW)
			{
				printToWorld("Status (Facing): CCW");
				return Behavior.ACT_TURN_CCW;
			}
			else
			{
				printToWorld("Status (Facing): Random");
				if(Math.random() > .5) {
					return Behavior.ACT_TURN_CW;
				} else {
					return Behavior.ACT_TURN_CCW;
				}
			}
		}
		else
		{
			printToWorld("Status (Facing): Aligned");
			return Behavior.ACT_NOTHING;
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
		case	Behavior.ACT_TURN_CCW:	turnCCW();			break;
		case	Behavior.ACT_TURN_CW:	turnCW();			break;
		}
	}
	public Space_Object getTargetPrimary()
	{
		return targets.size() > 0 ? targets.get(0) : null;
	}
	public double getMaxAngleDifference()
	{
		return 1;
	}
	public double getMinSeparationFromAttackers()
	{
		return 300;
	}
	public double getMaxSeparationFromTarget()
	{
		//return 300;
		return 700;
	}
	public double getMinSeparationFromTarget()
	{
		//return 200;
		return 400;
	}
	public double getMinSeparationFromOthers()
	{
		return 100;
	}
}