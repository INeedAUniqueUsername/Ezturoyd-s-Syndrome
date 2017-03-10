import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BehaviorController_Default extends Behavior {

	public BehaviorController_Default(Starship_NPC o) {
		super(o);
	}
	public void initialize() {
		owner.addOrder(new Order_GoToPos(owner, new Point2D.Double(500, 450)));
	}
	public void update() {
		System.out.println("Updating Controller");
		
		if(owner.getOrders().size() > 0) {
			updateCurrentOrder();
		} else {
			onOrdersCompleted();
		}
	}
	public void updateCurrentOrder() {
		Behavior behavior_current = owner.getOrder(0);
		
		behavior_current.update();
		copyActions(behavior_current);
		if(!behavior_current.getActive()) {
			owner.removeOrder(behavior_current);
		}
	}
	public void onOrdersCompleted() {
		owner.addOrder(new Order_Hold(owner));
		/*
		ArrayList<Starship> ships = GamePanel.world.getStarships();
		SpaceObject target = null;
		double distance = Double.MAX_VALUE;
		for(SpaceObject s : ships) {
			double d = owner.getDistanceBetween(s);
			if(!s.equals(owner) && d < distance) {
				target = s;
				distance = d;
			}
		}
		owner.addOrder(new Order_Attack(owner, target));
		*/
	}
	public final double getMaxAngleDifference()
	{
		return 1;
	}
	public final double getMinSeparationFromAttackers()
	{
		return 300;
	}
	public final double getMaxSeparationFromTarget()
	{
		//return 300;
		return 700;
	}
	public final double getMinSeparationFromTarget()
	{
		//return 200;
		return 400;
	}
	public final double getMinSeparationFromOthers()
	{
		return 100;
	}
}
