import java.awt.Graphics;

public class Body_StarshipExhaust extends Body_Projectile {
	public Body_StarshipExhaust(Projectile p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	public void updateShapes(Graphics g) {
		SpaceObject owner = getOwner();
		double pos_r = owner.getPosR();
		int height = 12;
		int width = 4;
		double angle = SpaceObject.arctanDegrees(height, width);
		double distance = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		setShapes(createPolygon(
				owner.polarOffset(pos_r+angle, distance),
				owner.polarOffset(pos_r-angle, distance),
				owner.polarOffset(pos_r+angle+180, distance),
				owner.polarOffset(pos_r-angle+180, distance),
				owner.polarOffset(pos_r+angle, distance)
				));
	}
}
