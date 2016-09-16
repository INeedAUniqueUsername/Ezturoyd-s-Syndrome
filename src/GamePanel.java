import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, MouseListener, KeyListener {

	final int INTERVAL = 10;
	Spaceship player;
	Asteroid rock;
	ArrayList<Projectile> projectiles;
	//counter for hits
	int hits= 0;
	ArrayList<Weapon> weapons;
	
	int tick;
	public GamePanel()
	{
		Timer ticker = new Timer(INTERVAL, this);
		ticker.start();
	}
	
	public void newGame()
	{
		tick = 0;
		System.out.println("*" + tick + "*");
		weapons = new ArrayList();
		projectiles = new ArrayList();
		player = new Spaceship(800, 450);
		player.setVelRectangular(5, 5);
		
		rock = new Asteroid();
		
		addWeapon(player, new Weapon(0, 10));
	}
	
	public void paintComponent(Graphics g)
	{	
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
		
		tick++;
		
		player.update();
		player.draw(g);
		
		rock.update();
		rock.draw(g);
		
		for(Weapon weapon: weapons)
		{
			weapon.update();
			weapon.draw(g);
			if(weapon.getFiring() && (weapon.getFireCooldownTime() > weapon.getFireCooldownMax()))
			{
				Space_Object owner = weapon.getOwner();
				Projectile shot = new Projectile(weapon.getPosX(), weapon.getPosY(), weapon.getFireAngle(), weapon.getProjectileLifetime());
				shot.setVelPolar(weapon.getFireAngle(), weapon.getProjectileSpeed());
				shot.incVelRectangular(owner.getVelX(), owner.getVelY());
				projectiles.add(shot);
				weapon.setFireCooldownTime(0);
			}
		}
		for(Projectile projectile: projectiles)
		{
			projectile.update();
			projectile.draw(g);
			if(projectile.getLifetime() < 0)
			{
				projectiles.remove(projectile);
			}
		}
		
		if(objectsIntersect(player, rock))
		{
			int forceAngle = (int) arctanDegrees(player.getVelY()-rock.getVelY(), player.getVelX() - rock.getVelX());
			double rockVelAngle = rock.getVelAngle();
		}
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
		System.out.println("Key Typed");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Key Pressed");
		if(e.getKeyCode() == e.VK_UP)
		{
			//Accelerate forward
			player.thrust();
		}
		else if(e.getKeyCode() == e.VK_DOWN)
		{
			player.decelerate(player.DECEL);
		}
		if(e.getKeyCode() == e.VK_LEFT)
		{
			player.rotateLeft(player.ROTATION_ACCEL);
		}
		else if(e.getKeyCode() == e.VK_RIGHT)
		{
			player.rotateRight(player.ROTATION_ACCEL);
		}
		if(e.getKeyCode() == e.VK_X)
		{
			player.setFiring(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == e.VK_X)
		{
			player.setFiring(false);
		}
	}
	
	public static double arctanDegrees(double y, double x)
	{
		double result;
		if(x < 0)
		{
			result = Math.toDegrees(Math.atan(y/x)) + 180;
		}
		else if(x == 0)
		{
			if(y < 0)
			{
				result = 270;
			}
			else if(y == 0)
			{
				result = 0;
			}
			else //ySpeed > 0
			{
				result =  90;
			}
		}
		else if(x > 0)
		{
			result = Math.toDegrees(Math.atan(y/x));
		}
		else
		{
			result = 0;
		}
		System.out.println("X: " + y);
		System.out.println("Y: " + x);
		System.out.println("R: " + result);
		return result;
	}

	public static boolean intersects(Shape shapeA, Shape shapeB) {
		   Area areaA = new Area(shapeA);
		   areaA.intersect(new Area(shapeB));
		   return !areaA.isEmpty();
		}
	
	public static boolean objectsIntersect(Space_Object a, Space_Object b) {
		   Area areaA = new Area(a.getBody());
		   areaA.intersect(new Area(b.getBody()));
		   return !areaA.isEmpty();
		}
	
	public void addWeapon(Spaceship ship, Weapon item)
	{
		ship.installWeapon(item);
		item.setOwner(ship);
		weapons.add(item);
	}
	
	public void print(String message)
	{
		System.out.println(tick + ". " + message);
	}

}
