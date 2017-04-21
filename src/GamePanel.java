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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, MouseListener, KeyListener {

	private boolean active = true;
	final int INTERVAL = 10;
	private Starship player;
	//private Starship_NPC enemy_test;
	private ArrayList<SpaceObject> universe;
	Level currentLevel;
	/*
	ArrayList<Starship> starships;
	ArrayList<Projectile> projectiles;
	ArrayList<Asteroid> asteroids;
	*/
	private ArrayList<String> debug = new ArrayList<String>();

	// counter for hits
	private int hits = 0;

	private int tick;
	public static GamePanel world;

	public GamePanel() {
		Timer ticker = new Timer(INTERVAL, this);
		ticker.start();
		world = this;
	}

	public void newGame() {
		setTick(0);
		print("*" + getTick() + "*");

		universe = new ArrayList<SpaceObject>();
		//starships = new ArrayList<Starship>();
		//projectiles = new ArrayList<Projectile>();
		//asteroids = new ArrayList<Asteroid>();

		player = new Starship();
		player.setPosRectangular(800, 450);

		addStarship(player);
		addWeapon(player, new Weapon_Key(0, 0, 0, 5, 30, 1, 90, Color.RED));
		//addWeapon(player, new Weapon_Mouse(0, 10, 0, 1, 30, 1, 30, Color.RED));
		player.setName("Player");
		/*∂
		enemy_test = new Starship_NPC();
		addStarship(enemy_test);
		enemy_test.setPosRectangular(400, 225);
		enemy_test.setName("Enemy");
		addWeapon(enemy_test, new Weapon(0, 10, 0, 5, 15, 1, 90, Color.RED));
		enemy_test.addOrder(new Order_Escort(enemy_test, player));
		*/
		currentLevel = new Level_Waves();
		currentLevel.start();
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
			setTick(getTick() + 1);
			
			currentLevel.update();
			
			player.setFiringMouse(false);
			for(SpaceObject o : universe) {
				o.update();
				o.draw(g);
			}
			
			ArrayList<SpaceObject> dead = new ArrayList<SpaceObject>();
			Iterator<SpaceObject> o_i_1 = universe.iterator();
			while (o_i_1.hasNext()) {
				SpaceObject o1 = o_i_1.next();
				
				//Update all weapons
				if(o1 instanceof Starship) {
					Iterator<Weapon> w_i = ((Starship) o1).getWeapon().iterator();
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
				}
				
				
				Iterator<SpaceObject> o_i_2 = universe.subList(universe.indexOf(o1)+1, universe.size()).iterator();
				while(o_i_2.hasNext()) {
					SpaceObject o2 = o_i_2.next();
					if(objectsIntersect(o1, o2)) {
						if(o1 instanceof Starship && o2 instanceof Starship) {
							collisionStarshipStarship((Starship) o1, (Starship) o2);
						} else if(o1 instanceof Starship && o2 instanceof Projectile) {
							Starship s = (Starship) o1;
							Projectile p = (Projectile) o2;
							if(!p.getOwner().equals(s)) {
								collisionStarshipProjectile(s, p);
							}
						} else if(o1 instanceof Projectile && o2 instanceof Starship) {
							Starship s = (Starship) o2;
							Projectile p = (Projectile) o1;
							if(!p.getOwner().equals(s)) {
								collisionStarshipProjectile(s, p);
							}
						} else if(o1 instanceof Projectile && o2 instanceof Projectile) {
							collisionProjectileProjectile((Projectile) o1, (Projectile) o2);
						}
						if(!o1.getActive()) {
							dead.add(o1);
						}
						if(!o2.getActive()) {
							dead.add(o2);
						}
					}
				}
				if(!o1.getActive()) {
					dead.add(o1);
				}
			}
			for(SpaceObject d : dead) {
				universe.remove(d);
			}
		}
		else
		{
			for(SpaceObject o: universe)
			{
				o.draw(g);
				if(o instanceof Starship) {
					for(Weapon w : ((Starship) o).getWeapon()) {
						w.draw(g);
					}
				}
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
		player.setAimPos(x, y);
		player.setFiringMouse(true);
		/*
		enemy_test.clearOrders();
		enemy_test.addOrder(new Order_AttackOrbit(enemy_test, player));
		*/
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
		switch(e.getKeyCode()) {
		case KeyEvent.VK_Z:		active = !active;			break;
		case KeyEvent.VK_UP:	player.setThrusting(true);	break;
		case KeyEvent.VK_DOWN:	player.setBraking(true);	break;
		case KeyEvent.VK_LEFT:	player.setTurningCCW(true);	break;
		case KeyEvent.VK_RIGHT:	player.setTurningCW(true);	break;
		case KeyEvent.VK_SHIFT:	player.setStrafing(true);	break;
		case KeyEvent.VK_X:		player.setFiringKey(true);	break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:	player.setThrusting(false);		break;
		case KeyEvent.VK_DOWN:	player.setBraking(false);		break;
		case KeyEvent.VK_LEFT:	player.setTurningCCW(false);	break;
		case KeyEvent.VK_RIGHT:	player.setTurningCW(false);		break;
		case KeyEvent.VK_SHIFT:	player.setStrafing(false);		break;
		case KeyEvent.VK_X:		player.setFiringKey(false);		break;
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

	public static boolean objectsIntersect(SpaceObject a, SpaceObject b) {
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
	}

	public void addStarship(Starship ship) {
		//starships.add(ship);
		universe.add(ship);
	}

	public void addProjectile(Projectile projectile) {
		//projectiles.add(projectile);
		universe.add(projectile);
	}
/*
	public void addAsteroid(Asteroid asteroid) {
		//asteroids.add(asteroid);
		universe.add(asteroid);
	}
*/
	
	/*
	public void removeStarship(Starship ship) {
		universe.remove(ship);
		starships.remove(ship);
	}

	public void removeProjectile(Projectile projectile) {
		universe.remove(projectile);
		projectiles.remove(projectile);
	}
	*/

	/*
	public void removeAsteroid(Asteroid asteroid) {
		universe.remove(asteroid);
		asteroids.remove(asteroid);
	}
	*/
	public void removeWeapon(Weapon weapon)
	{
		universe.remove(weapon);
	}
	public Starship getPlayer() {
		return player;
	}
	public ArrayList<SpaceObject> getUniverse()
	{
		return universe;
	}
	public ArrayList<Starship> getStarships()
	{
		//return starships;
		ArrayList<Starship> result = new ArrayList<Starship>();
		for(SpaceObject o : universe) {
			if(o instanceof Starship) {
				result.add((Starship) o);
			}
		}
		return result;
	}
	public ArrayList<Projectile> getProjectiles()
	{
		//return projectiles;
		ArrayList<Projectile> result = new ArrayList<Projectile>();
		for(SpaceObject o : universe) {
			if(o instanceof Projectile) {
				result.add((Projectile) o);
			}
		}
		return result;
	}
	/*
	public ArrayList<Asteroid> getAsteroids()
	{
		return asteroids;
	}
	*/
	
	public double angleBetween(SpaceObject from, SpaceObject to) {
		return to.getAngleFrom(from);
	}

	public boolean exists(Object what) {
		return what != null;
	}
	public boolean isAlive(SpaceObject what)
	{
		boolean result = false;
		if(what instanceof Starship)
		{
			result = getStarships().contains(what);
		}
		/*
		else if(what instanceof Asteroid)
		{
			result = asteroids.contains(what);
		}
		*/
		else if(what instanceof Projectile)
		{
			result = getProjectiles().contains(what);
		}
		return result;
	}

	public void print(String message) {
		System.out.println(getTick() + ". " + message);
	}
	
	public void collisionAsteroidStarship(Asteroid_Deprecated_2 a, Starship s) {
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
	public void collisionAsteroidAsteroid(Asteroid_Deprecated_2 a1, Asteroid_Deprecated_2 a2)
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
		s1.onAttacked(p1.getOwner());
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
	public void collisionProjectileProjectile(Projectile p1, Projectile p2)
	{
		p1.damage(p2.getDamage());
		p2.damage(p1.getDamage());
	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

}
