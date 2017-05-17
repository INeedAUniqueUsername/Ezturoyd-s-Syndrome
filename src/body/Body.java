package body;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import game.GameWindow;
import helpers.SpaceHelper;
import override.Polygon2;

public class Body implements IBody {
	private ArrayList<Polygon> shapes;
	public Body() {
		setShapes();
	}
	public Body(ArrayList<Polygon> ss) {
		setShapes(ss);
	}
	/* (non-Javadoc)
	 * @see Space.IBody#setShapes(java.util.ArrayList)
	 */
	@Override
	public final void setShapes(ArrayList<Polygon> ss) {
		shapes = ss;
	}
	/* (non-Javadoc)
	 * @see Space.IBody#setShapes(java.awt.Polygon)
	 */
	@Override
	public final void setShapes(Polygon... pp) {
		shapes = new ArrayList<>();
		for(Polygon p : pp) {
			addShape(p);
		}
	}
	/* (non-Javadoc)
	 * @see Space.IBody#getShapes()
	 */
	@Override
	public final ArrayList<Polygon> getShapes() {
		return shapes;
	}
	/* (non-Javadoc)
	 * @see Space.IBody#addShape(java.awt.Polygon)
	 */
	@Override
	public final void addShape(Polygon s) {
		shapes.add(s);
	}
	/* (non-Javadoc)
	 * @see Space.IBody#updateShapes()
	 */
	@Override
	public void updateShapes(){}
	/* (non-Javadoc)
	 * @see Space.IBody#draw(java.awt.Graphics)
	 */
	@Override
	public void draw(Graphics g) {
		drawDefault(g);
	}
	public void drawDefault(Graphics g) {
		for(Polygon p : shapes) {
			g.drawPolygon(p);
			drawWrapClones(g, p);
		}
	}
	public static final void drawWrapClones(Graphics g, Polygon p) {
		//Edge cases
		drawTranslate(g, p, GameWindow.GAME_WIDTH, 0);
		drawTranslate(g, p, -GameWindow.GAME_WIDTH, 0);
		drawTranslate(g, p, 0, GameWindow.GAME_HEIGHT);
		drawTranslate(g, p, 0, -GameWindow.GAME_HEIGHT);
		
		//Corner cases
		//Right corners
		drawTranslate(g, p, GameWindow.GAME_WIDTH, GameWindow.GAME_HEIGHT);
		drawTranslate(g, p, GameWindow.GAME_WIDTH, -GameWindow.GAME_HEIGHT);
		
		//Left corners
		drawTranslate(g, p, -GameWindow.GAME_WIDTH, GameWindow.GAME_HEIGHT);
		drawTranslate(g, p, -GameWindow.GAME_WIDTH, -GameWindow.GAME_HEIGHT);
	}
	public static final void drawTranslate(Graphics g, Polygon p, int x, int y) {
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
		double angle = SpaceHelper.arctanDegrees(width, height);
		double distance = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		return createPolygon(
				SpaceHelper.polarOffset(pos, facingAngle + angle, distance), //Top left
				SpaceHelper.polarOffset(pos, facingAngle - angle, distance), //Top right
				SpaceHelper.polarOffset(pos, facingAngle + angle + 180, distance), //Bottom right
				SpaceHelper.polarOffset(pos, facingAngle - angle + 180, distance), //Bottom left
				SpaceHelper.polarOffset(pos, facingAngle + angle, distance) //Top left
				);
	}
	public static Polygon createTriangle(Point2D pos, double angle, double radius) {
		return createPolygon(
				SpaceHelper.polarOffset(pos, angle, radius),
				SpaceHelper.polarOffset(pos, angle+120, radius),
				SpaceHelper.polarOffset(pos, angle-120, radius),
				SpaceHelper.polarOffset(pos, angle, radius)
				);
	}
	public static Polygon createEllipse(Point2D pos, double angle, double width, double height) {
		Polygon2 result = new Polygon2();
		for(int i = 0; i <= 24; i++) {
			double rotation = i*15;
			result.addPoint(SpaceHelper.polarOffset(
					pos,
					rotation + angle,
					(width * height) / Math.sqrt(
							Math.pow(height, 2) * Math.pow(SpaceHelper.sinDegrees(rotation), 2) +
							Math.pow(width, 2) * Math.pow(SpaceHelper.cosDegrees(rotation), 2)
							)
					));
		}
		return result;
	}
}
