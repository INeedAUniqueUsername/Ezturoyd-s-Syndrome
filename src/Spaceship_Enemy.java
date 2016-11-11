import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

public class Spaceship_Enemy extends Spaceship {
	
	Space_Object target;
	
	public void update()
	{
		updateSpaceship();
		if(exists(target))
		{
			faceTarget();
		}
		thrust();
	}
	public void setTarget(Space_Object target_new)
	{
		target = target_new;
	}
	public double getVelTowards(Space_Object object)
	{
		double angle_towards_object = getAngleTowards(object);
		return object.getVelAngled(angle_towards_object) - getVelAngled(angle_towards_object);
	}
	public void faceTarget()
	{
		double angle_to_target = getAngleTowards(target);
		double r_decel_time = Math.abs(vel_r/ROTATION_DECEL);
		double angle_to_target_future = angle_to_target + target.getVelR()() * r_decel_time;
		//Let's relearn AP Physics I!
		double pos_r_future = pos_r
				+ vel_r * r_decel_time
				//Make sure that the deceleration value has the opposite sign of the rotation velocity
				+ (1/2) * ((vel_r < 0) ? ROTATION_DECEL : -ROTATION_DECEL) * Math.pow(r_decel_time, 2);
		double angleDiffCCW = modRangeDegrees(angle_to_target - pos_r_future);
		double angleDiffCW = modRangeDegrees(pos_r_future - angle_to_target);
		double angleDiff = (angleDiffCW < angleDiffCCW) ? angleDiffCW : angleDiffCCW;
		if(angleDiff > getMaxAngleDifference())
		{
			if(angleDiffCCW < angleDiffCW)
			{
				turnLeft();
			}
			else if(angleDiffCW > angleDiffCCW)
			{
				turnRight();
			}
		}
	}
	public double getMaxAngleDifference()
	{
		return 90;
	}
}
