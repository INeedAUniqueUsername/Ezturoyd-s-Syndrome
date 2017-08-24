package factories;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import body.Body;
import body.Body_Starship;
import body.Body_Weapon;
import helpers.SpaceHelper;
import override.Polygon2;
import space.Starship;
import space.Starship_NPC;
import space.Starship_Player;
import space.Weapon;
import space.Weapon_Key;
import space.Weapon_Mouse;

//Please do not call our establishment a "factory." Unlike "factories," we don't just mass produce bunches of triangles and call them starships; we manufacture premium-quality vehicles for noble travellers of space.
public final class StarshipFactory {
	private StarshipFactory() {}
	public static final Starship_Player createPlayership() {
		Starship_Player player = new Starship_Player();
		player.setBody(new Body_Starship(player) {
			public void draw(Graphics g) {
				g.setColor(Color.RED);
				super.draw(g);
			}
		});
		player.installWeapon(new Weapon_Mouse(0, 0, 0, 10, 50, 210, 120));
		player.installWeapon(new Weapon_Key(0, 25, 0, 5, 40, 110, 90));
		player.setName("Player");
		return player;
	}
	public static final Starship_NPC createBasicEnemy() {
		Starship_NPC result = new Starship_NPC();
		Weapon w = new Weapon(0, 10, 0, 30, 6, 5, 240);
		result.installWeapon(w);
		result.setThrust(Starship.THRUST_DEFAULT*3);
		result.setDecel(Starship.DECEL_DEFAULT * 0.8);
		result.setBody(new Body() {
			public void updateShapes() {
				Point2D.Double pos = result.getPos();
				double r = result.getPosR();
				setShapes(
						createRectangle(SpaceHelper.polarOffset(pos, r+105, 10), r, 7, 7),
						createRectangle(SpaceHelper.polarOffset(pos, r-105, 10), r, 7, 7),
						createRectangle(pos, r, 10, 2),
						createTriangle(SpaceHelper.polarOffset(pos, r, 5), r, 5)
						);
			}
			public void draw(Graphics g) {
				g.setColor(Color.BLUE);
				drawDefault(g);
			}
		});
		return result;
	}
	public static final Starship_NPC createBasicEnemy2() {
		Starship_NPC result = new Starship_NPC();
		Weapon w = new Weapon(0, 10, 0, 20, 30, 2, 180);
		result.installWeapon(w);
		result.setRotation_accel(1.2);
		result.setRotation_decel(Starship.ROTATION_DECEL_DEFAULT*1.5);
		result.setBody(new Body() {
			public void updateShapes() {
				Point2D.Double pos = result.getPos();
				double r = result.getPosR();
				setShapes(
						createRectangle(pos, r, 5, 10),
						createTriangle(SpaceHelper.polarOffset(pos, r, 10), r, 10)
						);
			}
			public void draw(Graphics g) {
				g.setColor(Color.RED);
				drawDefault(g);
			}
		});
		return result;
	}
	/*
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
	*/
}
