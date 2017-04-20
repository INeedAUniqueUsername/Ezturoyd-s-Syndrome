import java.awt.geom.Point2D;

public class Order_Escort extends Behavior{
	private SpaceObject target;
	private int escort_angle = 180;
	private int escort_distance = 10;
	
	public Order_Escort(Starship_NPC owner, SpaceObject target) {
		this(owner, target, 180, 100);
		// TODO Auto-generated constructor stub
	}
	public Order_Escort(Starship_NPC owner, SpaceObject target, int angle, int distance) {
		super(owner);
		setOwner(owner);
		setParameters(target, angle, distance);
	}
	public void setParameters(SpaceObject target, int angle, int distance) {
		this.target = target;
		escort_angle = angle;
		escort_distance = distance;
	}
	public void update() {
		//To allow the AI to take advantage of wraparound, we make four clones of the destination, one for each side of the screen.
		if(!target.getActive()) {
			setActive(false);
			return;
		}
		Point2D.Double pos_owner = owner.getFuturePosWithDeceleration();
		Point2D.Double pos_destination = Behavior.getNearestPosClone(pos_owner, SpaceObject.polarOffset(target.getPos(), target.getPosR() + escort_angle, escort_distance));
		ThrustingState action_thrusting = ThrustingState.NONE;
		RotatingState action_rotation = RotatingState.NONE;
		StrafingState action_strafing = StrafingState.NONE;
		AttackingState action_weapon = AttackingState.NONE;
		//double angle_to_destination = getAngleTowardsPos(destination_x_focus, destination_y_focus);
		//double distance_to_destination = destination_distance_focus;
		
		double angle_to_destination = SpaceObject.calcFireAngle(
				SpaceObject.calcDiff(owner.getPos(), pos_destination),
				SpaceObject.calcDiff(owner.getVel(), target.getVel()),
				owner.MAX_SPEED
				);
		double angle_current = owner.getAngleTowards(target);
		double distance_to_destination = SpaceObject.getDistanceBetweenPos(pos_destination, pos_owner);
		owner.printToWorld("Angle to Escort Position: " + angle_to_destination);
		owner.printToWorld("Distance to Escort Position: " + distance_to_destination);
		//Move towards the escort position
		if(Math.abs(distance_to_destination - escort_distance) > 10 || Starship_NPC.calcAngleDiff(angle_to_destination, escort_angle) < 10) {
			owner.printToWorld("Approaching Escort Position");
			double faceAngleDiff = owner.calcFutureFacingDifference(angle_to_destination);
			if(faceAngleDiff > 6)
			{
				owner.printToWorld("Facing Towards Position");
				action_rotation = owner.calcTurnDirection(angle_to_destination);
			} else {
				owner.printToWorld("Moving Towards Position");
				action_thrusting = ThrustingState.THRUST;
			}
		} else {
			owner.printToWorld("Adjusting Velocity");
			//We are in escort position, so adjust our velocity to match
			//double velAngle_owner = owner.getVelAngle();
			double velAngle_target = target.getVelAngle();
			double velSpeed_owner = owner.getVelSpeed();
			double velSpeed_target = target.getVelSpeed();
			action_rotation = owner.calcTurnDirection(velAngle_target);
			if(Math.abs(velSpeed_owner - velSpeed_target) > 0) {
				owner.printToWorld("Adjusting Velocity Speed");
				if(velSpeed_owner < velSpeed_target - 5) {
					owner.printToWorld("Increasing Velocity");
					action_thrusting = ThrustingState.THRUST;
				} else if(velSpeed_owner > velSpeed_target) {
					owner.printToWorld("Decreasing Velocity");
					action_thrusting = ThrustingState.BRAKE;
				}
			}
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
