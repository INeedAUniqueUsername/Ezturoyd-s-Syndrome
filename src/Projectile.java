import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;

public class Projectile extends Space_Object {

	int lifetime;
	int width = 3;
	int damage;
	Space_Object owner;
	Color color = Color.RED;

	public Projectile(double posX, double posY, double posR, int damage, int life, Color color) {
		pos_x = posX;
		pos_y = posY;
		pos_r = posR;
		this.color = color;

		lifetime = life;
		this.damage = damage;

		updateBody();
		size = polygonArea(body.xpoints, body.ypoints, body.npoints);
	}

	public void draw(Graphics g) {
		g.setColor(color);
		updateBody();
		g.drawPolygon(body);
	}

	public void update() {
		updatePosition();
		lifetime--;
		if (lifetime < 0) {
			active = false;
		}
	}

	public void setOwner(Space_Object object) {
		owner = object;
	}

	public int getDamage() {
		return damage;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void updateBody() {
		int[] bodyX = new int[4];
		int[] bodyY = new int[4];

		int bodyFrontX = (int) (pos_x + width * cosDegrees(pos_r));
		int bodyFrontY = (int) (GameWindow.HEIGHT - (pos_y + width * sinDegrees(pos_r)));

		bodyX[0] = bodyFrontX;
		bodyY[0] = bodyFrontY;

		bodyX[1] = (int) (pos_x + width * cosDegrees(pos_r - 120));
		bodyY[1] = (int) (GameWindow.HEIGHT - (pos_y + width * sinDegrees(pos_r - 120)));

		bodyX[2] = (int) (pos_x + width * cosDegrees(pos_r + 120));
		bodyY[2] = (int) (GameWindow.HEIGHT - (pos_y + width * sinDegrees(pos_r + 120)));

		bodyX[3] = bodyFrontX;
		bodyY[3] = bodyFrontY;
		body = new Polygon(bodyX, bodyY, 4);
	}
}
