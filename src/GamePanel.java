import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
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

	boolean active = true;
	final int INTERVAL = 10;
	Starship player;
	ArrayList<Space_Object> universe;
	ArrayList<Starship> starships;
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

		universe = new ArrayList<Space_Object>();
		starships = new ArrayList<Starship>();
		projectiles = new ArrayList<Projectile>();
		asteroids = new ArrayList<Asteroid>();

		weapons = new ArrayList<Weapon>();

		player = new Starship();
		player.setPosRectangular(800, 450);

		addStarship(player);
		addWeapon(player, new Weapon_Key(0, 10, 0, 1, 30, 1, 30, Color.RED));
		addWeapon(player, new Weapon_Mouse(0, 10, 0, 1, 30, 1, 30, Color.RED));
		player.setName("Player");
		
		Starship_Enemy enemy1 = new Starship_Enemy();
		addStarship(enemy1);
		enemy1.setPosRectangular(400, 225);
		enemy1.setTarget(player);
		enemy1.setName("Enemy");
		addWeapon(enemy1, new Weapon(0, 10, 0, 5, 15, 1, 30, Color.RED));
		
		Starship_Enemy enemy2 = new Starship_Enemy();
		addStarship(enemy2);
		enemy2.setPosRectangular(800, 525);
		enemy2.setTarget(enemy1);
		enemy2.setName("Enemy");
		addWeapon(enemy2, new Weapon(0, 10, 0, 5, 15, 1, 30, Color.RED));
		
		Starship_Enemy enemy3 = new Starship_Enemy();
		addStarship(enemy3);
		enemy3.setPosRectangular(0, 0);
		enemy3.setTarget(player);
		enemy3.setName("Enemy");
		addWeapon(enemy3, new Weapon(0, 10, 0, 5, 15, 1, 30, Color.RED));

		/*
		Asteroid rock = new Asteroid();
		rock.initializeBody(10, 50, 70);
		addAsteroid(rock);
		rock.setPosRectangular(600, 375);
		rock.setName("Asteroid");
		*/
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
		 * Starship player = ships.get(0); Asteroid rock = asteroids.get(0);
		 * double angle = player.getAngleTowards(rock); int playerY = (int)
		 * player.getPosY(); int x = (int) player.getPosX(); int y = (int)
		 * (GameWindow.HEIGHT - playerY); int x2 = (int) (x + 50 *
		 * player.cosDegrees(angle)); int y2 = (int) (GameWindow.HEIGHT -
		 * (playerY + 50 * player.sinDegrees(angle)));
		 * 
		 * g.setColor(Color.WHITE); g.drawLine(x, y, x2, y2);
		 */
		if(active)
		{
			debug.clear();
			tick++;
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
				Iterator<Starship> s_i = starships.iterator();
				while (s_i.hasNext()) {
					Starship s = s_i.next();
					if (objectsIntersect(a1, s) && tick - a1.lastCollisionTick > 10 && tick - s.lastCollisionTick > 10) {
						a1.lastCollisionTick = tick;
						s.lastCollisionTick = tick;
						//print("--> GamePanel: Asteroid-Starship Collision");
						collisionAsteroidStarship(a1, s);
						//print("<-- GamePanel: Asteroid-Starship Collision");
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
							//print("--> GamePanel: Asteroid-Starship Collision");
							collisionAsteroidAsteroid(a1, a2);
							//print("<-- GamePanel: Asteroid-Starship Collision");
						}
					}
				}
			}
	
			Iterator<Starship> s1_i = starships.iterator();
			while (s1_i.hasNext()) {
				Starship s1 = s1_i.next();
				
				Iterator<Starship> s2_i = starships.iterator();
				while (s2_i.hasNext()) {
					Starship s2 = s2_i.next();
					if (s1 != s2) {
						if (objectsIntersect(s1, s2)) {
							collisionStarshipStarship(s1, s2);
						}
					}
				}
				Iterator<Projectile> p_i = projectiles.iterator();
				while(p_i.hasNext())
				{
					Projectile p1 = p_i.next();
					if(objectsIntersect(s1, p1) && !p1.getOwner().equals(s1))
					{
						collisionStarshipProjectile(s1, p1);
					}
				}
			}
			
			Iterator<Weapon> w_i = weapons.iterator();
			while (w_i.hasNext()) {
				Weapon w = w_i.next();
				w.update();
				w.draw(g);
				if (w.getFiring() && (w.getFireCooldownLeft() > w.getFireCooldownMax())) {
					print("--> " + (w.getOwner() == player ? "Human" : "Computer") + " Shot First");
					Projectile shot = w.getShot();
					addProjectile(shot);
					w.setFireCooldownLeft(0);
					print("<--" + (w.getOwner() == player ? "Human" : "Computer") + " Shot First");
				}
			}
			player.setFiringMouse(false);
			Iterator<Space_Object> o_i = universe.iterator();
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
					if (Starships.contains(object)) {
						Starships.remove(object);
					} else if (projectiles.contains(object)) {
						projectiles.remove(object);
					} else if (asteroids.contains(object)) {
						asteroids.remove(object);
					}
				}
				*/
			}
			/*
			 * if(objectsIntersect(player, rock)) { int forceAngle = (int)
			 * arctanDegrees(player.getVelY()-rock.getVelY(), player.getVelX() -
			 * rock.getVelX()); double rockVelAngle = rock.getVelAngle(); }
			 */
		}
		else
		{
			for(Space_Object o: universe)
			{
				o.draw(g);
			}
			for(Weapon w: weapons)
			{
				w.draw(g);
			}
		}
		
		//Print all current debug messages on screen. Debug list will only clear when the game is active.
		g.setColor(Color.WHITE);
		int print_height = 12;
		for(String s: debug)
		{
			g.drawString(s, 10, print_height);
			print_height += 12;
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
		
		if(e.getKeyCode() == KeyEvent.VK_Z)
			active = !active;
		
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			// Accelerate forward
			player.thrust();
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			player.brake();
		}
		
		//Regular turning
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			player.turnCCW();
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			player.turnCW();
		}

		if (e.getKeyCode() == KeyEvent.VK_X) {
			player.setFiringKey(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			player.setStrafing(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == e.VK_X)
		{
			player.setFiringKey(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			player.setStrafing(false);
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
		Area areaA = new Area();
		for(Polygon part : a.getBody())
		{
			areaA.add(new Area(part));
		}
		
		Area areaB = new Area();
		for(Polygon part : b.getBody())
		{
			areaB.add(new Area(part));
		}
		
		areaA.intersect(areaB);
		return !areaA.isEmpty();
	}

	public void addWeapon(Starship ship, Weapon item) {
		ship.installWeapon(item);
		weapons.add(item);
	}

	public void addStarship(Starship ship) {
		starships.add(ship);
		universe.add(ship);
	}

	public void addProjectile(Projectile projectile) {
		projectiles.add(projectile);
		universe.add(projectile);
	}

	public void addAsteroid(Asteroid asteroid) {
		asteroids.add(asteroid);
		universe.add(asteroid);
	}

	public void removeStarship(Starship ship) {
		universe.remove(ship);
		starships.remove(ship);
	}

	public void removeProjectile(Projectile projectile) {
		universe.remove(projectile);
		projectiles.remove(projectile);
	}

	public void removeAsteroid(Asteroid asteroid) {
		universe.remove(asteroid);
		asteroids.remove(asteroid);
	}
	public void removeWeapon(Weapon weapon)
	{
		universe.remove(weapon);
		weapons.remove(weapon);
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
	
	public void collisionAsteroidStarship(Asteroid a, Starship s) {
		//print("--> Collision (Starship)");
		double angle_asteroid_to_ship = a.getAngleTowards(s);
		double angle_ship_to_asteroid = a.getAngleFrom(s);
		double asteroidKineticEnergy = a.getKineticEnergyAngled(angle_asteroid_to_ship);
		double shipKineticEnergy = s.getKineticEnergyAngled(angle_ship_to_asteroid);
		double totalKineticEnergy = asteroidKineticEnergy + shipKineticEnergy;
		double halfKineticEnergy = totalKineticEnergy / 2;
		s.impulse(angle_ship_to_asteroid, halfKineticEnergy);
		a.impulse(angle_asteroid_to_ship, halfKineticEnergy);

		s.damage(halfKineticEnergy / 100);

		//print("Angle (Asteroid --> Ship): " + angle_asteroid_to_ship);
		//print("Angle (Asteroid <-- Ship): " + angle_ship_to_asteroid);
		//print("Momentum (Asteroid) " + asteroidMomentum);
		//print("Momentum (Ship): " + shipMomentum);
		//print("<-- Collision (Starship)");
	}
	public void collisionAsteroidAsteroid(Asteroid a1, Asteroid a2)
	{
		double angle_a1_to_a2 = a1.getAngleTowards(a2);
		double angle_a2_to_a1 = a2.getAngleTowards(a1);
		double halfKineticEnergy = a1.getKineticEnergyAngled(angle_a1_to_a2) + a2.getKineticEnergyAngled(angle_a2_to_a1);
		a1.impulse(angle_a2_to_a1, halfKineticEnergy);
		a2.impulse(angle_a1_to_a2, halfKineticEnergy);
		a1.damage((int) halfKineticEnergy/1000, a2.getPosX(), a2.getPosY());
		a2.damage((int) halfKineticEnergy/1000, a1.getPosX(), a1.getPosY());
	}
	public void collisionStarshipProjectile(Starship s1, Projectile p1)
	{
		s1.damage(p1.getDamage());
		p1.destroy();
	}
	public void collisionStarshipStarship(Starship s1, Starship s2)
	{
		//print("--> GamePanel: Starship-Starship Collision");
		double angle_s1_s2 = angleBetween(s1, s2);
		double angle_s2_s1 = angleBetween(s2, s1);
		double kinetic_energy_total = s1.getKineticEnergyAngled(angle_s1_s2) + s2.getKineticEnergyAngled(angle_s2_s1);
		double kinetic_energy_half = kinetic_energy_total / 2;
		s1.impulse(angle_s2_s1, kinetic_energy_half);
		s2.impulse(angle_s1_s2, kinetic_energy_half);
		//print("<-- GamePanel: Starship-Starship Collision");
	}

}
