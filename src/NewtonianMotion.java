import java.awt.geom.Point2D;

public interface NewtonianMotion {

	public abstract void setPosRectangular(double x, double y);

	public abstract void setPosRectangular(Point2D.Double pos);

	public abstract void setPosR(double posR);

	public abstract void setPos(double x, double y, double r);

	public abstract void setVelRectangular(double x, double y);

	public abstract void setVelRectangular(Point2D.Double vel);

	public abstract void setVel(double x, double y, double r);

	public abstract void setVelPolar(double angle, double speed);

	public abstract void incPosRectangular(double x, double y);

	public abstract void incPosPolar(double angle, double distance);

	public abstract void incVelRectangular(double x, double y);

	public abstract void incVelPolar(double angle, double speed);

	public abstract void accelerate(double angle, double speed);

	public abstract void accelerateEnergy(double angle, double kineticEnergy);

	public abstract void decelerate(double speed);

	public abstract Point2D.Double getPos();

	public abstract double getPosX();

	public abstract double getPosY();

	public abstract double getPosR();

	public abstract double getVelAngle();

	public abstract double getVelAtAngle(double angle);

	public abstract Point2D.Double getVel();

	public abstract double getVelX();

	public abstract double getVelY();

	public abstract double getVelR();

	public abstract double getVelSpeed();

	public abstract double getMass();

	public abstract double getKineticEnergy();

	public abstract double getKineticEnergyAngled(double angle);

}