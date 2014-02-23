package GameEngine;
import VMQ.Vec3;

public class WallGun extends Enemy implements hasComponents{

	private WallGunTurret wallGunTurret;
	
	public WallGun(Vec3 position,InPlayObj toTrack, float trackSpeed,boolean facingLeft) {
		super(position, 0.0f, 0.0f, 5,1); 	//health 5 damage 1
		this.wallGunTurret = new WallGunTurret(position,trackSpeed,toTrack);
		this.addMovement(new MovementNone());
		if (facingLeft) {
			this.rotate(180, new Vec3(0,1,0));
			wallGunTurret.rotate(90, new Vec3(0,1,0));
		} else {
			wallGunTurret.rotate(90, new Vec3(0,-1,0));
		}
	}

	public String getType() {
		return "WallGunBase";
	}
	
	public InPlayObj[] getComponentObjects() {
		return new InPlayObj[] {wallGunTurret};
	}
	
	public void decHealth(int decAmount) {
		super.decHealth(decAmount);
		wallGunTurret.decHealth(decAmount);
	}
	
	public boolean fires() {
		return false;
	}
}
