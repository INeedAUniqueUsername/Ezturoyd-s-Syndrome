package space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D.Double;

import body.Body;
import helpers.SpaceHelper;
import interfaces.GameObject;

public class BackgroundStar implements GameObject {
	int pos_x, pos_y;
	double drawAngle;
	int lineCount;
	public BackgroundStar() {
		this(0, 0, 0, 0);
	}
	public BackgroundStar(int x, int y, double angle, int lineCount) {
		setPos(x, y);
		setAngle(angle);
		setLineCount(lineCount);
	}
	public void setPos(int x, int y) {
		pos_x = x;
		pos_y = y;
	}
	public void setPosX(int x) {
		pos_x = x;
	}
	public void setPosY(int y) {
		pos_y = y;
	}
	public void setAngle(double angle) {
		drawAngle = angle;
	}
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		drawAngle = SpaceHelper.random(360);
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		Point pos = new Point(pos_x, pos_y);
		double angleInterval = 360 / lineCount;
		Polygon draw = new Polygon();
		//Add the center
		draw.addPoint(pos_x, pos_y);
		for(int i = 0; i < lineCount; i++) {
			double lineAngle = i * angleInterval + drawAngle;
			Double front = SpaceHelper.polarOffset(pos, lineAngle, 4);
			//Add the front point to the polygon
			draw.addPoint((int) front.getX(), (int) front.getY());
			Double back = SpaceHelper.polarOffset(pos, lineAngle+180, 2);
			//Add the back point to the polygon
			draw.addPoint((int) back.getX(), (int) back.getY());
			//Bring it back to the center
			draw.addPoint(pos_x, pos_y);
			g.setColor(Color.WHITE);
			g.drawPolygon(draw);
			Body.drawWrapClones(g, draw);
			
		}
	}
}
