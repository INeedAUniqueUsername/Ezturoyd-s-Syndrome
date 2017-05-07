import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Body_StarshipExhaust extends Body_Projectile {
	public Body_StarshipExhaust(Projectile p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	public void updateShapes() {
		SpaceObject owner = getOwner();
		setShapes(createRectangle(owner.getPos(), owner.getPosR(), 12, 4));
	}
	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		for(Polygon p : getShapes()) {
			g.drawPolygon(p);
		}
	}
}
