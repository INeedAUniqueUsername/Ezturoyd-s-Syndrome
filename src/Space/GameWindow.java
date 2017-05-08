package Space;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GameWindow {

	public static final int WIDTH;
	public static final int HEIGHT;
	private GamePanel panel;
	private JFrame frame;
	static {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		WIDTH = (int) screenSize.getWidth();
		HEIGHT = (int) screenSize.getHeight();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameWindow game = new GameWindow();
		
	}
	
	public GameWindow()
	{
		frame = new JFrame();
		frame.setSize(WIDTH, HEIGHT);
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
