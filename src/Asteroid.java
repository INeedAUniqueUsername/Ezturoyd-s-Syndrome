import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;

public class Asteroid extends Space_Object {

	int points_count;
	int[] points_distances;
	int points_interval;

	final int MIN_SIZE = 25;
	final int MAX_SIZE = 100;
	final int MAX_SPEED = 5;
	final int MAX_POINTS = 10;
	final int MAX_ROTATION = 3;

	public Asteroid() {
		initializeBody();

		//print("Count: " + points_count);
		//print("Interval: " + points_interval);

		pos_x = random(GameWindow.WIDTH);
		pos_y = random(GameWindow.HEIGHT);
		pos_r = (int) (random(360));
		vel_x = random(MAX_SPEED) * cosDegrees((int) (random(360)));
		vel_y = random(MAX_SPEED) * sinDegrees((int) (random(360)));
		vel_r = random(MAX_ROTATION);
	}

	public void initializeBody() {
		points_count = (int) (random(MAX_POINTS) + 1);
		points_distances = new int[points_count];
		points_interval = 360 / points_count;
		for (int i = 0; i < points_count; i++) {
			points_distances[i] = (int) (randomMin(MIN_SIZE, MAX_SIZE));
			//print("Distance at angle " + points_interval * i + ": " + points_distances[i]);
		}
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}

	public void initializeBody(int count) {
		points_count = (int) (random(count));
		points_distances = new int[points_count];
		points_interval = 360 / points_count;
		for (int i = 0; i < points_count; i++) {
			points_distances[i] = (int) (randomMin(MIN_SIZE, MAX_SIZE));
			//print("Distance at angle " + points_interval * i + ": " + points_distances[i]);
		}
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}

	public void initializeBody(int count, int minSize, int maxSize) {
		points_count = (int) (random(count));
		points_distances = new int[points_count];
		points_interval = 360 / points_count;
		for (int i = 0; i < points_count; i++) {
			points_distances[i] = (int) (randomMin(minSize, maxSize));
			//print("Distance at angle " + points_interval * i + ": " + points_distances[i]);
		}
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}

	public void initializeBody(int[] points) {
		points_count = points.length;
		points_distances = points;
		points_interval = 360 / points_count;
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}

	/*
	 * public void getCollisionDistance(int angle) { int leftPoint; int
	 * rightPoint;
	 * 
	 * for(int i = 0; i < points_count; i++) { double rightAngle =
	 * (i*points_interval - rPos); if(angle < rightAngle) { rightPoint = i;
	 * leftPoint = i - 1; if(leftPoint < 0) { leftPoint = points_count - 1; }
	 * break; } }
	 * 
	 * }
	 */
	public void collisionProjectile(Projectile p) {
		//print("--> Ateroid-Projectile Collision");
		double collisionAngle = getCollisionAngle(p);
		int index = getAngleIndex(collisionAngle);
		//print("Collision Angle: " + collisionAngle);
		//print("Point Index: " + index);

		int damage = p.getDamage();

		impulse(getAngleFrom(p), damage * 10);
		/*
		 * if(points_distances[index] >= damage) { points_distances[index] -=
		 * damage; } else { points_distances[index] = 0; }
		 */
		int applied_total = damage(index, damage);
		createFragment(applied_total, getAngleTowards(p), p.getPosX(), p.getPosY());
		//print("<-- Asteroid-Projectile Collision");
	}
	public int getCollisionIndex(Space_Object o)
	{
		return getAngleIndex(getCollisionAngle(o));
	}
	public double getCollisionAngle(Space_Object o)
	{
		return modRange(pos_r + getAngleTowards(o), 360);
	}
	public int getAngleIndex(double angle)
	{
		return (int) (angle / points_interval);
	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);

		updateBody();
		g.drawPolygon(body);
	}

	public void update() {
		//print("--> Asteroid Update");
		updatePosition();
		//print("<-- Asteroid Update");
	}

	public void updateBody() {
		int[] bodyX = new int[points_count + 1];
		int[] bodyY = new int[points_count + 1];
		for (int i = 0; i < points_count; i++) {
			double point_angle = (points_interval * i) + pos_r;
			int point_distance = points_distances[i];
			bodyX[i] = (int) (pos_x + point_distance * cosDegrees(point_angle));
			bodyY[i] = (int) (GameWindow.HEIGHT - (pos_y + point_distance * sinDegrees(point_angle)));
		}
		bodyX[points_count] = bodyX[0];
		bodyY[points_count] = bodyY[0];
		body = new Polygon(bodyX, bodyY, points_count);
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}

	public int damage(int damage, int index) {
		int maxIndex = points_distances.length - 1;
		int indexCW = (int) modRange(index - 1, maxIndex);
		int indexCCW = (int) modRange(index + 1, maxIndex);
		boolean clockwise = true;
		int applied_total = 0;
		while (damage > 0) {
			print("Count: " + points_distances.length);
			print("Index: " + index);
			print("Health: " + points_distances[index]);
			print("Size: " + size);
			print("Damage: " + damage);

			int applied;
			if (damage > size / 50) {
				destroy();
				damage = 0;
			} else {
				if (points_distances[index] > 0) {
					applied = (int) range(damage, 0, points_distances[index]);
					points_distances[index] -= applied;
					damage -= applied;
					applied_total += applied;
				}
				if (points_distances[indexCW] > 0) {
					applied = (int) range(damage, 0, points_distances[indexCW]);
					points_distances[indexCW] -= applied;
					damage -= applied;
					applied_total += applied;
				}
				if (points_distances[indexCCW] > 0) {
					applied = (int) range(damage, 0, points_distances[indexCCW]);
					points_distances[indexCCW] -= applied;
					damage -= applied;
					applied_total += applied;
				}
				
				//Recalculate the center
			}
			indexCW -= 1;
			indexCCW += 1;
			
			indexCW = (int) modRange(indexCW, maxIndex);
			indexCCW = (int) modRange(indexCCW, maxIndex);
		}

		return applied_total;
		// world.addAsteroid(new Asteroid());
	}

	public void createFragment(int damage, double angle, double x, double y)
	{
		if (damage > 5) {
			Asteroid fragment = new Asteroid();
			fragment.initializeBody(5, damage, damage * 2);
			fragment.setPosRectangular(x, y);
			fragment.setVelPolar(angle, damage / 2);
			world.addAsteroid(fragment);
		} else {
			Projectile fragment = new Projectile(x, y, angle, damage, 30, damage, damage, Color.WHITE);
			fragment.setVelPolar(angle, damage / 2);
			fragment.setOwner(this);
			world.addProjectile(fragment);
		}
	}
	
	public void destroy()
	{
		world.removeAsteroid(this);
	}
}
