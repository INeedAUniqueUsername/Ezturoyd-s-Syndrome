import java.awt.Color;
import java.awt.Point;

public class Weapon_Mouse extends Weapon{

	double target_x;
	double target_y;
	
	public Weapon_Mouse(double angle, double radius, double fire_angle, int cooldown, int speed, int damage, int lifetime, Color color)
	{
		super(angle, radius, fire_angle, cooldown, speed, damage, lifetime, color);
	}
	
	public double getFireAngle()
	{
		if(exists(target_x))
		{
			return owner.getAngleTowardsPos(target_x, target_y);
		}
		else
		{
			return fire_angle;
		}
	}
	public void setTargetPos(double x, double y)
	{
		target_x = x;
		target_y = y;
	}
}
