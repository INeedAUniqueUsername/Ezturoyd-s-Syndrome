import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

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
	ArrayList<Polygon> body;
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
	
	public void impulse(double angle, double kinetic_energy)
	{
		incVelPolar(angle, Math.sqrt((2*kinetic_energy)/size));
	}
	
	public Point2D.Double calcFuturePos(double time)
	{
		return new Point2D.Double(pos_x + time * vel_x, pos_y + time * vel_y);
	}
	public static final Point2D.Double calcFuturePos(Point2D.Double origin, Point2D.Double vel, double time)
	{
		double angle = arctanDegrees(vel.getY(), vel.getX());
		double speed = Math.sqrt(Math.pow(vel.getX(), 2) + Math.pow(vel.getY(), 2));
		return polarOffset(origin, angle, speed * time);
	}
	public static final void drawArrow(Graphics g, Point2D.Double origin, Point2D.Double dest)
	{
		g.setColor(Color.GREEN);
		
		double x1 = origin.getX();
		double y1 = origin.getY();
		
		double x2 = dest.getX();
		double y2 = dest.getY();
		
		double angle = arctanDegrees(y2 - y1, x2 - x1);
		
		g.drawLine((int) x1, GameWindow.HEIGHT - (int) y1, (int) x2, GameWindow.HEIGHT - (int) y2);
		
		Point2D.Double arrow_left = polarOffset(dest, angle + 120, 10);
		g.drawLine((int) x2, GameWindow.HEIGHT - (int) y2, (int) arrow_left.getX(), GameWindow.HEIGHT - (int) arrow_left.getY());
		
		Point2D.Double arrow_right = polarOffset(dest, angle - 120, 10);
		g.drawLine((int) x2, GameWindow.HEIGHT - (int) y2, (int) arrow_right.getX(), GameWindow.HEIGHT - (int) arrow_right.getY());
	}
	
	public static final double calcFireSolution(Point2D.Double pos_diff, Point2D.Double vel_diff, double weapon_speed)
	{
		Point2D.Double origin = new Point2D.Double(0, 0);
		//Here is our initial estimate. If the target is moving, then by the time the shot reaches the target's original position, the target will be somnewhere else
		double time_to_hit_estimate = getDistanceBetweenPos(origin, pos_diff) / weapon_speed;
		Point2D.Double pos_diff_future = calcFuturePos(pos_diff, vel_diff, time_to_hit_estimate);
		double angle_to_hit_estimate = getAngleTowardsPos(origin, pos_diff_future);
		
		//System.out.println("Try 0");
		//System.out.println("Time to Hit: " + time_to_hit_estimate);
		
		//Calculate the time to hit the target at its new position, and then calculate where the target will be relative to its original position after the new estimated time.
		boolean active = true;
		double time_to_hit_old = 0;
		for(int i = 1; i < 10; i++)
		{
			double time_to_hit = getDistanceBetweenPos(origin, pos_diff_future) / weapon_speed;
			pos_diff_future = calcFuturePos(pos_diff, vel_diff, time_to_hit);
			
			//System.out.println("Try " + i);
			//System.out.println("Time to Hit: " + time_to_hit);
			
			if(Math.abs(time_to_hit - time_to_hit_old) < 1)
			{
				active = false;
				break;
			}
			time_to_hit_old = time_to_hit;
		}
		
		double angle_to_hit = getAngleTowardsPos(origin, pos_diff_future);
		System.out.println("Angle (Original): " + angle_to_hit_estimate);
		System.out.println("Angle (Actual): " + angle_to_hit);
		return angle_to_hit;
	}
	
	/*
	public Point2D.Double polarOffset(double x, double y, double angle, double distance)
	{
		return new Point2D.Double(x + distance * cosDegrees(angle), y - distance * sinDegrees(angle));
	}
	*/
	public static final Point2D.Double polarOffset(Point2D.Double origin, double angle, double distance)
	{
		return new Point2D.Double(origin.getX() + distance * cosDegrees(angle), origin.getY() + distance * sinDegrees(angle));
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
	
	public void accelerate(double angle, double speed)
	{
		vel_x = (vel_x + speed*cosDegrees(angle));
		vel_y = (vel_y + speed*sinDegrees(angle));
	}
	
	public void decelerate(double speed)
	{
		int velAngle = (int) arctanDegrees(vel_y, vel_x);
		int decelAngle = velAngle + 180;
		double xSpeedOriginal = vel_x;
		double ySpeedOriginal = vel_y;
		
		vel_x = (vel_x + speed*cosDegrees(decelAngle));
		vel_y = (vel_y + speed*sinDegrees(decelAngle));
		
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
		/*
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
		///*
		System.out.println("X: " + y);
		System.out.println("Y: " + x);
		System.out.println("arctan(y/x) = " + result);
		//*/
		double result = Math.toDegrees(Math.atan2(y, x));
		return result;
	}
	
	public void rotateLeft(double accel)
	{
		vel_r = vel_r + accel;
	}
	public void rotateRight(double accel)
	{
		vel_r = vel_r - accel;
	}
	
	public ArrayList<Polygon> getBody() {
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
	public void updateSize()
	{
		size = 0;
		for(Polygon part : body)
		{
			size += Math.abs(polygonArea(part.xpoints, part.ypoints, part.npoints));
		}
	}
	
	public void update()
	{
		
	}
	
	public void draw(Graphics g)
	{
		
	}
	public void drawBody(Graphics g)
	{
		for(Polygon part : body)
		{
			g.drawPolygon(part);
		}
	}
	
	public void destroy()
	{
		setActive(false);
	}
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean b) {
		active = b;
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
	public static double getAngleTowardsPos(Point2D.Double origin, Point2D.Double dest) {
		return arctanDegrees(dest.getY() - origin.getY(), dest.getX() - origin.getX());
	}
	
	public double getAngleFromPos(double x, double y)
	{
		return arctanDegrees(getPosY() - y, getPosX() - x);
	}
	public double getAngleFromPos(Point2D.Double origin, Point2D.Double dest) {
		return arctanDegrees(origin.getY() - dest.getY(), origin.getX() - dest.getX());
	}
	
	public double getAngleTowardsPos (Point2D.Double pos)
	{
		return arctanDegrees(pos.getY() - getPosY(), pos.getX() - getPosX());
	}
	public double getAngleFromPos(Point2D.Double pos)
	{
		return arctanDegrees(getPosY() - pos.getY(), getPosX() - pos.getX());
	}
	
	public double getDistanceBetween(Space_Object target)
	{
		return Math.sqrt(Math.pow((target.getPosX() - getPosX()), 2) + Math.pow((target.getPosY() - getPosY()), 2));
	}
	public double getDistanceBetweenPos(Point2D.Double pos)
	{
		return Math.sqrt(Math.pow(pos.getX() - getPosX(), 2) + Math.pow(pos.getY() - getPosY(), 2));		
	}
	public static double getDistanceBetweenPos(Point2D.Double origin, Point2D.Double dest)
	{
		return Math.sqrt(Math.pow(dest.getX() - origin.getX(), 2) + Math.pow(dest.getY() - origin.getY(), 2));		
	}
	public double getDistanceBetweenPos(double x, double y)
	{
		return Math.sqrt(Math.pow((x - getPosX()), 2) + Math.pow((y - getPosY()), 2));		
	}
	public double getDistanceBetweenPos(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));		
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
	
	public Point2D.Double getPos()
	{
		return new Point2D.Double(pos_x, pos_y);
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
	
	public Point2D.Double calcPolarOffset(double angle, double distance)
	{
		return new Point2D.Double(pos_x + distance * cosDegrees(angle), pos_y + distance * sinDegrees(angle));
	}
	
	public double getVelAngle()
	{
		if(!(vel_x == 0 && vel_y == 0))
		{
			return arctanDegrees(vel_y, vel_x);
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
		return (1/2)*getMass()*Math.pow(getVelSpeed(), 2);
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
