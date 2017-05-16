package behavior.controllers;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import behavior.Behavior_Starship;
import behavior.orders.Order_Hold;
import space.Starship_NPC;

public class BehaviorController_Default extends Behavior_Starship {

	ArrayList<Behavior_Starship> orders;
	public BehaviorController_Default(Starship_NPC o) {
		super(o);
		initialize();
	}
	public void initialize() {
		orders = new ArrayList<Behavior_Starship>();
	}
	public final Behavior_Starship getOrder(int b) {
		return orders.get(b);
	}
	public final void setOrder(int i, Behavior_Starship b) {
		orders.set(i, b);
	}
	public final void addOrder(Behavior_Starship b) {
		orders.add(b);
	}
	public final void removeOrder(Behavior_Starship b) {
		orders.remove(b);
	}
	public final void clearOrders() {
		orders = new ArrayList<Behavior_Starship>();
	}
	public final ArrayList<Behavior_Starship> getOrders() {
		return orders;
	}
	public void update() {
		//System.out.println("Updating Controller");
		
		if(orders.size() > 0) {
			updateCurrentOrder();
		} else {
			onOrdersCompleted();
		}
	}
	public void updateCurrentOrder() {
		Behavior_Starship behavior_current = orders.get(0);
		
		behavior_current.update();
		copyActions(behavior_current);
		if(!behavior_current.getActive()) {
			orders.remove(behavior_current);
		}
	}
	public void onOrdersCompleted() {
		orders.add(new Order_Hold(getOwner()));
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
