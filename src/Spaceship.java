import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

public class Spaceship extends Space_Object{
	
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
	int structure;
	
	ArrayList<Weapon> weapons = new ArrayList();
	
	public Spaceship(int x, int y)
	{
		xPos = x;
		yPos = y;
		
		xVel = 0;
		yVel = 0;
		
		rPos = 45;
		
		structure = 100;
		updateBody();
		size = polygonArea(body.xpoints, body.ypoints, body.npoints);
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.RED);
		updateBody();
		
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
		int bodyFrontX = body.xpoints[0];
		int bodyFrontY = body.ypoints[0];
		
		int headFrontX = (int) (bodyFrontX+HEAD_SIZE*cosDegrees(rPos));
		int headFrontY = (int) (bodyFrontY-HEAD_SIZE*sinDegrees(rPos));
		
		headX[0] = headFrontX;
		headY[0] = headFrontY;
		
		headX[1] = (int) (bodyFrontX+HEAD_SIZE*cosDegrees(rPos-120));
		headY[1] = (int) (bodyFrontY-HEAD_SIZE*sinDegrees(rPos-120));
		
		headX[2] = (int) (bodyFrontX+HEAD_SIZE*cosDegrees(rPos+120));
		headY[2] = (int) (bodyFrontY-HEAD_SIZE*sinDegrees(rPos+120));
		
		headX[3] = headFrontX;
		headY[3] = headFrontY;
		
		/*
		double thrustCos = cosDegrees(angle + 180);
		double thrustSin = sinDegrees(angle + 180);
		
		int thrustLineStartX = thrustCos
		*/
		head = new Polygon(headX, headY, 4);
		
		g.drawPolygon(body);
		g.drawPolygon(head);
	}
	
	public void update()
	{	
		double rSpeedAbs = Math.abs(rVel);
		if(rSpeedAbs > 0)
		{
			if(rVel < 0)
			{
				if(rVel < -ROTATION_MAX)
				{
					rVel = -ROTATION_MAX;
				}
				else
				{
					rVel = rVel + ROTATION_DECEL;
					if(rVel > 0)
					{
						rVel = 0;
					}
				}
			}
			else if(rVel > 0)
			{
				if(rVel > ROTATION_MAX)
				{
					rVel = ROTATION_MAX;
				}
				else
				{
					rVel = rVel - ROTATION_DECEL;
					if(rVel < 0)
					{
						rVel = 0;
					}
				}
			}
		}
		updatePosition();
		if(Math.sqrt(Math.pow(xVel, 2) + Math.pow(yVel, 2)) > MAX_SPEED)
		{
			int velAngle = (int) arctanDegrees(yVel, xVel);
			xVel = MAX_SPEED*cosDegrees(velAngle);
			yVel = MAX_SPEED*sinDegrees(velAngle);
			
		}
	}
	
	public void updateBody()
	{
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];
		
		int bodyFrontX = (int) 						(xPos+BODY_SIZE*cosDegrees(rPos));
		int bodyFrontY = (int) (GameWindow.HEIGHT-	(yPos+BODY_SIZE*sinDegrees(rPos)));
		
		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;
		
		bodyX[1] = (int) 						(xPos+BODY_SIZE*cosDegrees(rPos-120));
		bodyY[1] = (int) (GameWindow.HEIGHT-	(yPos+BODY_SIZE*sinDegrees(rPos-120)));
		
		bodyX[2] = (int) 						(xPos+BODY_SIZE*cosDegrees(rPos+120));
		bodyY[2] = (int) (GameWindow.HEIGHT-	(yPos+BODY_SIZE*sinDegrees(rPos+120)));
		
		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		body = new Polygon(bodyX,bodyY, 4);
	}
	
	public void thrust()
	{
		accelerate(rPos, THRUST);
	}
	
	public void damage(int damage)
	{
		structure = structure - damage;
	}
	
	public Polygon getHead() {
		return head;
	}
	public Polygon getBody() {
		return body;
	}
	
	public void setFiring(boolean state)
	{
		for(Weapon weapon: weapons)
		{
			weapon.setFiring(state);
		}
	}
	
	public void installWeapon(Weapon item)
	{
		weapons.add(item);
	}
}
