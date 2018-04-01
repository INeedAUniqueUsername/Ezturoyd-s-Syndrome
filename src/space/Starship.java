package space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import behavior.Behavior_Starship;
import body.Body_Starship;
import body.Body_StarshipExhaust;
import factories.ExplosionFactory;
import game.GamePanel;
import helpers.SpaceHelper;
import interfaces.GameObject;

public class Starship extends SpaceObject {
	public static final double THRUST_DEFAULT = 2; //1//0.5
	public static final double MAX_SPEED_DEFAULT = 8;
	public static final double DECEL_DEFAULT = .2;
	//public static final double ROTATION_MAX_DEFAULT = 15; //Original
	public static final double ROTATION_MAX_DEFAULT = 8;
	//public static final double ROTATION_DECEL_DEFAULT = .4;
	public static final double ROTATION_DECEL_DEFAULT = .6;
	//public static final double ROTATION_ACCEL_DEFAULT = .6; //Original
	public static final double ROTATION_ACCEL_DEFAULT = .15;
	
	private double thrust, max_speed, decel, rotation_max, rotation_accel, rotation_decel;

	private int structure = 100;
	private int structure_max = 100;
	private ArrayList<String> print = new ArrayList<String>();

	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();

	public enum Sovereign {
		PLAYER, ENEMY
	}

	Sovereign alignment;

