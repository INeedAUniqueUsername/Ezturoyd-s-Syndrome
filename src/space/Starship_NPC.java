package space;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;

import behavior.Behavior_Starship;
import behavior.controllers.BehaviorController_Default;
import behavior.orders.Order_AttackDirect;
import behavior.orders.Order_AttackOrbit;
import behavior.orders.Order_Escort;
import helpers.SpaceHelper;

public class Starship_NPC extends Starship {
	private BehaviorController_Default controller;
	
	public Starship_NPC() {
		initializeAI();
		setAlignment(Sovereign.ENEMY);
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.RED);
		super.draw(g);
	}
	
	public void update()
	{
		if(getActive()) {
			super.update();
			controller.update();
			controller.updateActions();
		}
	}
	public final void setController(BehaviorController_Default c) {
		controller = c;
	}
	public final BehaviorController_Default getController() {
		return controller;
	}
	private final void initializeAI() {
		controller = new BehaviorController_Default(this);
		controller.initialize();
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
	public final Behavior_Starship.RotatingState calcTurnDirection(double target_angle)
	{
		double pos_r_future = getFutureAngleWithDeceleration();
		double faceAngleDiffCCW = SpaceHelper.modRangeDegrees(target_angle - pos_r_future);
		double faceAngleDiffCW = SpaceHelper.modRangeDegrees(pos_r_future - target_angle);
		double faceAngleDiff = SpaceHelper.min(faceAngleDiffCCW, faceAngleDiffCW);
		if(faceAngleDiff > controller.getMaxAngleDifference())
		{
			if(faceAngleDiffCW < faceAngleDiffCCW)
			{
				//printToWorld("Status (Facing): CW");
				return Behavior_Starship.RotatingState.CW;
			}
			else if(faceAngleDiffCCW < faceAngleDiffCW)
			{
				//printToWorld("Status (Facing): CCW");
				return Behavior_Starship.RotatingState.CCW;
			}
			else
			{
				//printToWorld("Status (Facing): Random");
				if(Math.random() > .5) {
					return Behavior_Starship.RotatingState.CW;
				} else {
					return Behavior_Starship.RotatingState.CCW;
				}
			}
		}
		else
		{
			//printToWorld("Status (Facing): Aligned");
			return Behavior_Starship.RotatingState.NONE;
		}
	}
}
