import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;

public class Projectile extends Space_Object{
	
	int lifetime;
	
	public Projectile(double posX, double posY, double posR, int life)
	{
		xPos = posX;
		yPos = posY;
		rPos = posR;
		
		lifetime = life;
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.RED);
		
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];
		
		int bodyFrontX = (int) 						(xPos+size*cosDegrees(rPos));
		int bodyFrontY = (int) (GameWindow.HEIGHT-	(yPos+size*sinDegrees(rPos)));
		
		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;
		
		bodyX[1] = (int) 						(xPos+size*cosDegrees(rPos-120));
		bodyY[1] = (int) (GameWindow.HEIGHT-	(yPos+size*sinDegrees(rPos-120)));
		
		bodyX[2] = (int) 						(xPos+size*cosDegrees(rPos+120));
		bodyY[2] = (int) (GameWindow.HEIGHT-	(yPos+size*sinDegrees(rPos+120)));
		
		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		body = new Polygon(bodyX, bodyY, 4);
		g.drawPolygon(body);
	}
	
	public void update()
	{
		updatePosition();
		lifetime--;
	}
	
	public int getLifetime()
	{
		return lifetime;
	}
}
