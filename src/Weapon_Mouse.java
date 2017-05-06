
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Weapon_Mouse extends Weapon{
	public Weapon_Mouse(double angle, double radius, double fire_angle, int cooldown, int speed, int damage, int lifetime, Color color)
	{
		super(angle, radius, fire_angle, cooldown, speed, damage, lifetime, color);
	}
	public final double getFireAngle()
	{
		Point aimPos = MouseInfo.getPointerInfo().getLocation();
		//return SpaceObject.calcFireAngle(new Point2D.Double(aimPos.getX() - owner.getPosX(), (GameWindow.HEIGHT - aimPos.getY() + 50) - getPosY()), new Point2D.Double(-owner.getVelX(), -owner.getVelY()), getProjectileSpeed());
		return SpaceObject.calcFireAngle(new Point2D.Double(aimPos.getX() - GameWindow.WIDTH/2, (GameWindow.HEIGHT - aimPos.getY() + 50) - GameWindow.HEIGHT/2), new Point2D.Double(-owner.getVelX(), -owner.getVelY()), getProjectileSpeed());
	}
}
