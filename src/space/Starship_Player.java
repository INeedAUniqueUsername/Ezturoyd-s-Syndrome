package space;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.Timer;

import game.GamePanel;
import helpers.SpaceHelper;


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
		GamePanel.getWorld().getScreenDamage().healDisplay();
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
	public void draw(Graphics g) {
		for(SpaceObject so : GamePanel.getWorld().getStarships()) {
			if(so.equals(this)) {
				continue;
			}
			double angle = getAngleTowards(so);
			Point2D.Double velDiff = SpaceHelper.calcDiff(getVel(), so.getVel());
			double velAngle = SpaceHelper.arctanDegrees(velDiff);
			double velSpeed = SpaceHelper.magnitude(velDiff);
			double distance = getDistanceBetween(so);
			Point2D.Double arrowStart = polarOffset(angle, 200);
			SpaceHelper.drawArrow(g, arrowStart, SpaceHelper.polarOffset(arrowStart, angle, (distance - 200)/10), Color.RED);
			SpaceHelper.drawArrow(g, arrowStart, SpaceHelper.polarOffset(arrowStart, velAngle, velSpeed), Color.YELLOW);
			SpaceHelper.drawArrow(g, arrowStart, SpaceHelper.polarOffset(arrowStart, so.getPosR(), 10), Color.WHITE);
		}
		super.draw(g);
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
	public final void onDamage(double damage) {
		double structure = getStructure();
		int[] damageLevels = new int[] {100, 95, 90, 85, 80, 75, 71, 67, 63, 59, 55, 52, 49, 46, 43, 40, 38, 36, 34, 32, 30, 29, 28, 27, 26, 25};
		for(int i = 0; i < damageLevels.length; i++) {
			int level = damageLevels[i];
			//If we are looking at a damage level that we have not yet reached, then stop looking
			if(structure < level) {
				if(structure + damage > level) {
					for(int j = 0; j < i; j++) {
						GamePanel.getWorld().getScreenDamage().damageDisplay();
					}
				}
			} else {
				break;
			}
		}
	}
	public final void onDestroy() {
		GamePanel.getWorld().getScreenDamage().blind();
	}
}
