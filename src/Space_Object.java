import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;

public class Space_Object {

	String name = "";
	final double c = 9131.35261864;
	
	static GamePanel world;
	
	double pos_x = 0;
	double pos_y = 0;
	double pos_r = 0;
	
	double vel_x = 0;
	double vel_y = 0;
	double vel_r = 0;
	Polygon body;
	double size;
	
	int lastCollisionTick = 0;
	
	boolean active = true;
	/*	=	=	=	=		Setters			=	=	=	=	=*/
	
	public void setName(String name_new)
	{
		name = name_new;
	}
	public String getName()
	{
		return name;
	}
	public void printToWorld(String text)
	{
		if(name.equals(""))
		{
			world.printToScreen(text);
		}
		else
		{
			world.printToScreen("[" + getClass().getName() + "]" + " " + name + " - " + text);
		}
		
	}
	
	public int factorialAddition(int input)
	{
		int result = 0;
		while(input > 0)
		{
			result += input;
			input--;
		}
		return result;
	}
	
	public void impulse(double angle, double momentum)
	{
		incVelPolar(angle, momentum/size);
	}
	
	public void setPosRectangular(double x, double y)
	{
		pos_x = x;
		pos_y = y;
	}
	
	public void setVelRectangular(double x, double y)
	{
		vel_x = x;
		vel_y = y;
	}
	
	public void setVelPolar(double angle, double speed)
	{
		setVelRectangular(speed*cosDegrees(angle), speed*sinDegrees(angle));
	}
	
	public void incVelRectangular(double x, double y)
	{
		setVelRectangular(getVelX() + x, getVelY() + y);
	}
	
	public void incVelPolar(double angle, double speed)
	{
		setVelRectangular(getVelX() + speed*cosDegrees(angle), getVelY() + speed*sinDegrees(angle));
	}
	
	public void setAngle(int newAngle)
	{
		pos_r = newAngle;
	}
	
	/*	=	=	=	=		Velocity		=	=	=	=	=*/
	
	public void accelerate(double angle, double thrust)
	{
		vel_x = (vel_x + thrust*cosDegrees(angle));
		vel_y = (vel_y + thrust*sinDegrees(angle));
	}
	
	public void decelerate(double magnitude)
	{
		int velAngle = (int) arctanDegrees(vel_y, vel_x);
		int decelAngle = velAngle + 180;
		double xSpeedOriginal = vel_x;
		double ySpeedOriginal = vel_y;
		
		vel_x = (vel_x + magnitude*cosDegrees(decelAngle));
		vel_y = (vel_y + magnitude*sinDegrees(decelAngle));
		
		if(Math.abs(vel_x) > Math.abs(xSpeedOriginal))
		{
			vel_x = 0;
		}
		if(Math.abs(vel_y) > Math.abs(ySpeedOriginal))
		{
			vel_y = 0;
		}
	}
	
	/*	=	=	=	=		Trigonometry		=	=	=	=	=*/
	
	public static double cosDegrees (double angle)
	{
		return Math.cos(Math.toRadians(angle));
	}
	
	public static double sinDegrees (double angle)
	{
		return Math.sin(Math.toRadians(angle));
	}
	
