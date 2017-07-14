package behavior.orders;
import java.awt.geom.Point2D;

import behavior.Behavior_Starship;
import behavior.Behavior_Starship.AttackingState;
import behavior.Behavior_Starship.RotatingState;
import behavior.Behavior_Starship.StrafingState;
import behavior.Behavior_Starship.ThrustingState;
import helpers.SpaceHelper;
import interfaces.NewtonianMotion;
import space.SpaceObject;
import space.Starship_NPC;

public class Order_AttackOrbit extends Behavior_Starship {
	private SpaceObject target;
	private int distance = 300;
	public Order_AttackOrbit(Starship_NPC o, SpaceObject t) {
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
		updateAttackOrbit();
	}
	public void updateAttackOrbit() {
		Starship_NPC owner = getOwner();
		printToWorld("AttackOrbit active");
		//Problem: Target is dead
		if(!target.getActive()) {
			printToWorld("Target dead");
			setActive(false);
			return; //Done
		}
		if(!owner.hasWeapon()) {
			printToWorld("No weapon");
			setActive(false);
			return;
		}
		Point2D.Double pos_target = getNearestTargetClone(owner, target);
		double target_x = pos_target.getX();
		double target_y = pos_target.getY();
		
		ThrustingState action_thrusting = ThrustingState.NONE;
		RotatingState action_rotation = RotatingState.NONE;
		StrafingState action_strafing = StrafingState.NONE;
		AttackingState action_weapon = AttackingState.NONE;
		
		double angle_to_destination = owner.calcFireAngle(target_x, target_y, target.getVelX(), target.getVelY(), owner.getMax_speed());
		double angle_to_aim = owner.calcFireAngle(target_x, target_y, target.getVelX(), target.getVelY(), owner.getWeaponPrimary().getProjectileSpeed());
		double futureFacingDiff = owner.calcFutureFacingDifference(angle_to_destination);
		double aimingDiff = owner.calcFacingDifference(angle_to_aim);
			
		//We are too far away on the target, so focus on orbiting
		if(SpaceHelper.getDistanceBetweenPos(owner.getPos(), target.getPos()) > distance){
			if(futureFacingDiff > 30) {
				action_rotation = owner.calcTurnDirection(angle_to_destination);
				owner.printToWorld("AttackOrbit: Not facing " + target.getName());
			} else {
				owner.printToWorld("AttackOrbit: Facing " + target.getName());
			}
			action_thrusting = ThrustingState.THRUST;
			
		} else {
			action_rotation = owner.calcTurnDirection(angle_to_aim);
			owner.printToWorld("AttackOrbit: Aiming at " + target.getName());
		}
		if(aimingDiff < 5) {
			action_weapon = AttackingState.FIRE;
			owner.printToWorld("AttackOrbit: Firing at " + target.getName());
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
