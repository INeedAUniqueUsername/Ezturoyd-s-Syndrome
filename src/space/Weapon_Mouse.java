package space;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.Point2D;

import game.GamePanel;
import helpers.SpaceHelper;

public class Weapon_Mouse extends Weapon {
	public Weapon_Mouse(double angle, double radius, double fire_angle, int cooldown, int speed, int damage,
			int lifetime) {
		super(angle, radius, fire_angle, cooldown, speed, damage, lifetime);
	}

	public void update() {
		updateBody();
		updateCooldown();
		double angle = getPosAngle() + owner.getPosR();
		setPos(owner.polarOffset(angle, getPosRadius()));
		double fireAngle;
		switch (GamePanel.getCameraMode()) {
		case FOLLOW_PLAYER:
			
			fireAngle = SpaceHelper.arctanDegrees(SpaceHelper.getMousePosRelativeToCenter());
			break;
		case FIXED:
		default:
			
			fireAngle = SpaceHelper.arctanDegrees(SpaceHelper.getMousePosRelativeToObject(owner));
			break;
		}
		setFireAngle(fireAngle);
	}
	
}
