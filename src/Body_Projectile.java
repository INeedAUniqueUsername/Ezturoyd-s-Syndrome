import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Body_Projectile extends Body {
	private Projectile owner;
	public Body_Projectile(Projectile p) {
		super();
		setOwner(p);
	}
	public void setOwner(Projectile p) {
		owner = p;
	}
	public Projectile getOwner() {
		return owner;
	}
	public void updateShapes() {
		int width = 2;	//3
		int height = 16;	//24
		
		double pos_x = owner.getPosX();
		double pos_y = owner.getPosY();
		double pos_r = owner.getPosR();
		
		//Rectangle
		int[] bodyX = new int[5];
		int[] bodyY = new int[5];
		
		//Bottom Left
		bodyX[0] = (int) (pos_x - (width/2) * SpaceObject.cosDegrees(pos_r));
		bodyY[0] = (int) (pos_y - (height/2) * SpaceObject.sinDegrees(pos_r));
		
		//Top left
		bodyX[1] = (int) (bodyX[0] + height * SpaceObject.cosDegrees(pos_r));
		bodyY[1] = (int) (bodyY[0] + height * SpaceObject.sinDegrees(pos_r));
		//Top right
		bodyX[2] = (int) (bodyX[1] + width * SpaceObject.cosDegrees(pos_r+90));
		bodyY[2] = (int) (bodyY[1] + width * SpaceObject.sinDegrees(pos_r+90));
		
		//Bottom right
		bodyX[3] = (int) (bodyX[0] + width * SpaceObject.cosDegrees(pos_r+90));
		bodyY[3] = (int) (bodyY[0] + width * SpaceObject.sinDegrees(pos_r+90));
		
		bodyX[4] = bodyX[0];
		bodyY[4] = bodyY[0];
		setShapes(new Polygon(bodyX, bodyY, 5));
	}
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		super.draw(g);
	}
}
