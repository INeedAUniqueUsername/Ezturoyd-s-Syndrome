package space;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import behavior.orders.Order_Escort;
import game.GamePanel;
import game.GameWindow;
import space.Starship.Sovereign;

public class Level_Waves extends Level {
	ArrayList<Wave> waves;
	public Level_Waves() {
		Starship player = GamePanel.getWorld().getPlayer();
		
		Starship_NPC enemy_0 = Factory_Starship.createEnemy2();
		enemy_0.setPosRectangular(GameWindow.SCREEN_CENTER_X/2, GameWindow.SCREEN_CENTER_Y/2);
		enemy_0.getController().addOrder(new Order_Escort(enemy_0, player));
		enemy_0.setAlignment(Sovereign.PLAYER);
		
		Starship_NPC enemy_1a = createEnemyStarship(), enemy_1b = createEnemyStarship(), enemy_1c = createEnemyStarship();
		
		enemy_1a.installWeapon(new Weapon());
		enemy_1b.installWeapon(new Weapon());
		enemy_1c.installWeapon(new Weapon());
		
		enemy_1b.setThrust(2);
		enemy_1b.setMax_speed(5);
		
		//enemy_1a.addOrderAttackDirect(player);
		//enemy_1b.addOrderAttackDirect(player);
		//enemy_1c.addOrderAttackDirect(player);
		
		Starship_NPC enemy_2a = createEnemyStarship(), enemy_2b = createEnemyStarship(), enemy_2c = createEnemyStarship();
		
		
		//enemy_2a.addOrderAttackOrbit(player);
		enemy_2a.setMax_speed(12);
		enemy_2a.setRotation_max(24);
		enemy_2b.setMax_speed(4);
		enemy_2b.setThrust(2);
		enemy_2b.setRotation_accel(0.3);
		enemy_2b.setRotation_decel(0.2);
		enemy_2c.setMax_speed(10);
		enemy_2c.setRotation_max(21);
		
		
		
		enemy_2a.installWeapon(new Weapon(0, 0, 0, 10, 20, 12, 50));
		enemy_2b.installWeapon(new Weapon(0, 0, 0, 30, 40, 30, 30));
		enemy_2c.installWeapon(new Weapon(0, 0, 0, 10, 20, 12, 50));
		
		//enemy_2a.addOrderAttackOrbit(player);
		//enemy_2b.addOrderAttackDirect(player);
		//enemy_2c.addOrderEscort(enemy_2a);
		
		player.setStructure(100);
		
		setWaves(
				new Wave(enemy_0),
				new Wave(enemy_1a, enemy_1b, enemy_1c),
				new Wave(enemy_2a, enemy_2b, enemy_2c));
	}
	public void setWaves(Wave... w) {
		this.waves = new ArrayList<>();
		this.waves.addAll(Arrays.asList(w));
	}
	public void addWave(Wave w) {
		waves.add(w);
	}
	public void setPosOffscreen(Starship s) {
		int x = 0;
		int y = 0;
		int borderDistance = 100;
		double roll = Math.random() * 3;
		if(roll < 1) {
			//Spawn somewhere past the left or right border
			x = Math.random() > 0.5 ? GameWindow.GAME_WIDTH + borderDistance : -borderDistance;
			y = (int) (Math.random() * (GameWindow.GAME_HEIGHT + (2 * borderDistance) - borderDistance));
		}
		else if(roll < 2) {
			//Spawn somewhere past the left or right border
			x = (int) (Math.random() * (GameWindow.GAME_HEIGHT + (2 * borderDistance) - borderDistance));
			y = Math.random() > 0.5 ? GameWindow.GAME_WIDTH + borderDistance : -borderDistance;
		} else {
			//On one of the corners
			x = Math.random() > 0.5 ? GameWindow.GAME_WIDTH + borderDistance : -borderDistance;
			y = Math.random() > 0.5 ? GameWindow.GAME_WIDTH + borderDistance : -borderDistance;
		}
		s.setPosRectangular(x, y);
	}
	public Starship_NPC createEnemyStarship() {
		Starship_NPC s = new Starship_NPC();
		setPosOffscreen(s);
		return s;
	}
	public void update() {
		if(waves.size() == 0) {
		}
		else if(waves.get(0).getActiveShips().size() == 0) {
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
		for(Starship s : ships) {
			GamePanel.getWorld().createSpaceObject(s);
		}
	}
	public ArrayList<Starship> getActiveShips() {
		ArrayList<Starship> result = new ArrayList<>();
		for(Starship s : ships) {
			if(s.getActive()) {
				result.add(s);
			}
		}
		return result;
	}
	public boolean areAllShipsInactive() {
		//If we have any active ships, then we immediately return false
		for(Starship s : ships) {
			if(s.getActive()) {
				return false;
			}
		}
		return true;
	}
}