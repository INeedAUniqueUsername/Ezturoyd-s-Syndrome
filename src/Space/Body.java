package Space;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
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
	public void draw(Graphics g) {
		for(Polygon p : shapes) {
			g.drawPolygon(p);
			drawWrapClones(g, p);
		}
	}
	public final void drawWrapClones(Graphics g, Polygon p) {
		drawTranslate(g, p, GameWindow.WIDTH, 0);
		drawTranslate(g, p, -GameWindow.WIDTH, 0);
		drawTranslate(g, p, 0, GameWindow.HEIGHT);
		drawTranslate(g, p, 0, -GameWindow.HEIGHT);
	}
	public final void drawTranslate(Graphics g, Polygon p, int x, int y) {
		Polygon translated = new Polygon();
		int[] xPoints = p.xpoints;
		int[] yPoints = p.ypoints;
		int nPoints = p.npoints;
		for(int i = 0; i < nPoints; i++) {
			translated.addPoint(xPoints[i]+x, yPoints[i]+y);
		}
		//System.out.println("Drawing clone: " + "(" + x + ", " + y + ")");
		g.drawPolygon(translated);
	}
	public static Polygon createPolygon(Point2D.Double... points) {
		Polygon result = new Polygon();
		for(Point2D.Double p : points) {
			result.addPoint((int) p.getX(), (int) p.getY());
		}
		return result;
	}
	public static Polygon createRectangle(Point2D.Double pos, double facingAngle, double width, double height) {
		double angle = Helper.arctanDegrees(width, height);
		double distance = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		return createPolygon(
				Helper.polarOffset(pos, facingAngle + angle, distance), //Top left
				Helper.polarOffset(pos, facingAngle - angle, distance), //Top right
				Helper.polarOffset(pos, facingAngle + angle + 180, distance), //Bottom right
				Helper.polarOffset(pos, facingAngle - angle + 180, distance), //Bottom left
				Helper.polarOffset(pos, facingAngle + angle, distance) //Top left
				);
	}
}
