package space;

import java.awt.Color;

public class Weapon_Key extends Weapon {
	/*
	 * public Weapon_Key(double angle, double radius, double fire_angle, int
	 * cooldown, int speed, int damage, int lifetime, Color color) { posAngle =
	 * angle; posRadius = radius; this.fire_angle = fire_angle;
	 * fire_cooldown_max = cooldown; fire_cooldown_time = fire_cooldown_max;
	 * projectile_speed = speed; projectile_damage = damage; projectile_lifetime
	 * = lifetime; this.color = color; }
	 */
	public Weapon_Key(double posAngle, double posRadius, double fire_angle, int cooldown, int speed, int damage,
			int lifetime) {
		super(posAngle, posRadius, fire_angle, cooldown, speed, damage, lifetime);
	}
}
