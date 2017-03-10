import java.awt.geom.Point2D;

public class Order_Escort extends Behavior{
	private SpaceObject target;
	
	public Order_Escort(Starship_NPC o) {
		super(o);
		// TODO Auto-generated constructor stub
	}
	public void update() {
		//To allow the AI to take advantage of wraparound, we make four clones of the destination, one for each side of the screen.
		if(!target.getActive()) {
			setActive(false);
			return;
		}
		
		double[] targetStats = getNearestTargetClone(owner, target);
		double target_x = targetStats[0];
		double target_y = targetStats[1];
		double target_distance = targetStats[2];
		
		ThrustingState action_thrusting = ThrustingState.NOTHING;
		RotatingState action_rotation = RotatingState.NOTHING;
		StrafingState action_strafing = StrafingState.NOTHING;
		AttackingState action_weapon = AttackingState.NOTHING;
		//double angle_to_destination = getAngleTowardsPos(destination_x_focus, destination_y_focus);
		//double distance_to_destination = destination_distance_focus;
		
		double angle_to_destination = SpaceObject.calcFireAngle(
				new Point2D.Double(
						target_x - owner.getPosX(),
						target_y - owner.getPosY()
						),
				new Point2D.Double(
						target.getVelX() - owner.getVelX(),
						target.getVelY() - owner.getVelY()
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
			action_thrusting = ThrustingState.THRUST;
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
