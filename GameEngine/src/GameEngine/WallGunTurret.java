package GameEngine;
import VMQ.Vec3;

public class WallGunTurret extends Enemy implements hasComponents{
	
	private GunBarrel wallGunBarrel;
	
	public WallGunTurret (Vec3 position, float trackSpeed, InPlayObj toTrack) {
		super(position, 0.0f,trackSpeed, 5,1); 	//health 5 damage 1
		this.wallGunBarrel = new GunBarrel(position,100.0f,this,new Vec3(0,0,3.5f),1);
		this.addMovement(new MovementTrack(toTrack));
	}

	@Override
	public void tick(float timeSinceLastTick) {
		super.tick(timeSinceLastTick);
		wallGunBarrel.track();
	}

	@Override
	public String getType() {
		return "WallGunTurret";
	}
	
	@Override
	public InPlayObj[] getComponentObjects() {
		return new InPlayObj[] {wallGunBarrel};
	}
	
	@Override
	public void decHealth(int decAmount) {
		super.decHealth(decAmount);
		wallGunBarrel.decHealth(decAmount);
	}
	
	public boolean fires() {
		return false;
	}
}
