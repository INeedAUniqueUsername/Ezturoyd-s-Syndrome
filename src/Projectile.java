import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

public class Projectile extends SpaceObject {

	private int lifetime;
	private int damage;
	private SpaceObject owner;
	private Color color;
	public Projectile() {
		lifetime = 0;
		damage = 0;
		setOwner(new Starship());
		setColor(new Color(0));
	}
	public Projectile(double posX, double posY, double posR, int damage, int life, Color color) {
		setPosRectangular(posX, posY);
		setPosR(posR);
		setColor(color);
		setLifetime(life);
		setDamage(damage);

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
	public final void setColor(Color c) {
		color = c;
	}
	public final Color getColor() {
		return color;
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
