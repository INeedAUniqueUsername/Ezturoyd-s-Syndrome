import javax.swing.JFrame;

public class GameWindow {

	public static final int WIDTH = 2000;
	public static final int HEIGHT = 1200;
	GamePanel panel;
	JFrame frame;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameWindow game = new GameWindow();
	}
	
	public GameWindow()
	{
		frame = new JFrame();
		frame.setSize(WIDTH, HEIGHT);
		
		panel = new GamePanel();
		frame.add(panel);
		frame.addMouseListener(panel);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setTitle("Ezturoyd's Syndrome");
		
		panel.newGame();
	}

}
