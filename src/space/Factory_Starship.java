package space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

import body.Body;
import body.Body_Starship;
import body.Body_Weapon;
import override.Polygon2;

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
		Weapon weapon = new Weapon(0, 0, 0, 5, 60, 10, 90);
		weapon.setBody(new Body_Weapon(weapon) {
			public void updateShapes() {
				setShapes(
						createEllipse(result.getPos(), result.getPosR(), 16, 8),
						createEllipse(result.getPos(), result.getPosR(), 5, 5),
						createEllipse(result.getPos(), result.getPosR(), 4, 4),
						createEllipse(result.getPos(), result.getPosR(), 3, 3)
						);
			}
			public void draw(Graphics g) {
				g.setColor(Color.GRAY);
				for(Polygon p : getShapes()) {
					g.drawPolygon(p);
					drawWrapClones(g, p);
				}
			}
		});
		result.installWeapon(weapon);
		result.setBody(new Body_Starship(result) {
			public void updateShapes() {
				Starship owner = getOwner();
				double pos_r = owner.getPosR();
				setShapes(
						createTriangle(owner.getPos(), pos_r, 40)
						);
			}
			public void draw(Graphics g) {
				g.setColor(Color.GRAY);
				drawDefault(g);
			}
		});
		return result;
	}
}
