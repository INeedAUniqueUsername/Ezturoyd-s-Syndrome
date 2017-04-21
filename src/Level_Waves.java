import java.util.ArrayList;
import java.util.Arrays;

public class Level_Waves extends Level {
	ArrayList<Wave> waves;
	public Level_Waves() {
		Starship player = GamePanel.world.getPlayer();
		Starship_NPC enemy_1A = createEnemyStarship();
		Starship_NPC enemy_1B = createEnemyStarship();
		Starship_NPC enemy_1C = createEnemyStarship();
		
		enemy_1A.installWeapon(new Weapon());
		enemy_1B.installWeapon(new Weapon());
		enemy_1C.installWeapon(new Weapon());
		
		enemy_1A.addOrder(new Order_Escort(enemy_1A, enemy_1B, 45, 30));
		enemy_1B.addOrder(new Order_Attack(enemy_1B, player));
		enemy_1C.addOrder(new Order_Escort(enemy_1C, enemy_1B, 135, 30));
		
		setWaves(new Wave(enemy_1A, enemy_1B, enemy_1C));
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
			x = Math.random() > 0.5 ? GameWindow.WIDTH + borderDistance : -borderDistance;
			y = (int) (Math.random() * (GameWindow.HEIGHT + (2 * borderDistance) - borderDistance));
		}
		else if(roll < 2) {
			//Spawn somewhere past the left or right border
			x = (int) (Math.random() * (GameWindow.HEIGHT + (2 * borderDistance) - borderDistance));
			y = Math.random() > 0.5 ? GameWindow.WIDTH + borderDistance : -borderDistance;
		} else {
			//On one of the corners
			x = Math.random() > 0.5 ? GameWindow.WIDTH + borderDistance : -borderDistance;
			y = Math.random() > 0.5 ? GameWindow.WIDTH + borderDistance : -borderDistance;
		}
	}
	public Starship_NPC createEnemyStarship() {
		Starship_NPC s = new Starship_NPC();
		setPosOffscreen(s);
		return s;
	}
	public void update() {
		
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
			GamePanel.world.addStarship(s);
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