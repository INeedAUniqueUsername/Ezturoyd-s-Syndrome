import java.awt.geom.Point2D;

public class Behavior_GoToPos extends Behavior {
	Point2D.Double destination;
	int min_distance = 10;
	public Behavior_GoToPos(Starship_NPC o) {
		super(o);
		// TODO Auto-generated constructor stub
	}
	public void update() {
		//To allow the AI to take advantage of wraparound, we make four clones of the destination, one for each side of the screen.
		if(owner.getDistanceBetweenPos(destination) < min_distance) {
			System.out.println("Attack order done");
			setActive(false);
			return;
		}
		double pos_x = owner.getPosX();
		double pos_y = owner.getPosY();
		double destination_x_center = destination.getX();
		double destinaiton_y_center = destination.getY();
		double destination_distance_center = owner.getDistanceBetweenPos(pos_x, pos_y, destination_x_center, destinaiton_y_center);
		
		double destination_x_up = destination_x_center;
		double destination_y_up = destinaiton_y_center - GameWindow.HEIGHT;
		double destination_distance_up = owner.getDistanceBetweenPos(pos_x, pos_y, destination_x_up, destination_y_up);
		
		double destination_x_down = destination_x_center;
		double destination_y_down = destinaiton_y_center + GameWindow.HEIGHT;
		double destination_distance_down = owner.getDistanceBetweenPos(pos_x, pos_y, destination_x_down, destination_y_down);
		
		double destination_x_right = destination_x_center + GameWindow.WIDTH;
		double destination_y_right = destinaiton_y_center;
		double destination_distance_right = owner.getDistanceBetweenPos(pos_x, pos_y, destination_x_right, destination_y_right);
		
		double destination_x_left = destination_x_center - GameWindow.WIDTH;
		double destination_y_left = destinaiton_y_center;
		double destination_distance_left = owner.getDistanceBetweenPos(pos_x, pos_y, destination_x_left, destination_y_left);
		
		double destination_x_focus = destination_x_center;
		double destination_y_focus = destinaiton_y_center;
		double destination_distance_focus = destination_distance_center;
		
		if(destination_distance_focus > destination_distance_up)
		{
			destination_x_focus = destination_x_up;
			destination_y_focus = destination_y_up;
			destination_distance_focus = destination_distance_up;
		}
		if(destination_distance_focus > destination_distance_down)
		{
			destination_x_focus = destination_x_down;
			destination_y_focus = destination_y_down;
			destination_distance_focus = destination_distance_down;
		}
		if(destination_distance_focus > destination_distance_right)
		{
			destination_x_focus = destination_x_right;
			destination_y_focus = destination_y_right;
			destination_distance_focus = destination_distance_right;
		}
		if(destination_distance_focus > destination_distance_left)
		{
			destination_x_focus = destination_x_left;
			destination_y_focus = destination_y_left;
			destination_distance_focus = destination_distance_left;
		}
		
		String action_thrusting = ACT_NOTHING;
		String action_rotation = ACT_NOTHING;
		String action_strafing = ACT_NOTHING;
		String action_weapon = ACT_NOTHING;
		//double angle_to_destination = getAngleTowardsPos(destination_x_focus, destination_y_focus);
		//double distance_to_destination = destination_distance_focus;
		
		double angle_to_destination = SpaceObject.calcFireAngle(
				new Point2D.Double(
						destination_x_focus - owner.getPosX(),
						destination_y_focus - owner.getPosY()
						),
				new Point2D.Double(
						0 - owner.getVelX(),
						0 - owner.getVelY()
						),
				owner.MAX_SPEED
				);
		double faceAngleDiff = owner.calcFutureAngleDifference(angle_to_destination);
		/*
		double velAngle = owner.getVelAngle();
		double velAngleDiffCCW = Space_Object.modRangeDegrees(angle_to_destination - velAngle);
		double velAngleDiffCW = Space_Object.modRangeDegrees(velAngle - angle_to_destination);
		
		double velAngleDiff = Space_Object.min(velAngleDiffCCW, velAngleDiffCW);
		*/
		
		//double velDiff = owner.getVelRadial(angle_to_destination) - destination.getVelRadial(angle_to_destination);
		
		if(faceAngleDiff > 6)
		{
			action_rotation = owner.calcTurnDirection(angle_to_destination);
		} else {
			action_thrusting = ACT_THRUST;
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
