import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

public class Projectile extends SpaceObject {

	private int lifetime;
	private int width = 2; //3
	private int height = 16; //24
	private int damage;
	private SpaceObject owner;
	private Color color = Color.RED;

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
		setPosRectangular(posX, posY);
		setPosR(posR);
		this.color = color;

		lifetime = life;
		this.damage = damage;

		initialize();
	}

	public final void initialize()
	{
		System.out.println("Projectile X: " + pos_x);
		System.out.println("Projectile Y: " + pos_y);
		System.out.println("Projectile R: " + pos_r);
		setBody(new Body_Projectile(this));
		updateBody();
		updateSize();
	}
	public final void draw(Graphics g) {
		g.setColor(color);
		updateBody();
		drawBody(g);
	}

	public void update() {
		if(getActive()) {
			updatePosition();
			lifetime--;
			if (lifetime < 0) {
				destroy();
			}
		}
	}

	public final void setOwner(SpaceObject object) {
		owner = object;
	}
	public final SpaceObject getOwner()
	{
		return owner;
	}
	public final void setDamage(int d) {
		damage = d;
	}
	public final int getDamage() {
		return damage;
	}
	public final void setLifetime(int lt) {
		lifetime = lt;
	}
	public final int getLifetime() {
		return lifetime;
	}
	public final void damage(int amount) {
		damage -= amount;
	}
}
