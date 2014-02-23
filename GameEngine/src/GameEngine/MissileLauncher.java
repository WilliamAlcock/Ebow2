package GameEngine;
import VMQ.Vec3;

public class MissileLauncher extends Weapon implements hasComponents{

	private MissileLauncherBarrel missileLauncherBarrel;
	
	public MissileLauncher(Level level) {
		super(new Vec3(), 0, 100, new Vec3(-4.5f,-0.4f,0),20, 1);
		this.setFollowRotation(true);
		this.missileLauncherBarrel = new MissileLauncherBarrel(level); 
	}

	@Override
	public String getType() {
		return "MissileLauncher";
	}

	@Override
	// Might be possible to remove this - not sure till i write the code to destroy the weapons.
	public InPlayObj[] getComponentObjects() {
		return new InPlayObj[] {missileLauncherBarrel};
	}
}