	public Starship() {
		setBody(new Body_Starship(this));
		updateBody();
		updateSize();
		setManeuverStats(THRUST_DEFAULT, MAX_SPEED_DEFAULT, DECEL_DEFAULT);
		setRotationStats(ROTATION_MAX_DEFAULT, ROTATION_ACCEL_DEFAULT, ROTATION_DECEL_DEFAULT);
	}

	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);

		updateBody();
		drawBody(g);
	}

	public void update() {
		if (getActive()) {
			updateActive();
		}
	}

	public void updateActive() {
		double speed_r = Math.abs(vel_r);
		if (speed_r > 0)
			if (speed_r > rotation_max) {
				vel_r = (vel_r > 0 ? 1 : -1) * rotation_max;
			} else {
				double vel_r_original = vel_r;
				vel_r -= (vel_r > 0 ? 1 : -1) * rotation_decel;
				// Check if vel_r changed positive to negative or vice versa. If it did, then set it to zero
				if (vel_r / vel_r_original < 0) {
					vel_r = 0;
				}
			}
		/*
		 * if (Math.sqrt(Math.pow(vel_x, 2) + Math.pow(vel_y, 2)) > max_speed) { int velAngle = (int)
		 * Helper.arctanDegrees(vel_y, vel_x); vel_x = max_speed * Helper.cosDegrees(velAngle); vel_y = max_speed *
		 * Helper.sinDegrees(velAngle); }
		 */
		updatePosition();
	}

	public final void onAttacked(GameObject attacker) {

	}

	public final void thrust() {
		// Add rectangular exhaust effects
		double exhaustAngle = pos_r + 180;
		Point2D.Double exhaustPos = polarOffset(exhaustAngle, 10);
		Projectile exhaust = new Projectile(exhaustPos.getX(), exhaustPos.getY(), exhaustAngle, 15, 10);
		exhaust.setOwner(this);
		exhaust.setBody(new Body_StarshipExhaust(exhaust));
		exhaust.incVelPolar(getVelAngle(), getVelSpeed());
		// exhaust.setVelPolar(velAngle, getVelSpeed());
		exhaust.incVelPolar(exhaustAngle, thrust * 8);
		// exhaust.incVelPolar(exhaustAngle + (vel_r > 0 ? 90 : -90), vel_r*2);
		exhaust.setPosR(pos_r);
		GamePanel.getWorld().createSpaceObject(exhaust);
		accelerateEnergy(pos_r, thrust * 5);
		//printToWorld("Thrust Energy: " + thrust * 5);
		//printToWorld("Thrust Acceleration: " + getAcceleration(thrust * 5));
		//printToWorld("Relativistic Mass: " + getRelativisticMass());
	}

	public final void turnCCW() {
		rotateLeft(rotation_accel + rotation_decel); //We add the decel value because decel happens constantly
	}
	public final void turnCW() {
		rotateRight(rotation_accel + rotation_decel); //We add the decel value because decel happens constantly
	}
	public final void strafeLeft() {
		accelerateEnergy(pos_r + 90, thrust);
	}
	public final void strafeRight() {
		accelerateEnergy(pos_r - 90, thrust);
	}
	public final void brake() {
		decelerate(decel);
	}
	public final void damage(int damage) {
		onDamage(damage);
		setStructure(getStructure() - damage);
		if (getStructure() < 0) {
			destroy();
			ExplosionFactory.createExplosion(getPos());
		}
	}
	
	public void onDamage(double damage) { }
	public final ArrayList<Weapon> getWeapon() { return weapons; }
	public final Weapon getWeaponPrimary() { return weapons.size() > 0 ? weapons.get(0) : null; }
	public boolean hasWeapon() { return weapons.size() > 0; }
	public final void setFiring(boolean state) { weapons.forEach(w -> w.setFiring(state)); }
	public void installWeapon(Weapon item) {
		item.setOwner(this);
		weapons.add(item);
		print("Installed Weapon");
	}
	
	public final double getVelTowards(SpaceObject object) {
		double angle_towards_object = getAngleTowards(object);
		return object.getVelAtAngle(angle_towards_object) - getVelAtAngle(angle_towards_object);
	}
	public final Point2D.Double getFuturePosWithDeceleration() {
		double decelTime = getVelSpeed() / decel;
		return new Point2D.Double(pos_x + vel_x * decelTime + ((vel_x > 0) ? -1 : 1) * 0.5 * decel * Math.pow(decelTime, 2),
				pos_y + vel_y * decelTime + ((vel_y > 0) ? -1 : 1) * 0.5 * decel * Math.pow(decelTime, 2));
	}
	public final double getTimeNeededToDecel(double targetSpeed) { return (getVelSpeed() - targetSpeed) / decel; }
	public final double getFutureAngleWithDeceleration() {
		double r_decel_time = Math.abs(vel_r / rotation_decel);
		// double angle_to_target_future = angle_to_target + target.getVelR() * r_decel_time;
		// Let's relearn AP Physics I!
		double pos_r_future = pos_r + vel_r * r_decel_time + ((vel_r > 0) ? -1 : 1) * 0.5 * rotation_decel * Math.pow(r_decel_time, 2);
		// Make sure that the deceleration value has the opposite sign of the rotation speed
		return pos_r_future;
	}
	public final double calcFutureFacingDifference(double angle_target) {
		double pos_r_future = getFutureAngleWithDeceleration();
		return calcAngleDiff(pos_r_future, angle_target);
	}
	public final double calcFacingDifference(double angle_target) {
		return calcAngleDiff(pos_r, angle_target);
	}
	public static final double calcAngleDiff(double angle1, double angle2) {
		double angleDiffCCW = SpaceHelper.modRangeDegrees(angle1 - angle2);
		double angleDiffCW = SpaceHelper.modRangeDegrees(angle2 - angle1);
		return SpaceHelper.min(angleDiffCCW, angleDiffCW);
	}
	
	public final void turnDirection(Behavior_Starship.RotatingState direction) {
		switch (direction) {
		case CCW:
			turnCCW();
			break;
		case CW:
			turnCW();
			break;
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
		for (Starship o : GamePanel.getWorld().getStarships()) {
			double d = getDistanceBetween(o);
			if (!o.equals(this) && targetIsEnemy(o) && d < distance) {
				result = o;
				distance = d;
			}
		}
		return result;
	}
	
	public void setManeuverStats(double thrust, double max_speed, double decel) {
		setThrust(thrust);
		setMaxSpeed(max_speed);
		setDecel(decel);
	}
	
	public void setRotationStats(double rotation_max, double rotation_accel, double rotation_decel) {
		setRotationMax(rotation_max);
		setRotationAccel(rotation_accel);
		setRotationDecel(rotation_decel);
	}
	
	public double getThrust() { return thrust; }
	public void setThrust(double thrust) { this.thrust = thrust; }
	public double getMaxSpeed() { return max_speed; }
	public void setMaxSpeed(double max_speed) { this.max_speed = max_speed; }
	public double getDecel() { return decel; }
	public void setDecel(double decel) { this.decel = decel; }
	public double getRotationMax() { return rotation_max; }
	public void setRotationMax(double rotation_max) { this.rotation_max = rotation_max; }
	public double getRotationAccel() { return rotation_accel; }
	public void setRotationAccel(double rotation_accel) { this.rotation_accel = rotation_accel; }
	public double getRotationDecel() { return rotation_decel; }
	public void setRotationDecel(double rotation_decel) { this.rotation_decel = rotation_decel; }

	public int getStructureMax() { return structure_max; }
	public void setStructureMax(int max) { structure_max = max; }
	public int getStructure() { return structure; }
	public void setStructure(int structure) { this.structure = structure; }
}
