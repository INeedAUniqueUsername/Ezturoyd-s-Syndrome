package game;

import static game.GameWindow.SCREEN_HEIGHT;
import static game.GameWindow.SCREEN_WIDTH;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JPanel;
import javax.swing.Timer;

import capture.GifSequenceWriter;
import display.ScreenDamage;
import factories.StarshipFactory;
import helpers.SpaceHelper;
import interfaces.NewtonianMotion;
import space.BackgroundStar;
import space.Level;
import space.Level_Waves;
import space.Projectile;
import space.SpaceObject;
import space.Starship;
import space.Starship_Player;
import space.Weapon;

public class GamePanel extends JPanel implements ActionListener, MouseListener, KeyListener {
	private boolean active = true;
	private boolean cheat_playerActive = true;
	private boolean strafeMode = false;

	public enum CameraMode {
		FIXED, FOLLOW_PLAYER
	}
	
	public enum VerticalDirection {
		UP, DOWN, NONE;
	}
	public enum HorizontalDirection {
		RIGHT, LEFT, NONE;
	}
	
	public VerticalDirection verticalScroll = VerticalDirection.NONE;
	public HorizontalDirection horizontalScroll = HorizontalDirection.NONE;

	private static final CameraMode camera = CameraMode.FOLLOW_PLAYER;
	public static final double epsilon = .000000000000000000000000000000000000001;
	public static final double LIGHT_SPEED = 30;
	private int cameraOffset_x, cameraOffset_y;
	final int INTERVAL = 1000/30;
	private SpaceObject pov;
	private Starship_Player player;
	// private Starship_NPC enemy_test;
	private ArrayList<SpaceObject> universe;
	private ArrayList<SpaceObject> objectsCreated;
	private ArrayList<SpaceObject> objectsDestroyed;
	private ArrayList<BackgroundStar> background;
	private Level currentLevel;

	private long score = 0;

	// ScreenCracking screenEffect = new
	// ScreenCracking(GameWindow.SCREEN_CENTER_X, GameWindow.SCREEN_CENTER_Y,
	// 10);
	ScreenDamage screenEffect = new ScreenDamage(new Point(GameWindow.SCREEN_CENTER_X, GameWindow.SCREEN_CENTER_Y));
	/*
	 * ArrayList<Starship> starships; ArrayList<Projectile> projectiles;
	 * ArrayList<Asteroid> asteroids;
	 */
	private ArrayList<String> debugPrint = new ArrayList<String>(0);
	private ArrayList<Consumer<Graphics>> debugDraw = new ArrayList<Consumer<Graphics>>(0);

	// counter for hits
	// private int hits = 0;

	private long tick;
	private boolean gameover = false;
	private boolean scrollBackToPlayer = true;
	private static GamePanel world;
	private BufferedImage lastFrame;
	ImageOutputStream output;
	GifSequenceWriter capture;
	boolean recording;
	public GamePanel() {
		Timer ticker = new Timer(INTERVAL, this);
		ticker.start();
		addKeyListener(this);
		addMouseListener(this);
		world = this;
	}

	public static GamePanel getWorld() {
		return world;
	}

	public static CameraMode getCameraMode() {
		return camera;
	}

