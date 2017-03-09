import java.util.ArrayList;

public class Behavior {
	Starship_NPC owner;
	private boolean active;
	public static enum ThrustingState {
		NOTHING, BRAKE, THRUST
	}
	public static enum RotatingState {
		NOTHING, CCW, CW
	}
	public static enum StrafingState {
		NOTHING, LEFT, RIGHT
	}
	public static enum AttackingState {
		NOTHING, FIRE
	}
	private ThrustingState action_thrusting = ThrustingState.NOTHING;
	private RotatingState action_rotating = RotatingState.NOTHING;
	private StrafingState action_strafing = StrafingState.NOTHING;
	private AttackingState action_attacking = AttackingState.NOTHING;
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
		case NOTHING:
			break;
		default:
			break;
		}
		switch(action_rotating) {
		case	CCW:		owner.turnCCW();		break;
		case	CW:			owner.turnCW();			break;
		case NOTHING:								break;
		default:									break;
		}
		switch(action_attacking) {
		case	FIRE:		owner.setFiring(true);	break;
		case	NOTHING:	owner.setFiring(false);	break;
		default:			owner.setFiring(false);	break;
		}
	}
}
