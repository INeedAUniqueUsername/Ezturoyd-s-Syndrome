import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

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
		
		double angle = SpaceObject.arctanDegrees(height, width);
		double distance = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		setShapes(createPolygon(
				owner.polarOffset(pos_r - angle, distance),			//Top right
				owner.polarOffset(pos_r + angle, distance),			//Top left
				owner.polarOffset(180 + (pos_r - angle), distance),	//Bottom left
				owner.polarOffset(180 + (pos_r + angle), distance)	//Bottom right
				));
	}
}
