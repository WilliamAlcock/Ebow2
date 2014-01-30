package GameEngine;
import java.util.Iterator;
import java.util.LinkedList;

public class ParticleEngine {

	private LinkedList<Particle> particles = new LinkedList<Particle>();
	
	public ParticleEngine() {}
	
	public void add(ParticleGenerator particleGenerator) {
		particleGenerator.generate(particles);
	}
	
	public LinkedList<Particle> getParticles() {
		return particles;
	}
	
	public void tick(float timeSinceLastTick) {
		Iterator<Particle> it = particles.iterator();
		while (it.hasNext()) {
			Particle curPart = it.next();
			curPart.move(timeSinceLastTick);
			if (curPart.getLife()<=0) {
				it.remove();
			}
		}
	}		
}
