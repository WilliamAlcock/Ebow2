package GameEngine;
import java.util.LinkedList;

import VMQ.Vec3;

public class Engine implements ParticleGenerator{

	private int numberOfParticles;
	private Vec3 offset;
	private InPlayObj ship;
	private Vec3 gravity = new Vec3();
	private int minPower,maxPower,power;
	private MySwitch killed;
	
	public Engine(InPlayObj ship,Vec3 offset,Vec3 gravity,int numberOfParticles,int minPower,int maxPower) {
		this.numberOfParticles = numberOfParticles;
		this.offset = offset;
		this.ship = ship;
		this.minPower = minPower;
		this.maxPower = maxPower;
		this.power = minPower;
		this.killed = new MySwitch(false);
		setGravity(gravity);
	}
	
	public void kill() {
		killed.set(true);
	}
	
	public void increasePower(int increase) {
		if (power<maxPower) {
			power += increase;
		}
	}
	
	public void decreasePower(int decrease) {
		if (power>minPower) {
			power -= decrease;
		}
	}
	
	public void setGravity(Vec3 newGravity){
		newGravity = newGravity.normalize().multiply(power);
		gravity.setX(newGravity.getX());
		gravity.setY(newGravity.getY());
		gravity.setZ(newGravity.getZ());
	}

	@Override
	public void generate(LinkedList<Particle> particles) {
		for (int i=0;i<numberOfParticles;i++) {
			particles.add(new JetFire(ship,offset.copy(),gravity,1f,killed));
		}
	}
}
