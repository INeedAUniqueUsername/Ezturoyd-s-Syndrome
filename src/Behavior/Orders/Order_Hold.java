package Behavior.Orders;
import java.awt.geom.Point2D;

import Behavior.Behavior_Starship;
import Behavior.Behavior_Starship.AttackingState;
import Behavior.Behavior_Starship.RotatingState;
import Behavior.Behavior_Starship.StrafingState;
import Behavior.Behavior_Starship.ThrustingState;
import Space.SpaceObject;
import Space.Starship_NPC;
public class Order_Hold extends Behavior_Starship {
	public Order_Hold(Starship_NPC o) {
		super(o);
	}
	public void update() {
		Starship_NPC owner = getOwner();
		
		//To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the screen.
		SpaceObject target = owner.getClosestEnemyStarship();
		if(target == null) {
			return;
		}
		if(!owner.hasWeapon()) {
			return;
		}
		//Problems: None. Attack as normal
		Point2D.Double pos_target = getNearestTargetClone(owner, target);
		double target_x = pos_target.getX();
		double target_y = pos_target.getY();
		double target_distance = owner.getDistanceBetweenPos(pos_target);
		
		ThrustingState action_thrusting = ThrustingState.BRAKE;
		RotatingState action_rotation = RotatingState.NONE;
		StrafingState action_strafing = StrafingState.NONE;
		AttackingState action_weapon = AttackingState.NONE;
		//double angle_to_target = getAngleTowardsPos(target_x_focus, target_y_focus);
		
		double angle_to_target = owner.calcFireAngle(
				target_x,
				target_y,
				target.getVelX(),
				target.getVelY(),
				owner.getWeaponPrimary().getProjectileSpeed()
				);
		double faceAngleDiff = owner.calcFutureFacingDifference(angle_to_target);
		
		if(faceAngleDiff > owner.getController().getMaxAngleDifference())
		{
			action_rotation = owner.calcTurnDirection(angle_to_target);
		}
		else
		{
			double distance_to_target = target_distance;
			if(distance_to_target < owner.getWeaponPrimary().getProjectileRange()) {
				action_weapon = AttackingState.FIRE;
			}
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
