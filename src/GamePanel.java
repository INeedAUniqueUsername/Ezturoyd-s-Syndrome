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
	ArrayList<Space_Object> world;
	ArrayList<Spaceship> spaceships;
	ArrayList<Projectile> projectiles;
	ArrayList<Asteroid> asteroids;
	
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
		print("*" + tick + "*");
		
		Space_Object.world = this;
		
		world = new ArrayList();
		spaceships = new ArrayList();
		projectiles = new ArrayList();
		asteroids = new ArrayList();
		
		weapons = new ArrayList();
		
		player = new Spaceship(800, 450);
		
		addSpaceship(player);
		addWeapon(player, new Weapon(0, 10));
		
		Asteroid rock = new Asteroid();
		rock.setVelRectangular(0, 0);
		rock.setPosRectangular(500, 500);
		addAsteroid(rock);
	}
	
	public void paintComponent(Graphics g)
	{	
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
		/*
		Spaceship player = ships.get(0);
		Asteroid rock = asteroids.get(0);
		double angle = player.getAngleTowards(rock);
		int playerY = (int) player.getPosY();
		int x = (int) player.getPosX();
		int y = (int) (GameWindow.HEIGHT - playerY);
		int x2 = (int) (x + 50 * player.cosDegrees(angle));
		int y2 = (int) (GameWindow.HEIGHT - (playerY + 50 * player.sinDegrees(angle)));
		
		g.setColor(Color.WHITE);
		g.drawLine(x, y, x2, y2);
		*/
		tick++;
		for(Space_Object object: world)
		{
			
			if(object.getActive())
			{
				object.update();
				object.draw(g);
			}
			else
			{
				object.destroy();
				world.remove(object);
				if(spaceships.contains(object))
				{
					spaceships.remove(object);
				}
				else if(projectiles.contains(object))
				{
					projectiles.remove(object);
				}
				else if(asteroids.contains(object))
				{
					asteroids.remove(object);
				}
			}
		}
		
		for(Asteroid a1: asteroids)
		{
			for(Projectile p: projectiles)
			{
				if(objectsIntersect(a1, p))
				{
					print("--> GamePanel: Asteroid-Projectile Collision");
					a1.collisionProjectile(p);
					
					
					p.destroy();
					removeProjectile(p);
					print("<-- GamePanel: Asteroid-Projectile Collision");
				}
			}
			for(Spaceship s: spaceships)
			{
				if(objectsIntersect(a1, s) && tick - a1.lastCollisionTick > 10 && tick - s.lastCollisionTick > 10)
				{
					a1.lastCollisionTick = tick;
					s.lastCollisionTick = tick;
					print("--> GamePanel: Asteroid-Spaceship Collision");
					a1.collisionSpaceship(s);
					print("<-- GamePanel: Asteroid-Spaceship Collision");
				}
			}
		}
		
		for(Spaceship s1: spaceships)
		{
			for(Spaceship s2: spaceships)
			{
				if(s1 != s2)
				{
					if(objectsIntersect(s1, s2))
					{
						print("--> GamePanel: Spaceship-Spaceship Collision");
						double angle_s1_s2 = angleBetween(s1, s2);
						double angle_s2_s1 = angleBetween(s2, s1);
						double momentum_total = s1.getMomentumAngled(angle_s1_s2) + s2.getMomentumAngled(angle_s2_s1);
						double momentum_half = momentum_total/2;
						s1.impulse(angle_s2_s1, momentum_half);
						s2.impulse(angle_s1_s2, momentum_half);
						print("<-- GamePanel: Spaceship-Spaceship Collision");
					}
				}
			}
		}
		
		for(Weapon weapon: weapons)
		{
			print("--> Human Shot First");
			weapon.update();
			weapon.draw(g);
			if(weapon.getFiring() && (weapon.getFireCooldownTime() > weapon.getFireCooldownMax()))
			{
				Projectile shot = weapon.getShot();
				addProjectile(shot);
				weapon.setFireCooldownTime(0);
			}
			print("<-- Human Shot First");
		}
		
		/*
		if(objectsIntersect(player, rock))
		{
			int forceAngle = (int) arctanDegrees(player.getVelY()-rock.getVelY(), player.getVelX() - rock.getVelX());
			double rockVelAngle = rock.getVelAngle();
		}
		*/
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
		print("Key Typed");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		print("Key Pressed");
		if(e.getKeyCode() == e.VK_UP)
		{
			//Accelerate forward
			player.thrust();
		}
		if(e.getKeyCode() == e.VK_DOWN)
		{
			player.decelerate(player.DECEL);
		}
		
		if(e.getKeyCode() == e.VK_LEFT)
		{
			player.rotateLeft(player.ROTATION_ACCEL);
		}
		if(e.getKeyCode() == e.VK_RIGHT)
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
	
	public double arctanDegrees(double y, double x)
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
		print("X: " + y);
		print("Y: " + x);
		print("R: " + result);
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
	
	
	
	public void addSpaceship(Spaceship ship)
	{
		spaceships.add(ship);
		world.add(ship);
	}
	public void addProjectile(Projectile projectile)
	{
		projectiles.add(projectile);
		world.add(projectile);
	}
	public void addAsteroid(Asteroid asteroid)
	{
		asteroids.add(asteroid);
		world.add(asteroid);
	}
	
	public void removeSpaceship(Spaceship ship)
	{
		spaceships.remove(ship);
		world.remove(ship);
	}
	public void removeProjectile(Projectile projectile)
	{
		projectiles.remove(projectile);
		world.remove(projectile);
	}
	public void removeAsteroid(Asteroid asteroid)
	{
		asteroids.remove(asteroid);
		world.remove(asteroid);
	}
	
	public double angleBetween(Space_Object from, Space_Object to)
	{
		return to.getAngleFrom(from);
	}
	
	public boolean exists(Object what)
	{
		return what != null;
	}
	
	public void print(String message)
	{
		System.out.println(tick + ". " + message);
	}

}
