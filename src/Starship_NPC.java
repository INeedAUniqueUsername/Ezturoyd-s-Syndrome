import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;

public class Starship_NPC extends Starship {
	private BehaviorController controller;
	private ArrayList<Behavior> behaviors;
	
	public Starship_NPC() {
		initializeAI();
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.RED);
		drawStarship(g);
	}
	
	public void update()
	{
		updateSpaceship();
		controller.update();
		controller.updateActions();
		//thrust();
	}
	public final Behavior getBehavior(int b) {
		return behaviors.get(b);
	}
	public final void setBehavior(int i, Behavior b) {
		behaviors.set(i, b);
	}
	public final void addBehavior(Behavior b) {
		behaviors.add(b);
	}
	public final void removeBehavior(Behavior b) {
		behaviors.remove(b);
	}
	public final ArrayList<Behavior> getBehaviors() {
		return behaviors;
	}
	public final void setController(BehaviorController c) {
		controller = c;
	}
	public final Behavior getController() {
		return controller;
	}
	private final void initializeAI() {
		controller = new BehaviorController(this);
		behaviors = new ArrayList<Behavior>();
	}
	public final double getVelTowards(SpaceObject object)
	{
		double angle_towards_object = getAngleTowards(object);
		return object.getVelAtAngle(angle_towards_object) - getVelAtAngle(angle_towards_object);
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
	public final double calcFutureAngle()
	{
		double r_decel_time = Math.abs(vel_r/ROTATION_DECEL);
		//double angle_to_target_future = angle_to_target + target.getVelR() * r_decel_time;
		//Let's relearn AP Physics I!
		return    pos_r
				+ vel_r * r_decel_time
				+ ((vel_r > 0) ? -1 : 1) * (1/2) * ROTATION_DECEL * Math.pow(r_decel_time, 2)
				;	//Make sure that the deceleration value has the opposite sign of the rotation speed
	}
	public final Behavior.RotatingState calcTurnDirection(double target_angle)
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
				return Behavior.RotatingState.CW;
			}
			else if(faceAngleDiffCCW < faceAngleDiffCW)
			{
				printToWorld("Status (Facing): CCW");
				return Behavior.RotatingState.CCW;
			}
			else
			{
				printToWorld("Status (Facing): Random");
				if(Math.random() > .5) {
					return Behavior.RotatingState.CW;
				} else {
					return Behavior.RotatingState.CCW;
				}
			}
		}
		else
		{
			printToWorld("Status (Facing): Aligned");
			return Behavior.RotatingState.NOTHING;
		}
	}
	public final double calcFutureAngleDifference(double angle_target)
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
	public final void turnDirection(Behavior.RotatingState direction)
	{
		switch(direction)
		{
		case	CCW:	turnCCW();			break;
		case	CW:	turnCW();			break;
		}
	}
	public final double getMaxAngleDifference()
	{
		return 1;
	}
	public final double getMinSeparationFromAttackers()
	{
		return 300;
	}
	public final double getMaxSeparationFromTarget()
	{
		//return 300;
		return 700;
	}
	public final double getMinSeparationFromTarget()
	{
		//return 200;
		return 400;
	}
	public final double getMinSeparationFromOthers()
	{
		return 100;
	}
}
