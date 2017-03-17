import java.awt.geom.Point2D;

public class Order_GoToPos extends Behavior {
	private Point2D.Double destination;
	private int min_distance = 50;
	public Order_GoToPos(Starship_NPC o) {
		super(o);
		setDestination(owner.getPos());
		// TODO Auto-generated constructor stub
	}
	public Order_GoToPos(Starship_NPC o, Point2D.Double dest) {
		super(o);
		setDestination(dest);
	}
	public void setDestination(Point2D.Double dest) {
		destination = dest;
	}
	public Point2D.Double getDestination() {
		return destination;
	}
	public void update() {
		//To allow the AI to take advantage of wraparound, we make four clones of the destination, one for each side of the screen.
		if(owner.getDistanceBetweenPos(destination) < min_distance) {
			System.out.println("GoToPos order done");
			setActive(false);
			return;
		}
		
		double[] targetStats = getNearestPosClone(owner.getPos(), destination);
		double destination_x = targetStats[0];
		double destination_y = targetStats[1];
		
		ThrustingState action_thrusting = ThrustingState.NOTHING;
		RotatingState action_rotation = RotatingState.NOTHING;
		StrafingState action_strafing = StrafingState.NOTHING;
		AttackingState action_weapon = AttackingState.NOTHING;
		//double angle_to_destination = getAngleTowardsPos(destination_x_focus, destination_y_focus);
		//double distance_to_destination = destination_distance_focus;
		
		double angle_to_target = SpaceObject.calcFireAngle(
				new Point2D.Double(
						destination_x - owner.getPosX(),
						destination_y - owner.getPosY()
						),
				new Point2D.Double(
						0 - owner.getVelX(),
						0 - owner.getVelY()
						),
				owner.MAX_SPEED
				);
		double faceAngleDiff = owner.calcFutureAngleDifference(angle_to_target);
		/*
		double velAngle = owner.getVelAngle();
		double velAngleDiffCCW = Space_Object.modRangeDegrees(angle_to_destination - velAngle);
		double velAngleDiffCW = Space_Object.modRangeDegrees(velAngle - angle_to_destination);
		
		double velAngleDiff = Space_Object.min(velAngleDiffCCW, velAngleDiffCW);
		*/
		
		//double velDiff = owner.getVelRadial(angle_to_destination) - destination.getVelRadial(angle_to_destination);
		
		if(faceAngleDiff > 1)
		{
			action_rotation = owner.calcTurnDirection(angle_to_target);
		} else {
			action_weapon = AttackingState.FIRE;
			if(SpaceObject.getDistanceBetweenPos(owner.getFuturePosWithDeceleration(), destination) > min_distance){
				action_thrusting = ThrustingState.THRUST;
			}
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}