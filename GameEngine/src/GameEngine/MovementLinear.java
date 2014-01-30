package GameEngine;
import VMQ.Vec3;

public class MovementLinear extends Movement{
	private Vec3 vector;
	
	public MovementLinear(Vec3 vector,float distance) {
		super(distance);
		this.vector = vector.normalize();
	}
	
	public boolean move(InPlayObj toMove,float timeSinceLastTick) {
		if (toMove.getCurDist()>=getEndDistance()) {
			return true;
		} else {
			toMove.moveTimeBased(timeSinceLastTick, vector);
			return false;
		}			
	}
}
