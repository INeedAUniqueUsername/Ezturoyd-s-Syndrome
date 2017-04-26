import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

public class Body {
	private ArrayList<Polygon> shapes;
	public Body() {
		setShapes();
	}
	public Body(ArrayList<Polygon> ss) {
		setShapes(ss);
	}
	public final void setShapes(ArrayList<Polygon> ss) {
		shapes = ss;
	}
	public final void setShapes(Polygon... pp) {
		shapes = new ArrayList<>();
		for(Polygon p : pp) {
			addShape(p);
		}
	}
	public final ArrayList<Polygon> getShapes() {
		return shapes;
	}
	public final void addShape(Polygon s) {
		shapes.add(s);
	}
	public void updateShapes(){}
	public final void draw(Graphics g) {
		for(Polygon s : shapes) {
			g.drawPolygon(s);
		}
	}
}
