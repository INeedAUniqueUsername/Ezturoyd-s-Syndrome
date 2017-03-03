import java.awt.geom.Point2D;

public class Behavior_Hold extends Behavior {
	public Behavior_Hold(Starship_NPC o) {
		super(o);
	}
	public SpaceObject getClosestEnemy() {
		double distance = Integer.MAX_VALUE;
		SpaceObject result = null;
		for(SpaceObject o : GamePanel.world.getStarships()) {
			double d = owner.getDistanceBetween(o);
			if(d < distance) {
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
		
		String action_thrusting = ACT_BRAKE;
		String action_rotation = ACT_NOTHING;
		String action_strafing = ACT_NOTHING;
		String action_weapon = ACT_NOTHING;
		//double angle_to_target = getAngleTowardsPos(target_x_focus, target_y_focus);
		
		double angle_to_target = owner.calcFireAngle(
				target_x_focus,
				target_y_focus,
				target.getVelX(),
				target.getVelY(),
				owner.getWeaponPrimary().getProjectileSpeed()
				);
		double faceAngleDiff = owner.calcFutureAngleDifference(angle_to_target);
		
		if(faceAngleDiff > owner.getMaxAngleDifference())
		{
			action_rotation = owner.calcTurnDirection(angle_to_target);
		}
		else
		{
			double distance_to_target = target_distance_focus;
			if(distance_to_target < owner.getWeaponPrimary().getProjectileRange()) {
				action_weapon = ACT_FIRE;
			}
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
