import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

public class Projectile extends Space_Object {

	int lifetime;
	int width = 3;
	int height = 24;
	int damage;
	Space_Object owner;
	Color color = Color.RED;

	public Projectile(double posX, double posY, double posR, int damage, int life, int width, int height, Color color) {
		pos_x = posX;
		pos_y = posY;
		pos_r = posR;
		this.color = color;

		this.width = width;
		this.height = height;
		
		lifetime = life;
		this.damage = damage;
		
		initialize();
	}
	
	public Projectile(double posX, double posY, double posR, int damage, int life, Color color) {
		pos_x = posX;
		pos_y = posY;
		pos_r = posR;
		this.color = color;

		lifetime = life;
		this.damage = damage;

		initialize();
	}

	public void initialize()
	{
		System.out.println("Projectile X: " + pos_x);
		System.out.println("Projectile Y: " + pos_y);
		System.out.println("Projectile R: " + pos_r);
		
		updateBody();
		updateSize();
	}
	public void draw(Graphics g) {
		g.setColor(color);
		updateBody();
		drawBody(g);
	}

	public void update() {
		updatePosition();
		lifetime--;
		if (lifetime < 0) {
			destroy();
		}
	}

	public void setOwner(Space_Object object) {
		owner = object;
	}
	public Space_Object getOwner()
	{
		return owner;
	}

	public int getDamage() {
		return damage;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void updateBody() {
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
		
		//Rectangle
		int[] bodyX = new int[5];
		int[] bodyY = new int[5];
		
		//Bottom Left
		bodyX[0] = (int) (pos_x - (width/2) * cosDegrees(pos_r));
		bodyY[0] = (int) (GameWindow.HEIGHT - (pos_y - (height/2) * sinDegrees(pos_r)));
		
		//Top left
		bodyX[1] = (int) (bodyX[0] + height * cosDegrees(pos_r));
		bodyY[1] = (int) (bodyY[0] - height * sinDegrees(pos_r));
		//Top right
		bodyX[2] = (int) (bodyX[1] + width * cosDegrees(pos_r+90));
		bodyY[2] = (int) (bodyY[1] - width * sinDegrees(pos_r+90));
		
		//Bottom right
		bodyX[3] = (int) (bodyX[0] + width * cosDegrees(pos_r+90));
		bodyY[3] = (int) (bodyY[0] - width * sinDegrees(pos_r+90));
		
		bodyX[4] = bodyX[0];
		bodyY[4] = bodyY[0];
		body = new ArrayList<Polygon>();
		body.add(new Polygon(bodyX, bodyY, 5));
	}
	public void destroy()
	{
		world.removeProjectile(this);
	}
}
