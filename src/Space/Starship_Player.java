package Space;
import java.util.ArrayList;


public class Starship_Player extends Starship {
	private ArrayList<Weapon_Key> weapons_key = new ArrayList<Weapon_Key>();
	private ArrayList<Weapon_Mouse> weapons_mouse = new ArrayList<Weapon_Mouse>();
	public Starship_Player() {
		super();
		setAlignment(Sovereign.PLAYER);
	}
	private boolean thrusting;
	private boolean turningCCW;
	private boolean turningCW;
	private boolean strafingLeft;
	private boolean strafingRight;
	private boolean braking;
	public void update() {
		if(getActive()) {
			updateActive();
			if(thrusting)
				thrust();
			if(braking)
				brake();
			if(turningCCW)
				turnCCW();
			if(turningCW)
				turnCW();
			if(strafingLeft)
				strafeLeft();
			if(strafingRight)
				strafeRight();
		}
	}
	public final void setThrusting(boolean b) {
		thrusting = b;
	}
	public final void setTurningCCW(boolean b)
	{
		turningCCW = b;
	}
	public final void setTurningCW(boolean b)
	{
		turningCW = b;
	}
	public final void setBraking(boolean b)
	{
		braking = b;
	}
	public final void setStrafingLeft(boolean enabled)
	{
		strafingLeft = enabled;
	}
	public final void setStrafingRight(boolean enabled)
	{
		strafingRight = enabled;
	}
	
	public final boolean getStrafingLeft()
	{
		return strafingLeft;
	}
	public final boolean getStrafingRight()
	{
		return strafingRight;
	}
	
	public final void installWeapon(Weapon item) {
		super.installWeapon(item);
		if(item instanceof Weapon_Key)
		{
			weapons_key.add((Weapon_Key) item);
		}
		else if(item instanceof Weapon_Mouse)
		{
			weapons_mouse.add((Weapon_Mouse) item);
		}
	}
	public final void setFiringKey(boolean firing)
	{
		for(Weapon_Key w: weapons_key)
		{
			w.setFiring(firing);
		}
	}
	public final void setFiringMouse(boolean firing)
	{
		for(Weapon_Mouse w: weapons_mouse)
		{
			w.setFiring(firing);
		}
	}
}