	public void newGame() {
		
		try {
			recording = false;
			output = new FileImageOutputStream(new File("Demo.gif"));
			capture = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 1, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setTick(0);
		print("*" + getTick() + "*");

		universe = new ArrayList<SpaceObject>(0);
		objectsCreated = new ArrayList<SpaceObject>(0);
		objectsDestroyed = new ArrayList<SpaceObject>(0);
		background = new ArrayList<BackgroundStar>(0);
		for (int i = 0; i < 400; i++) {
			background.add(new BackgroundStar(GameWindow.randomGameWidth(), GameWindow.randomGameHeight(),
					SpaceHelper.random(360), 5));
		}

		BufferedImage backgroundImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics backgroundG = backgroundImage.getGraphics();
		backgroundG.setColor(Color.BLACK);
		backgroundG.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		for (BackgroundStar o : background) {
			o.draw(backgroundG);
		}
		GameWindow.writeImage(backgroundImage, "Background");
		
		// starships = new ArrayList<Starship>();
		// projectiles = new ArrayList<Projectile>();
		// asteroids = new ArrayList<Asteroid>();

		player = StarshipFactory.createPlayership();
		player.setPosRectangular(GameWindow.GAME_WIDTH / 2, GameWindow.GAME_HEIGHT / 2);
		pov = player;

		universeAdd(player);

		/*
		 * ∂ enemy_test = new Starship_NPC(); addStarship(enemy_test);
		 * enemy_test.setPosRectangular(400, 225); enemy_test.setName("Enemy");
		 * addWeapon(enemy_test, new Weapon(0, 10, 0, 5, 15, 1, 90, Color.RED));
		 * enemy_test.addOrder(new Order_Escort(enemy_test, player));
		 */
		currentLevel = new Level_Waves();
		currentLevel.start();
		lastFrame = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics g = lastFrame.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		setBackground(Color.BLACK);
	}

	public void paintComponent(Graphics g) {
		// g.clearRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
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
		super.paintComponent(g);
		if (active) {
			tick++;
			updateUniverse();
			updateDraw(lastFrame.createGraphics());
			g.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			g.drawImage(lastFrame, 0, 0, this);
			if(recording) {
				try {
					capture.writeToSequence(lastFrame);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			/*
			 * BufferedImage b = new BufferedImage(GameWindow.SCREEN_WIDTH,
			 * GameWindow.SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			 * updateDraw(b.getGraphics()); video.add(b);
			 */
			 
		}

	}

	public void updateUniverse() {
		//Score is meaningless
		if(player.getActive()) {
			score += tick / 30;
		}
		// Update everything
		currentLevel.update();

		for (BackgroundStar b : background) {
			// b.update();
		}
		for (int i1 = 0; i1 < universe.size(); i1++) {
			SpaceObject o1 = universe.get(i1);

			o1.update();
			// Update all weapons
			if (o1 instanceof Starship) {
				for (Weapon w : ((Starship) o1).getWeapon()) {
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
			for (int i2 = i1 + 1; i2 < universe.size(); i2++) {
				SpaceObject o2 = universe.get(i2);
				Area intersection = getIntersection(o1, o2);
				if (!intersection.isEmpty()) {
					if (o1 instanceof Starship && o2 instanceof Starship) {
						System.out.println("Starship Collision");
						collisionStarshipStarship((Starship) o1, (Starship) o2, intersection);
					} else if (o1 instanceof Starship && o2 instanceof Projectile) {
						Starship s = (Starship) o1;
						Projectile p = (Projectile) o2;
						if (!p.getOwner().equals(s)) {
							collisionStarshipProjectile(s, p, intersection);
						}
					} else if (o1 instanceof Projectile && o2 instanceof Starship) {
						Starship s = (Starship) o2;
						Projectile p = (Projectile) o1;
						if (!p.getOwner().equals(s)) {
							collisionStarshipProjectile(s, p, intersection);
						}
					}
					/*
					 * else if (o1 instanceof Projectile && o2 instanceof
					 * Projectile) { collisionProjectileProjectile((Projectile)
					 * o1, (Projectile) o2, intersection); }
					 */
				}
			}
		}

		universe.removeIf((SpaceObject o) -> {
			return !o.getActive();
		});

		// Add objects to be created and remove objects to be destroyed
		for (SpaceObject o : objectsCreated) {
			universeAdd(o);
		}
		objectsCreated.clear();
		for (NewtonianMotion d : objectsDestroyed) {
			universeRemove(d);
		}
		objectsDestroyed.clear();
	}

	public void updateDraw(Graphics g) {
		int scrollRate = 50;
		switch(verticalScroll) {
		case UP:
			cameraOffset_y += scrollRate;
			break;
		case DOWN:
			cameraOffset_y -= scrollRate;
			break;
		}
		switch(horizontalScroll) {
		case RIGHT:
			cameraOffset_x += scrollRate;
			break;
		case LEFT:
			cameraOffset_x -= scrollRate;
			break;
		}
		if(scrollBackToPlayer) {
			cameraOffset_x *= 0.95;
			cameraOffset_y *= 0.88;
		}
		int lowerLimit = 25;
		g.setColor(new Color(0, 0, 0, 25 + (int) (175 * Math.pow(((double) Math.max(player.getStructure() - lowerLimit, 0) / (player.getStructureMax() - lowerLimit)), 2))));
		g.fillRect(0, 0, GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT);

		Graphics2D g2D = ((Graphics2D) g);
		// g2D.rotate(-Math.toRadians(pos_r_player));
		double pos_x_pov = pov.getPosX();
		double pos_y_pov = pov.getPosY();
		double pos_r_pov = pov.getPosR();

		double translateX = GameWindow.SCREEN_CENTER_X - (pos_x_pov + cameraOffset_x);
		double translateY = GameWindow.SCREEN_CENTER_Y + (pos_y_pov + cameraOffset_y);

		g2D.translate(translateX, translateY);
		drawUniverse(g2D);
		g2D.translate(-translateX, -translateY);

		g2D.translate(-cameraOffset_x, cameraOffset_y);
		screenEffect.draw(g2D);
		g2D.translate(cameraOffset_x, -cameraOffset_y);

		// Print all current debug messages on screen. Debug list will only
		// clear when the game is active.
		g2D.setColor(Color.WHITE);
		g2D.setFont(new Font("Consolas", Font.PLAIN, 18));
		final int line_height = 18;
		int print_y = line_height;

		if(player.getActive()) {
			g2D.drawString("Score: " + score, 10, print_y);
			print_y += line_height;

			//g2D.drawString("Structure: " + player.getStructure(), 10, print_y);
			g2D.setColor(Color.GREEN);
			g2D.fillRect(10, print_y, (180 * player.getStructure()) / player.getStructureMax(), line_height);
		} else {
			if(!gameover) {
				gameover = true;
				tick = 0;
				return;
			}
			System.out.println("Dead: " + tick);
			if (tick % 90 > 45) {
				drawStringCentered(g, "Final Score: " + score, 48, Color.red);
			}
		}
		
		for (String s : debugPrint) {
			//g2D.drawString(s, 10, print_y);
			print_y += line_height;
		}
		debugPrint.clear();
	}

	public void drawUniverse(Graphics2D g2D) {
		g2D.scale(1, -1);

		for (Consumer<Graphics> c : debugDraw) {
			c.accept(g2D);
		}
		debugDraw.clear();

		for (BackgroundStar b : background) {
			b.draw(g2D);
		}
		for (SpaceObject o : universe) {
			o.draw(g2D);

			if (o instanceof Starship) {
				for (Weapon w : ((Starship) o).getWeapon()) {
					w.draw(g2D);
				}
			}
		}
		g2D.scale(1, -1);
	}

	public void printToScreen(String text) {
		debugPrint.add(text);
	}

	public void drawToScreen(Consumer<Graphics> c) {
		debugDraw.add(c);
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
		setMouseState(e.getButton(), true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setMouseState(e.getButton(), false);
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void setMouseState(int code, boolean state) {
		switch(code) {
		case MouseEvent.BUTTON1:
			player.setFiringMouse(state);
			break;
		/*
		case MouseEvent.BUTTON3:
			SpaceHelper.random(getStarships()).damage(10);
			break;
		*/
		}
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
		switch (code) {
		case KeyEvent.VK_UP:
			player.setThrusting(state);
			break;
		case KeyEvent.VK_DOWN:
			player.setBraking(state);
			break;
		case KeyEvent.VK_LEFT:
			if (strafeMode) {
				player.setStrafingLeft(state);
			} else {
				player.setTurningCCW(state);
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (strafeMode) {
				player.setStrafingRight(state);
			} else {
				player.setTurningCW(state);
			}
			break;
		case KeyEvent.VK_W:
			if (state){// && cameraOffset_y < GameWindow.SCREEN_CENTER_Y-200)
				//cameraOffset_y += 50 * 5;
				verticalScroll = VerticalDirection.UP;
			} else {
				if(verticalScroll == VerticalDirection.UP) {
					verticalScroll = VerticalDirection.NONE;
				}
			}
			break;
		case KeyEvent.VK_S:
			if (state){// && cameraOffset_y > 200-GameWindow.SCREEN_CENTER_Y)
				//cameraOffset_y -= 50 * 5;
				verticalScroll = VerticalDirection.DOWN;
			} else {
				if(verticalScroll == VerticalDirection.DOWN) {
					verticalScroll = VerticalDirection.NONE;
				}
			}
			break;
		case KeyEvent.VK_A:
			if (state){// && cameraOffset_x > 200-GameWindow.SCREEN_CENTER_X)
				//cameraOffset_x -= 50 * 5;
				horizontalScroll = HorizontalDirection.LEFT;
			} else {
				if(horizontalScroll == HorizontalDirection.LEFT) {
					horizontalScroll = HorizontalDirection.NONE;
				}
			}
			break;
		case KeyEvent.VK_D:
			if (state){// && cameraOffset_x < GameWindow.SCREEN_CENTER_X-200) {
				//cameraOffset_x += 50 * 5;
				horizontalScroll = HorizontalDirection.RIGHT;
			}
			else {
				if(horizontalScroll == HorizontalDirection.RIGHT) {
					horizontalScroll = HorizontalDirection.NONE;
				}
			}
			break;
		case KeyEvent.VK_F:
			if(state) {
				scrollBackToPlayer = !scrollBackToPlayer;
			}
			
			break;
		case KeyEvent.VK_E:
			if (state) {
				for (int i = 0; i < universe.size(); i++) {
					SpaceObject o = universe.get(i);
					if (o instanceof Starship && ((Starship) o).getAlignment().equals(Starship.Sovereign.ENEMY)
							&& i > universe.indexOf(pov)) {
						pov = o;
						break;
					}
				}
			}
			break;
		case KeyEvent.VK_Q:
			if (state) {
				for (int i = 0; i < universe.size(); i++) {
					SpaceObject o = universe.get(i);
					if (o instanceof Starship && ((Starship) o).getAlignment().equals(Starship.Sovereign.ENEMY)
							&& i < universe.indexOf(pov)) {
						pov = o;
						break;
					}
				}
			}
			break;
		case KeyEvent.VK_SHIFT:
			strafeMode = state;
			break;
		case KeyEvent.VK_C:
			int scale = 2;
			BufferedImage previewImage = new BufferedImage(SCREEN_WIDTH*scale, SCREEN_HEIGHT*scale, BufferedImage.TYPE_INT_ARGB);
			Graphics2D previewG = (Graphics2D) previewImage.getGraphics();
			previewG.setColor(Color.BLACK);
			previewG.fillRect(0, 0, SCREEN_WIDTH*scale, SCREEN_HEIGHT*scale);
			previewG.scale(scale, scale);
			//previewG.translate(-SCREEN_WIDTH * 2.5, -SCREEN_HEIGHT * 2.5);
			updateDraw(previewG);
			GameWindow.writeImage(previewImage, "Preview2");
			break;
		case KeyEvent.VK_R:
			if(state) {
				recording = !recording;
			}
			
			break;
		case KeyEvent.VK_X:
			player.setFiringKey(state);
			break;
		case KeyEvent.VK_Z:
			if(state) {
				active = !active;
			}
			break;
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		case KeyEvent.VK_BACK_SPACE:
			newGame();
			break;
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

	public static Area getIntersection(SpaceObject a, SpaceObject b) {
		Area areaA = new Area();
		for (Polygon part : a.getBody().getShapes()) {
			areaA.add(new Area(part));
		}

		Area areaB = new Area();
		for (Polygon part : b.getBody().getShapes()) {
			areaB.add(new Area(part));
		}

		areaA.intersect(areaB);
		return areaA;
	}

	// Use these when the universe is in the middle of updating
	public void createSpaceObject(SpaceObject so) {
		objectsCreated.add(so);
	}

	public void destroySpaceObject(SpaceObject so) {
		objectsDestroyed.add(so);
	}

	/*
	 * public void addWeapon(Starship ship, Weapon item) {
	 * ship.installWeapon(item); }
	 * 
	 * public void addStarship(Starship ship) { //starships.add(ship);
	 * universe.add(ship); }
	 * 
	 * public void addProjectile(Projectile projectile) {
	 * //projectiles.add(projectile); universe.add(projectile); }
	 */
	/*
	 * public void addAsteroid(Asteroid asteroid) { //asteroids.add(asteroid);
	 * universe.add(asteroid); }
	 */
	// Warning: Do not use during universe iteration
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
	 * public void removeStarship(Starship ship) { universe.remove(ship);
	 * starships.remove(ship); }
	 * 
	 * public void removeProjectile(Projectile projectile) {
	 * universe.remove(projectile); projectiles.remove(projectile); }
	 */

	/*
	 * public void removeAsteroid(Asteroid asteroid) {
	 * universe.remove(asteroid); asteroids.remove(asteroid); }
	 */
	public ScreenDamage getScreenDamage() {
		return screenEffect;
	}

	public Starship getPlayer() {
		return player;
	}

	public ArrayList<SpaceObject> getUniverse() {
		return universe;
	}

	public ArrayList<Starship> getStarships() {
		// return starships;
		ArrayList<Starship> result = new ArrayList<Starship>();
		for (NewtonianMotion o : universe) {
			if (o instanceof Starship) {
				result.add((Starship) o);
			}
		}
		return result;
	}

	public ArrayList<Projectile> getProjectiles() {
		// return projectiles;
		ArrayList<Projectile> result = new ArrayList<Projectile>();
		for (NewtonianMotion o : universe) {
			if (o instanceof Projectile) {
				result.add((Projectile) o);
			}
		}
		return result;
	}
	/*
	 * public ArrayList<Asteroid> getAsteroids() { return asteroids; }
	 */

	public double angleBetween(SpaceObject from, SpaceObject to) {
		return to.getAngleFrom(from);
	}

	public boolean exists(Object what) {
		return what != null;
	}

	public boolean isAlive(NewtonianMotion what) {
		boolean result = false;
		if (what instanceof Starship) {
			result = getStarships().contains(what);
		}
		/*
		 * else if(what instanceof Asteroid) { result =
		 * asteroids.contains(what); }
		 */
		else if (what instanceof Projectile) {
			result = getProjectiles().contains(what);
		}
		return result;
	}

	public void print(String message) {
		System.out.println(getTick() + ". " + message);
	}

	/*
	 * public void collisionAsteroidStarship(Asteroid_Deprecated_2 a, Starship
	 * s) { //print("--> Collision (Starship)"); double angle_asteroid_to_ship =
	 * a.getAngleTowards(s); double angle_ship_to_asteroid = a.getAngleFrom(s);
	 * double asteroidKineticEnergy =
	 * a.getKineticEnergyAngled(angle_asteroid_to_ship); double
	 * shipKineticEnergy = s.getKineticEnergyAngled(angle_ship_to_asteroid);
	 * double totalKineticEnergy = asteroidKineticEnergy + shipKineticEnergy;
	 * double halfKineticEnergy = totalKineticEnergy / 2;
	 * s.impulse(angle_ship_to_asteroid, halfKineticEnergy);
	 * a.impulse(angle_asteroid_to_ship, halfKineticEnergy);
	 * 
	 * s.damage(halfKineticEnergy / 100);
	 * 
	 * //print("Angle (Asteroid --> Ship): " + angle_asteroid_to_ship); //print(
	 * "Angle (Asteroid <-- Ship): " + angle_ship_to_asteroid); //print(
	 * "Momentum (Asteroid) " + asteroidMomentum); //print("Momentum (Ship): " +
	 * shipMomentum); //print("<-- Collision (Starship)"); } public void
	 * collisionAsteroidAsteroid(Asteroid_Deprecated_2 a1, Asteroid_Deprecated_2
	 * a2) { double angle_a1_to_a2 = a1.getAngleTowards(a2); double
	 * angle_a2_to_a1 = a2.getAngleTowards(a1); double halfKineticEnergy =
	 * a1.getKineticEnergyAngled(angle_a1_to_a2) +
	 * a2.getKineticEnergyAngled(angle_a2_to_a1); a1.impulse(angle_a2_to_a1,
	 * halfKineticEnergy); a2.impulse(angle_a1_to_a2, halfKineticEnergy);
	 * a1.damage((int) halfKineticEnergy/1000, a2.getPosX(), a2.getPosY());
	 * a2.damage((int) halfKineticEnergy/1000, a1.getPosX(), a1.getPosY()); }
	 */
	public void collisionStarshipProjectile(Starship s1, Projectile p1, Area intersection) {
		s1.damage(p1.getDamage());
		s1.onAttacked(p1.getOwner());
		s1.accelerateEnergy(p1.getVelAngle(), p1.getKineticEnergy());
		p1.destroy();

		if (p1.getOwner() == player) {
			//score += p1.getDamage() * (0.2 * s1.getDistanceBetween(player)) / (0.1 * p1.getLifetime());
			score += p1.getDamage() * ((tick % 300) / 30);
		} else if (s1 == player) {
			score += p1.getDamage() * ((200 - player.getStructure()) / player.getStructureMax());
		}
	}

	public void collisionStarshipStarship(Starship s1, Starship s2, Area intersection) {
		// print("--> GamePanel: Starship-Starship Collision");
		double angle_s1 =
				// s1.getVelAngle()
				angleBetween(s1, s2);
		double angle_s2 =
				// s2.getVelAngle()
				angleBetween(s2, s1);

		// Old collision model
		/*
		 * double kinetic_energy_total = s1.getKineticEnergyAngled(angle_s1) +
		 * s2.getKineticEnergyAngled(angle_s2); double kinetic_energy_half =
		 * kinetic_energy_total / 2; s1.accelerateEnergy(angle_s2,
		 * kinetic_energy_half); s2.accelerateEnergy(angle_s1,
		 * kinetic_energy_half);
		 */

		/*
		 * double angle_diff_ccw = Helper.modRangeDegrees(angle_s1 - angle_s2);
		 * double angle_diff_cw = Helper.modRangeDegrees(angle_s2 - angle_s1);
		 * double angle_diff = Helper.min(angle_diff_ccw, angle_diff_cw);
		 */
		double velAngle_s1 = s1.getVelAngle();
		double velAngle_s2 = s2.getVelAngle();
		double velAngle_diff_ccw = SpaceHelper.modRangeDegrees(velAngle_s1 - velAngle_s2);
		double velAngle_diff_cw = SpaceHelper.modRangeDegrees(velAngle_s2 - velAngle_s1);
		double velAngle_diff = SpaceHelper.min(velAngle_diff_ccw, velAngle_diff_cw);

		double impactEnergy_s1 = Math
				.abs(s1.getKineticEnergy() - s2.getKineticEnergy() * SpaceHelper.cosDegrees(velAngle_diff));
		double impactEnergy_s2 = Math
				.abs(s2.getKineticEnergy() - s1.getKineticEnergy() * SpaceHelper.cosDegrees(velAngle_diff));

		s1.accelerateEnergy(angle_s2, impactEnergy_s1 * 0.01);
		s2.accelerateEnergy(angle_s1, impactEnergy_s2 * 0.01);
		s1.damage((int) (impactEnergy_s1 / s1.getMass()));
		s2.damage((int) (impactEnergy_s1 / s2.getMass()));
		// print("<-- GamePanel: Starship-Starship Collision");
	}

	public void collisionProjectileProjectile(Projectile p1, Projectile p2, Area intersection) {
		p1.damage(p2.getDamage());
		p2.damage(p1.getDamage());

		p1.accelerateEnergy(p2.getVelAngle(), p2.getKineticEnergy());
		p2.accelerateEnergy(p1.getVelAngle(), p1.getKineticEnergy());

		p1.destroy();
		p2.destroy();
	}

	public long getTick() {
		return tick;
	}

	public void setTick(long tick) {
		this.tick = tick;
	}

	public void drawStringCentered(Graphics g, String s, int size, Color color) {
		g.setFont(new Font("Consolas", Font.BOLD, size));
		g.setColor(color);
		g.drawString(s, (GameWindow.SCREEN_WIDTH / 2) - (g.getFontMetrics().stringWidth(s) / 2),
				(GameWindow.SCREEN_HEIGHT / 2) - (size / 2)
				);
	}

}
