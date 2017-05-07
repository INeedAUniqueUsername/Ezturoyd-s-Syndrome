import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Body_Weapon extends Body {
	private Weapon owner;
	public Body_Weapon(Weapon w) {
		super();
		setOwner(w);
	}
	public void setOwner(Weapon w) {
		owner = w;
	}
	public Weapon getOwner() {
		return owner;
	}
	public void updateShapes() {
		double pos_x = owner.getPosX();
		double pos_y = owner.getPosY();
		double fire_angle = owner.getFireAngle();
		final int SIZE = 5; //10
		
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];

		int bodyFrontX = (int) (pos_x + SIZE * SpaceObject.cosDegrees(fire_angle));
		int bodyFrontY = (int) (pos_y + SIZE * SpaceObject.sinDegrees(fire_angle));

		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;

		bodyX[1] = (int) (pos_x + SIZE * SpaceObject.cosDegrees(fire_angle - 120));
		bodyY[1] = (int) (pos_y + SIZE * SpaceObject.sinDegrees(fire_angle - 120));

		bodyX[2] = (int) (pos_x + SIZE * SpaceObject.cosDegrees(fire_angle + 120));
		bodyY[2] = (int) (pos_y + SIZE * SpaceObject.sinDegrees(fire_angle + 120));

		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		
		setShapes(new Polygon(bodyX, bodyY, 4));
	}
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		super.draw(g);
	}
}
