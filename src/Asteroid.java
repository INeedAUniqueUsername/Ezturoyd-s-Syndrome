import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Asteroid extends Space_Object {

	HashMap<Integer, Integer> points;

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
		points = new HashMap<Integer, Integer>();
		for(int degree = 0; degree < 360; degree += Math.random()*60)
		{
			int distance = (int) (Math.random()*100);
			points.put(degree, distance);
		}
		updateBody();
		updateSize();
	}
	public void initializeBody(int count, int minSize, int maxSize) {
		points = new HashMap<Integer, Integer>();
		int degreesLeft = 360;
		int previousAngle = 0;
		for(int i = 0; i < count; i++)
		{
			int angle = (int) (previousAngle + Math.random()*degreesLeft);
			int distance = (int) (randomMin(minSize, maxSize));
			points.put(angle, distance);
			degreesLeft = 360 - angle;
		}
		updateBody();
		updateSize();
	}
	public void collisionProjectile(Projectile p) {
		//print("--> Ateroid-Projectile Collision");
		double collisionAngle = getCollisionAngle(p);
		//print("Collision Angle: " + collisionAngle);
		
		int damage = p.getDamage();

		damage(damage, p.getPosX(), p.getPosY());
		impulse(getAngleFrom(p), damage * 10);
		//createFragment(damage, getAngleTowards(p), p.getPosX(), p.getPosY());
		//print("<-- Asteroid-Projectile Collision");
	}
	public double getCollisionAngle(Space_Object o)
	{
		return modRange(pos_r + getAngleTowards(o), 360);
	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);

		updateBody();
		drawBody(g);
	}

	public void update() {
		//print("--> Asteroid Update");
		updatePosition();
		//print("<-- Asteroid Update");
	}

	public void updateBody() {
		int points_count = points.size();
		int[] bodyX = new int[points_count + 1];
		int[] bodyY = new int[points_count + 1];
		Integer[] degree_list = points.keySet().toArray(new Integer[points_count]);
		int[] degree_list_int = new int[degree_list.length];
		for(int i = 0; i < degree_list.length; i++)
		{
			degree_list_int[i] = degree_list[i].intValue();
		}
		Arrays.sort(degree_list_int);
		
		for (int i = 0; i < points_count; i++) {
			int degree = degree_list_int[i];
			int distance = points.get(degree);
			double angle = (degree + pos_r);
			bodyX[i] = (int) (pos_x + distance * cosDegrees(angle));
			bodyY[i] = (int) (GameWindow.HEIGHT - (pos_y + distance * sinDegrees(angle)));
		}
		bodyX[points_count] = bodyX[0];
		bodyY[points_count] = bodyY[0];
		body = new ArrayList<Polygon>();
		body.add(new Polygon(bodyX, bodyY, points_count));
		updateSize();
		/*
		System.out.println();
		System.out.print("Angles: ");
		for(int i = 0; i < degree_list_int.length; i++)
		{
			int angle = degree_list_int[i];
			System.out.print(angle + ":" + points.get(angle) + ", ");
		}
		*/
	}

	public void damage(int damage, double x, double y) {
		int angle = (int) getAngleTowardsPos(x, y);
		int angle_total = (int) modRangeDegrees(angle - pos_r);
		for(int i = -damage*3; i < damage*3; i++)
		{
			int angle_i = angle_total + i;
			int distance = points.containsKey(angle_i) ? points.get(angle_i) : (int) getDistanceBetweenPos(x, y);
			int distance_new = distance - (damage /* - Math.abs(i) */ );
			points.put(angle_i, distance_new > 0 ? distance_new : 0);
			System.out.println(angle_i + " (NEW) : " + distance + " -> " + distance_new);
		}
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
