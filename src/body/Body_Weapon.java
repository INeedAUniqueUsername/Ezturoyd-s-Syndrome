package body;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import helpers.SpaceHelper;
import space.Weapon;

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
		double fire_angle = owner.getFireAngle();
		final int SIZE = 5; //10
		
		setShapes(createTriangle(owner.getPos(), fire_angle, SIZE));
	}
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		drawDefault(g);
	}
}
