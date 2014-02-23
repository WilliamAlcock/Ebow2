package GameEngine;
import VMQ.Vec3;

public class MovementTrack extends Movement{

	private InPlayObj toTrack;
	
	public MovementTrack(InPlayObj toTrack) {
		super(0.0f);
		this.toTrack = toTrack;
	}
	
	public boolean move(InPlayObj toMove, float timeSinceLastTick) {
		Vec3 zVector = toMove.getRotation().GetZVector();
		System.out.println("ZVECTOR "+zVector);
		Vec3 vectorToTrack = toTrack.getPosition().sub(toMove.getPosition());
		System.out.println("VECTORTOTRACK "+zVector);
		float angleToMove = zVector.getAngleBetween(vectorToTrack);
		System.out.println("ANGLETOMOVE "+zVector);
		Vec3 crossP = zVector.crossProduct(vectorToTrack).normalize();
		System.out.println("CROSSP "+zVector);
		toMove.setCurDist(angleToMove);
		toMove.rotateTimeBased(timeSinceLastTick, crossP);		
		return false;
	}
}

	
