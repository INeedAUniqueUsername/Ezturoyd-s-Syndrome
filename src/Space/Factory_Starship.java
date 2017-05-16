package Space;

import java.awt.Color;
import java.awt.Graphics;

import Body.Body;
import Body.Body_Starship;
import Override.Polygon2;

//Please do not call our establishment a "factory." Unlike "factories," we don't just mass produce bunches of triangles and call them starships; we manufacture premium-quality vehicles for noble travellers of space.
public final class Factory_Starship {
	private Factory_Starship() {}
	public static final Starship_Player createPlayership() {
		Starship_Player player = new Starship_Player();
		player.setBody(new Body_Starship(player) {
			public void draw(Graphics g) {
				g.setColor(Color.RED);
				super.draw(g);
			}
		});
		player.installWeapon(new Weapon_Mouse(0, 0, 0, 5, 30, 10, 90));
		player.installWeapon(new Weapon_Key(0, 25, 0, 5, 30, 10, 90));
		player.setName("Player");
		return player;
	}
	public static final Starship_NPC createEnemy() {
		Starship_NPC result = new Starship_NPC();
		result.installWeapon(new Weapon(0, 20, 0, 5, 30, 10, 90));
		result.setBody(new Body_Starship(result) {
			public void updateShapes() {
				Starship owner = getOwner();
				double pos_r = owner.getPosR();
				setShapes(
						createTriangle(owner.polarOffset(pos_r, 25), pos_r, 30),
						createRectangle(owner.getPos(), pos_r, 30, 10),
						createRectangle(owner.polarOffset(pos_r+150, 30), pos_r, 6, 20),
						createRectangle(owner.polarOffset(pos_r-150, 30), pos_r, 6, 20)
						);
			}
		});
		return result;
	}
	public static final Starship_NPC createEnemy2() {
		Starship_NPC result = new Starship_NPC();
		result.installWeapon(new Weapon(60, 30, -1, 5, 30, 10, 90));
		result.installWeapon(new Weapon(-60, 30, 1, 5, 30, 10, 90));
		result.setBody(new Body_Starship(result) {
			public void updateShapes() {
				Starship owner = getOwner();
				double pos_r = owner.getPosR();
				setShapes(
						createTriangle(owner.polarOffset(pos_r, 25), pos_r, 30),
						createTriangle(owner.polarOffset(pos_r+60, 30), pos_r, 15),
						createTriangle(owner.polarOffset(pos_r-60, 30), pos_r, 15),
						createRectangle(owner.getPos(), pos_r, 20, 10)
						);
			}
		});
		return result;
	}
}
