package Space;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

import Interfaces.NewtonianMotion;

public class Projectile extends SpaceObject {

	private int lifetime;
	private int damage;
	private NewtonianMotion owner;
	public Projectile() {
		lifetime = 0;
		damage = 0;
		setOwner(new Starship());
	}
	public Projectile(double posX, double posY, double posR, int damage, int life) {
		this(posX, posY, posR, damage, life, new Body());
		setBody(new Body_Projectile(this));
	}
	public Projectile(double posX, double posY, double posR, int damage, int life, Body body) {
		setPosRectangular(posX, posY);
		setPosR(posR);
		setLifetime(life);
		setDamage(damage);
		setBody(body);
		updateBody();
		updateSize();
	}
	public final void draw(Graphics g) {
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

	public final void setOwner(NewtonianMotion object) {
		owner = object;
	}
	public final NewtonianMotion getOwner()
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
