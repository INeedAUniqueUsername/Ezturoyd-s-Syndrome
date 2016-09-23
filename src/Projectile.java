import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;

public class Projectile extends Space_Object{
	
	int lifetime;
	int width = 10;
	Space_Object owner;
	
	public Projectile(double posX, double posY, double posR, int life)
	{
		xPos = posX;
		yPos = posY;
		rPos = posR;
		
		lifetime = life;
		
		updateBody();
		size = polygonArea(body.xpoints, body.ypoints, body.npoints);
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.RED);
		updateBody();
		g.drawPolygon(body);
	}
	
	public void update()
	{
		updatePosition();
		lifetime--;
		if(lifetime < 0)
		{
			active = false;
		}
	}
	
	public void setOwner(Space_Object object)
	{
		owner = object;
	}
	
	public int getLifetime()
	{
		return lifetime;
	}
	
	public void updateBody()
	{
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];
		
		int bodyFrontX = (int) 						(xPos+width*cosDegrees(rPos));
		int bodyFrontY = (int) (GameWindow.HEIGHT-	(yPos+width*sinDegrees(rPos)));
		
		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;
		
		bodyX[1] = (int) 						(xPos+width*cosDegrees(rPos-120));
		bodyY[1] = (int) (GameWindow.HEIGHT-	(yPos+width*sinDegrees(rPos-120)));
		
		bodyX[2] = (int) 						(xPos+width*cosDegrees(rPos+120));
		bodyY[2] = (int) (GameWindow.HEIGHT-	(yPos+width*sinDegrees(rPos+120)));
		
		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		body = new Polygon(bodyX, bodyY, 4);
	}
}
