import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;

public class Asteroid extends Space_Object{

	int points_count;
	int[] points_distances;
	int points_interval;
	
	
	final int MIN_SIZE = 25;
	final int MAX_SIZE = 100;
	final int MAX_SPEED = 5;
	final int MAX_POINTS = 10;
	final int MAX_ROTATION = 3;
	
	
	public Asteroid()
	{
		initializeBody();
		
		System.out.println("Count: " + points_count);
		System.out.println("Interval: " + points_interval);
		
		pos_x = random(GameWindow.WIDTH);
		pos_y = random(GameWindow.HEIGHT);
		pos_r = (int) (random(360));
		vel_x = random(MAX_SPEED)*cosDegrees((int) (random(360)));
		vel_y = random(MAX_SPEED)*sinDegrees((int) (random(360)));
		vel_r = random(MAX_ROTATION);
	}
	
	public void initializeBody()
	{
		points_count = (int) (random(MAX_POINTS) + 1);
		points_distances = new int[points_count];
		points_interval = 360/points_count;
		for(int i = 0; i < points_count; i++)
		{
			points_distances[i] = (int) (randomMin(MIN_SIZE, MAX_SIZE));
			System.out.println("Distance at angle " + points_interval*i + ": " + points_distances[i]);
		}
		updateBody();
		size = polygonArea(body.xpoints, body.ypoints, body.npoints);
	}
	
	public void initializeBody(int count)
	{
		points_count = (int) (random(count));
		points_distances = new int[points_count];
		points_interval = 360/points_count;
		for(int i = 0; i < points_count; i++)
		{
			points_distances[i] = (int) (randomMin(MIN_SIZE, MAX_SIZE));
			System.out.println("Distance at angle " + points_interval*i + ": " + points_distances[i]);
		}
		updateBody();
		size = polygonArea(body.xpoints, body.ypoints, body.npoints);
	}
	public void initializeBody(int count, int minSize, int maxSize)
	{
		points_count = (int) (random(count));
		points_distances = new int[points_count];
		points_interval = 360/points_count;
		for(int i = 0; i < points_count; i++)
		{
			points_distances[i] = (int) (randomMin(minSize, maxSize));
			System.out.println("Distance at angle " + points_interval*i + ": " + points_distances[i]);
		}
		updateBody();
		size = polygonArea(body.xpoints, body.ypoints, body.npoints);
	}
	public void initializeBody(int[] points)
	{
		points_count = points.length;
		points_distances = points;
		points_interval = 360/points_count;
		updateBody();
		size = polygonArea(body.xpoints, body.ypoints, body.npoints);
	}
	
	/*
	public void getCollisionDistance(int angle)
	{
		int leftPoint;
		int rightPoint;
		
		for(int i = 0; i < points_count; i++)
		{
			double rightAngle = (i*points_interval - rPos);
			if(angle < rightAngle)
			{
				rightPoint = i;
				leftPoint = i - 1;
				if(leftPoint < 0)
				{
					leftPoint = points_count - 1;
				}
				break;
			}
		}
		
	}
	*/
	public void collisionProjectile(Projectile p)
	{
		System.out.println("--> Collision (Projectile)");
		double collisionAngle = modRange(pos_r - getAngleTowards(p), 360);
		
		int index = (int) (collisionAngle/points_interval);
		
		System.out.println("Collision Angle: " + collisionAngle);
		System.out.println("Point Index: " + index);
		int distance = points_distances[index];
		if(distance >= 5)
		{
			points_distances[index] = distance - 5;
		}
		
		//world.addAsteroid(new Asteroid());
		
		System.out.println("<-- Collision (Projectile)");
	}
	public void collisionSpaceship(Spaceship s)
	{
		System.out.println("--> Collision (Spaceship)");
		double collisionAngle = getAngleTowards(s);
		s.incVelPolar(collisionAngle, getForceAngled(collisionAngle));
		
		System.out.println("<-- Collision (Spaceship)");
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.WHITE);
		
		updateBody();
		g.drawPolygon(body);
	}
	
	public void update()
	{
		System.out.println("--> Asteroid Update");
		updatePosition();
		System.out.println("<-- Asteroid Update");
	}
	
	public void updateBody()
	{
		int[] bodyX = new int[points_count+1];
		int[] bodyY = new int[points_count+1];
		for(int i = 0; i < points_count; i++)
		{
			double point_angle = (points_interval*i) + pos_r;
			int point_distance = points_distances[i];
			bodyX[i] = (int) (pos_x + point_distance*cosDegrees(point_angle));
			bodyY[i] = (int) (GameWindow.HEIGHT - (pos_y + point_distance*sinDegrees(point_angle)));
		}
		bodyX[points_count] = bodyX[0];
		bodyY[points_count] = bodyY[0];
		body = new Polygon(bodyX, bodyY, points_count);
	}
}
