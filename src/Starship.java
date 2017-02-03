import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Starship extends Space_Object {

	final int HEAD_SIZE = 20;
	final int BODY_SIZE = 30;
	final int THRUST = 1; //1
	final int MAX_SPEED = 8;
	final double DECEL = .5;
	final int ROTATION_MAX = 15;
	final int ROTATION_ACCEL = 4;
	final double ROTATION_DECEL = .4;
	boolean thrusting;
	boolean strafing;
	double structure = 100;
	ArrayList<String> print = new ArrayList<String>();

	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	ArrayList<Weapon_Key> weapons_key = new ArrayList<Weapon_Key>();
	ArrayList<Weapon_Mouse> weapons_mouse = new ArrayList<Weapon_Mouse>();

	public Starship() {
		updateBody();
		updateSize();
	}

	public void draw(Graphics g) {
		drawStarship(g);
	}
	public void drawStarship(Graphics g)
	{
		g.setColor(Color.RED);
		updateBody();

		/*
		 * double thrustCos = cosDegrees(angle + 180); double thrustSin =
		 * sinDegrees(angle + 180);
		 * 
		 * int thrustLineStartX = thrustCos
		 */

		drawBody(g);
		//drawVel(g, headFrontX, headFrontY);
		
		//drawArrow(g, getPos(), polarOffset(getPos(), pos_r, 50));
		
		//printToWorld("Velocity Angle: " + getVelAngle());
	}
	/*
	public void drawVel(Graphics g, double x1, double y1)
	{
		g.setColor(Color.GREEN);
		double velAngle = getVelAngle();
		
		Point2D.Double arrow_head = polarOffset(x1, y1, velAngle, 20);
		double x2 = arrow_head.getX();
		double y2 = arrow_head.getY();
		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		
		Point2D.Double arrow_left = polarOffset(x2, y2, velAngle + 120, 10);
		g.drawLine((int) x2, (int) y2, (int) arrow_left.getX(), (int) arrow_left.getY());
		
		Point2D.Double arrow_right = polarOffset(x2, y2, velAngle - 120, 10);
		g.drawLine((int) x2, (int) y2, (int) arrow_right.getX(), (int) arrow_right.getY());
	}
	*/
	public void drawArrow(Graphics g, Point2D.Double origin, Point2D.Double dest)
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
	
	/*
	public Point2D.Double polarOffset(double x, double y, double angle, double distance)
	{
		return new Point2D.Double(x + distance * cosDegrees(angle), y - distance * sinDegrees(angle));
	}
	*/
	public Point2D.Double polarOffset(Point2D.Double origin, double angle, double distance)
	{
		return new Point2D.Double(origin.getX() + distance * cosDegrees(angle), origin.getY() + distance * sinDegrees(angle));
	}

	public void update() {
		updateSpaceship();
	}
	
	public void updateSpaceship()
	{
		double rSpeedAbs = Math.abs(vel_r);
		if (rSpeedAbs > 0) {
			if (vel_r < 0) {
				if (vel_r < -ROTATION_MAX) {
					vel_r = -ROTATION_MAX;
				} else {
					vel_r = vel_r + ROTATION_DECEL;
					if (vel_r > 0) {
						vel_r = 0;
					}
				}
			} else if (vel_r > 0) {
				if (vel_r > ROTATION_MAX) {
					vel_r = ROTATION_MAX;
				} else {
					vel_r = vel_r - ROTATION_DECEL;
					if (vel_r < 0) {
						vel_r = 0;
					}
				}
			}
		}
		updatePosition();
		if (Math.sqrt(Math.pow(vel_x, 2) + Math.pow(vel_y, 2)) > MAX_SPEED) {
			int velAngle = (int) arctanDegrees(vel_y, vel_x);
			vel_x = MAX_SPEED * cosDegrees(velAngle);
			vel_y = MAX_SPEED * sinDegrees(velAngle);
		}
		
	}

	public void updateBody() {
		int[] middleX = new int[4];
		int[] middleY = new int[4];

		int middleFrontX = (int) (pos_x + BODY_SIZE * cosDegrees(pos_r));
		int middleFrontY = (int) (GameWindow.HEIGHT - (pos_y + BODY_SIZE * sinDegrees(pos_r)));

		middleX[0] = middleFrontX;
		middleY[0] = middleFrontY;

		middleX[1] = (int) (pos_x + BODY_SIZE * cosDegrees(pos_r - 120));
		middleY[1] = (int) (GameWindow.HEIGHT - (pos_y + BODY_SIZE * sinDegrees(pos_r - 120)));

		middleX[2] = (int) (pos_x + BODY_SIZE * cosDegrees(pos_r + 120));
		middleY[2] = (int) (GameWindow.HEIGHT - (pos_y + BODY_SIZE * sinDegrees(pos_r + 120)));

		middleX[3] = middleFrontX;
		middleY[3] = middleFrontY;
		Polygon middle = new Polygon(middleX, middleY, 4);
		
		int[] headX = new int[4];
		int[] headY = new int[4];
		
		/*
		 * int topCornerX = (int) (xPos+SIZE*cosDegrees(angle)); int topCornerY
		 * = (int) (yPos+SIZE*sinDegrees(angle));
		 * 
		 * int bottomRightCornerX = (int) (xPos+SIZE*cosDegrees(angle-120)); int
		 * bottomRightCornerY = (int) (yPos+SIZE*sinDegrees(angle-120));
		 * 
		 * int bottomLeftCornerX = (int) (xPos+SIZE*cosDegrees(angle+120)); int
		 * bottomLeftCornerY = (int) (xPos+SIZE*sinDegrees(angle+120));
		 */

		int headFrontX = (int) (middleFrontX + HEAD_SIZE * cosDegrees(pos_r));
		int headFrontY = (int) (middleFrontY - HEAD_SIZE * sinDegrees(pos_r));

		headX[0] = headFrontX;
		headY[0] = headFrontY;

		headX[1] = (int) (middleFrontX + HEAD_SIZE * cosDegrees(pos_r - 120));
		headY[1] = (int) (middleFrontY - HEAD_SIZE * sinDegrees(pos_r - 120));

		headX[2] = (int) (middleFrontX + HEAD_SIZE * cosDegrees(pos_r + 120));
		headY[2] = (int) (middleFrontY - HEAD_SIZE * sinDegrees(pos_r + 120));

		headX[3] = headFrontX;
		headY[3] = headFrontY;
		
		Polygon head = new Polygon(headX, headY, 4);
		
		body = new ArrayList<Polygon>();
		body.add(middle);
		body.add(head);
	}
	
	public void onAttacked(Space_Object attacker)
	{
		
	}

	public void thrust() {
		accelerate(pos_r, THRUST);
	}
	public void turnCCW()
	{
		rotateLeft(ROTATION_ACCEL);
	}
	public void turnCW()
	{
		rotateRight(ROTATION_ACCEL);
	}
	public void strafeLeft()
	{
		accelerate(pos_r, THRUST);
	}
	public void strafeRight()
	{
		accelerate(pos_r, THRUST);
	}
	public void brake()
	{
		decelerate(DECEL);
	}

	public void setFiringKey(boolean firing)
	{
		for(Weapon_Key w: weapons_key)
		{
			w.setFiring(firing);
		}
	}
	public void setFiringMouse(boolean firing)
	{
		for(Weapon_Mouse w: weapons_mouse)
		{
			w.setFiring(firing);
		}
	}
	public void setStrafing(boolean enabled)
	{
		strafing = enabled;
	}
	public boolean getStrafing()
	{
		return strafing;
	}

	public void damage(double damage) {
		structure = structure - damage;
		if(structure < 0)
		{
			destroy();
		}
	}
	
	public ArrayList<Weapon> getWeapon()
	{
		return weapons;
	}
	public Weapon getWeaponPrimary()
	{
		return weapons.size() > 0 ? weapons.get(0) : null;
	}

	public void setFiring(boolean state) {
		for (Weapon weapon : weapons) {
			weapon.setFiring(state);
		}
	}
	public void setTargetPos(double x, double y)
	{
		for(Weapon weapon: weapons)
		{
			weapon.setTargetPos(x, y);
		}
			
	}

	public void installWeapon(Weapon item) {
		item.setOwner(this);
		weapons.add(item);
		if(item instanceof Weapon_Key)
		{
			weapons_key.add((Weapon_Key) item);
		}
		else if(item instanceof Weapon_Mouse)
		{
			weapons_mouse.add((Weapon_Mouse) item);
		}
		print("Installed Weapon");
	}
	
	public void destroy()
	{
		for(Weapon w: weapons)
		{
			world.removeWeapon(w);
		}
		world.removeStarship(this);
	}
}
