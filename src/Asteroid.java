import java.awt.Color;
import java.awt.Graphics;
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
		points_count = (int) (random(MAX_POINTS));
		points_distances = new int[points_count];
		points_interval = 360/points_count;

		System.out.println("Count: " + points_count);
		System.out.println("Interval: " + points_interval);
		
		for(int i = 0; i < points_count; i++)
		{
			points_distances[i] = (int) (randomMin(MIN_SIZE, MAX_SIZE));
			System.out.println("Distance at angle " + points_interval*i + ": " + points_distances[i]);
		}
		
		xPos = random(GameWindow.WIDTH);
		yPos = random(GameWindow.HEIGHT);
		rPos = (int) (random(360));
		xVel = random(MAX_SPEED)*cosDegrees((int) (random(360)));
		yVel = random(MAX_SPEED)*sinDegrees((int) (random(360)));
		rVel = random(MAX_ROTATION);
		
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
		double collisionAngle = rPos - modRange(getAngleTowards(p), 360);
		while(collisionAngle < 0)
		{
			collisionAngle = collisionAngle + 360;
		}
		int index = (int) (collisionAngle/points_interval);
		
		System.out.println("Collision Angle: " + collisionAngle);
		System.out.println("Point Index: " + index);
		points_distances[index] = points_distances[index] - 5;
		System.out.println("<-- Collision (Projectile)");
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.WHITE);
		
		updateBody();
		g.drawPolygon(body);
	}
	
	public void update()
	{
		updatePosition();
	}
	
	public void updateBody()
	{
		int[] bodyX = new int[points_count+1];
		int[] bodyY = new int[points_count+1];
		for(int i = 0; i < points_count; i++)
		{
			double point_angle = (points_interval*i) + rPos;
			int point_distance = points_distances[i];
			bodyX[i] = (int) (xPos + point_distance*cosDegrees(point_angle));
			bodyY[i] = (int) (GameWindow.HEIGHT - (yPos + point_distance*sinDegrees(point_angle)));
		}
		bodyX[points_count] = bodyX[0];
		bodyY[points_count] = bodyY[0];
		body = new Polygon(bodyX, bodyY, points_count);
	}
}
