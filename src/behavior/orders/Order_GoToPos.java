package behavior.orders;
import java.awt.geom.Point2D;

import behavior.Behavior_Starship;
import behavior.Behavior_Starship.AttackingState;
import behavior.Behavior_Starship.RotatingState;
import behavior.Behavior_Starship.StrafingState;
import behavior.Behavior_Starship.ThrustingState;
import helpers.SpaceHelper;
import space.Starship_NPC;

public class Order_GoToPos extends Behavior_Starship {
	private Point2D.Double destination;
	private int min_distance = 50;
	public Order_GoToPos(Starship_NPC o) {
		super(o);
		setDestination(o.getPos());
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
		super.update();
		if(tick % UPDATE_INTERVAL != 0) {
			return;
		}
		Starship_NPC owner = getOwner();
		//To allow the AI to take advantage of wraparound, we make four clones of the destination, one for each side of the screen.
		if(owner.getDistanceBetweenPos(destination) < min_distance) {
			System.out.println("GoToPos order done");
			setActive(false);
			return;
		}
		
		Point2D.Double pos_destination = getNearestPosClone(owner.getPos(), destination);
		double destination_x = pos_destination.getY();
		double destination_y = pos_destination.getX();
		
		ThrustingState action_thrusting = ThrustingState.NONE;
		RotatingState action_rotation = RotatingState.NONE;
		StrafingState action_strafing = StrafingState.NONE;
		AttackingState action_weapon = AttackingState.NONE;
		//double angle_to_destination = getAngleTowardsPos(destination_x_focus, destination_y_focus);
		//double distance_to_destination = destination_distance_focus;
		
		double angle_to_target = SpaceHelper.calcFireAngle(
				new Point2D.Double(
						destination_x - owner.getPosX(),
						destination_y - owner.getPosY()
						),
				new Point2D.Double(
						0 - owner.getVelX(),
						0 - owner.getVelY()
						),
				owner.getMax_speed()
				);
		double faceAngleDiff = owner.calcFutureFacingDifference(angle_to_target);
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
			if(SpaceHelper.getDistanceBetweenPos(owner.getFuturePosWithDeceleration(), destination) > min_distance){
				action_thrusting = ThrustingState.THRUST;
			}
		}
		setActions(action_thrusting, action_rotation, action_strafing, action_weapon);
	}
}
