import Space.Projectile;

public class WeaponType implements IWeaponType {
	private int fire_cooldown_time = 10;
	private int projectile_speed = 10;
	private int projectile_damage = 5;
	private int projectile_lifetime = 60;
	
	@Override
	public Projectile getProjectile() {
		// TODO Auto-generated method stub
		Projectile result = new Projectile();
		result.setDamage(projectile_damage);
		result.setLifetime(projectile_lifetime);
		return result;
	}
	public int getProjectileSpeed() {
		return projectile_speed;
	}
}
