import java.awt.Color;
import java.awt.Graphics;

public class Projectile {

	double xPos;
	double yPos;
	int rPos;
	
	double xSpeed;
	double ySpeed;
	double rSpeed;
	
	int size;
	
	public Projectile(double posX, double posY, int posR, double speedX, double speedY, double speedR, int size)
	{
		xPos = posX;
		yPos = posY;
		rPos = posR;
		xSpeed = speedX;
		ySpeed = speedY;
		rSpeed = speedR;
		this.size = size;
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
		g.setColor(Color.RED);
		
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];
		
		int bodyFrontX = (int) 						(xPos+size*cosDegrees(rPos));
		int bodyFrontY = (int) (GameWindow.HEIGHT-	(yPos+size*sinDegrees(rPos)));
		
		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;
		
		bodyX[1] = (int) 						(xPos+size*cosDegrees(rPos-120));
		bodyY[1] = (int) (GameWindow.HEIGHT-	(yPos+size*sinDegrees(rPos-120)));
		
		bodyX[2] = (int) 						(xPos+size*cosDegrees(rPos+120));
		bodyY[2] = (int) (GameWindow.HEIGHT-	(yPos+size*sinDegrees(rPos+120)));
		
		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		g.drawPolygon(bodyX, bodyY, 4);
	}
	
	public void update()
	{
		rPos = (int) (rPos + rSpeed);
		xPos = xPos + xSpeed;
		yPos = yPos + ySpeed;
		
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
	
	public double tanDegrees(double y, double x)
	{
		double result;
		if(x < 0)
		{
			result = Math.toDegrees(Math.atan(y/x)) + 180;
		}
		else if(x == 0)
		{
			if(y < 0)
			{
				result = 270;
			}
			else if(y == 0)
			{
				result = 0;
			}
			else //ySpeed > 0
			{
				result =  90;
			}
		}
		else if(x > 0)
		{
			result = Math.toDegrees(Math.atan(y/x));
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
}
