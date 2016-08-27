import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, MouseListener, KeyListener {

	final int INTERVAL = 10;
	Spaceship player;
	Asteroid rock;
	public GamePanel()
	{
		Timer ticker = new Timer(INTERVAL, this);
		ticker.start();
	}
	
	public void newGame()
	{
		player = new Spaceship(800, 450);
		player.setVel(5, 5);
		
		rock = new Asteroid();
	}
	
	public void paintComponent(Graphics g)
	{	
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
		
		player.update();
		player.draw(g);
		
		rock.update();
		rock.draw(g);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == e.VK_UP)
		{
			//Accelerate forward
			player.thrust();
		}
		else if(e.getKeyCode() == e.VK_LEFT)
		{
			player.rotateLeft(player.ROTATION_ACCEL);
		}
		else if(e.getKeyCode() == e.VK_RIGHT)
		{
			player.rotateRight(player.ROTATION_ACCEL);
		}
		else if(e.getKeyCode() == e.VK_DOWN)
		{
			player.decelerate(player.DECEL);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
