import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

public class SpaceObject {
	private String name = "";
	private final double c = 9131.35261864;
	
	protected double pos_x = 0; //Transition to get/set
	protected double pos_y = 0; //Transition to get/set
	protected double pos_r = 0; //Transition to get/set
	
	protected double vel_x = 0; //Transition to get/set
	protected double vel_y = 0; //Transition to get/set
	protected double vel_r = 0; //Transition to get/set
	protected ArrayList<Polygon> body; //Transition to private get/set
	private double size;
	
	private int last_collision_tick = 0;
	
	private boolean active = true;
	/*	=	=	=	=		Setters			=	=	=	=	=*/
	
	public final void setName(String name_new)
	{
		name = name_new;
	}
	public final String getName()
	{
		return name;
	}
	public final void printToWorld(String text)
	{
		if(name.equals(""))
		{
			GamePanel.world.printToScreen(text);
		}
		else
		{
			GamePanel.world.printToScreen("[" + getClass().getName() + "]" + " " + name + " - " + text);
		}
		
	}
	
	public final int factorialAddition(int input)
	{
		int result = 0;
		while(input > 0)
		{
			result += input;
			input--;
		}
		return result;
	}
	
	public final void impulse(double angle, double kinetic_energy)
	{
		incVelPolar(angle, Math.sqrt((2*kinetic_energy)/size));
	}
	
