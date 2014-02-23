package GameEngine;
import VMQ.Vec3;

public class PowerUpHealth extends PowerUp{

	private int strength;
	
	public PowerUpHealth(Vec3 position, float speed,int strength) {
		super(position, speed);
		this.strength = strength;
	}
	
	public void handleCollision(InPlayObj objCollidingWith) {
		if (objCollidingWith.getCategory().equals("PlayerPart")) {
			objCollidingWith.incHealth(strength);
		} else {
			objCollidingWith.decHealth(1);
		}
		super.handleCollision(objCollidingWith);
	}
}
