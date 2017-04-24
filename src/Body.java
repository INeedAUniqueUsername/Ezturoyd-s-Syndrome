import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

public abstract class Body {
	private ArrayList<Polygon> shapes;
	public Body() {
		resetShapes();
	}
	public Body(ArrayList<Polygon> ss) {
		setShapes(ss);
	}
	public final void resetShapes() {
		setShapes(new ArrayList<Polygon>());
	}
	public final void setShapes(ArrayList<Polygon> ss) {
		shapes = ss;
	}
	public final ArrayList<Polygon> getShapes() {
		return shapes;
	}
	public final void addShape(Polygon s) {
		shapes.add(s);
	}
	public abstract void updateShapes();
	public final void draw(Graphics g) {
		for(Polygon s : shapes) {
			g.drawPolygon(s);
		}
	}
}
