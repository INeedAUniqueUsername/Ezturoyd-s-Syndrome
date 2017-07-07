package factories;

import java.awt.geom.Point2D;

import body.Body_Projectile;
import game.GamePanel;
import helpers.SpaceHelper;
import space.Projectile;

public class ExplosionFactory {
	public static final void createExplosion(Point2D pos) {
		int count = 36;
		double interval = 360 / count;
		for(int i = 0; i < count; i++) {
			Projectile result = new Projectile();
			result.setSize(20);
			result.setPosRectangular(pos);
			double angle = i * interval + SpaceHelper.random(interval * 2) - interval;
			double speed = (SpaceHelper.random(30) + 20);
			result.setVelPolar(angle, speed);
			result.setPosR(angle);
			result.setVelR((SpaceHelper.random(30) - 15) * 5);
			result.setLifetime((int) (SpaceHelper.random(120) + 60));
			result.setActive(true);
			result.setBody(new Body_Projectile(result) {
				int maxLife = result.getLifetime();
				public void updateShapes() {
					setShapes(
							createTriangle(result.getPos(), result.getPosR(), 10 * result.getLifetime() / maxLife)
							);
				}
			});
			GamePanel.getWorld().createSpaceObject(result);
		}
	}
}
