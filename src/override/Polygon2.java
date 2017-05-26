package override;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Polygon2 extends Polygon {
	public Polygon2() {
		super();
	}
	public Polygon2(ArrayList<Point2D> points) {
		super();
		for(Point2D p : points) {
			addPoint(p);
		}
	}
	public void addPoint(Point2D point) {
		addPoint((int) point.getX(), (int) point.getY());
	}
	public void addPoint(Point2D point, int index) {
		npoints++;
		int[] xpoints_result = new int[npoints];
		int[] ypoints_result = new int[npoints];
		for(int i = 0; i < index; i++) {
			xpoints_result[i] = xpoints[i];
			ypoints_result[i] = ypoints[i];
		}
		xpoints[index] = (int) point.getX();
		ypoints[index] = (int) point.getY();
		for(int i = index; i < npoints; i++) {
			xpoints_result[i] = xpoints[i-1];
			ypoints_result[i] = ypoints[i-1];
		}
		xpoints = xpoints_result;
		ypoints = ypoints_result;
	}
	public Point[] getPoints() {
		Point[] result = new Point[npoints];
		for(int i = 0; i < npoints; i++) {
			result[i] = new Point(xpoints[i], ypoints[i]);
		}
		return result;
	}
	public Point getPoint(int index) {
		return new Point(xpoints[index], ypoints[index]);
	}
	public void setPoints(Point2D... points) {
		npoints = points.length;
		xpoints = new int[npoints];
		ypoints = new int[npoints];
		for(int i = 0; i < npoints; i++) {
			xpoints[i] = (int) points[i].getX();
			ypoints[i] = (int) points[i].getY();
		}
	}
	public void insertPointsAlongEdges(int interval) {
		for(int pointIndex = 0; pointIndex < npoints - 1; pointIndex++) {
			Point p1 = getPoint(pointIndex);
			int nextIndex = pointIndex+1;
			Point p2 = getPoint(nextIndex);
			double distance = p1.distance(p2);
			int insertCount = (int) (distance/interval);
			double xInterval = (p2.getX() - p1.getX()) / insertCount;
			double yInterval = (p2.getY() - p1.getY()) / insertCount;
			for(int i = 1; i < insertCount; i++) {
				addPoint(new Point((int) (p1.getX() + xInterval*i), (int) (p1.getY() + yInterval*i)), pointIndex+i);
			}
		}
	}
	
}
