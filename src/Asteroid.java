import java.awt.Color;
import java.awt.Graphics;

public class Asteroid {

	double xPos;
	double yPos;
	int rPos;
	double xSpeed;
	double ySpeed;
	double rSpeed;
	int points_count;
	int[] points_distances;
	int points_angleDiff;
	
	final int MAX_SIZE = 100;
	final int MAX_SPEED = 5;
	final int MAX_POINTS = 10;
	final int MAX_ROTATION = 3;
	
	public Asteroid()
	{
		xPos = random(GameWindow.WIDTH);
		yPos = random(GameWindow.HEIGHT);
		points_count = (int) (random(MAX_POINTS));
		points_distances = new int[points_count];
		points_angleDiff = 360/points_count;
		for(int i = 0; i < points_count; i++)
		{
			points_distances[i] = (int) (Math.random()*MAX_SIZE);
		}
		rPos = (int) (random(360));
		xSpeed = random(MAX_SPEED)*cosDegrees((int) (random(360)));
		ySpeed = random(MAX_SPEED)*sinDegrees((int) (random(360)));
		rSpeed = random(MAX_ROTATION);
	}
	
	public double random(double input)
	{
		return Math.random()*input;
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.WHITE);
		
		int[] bodyX = new int[points_count+1];
		int[] bodyY = new int[points_count+1];
		for(int i = 0; i < points_count; i++)
		{
			int point_angle = (points_angleDiff*i) + rPos;
			int point_distance = points_distances[i];
			bodyX[i] = (int) (xPos + point_distance*cosDegrees(point_angle));
			bodyY[i] = (int) (GameWindow.HEIGHT - (yPos + point_distance*cosDegrees(point_angle)));
		}
		bodyX[points_count] = bodyX[0];
		bodyY[points_count] = bodyY[0];
		
		g.drawPolygon(bodyX, bodyY, points_count);
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
