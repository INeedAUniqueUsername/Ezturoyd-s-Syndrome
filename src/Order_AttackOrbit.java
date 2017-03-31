public class Order_AttackOrbit extends Behavior {
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
		
		owner.printToWorld("AttackOrbit active");
		//Problem: Target is dead
		if(!target.getActive()) {
			setActive(false);
			return; //Done
		}
		
		double[] targetStats = getNearestTargetClone(owner, target);
		double target_x = targetStats[0];
		double target_y = targetStats[1];
		
		ThrustingState action_thrusting = ThrustingState.NOTHING;
		RotatingState action_rotation = RotatingState.NOTHING;
		StrafingState action_strafing = StrafingState.NOTHING;
		AttackingState action_weapon = AttackingState.NOTHING;
		
		double angle_to_destination = owner.calcFireAngle(target_x, target_y, target.getVelX(), target.getVelY(), owner.MAX_SPEED);
		double angle_to_aim = owner.calcFireAngle(target_x, target_y, target.getVelX(), target.getVelY(), owner.getWeaponPrimary().getProjectileSpeed());
		double futureFacingDiff = owner.calcFutureFacingDifference(angle_to_destination);
		double aimingDiff = owner.calcFacingDifference(angle_to_aim);
			
		//We are too far away on the target, so focus on orbiting
		if(SpaceObject.getDistanceBetweenPos(owner.getPos(), target.getPos()) > distance){
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
