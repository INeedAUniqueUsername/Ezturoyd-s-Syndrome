import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

public class Spaceship_Enemy extends Spaceship {
	
	Space_Object target;
	
	public void Spaceship_Enemy(){
		
	}
	
	public void update()
	{
		
	}
	
	public void faceTarget()
	{
		double angle_to_target = getAngleTowards(target);
		double angleDiffCCW = modRangeDegrees(angle_to_target - pos_r);
		double angleDiffCW = modRangeDegrees(pos_r - angle_to_target);
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
