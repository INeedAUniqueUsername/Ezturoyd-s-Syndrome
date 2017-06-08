package game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;
import javax.swing.JFrame;

import space.Starship;
import space.Weapon;
import factories.StarshipFactory;
import helpers.SpaceHelper;

public class GameWindow {
	public static final int SCREEN_WIDTH;
	public static final int SCREEN_HEIGHT;
	
	public static final int GAME_WIDTH;
	public static final int GAME_HEIGHT;
	
	static {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		SCREEN_WIDTH = (int) screenSize.getWidth();
		SCREEN_HEIGHT = (int) screenSize.getHeight();
		
		GAME_WIDTH = SCREEN_WIDTH*5;
		GAME_HEIGHT = SCREEN_HEIGHT*5;
	}
	
	public static final int SCREEN_CENTER_X = SCREEN_WIDTH/2;
	public static final int SCREEN_CENTER_Y = SCREEN_HEIGHT/2;
	public static final Point SCREEN_CENTER = new Point(SCREEN_CENTER_X, SCREEN_CENTER_Y);
	
	private GamePanel panel;
	private JFrame frame;
	public static void main(String[] args) {
		new GamePanel().newGame();
		
		
		Starship ship = StarshipFactory.createPlayership();
		
		for(int i = 0; i < 360; i++) {
			ship.setPos(0,0,i);
			ship.updateBody();
			Area area = new Area();
			for(Polygon p : ship.getBody().getShapes()) {
				area.add(new Area(p.getBounds()));
			}
			for(Weapon w : ship.getWeapon()) {
				w.setFireAngle(0);
				w.update();
				w.updateBody();
				for(Polygon p : w.getBody().getShapes()) {
					area.add(new Area(p.getBounds()));
				}
			}
			Rectangle bounds = area.getBounds();
			System.out.println("X: " + bounds.getX());
			System.out.println("Y: " + bounds.getY());
			System.out.println("W: " + bounds.getWidth());
			System.out.println("H: " + bounds.getHeight());
			BufferedImage result = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2D = (Graphics2D) result.getGraphics();
			g2D.setColor(Color.BLACK);
			g2D.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			g2D.translate(-bounds.x, -bounds.y);
			ship.draw(g2D);
			for(Weapon w : ship.getWeapon()) {
				w.draw(g2D);
			}
		}
		
		/*
		try {
			ImageIO.write(result, "png", new File("./Output.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		System.exit(0);
		
		
		
		// TODO Auto-generated method stub
		 /* Total number of processors or cores available to the JVM */
	    System.out.println("Available processors (cores): " + 
	        Runtime.getRuntime().availableProcessors());

	    /* Total amount of free memory available to the JVM */
	    System.out.println("Free memory (mb): " + 
	        Runtime.getRuntime().freeMemory()/1000000);

	    /* This will return Long.MAX_VALUE if there is no preset limit */
	    long maxMemory = Runtime.getRuntime().maxMemory();
	    /* Maximum amount of memory the JVM will attempt to use */
	    System.out.println("Maximum memory (mb): " + 
	        (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory/1000000));

	    /* Total memory currently available to the JVM */
	    System.out.println("Total memory available to JVM (mb): " + 
	        Runtime.getRuntime().totalMemory()/1000000);

	    /* Get a list of all filesystem roots on this system */
	    File[] roots = File.listRoots();

	    /* For each filesystem root, print some info */
	    for (File root : roots) {
	      System.out.println("File system root: " + root.getAbsolutePath());
	      System.out.println("Total space (mb): " + root.getTotalSpace()/1000000);
	      System.out.println("Free space (mb): " + root.getFreeSpace()/1000000);
	      System.out.println("Usable space (mb): " + root.getUsableSpace()/1000000);
	    }
		
		GameWindow game = new GameWindow();
		
	}
	public static int randomGameWidth() {
		return (int) SpaceHelper.random(GAME_WIDTH);
	}
	public static int randomGameHeight() {
		return (int) SpaceHelper.random(GAME_HEIGHT);
	}
	public static double randomScreenWidth() {
		return (int) SpaceHelper.random(SCREEN_WIDTH);
	}
	public static double randomScreenHeight() {
		return (int) SpaceHelper.random(SCREEN_HEIGHT);
	}
	public GameWindow()
	{
		frame = new JFrame();
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		//frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		panel = new GamePanel();
		frame.add(panel);
		frame.addMouseListener(panel);
		frame.addKeyListener(panel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setTitle("Ezturoyds Syndrome");
		
		frame.setVisible(true);
		
		panel.newGame();
	}
	
	public double scaleLinear(double input, double minFrom, double maxFrom, double minTo, double maxTo)
	{
		double rangeFrom = maxFrom - minFrom;
		double rangeTo = maxTo - minTo;
		
		double rangeRatio = rangeTo/rangeFrom;
		
		double inputDiff = input - minFrom;
		
		return minTo + inputDiff*rangeRatio;
	}

}
