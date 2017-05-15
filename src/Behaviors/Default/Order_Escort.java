package Behaviors.Default;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import Behaviors.Behavior_Starship;
import Behaviors.Behavior_Starship.AttackingState;
import Behaviors.Behavior_Starship.RotatingState;
import Behaviors.Behavior_Starship.StrafingState;
import Behaviors.Behavior_Starship.ThrustingState;
import Game.GamePanel;
import Space.Helper;
import Space.SpaceObject;
import Space.Starship;
import Space.Starship_NPC;

class Order_Escort extends Behavior_Starship{
	private SpaceObject target;
	private int escort_angle = 180;
	private int escort_distance = 10;
	
	Order_AttackDirect attackMode;
	
	public Order_Escort(Starship_NPC owner, SpaceObject target) {
		this(owner, target, 180, 100);
		// TODO Auto-generated constructor stub
	}
	public Order_Escort(Starship_NPC owner, SpaceObject target, int angle, int distance) {
		super(owner);
		setOwner(owner);
		setParameters(target, angle, distance);
		
		attackMode = new Order_AttackDirect(owner, null);
		attackMode.setActive(false);
	}
	public void setParameters(SpaceObject target, int angle, int distance) {
		this.target = target;
		escort_angle = angle;
		escort_distance = distance;
	}
	public void update() {
		Starship_NPC owner = getOwner();
		if(!target.getActive()) {
			setActive(false);
			return;
		}
		if(attackMode.getActive()) {
			attackMode.update();
			return;
		}
		//If there is an enemy nearby, go into attack mode
		ArrayList<SpaceObject> nearbyEnemies = getNearbyEnemies();
		if(nearbyEnemies.size() > 0) {
			SpaceObject closest = null;
			double closestDistance = Integer.MAX_VALUE;
			for(SpaceObject o : nearbyEnemies) {
				double distance = owner.getDistanceBetween(o);
				if(distance < closestDistance) {
					closest = o;
					closestDistance = distance;
				}
			}
			attackMode = new Order_AttackDirect(owner, closest);
			attackMode.update();
			return;
		}
		updateEscort();
	}
	public ArrayList<SpaceObject> getNearbyEnemies() {
		Starship_NPC owner = getOwner();
		ArrayList<SpaceObject> result = new ArrayList<SpaceObject>();
		int range = getMaxDefendRange();
		for(Starship s : GamePanel.getWorld().getStarships()) {
			if(s.targetIsEnemy(owner) && target.getDistanceBetween(s) < getMaxDefendRange()) {
				result.add(s);
			}
		}
		return result;
	}
	//The maximum range from the target at which the owner is willing to attack an enemy
	public int getMaxDefendRange() {
		return 500;
	}
	public void updateEscort() {
		Starship_NPC owner = getOwner();
		Point2D.Double pos_owner = owner.getFuturePosWithDeceleration();
		Point2D.Double pos_destination = Behavior_Starship.getNearestPosClone(pos_owner, Helper.polarOffset(target.getPos(), target.getPosR() + escort_angle, escort_distance));
		ThrustingState action_thrusting = ThrustingState.NONE;
		RotatingState action_rotation = RotatingState.NONE;
		StrafingState action_strafing = StrafingState.NONE;
		AttackingState action_weapon = AttackingState.NONE;
		//double angle_to_destination = getAngleTowardsPos(destination_x_focus, destination_y_focus);
		//double distance_to_destination = destination_distance_focus;
		
		double angle_to_destination = Helper.calcFireAngle(
				Helper.calcDiff(owner.getPos(), pos_destination),
				Helper.calcDiff(owner.getVel(), target.getVel()),
				owner.getMax_speed()
				);
		double angle_current = owner.getAngleTowards(target);
		double distance_to_destination = Helper.getDistanceBetweenPos(pos_destination, pos_owner);
		owner.printToWorld("Angle to Escort Position: " + angle_to_destination);
		owner.printToWorld("Distance to Escort Position: " + distance_to_destination);
		//Move towards the escort position
		if(Math.abs(distance_to_destination - escort_distance) > 10 || Starship_NPC.calcAngleDiff(angle_to_destination, escort_angle) < 10) {
			owner.printToWorld("Approaching Escort Position");
			double faceAngleDiff = owner.calcFutureFacingDifference(angle_to_destination);
			if(faceAngleDiff > 6)
			{
				owner.printToWorld("Facing Towards Position");
				action_rotation = owner.calcTurnDirection(angle_to_destination);
			} else {
				owner.printToWorld("Moving Towards Position");
				action_thrusting = ThrustingState.THRUST;
			}
		} else {
			owner.printToWorld("Adjusting Velocity");
			//We are in escort position, so adjust our velocity to match
			//double velAngle_owner = owner.getVelAngle();
			double velAngle_target = target.getVelAngle();
			double velSpeed_owner = owner.getVelSpeed();
			double velSpeed_target = target.getVelSpeed();
			action_rotation = owner.calcTurnDirection(velAngle_target);
			if(Math.abs(velSpeed_owner - velSpeed_target) > 0) {
				owner.printToWorld("Adjusting Velocity Speed");
				if(velSpeed_owner < velSpeed_target - 5) {
					owner.printToWorld("Increasing Velocity");
					action_thrusting = ThrustingState.THRUST;
				} else if(velSpeed_owner > velSpeed_target) {
					owner.printToWorld("Decreasing Velocity");
					action_thrusting = ThrustingState.BRAKE;
				}
			}
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}