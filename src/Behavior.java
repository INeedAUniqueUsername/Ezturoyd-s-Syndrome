import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class Behavior {
	Starship_NPC owner;
	private boolean active;
	public static enum ThrustingState {
		NONE, BRAKE, THRUST
	}
	public static enum RotatingState {
		NONE, CCW, CW
	}
	public static enum StrafingState {
		NONE, LEFT, RIGHT
	}
	public static enum AttackingState {
		NONE, FIRE
	}
	private ThrustingState action_thrusting = ThrustingState.NONE;
	private RotatingState action_rotating = RotatingState.NONE;
	private StrafingState action_strafing = StrafingState.NONE;
	private AttackingState action_attacking = AttackingState.NONE;
	public Behavior(Starship_NPC o) {
		setOwner(o);
		setActive(true);
	}
	public final void setOwner(Starship_NPC o) {
		owner = o;
	}
	public final SpaceObject getOwner() {
		return owner;
	}
	public void update() {
		
	}
	public final void setActive(boolean a) {
		active = a;
	}
	public final boolean getActive() {
		return active;
	}
	public final void setActions(ThrustingState t, RotatingState r, StrafingState s, AttackingState a) {
		setThrusting(t);
		setRotating(r);
		setStrafing(s);
		setAttacking(a);
	}
	public final void copyActions(Behavior b) {
		setActions(b.getThrusting(), b.getRotating(), b.getStrafing(), b.getAttacking());
	}
	public final void setThrusting(ThrustingState t) {
		action_thrusting = t;
	}
	public final ThrustingState getThrusting() {
		return action_thrusting;
	}
	public final void setRotating(RotatingState r) {
		action_rotating = r;
	}
	public final RotatingState getRotating() {
		return action_rotating;
	}
	public final void setStrafing(StrafingState s) {
		action_strafing = s;
	}
	public final StrafingState getStrafing() {
		return action_strafing;
	}
	public final void setAttacking(AttackingState a) {
		action_attacking = a;
	}
	public final AttackingState getAttacking() {
		return action_attacking;
	}
	public final void updateActions() {
		switch(action_thrusting) {
		case	THRUST:		owner.thrust();			break;
		case	BRAKE:		owner.brake();			break;
		case NONE:
			break;
		default:
			break;
		}
		switch(action_rotating) {
		case	CCW:		owner.turnCCW();		break;
		case	CW:			owner.turnCW();			break;
		case NONE:								break;
		default:									break;
		}
		switch(action_attacking) {
		case	FIRE:		owner.setFiring(true);	break;
		case	NONE:	owner.setFiring(false);	break;
		default:			owner.setFiring(false);	break;
		}
	}
	//Order_Attack helpers
	public static final Point2D.Double getNearestTargetClone(SpaceObject attacker, SpaceObject target) {
		//To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the screen.
		return getNearestPosClone(attacker.getPos(), target.getPos());
	}
	//Order_Attack helpers
	public static final Point2D.Double getNearestPosClone(Point2D.Double origin, Point2D.Double destination) {
		//To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the screen.
		double pos_x = origin.getX();
		double pos_y = origin.getY();
		double target_x_center = destination.getX();
		double target_y_center = destination.getY();
		double target_distance_center = SpaceObject.getDistanceBetweenPos(pos_x, pos_y, target_x_center, target_y_center);
		
		double target_x_up = target_x_center;
		double target_y_up = target_y_center - GameWindow.HEIGHT;
		double target_distance_up = SpaceObject.getDistanceBetweenPos(pos_x, pos_y, target_x_up, target_y_up);
		
		double target_x_down = target_x_center;
		double target_y_down = target_y_center + GameWindow.HEIGHT;
		double target_distance_down = SpaceObject.getDistanceBetweenPos(pos_x, pos_y, target_x_down, target_y_down);
		
		double target_x_right = target_x_center + GameWindow.WIDTH;
		double target_y_right = target_y_center;
		double target_distance_right = SpaceObject.getDistanceBetweenPos(pos_x, pos_y, target_x_right, target_y_right);
		
		double target_x_left = target_x_center - GameWindow.WIDTH;
		double target_y_left = target_y_center;
		double target_distance_left = SpaceObject.getDistanceBetweenPos(pos_x, pos_y, target_x_left, target_y_left);
		
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
		return new Point2D.Double(target_x_focus, target_y_focus);
	}
}
