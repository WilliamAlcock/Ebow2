package GameEngine;

public class MovementWait extends Movement{

	public MovementWait(float timeToWait) {
		super(timeToWait);
	}
	
	public boolean move(InPlayObj toMove, float timeSinceLastTick) {
		if (toMove.getCurDist()>=getEndDistance()) {
			return true;
		} else {
			toMove.setCurDist(toMove.getCurDist()+timeSinceLastTick);
			return false;
		}	
	}
}
