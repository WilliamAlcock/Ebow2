package GameEngine;

public class MovementNone extends Movement{

	public MovementNone() {
		super(0.0f);
	}
	
	public boolean move(InPlayObj toMove, float timeSinceLastTick) {
		return false;
	}
}
