package GameEngine;

import VMQ.Vec3;

public class Alien1 extends Enemy {
	
	public Alien1(Vec3 position, float speed,float rotSpeed) {
		super(position, speed, rotSpeed, 5,1); 	//health 5 damage 1
	}

	@Override
	public void tick(float timeSinceLastTick) {
		super.tick(timeSinceLastTick);
		super.rotateTimeBased(timeSinceLastTick,new Vec3(0,0,1));
	}

	@Override
	public String getType() {
		return "Alien1";
	}
}

