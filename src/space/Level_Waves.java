package space;

import static java.lang.Math.random;

import java.util.ArrayList;
import java.util.Arrays;

import behavior.orders.Order_AttackDirect;
import behavior.orders.Order_Escort;
import game.GamePanel;
import game.GameWindow;

public class Level_Waves extends Level {
	ArrayList<Wave> waves;

	//Randomize maneuver
	public Level_Waves() {
		Starship player = GamePanel.getWorld().getPlayer();
		/*
		 * Starship_NPC enemy_0 = StarshipFactory.createBasicEnemy2(); enemy_0.setPosRectangular(player.polarOffset(9,
		 * 200)); enemy_0.getController().addOrder(new Order_AttackDirect(enemy_0, player)); Weapon weapon_01 = new
		 * Weapon(); weapon_01.setProjectileSpeed(30); weapon_01.setProjectileDamage(1);
		 * //enemy_0.installWeapon(weapon_01);
		 * 
		 * Starship_NPC enemy_1a = createEnemyStarship(), enemy_1b = createEnemyStarship(), enemy_1c =
		 * createEnemyStarship();
		 * 
		 * enemy_1a.installWeapon(new Weapon(0, 0, 0, 20, 20, 2, 60)); enemy_1b.installWeapon(new Weapon(0, 0, 0, 20,
		 * 20, 2, 60)); enemy_1c.installWeapon(new Weapon(0, 0, 0, 20, 20, 2, 60));
		 * 
		 * enemy_1b.setThrust(2); enemy_1b.setMax_speed(5);
		 * 
		 * enemy_1a.getController().addOrder(new Order_Escort(enemy_1a, enemy_1b));
		 * enemy_1a.getController().addOrder(new Order_AttackOrbit(enemy_1a, player));
		 * 
		 * enemy_1b.getController().addOrder(new Order_AttackOrbit(enemy_1b, player));
		 * 
		 * enemy_1c.getController().addOrder(new Order_Escort(enemy_1c, enemy_1b));
		 * enemy_1c.getController().addOrder(new Order_AttackOrbit(enemy_1c, player));
		 * 
		 * Starship_NPC enemy_2a = createEnemyStarship(), enemy_2b = createEnemyStarship(), enemy_2c =
		 * createEnemyStarship();
		 * 
		 * 
		 * //enemy_2a.addOrderAttackOrbit(player); enemy_2a.setMax_speed(12); enemy_2a.setRotation_max(24);
		 * enemy_2b.setMax_speed(4); enemy_2b.setThrust(2); enemy_2b.setRotation_accel(0.3);
		 * enemy_2b.setRotation_decel(0.2); enemy_2c.setMax_speed(10); enemy_2c.setRotation_max(21);
		 * 
		 * 
		 * 
		 * enemy_2a.installWeapon(new Weapon(0, 0, 0, 10, 20, 3, 50)); enemy_2b.installWeapon(new Weapon(0, 0, 0, 30,
		 * 40, 3, 30)); enemy_2c.installWeapon(new Weapon(0, 0, 0, 10, 20, 3, 50));
		 * 
		 * enemy_2a.getController().addOrder(new Order_AttackDirect(enemy_2a, player));
		 * enemy_2b.getController().addOrder(new Order_AttackDirect(enemy_2b, player));
		 * enemy_2c.getController().addOrder(new Order_AttackOrbit(enemy_2c, player));
		 * 
		 * setWaves( new Wave(enemy_0), new Wave(enemy_1a, enemy_1b, enemy_1c), new Wave(enemy_2a, enemy_2b, enemy_2c));
		 */
		setWaves();
		int count_waves = 20;
		Wave[] waves = new Wave[count_waves];
		for (int i = 0; i < count_waves; i++) {
			int count_enemies = 2 * 2 + i / 2;
			Starship_NPC[] enemies = new Starship_NPC[count_enemies];
			int count_leaders = count_enemies / 4;
			for (int j = 0; j < count_enemies; j++) {
				Starship_NPC enemy = createEnemyStarship();
				enemy.getController().addOrder(j > count_leaders ? new Order_Escort(enemy, enemies[(int) (random() * count_leaders)]) :
				// random() * j < count_enemies/4 ?
						new Order_AttackDirect(enemy, player));

				enemy.installWeapon(new Weapon(0, 0, 0, (int) (5 + random() * 10), (int) (10 + random() * 20), (int) (random() * 4 + 3), (int) (random() * 50 + 20)));
				enemies[j] = enemy;
			}
			waves[i] = new Wave(enemies);
		}
		addWaves(waves);
	}

	public void setWaves(Wave... w) {
		this.waves = new ArrayList<>();
		this.waves.addAll(Arrays.asList(w));
	}

	public void addWaves(Wave... waves) {
		this.waves.addAll(Arrays.asList(waves));
	}

	public void setPosOffscreen(Starship s) {
		int x = 0;
		int y = 0;
		int borderDistance = 100;
		double roll = random() * 3;
		if (roll < 1) {
			// Spawn somewhere past the left or right border
			x = random() > 0.5 ? GameWindow.GAME_WIDTH + borderDistance : -borderDistance;
			y = (int) (random() * (GameWindow.GAME_HEIGHT + (2 * borderDistance) - borderDistance));
		} else if (roll < 2) {
			// Spawn somewhere past the left or right border
			x = (int) (random() * (GameWindow.GAME_HEIGHT + (2 * borderDistance) - borderDistance));
			y = random() > 0.5 ? GameWindow.GAME_WIDTH + borderDistance : -borderDistance;
		} else {
			// On one of the corners
			x = random() > 0.5 ? GameWindow.GAME_WIDTH + borderDistance : -borderDistance;
			y = random() > 0.5 ? GameWindow.GAME_WIDTH + borderDistance : -borderDistance;
		}
		s.setPosRectangular(x, y);
	}

	public Starship_NPC createEnemyStarship() {
		Starship_NPC s = new Starship_NPC();
		setPosOffscreen(s);
		return s;
	}

	public void update() {
		if (waves.size() == 0) {
		} else if (waves.get(0).getActiveShips().size() == 0) {
			System.out.println("Next Wave");
			waves.remove(0);
			waves.get(0).activate();
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		waves.get(0).activate();
	}
}

class Wave {
	ArrayList<Starship> ships;

	public Wave(Starship... ships) {
		setShips(ships);
	}

	public void setShips(Starship... ships) {
		this.ships = new ArrayList<>();
		this.ships.addAll(Arrays.asList(ships));
	}

	public ArrayList<Starship> getShips() {
		return ships;
	}

	public void addShip(Starship s) {
		ships.add(s);
	}

	public void activate() {
		for (Starship s : ships) {
			GamePanel.getWorld().createSpaceObject(s);
		}
	}

	public ArrayList<Starship> getActiveShips() {
		ArrayList<Starship> result = new ArrayList<>();
		for (Starship s : ships) {
			if (s.getActive()) {
				result.add(s);
			}
		}
		return result;
	}

	public boolean areAllShipsInactive() {
		// If we have any active ships, then we immediately return false
		for (Starship s : ships) {
			if (s.getActive()) {
				return false;
			}
		}
		return true;
	}
}