import java.awt.geom.Point2D;
public class Order_Hold extends Behavior {
	public Order_Hold(Starship_NPC o) {
		super(o);
	}
	public SpaceObject getClosestEnemy() {
		double distance = Integer.MAX_VALUE;
		SpaceObject result = null;
		for(SpaceObject o : GamePanel.world.getStarships()) {
			double d = owner.getDistanceBetween(o);
			if(!o.equals(owner) && d < distance) {
				result = o;
				distance = d;
			}
		}
		return result;
	}
	public void update() {
		//To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the screen.
		SpaceObject target = getClosestEnemy();
		if(target == null) {
			return;
		}
		//Problems: None. Attack as normal
		double[] targetStats = getNearestTargetClone(owner, target);
		double target_x = targetStats[0];
		double target_y = targetStats[1];
		double target_distance = targetStats[2];
		
		ThrustingState action_thrusting = ThrustingState.NOTHING;
		RotatingState action_rotation = RotatingState.NOTHING;
		StrafingState action_strafing = StrafingState.NOTHING;
		AttackingState action_weapon = AttackingState.NOTHING;
		//double angle_to_target = getAngleTowardsPos(target_x_focus, target_y_focus);
		
		double angle_to_target = owner.calcFireAngle(
				target_x,
				target_y,
				target.getVelX(),
				target.getVelY(),
				owner.getWeaponPrimary().getProjectileSpeed()
				);
		double faceAngleDiff = owner.calcFutureAngleDifference(angle_to_target);
		
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
