package Space;
import java.awt.Color;
import java.awt.Graphics;

public class Body_StarshipExhaust extends Body {
	private Projectile owner;
	public Body_StarshipExhaust(Projectile p) {
		super();
		setOwner(p);
		// TODO Auto-generated constructor stub
	}
	public Projectile getOwner() {
		return owner;
	}
	public void setOwner(Projectile p) {
		owner = p;
	}
	public void updateShapes() {
		SpaceObject owner = getOwner();
		setShapes(createRectangle(owner.getPos(), owner.getPosR(), 12, 2));
	}
	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		System.out.println("Starship Exhaust");
		super.draw(g);
	}
}
