package behavior.orders;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import behavior.Behavior_Starship;
import behavior.Behavior_Starship.AttackingState;
import behavior.Behavior_Starship.RotatingState;
import behavior.Behavior_Starship.StrafingState;
import behavior.Behavior_Starship.ThrustingState;
import game.GamePanel;
import helpers.SpaceHelper;
import interfaces.NewtonianMotion;
import space.SpaceObject;
import space.Starship_NPC;

public class Order_AttackDirect extends Behavior_Starship {
	private SpaceObject target;
	public Order_AttackDirect(Starship_NPC o, SpaceObject t) {
		super(o);
		setTarget(t);
	}
	
	public SpaceObject getTarget() {
		return target;
	}
	public void setTarget(SpaceObject t) {
		target = t;
	}
	public void update() {
		super.update();
		if(tick % UPDATE_INTERVAL != 0) {
			return;
		}
		Starship_NPC owner = getOwner();
		//Problem: Target is dead
		if(!target.getActive()) {
			//printToWorld("Attack order done");
			setActive(false);
			return; //Done
		}
		if(shouldAvoidNearby()) {
			//Problem: Objects are too close to us
			ArrayList<SpaceObject> objectsTooClose = getObjectsTooClose();
			int objectsTooCloseCount = objectsTooClose.size();
			if(objectsTooCloseCount > 0)
			{
				double angle_destination = 0;
				for(SpaceObject o : objectsTooClose)
				{
					angle_destination += owner.getAngleFrom(o);
				}
				angle_destination /= objectsTooCloseCount;
				owner.turnDirection(owner.calcTurnDirection(angle_destination));
				setThrusting(ThrustingState.THRUST);
				System.out.println("Destination Angle: " + angle_destination);
				return; //Done
			}
		}
		if(!owner.hasWeapon()) {
			setActive(false);
			return;
		}
		//Problems: None. Attack as normal
		updateAttack();
	}
	public boolean shouldAvoidNearby() {
		return false;
	}
	public ArrayList<SpaceObject> getObjectsTooClose() {
		Starship_NPC owner = getOwner();
		ArrayList<SpaceObject> result = new ArrayList<SpaceObject>();
		for(SpaceObject o : GamePanel.getWorld().getStarships())
		{
			if(!o.equals(owner))
			{
				if(owner.getDistanceBetween(o) < owner.getController().getMinSeparationFromOthers())
				{
					System.out.println(o.getName() + " is " + owner.getDistanceBetween(o) + " away (too close).");
					result.add(o);
				}
			} else {
				//System.out.println("It's you.");
			}
		}
		return result;
	}
	public void updateAttack() {
		Starship_NPC owner = getOwner();
		
		Point2D.Double pos_target = getNearestTargetClone(owner, target);
		double target_x = pos_target.getX();
		double target_y = pos_target.getY();
		double target_distance = owner.getDistanceBetweenPos(pos_target);
		
		ThrustingState action_thrusting = ThrustingState.NONE;
		RotatingState action_rotation = RotatingState.NONE;
		StrafingState action_strafing = StrafingState.NONE;
		AttackingState action_weapon = AttackingState.NONE;
		//double angle_to_target = getAngleTowardsPos(target_x_focus, target_y_focus);
		double distance_to_target = target_distance;
		
		double angle_to_target = owner.calcFireAngle(
				target_x,
				target_y,
				target.getVelX(),
				target.getVelY(),
				owner.getWeaponPrimary().getProjectileSpeed()
				);
		double faceAngleDiff = owner.calcFutureFacingDifference(angle_to_target);
		
		double velAngle = owner.getVelAngle();
		double velAngleDiffCCW = SpaceHelper.modRangeDegrees(angle_to_target - velAngle);
		double velAngleDiffCW = SpaceHelper.modRangeDegrees(velAngle - angle_to_target);
		
		double velAngleDiff = SpaceHelper.min(velAngleDiffCCW, velAngleDiffCW);
		
		//double velDiff = owner.getVelRadial(angle_to_target) - target.getVelRadial(angle_to_target);
		
		if(faceAngleDiff > owner.getController().getMaxAngleDifference())
		{
			action_rotation = owner.calcTurnDirection(angle_to_target);
		}
		else
		{
			////printToWorld("Status (Facing): Aligned");
			action_weapon = AttackingState.FIRE;
		}
		
		if(velAngleDiff > 120)
		{
			action_thrusting = ThrustingState.BRAKE;
			////printToWorld("Status: Brake");
		}
		else if(velAngleDiff > 60)
		{
			////printToWorld("Status: Nothing");
		}
		else
		{
			action_thrusting = ThrustingState.THRUST;
			////printToWorld("Status: Thrust");
		}
		if(distance_to_target > owner.getWeaponPrimary().getProjectileRange()) //owner.getMaxSeparationFromTarget()
		{
			//Move towards target
			action_thrusting = ThrustingState.THRUST;
			
			//printToWorld("Status (Distance): Far");
		} else if(distance_to_target < owner.getController().getMinSeparationFromTarget()) {
			//Move away from target
			action_rotation = owner.calcTurnDirection(owner.getAngleFrom(target));
			if(faceAngleDiff > 90)
			{
				action_thrusting = ThrustingState.THRUST;
			}
		} else {
			action_thrusting = ThrustingState.BRAKE;
			//printToWorld("Status (Distance): Close");
		}
		//printToWorld("Angle to Target: " + angle_to_target);
		//printToWorld("Max Facing Angle Difference: " + owner.getController().getMaxAngleDifference());
		//printToWorld("Velocity Angle: " + velAngle);
		//printToWorld("Velocity Angle Difference CCW: " + velAngleDiffCCW);
		//printToWorld("Velocity Angle Difference CW: " + velAngleDiffCW);
		//printToWorld("Velocity Angle Difference: " + velAngleDiff);
		//printToWorld("Weapons: " + action_weapon);
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
