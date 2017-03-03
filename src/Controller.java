import java.util.ArrayList;

public class Controller extends Behavior {

	public Controller(Starship_NPC o) {
		super(o);
		// TODO Auto-generated constructor stub
	}
	public void update() {
		System.out.println("Updating Brain");
		ArrayList<Space_Object> objectsTooClose = new ArrayList<Space_Object>();
		for(Space_Object o : GamePanel.world.getStarships())
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
			for(Space_Object o : objectsTooClose)
			{
				angle_destination += owner.getAngleFrom(o);
			}
			angle_destination /= objectsTooCloseCount;
			owner.turnDirection(owner.calcTurnDirection(angle_destination));
			setThrusting(ACT_THRUST);
			System.out.println("Destination Angle: " + angle_destination);
		} else if(owner.getBehaviors().size() > 0) {
			Behavior behavior_current = owner.getBehavior(0);
			
			behavior_current.update();
			setActions(behavior_current.getActions());
			if(!behavior_current.getActive()) {
				owner.removeBehavior(behavior_current);
			}
		} else {
			ArrayList<Starship> ships = GamePanel.world.getStarships();
			Space_Object target = ships.get((int) (ships.size()*Math.random()));
			owner.addBehavior(new Behavior_Attack(owner, target));
		}
	}
}
