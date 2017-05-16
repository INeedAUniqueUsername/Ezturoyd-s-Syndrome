package Space;
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

import Body.Body_Starship;
import Body.Body_StarshipExhaust;
import Game.GamePanel;
import Helpers.SpaceHelper;

import java.util.Set;

import Interfaces.GameObject;
import Interfaces.IStarship;
import Interfaces.NewtonianMotion;

public class Starship extends SpaceObject implements IStarship {

	private double thrust, max_speed, decel, rotation_max, rotation_accel, rotation_decel;

	private double structure = 100;
	private ArrayList<String> print = new ArrayList<String>();

	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();

	enum Sovereign {
		PLAYER, ENEMY
	}
	Sovereign alignment;
	
	public Starship() {
		setBody(new Body_Starship(this));
		updateBody();
		updateSize();
		setManeuverStats(
				THRUST_DEFAULT,
				MAX_SPEED_DEFAULT,
				DECEL_DEFAULT
				);
		setRotationStats(
				ROTATION_MAX_DEFAULT,
				ROTATION_ACCEL_DEFAULT,
				ROTATION_DECEL_DEFAULT
				);
	}

	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		
		updateBody();
		drawBody(g);
	}
	public void update() {
		if(getActive()) {
			updateActive();
		}
	}
	public void updateActive() {
		double speed_r = Math.abs(vel_r);
		if (speed_r > 0)
			if(speed_r > rotation_max) {
				vel_r = (vel_r > 0 ? 1 : -1) * rotation_max;
			}
			else {
				double vel_r_original = vel_r;
				vel_r -= (vel_r > 0 ? 1 : -1)*rotation_decel;
				//Check if vel_r changed positive to negative or vice versa. If it did, then set it to zero
				if(vel_r / vel_r_original < 0) {
					vel_r = 0;
				}
			}
		/*
		if (Math.sqrt(Math.pow(vel_x, 2) + Math.pow(vel_y, 2)) > max_speed) {
			int velAngle = (int) Helper.arctanDegrees(vel_y, vel_x);
			vel_x = max_speed * Helper.cosDegrees(velAngle);
			vel_y = max_speed * Helper.sinDegrees(velAngle);
		}
		*/
		updatePosition();
	}
	
	public final void onAttacked(GameObject attacker)
	{
		
	}

	public final void thrust() {
		//Add rectangular exhaust effects
		double exhaustAngle = pos_r + 180;
		Point2D.Double exhaustPos = polarOffset(exhaustAngle, 10);
		Projectile exhaust = new Projectile(exhaustPos.getX(), exhaustPos.getY(), exhaustAngle, 10, 10);
		exhaust.setOwner(this);
		exhaust.setBody(new Body_StarshipExhaust(exhaust));
		//exhaust.setVelPolar(velAngle, getVelSpeed());
		exhaust.incVelPolar(exhaustAngle, 10);
		//exhaust.incVelPolar(exhaustAngle + (vel_r > 0 ? 90 : -90), vel_r*2);
		exhaust.setPosR(pos_r);
		GamePanel.getWorld().createSpaceObject(exhaust);
		accelerateEnergy(pos_r, thrust);
	}
	/* (non-Javadoc)
	 * @see IStarship#turnCCW()
	 */
	@Override
	public final void turnCCW() {
		rotateLeft(rotation_accel);
	}
	/* (non-Javadoc)
	 * @see IStarship#turnCW()
	 */
	@Override
	public final void turnCW() {
		rotateRight(rotation_accel);
	}
	/* (non-Javadoc)
	 * @see IStarship#strafeLeft()
	 */
	@Override
	public final void strafeLeft() {
		accelerateEnergy(pos_r+90, thrust);
	}
	/* (non-Javadoc)
	 * @see IStarship#strafeRight()
	 */
	@Override
	public final void strafeRight() {
		accelerateEnergy(pos_r-90, thrust);
	}
	/* (non-Javadoc)
	 * @see IStarship#brake()
	 */
	@Override
	public final void brake() {
		decelerate(decel);
	}

	public final void damage(double damage) {
		setStructure(getStructure() - damage);
		if(getStructure() < 0)
		{
			destroy();
		}
		onDamage(damage);
	}
	public void onDamage(double damage) {
		
	}
	
	public final ArrayList<Weapon> getWeapon()
	{
		return weapons;
	}
	public final Weapon getWeaponPrimary()
	{
		return weapons.size() > 0 ? weapons.get(0) : null;
	}
	public boolean hasWeapon() {
		return weapons.size() > 0;
	}
	public final void setFiring(boolean state) {
		for (Weapon weapon : weapons) {
			weapon.setFiring(state);
		}
	}

	public void installWeapon(Weapon item) {
		item.setOwner(this);
		weapons.add(item);
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
	
	/* (non-Javadoc)
	 * @see IStarship#getFuturePosWithDeceleration()
	 */
	@Override
	public final Point2D.Double getFuturePosWithDeceleration() {
		double x_decel_time = Math.abs(vel_x/decel);
		double y_decel_time = Math.abs(vel_y/decel);
		return new Point2D.Double(
				pos_x +
				vel_x * x_decel_time +
				((vel_x > 0) ? -1 : 1) * 0.5 * decel * Math.pow(x_decel_time, 2),
				pos_y +
				vel_y * y_decel_time+
				((vel_y > 0) ? -1 : 1) * 0.5 * decel * Math.pow(y_decel_time, 2)
				);
	}
	/* (non-Javadoc)
	 * @see IStarship#getFutureAngleWithDeceleration()
	 */
	@Override
	public final double getFutureAngleWithDeceleration() {
		double r_decel_time = Math.abs(vel_r/rotation_decel);
		//double angle_to_target_future = angle_to_target + target.getVelR() * r_decel_time;
		//Let's relearn AP Physics I!
		double pos_r_future =
				pos_r
				+ vel_r * r_decel_time
				+ ((vel_r > 0) ? -1 : 1) * 0.5 * rotation_decel * Math.pow(r_decel_time, 2)
				;	//Make sure that the deceleration value has the opposite sign of the rotation speed
		return pos_r_future;
	}
	/* (non-Javadoc)
	 * @see IStarship#calcFutureFacingDifference(double)
	 */
	@Override
	public final double calcFutureFacingDifference(double angle_target)
	{
		double pos_r_future = getFutureAngleWithDeceleration();
		return calcAngleDiff(pos_r_future, angle_target);
	}
	/* (non-Javadoc)
	 * @see IStarship#calcFacingDifference(double)
	 */
	@Override
	public final double calcFacingDifference(double angle_target)
	{
		return calcAngleDiff(pos_r, angle_target);
	}
	public static final double calcAngleDiff(double angle1, double angle2) {
		double angleDiffCCW = SpaceHelper.modRangeDegrees(angle1 - angle2);
		double angleDiffCW = SpaceHelper.modRangeDegrees(angle2 - angle1);
		return SpaceHelper.min(angleDiffCCW, angleDiffCW);
	}
	/* (non-Javadoc)
	 * @see IStarship#turnDirection(Behavior.RotatingState)
	 */
	@Override
	public final void turnDirection(Behavior.RotatingState direction)
	{
		switch(direction)
		{
		case	CCW:	turnCCW();			break;
		case	CW:	turnCW();			break;
		}
	}
	public void setAlignment(Sovereign a) {
		alignment = a;
	}
	public Sovereign getAlignment() {
		return alignment;
	}
	public boolean targetIsEnemy(Starship s) {
		return !alignment.equals(s.getAlignment());
	}
	
	public final Starship getClosestEnemyStarship() {
		double distance = Integer.MAX_VALUE;
		Starship result = null;
		for(Starship o : GamePanel.getWorld().getStarships()) {
			double d = getDistanceBetween(o);
			if(!o.equals(this) && targetIsEnemy(o) && d < distance) {
				result = o;
				distance = d;
			}
		}
		return result;
	}
	public void setStructure(int structure) {
		this.structure = structure;
	}
	/* (non-Javadoc)
	 * @see IStarship#setManeuverStats(double, double, double)
	 */
	@Override
	public void setManeuverStats(double thrust, double max_speed, double decel) {
		setThrust(thrust);
		setMax_speed(max_speed);
		setDecel(decel);
	}
	/* (non-Javadoc)
	 * @see IStarship#setRotationStats(double, double, double)
	 */
	@Override
	public void setRotationStats(double rotation_max, double rotation_accel, double rotation_decel) {
		setRotation_max(rotation_max);
		setRotation_accel(rotation_accel);
		setRotation_decel(rotation_decel);
	}
	/* (non-Javadoc)
	 * @see IStarship#getThrust()
	 */
	@Override
	public double getThrust() {
		return thrust;
	}

	/* (non-Javadoc)
	 * @see IStarship#setThrust(double)
	 */
	@Override
	public void setThrust(double thrust) {
		this.thrust = thrust;
	}

	/* (non-Javadoc)
	 * @see IStarship#getMax_speed()
	 */
	@Override
	public double getMax_speed() {
		return max_speed;
	}

	/* (non-Javadoc)
	 * @see IStarship#setMax_speed(double)
	 */
	@Override
	public void setMax_speed(double max_speed) {
		this.max_speed = max_speed;
	}

	/* (non-Javadoc)
	 * @see IStarship#getDecel()
	 */
	@Override
	public double getDecel() {
		return decel;
	}

	public void setDecel(double decel) {
		this.decel = decel;
	}

	/* (non-Javadoc)
	 * @see IStarship#getRotation_max()
	 */
	@Override
	public double getRotation_max() {
		return rotation_max;
	}

	/* (non-Javadoc)
	 * @see IStarship#setRotation_max(double)
	 */
	@Override
	public void setRotation_max(double rotation_max) {
		this.rotation_max = rotation_max;
	}

	/* (non-Javadoc)
	 * @see IStarship#getRotation_accel()
	 */
	@Override
	public double getRotation_accel() {
		return rotation_accel;
	}

	/* (non-Javadoc)
	 * @see IStarship#setRotation_accel(double)
	 */
	@Override
	public void setRotation_accel(double rotation_accel) {
		this.rotation_accel = rotation_accel;
	}

	/* (non-Javadoc)
	 * @see IStarship#getRotation_decel()
	 */
	@Override
	public double getRotation_decel() {
		return rotation_decel;
	}

	/* (non-Javadoc)
	 * @see IStarship#setRotation_decel(double)
	 */
	@Override
	public void setRotation_decel(double rotation_decel) {
		this.rotation_decel = rotation_decel;
	}

	public double getStructure() {
		return structure;
	}

	public void setStructure(double structure) {
		this.structure = structure;
	}
}
