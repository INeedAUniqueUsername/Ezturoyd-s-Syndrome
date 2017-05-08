
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Weapon_Mouse extends Weapon{
	public Weapon_Mouse(double angle, double radius, double fire_angle, int cooldown, int speed, int damage, int lifetime)
	{
		super(angle, radius, fire_angle, cooldown, speed, damage, lifetime);
	}
	public void update() {
		updateBody();
		updateCooldown();
		double angle = getPosAngle() + owner.getPosR();
		setPos(owner.polarOffset(angle, getPosRadius()));
		Point aimPos = MouseInfo.getPointerInfo().getLocation();
		/*
		Point2D.Double relativePos = new Point2D.Double(
				(aimPos.getX() - GameWindow.WIDTH/2 + owner.getPosX())%GameWindow.WIDTH,
				((GameWindow.HEIGHT - aimPos.getY()) + 30 - GameWindow.HEIGHT/2 + owner.getPosY())%GameWindow.HEIGHT
				);
		SpaceObject nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		for(Starship s : GamePanel.getWorld().getStarships()) {
			double distance = relativePos.distance(s.getPos());
			if(s.targetIsEnemy((Starship) owner) && distance < nearestDistance) {
				nearest = s;
				nearestDistance = distance;
			}
		}
		
		double fireAngle;
		if(nearestDistance < 100) {
			fireAngle = SpaceObject.calcFireAngle(new Point2D.Double(nearest.getPosX() - GameWindow.WIDTH/2, nearest.getPosY() - GameWindow.HEIGHT/2), new Point2D.Double(nearest.getVelX() - owner.getVelX(), nearest.getVelX() - owner.getVelY()), getProjectileSpeed());
		} else {
			fireAngle = SpaceObject.calcFireAngle(new Point2D.Double(aimPos.getX() - GameWindow.WIDTH/2, (GameWindow.HEIGHT - aimPos.getY()) + 30 - GameWindow.HEIGHT/2), new Point2D.Double(-owner.getVelX(), -owner.getVelY()), getProjectileSpeed());
		}
		*/
		double fireAngle;
		switch(GamePanel.camera) {
		case FOLLOW_PLAYER:
			fireAngle = Helper.calcFireAngle(new Point2D.Double(aimPos.getX() - GameWindow.WIDTH/2, (GameWindow.HEIGHT - aimPos.getY()) + 30 - GameWindow.HEIGHT/2), new Point2D.Double(-owner.getVelX(), -owner.getVelY()), getProjectileSpeed());
			break;
		case FIXED:
		default:
			fireAngle = Helper.calcFireAngle(new Point2D.Double(aimPos.getX() - owner.getPosX(), (GameWindow.HEIGHT - aimPos.getY() + 50) - getPosY()), new Point2D.Double(-owner.getVelX(), -owner.getVelY()), getProjectileSpeed());
			break;
		}
		setFireAngle(fireAngle);
	}
	/*
	public final double getFireAngle()
	{
		Point aimPos = MouseInfo.getPointerInfo().getLocation();
		//return SpaceObject.calcFireAngle(new Point2D.Double(aimPos.getX() - owner.getPosX(), (GameWindow.HEIGHT - aimPos.getY() + 50) - getPosY()), new Point2D.Double(-owner.getVelX(), -owner.getVelY()), getProjectileSpeed());
		return SpaceObject.calcFireAngle(new Point2D.Double(aimPos.getX() - GameWindow.WIDTH/2, (GameWindow.HEIGHT - aimPos.getY()) + 30 - GameWindow.HEIGHT/2), new Point2D.Double(-owner.getVelX(), -owner.getVelY()), getProjectileSpeed());
	}
	*/
}
