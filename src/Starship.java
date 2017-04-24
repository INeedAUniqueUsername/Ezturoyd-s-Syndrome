import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Starship extends SpaceObject {

	final int HEAD_SIZE = 10; //20
	final int BODY_SIZE = 15; //30
	final double THRUST = .5; //1
	final int MAX_SPEED = 8;
	final double DECEL = .2;
	final double ROTATION_MAX = 15;
	final double ROTATION_ACCEL = .6;
	final double ROTATION_DECEL = .4;
	private boolean thrusting;
	private boolean turningCCW;
	private boolean turningCW;
	private boolean strafing;
	private boolean braking;
	private double structure = 1000;
	private ArrayList<String> print = new ArrayList<String>();

	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private ArrayList<Weapon_Key> weapons_key = new ArrayList<Weapon_Key>();
	private ArrayList<Weapon_Mouse> weapons_mouse = new ArrayList<Weapon_Mouse>();

	public Starship() {
		setBody(new Body_Starship(this));
		updateBody();
		updateSize();
	}

	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		
		updateBody();
		drawBody(g);
	}
	public void update() {
		if(getActive()) {
			double speed_r = Math.abs(vel_r);
			if (speed_r > 0)
				if(speed_r > ROTATION_MAX) {
					vel_r = (vel_r > 0 ? 1 : -1) * ROTATION_MAX;
				}
				else {
					double vel_r_original = vel_r;
					vel_r -= (vel_r > 0 ? 1 : -1)*ROTATION_DECEL;
					//Check if vel_r changed positive to negative or vice versa. If it did, then set it to zero
					if(vel_r / vel_r_original < 0) {
						vel_r = 0;
					}
				}
			if(thrusting)
				thrust();
			if(braking)
				brake();
			if(turningCCW)
				turnCCW();
			if(turningCW)
				turnCW();
			if (Math.sqrt(Math.pow(vel_x, 2) + Math.pow(vel_y, 2)) > MAX_SPEED) {
				int velAngle = (int) arctanDegrees(vel_y, vel_x);
				vel_x = MAX_SPEED * cosDegrees(velAngle);
				vel_y = MAX_SPEED * sinDegrees(velAngle);
			}
			updatePosition();
		}
	}
	
	public final void onAttacked(SpaceObject attacker)
	{
		
	}

	public final void thrust() {
		//Add rectangular exhaust effects
		//GamePanel.world.addSpaceObject();
		accelerate(pos_r, THRUST);
	}
	public final void turnCCW() {
		rotateLeft(ROTATION_ACCEL);
	}
	public final void turnCW() {
		rotateRight(ROTATION_ACCEL);
	}
	public final void strafeLeft() {
		accelerate(pos_r, THRUST);
	}
	public final void strafeRight() {
		accelerate(pos_r, THRUST);
	}
	public final void brake() {
		decelerate(DECEL);
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
	public final void setStrafing(boolean enabled)
	{
		strafing = enabled;
	}
	public final boolean getStrafing()
	{
		return strafing;
	}

	public final void damage(double damage) {
		structure = structure - damage;
		if(structure < 0)
		{
			destroy();
		}
	}
	
	public final ArrayList<Weapon> getWeapon()
	{
		return weapons;
	}
	public final Weapon getWeaponPrimary()
	{
		return weapons.size() > 0 ? weapons.get(0) : null;
	}

	public final void setFiring(boolean state) {
		for (Weapon weapon : weapons) {
			weapon.setFiring(state);
		}
	}
	public final void setAimPos(double x, double y)
	{
		for(Weapon weapon: weapons)
		{
			weapon.setAimPos(x, y);
		}
			
	}

	public final void installWeapon(Weapon item) {
		item.setOwner(this);
		weapons.add(item);
		if(item instanceof Weapon_Key)
		{
			weapons_key.add((Weapon_Key) item);
		}
		else if(item instanceof Weapon_Mouse)
		{
			weapons_mouse.add((Weapon_Mouse) item);
		}
		print("Installed Weapon");
	}
	/*
	public final void destroy()
	{
		for(Weapon w: weapons)
		{
			GamePanel.world.removeWeapon(w);
		}
		super.destroy();
	}
	*/
	
	public final double getVelTowards(SpaceObject object)
	{
		double angle_towards_object = getAngleTowards(object);
		return object.getVelAtAngle(angle_towards_object) - getVelAtAngle(angle_towards_object);
	}
	
	public final Point2D.Double getFuturePosWithDeceleration() {
		double x_decel_time = Math.abs(vel_x/DECEL);
		double y_decel_time = Math.abs(vel_y/DECEL);
		return new Point2D.Double(
				pos_x +
				vel_x * x_decel_time +
				((vel_x > 0) ? -1 : 1) * (1/2) * DECEL * Math.pow(x_decel_time, 2),
				pos_y +
				vel_y * y_decel_time+
				((vel_y > 0) ? -1 : 1) * (1/2) * DECEL * Math.pow(y_decel_time, 2)
				);
	}
	public final double getFutureAngleWithDeceleration() {
		double r_decel_time = Math.abs(vel_r/ROTATION_DECEL);
		//double angle_to_target_future = angle_to_target + target.getVelR() * r_decel_time;
		//Let's relearn AP Physics I!
		double pos_r_future =
				pos_r
				+ vel_r * r_decel_time
				+ ((vel_r > 0) ? -1 : 1) * (1/2) * ROTATION_DECEL * Math.pow(r_decel_time, 2)
				;	//Make sure that the deceleration value has the opposite sign of the rotation speed
		return pos_r_future;
	}
	public final double calcFutureFacingDifference(double angle_target)
	{
		double pos_r_future = getFutureAngleWithDeceleration();
		return calcAngleDiff(pos_r_future, angle_target);
	}
	public final double calcFacingDifference(double angle_target)
	{
		return calcAngleDiff(pos_r, angle_target);
	}
	public static final double calcAngleDiff(double angle1, double angle2) {
		double angleDiffCCW = modRangeDegrees(angle1 - angle2);
		double angleDiffCW = modRangeDegrees(angle2 - angle1);
		return min(angleDiffCCW, angleDiffCW);
	}
	public final void turnDirection(Behavior.RotatingState direction)
	{
		switch(direction)
		{
		case	CCW:	turnCCW();			break;
		case	CW:	turnCW();			break;
		}
	}
}
