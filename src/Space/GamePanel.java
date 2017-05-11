package Space;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.Timer;

import Interfaces.NewtonianMotion;

public class GamePanel extends JPanel implements ActionListener, MouseListener, KeyListener {
	private boolean active = true;
	private boolean cheat_playerActive = true;
	enum CameraMode {
		FIXED, FOLLOW_PLAYER
	}
	public static final CameraMode camera = CameraMode.FOLLOW_PLAYER;
	final int INTERVAL = 10;
	private Starship_Player player;
	//private Starship_NPC enemy_test;
	private ArrayList<SpaceObject> universe;
	private ArrayList<SpaceObject> objectsCreated;
	private ArrayList<SpaceObject> objectsDestroyed;
	Level currentLevel;
	/*
	ArrayList<Starship> starships;
	ArrayList<Projectile> projectiles;
	ArrayList<Asteroid> asteroids;
	*/
	private ArrayList<String> debugQueue = new ArrayList<String>(0);

	// counter for hits
	//private int hits = 0;

	private int tick;
	private static GamePanel world;

	public GamePanel() {
		Timer ticker = new Timer(INTERVAL, this);
		ticker.start();
		world = this;
	}
	public static GamePanel getWorld() {
		return world;
	}
	public void newGame() {
		setTick(0);
		print("*" + getTick() + "*");

		universe = new ArrayList<SpaceObject>(0);
		//starships = new ArrayList<Starship>();
		//projectiles = new ArrayList<Projectile>();
		//asteroids = new ArrayList<Asteroid>();

		player = new Starship_Player();
		player.setPosRectangular(GameWindow.GAME_WIDTH/2, GameWindow.GAME_HEIGHT/2);

		universeAdd(player);
		
		objectsCreated = new ArrayList<SpaceObject>(0);
		objectsDestroyed = new ArrayList<SpaceObject>(0);
		
		player.installWeapon(new Weapon_Mouse(0, 0, 0, 5, 30, 10, 90));
		//addWeapon(player, new Weapon_Mouse(0, 10, 0, 1, 30, 1, 30, Color.RED));
		player.installWeapon(new Weapon_Key(0, 0, 0, 5, 30, 10, 90));
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
		g.fillRect(0, 0, GameWindow.GAME_WIDTH, GameWindow.GAME_HEIGHT);
		//g.clearRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
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
		updateUniverse();
		updateDraw(g);
	}
	public void updateUniverse() {
		//Update everything
		if(active)
		{
			setTick(getTick() + 1);
			
			//currentLevel.update();
			for(int i1 = 0; i1 < universe.size(); i1++) {
				SpaceObject o1 = universe.get(i1);
				
				o1.update();
				//Update all weapons
				if(o1 instanceof Starship) {
					for(Weapon w : ((Starship) o1).getWeapon()) {
						w.update();
						if (w.getFiring() && (w.getFireCooldownLeft() > w.getFireCooldownMax())) {
							print("--> " + (w.getOwner() == player ? "Human" : "Computer") + " Shot First");
							Projectile shot = w.createShot();
							createSpaceObject(shot);
							w.setFireCooldownLeft(0);
							print("<--" + (w.getOwner() == player ? "Human" : "Computer") + " Shot First");
						}
					}
				}
				for(int i2 = i1 + 1; i2 < universe.size(); i2++) {
					SpaceObject o2 = universe.get(i2);
					if(objectsIntersect(o1, o2)) {
						if(o1 instanceof Starship && o2 instanceof Starship) {
							System.out.println("Starship Collision");
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
					}
				}
			}
			
			universe.removeIf((SpaceObject o) -> {
				return !o.getActive();
			});
			
			//Add objects to be created and remove objects to be destroyed
			for(SpaceObject o : objectsCreated) {
				universeAdd(o);
			}
			objectsCreated.clear();
			for(NewtonianMotion d : objectsDestroyed) {
				universeRemove(d);
			}
			objectsDestroyed.clear();
		} else {
			//player.update();
		}
	}
	public void updateDraw(Graphics g) {
		Graphics2D g2D = ((Graphics2D) g);
		//g2D.rotate(-Math.toRadians(pos_r_player));
		double pos_x_player = player.getPosX();
		double pos_y_player = player.getPosY();
		double pos_r_player = player.getPosR();
		
		double translateX = GameWindow.SCREEN_CENTER_X - pos_x_player;
		double translateY = GameWindow.SCREEN_CENTER_Y + pos_y_player;
		
		switch(camera) {
		case FOLLOW_PLAYER:
			g2D.translate(translateX, translateY);
			break;
		default:
			break;
		}
		
		drawUniverse(g2D);
		
		switch(camera) {
		case FOLLOW_PLAYER:
			g2D.translate(-translateX, -translateY);
			break;
		default:
			break;
		}
		
		//Print all current debug messages on screen. Debug list will only clear when the game is active.
		g2D.setColor(Color.WHITE);
		g2D.setFont(new Font("Consolas", Font.PLAIN, 18));
		final int line_height = 18;
		int print_y = line_height;
		for(String s: debugQueue)
		{
			g2D.drawString(s, 10, print_y);
			print_y += line_height;
		}
		debugQueue.clear();
		
		g2D.dispose();
	}
	public void drawUniverse(Graphics2D g2D) {
		g2D.scale(1, -1);
		
		for(SpaceObject o: universe)
		{
			o.draw(g2D);
			if(o instanceof Starship) {
				for(Weapon w : ((Starship) o).getWeapon()) {
					w.draw(g2D);
				}
			}
		}
		g2D.scale(1, -1);
	}

	public void printToScreen(String text)
	{
		debugQueue.add(text);
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
		player.setFiringMouse(true);
		/*
		enemy_test.clearOrders();
		enemy_test.addOrder(new Order_AttackOrbit(enemy_test, player));
		*/
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		player.setFiringMouse(false);
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
		setKeyState(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		setKeyState(e.getKeyCode(), false);
	}
	public void setKeyState(int code, boolean state) {
		switch(code) {
		case KeyEvent.VK_UP:	player.setThrusting(state);		break;
		case KeyEvent.VK_DOWN:	player.setBraking(state);		break;
		case KeyEvent.VK_LEFT:	player.setTurningCCW(state);	break;
		case KeyEvent.VK_RIGHT:	player.setTurningCW(state);		break;
		case KeyEvent.VK_SHIFT:	player.setStrafing(state);		break;
		case KeyEvent.VK_X:		player.setFiringKey(state);		break;
		
		case KeyEvent.VK_Z:		active = !state;				break;
		case KeyEvent.VK_ESCAPE:	System.exit(0);
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
		for(Polygon part : a.getBody().getShapes())
		{
			areaA.add(new Area(part));
		}
		
		Area areaB = new Area();
		for(Polygon part : b.getBody().getShapes())
		{
			areaB.add(new Area(part));
		}
		
		areaA.intersect(areaB);
		return !areaA.isEmpty();
	}
	//Use these when the universe is in the middle of updating
	public void createSpaceObject(SpaceObject so) {
		objectsCreated.add(so);
	}
	public void destroySpaceObject(SpaceObject so) {
		objectsDestroyed.add(so);
	}
	/*
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
	*/
/*
	public void addAsteroid(Asteroid asteroid) {
		//asteroids.add(asteroid);
		universe.add(asteroid);
	}
*/
	//Warning: Do not use during universe iteration
	private void universeAdd(SpaceObject so) {
		universe.add(so);
	}
	private void universeRemove(NewtonianMotion so) {
		universe.remove(so);
	}
	private void universeRemove(int i) {
		universe.remove(i);
	}
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
		for(NewtonianMotion o : universe) {
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
		for(NewtonianMotion o : universe) {
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
	public boolean isAlive(NewtonianMotion what)
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
	/*
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
	*/
	public void collisionStarshipProjectile(Starship s1, Projectile p1)
	{
		s1.damage(p1.getDamage());
		s1.onAttacked(p1.getOwner());
		p1.destroy();
	}
	public void collisionStarshipStarship(Starship s1, Starship s2)
	{
		//print("--> GamePanel: Starship-Starship Collision");
		double angle_s1 = s1.getVelAngle(); //angleBetween(s1, s2);
		double angle_s2 = s2.getVelAngle(); //angleBetween(s2, s1);
		double kinetic_energy_total = s1.getKineticEnergyAngled(angle_s1) + s2.getKineticEnergyAngled(angle_s2);
		double kinetic_energy_half = kinetic_energy_total / 2;
		s1.accelerateEnergy(angle_s2, kinetic_energy_half);
		s2.accelerateEnergy(angle_s1, kinetic_energy_half);
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
