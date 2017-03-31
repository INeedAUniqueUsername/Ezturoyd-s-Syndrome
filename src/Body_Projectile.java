import java.awt.Polygon;

public class Body_Projectile extends Body {
	Projectile owner;
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
		/*
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];

		int bodyFrontX = (int) (pos_x + height * cosDegrees(pos_r));
		int bodyFrontY = (int) (GameWindow.HEIGHT - (pos_y + height * sinDegrees(pos_r)));

		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;

		bodyX[1] = (int) (pos_x + width * cosDegrees(pos_r - 120));
		bodyY[1] = (int) (GameWindow.HEIGHT - (pos_y + width * sinDegrees(pos_r - 120)));

		bodyX[2] = (int) (pos_x + width * cosDegrees(pos_r + 120));
		bodyY[2] = (int) (GameWindow.HEIGHT - (pos_y + width * sinDegrees(pos_r + 120)));

		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		*/
		
		int width = 2;
		int height = 16;
		
		double pos_x = owner.getPosX();
		double pos_y = owner.getPosY();
		double pos_r = owner.getPosR();
		
		//Rectangle
		int[] bodyX = new int[5];
		int[] bodyY = new int[5];
		
		//Bottom Left
		bodyX[0] = (int) (pos_x - (width/2) * SpaceObject.cosDegrees(pos_r));
		bodyY[0] = (int) (GameWindow.HEIGHT - (pos_y - (height/2) * SpaceObject.sinDegrees(pos_r)));
		
		//Top left
		bodyX[1] = (int) (bodyX[0] + height * SpaceObject.cosDegrees(pos_r));
		bodyY[1] = (int) (bodyY[0] - height * SpaceObject.sinDegrees(pos_r));
		//Top right
		bodyX[2] = (int) (bodyX[1] + width * SpaceObject.cosDegrees(pos_r+90));
		bodyY[2] = (int) (bodyY[1] - width * SpaceObject.sinDegrees(pos_r+90));
		
		//Bottom right
		bodyX[3] = (int) (bodyX[0] + width * SpaceObject.cosDegrees(pos_r+90));
		bodyY[3] = (int) (bodyY[0] - width * SpaceObject.sinDegrees(pos_r+90));
		
		bodyX[4] = bodyX[0];
		bodyY[4] = bodyY[0];
		resetShapes();
		addShape(new Polygon(bodyX, bodyY, 5));
	}
}
