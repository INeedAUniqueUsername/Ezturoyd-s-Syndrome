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
import java.util.HashMap;
import java.util.Iterator;

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
	ArrayList<String> debug = new ArrayList<String>();

	// counter for hits
	int hits = 0;
	ArrayList<Weapon> weapons;

	int tick;

	public GamePanel() {
		Timer ticker = new Timer(INTERVAL, this);
		ticker.start();
	}

	public void newGame() {
		tick = 0;
		print("*" + tick + "*");

		Space_Object.world = this;

		world = new ArrayList();
		spaceships = new ArrayList();
		projectiles = new ArrayList();
		asteroids = new ArrayList();

		weapons = new ArrayList();

		player = new Spaceship();
		player.setPosRectangular(800, 450);

		addSpaceship(player);
		addWeapon(player, new Weapon_Key(0, 10, 0, 1, 30, 1, 30, Color.RED));
		addWeapon(player, new Weapon_Mouse());
		player.setName("Player");
		
		Spaceship_Enemy enemy = new Spaceship_Enemy();
		addSpaceship(enemy);
		enemy.setPosRectangular(400, 225);
		enemy.setTarget(player);

		/*
		player.setVelRectangular(5, 0);
		for(int i = 0; i < 360; i++)
		{
			print("Momentum at " + i + "°: " + player.getMomentumAngled(i));
			i++;
		}
		*/
		
		/*
		Asteroid rock = new Asteroid();
		rock.setVelRectangular(0, 0);
		rock.setPosRectangular(500, 500);
		
		addAsteroid(rock);
		*/
		
		/*
		addAsteroid(new Asteroid());
		addAsteroid(new Asteroid());
		addAsteroid(new Asteroid());
		addAsteroid(new Asteroid());
		addAsteroid(new Asteroid());
		addAsteroid(new Asteroid());
		addAsteroid(new Asteroid());
		*/
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
		/*
		 * Spaceship player = ships.get(0); Asteroid rock = asteroids.get(0);
		 * double angle = player.getAngleTowards(rock); int playerY = (int)
		 * player.getPosY(); int x = (int) player.getPosX(); int y = (int)
		 * (GameWindow.HEIGHT - playerY); int x2 = (int) (x + 50 *
		 * player.cosDegrees(angle)); int y2 = (int) (GameWindow.HEIGHT -
		 * (playerY + 50 * player.sinDegrees(angle)));
		 * 
		 * g.setColor(Color.WHITE); g.drawLine(x, y, x2, y2);
		 */
		tick++;
		
		Iterator<Weapon> w_i = weapons.iterator();
		while (w_i.hasNext()) {
			Weapon w = w_i.next();
			//print("--> Human Shot First");
			w.update();
			w.draw(g);
			if (w.getFiring() && (w.getFireCooldownLeft() > w.getFireCooldownMax())) {
				Projectile shot = w.getShot();
				addProjectile(shot);
				w.setFireCooldownLeft(0);
			}
			//print("<-- Human Shot First");
		}
		Iterator<Space_Object> o_i = world.iterator();
		while (o_i.hasNext()) {
			Space_Object o = o_i.next();
			o.update();
			o.draw(g);
			/*
			if (object.getActive()) {
			}
			*/
			/*
			else
			{
				object.destroy();
				world.remove(object);
				if (spaceships.contains(object)) {
					spaceships.remove(object);
				} else if (projectiles.contains(object)) {
					projectiles.remove(object);
				} else if (asteroids.contains(object)) {
					asteroids.remove(object);
				}
			}
			*/
		}

		Iterator<Asteroid> a1_i = asteroids.iterator();
		while (a1_i.hasNext()) {
			Asteroid a1 = a1_i.next();
			Iterator<Projectile> p_i = projectiles.iterator();
			while (p_i.hasNext()) {
				Projectile p = p_i.next();
				if (objectsIntersect(a1, p)) {
					//print("--> GamePanel: Asteroid-Projectile Collision");
					a1.collisionProjectile(p);

					p.destroy();
					removeProjectile(p);
					//print("<-- GamePanel: Asteroid-Projectile Collision");
				}
			}
			Iterator<Spaceship> s_i = spaceships.iterator();
			while (s_i.hasNext()) {
				Spaceship s = s_i.next();
				if (objectsIntersect(a1, s) && tick - a1.lastCollisionTick > 10 && tick - s.lastCollisionTick > 10) {
					a1.lastCollisionTick = tick;
					s.lastCollisionTick = tick;
					//print("--> GamePanel: Asteroid-Spaceship Collision");
					collisionAsteroidSpaceship(a1, s);
					//print("<-- GamePanel: Asteroid-Spaceship Collision");
				}
			}
			Iterator<Asteroid> a2_i = asteroids.iterator();
			while(a2_i.hasNext())
			{
				Asteroid a2 = a2_i.next();
				if(a1 != a2)
				{
					if (objectsIntersect(a1, a2) && tick - a1.lastCollisionTick > 10 && tick - a2.lastCollisionTick > 10) {
						a1.lastCollisionTick = tick;
						a2.lastCollisionTick = tick;
						//print("--> GamePanel: Asteroid-Spaceship Collision");
						collisionAsteroidAsteroid(a1, a2);
						//print("<-- GamePanel: Asteroid-Spaceship Collision");
					}
				}
			}
		}

		Iterator<Spaceship> s1_i = spaceships.iterator();
		while (s1_i.hasNext()) {
			Spaceship s1 = s1_i.next();
			
			Iterator<Spaceship> s2_i = spaceships.iterator();
			while (s2_i.hasNext()) {
				Spaceship s2 = s2_i.next();
				if (s1 != s2) {
					if (objectsIntersect(s1, s2)) {
						//print("--> GamePanel: Spaceship-Spaceship Collision");
						double angle_s1_s2 = angleBetween(s1, s2);
						double angle_s2_s1 = angleBetween(s2, s1);
						double momentum_total = s1.getKineticEnergyAngled(angle_s1_s2) + s2.getKineticEnergyAngled(angle_s2_s1);
						double momentum_half = momentum_total / 2;
						s1.impulse(angle_s2_s1, momentum_half);
						s2.impulse(angle_s1_s2, momentum_half);
						//print("<-- GamePanel: Spaceship-Spaceship Collision");
					}
				}
			}
		}

		/*
		 * if(objectsIntersect(player, rock)) { int forceAngle = (int)
		 * arctanDegrees(player.getVelY()-rock.getVelY(), player.getVelX() -
		 * rock.getVelX()); double rockVelAngle = rock.getVelAngle(); }
		 */
		
		//Print all debug strings on the screen
		g.setColor(Color.WHITE);
		
		int print_height = 0;
		Iterator<String> print_i = debug.iterator();
		while(print_i.hasNext())
		{
			print_height += 12;
			g.drawString(print_i.next(), 10, print_height);
			print_i.remove();
		}
	}

	public void printToScreen(String text)
	{
		debug.add(text);
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
		double x = e.getX();
		double y = GameWindow.HEIGHT - e.getY();
		player.setTargetPos(x, y);
		player.setFiringMouse(true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		player.setFiringMouse(false);;
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
		if (e.getKeyCode() == e.VK_UP) {
			// Accelerate forward
			player.thrust();
		}
		if (e.getKeyCode() == e.VK_DOWN) {
			player.brake();
		}

		if (e.getKeyCode() == e.VK_LEFT) {
			player.turnCCW();
		}
		else if (e.getKeyCode() == e.VK_RIGHT) {
			player.turnCW();
		}

		if (e.getKeyCode() == e.VK_X) {
			player.setFiringKey(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == e.VK_X)
		{
			player.setFiringKey(false);
		}
	}

	public double arctanDegrees(double y, double x) {
		double result;
		if (x < 0) {
			result = Math.toDegrees(Math.atan(y / x)) + 180;
		} else if (x == 0) {
			if (y < 0) {
				result = 270;
			} else if (y == 0) {
				result = 0;
			} else // ySpeed > 0
			{
				result = 90;
			}
		} else if (x > 0) {
			result = Math.toDegrees(Math.atan(y / x));
		} else {
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

	public void addWeapon(Spaceship ship, Weapon_Mouse item) {
		ship.installWeapon(item);
		weapons.add(item);
	}
	public void addWeapon(Spaceship ship, Weapon_Key item)
	{
		ship.installWeapon(item);
		weapons.add(item);
	}

	public void addSpaceship(Spaceship ship) {
		spaceships.add(ship);
		world.add(ship);
	}

	public void addProjectile(Projectile projectile) {
		projectiles.add(projectile);
		world.add(projectile);
	}

	public void addAsteroid(Asteroid asteroid) {
		asteroids.add(asteroid);
		world.add(asteroid);
	}

	public void removeSpaceship(Spaceship ship) {
		spaceships.remove(ship);
		world.remove(ship);
	}

	public void removeProjectile(Projectile projectile) {
		projectiles.remove(projectile);
		world.remove(projectile);
	}

	public void removeAsteroid(Asteroid asteroid) {
		asteroids.remove(asteroid);
		world.remove(asteroid);
	}

	public double angleBetween(Space_Object from, Space_Object to) {
		return to.getAngleFrom(from);
	}

	public boolean exists(Object what) {
		return what != null;
	}

	public void print(String message) {
		System.out.println(tick + ". " + message);
	}
	
	public void collisionAsteroidSpaceship(Asteroid a, Spaceship s) {
		//print("--> Collision (Spaceship)");
		double angle_asteroid_to_ship = a.getAngleTowards(s);
		double angle_ship_to_asteroid = a.getAngleFrom(s);
		double asteroidMomentum = a.getKineticEnergyAngled(angle_asteroid_to_ship);
		double shipMomentum = s.getKineticEnergyAngled(angle_ship_to_asteroid);
		double totalMomentum = asteroidMomentum + shipMomentum;
		double halfMomentum = totalMomentum / 2;
		s.impulse(angle_ship_to_asteroid, totalMomentum);
		a.impulse(angle_asteroid_to_ship, totalMomentum);

		s.damage(halfMomentum / 100);

		//print("Angle (Asteroid --> Ship): " + angle_asteroid_to_ship);
		//print("Angle (Asteroid <-- Ship): " + angle_ship_to_asteroid);
		//print("Momentum (Asteroid) " + asteroidMomentum);
		//print("Momentum (Ship): " + shipMomentum);
		//print("<-- Collision (Spaceship)");
	}
	public void collisionAsteroidAsteroid(Asteroid a1, Asteroid a2)
	{
		double angle_a1_to_a2 = a1.getAngleTowards(a2);
		double angle_a2_to_a1 = a2.getAngleTowards(a1);
		double halfMomentum = a1.getKineticEnergyAngled(angle_a1_to_a2) + a2.getKineticEnergyAngled(angle_a2_to_a1);
		a1.impulse(angle_a2_to_a1, halfMomentum);
		a2.impulse(angle_a1_to_a2, halfMomentum);
		a1.damage((int) halfMomentum/1000, a1.getCollisionIndex(a2));
		a2.damage((int) halfMomentum/1000, a2.getCollisionIndex(a1));
	}

}
