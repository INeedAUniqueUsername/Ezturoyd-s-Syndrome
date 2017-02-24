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
	String action_rotation = ACT_NOTHING;
	String action_strafing = ACT_NOTHING;
	String action_weapon = ACT_NOTHING;
	public Behavior(Starship_NPC o) {
		setOwner(o);
	}
	public void setOwner(Starship_NPC o) {
		owner = o;
		active = true;
	}
	public Space_Object getOwner() {
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
	public void setActions(String t, String r, String s, String w) {
		action_thrusting = t;
		action_rotation = r;
		action_strafing = s;
		action_weapon = w;
	}
	public void updateActions() {
		switch(action_thrusting) {
		case	ACT_THRUST:		owner.thrust();			break;
		case	ACT_BRAKE:		owner.brake();			break;
		}
		switch(action_rotation) {
		case	ACT_TURN_CCW:	owner.turnCCW();			break;
		case	ACT_TURN_CW:	owner.turnCW();			break;
		}
		switch(action_weapon) {
		case	ACT_FIRE:		owner.setFiring(true);	break;
		default:				owner.setFiring(false);	break;
		}
	}
}
