import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Weapon_Mouse extends Weapon{
	public Weapon_Mouse(double angle, double radius, double fire_angle, int cooldown, int speed, int damage, int lifetime, Color color)
	{
		super(angle, radius, fire_angle, cooldown, speed, damage, lifetime, color);
	}
	
	public double getFireAngle()
	{
		return SpaceObject.calcFireAngle(new Point2D.Double(aim_x - owner.getPosX(), aim_y - getPosY()), new Point2D.Double(-owner.getVelX(), -owner.getVelY()), getProjectileSpeed());
	}
}
