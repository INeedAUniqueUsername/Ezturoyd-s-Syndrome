package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import body.Body_StarshipExhaust;
import factories.StarshipFactory;
import helpers.SpaceHelper;
import space.Projectile;
import space.SpaceObject;
import space.Starship;
import space.Starship_NPC;
import space.Weapon;

public class GameWindow implements Runnable {
	public static final int SCREEN_WIDTH;
	public static final int SCREEN_HEIGHT;

	public static final int GAME_WIDTH = 3000 + (int) (Math.random() * 1500);
	public static final int GAME_HEIGHT = 2000 + (int) (Math.random() * 1000);

	static {
		
		//https://stackoverflow.com/questions/32555329/java-get-maximized-state-window-size
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		Rectangle bounds = gd.getDefaultConfiguration().getBounds();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gd.getDefaultConfiguration());

		Rectangle safeBounds = new Rectangle(bounds);
		safeBounds.x += insets.left;
		safeBounds.y += insets.top;
		safeBounds.width -= (insets.left + insets.right);
		safeBounds.height -= (insets.top + insets.bottom);
		
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenSize = safeBounds.getBounds();
		SCREEN_WIDTH = (int) screenSize.getWidth();
		SCREEN_HEIGHT = (int) screenSize.getHeight();
	}

	public static final int SCREEN_CENTER_X = SCREEN_WIDTH / 2;
	public static final int SCREEN_CENTER_Y = SCREEN_HEIGHT / 2;
	public static final Point SCREEN_CENTER = new Point(SCREEN_CENTER_X, SCREEN_CENTER_Y);

	private JFrame frame;

	public static void generateSprite(SpaceObject o, String name) {
		o.updateBody();
		Area area = new Area();
		for (Polygon p : o.getBody().getShapes()) {
			area.add(new Area(p.getBounds()));
		}
		if (o instanceof Starship) {
			for (Weapon w : ((Starship) o).getWeapon()) {
				w.update();
				w.updateBody();
				for (Polygon p : w.getBody().getShapes()) {
					area.add(new Area(p.getBounds()));
				}
			}
		}

		Rectangle bounds = area.getBounds();
		System.out.println("X: " + bounds.getX());
		System.out.println("Y: " + bounds.getY());
		System.out.println("W: " + bounds.getWidth());
		System.out.println("H: " + bounds.getHeight());
		int scale = 25;
		int width = bounds.width * scale;
		int height = bounds.height * scale;
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = (Graphics2D) result.getGraphics();
		g2D.setBackground(new Color(0, 0, 0, 255));
		g2D.clearRect(0, 0, width, height);
		g2D.translate(-bounds.x * scale, -bounds.y * scale);
		g2D.scale(scale, scale);
		o.draw(g2D);
		if (o instanceof Starship) {
			for (Weapon w : ((Starship) o).getWeapon()) {
				w.draw(g2D);
			}
		}
		writeImage(result, name);
	}

	public static void writeImage(BufferedImage image, String name) {
		try {
			ImageIO.write(image, "png", new File("./" + name + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new GamePanel().newGame();
		/*
		SpaceObject player = StarshipFactory.createPlayership();
		player.setPosR(0);
		generateSprite(player, "Courier v3");
		*/
		/*
		SpaceObject laser = StarshipFactory.createPlayership().getWeapon().get(0).getShotType();
		laser.setPosR(-30);
		generateSprite(laser, "Player Laser");
		*/
		/*
		Starship_NPC enemy = new Starship_NPC();
		enemy.setPos(0, 0, 0);
		enemy.installWeapon(new Weapon(0, 0, 0, 0, 0, 0, 0));
		generateSprite(enemy, "Courier v2 (Hostile)");
		*/
		/*
		Projectile exhaust = new Projectile(0, 0, 0, 5, 10);
		exhaust.setBody(new Body_StarshipExhaust(exhaust));
		generateSprite(exhaust, "Exhaust");
		*/
		/*
		Projectile exhaust2 = new Projectile(0, 0, 45, 5, 10);
		exhaust2.setBody(new Body_StarshipExhaust(exhaust2));
		generateSprite(exhaust2, "Exhaust Enemy");
		*/
		/*
		 * Starship ship = StarshipFactory.createPlayership();
		 * 
		 * for(int i = 0; i < 1; i++) { ship.setPos(0,0,i); ship.updateBody();
		 * Area area = new Area(); for(Polygon p : ship.getBody().getShapes()) {
		 * area.add(new Area(p.getBounds())); } for(Weapon w : ship.getWeapon())
		 * { w.setFireAngle(0); w.update(); w.updateBody(); for(Polygon p :
		 * w.getBody().getShapes()) { area.add(new Area(p.getBounds())); } }
		 * Rectangle bounds = area.getBounds(); System.out.println("X: " +
		 * bounds.getX()); System.out.println("Y: " + bounds.getY());
		 * System.out.println("W: " + bounds.getWidth()); System.out.println(
		 * "H: " + bounds.getHeight()); int scale = 200; int width =
		 * bounds.width * scale; int height = bounds.height * scale;
		 * BufferedImage result = new BufferedImage(width, height,
		 * BufferedImage.TYPE_INT_ARGB); Graphics2D g2D = (Graphics2D)
		 * result.getGraphics(); g2D.setColor(Color.BLACK); g2D.fillRect(0, 0,
		 * width, height); g2D.translate(-bounds.x*scale, -bounds.y*scale);
		 * g2D.scale(scale, scale); ship.draw(g2D); for(Weapon w :
		 * ship.getWeapon().subList(0, 1)) { w.draw(g2D); } try {
		 * ImageIO.write(result, "png", new File("./Output.png")); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */

		/*
		 * try { ImageIO.write(result, "png", new File("./Output.png")); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		//System.exit(0);

		// TODO Auto-generated method stub
		/* Total number of processors or cores available to the JVM */
		System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

		/* Total amount of free memory available to the JVM */
		System.out.println("Free memory (mb): " + Runtime.getRuntime().freeMemory() / 1000000);

		/* This will return Long.MAX_VALUE if there is no preset limit */
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		System.out.println("Maximum memory (mb): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / 1000000));

		/* Total memory currently available to the JVM */
		System.out.println("Total memory available to JVM (mb): " + Runtime.getRuntime().totalMemory() / 1000000);
		/* Get a list of all filesystem roots on this system */
		File[] roots = File.listRoots();

		/* For each filesystem root, print some info */
		for (File root : roots) {
			System.out.println("File system root: " + root.getAbsolutePath());
			System.out.println("Total space (mb): " + root.getTotalSpace() / 1000000);
			System.out.println("Free space (mb): " + root.getFreeSpace() / 1000000);
			System.out.println("Usable space (mb): " + root.getUsableSpace() / 1000000);
		}
		SwingUtilities.invokeLater(new GameWindow());
		//GameWindow game = new GameWindow();

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

	public GameWindow() {
		frame = new JFrame();
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		// frame.setExtendedState(frame.getExtendedState() |
		// JFrame.MAXIMIZED_BOTH);
		frame.setBackground(Color.BLACK);
		frame.add(new InstructionPanel());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setTitle("Super Nostalgia Entertainment Syndrome");
		frame.requestFocus();
		frame.setVisible(true);
	}

	public double scaleLinear(double input, double minFrom, double maxFrom, double minTo, double maxTo) {
		double rangeFrom = maxFrom - minFrom;
		double rangeTo = maxTo - minTo;

		double rangeRatio = rangeTo / rangeFrom;

		double inputDiff = input - minFrom;

		return minTo + inputDiff * rangeRatio;
	}
	class InstructionPanel extends JPanel implements KeyListener, MouseListener{
		String FORMAT = "\t%-24s%s";
		int printY;
		final int PRINT_X = 256;
		final int FONT_SIZE = 27;
		{
			frame.addMouseListener(this);
			frame.addKeyListener(this);
			
			setBackground(Color.BLACK);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JLabel title = new JLabel("Super Nostalgia Entertainment Syndrome");
			title.setFont(new Font("Monospaced", Font.BOLD, FONT_SIZE + 6));
			title.setBackground(Color.BLACK);
			title.setForeground(Color.RED);
			title.setAlignmentX(CENTER_ALIGNMENT);
			add(title);
			
			JPanel row = new JPanel();
			row.setBackground(Color.BLACK);
			row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
			
			row.add(createTextArea(
					"\t\t\t\t\tControls" + "\n" + "\n" +
					String.format(FORMAT, "\u2191", "Thrust Forward")			+ "\n" +
					String.format(FORMAT, "\u2193", "Decelerate (Magically)")	+ "\n" + "\n" +
					
					String.format(FORMAT, "\u2190", "Turn Left")				+ "\n" +
					String.format(FORMAT, "\u2192", "Turn Right")				+ "\n" + "\n" +
					
					String.format(FORMAT, "\u2190 + \u21E7", "Strafe Left")		+ "\n" +
					String.format(FORMAT, "\u2192 + \u21E7", "Strafe Right")	+ "\n" + "\n" +
					
					String.format(FORMAT, "W", "Move Camera Up")				+ "\n" +
					String.format(FORMAT, "S", "Move Camera Down")				+ "\n" +
					String.format(FORMAT, "A", "Move Camera Left")				+ "\n" +
					String.format(FORMAT, "D", "Move Camera Right")				+ "\n" + "\n" +
					
					String.format(FORMAT, "F", "Center Camera on Player")		+ "\n" + "\n" +
					
					String.format(FORMAT, "Mouse", "Aim Turret")				+ "\n" +
					String.format(FORMAT, "Left Click", "Fire Turret")			+ "\n" + "\n" +
					
					String.format(FORMAT, "X", "Fire Cannon")					+ "\n" + "\n" +
					
					String.format(FORMAT, "\u232B", "Restart Game")			+ "\n" +
					String.format(FORMAT, "\u238B", "End Game")					+ "\n" +
					String.format(FORMAT, "Any Click", "Start")));
			row.add(createTextArea(
					"\t\t\t\t\tInstructions" + "\n" + "\n" +
					"-Enemies attack in waves that spawn around your position." + "\n" +
					"-Enemies grow progressively stronger after every wave." + "\n" +
					"-Destroy all enemies to advance to the next wave." + "\n" +
					"-Your ship takes damage from lasers and collisions" + "\n" +
					"-Your interface degrades based on your ship's damage." + "\n" +
					"-Your ship's structure repairs slowly over time."));
			add(row);
			
			requestFocus();
			
		}
		
		public JTextArea createTextArea(String text) {
			JTextArea area = new JTextArea();
			area.setTabSize(4);
			area.addMouseListener(this);
			area.addKeyListener(this);
			area.setFont(new Font("Monospaced", Font.BOLD, FONT_SIZE));
			area.setText(text);
			area.setEditable(false);
			area.setBackground(Color.BLACK);
			area.setForeground(Color.RED);
			area.setFocusable(false);
			area.addKeyListener(this);
			area.addMouseListener(this);
			return area;
		}
		public void paintComponent(Graphics g) {
			//printY = 128;
			
			super.paintComponent(g);
		}
		/*
		public void drawText(Graphics g, String message) {
			g.drawString(message, PRINT_X, printY);
			printY += FONT_SIZE;
		}
		*/
		public void beginGame() {
			frame.removeMouseListener(this);
			frame.removeKeyListener(this);
			frame.remove(this);
			GamePanel game = new GamePanel();
			frame.add(game);
			frame.addMouseListener(game);
			frame.addKeyListener(game);
			Dimension frameSize = frame.getSize();
			frame.pack();
			frame.setSize(frameSize);
			game.requestFocus();
			game.newGame();
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			beginGame();
		}
		public void mousePressed(MouseEvent e) {
			beginGame();			
		}
		public void mouseReleased(MouseEvent e) {
			beginGame();			
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void keyTyped(KeyEvent e) {
			System.out.println("Type");
			beginGame();
		}
		public void keyPressed(KeyEvent e) {
			System.out.println("Pressed");
			beginGame();
		}
		public void keyReleased(KeyEvent e) {
			System.out.println("Released");
			beginGame();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
