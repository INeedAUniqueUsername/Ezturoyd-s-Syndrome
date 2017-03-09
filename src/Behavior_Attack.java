import java.awt.geom.Point2D;

public class Behavior_Attack extends Behavior {
	private SpaceObject target;
	public Behavior_Attack(Starship_NPC o, SpaceObject t) {
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
		//To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the screen.
		if(!target.getActive()) {
			System.out.println("Attack order done");
			setActive(false);
			return;
		}
		double pos_x = owner.getPosX();
		double pos_y = owner.getPosY();
		double target_x_center = target.getPosX();
		double target_y_center = target.getPosY();
		double target_distance_center = owner.getDistanceBetweenPos(pos_x, pos_y, target_x_center, target_y_center);
		
		double target_x_up = target_x_center;
		double target_y_up = target_y_center - GameWindow.HEIGHT;
		double target_distance_up = owner.getDistanceBetweenPos(pos_x, pos_y, target_x_up, target_y_up);
		
		double target_x_down = target_x_center;
		double target_y_down = target_y_center + GameWindow.HEIGHT;
		double target_distance_down = owner.getDistanceBetweenPos(pos_x, pos_y, target_x_down, target_y_down);
		
		double target_x_right = target_x_center + GameWindow.WIDTH;
		double target_y_right = target_y_center;
		double target_distance_right = owner.getDistanceBetweenPos(pos_x, pos_y, target_x_right, target_y_right);
		
		double target_x_left = target_x_center - GameWindow.WIDTH;
		double target_y_left = target_y_center;
		double target_distance_left = owner.getDistanceBetweenPos(pos_x, pos_y, target_x_left, target_y_left);
		
		double target_x_focus = target_x_center;
		double target_y_focus = target_y_center;
		double target_distance_focus = target_distance_center;
		
		if(target_distance_focus > target_distance_up)
		{
			target_x_focus = target_x_up;
			target_y_focus = target_y_up;
			target_distance_focus = target_distance_up;
		}
		if(target_distance_focus > target_distance_down)
		{
			target_x_focus = target_x_down;
			target_y_focus = target_y_down;
			target_distance_focus = target_distance_down;
		}
		if(target_distance_focus > target_distance_right)
		{
			target_x_focus = target_x_right;
			target_y_focus = target_y_right;
			target_distance_focus = target_distance_right;
		}
		if(target_distance_focus > target_distance_left)
		{
			target_x_focus = target_x_left;
			target_y_focus = target_y_left;
			target_distance_focus = target_distance_left;
		}
		
		ThrustingState action_thrusting = ThrustingState.NOTHING;
		RotatingState action_rotation = RotatingState.NOTHING;
		StrafingState action_strafing = StrafingState.NOTHING;
		AttackingState action_weapon = AttackingState.NOTHING;
		//double angle_to_target = getAngleTowardsPos(target_x_focus, target_y_focus);
		double distance_to_target = target_distance_focus;
		
		double angle_to_target = owner.calcFireAngle(
				target_x_focus,
				target_y_focus,
				target.getVelX(),
				target.getVelY(),
				owner.getWeaponPrimary().getProjectileSpeed()
				);
		double faceAngleDiff = owner.calcFutureAngleDifference(angle_to_target);
		
		double velAngle = owner.getVelAngle();
		double velAngleDiffCCW = SpaceObject.modRangeDegrees(angle_to_target - velAngle);
		double velAngleDiffCW = SpaceObject.modRangeDegrees(velAngle - angle_to_target);
		
		double velAngleDiff = SpaceObject.min(velAngleDiffCCW, velAngleDiffCW);
		
		//double velDiff = owner.getVelRadial(angle_to_target) - target.getVelRadial(angle_to_target);
		
		if(faceAngleDiff > owner.getMaxAngleDifference())
		{
			action_rotation = owner.calcTurnDirection(angle_to_target);
		}
		else
		{
			owner.printToWorld("Status (Facing): Aligned");
			action_weapon = AttackingState.FIRE;
		}
		
		if(velAngleDiff > 120)
		{
			action_thrusting = ThrustingState.BRAKE;
			owner.printToWorld("Status: Brake");
		}
		else if(velAngleDiff > 60)
		{
			owner.printToWorld("Status: Nothing");
		}
		else
		{
			action_thrusting = ThrustingState.THRUST;
			owner.printToWorld("Status: Thrust");
		}
		if(distance_to_target > owner.getWeaponPrimary().getProjectileRange()) //owner.getMaxSeparationFromTarget()
		{
			//Move towards target
			action_thrusting = ThrustingState.THRUST;
			
			owner.printToWorld("Status (Distance): Far");
		} else if(distance_to_target < owner.getMinSeparationFromTarget()) {
			//Move away from target
			action_rotation = owner.calcTurnDirection(owner.getAngleFrom(target));
			if(faceAngleDiff > 90)
			{
				action_thrusting = ThrustingState.THRUST;
			}
		} else {
			action_thrusting = ThrustingState.BRAKE;
			owner.printToWorld("Status (Distance): Close");
		}
		owner.printToWorld("Angle to Target: " + angle_to_target);
		owner.printToWorld("Max Facing Angle Difference: " + owner.getMaxAngleDifference());
		owner.printToWorld("Velocity Angle: " + velAngle);
		owner.printToWorld("Velocity Angle Difference CCW: " + velAngleDiffCCW);
		owner.printToWorld("Velocity Angle Difference CW: " + velAngleDiffCW);
		owner.printToWorld("Velocity Angle Difference: " + velAngleDiff);
		owner.printToWorld("Weapons: " + action_weapon);
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
