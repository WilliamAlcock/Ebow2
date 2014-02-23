package GameEngine;
import java.util.List;

import VMQ.Vec3;

public class MissileLauncherBarrel extends Weapon implements Fires {

	private BulletGenerator bulletGenerator;
	private float cockingDistance = -2;
	private Level level;
	
	public MissileLauncherBarrel(Level level) {
		super(new Vec3(), 0, 100, new Vec3(-4.5f,-0.4f,0), 20, 1);
		this.level = level;
		this.setFollowRotation(true);
		LaserRound laser = new LaserRound(new Vec3(),60);
		bulletGenerator = new BulletGenerator(laser);
	}
	
	@Override
	public String getType() {
		return "MissileLauncherBarrel";
	}

	@Override
	public void fireWeapon(float timeSinceLastTick,List<InPlayObj> fireList) {
		if (bulletGenerator.readyToFire()) {
			InPlayObj followObj = level.getClosestObject(this);
			if (followObj!=null) {
				bulletGenerator.setPosition(getPosition().add(new Vec3(0,0,-3)));
				bulletGenerator.clearMovement();
				bulletGenerator.addMovement(new MovementFollow(followObj,0,0));
				fireList.add(bulletGenerator.generateBullet());
			}
		} 
	}
	
	@Override
	public void tick(float timeSinceLastTick) {
		bulletGenerator.tick(timeSinceLastTick);
		getOffset().setZ((-cockingDistance/100)*bulletGenerator.getCockingPosition());
		super.tick(timeSinceLastTick);
	}
	
	@Override
	public void setRateOfFire(float rateOfFire) {
		bulletGenerator.setRateOfFire(rateOfFire);
	}
}