	public static double arctanDegrees(double y, double x)
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
		/*
		System.out.println("X: " + y);
		System.out.println("Y: " + x);
		System.out.println("arctan(y/x) = " + result);
		*/
		return result;
	}
	
	public void rotateLeft(int accel)
	{
		vel_r = vel_r + accel;
	}
	public void rotateRight(int accel)
	{
		vel_r = vel_r - accel;
	}
	
	public Polygon getBody() {
		return body;
	}
	
	public static double modRange(double input, double range)
	{
		double result = input % range;
		while(result < 0)
		{
			result = result + range;
		}
		return result;
	}
	public static double modRangeDegrees(double input)
	{
		return modRange(input, 360);
	}
	public static double min(double number1, double number2)
	{
		return (number1 < number2 ? number1 : number2);
	}
	public static double range(double input, double min, double max)
	{
		if(input > max)
		{
			return max;
		}
		else if(input < min)
		{
			return min;
		}
		else
		{
			return input;
		}
	}
	public static double random(double input)
	{
		return Math.random()*input;
	}
	
	public static double randomMin(double minimum, double input)
	{
		return (minimum + Math.random()*(input - minimum));
	}
	
	public static double scaleLinearUp(double input, double minFrom, double maxFrom, double minTo, double maxTo)
	{
		double rangeFrom = maxFrom - minFrom;
		double rangeTo = maxTo - minTo;
		double rangeRatio = rangeTo/rangeFrom;
		double inputDiff = input - minFrom;
		
		return minTo + inputDiff*rangeRatio;
	}
	
	//Source: http://www.mathopenref.com/coordpolygonarea2.html
	public static double polygonArea(int[] X, int[] Y, int numPoints) 
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
	
	public void draw(Graphics g)
	{
		
	}
	
	public void destroy()
	{
		
	}
	
	public double getAngleTowards(Space_Object other)
	{
		return arctanDegrees(other.getPosY() - getPosY(), other.getPosX() - getPosX());
	}
	public double getAngleFrom(Space_Object other)
	{
		return arctanDegrees(getPosY() - other.getPosY(), getPosX() - other.getPosX());
	}
	
	public double getAngleTowardsPos (double x, double y)
	{
		return arctanDegrees(y - getPosY(), x - getPosX());
	}
	
	public double getAngleFromPos(double x, double y)
	{
		return arctanDegrees(getPosY() - y, getPosX() - x);
	}
	
	public double getDistanceBetween(Space_Object target)
	{
		return Math.sqrt(Math.pow((target.getPosX() - getPosX()), 2) + Math.pow((target.getPosY() - getPosY()), 2));
	}
	
	public void updatePosition()
	{
		pos_r = (int) (pos_r + vel_r);
		pos_x = pos_x + vel_x;
		pos_y = pos_y + vel_y;
		
		if(pos_x < 0)
		{
			pos_x = GameWindow.WIDTH;
		}
		else if(pos_x > GameWindow.WIDTH)
		{
			pos_x = 0;
		}
		
		if(pos_y < 0)
		{
			pos_y = GameWindow.HEIGHT;
		}
		if(pos_y > GameWindow.HEIGHT)
		{
			pos_y = 0;
		}
		
	}
	
	public Point getPos()
	{
		Point result = new Point();
		result.setLocation(pos_x, pos_y);
		return result;
	}
	
	public double getPosX()
	{
		return pos_x;
	}
	
	public double getPosY()
	{
		return pos_y;
	}
	
	public double getPosR()
	{
		return pos_r;
	}
	
	public boolean getActive()
	{
		return active;
	}
	
	public double getVelAngle()
	{
		if(vel_x > 0)
		{
			return arctanDegrees(-vel_y, vel_x);
		}
		else
		{
			return pos_r;
		}
	}
	public double getVelRadial(double angle)
	{
		return getVelSpeed()*cosDegrees(getVelAngle() - angle);
	}
	public double getVelX()
	{
		return vel_x;
	}
	
	public double getVelY()
	{
		return vel_y;
	}
	
	public double getVelR()
	{
		return vel_r;
	}
	
	public double getVelSpeed()
	{
		return Math.sqrt(Math.pow(vel_x, 2) + Math.pow(vel_y, 2));
	}
	public double getMass()
	{
		return size;
	}
	public double getKineticEnergy()
	{
		//System.out.println("Speed: " + getVelSpeed());
		//System.out.println("Size: " + size);
		//System.out.println("Momentum: " + getVelSpeed()*size);
		return getVelSpeed()*getMass();
	}
	
	public double getKineticEnergyAngled(double angle)
	{
		/*
		double angleCW = Math.abs(pos_r - angle);
		double angleCCW = Math.abs(angle - pos_r);
		double angleDiff;
		if(angleCW < angleCCW)
		{
			angleDiff = angleCW;
		}
		else
		{
			angleDiff = angleCCW;
		}
		
		return getMomentum()*cosDegrees(angleDiff);
		*/
		return getKineticEnergy()*cosDegrees(getVelAngle()-angle);
	}
	public void print(String message)
	{
		System.out.println(world.tick + ". " + message);
	}
	
	public boolean exists(Object o)
	{
		return o != null;
	}
}
