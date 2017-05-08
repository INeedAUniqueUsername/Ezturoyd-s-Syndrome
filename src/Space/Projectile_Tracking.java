package Space;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import Interfaces.NewtonianMotion;

public class Projectile_Tracking extends Projectile {
	SpaceObject target;
	public Projectile_Tracking(double posX, double posY, double posR, int damage, int life) {
		super(posX, posY, posR, damage, life);
		// TODO Auto-generated constructor stub
	}
	public void setTarget(SpaceObject so) {
		target = so;
	}
	public NewtonianMotion getTarget() {
		return target;
	}
	public void update() {
		super.update();
		updateTracking();
	}
	public void updateTracking() {
		/*
		if(target == null) {
			return;
		} else if(!target.getActive()) {
			target = null;
			return;
		}
		*/
		ArrayList<Starship> universe = GamePanel.getWorld().getStarships();
		if(target == null && universe.size() > 0) {
			target = GamePanel.getWorld().getStarships().get((int)(Math.random()*universe.size()));
		}
		int turnRate = 3;
		Point2D.Double pos = getPos();
		Point2D.Double pos_target = Behavior.getNearestTargetClone(this, target);
		double velAngle = getVelAngle();
		double turnLeftDistance = Helper.getDistanceBetweenPos(Helper.polarOffset(pos, velAngle-90, 1), pos_target);
		double turnRightDistance = Helper.getDistanceBetweenPos(Helper.polarOffset(pos, velAngle+90, 1), pos_target);
		if(turnLeftDistance < turnRightDistance) {
			pos_r -= turnRate;
		} else if(turnRightDistance < turnLeftDistance) {
			pos_r += turnRate;
		}
		setVelPolar(pos_r, getVelSpeed());
	}
}
