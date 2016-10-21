import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class Weapon {

	Space_Object owner;

	final int SIZE = 10;

	boolean firing = false;

	double posAngle = 0;
	double posRadius = 0;

	double fire_angle = 0;

	double posX;
	double posY;

	int fire_cooldown_max = 10;
	int fire_cooldown_time = fire_cooldown_max;
	int projectile_speed = 10;
	int projectile_damage = 5;
	int projectile_lifetime = 60;

	Color color;
	public Weapon()
	{
		
	}
	public Weapon(double angle, double radius, double fire_angle, int cooldown, int speed, int damage, int lifetime, Color color) {
		posAngle = angle;
		posRadius = radius;
		this.fire_angle = fire_angle;
		fire_cooldown_max = cooldown;
		fire_cooldown_time = fire_cooldown_max;
		projectile_speed = speed;
		projectile_damage = damage;
		projectile_lifetime = lifetime;
		this.color = color;
	}

	public void update() {
		System.out.println("---> Weapon Update");
		fire_cooldown_time++;
		double angle = posAngle + owner.getPosR();
		posX = owner.getPosX() + posRadius * cosDegrees(angle);
		posY = owner.getPosY() + posRadius * sinDegrees(angle);

		fire_angle = angle;
		System.out.println("<--- Weapon Update");
	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];

		int bodyFrontX = (int) (posX + SIZE * cosDegrees(fire_angle));
		int bodyFrontY = (int) (GameWindow.HEIGHT - (posY + SIZE * sinDegrees(fire_angle)));

		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;

		bodyX[1] = (int) (posX + SIZE * cosDegrees(fire_angle - 120));
		bodyY[1] = (int) (GameWindow.HEIGHT - (posY + SIZE * sinDegrees(fire_angle - 120)));

		bodyX[2] = (int) (posX + SIZE * cosDegrees(fire_angle + 120));
		bodyY[2] = (int) (GameWindow.HEIGHT - (posY + SIZE * sinDegrees(fire_angle + 120)));

		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		g.drawPolygon(new Polygon(bodyX, bodyY, 4));
	}

	public Projectile getShotType() {
		return new Projectile(getPosX(), getPosY(), getFireAngle(), getProjectileDamage(), getProjectileLifetime(), color);
	}

	public Projectile getShot() {
		Projectile shot = getShotType();
		shot.setVelPolar(getFireAngle(), getProjectileSpeed());
		shot.incVelRectangular(owner.getVelX(), owner.getVelY());
		shot.setOwner(owner);
		return shot;
	}

	public void setFiring(boolean state) {
		firing = state;
	}

	public boolean getFiring() {
		return firing;
	}

	public int getFireCooldownLeft() {
		return fire_cooldown_time;
	}

	public void setFireCooldownLeft(int time) {
		fire_cooldown_time = time;
	}

	public int getFireCooldownMax() {
		return fire_cooldown_max;
	}

	public double getFireAngle() {
		return fire_angle;
	}

	public int getProjectileSpeed() {
		return projectile_speed;
	}
	
	public void setProjectileSpeed(int speed) {
		projectile_speed = speed;
	}

	public int getProjectileDamage() {
		return projectile_damage;
	}

	public void setProjectileDamage(int damage) {
		projectile_damage = damage;
	}

	public int getProjectileLifetime() {
		return projectile_lifetime;
	}
	
	public void setProjectileLifetime(int lifetime) {
		projectile_lifetime = lifetime;
	}

	public Space_Object getOwner() {
		return owner;
	}

	public void setOwner(Spaceship owner_new) {
		owner = owner_new;
		owner_new.installWeapon(this);
	}

	public void setPosAngle(double angle) {
		posAngle = angle;
	}

	public void setPosRadius(double radius) {
		posRadius = radius;
	}

	public void setFireCooldownTime(int time) {
		fire_cooldown_max = time;
	}

	public int getFireCooldownTime() {
		return fire_cooldown_max;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public double cosDegrees(double angle) {
		return Math.cos(Math.toRadians(angle));
	}

	public double sinDegrees(double angle) {
		return Math.sin(Math.toRadians(angle));
	}
}
