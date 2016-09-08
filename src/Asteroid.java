import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Asteroid {

	double xPos;
	double yPos;
	int rPos;
	double xSpeed;
	double ySpeed;
	double rSpeed;
	int points_count;
	int[] points_distances;
	int points_interval;
	
	final int MIN_SIZE = 25;
	final int MAX_SIZE = 100;
	final int MAX_SPEED = 5;
	final int MAX_POINTS = 10;
	final int MAX_ROTATION = 3;
	Polygon body;
	
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
		xSpeed = random(MAX_SPEED)*cosDegrees((int) (random(360)));
		ySpeed = random(MAX_SPEED)*sinDegrees((int) (random(360)));
		rSpeed = random(MAX_ROTATION);
		
	}
	
	public double random(double input)
	{
		return Math.random()*input;
	}
	
	public double randomMin(double minimum, double input)
	{
		return (minimum + Math.random()*(input - minimum));
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.WHITE);
		
		int[] bodyX = new int[points_count+1];
		int[] bodyY = new int[points_count+1];
		for(int i = 0; i < points_count; i++)
		{
			int point_angle = (points_interval*i) + rPos;
			int point_distance = points_distances[i];
			bodyX[i] = (int) (xPos + point_distance*cosDegrees(point_angle));
			bodyY[i] = (int) (GameWindow.HEIGHT - (yPos + point_distance*sinDegrees(point_angle)));
		}
		bodyX[points_count] = bodyX[0];
		bodyY[points_count] = bodyY[0];
		body = new Polygon(bodyX, bodyY, points_count);
		g.drawPolygon(body);
	}
	public Polygon getBody() {
		return body;
	}
	public void getCollisionDistance(int angle)
	{
		int leftPoint;
		int rightPoint;
		
		for(int i = 0; i < points_count; i++)
		{
			int rightAngle = (i*points_interval - rPos);
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
	
	public double scaleLinearUp(double input, double minFrom, double maxFrom, double minTo, double maxTo)
	{
		double rangeFrom = maxFrom - minFrom;
		double rangeTo = maxTo - minTo;
		double rangeRatio = rangeTo/rangeFrom;
		double inputDiff = input - minFrom;
		
		return minTo + inputDiff*rangeRatio;
	}
	
	public void update()
	{
		rPos = (int) (rPos + rSpeed);
		xPos = xPos + xSpeed;
		yPos = yPos + ySpeed;
		
		if(Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2)) > MAX_SPEED)
		{
			int velAngle = (int) tanDegrees(ySpeed, xSpeed);
			xSpeed = MAX_SPEED*cosDegrees(velAngle);
			ySpeed = MAX_SPEED*sinDegrees(velAngle);
			
		}
		
		if(xPos < 0)
		{
			xPos = GameWindow.WIDTH;
		}
		else if(xPos > GameWindow.WIDTH)
		{
			xPos = 0;
		}
		
		if(yPos < 0)
		{
			yPos = GameWindow.HEIGHT;
		}
		if(yPos > GameWindow.HEIGHT)
		{
			yPos = 0;
		}
	}
	
	public void setVel(int x, int y)
	{
		xSpeed = x;
		ySpeed = y;
	}
	
	public void setAngle(int newAngle)
	{
		rPos = newAngle;
	}
	
	public double cosDegrees (int angle)
	{
		return Math.cos(Math.toRadians(angle));
	}
	
	public double sinDegrees (int angle)
	{
		return Math.sin(Math.toRadians(angle));
	}
	
	public double tanDegrees(double ySpeed, double xSpeed)
	{
		double result;
		if(xSpeed < 0)
		{
			result = Math.toDegrees(Math.atan(ySpeed/xSpeed)) + 180;
		}
		else if(xSpeed == 0)
		{
			if(ySpeed < 0)
			{
				result = 270;
			}
			else if(ySpeed == 0)
			{
				result = 0;
			}
			else //ySpeed > 0
			{
				result =  90;
			}
		}
		else if(xSpeed > 0)
		{
			result = Math.toDegrees(Math.atan(ySpeed/xSpeed));
		}
		else
		{
			result = 0;
		}
		System.out.println("X: " + xSpeed);
		System.out.println("Y: " + ySpeed);
		System.out.println("R: " + result);
		return result;
	}
	
	
	public void accelerate(int angle, double thrust)
	{
		xSpeed = (xSpeed + thrust*cosDegrees(angle));
		ySpeed = (ySpeed + thrust*sinDegrees(angle));
	}
	
	public void decelerate(double thrust)
	{
		int velAngle = (int) tanDegrees(ySpeed, xSpeed);
		xSpeed = (xSpeed + thrust*cosDegrees(velAngle+180));
		ySpeed = (ySpeed + thrust*sinDegrees(velAngle+180));
		
		if(Math.abs(xSpeed) < 0)
		{
			xSpeed = 0;
		}
		if(Math.abs(ySpeed) < 0)
		{
			ySpeed = 0;
		}
	}
	
	public void rotateLeft(int accel)
	{
		rSpeed = rSpeed + accel;
	}
	public void rotateRight(int accel)
	{
		rSpeed = rSpeed - accel;
	}
}
