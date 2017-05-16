package body;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import space.Projectile;

public class Body_Projectile extends Body {
	private Projectile owner;
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
		int width = 2;	//3
		int height = 16;	//24
		
		setShapes(createRectangle(owner.getPos(), owner.getPosR(), width, height));
	}
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		super.draw(g);
	}
}
