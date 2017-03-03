import java.awt.geom.Point2D;

public class Behavior {
	final static String ACT_THRUST = "Thrust";
	final static String ACT_BRAKE = "Brake";
	final static String ACT_TURN_CCW = "CCW";
	final static String ACT_TURN_CW = "CW";
	final static String ACT_FIRE = "Fire";
	final static String ACT_NOTHING = "Nothing";
	
	protected Starship_NPC owner;
	boolean active;
	String action_thrusting = ACT_NOTHING;
	String action_rotating = ACT_NOTHING;
	String action_strafing = ACT_NOTHING;
	String action_attacking = ACT_NOTHING;
	public Behavior(Starship_NPC o) {
		setOwner(o);
	}
	public void setOwner(Starship_NPC o) {
		owner = o;
		active = true;
	}
	public SpaceObject getOwner() {
		return owner;
	}
	public void update() {
		
	}
	public void setActive(boolean a) {
		active = a;
	}
	public boolean getActive() {
		return active;
	}
	public void setActions(String[] actions) {
		setThrusting(actions[0]);
		setRotating(actions[1]);
		setStrafing(actions[2]);
		setAttacking(actions[3]);
	}
	public String[] getActions() {
		return new String[] {action_thrusting, action_rotating, action_strafing, action_attacking};
	}
	public void setActions(String t, String r, String s, String a) {
		setThrusting(t);
		setRotating(r);
		setStrafing(s);
		setAttacking(a);
	}
	public void setThrusting(String t) {
		action_thrusting = t;
	}
	public void setRotating(String r) {
		action_rotating = r;
	}
	public void setStrafing(String s) {
		action_strafing = s;
	}
	public void setAttacking(String a) {
		action_attacking = a;
	}
	public void updateActions() {
		switch(action_thrusting) {
		case	ACT_THRUST:		owner.thrust();			break;
		case	ACT_BRAKE:		owner.brake();			break;
		}
		switch(action_rotating) {
		case	ACT_TURN_CCW:	owner.turnCCW();			break;
		case	ACT_TURN_CW:	owner.turnCW();			break;
		}
		switch(action_attacking) {
		case	ACT_FIRE:		owner.setFiring(true);	break;
		default:				owner.setFiring(false);	break;
		}
	}
}
