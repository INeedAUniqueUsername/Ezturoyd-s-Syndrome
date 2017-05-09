package Space;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;


public class Helper {

	public static final void drawArrow(Graphics g, Point2D origin, Point2D dest)
	{
		g.setColor(Color.GREEN);
		
		double x1 = origin.getX();
		double y1 = origin.getY();
		
		double x2 = dest.getX();
		double y2 = dest.getY();
		
		double angle = Helper.arctanDegrees(y2 - y1, x2 - x1);
		
		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		
		Point2D.Double arrow_left = Helper.polarOffset(dest, angle + 120, 10);
		g.drawLine((int) x2, (int) y2, (int) arrow_left.getX(), (int) arrow_left.getY());
		
		Point2D.Double arrow_right = Helper.polarOffset(dest, angle - 120, 10);
		g.drawLine((int) x2, (int) y2, (int) arrow_right.getX(), (int) arrow_right.getY());
	}

	public static final Point2D.Double calcFuturePos(Point2D.Double origin, Point2D.Double vel, double time)
	{
		double angle = Helper.arctanDegrees(vel.getY(), vel.getX());
		double speed = Math.sqrt(Math.pow(vel.getX(), 2) + Math.pow(vel.getY(), 2));
		return Helper.polarOffset(origin, angle, speed * time);
	}

	public static final Point2D.Double calcFireTargetPosDiff(Point2D.Double pos_diff, Point2D.Double vel_diff, double weapon_speed) {
		Point2D.Double origin = new Point2D.Double(0, 0);
		//Here is our initial estimate. If the target is moving, then by the time the shot reaches the target's original position, the target will be somnewhere else
		double time_to_hit_estimate = Helper.getDistanceBetweenPos(origin, pos_diff) / weapon_speed;
		Point2D.Double pos_diff_future = calcFuturePos(pos_diff, vel_diff, time_to_hit_estimate);
		
		//System.out.println("Try 0");
		//System.out.println("Time to Hit: " + time_to_hit_estimate);
		
		double time_to_hit_old = 0;
		for(int i = 1; i < 10; i++)
		{
			double time_to_hit = Helper.getDistanceBetweenPos(origin, pos_diff_future) / weapon_speed;
			pos_diff_future = calcFuturePos(pos_diff, vel_diff, time_to_hit);
			
			//System.out.println("Try " + i);
			//System.out.println("Time to Hit: " + time_to_hit);
			
			if(Math.abs(time_to_hit - time_to_hit_old) < 1)
			{
				break;
			}
			time_to_hit_old = time_to_hit;
		}
		return pos_diff_future;
	}

	public static final double calcFireAngle(Point2D.Double pos_diff, Point2D.Double vel_diff, double weapon_speed)
	{
		return Helper.getAngleTowardsPos(new Point2D.Double(0, 0), calcFireTargetPosDiff(pos_diff, vel_diff, weapon_speed));
	}

	public static final Point2D.Double calcDiff(Point2D.Double origin, Point2D.Double destination) {
		return new Point2D.Double(destination.getX() - origin.getX(), destination.getY() - origin.getY());
	}

	public static final double calcFireDistance(Point2D.Double pos_diff, Point2D.Double vel_diff, double weapon_speed)
	{
		return Helper.getDistanceBetweenPos(new Point2D.Double(0, 0), calcFireTargetPosDiff(pos_diff, vel_diff, weapon_speed));
	}

	/*
	public Point2D.Double polarOffset(double x, double y, double angle, double distance)
	{
		return new Point2D.Double(x + distance * cosDegrees(angle), y - distance * sinDegrees(angle));
	}
	*/
	public static final Point2D.Double polarOffset(Point2D origin, double angle, double distance)
	{
		return new Point2D.Double(origin.getX() + distance * Helper.cosDegrees(angle), origin.getY() + distance * Helper.sinDegrees(angle));
	}

	public final static double cosDegrees (double angle)
	{
		return Math.cos(Math.toRadians(angle));
	}

	public final static double arctanDegrees(double y, double x)
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

	public final static double sinDegrees (double angle)
	{
		return Math.sin(Math.toRadians(angle));
	}

	public final static double modRange(double input, double range)
	{
		double result = input % range;
		while(result < 0)
		{
			result = result + range;
		}
		return result;
	}

	public final static double modRangeDegrees(double input)
	{
		return modRange(input, 360);
	}

	public final static double min(double number1, double number2)
	{
		return (number1 < number2 ? number1 : number2);
	}

	public final static double range(double input, double min, double max)
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

	public final static double random(double input)
	{
		return Math.random()*input;
	}

	public final static double randomMin(double minimum, double input)
	{
		return (minimum + Math.random()*(input - minimum));
	}

	public final static double scaleLinearUp(double input, double minFrom, double maxFrom, double minTo, double maxTo)
	{
		double rangeFrom = maxFrom - minFrom;
		double rangeTo = maxTo - minTo;
		double rangeRatio = rangeTo/rangeFrom;
		double inputDiff = input - minFrom;
		
		return minTo + inputDiff*rangeRatio;
	}

	//Source: http://www.mathopenref.com/coordpolygonarea2.html
	public final static double polygonArea(int[] X, int[] Y, int numPoints) 
	{ 
	  double area = 0;         // Accumulates area in the loop
	  int j = numPoints-1;  // The last vertex is the 'previous' one to the first
	
	  for (int i=0; i<numPoints; i++)
	    { area = area +  (X[j]+X[i]) * (Y[j]-Y[i]); 
	      j = i;  //j is previous vertex to i
	    }
	  return area/2;
	}

	public final static double getAngleTowardsPos(Point2D.Double origin, Point2D.Double dest) {
		return arctanDegrees(dest.getY() - origin.getY(), dest.getX() - origin.getX());
	}

	public final static double getAngleFromPos(Point2D.Double origin, Point2D.Double dest) {
		return arctanDegrees(origin.getY() - dest.getY(), origin.getX() - dest.getX());
	}

	public final static double getDistanceBetweenPos(Point2D.Double origin, Point2D.Double dest)
	{
		return Helper.getDistanceBetweenPos(origin.getX(), origin.getY(), dest.getX(), dest.getY());
	}

	public final static double getDistanceBetweenPos(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));		
	}

}
