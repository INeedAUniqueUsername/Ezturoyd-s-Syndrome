import java.util.ArrayList;

public class BehaviorController extends Behavior {

	public BehaviorController(Starship_NPC o) {
		super(o);
	}
	public void update() {
		System.out.println("Updating Controller");
		ArrayList<SpaceObject> objectsTooClose = new ArrayList<SpaceObject>();
		for(SpaceObject o : GamePanel.world.getStarships())
		{
			if(!o.equals(owner))
			{
				if(owner.getDistanceBetween(o) < owner.getMinSeparationFromOthers())
				{
					System.out.println(o.getName() + " is " + owner.getDistanceBetween(o) + " away (too close).");
					objectsTooClose.add(o);
				}
			} else {
				System.out.println("It's you.");
			}
		}
		
		int objectsTooCloseCount = objectsTooClose.size();
		if(objectsTooCloseCount > 0)
		{
			double angle_destination = 0;
			for(SpaceObject o : objectsTooClose)
			{
				angle_destination += owner.getAngleFrom(o);
			}
			angle_destination /= objectsTooCloseCount;
			owner.turnDirection(owner.calcTurnDirection(angle_destination));
			setThrusting(ThrustingState.THRUST);
			System.out.println("Destination Angle: " + angle_destination);
		} else if(owner.getBehaviors().size() > 0) {
			Behavior behavior_current = owner.getBehavior(0);
			
			behavior_current.update();
			copyActions(behavior_current);
			if(!behavior_current.getActive()) {
				owner.removeBehavior(behavior_current);
			}
		} else {
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
			owner.addBehavior(new Behavior_Attack(owner, target));
		}
	}
}
