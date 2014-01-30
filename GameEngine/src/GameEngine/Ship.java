package GameEngine;
import VMQ.Vec3;
import java.util.List;

public class Ship extends PlayerPart implements Fires{
	
	private boolean fires = true;
	private BulletGenerator bulletGenerator;
	
	public Ship(Vec3 position,float speed,Player player) {
		super(position,speed,0.0f,100,1);			// 500 health damage 1
//		setScale(new Vec3(2.0f/3.0f,2.0f/3.0f,2.0f/3.0f));
		bulletGenerator = new BulletGenerator(new LaserRectangle(new Vec3(),100));
		setPlayer(player);
	}
	
	@Override
	public void tick(float timeSinceLastTick) {
		bulletGenerator.tick(timeSinceLastTick);
	}
	
	@Override
	public void setRateOfFire(float rateOfFire) {
		bulletGenerator.setRateOfFire(rateOfFire);
	}
	
	public void forward(float timeSinceLastTick) {
		getPosition().setZ(getPosition().getZ()+timeSinceLastTick*getSpeed());
	}
	
	public void reverse(float timeSinceLastTick) {
		getPosition().setZ(getPosition().getZ()+timeSinceLastTick*-getSpeed());
	}
	
	public void bankLeft(float timeSinceLastTick) {
		getPosition().setX(getPosition().getX()+timeSinceLastTick*getSpeed());
		super.bankLeft();
	}
	
	public void bankRight(float timeSinceLastTick) {
		getPosition().setX(getPosition().getX()+timeSinceLastTick*-getSpeed());
		super.bankRight();
	}
	
	@Override
	public Explosion getExplosion(Vec3 dimensions) {
		return new Explosion(getPosition(),15,dimensions,true,true,true,true,true,true);
	}
	
	@Override
	public void decHealth(int decAmount) {
		super.decHealth(decAmount);
		this.setChanged();
		this.notifyObservers(this);
	}
	
	public void setFires(boolean fires) {
		this.fires = fires;
	}
	
	@Override
	public String getType() {
		return "Ship";
	}

	public Ship getShip() {
		return this;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	@Override
	public void fireWeapon(float timeSinceLastTick,List<InPlayObj> fireList) {
		if (fires) {
			if (bulletGenerator.readyToFire()) {
				bulletGenerator.setPosition(new Vec3(getPosition().getX(),getPosition().getY(),getPosition().getZ()+5.0f));
				bulletGenerator.addMovement(new MovementLinear(getRotation().GetZVector(),60.0f));
				fireList.add(bulletGenerator.generateBullet());
			}
		}
	}
}