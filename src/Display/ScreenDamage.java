package Display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import Game.GameWindow;
import Interfaces.GameObject;
import Space.Helper;

public class ScreenDamage implements GameObject {
	private static ArrayList<Polygon> screenSections;
	static {
		screenSections = new ArrayList<Polygon>();
		Polygon screen = new Polygon();
		int width = GameWindow.SCREEN_WIDTH;
		int height = GameWindow.SCREEN_HEIGHT;
		int widthInterval = width/10;
		int heightInterval = height/10;
		
		//Add points along the borders of the screen
		for(int x = 0; x <= width; x += widthInterval) {
			screen.addPoint(x, 0);
		}
		for(int y = 0; y <= height; y += heightInterval) {
			screen.addPoint(width, y);
		}
		for(int x = width; x >= 0; x -= widthInterval) {
			screen.addPoint(x, height);
		}
		for(int y = height; y >= 0; y -= heightInterval) {
			screen.addPoint(0, y);
		}
		screenSections.add(screen);
		for(int i = 0; i < 1; i++) {
			screenSections = dividePolygons(screenSections);
		}
	}
	public static ArrayList<Polygon> dividePolygons(ArrayList<Polygon> polygons) {
		ArrayList<Polygon> dividedSections = new ArrayList<Polygon>();
		for(Polygon section : polygons) {
			int[] xPoints = section.xpoints;
			int[] yPoints = section.ypoints;
			int nPoints = section.npoints;
			int startIndex = (int) Helper.random(section.npoints);
			int startX = xPoints[startIndex];
			int startY = yPoints[startIndex];
			int endIndex = (int) Helper.random(section.npoints);
			int endX = xPoints[endIndex];
			int endY = yPoints[endIndex];
			Polygon left = new Polygon();
			Polygon right = new Polygon();
			
			left.addPoint(startX, startY);
			right.addPoint(startX, startY);
			
			for(int i = 0; i < section.npoints; i++) {
				int x = xPoints[(i + startIndex) % nPoints];
				int y = yPoints[(i + startIndex) % nPoints];
				double compare = (endX - startX)*(y - startY) - (endY - startY)*(x - startX);
				if(compare > 0) {
					right.addPoint(x, y);
				} else if(compare < 0) {
					left.addPoint(x, y);
				} else {
					System.out.println("Discard");
				}
			}
			left.addPoint(endX, endY);
			right.addPoint(endX, endY);
			
			left.addPoint(left.xpoints[0], left.ypoints[0]);
			right.addPoint(right.xpoints[0], right.ypoints[0]);
			dividedSections.add(left);
			dividedSections.add(right);
		}
		return dividedSections;
	}
	private static final TexturePaint[] snow;
	static {
		int count = 10;
		snow = new TexturePaint[count];
		for(int i = 0; i < count; i++) {
			BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			int[] pixels = ( (DataBufferInt) image.getRaster().getDataBuffer() ).getData();
			for(int j = 0; j < pixels.length; j++) {
				pixels[j] = new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256), 255).getRGB();
			}
			System.out.println(image);
			snow[i] = new TexturePaint(image, new Rectangle(0, 0, 16, 16));
		}
	}
	ArrayList<Point2D> points;
	ArrayList<Shape> effect;
	public ScreenDamage() {
		points = new ArrayList<Point2D>();
		effect = new ArrayList<Shape>();
	}
	public ScreenDamage(Point2D origin) {
		this();
		points.add(origin);
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
		for(int i = 0; i < 10; i++) {
			points.add(new Point2D.Double(GameWindow.randomGameWidth(), GameWindow.randomGameHeight()));
		}
		Polygon nextEffect = new Polygon();
		Point2D first = Helper.random(points);
		nextEffect.addPoint((int) first.getX(), (int) first.getY());
		for(Point2D p : points) {
			if(first.distance(p) < 200 && Math.random() < 1) {
				nextEffect.addPoint((int) p.getX(), (int) p.getY());
			}
		}
		nextEffect.addPoint((int) first.getX(), (int) first.getY());
		//nextEffect.addPoint(nextEffect.xpoints[0], nextEffect.ypoints[0]);
		effect.add(nextEffect);
		/*
		for(int i = 0; i < 3; i++) {
			Point2D p1 = Helper.random(points);
			Point2D p2 = Helper.random(points);
			effect.add(new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
		}
		*/
	}
	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		Graphics2D g2D = (Graphics2D) g;
		g2D.setPaint(snow[0]/*Helper.random(snow)*/);
		for(Shape s : screenSections) {
			g2D.fill(s);
		}
		
	}
}
