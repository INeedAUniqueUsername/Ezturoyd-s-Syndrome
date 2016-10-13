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
		
		print("Count: " + points_count);
		print("Interval: " + points_interval);
		
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
			print("Distance at angle " + points_interval*i + ": " + points_distances[i]);
		}
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}
	
	public void initializeBody(int count)
	{
		points_count = (int) (random(count));
		points_distances = new int[points_count];
		points_interval = 360/points_count;
		for(int i = 0; i < points_count; i++)
		{
			points_distances[i] = (int) (randomMin(MIN_SIZE, MAX_SIZE));
			print("Distance at angle " + points_interval*i + ": " + points_distances[i]);
		}
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}
	public void initializeBody(int count, int minSize, int maxSize)
	{
		points_count = (int) (random(count));
		points_distances = new int[points_count];
		points_interval = 360/points_count;
		for(int i = 0; i < points_count; i++)
		{
			points_distances[i] = (int) (randomMin(minSize, maxSize));
			print("Distance at angle " + points_interval*i + ": " + points_distances[i]);
		}
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}
	public void initializeBody(int[] points)
	{
		points_count = points.length;
		points_distances = points;
		points_interval = 360/points_count;
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
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
		print("--> Ateroid-Projectile Collision");
		double collisionAngle = modRange(pos_r + getAngleTowards(p), 360);
		int index = (int) (collisionAngle/points_interval);
		print("Collision Angle: " + collisionAngle);
		print("Point Index: " + index);
		
		
		int damage = p.getDamage();
		
		impulse(getAngleFrom(p), damage*10);
		/*
		if(points_distances[index] >= damage)
		{
			points_distances[index] -= damage;
		}
		else
		{
			points_distances[index] = 0;
		}
		*/
		int applied_total = damage(index, damage);
		Asteroid fragment = new Asteroid();
		fragment.initializeBody(5, applied_total, applied_total*2);
		fragment.setPosRectangular(p.getPosX(), p.getPosY());
		world.addAsteroid(fragment);
		print("<-- Asteroid-Projectile Collision");
	}
	
	public void collisionSpaceship(Spaceship s)
	{
		print("--> Collision (Spaceship)");
		double angle_asteroid_to_ship = getAngleTowards(s);
		double angle_ship_to_asteroid = getAngleFrom(s);
		double asteroidMomentum = getMomentumAngled(angle_asteroid_to_ship);
		double shipMomentum = s.getMomentumAngled(angle_ship_to_asteroid);
		double totalMomentum = asteroidMomentum + shipMomentum;
		double halfMomentum = totalMomentum/2;
		s.impulse(angle_asteroid_to_ship, halfMomentum);
		impulse(angle_ship_to_asteroid, halfMomentum);
		
		s.damage(halfMomentum/100);
		
		print("Angle (Asteroid --> Ship): " + angle_asteroid_to_ship);
		print("Angle (Asteroid <-- Ship): " + angle_ship_to_asteroid);
		print("Momentum (Asteroid) " + asteroidMomentum);
		print("Momentum (Ship): " + shipMomentum);
		print("<-- Collision (Spaceship)");
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.WHITE);
		
		updateBody();
		g.drawPolygon(body);
	}
	
	public void update()
	{
		print("--> Asteroid Update");
		updatePosition();
		print("<-- Asteroid Update");
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
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}
	
	public int damage(int damage, int index)
	{
		int indexInc = 1;
		boolean clockwise = true;
		int applied_total = 0;
		while(damage > 0)
		{
			print("Size: " + size);
			print("Damage: " + damage);
			if(damage > size/50)
			{
				world.removeAsteroid(this);
				damage = 0;
			}
			else
			{
				int maxIndex = points_distances.length - 1;
				int applied = (int) range(damage, 0, points_distances[index]);
				points_distances[index] -= applied;
				
				damage -= applied;
				applied_total += applied;
				if(clockwise)
				{
					clockwise = false;
					index += indexInc;
					index = (int) modRange(index, maxIndex);
				}
				else
				{
					clockwise = true;
					index -= indexInc;
					index = (int) modRange(index, maxIndex);
				}
				indexInc += (index > 0)? index + 1: index - 1;
				
				print("Index: " + index);
			}
		}
		
		return applied_total;
		//world.addAsteroid(new Asteroid());
	}
}
