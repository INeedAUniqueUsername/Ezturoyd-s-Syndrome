package Space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;

import Body.Body_Weapon;
import Body.IBody;
import Interfaces.NewtonianMotion;

public class Weapon {

	SpaceObject owner;

	private boolean firing = false;

	private double pos_angle;
	private double pos_radius;

	private double fire_angle_offset;
	private double fire_angle;

	private double pos_x;
	private double pos_y;

	private int fire_cooldown_time = 10;
	private int fire_cooldown_left = fire_cooldown_time;
	private int projectile_speed = 10;
	private int projectile_damage = 5;
	private int projectile_lifetime = 60;
	
	private IBody body;
	
	public Weapon()
	{
		this(0, 0, 0, 10, 10, 5, 60);
	}
	public Weapon(double posAngle, double posRadius, double fire_angle_offset, int fire_cooldown_time, int projectile_speed, int projectile_damage, int projectile_lifetime) {
		setPosAngle(posAngle);
		setPosRadius(posRadius);
		
		setFireAngleOffset(fire_angle_offset);
		setFireCooldownTime(fire_cooldown_time);
		setFireCooldownLeft(fire_cooldown_time);
		setProjectileSpeed(projectile_speed);
		setProjectileDamage(projectile_damage);
		setProjectileLifetime(projectile_lifetime);
		setBody(new Body_Weapon(this));
	}
	public void update() {
		//System.out.println("---> Weapon Update");
		updateCooldown();
		double angle = pos_angle + owner.getPosR();
		setPos(owner.polarOffset(angle, pos_radius));
		setFireAngle(owner.getPosR() + fire_angle_offset);
		//System.out.println("<--- Weapon Update");
	}
	public void updateBody() {
		body.updateShapes();
	}
	public void updateCooldown() {
		fire_cooldown_left++;
	}
	public void draw(Graphics g) {
		owner.printToWorld("Drawing Weapon");
		g.setColor(Color.WHITE);
		body.updateShapes();
		drawBody(g);
		
		
		/*
		g.setColor(Color.RED);
		double trajectoryAngle = SpaceObject.arctanDegrees(
				projectile_speed * SpaceObject.sinDegrees(fire_angle) + owner.getVelY(),
				projectile_speed * SpaceObject.cosDegrees(fire_angle) + owner.getVelX()
				);
		
		Point2D.Double projectile_end = owner.polarOffset(trajectoryAngle, getProjectileRange());
		g.drawLine((int) pos_x, (int) pos_y, (int) projectile_end.getX(), (int) projectile_end.getY());
		*/
	}
	public final void drawBody(Graphics g) {
		body.draw(g);
	}
	public final Projectile getShotType() {
		return new Projectile(getPosX(), getPosY(), getFireAngle(), getProjectileDamage(), getProjectileLifetime());
	}

	public final Projectile createShot() {
		Projectile shot = getShotType();
		shot.setPosRectangular(owner.polarOffset(fire_angle, projectile_speed));
		shot.incPosPolar(owner.getPosR() + pos_angle, pos_radius);
		shot.setVelPolar(getFireAngle(), getProjectileSpeed());
		shot.incVelRectangular(owner.getVelX(), owner.getVelY());
		shot.setOwner(owner);
		return shot;
	}

	public final void setBody(IBody b) {
		body = b;
	}
	public final IBody getBody() {
		return body;
	}
	public final void setFiring(boolean state) {
		firing = state;
	}

	public final boolean getFiring() {
		return firing;
	}

	public final int getFireCooldownLeft() {
		return fire_cooldown_left;
	}

	public final void setFireCooldownLeft(int time) {
		fire_cooldown_left = time;
	}

	public final int getFireCooldownMax() {
		return fire_cooldown_time;
	}
	public double getFireAngle() {
		return fire_angle;
	}
	public void setFireAngle(double fire_angle) {
		this.fire_angle = fire_angle;
	}
	public double getFireAngleOffset() {
		return fire_angle_offset;
	}
	public void setFireAngleOffset(double fire_angle_offset) {
		this.fire_angle_offset = fire_angle_offset;
	}
	
	public final int getProjectileSpeed() {
		return projectile_speed;
	}
	
	public final void setProjectileSpeed(int projectile_speed) {
		this.projectile_speed = projectile_speed;
	}

	public final int getProjectileDamage() {
		return projectile_damage;
	}

	public final void setProjectileDamage(int projectile_damage) {
		this.projectile_damage = projectile_damage;
	}

	public final int getProjectileLifetime() {
		return projectile_lifetime;
	}
	
	public final void setProjectileLifetime(int projectile_lifetime) {
		this.projectile_lifetime = projectile_lifetime;
	}
	
	public final int getProjectileRange() {
		return projectile_speed * projectile_lifetime;
	}

	public final NewtonianMotion getOwner() {
		return owner;
	}

	public final void setOwner(Starship owner_new) {
		owner = owner_new;
	}

	public final void setPosAngle(double angle) {
		pos_angle = angle;
	}

	public final void setPosRadius(double radius) {
		pos_radius = radius;
	}
	public double getPosAngle() {
		return pos_angle;
	}
	public double getPosRadius() {
		return pos_radius;
	}

	public final void setFireCooldownTime(int time) {
		fire_cooldown_time = time;
	}

	public final int getFireCooldownTime() {
		return fire_cooldown_time;
	}

	public final double getPosX() {
		return pos_x;
	}
	public final double getPosY() {
		return pos_y;
	}
	public void setPosX(double pos_x) {
		this.pos_x = pos_x;
	}
	public void setPosY(double pos_y) {
		this.pos_y = pos_y;
	}
	public void setPos(Point2D.Double pos) {
		setPosX(pos.getX());
		setPosY(pos.getY());
	}
	public final double cosDegrees(double angle) {
		return Math.cos(Math.toRadians(angle));
	}

	public final double sinDegrees(double angle) {
		return Math.sin(Math.toRadians(angle));
	}
	
	public final boolean exists(Object o)
	{
		return o != null;
	}
}
