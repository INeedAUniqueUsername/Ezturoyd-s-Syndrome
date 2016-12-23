import java.awt.Point;

public class Weapon_Mouse extends Weapon{

	double target_x;
	double target_y;
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
