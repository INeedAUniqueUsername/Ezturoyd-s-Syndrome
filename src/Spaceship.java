import java.awt.Color;
import java.awt.Graphics;

public class Spaceship {
	
	final int HEAD_SIZE = 20;
	final int BODY_SIZE = 30;
	final int THRUST = 1;
	final int MAX_SPEED = 8;
	final double DECEL = .5;
	final int ROTATION_MAX = 15;
	final int ROTATION_ACCEL = 4;
	final double ROTATION_DECEL = .4;
	double xPos;
	double yPos;
	double xSpeed;
	double ySpeed;
	double rSpeed;
	int angle;
	boolean thrusting;
	
	public Spaceship(int x, int y)
	{
		xPos = x;
		yPos = y;
		
		xSpeed = 0;
		ySpeed = 0;
		
		angle = 45;
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.RED);
		
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];
		
		int[] headX = new int[4];
		int[] headY = new int[4];
		/*
		int topCornerX = (int) (xPos+SIZE*cosDegrees(angle));
		int topCornerY = (int) (yPos+SIZE*sinDegrees(angle));
		
		int bottomRightCornerX = (int) (xPos+SIZE*cosDegrees(angle-120));
		int bottomRightCornerY = (int) (yPos+SIZE*sinDegrees(angle-120));
		
		int bottomLeftCornerX = (int) (xPos+SIZE*cosDegrees(angle+120));
		int bottomLeftCornerY = (int) (xPos+SIZE*sinDegrees(angle+120));
		*/
		
		int bodyFrontX = (int) 						(xPos+BODY_SIZE*cosDegrees(angle));
		int bodyFrontY = (int) (GameWindow.HEIGHT-	(yPos+BODY_SIZE*sinDegrees(angle)));
		
		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;
		
		bodyX[1] = (int) 						(xPos+BODY_SIZE*cosDegrees(angle-120));
		bodyY[1] = (int) (GameWindow.HEIGHT-	(yPos+BODY_SIZE*sinDegrees(angle-120)));
		
		bodyX[2] = (int) 						(xPos+BODY_SIZE*cosDegrees(angle+120));
		bodyY[2] = (int) (GameWindow.HEIGHT-	(yPos+BODY_SIZE*sinDegrees(angle+120)));
		
		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		
		int headFrontX = (int) (bodyFrontX+HEAD_SIZE*cosDegrees(angle));
		int headFrontY = (int) (bodyFrontY-HEAD_SIZE*sinDegrees(angle));
		
		headX[0] = headFrontX;
		headY[0] = headFrontY;
		
		headX[1] = (int) (bodyFrontX+HEAD_SIZE*cosDegrees(angle-120));
		headY[1] = (int) (bodyFrontY-HEAD_SIZE*sinDegrees(angle-120));
		
		headX[2] = (int) (bodyFrontX+HEAD_SIZE*cosDegrees(angle+120));
		headY[2] = (int) (bodyFrontY-HEAD_SIZE*sinDegrees(angle+120));
		
		headX[3] = headFrontX;
		headY[3] = headFrontY;
		
		/*
		double thrustCos = cosDegrees(angle + 180);
		double thrustSin = sinDegrees(angle + 180);
		
		int thrustLineStartX = thrustCos
		*/
		g.drawPolygon(bodyX, bodyY, 4);
		g.drawPolygon(headX, headY, 4);
	}
	
	public void update()
	{
		angle = (int) (angle + rSpeed);
		
		double rSpeedAbs = Math.abs(rSpeed);
		if(rSpeedAbs > 0)
		{
			if(rSpeed < 0)
			{
				if(rSpeed < -ROTATION_MAX)
				{
					rSpeed = -ROTATION_MAX;
				}
				else
				{
					rSpeed = rSpeed + ROTATION_DECEL;
					if(rSpeed > 0)
					{
						rSpeed = 0;
					}
				}
			}
			else if(rSpeed > 0)
			{
				if(rSpeed > ROTATION_MAX)
				{
					rSpeed = ROTATION_MAX;
				}
				else
				{
					rSpeed = rSpeed - ROTATION_DECEL;
					if(rSpeed < 0)
					{
						rSpeed = 0;
					}
				}
			}
		}
		
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
	
	public void thrust()
	{
		accelerate(angle, THRUST);
	}
	
	public void setVel(int x, int y)
	{
		xSpeed = x;
		ySpeed = y;
	}
	
	public void setAngle(int newAngle)
	{
		angle = newAngle;
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
	
	public void fire()
	{
		
	}
	
	public void accelerate(int angle, double thrust)
	{
		xSpeed = (xSpeed + thrust*cosDegrees(angle));
		ySpeed = (ySpeed + thrust*sinDegrees(angle));
	}
	
	public void decelerate(double thrust)
	{
		int velAngle = (int) tanDegrees(ySpeed, xSpeed);
		double xSpeedOriginal = xSpeed;
		double ySpeedOriginal = ySpeed;
		
		xSpeed = (xSpeed + thrust*cosDegrees(velAngle+180));
		ySpeed = (ySpeed + thrust*sinDegrees(velAngle+180));
		
		if(Math.abs(xSpeed) > Math.abs(xSpeedOriginal))
		{
			xSpeed = 0;
		}
		if(Math.abs(ySpeed) > Math.abs(ySpeedOriginal))
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
