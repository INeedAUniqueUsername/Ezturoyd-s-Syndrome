import java.awt.Color;
import java.awt.Graphics;

public class Spaceship {
	
	final int SIZE = 30;
	final int THRUST = 5;
	int xPos;
	int yPos;
	int xVel;
	int yVel;
	int angle;
	boolean thrusting;
	
	public Spaceship(int x, int y)
	{
		xPos = x;
		yPos = y;
		
		xVel = 0;
		yVel = 0;
		angle = 45;
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.WHITE);
		
		int[] cornersX = new int[4];
		int[] cornersY = new int[4];
		/*
		int topCornerX = (int) (xPos+SIZE*cosDegrees(angle));
		int topCornerY = (int) (yPos+SIZE*sinDegrees(angle));
		
		int bottomRightCornerX = (int) (xPos+SIZE*cosDegrees(angle-120));
		int bottomRightCornerY = (int) (yPos+SIZE*sinDegrees(angle-120));
		
		int bottomLeftCornerX = (int) (xPos+SIZE*cosDegrees(angle+120));
		int bottomLeftCornerY = (int) (xPos+SIZE*sinDegrees(angle+120));
		*/
		
		cornersX[0] = (int) (xPos+SIZE*cosDegrees(angle));
		cornersY[0] = (int) (900-(yPos+SIZE*sinDegrees(angle)));
		
		cornersX[1] = (int) (xPos+SIZE*cosDegrees(angle-120));
		cornersY[1] = (int) (900-(yPos+SIZE*sinDegrees(angle-120)));
		
		cornersX[2] = (int) (xPos+SIZE*cosDegrees(angle+120));
		cornersY[2] = (int) (900-(yPos+SIZE*sinDegrees(angle+120)));
		
		cornersX[3] = cornersX[0];
		cornersY[3] = cornersY[0];
		
		/*
		double thrustCos = cosDegrees(angle + 180);
		double thrustSin = sinDegrees(angle + 180);
		
		int thrustLineStartX = thrustCos
		*/
		g.drawPolyline(cornersX, cornersY, 4);
	}
	
	public void update()
	{
		if(thrusting)
		{
			accelerate(angle, THRUST);
		}
		xPos = (int) (xPos + xVel*cosDegrees(angle));
		yPos = (int) (yPos + yVel*sinDegrees(angle));
	}
	
	public void setVel(int x, int y)
	{
		xVel = x;
		yVel = y;
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
	
	public void setThrusting(boolean mode)
	{
		thrusting = mode;
	}
	
	public void accelerate(int angle, int thrust)
	{
		xVel = (int) (xVel + thrust*cosDegrees(angle));
		yVel = (int) (yVel + thrust*sinDegrees(angle));
	}
	
	public void decelerate(int thrust)
	{
		xVel = (int) (xVel - thrust*cosDegrees(angle));
		yVel = (int) (yVel - thrust*sinDegrees(angle));
		
		if(xVel < 0)
		{
			xVel = 0;
		}
		if(yVel < 0)
		{
			yVel = 0;
		}
	}
	
	public void rotateLeft(int change)
	{
		angle = angle + change;
	}
	
	public void rotateRight(int change)
	{
		angle = angle - change;
	}
	
}
