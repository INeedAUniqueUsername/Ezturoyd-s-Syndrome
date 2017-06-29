package behavior;

import static game.GameWindow.GAME_HEIGHT;
import static game.GameWindow.GAME_WIDTH;
import static java.lang.Math.abs;

import java.awt.geom.Point2D;

import space.SpaceObject;
import space.Starship_NPC;

public abstract class Behavior_Starship {
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

	public Behavior_Starship(Starship_NPC o) {
		setOwner(o);
		setActive(true);
	}

	public final void setOwner(Starship_NPC o) {
		owner = o;
	}

	public final Starship_NPC getOwner() {
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

	public final void copyActions(Behavior_Starship b) {
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
		switch (action_thrusting) {
		case THRUST:
			owner.thrust();
			break;
		case BRAKE:
			owner.brake();
			break;
		case NONE:
			break;
		default:
			break;
		}
		switch (action_rotating) {
		case CCW:
			owner.turnCCW();
			break;
		case CW:
			owner.turnCW();
			break;
		case NONE:
			break;
		default:
			break;
		}
		switch (action_attacking) {
		case FIRE:
			owner.setFiring(true);
			break;
		case NONE:
			owner.setFiring(false);
			break;
		default:
			owner.setFiring(false);
			break;
		}
	}

	// Order_Attack helpers
	public static final Point2D.Double getNearestTargetClone(SpaceObject attacker, SpaceObject target) {
		// To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the
		// screen.
		return getNearestPosClone(attacker.getPos(), target.getPos());
	}

	// Order_Attack helpers
	public static final Point2D.Double getNearestPosClone(Point2D.Double origin, Point2D.Double destination) {
		// To allow the AI to take advantage of wraparound, we make four clones of the target, one for each side of the
		// screen.
		double pos_x = origin.getX();
		double pos_y = origin.getY();
		double target_x_focus = destination.getX();
		double target_y_focus = destination.getY();
		double distance = target_x_focus - pos_x;
		// Check if the target is more than halfway past the game width. If so, then use a wrap clone
		while (abs(distance) > GAME_WIDTH / 2) {
			// Target is east, so we take the west wrap clone.
			if (distance > 0) {
				target_x_focus -= GAME_WIDTH;
			} else {
				// Target is west, so we take the east wrap clone
				target_x_focus += GAME_WIDTH;
			}
			distance = target_x_focus - pos_x;
		}
		distance = target_y_focus - pos_y;
		while (abs(distance) > GAME_HEIGHT / 2) {
			// Target is north, so we take the south wrap clone.
			if (distance > 0) {
				target_y_focus -= GAME_HEIGHT;
			} else {
				// Target is south, so we take the north wrap clone
				target_y_focus += GAME_HEIGHT;
			}
			distance = target_y_focus - pos_y;
		}
		return new Point2D.Double(target_x_focus, target_y_focus);
	}

	public void printToWorld(String message) {
		owner.printToWorld(getClass().getName() + " " + message);
	}
}
