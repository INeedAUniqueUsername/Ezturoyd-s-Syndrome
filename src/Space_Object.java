import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;

public class Space_Object {

	double xPos;
	double yPos;
	double rPos;
	
	double xVel;
	double yVel;
	double rVel;
	Polygon body;
	double size;
	/*	=	=	=	=		Setters			=	=	=	=	=*/
	public void setVelRectangular(double x, double y)
	{
		xVel = x;
		yVel = y;
	}
	
	public void setVelPolar(double angle, double speed)
	{
		setVelRectangular(speed*cosDegrees(angle), speed*sinDegrees(angle));
	}
	
	public void incVelRectangular(double x, double y)
	{
		setVelRectangular(getVelX() + x, getVelY() + y);
	}
	
	public void setAngle(int newAngle)
	{
		rPos = newAngle;
	}
	
	/*	=	=	=	=		Velocity		=	=	=	=	=*/
	
	public void accelerate(double angle, double thrust)
	{
		xVel = (xVel + thrust*cosDegrees(angle));
		yVel = (yVel + thrust*sinDegrees(angle));
	}
	
	public void decelerate(double thrust)
	{
		int velAngle = (int) arctanDegrees(yVel, xVel);
		double xSpeedOriginal = xVel;
		double ySpeedOriginal = yVel;
		
		xVel = (xVel + thrust*cosDegrees(velAngle+180));
		yVel = (yVel + thrust*sinDegrees(velAngle+180));
		
		if(Math.abs(xVel) > Math.abs(xSpeedOriginal))
		{
			xVel = 0;
		}
		if(Math.abs(yVel) > Math.abs(ySpeedOriginal))
		{
			yVel = 0;
		}
	}
	
	/*	=	=	=	=		Trigonometry		=	=	=	=	=*/
	
	public double cosDegrees (double angle)
	{
		return Math.cos(Math.toRadians(angle));
	}
	
	public double sinDegrees (double angle)
	{
		return Math.sin(Math.toRadians(angle));
	}
	
	public double arctanDegrees(double y, double x)
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
		System.out.println("X: " + y);
		System.out.println("Y: " + x);
		System.out.println("R: " + result);
		return result;
	}
	
	public void rotateLeft(int accel)
	{
		rVel = rVel + accel;
	}
	public void rotateRight(int accel)
	{
		rVel = rVel - accel;
	}
	
	public Polygon getBody() {
		return body;
	}
	
	public double random(double input)
	{
		return Math.random()*input;
	}
	
	public double randomMin(double minimum, double input)
	{
		return (minimum + Math.random()*(input - minimum));
	}
	
	public double scaleLinearUp(double input, double minFrom, double maxFrom, double minTo, double maxTo)
	{
		double rangeFrom = maxFrom - minFrom;
		double rangeTo = maxTo - minTo;
		double rangeRatio = rangeTo/rangeFrom;
		double inputDiff = input - minFrom;
		
		return minTo + inputDiff*rangeRatio;
	}
	
	public double getVelAngle()
	{
		return arctanDegrees(yVel, xVel);
	}
	
	public double getVelX()
	{
		return xVel;
	}
	
	public double getVelY()
	{
		return yVel;
	}
	
	public double getVelSpeed()
	{
		return Math.sqrt(Math.pow(xVel, 2) + Math.pow(yVel, 2));
	}
	
	public double getForce()
	{
		return getVelSpeed()*size;
	}
	
	public double getForceAngled(int angle)
	{
		double angleCW = Math.abs(rPos - angle);
		double angleCCW = Math.abs(angle - rPos);
		double angleDiff;
		if(angleCW < angleCCW)
		{
			angleDiff = angleCW;
		}
		else
		{
			angleDiff = angleCCW;
		}
		return getForce()*Math.cos(angleDiff);
	}
	
	//Source: http://www.mathopenref.com/coordpolygonarea2.html
	public double polygonArea(int[] X, int[] Y, int numPoints) 
	{ 
	  double area = 0;         // Accumulates area in the loop
	  int j = numPoints-1;  // The last vertex is the 'previous' one to the first

	  for (int i=0; i<numPoints; i++)
	    { area = area +  (X[j]+X[i]) * (Y[j]-Y[i]); 
	      j = i;  //j is previous vertex to i
	    }
	  return area/2;
	}
	
	public void update()
	{
		
	}
	
	public void updatePosition()
	{
		rPos = (int) (rPos + rVel);
		xPos = xPos + xVel;
		yPos = yPos + yVel;
		
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
	
	public double getPosX()
	{
		return xPos;
	}
	
	public double getPosY()
	{
		return yPos;
	}
	
	public double getPosR()
	{
		return rPos;
	}
	
}
