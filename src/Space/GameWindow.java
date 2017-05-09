package Space;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

public class GameWindow {
	public static final int SCREEN_WIDTH;
	public static final int SCREEN_HEIGHT;
	
	public static final int GAME_WIDTH;
	public static final int GAME_HEIGHT;
	
	static {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		SCREEN_WIDTH = (int) screenSize.getWidth();
		SCREEN_HEIGHT = (int) screenSize.getHeight();
		
		GAME_WIDTH = SCREEN_WIDTH*2;
		GAME_HEIGHT = SCREEN_HEIGHT*2;
	}
	
	public static final int SCREEN_CENTER_X = SCREEN_WIDTH/2;
	public static final int SCREEN_CENTER_Y = SCREEN_HEIGHT/2;
	public static final Point2D SCREEN_CENTER = new Point(SCREEN_CENTER_X, SCREEN_CENTER_Y);
	
	private GamePanel panel;
	private JFrame frame;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameWindow game = new GameWindow();
		
	}
	
	public GameWindow()
	{
		frame = new JFrame();
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
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