	public final Point2D.Double calcFuturePos(double time)
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
	public final Point2D.Double calcFireTargetPos(Point2D.Double pos_diff, Point2D.Double vel_diff, double weapon_speed) {
		Point2D.Double posDiff = calcFireTargetPosDiff(pos_diff, vel_diff, weapon_speed);
		return new Point2D.Double(getPosX() + posDiff.getX(), getPosY() + posDiff.getY());
	}
	public final double calcFireAngle(double target_pos_x, double target_pos_y, double target_vel_x, double target_vel_y, double projectile_speed) {
		return calcFireAngle(
				new Point2D.Double(
						target_pos_x - getPosX(),
						target_pos_y - getPosY()
						),
				new Point2D.Double(
						target_vel_x - getVelX(),
						target_vel_y - getVelY()
						),
				projectile_speed
				);
	}
	public final double calcFireDistance(double target_pos_x, double target_pos_y, double target_vel_x, double target_vel_y, double projectile_speed) {
		return calcFireDistance(
				new Point2D.Double(
						target_pos_x - getPosX(),
						target_pos_y - getPosY()
						),
				new Point2D.Double(
						target_vel_x - getVelX(),
						target_vel_y - getVelY()
						),
				projectile_speed
				);
	}
	public static final Point2D.Double calcFireTargetPosDiff(Point2D.Double pos_diff, Point2D.Double vel_diff, double weapon_speed) {
		Point2D.Double origin = new Point2D.Double(0, 0);
		//Here is our initial estimate. If the target is moving, then by the time the shot reaches the target's original position, the target will be somnewhere else
		double time_to_hit_estimate = getDistanceBetweenPos(origin, pos_diff) / weapon_speed;
		Point2D.Double pos_diff_future = calcFuturePos(pos_diff, vel_diff, time_to_hit_estimate);
		
		//System.out.println("Try 0");
		//System.out.println("Time to Hit: " + time_to_hit_estimate);
		
		double time_to_hit_old = 0;
		for(int i = 1; i < 10; i++)
		{
			double time_to_hit = getDistanceBetweenPos(origin, pos_diff_future) / weapon_speed;
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
		return getAngleTowardsPos(new Point2D.Double(0, 0), calcFireTargetPosDiff(pos_diff, vel_diff, weapon_speed));
	}
	
	public static final double calcFireDistance(Point2D.Double pos_diff, Point2D.Double vel_diff, double weapon_speed)
	{
		return getDistanceBetweenPos(new Point2D.Double(0, 0), calcFireTargetPosDiff(pos_diff, vel_diff, weapon_speed));
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
	
	public final void setPosRectangular(double x, double y)
	{
		pos_x = x;
		pos_y = y;
	}
	
	public final void setVelRectangular(double x, double y)
	{
		vel_x = x;
		vel_y = y;
	}
	
	public final void setVelPolar(double angle, double speed)
	{
		setVelRectangular(speed*cosDegrees(angle), speed*sinDegrees(angle));
	}
	
	public final void incVelRectangular(double x, double y)
	{
		setVelRectangular(getVelX() + x, getVelY() + y);
	}
	
	public final void incVelPolar(double angle, double speed)
	{
		setVelRectangular(getVelX() + speed*cosDegrees(angle), getVelY() + speed*sinDegrees(angle));
	}
	
	public final void setAngle(int newAngle)
	{
		pos_r = newAngle;
	}
	
	/*	=	=	=	=		Velocity		=	=	=	=	=*/
	
	public final void accelerate(double angle, double speed)
	{
		vel_x = (vel_x + speed*cosDegrees(angle));
		vel_y = (vel_y + speed*sinDegrees(angle));
	}
	
	public final void decelerate(double speed)
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
	
	public final static double cosDegrees (double angle)
	{
		return Math.cos(Math.toRadians(angle));
	}
	
	public final static double sinDegrees (double angle)
	{
		return Math.sin(Math.toRadians(angle));
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
	
	public final void rotateLeft(double accel)
	{
		vel_r = vel_r + accel;
	}
	public final void rotateRight(double accel)
	{
		vel_r = vel_r - accel;
	}
	
	public final ArrayList<Polygon> getBody() {
		return body;
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
	public final void updateSize()
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
	public final void drawBody(Graphics g)
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
	public final boolean getActive() {
		return active;
	}
	public final void setActive(boolean b) {
		active = b;
	}
	public final double getAngleTowards(SpaceObject other)
	{
		return getAngleTowardsPos(getPos(), other.getPos());
	}
	public final double getAngleFrom(SpaceObject other)
	{
		return getAngleFromPos(getPos(), other.getPos());
	}
	
	public final static double getAngleTowardsPos(Point2D.Double origin, Point2D.Double dest) {
		return arctanDegrees(dest.getY() - origin.getY(), dest.getX() - origin.getX());
	}
	
	public final static double getAngleFromPos(Point2D.Double origin, Point2D.Double dest) {
		return arctanDegrees(origin.getY() - dest.getY(), origin.getX() - dest.getX());
	}
	
	public final double getAngleTowardsPos (Point2D.Double pos)
	{
		return arctanDegrees(pos.getY() - getPosY(), pos.getX() - getPosX());
	}
	public final double getAngleFromPos(Point2D.Double pos)
	{
		return arctanDegrees(getPosY() - pos.getY(), getPosX() - pos.getX());
	}
	
	public final double getDistanceBetween(SpaceObject target)
	{
		return getDistanceBetweenPos(getPos(), target.getPos());
	}
	public final double getDistanceBetweenPos(Point2D.Double pos)
	{
		return getDistanceBetweenPos(getPos(), pos);		
	}
	public final static double getDistanceBetweenPos(Point2D.Double origin, Point2D.Double dest)
	{
		return getDistanceBetweenPos(origin.getX(), origin.getY(), dest.getX(), dest.getY());
	}
	public final static double getDistanceBetweenPos(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));		
	}
	
	public final void updatePosition()
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
	
	public final Point2D.Double getPos()
	{
		return new Point2D.Double(pos_x, pos_y);
	}
	
	public final double getPosX()
	{
		return pos_x;
	}
	
	public final double getPosY()
	{
		return pos_y;
	}
	
	public final double getPosR()
	{
		return pos_r;
	}
	
	public final Point2D.Double calcPolarOffset(double angle, double distance)
	{
		return new Point2D.Double(pos_x + distance * cosDegrees(angle), pos_y + distance * sinDegrees(angle));
	}
	
	public final double getVelAngle()
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
	public final double getVelAtAngle(double angle)
	{
		return getVelSpeed()*cosDegrees(getVelAngle() - angle);
	}
	public final double getVelX()
	{
		return vel_x;
	}
	
	public final double getVelY()
	{
		return vel_y;
	}
	
	public final double getVelR()
	{
		return vel_r;
	}
	
	public final double getVelSpeed()
	{
		return Math.sqrt(Math.pow(vel_x, 2) + Math.pow(vel_y, 2));
	}
	public final double getMass()
	{
		return size;
	}
	public final double getKineticEnergy()
	{
		//System.out.println("Speed: " + getVelSpeed());
		//System.out.println("Size: " + size);
		//System.out.println("Momentum: " + getVelSpeed()*size);
		return (1/2)*getMass()*Math.pow(getVelSpeed(), 2);
	}
	
	public final double getKineticEnergyAngled(double angle)
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
	
	public final void print(String message)
	{
		System.out.println(GamePanel.world.getTick() + ". " + message);
	}
	public final boolean exists(Object o)
	{
		return o != null;
	}
}
