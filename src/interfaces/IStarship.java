package interfaces;
import java.awt.geom.Point2D;

import behavior.Behavior_Starship;

public interface IStarship {

	/*
	final int HEAD_SIZE = 10; //20
	final int BODY_SIZE = 15; //30
	 */
	public static final double THRUST_DEFAULT = 0.5; //1
	public static final double MAX_SPEED_DEFAULT = 8;
	public static final double DECEL_DEFAULT = .2;
	//public static final double ROTATION_MAX_DEFAULT = 15; //Original
	public static final double ROTATION_MAX_DEFAULT = 8;
	//public static final double ROTATION_DECEL_DEFAULT = .4;
	public static final double ROTATION_DECEL_DEFAULT = .6;
	//public static final double ROTATION_ACCEL_DEFAULT = .6; //Original
	public static final double ROTATION_ACCEL_DEFAULT = .15;
	

	public abstract void turnCCW();

	public abstract void turnCW();

	public abstract void strafeLeft();

	public abstract void strafeRight();

	public abstract void brake();

	public abstract Point2D.Double getFuturePosWithDeceleration();

	public abstract double getFutureAngleWithDeceleration();

	public abstract double calcFutureFacingDifference(double angle_target);

	public abstract double calcFacingDifference(double angle_target);

	public abstract void turnDirection(Behavior_Starship.RotatingState direction);

	public abstract void setManeuverStats(double thrust, double max_speed,
			double decel);

	public abstract void setRotationStats(double rotation_max,
			double rotation_accel, double rotation_decel);

	public abstract double getThrust();

	public abstract void setThrust(double thrust);

	public abstract double getMax_speed();

	public abstract void setMax_speed(double max_speed);

	public abstract double getDecel();

	public abstract double getRotation_max();

	public abstract void setRotation_max(double rotation_max);

	public abstract double getRotation_accel();

	public abstract void setRotation_accel(double rotation_accel);

	public abstract double getRotation_decel();

	public abstract void setRotation_decel(double rotation_decel);

}