package behavior.orders;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;


import behavior.Behavior_Starship;
import behavior.Behavior_Starship.AttackingState;
import behavior.Behavior_Starship.RotatingState;
import behavior.Behavior_Starship.StrafingState;
import behavior.Behavior_Starship.ThrustingState;
import game.GamePanel;
import helpers.SpaceHelper;
import space.SpaceObject;
import space.Starship;
import space.Starship_NPC;

public class Order_Escort extends Behavior_Starship{
	private SpaceObject target;
	private int escort_angle;
	private int escort_distance;
	
	Order_AttackDirect attackMode;
	
	public Order_Escort(Starship_NPC owner, SpaceObject target) {
		this(owner, target, (int) (Math.random() * 360), 200 + (int) (Math.random() * 300));
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
		super.update();
		if(tick % UPDATE_INTERVAL != 0) {
			return;
		}
		Starship_NPC owner = getOwner();
		if(!target.getActive()) {
			//printToWorld("Target dead");
			setActive(false);
			return;
		}
		if(attackMode.getActive() && owner.getDistanceBetween(attackMode.getTarget()) < getMaxDefendRange()) {
			//printToWorld("Attack Mode active");
			attackMode.update();
			copyActions(attackMode);
			return;
		}
		//If there is an enemy nearby, go into attack mode
		ArrayList<SpaceObject> nearbyEnemies = getNearbyEnemies();
		if(nearbyEnemies.size() > 0) {
			//printToWorld("Attacking nearby enemies");
			
			//Find closest enemy
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
			copyActions(attackMode);
			return;
		}
		updateEscort();
	}
	public ArrayList<SpaceObject> getNearbyEnemies() {
		Starship_NPC owner = getOwner();
		ArrayList<SpaceObject> result = new ArrayList<SpaceObject>();
		int range = getMaxDefendRange();
		for(Starship s : GamePanel.getWorld().getStarships()) {
			if(s.targetIsEnemy(owner) && s != target && owner.getDistanceBetween(s) < getMaxDefendRange()) {
				result.add(s);
			}
		}
		return result;
	}
	//The maximum range from the target at which the owner is willing to attack an enemy
	public int getMaxDefendRange() {
		return 10000;
	}
	public void updateEscort() {
		ThrustingState action_thrusting = ThrustingState.NONE;
		RotatingState action_rotation = RotatingState.NONE;
		StrafingState action_strafing = StrafingState.NONE;
		AttackingState action_weapon = AttackingState.NONE;
		
		Starship_NPC owner = getOwner();
		//double speedDiff = target.getVelSpeed() * SpaceHelper.cosDegrees(SpaceHelper.getAngleDiff(owner.getVelAngle(), target.getVelAngle()));
		//Point2D.Double pos_owner = SpaceHelper.calcFuturePos(owner.getPos(), owner.getVel(), speedDiff);
		Point2D.Double pos_owner = owner.getPos();
		Point2D.Double pos_destination = getNearestPosClone(pos_owner, target.polarOffset(target.getPosR() + escort_angle, escort_distance));
		//double angle_to_destination = getAngleTowardsPos(destination_x_focus, destination_y_focus);
		//double distance_to_destination = destination_distance_focus;
		
		Point2D.Double velDiff = SpaceHelper.calcDiff(owner.getVel(), target.getVel());
		//printToWorld("Owner thrust: " + owner.getThrust());
		
		double angle_current = owner.getAngleTowards(target);
		double distance_to_destination = SpaceHelper.getDistanceBetweenPos(pos_destination, pos_owner);
		//Move towards the escort position
		if(distance_to_destination > 20) { // || Starship_NPC.calcAngleDiff(angle_to_destination, escort_angle) < 10
			double angle_to_destination = SpaceHelper.calcFireAngle(
					SpaceHelper.calcDiff(pos_owner, pos_destination),
					velDiff,
					/*10 - velDiff.distance(0, 0)*/
					owner.getAcceleration(20 * owner.getDecel() * owner.getThrust()) * Math.max(5, (GamePanel.LIGHT_SPEED - owner.getVelSpeed()))
					);
			
			//printToWorld("Angle to Escort Position: " + angle_to_destination);
			//printToWorld("Distance to Escort Position: " + distance_to_destination);
			
			//printToWorld("Approaching Escort Position");
			double faceAngleDiff = owner.calcFutureFacingDifference(angle_to_destination);
			
			//printToWorld("Facing Towards Position");
			action_rotation = owner.calcTurnDirection(angle_to_destination);
			
			if(faceAngleDiff < 15) {
				//printToWorld("Moving Towards Position");
				action_thrusting = ThrustingState.THRUST;
			}
		} else {
			//printToWorld("Adjusting Velocity");
			//We are in escort position, so adjust our velocity to match
			double velAngle_owner = owner.getVelAngle();
			double velAngle_target = target.getVelAngle();
			double velSpeed_owner = owner.getVelSpeed();
			double velSpeed_target = target.getVelSpeed();
			double thrustAngle = SpaceHelper.arctanDegrees(velDiff.getY(), velDiff.getX());
			action_rotation = owner.calcTurnDirection(thrustAngle);
			/*
			if(SpaceHelper.getAngleDiff(velAngle_owner, velAngle_target) > 10) {
				action_thrusting = ThrustingState.BRAKE;
				printToWorld("Braking");
			}
			*/
			if(Math.abs(velSpeed_owner - velSpeed_target) > 0) {
				//printToWorld("Adjusting Velocity Speed");
				if(velSpeed_owner < velSpeed_target) {
					//printToWorld("Increasing Velocity");
					action_thrusting = ThrustingState.THRUST;
				} else if(velSpeed_owner > velSpeed_target) {
					//printToWorld("Decreasing Velocity");
					action_thrusting = ThrustingState.BRAKE;
				}
			} else {
				action_rotation = owner.calcTurnDirection(target.getPosR());
			}
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
		GamePanel.getWorld().drawToScreen((Graphics g) -> {
			g.setColor(Color.WHITE);
			g.drawOval((int) pos_owner.getX(), (int) pos_owner.getY(), 5, 5);
		});
		GamePanel.getWorld().drawToScreen((Graphics g) -> {
			g.setColor(Color.WHITE);
			g.drawOval((int) pos_destination.getX(), (int) pos_destination.getY(), 5, 5);
		});
	}
}
