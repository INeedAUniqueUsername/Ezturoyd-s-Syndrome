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

public class Starship extends SpaceObject {

	final int HEAD_SIZE = 10; //20
	final int BODY_SIZE = 15; //30
	final double THRUST = .5; //1
	final int MAX_SPEED = 8;
	final double DECEL = .2;
	final double ROTATION_MAX = 15;
	final double ROTATION_ACCEL = .6;
	final double ROTATION_DECEL = .4;
	private boolean thrusting;
	private boolean turningCCW;
	private boolean turningCW;
	private boolean strafing;
	private boolean braking;
	private double structure = 1000;
	private ArrayList<String> print = new ArrayList<String>();

	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private ArrayList<Weapon_Key> weapons_key = new ArrayList<Weapon_Key>();
	private ArrayList<Weapon_Mouse> weapons_mouse = new ArrayList<Weapon_Mouse>();

	public Starship() {
		updateBody();
		updateSize();
	}

	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		drawStarship(g);
	}
	public final void drawStarship(Graphics g)
	{
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

	public void update() {
		double speed_r = Math.abs(vel_r);
		if (speed_r > 0)
			if(speed_r > ROTATION_MAX) {
				vel_r = (vel_r > 0 ? 1 : -1) * ROTATION_MAX;
			}
			else {
				double vel_r_original = vel_r;
				vel_r -= (vel_r > 0 ? 1 : -1)*ROTATION_DECEL;
				//Check if vel_r changed positive to negative or vice versa. If it did, then set it to zero
				if(vel_r / vel_r_original < 0) {
					vel_r = 0;
				}
			}
		if(thrusting)
			thrust();
		if(braking)
			brake();
		if(turningCCW)
			turnCCW();
		if(turningCW)
			turnCW();
		if (Math.sqrt(Math.pow(vel_x, 2) + Math.pow(vel_y, 2)) > MAX_SPEED) {
			int velAngle = (int) arctanDegrees(vel_y, vel_x);
			vel_x = MAX_SPEED * cosDegrees(velAngle);
			vel_y = MAX_SPEED * sinDegrees(velAngle);
		}
		updatePosition();
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
	
	public final void onAttacked(SpaceObject attacker)
	{
		
	}

	public final void thrust() {
		accelerate(pos_r, THRUST);
	}
	public final void turnCCW() {
		rotateLeft(ROTATION_ACCEL);
	}
	public final void turnCW() {
		rotateRight(ROTATION_ACCEL);
	}
	public final void strafeLeft() {
		accelerate(pos_r, THRUST);
	}
	public final void strafeRight() {
		accelerate(pos_r, THRUST);
	}
	public final void brake() {
		decelerate(DECEL);
	}
	
	public final void setThrusting(boolean b) {
		thrusting = b;
	}
	public final void setTurningCCW(boolean b)
	{
		turningCCW = b;
	}
	public final void setTurningCW(boolean b)
	{
		turningCW = b;
	}
	public final void setBraking(boolean b)
	{
		braking = b;
	}

	public final void setFiringKey(boolean firing)
	{
		for(Weapon_Key w: weapons_key)
		{
			w.setFiring(firing);
		}
	}
	public final void setFiringMouse(boolean firing)
	{
		for(Weapon_Mouse w: weapons_mouse)
		{
			w.setFiring(firing);
		}
	}
	public final void setStrafing(boolean enabled)
	{
		strafing = enabled;
	}
	public final boolean getStrafing()
	{
		return strafing;
	}

	public final void damage(double damage) {
		structure = structure - damage;
		if(structure < 0)
		{
			destroy();
		}
	}
	
	public final ArrayList<Weapon> getWeapon()
	{
		return weapons;
	}
	public final Weapon getWeaponPrimary()
	{
		return weapons.size() > 0 ? weapons.get(0) : null;
	}

	public final void setFiring(boolean state) {
		for (Weapon weapon : weapons) {
			weapon.setFiring(state);
		}
	}
	public final void setAimPos(double x, double y)
	{
		for(Weapon weapon: weapons)
		{
			weapon.setAimPos(x, y);
		}
			
	}

	public final void installWeapon(Weapon item) {
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
	
	public final void destroy()
	{
		for(Weapon w: weapons)
		{
			GamePanel.world.removeWeapon(w);
		}
		super.destroy();
	}
}
