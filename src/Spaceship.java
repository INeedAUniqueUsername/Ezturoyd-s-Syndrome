import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

public class Spaceship extends Space_Object {

	final int HEAD_SIZE = 20;
	final int BODY_SIZE = 30;
	final int THRUST = 1;
	final int MAX_SPEED = 8;
	final double DECEL = .5;
	final int ROTATION_MAX = 15;
	final int ROTATION_ACCEL = 4;
	final double ROTATION_DECEL = .4;
	boolean thrusting;
	Polygon head;
	double structure = 100;

	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	ArrayList<Weapon_Key> weapons_key = new ArrayList<Weapon_Key>();
	ArrayList<Weapon_Mouse> weapons_mouse = new ArrayList<Weapon_Mouse>();

	public Spaceship() {
		updateBody();
		size = Math.abs(polygonArea(body.xpoints, body.ypoints, body.npoints));
	}

	public void draw(Graphics g) {
		g.setColor(Color.RED);
		updateBody();

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
		int bodyFrontX = body.xpoints[0];
		int bodyFrontY = body.ypoints[0];

		int headFrontX = (int) (bodyFrontX + HEAD_SIZE * cosDegrees(pos_r));
		int headFrontY = (int) (bodyFrontY - HEAD_SIZE * sinDegrees(pos_r));

		headX[0] = headFrontX;
		headY[0] = headFrontY;

		headX[1] = (int) (bodyFrontX + HEAD_SIZE * cosDegrees(pos_r - 120));
		headY[1] = (int) (bodyFrontY - HEAD_SIZE * sinDegrees(pos_r - 120));

		headX[2] = (int) (bodyFrontX + HEAD_SIZE * cosDegrees(pos_r + 120));
		headY[2] = (int) (bodyFrontY - HEAD_SIZE * sinDegrees(pos_r + 120));

		headX[3] = headFrontX;
		headY[3] = headFrontY;

		/*
		 * double thrustCos = cosDegrees(angle + 180); double thrustSin =
		 * sinDegrees(angle + 180);
		 * 
		 * int thrustLineStartX = thrustCos
		 */
		head = new Polygon(headX, headY, 4);

		g.drawPolygon(body);
		g.drawPolygon(head);
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
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];

		int bodyFrontX = (int) (pos_x + BODY_SIZE * cosDegrees(pos_r));
		int bodyFrontY = (int) (GameWindow.HEIGHT - (pos_y + BODY_SIZE * sinDegrees(pos_r)));

		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;

		bodyX[1] = (int) (pos_x + BODY_SIZE * cosDegrees(pos_r - 120));
		bodyY[1] = (int) (GameWindow.HEIGHT - (pos_y + BODY_SIZE * sinDegrees(pos_r - 120)));

		bodyX[2] = (int) (pos_x + BODY_SIZE * cosDegrees(pos_r + 120));
		bodyY[2] = (int) (GameWindow.HEIGHT - (pos_y + BODY_SIZE * sinDegrees(pos_r + 120)));

		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		body = new Polygon(bodyX, bodyY, 4);
	}

	public void thrust() {
		accelerate(pos_r, THRUST);
	}
	public void turnLeft()
	{
		rotateLeft(ROTATION_ACCEL);
	}
	public void turnRight()
	{
		rotateRight(ROTATION_ACCEL);
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

	public void damage(double damage) {
		structure = structure - damage;
	}

	public Polygon getHead() {
		return head;
	}

	public Polygon getBody() {
		return body;
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
	}
	public void installWeapon(Weapon_Key item) {
		item.setOwner(this);
		weapons.add(item);
		weapons_key.add(item);
	}
	public void installWeapon(Weapon_Mouse item) {
		item.setOwner(this);
		weapons.add(item);
		weapons_mouse.add(item);
	}
	
	public void destroy()
	{
		world.removeSpaceship(this);
	}
}
