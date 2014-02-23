package GameEngine;
import VMQ.Quaternion;
import VMQ.Vec3;

public class MovementMirror extends Movement {

	private InPlayObj toMirror;
	private Vec3 movementVector;
	
	private Vec3 facing;
	private Vec3 up;
	private Quaternion rotation;
	
	public MovementMirror(InPlayObj toMirror,Vec3 facing,Vec3 up) {
		super(0.0f);
		this.toMirror = toMirror;
		
		this.facing = facing.normalize();	
		this.up = up.normalize();
		this.movementVector = facing.crossProduct(up);
		
		float newFacingAngle = new Vec3(0,0,-1).getAngleBetween(this.facing);	
		
		rotation = Quaternion.Identity.multiply(new Quaternion(newFacingAngle,facing.crossProduct(new Vec3(0,0,-1)))).normalize();
	}		

	public boolean move(InPlayObj toMove, float timeSinceLastTick) {		
		Vec3 between = toMirror.getPosition().sub(toMove.getPosition());
		
		float direction = movementVector.dotProduct(between);
		
		if (direction<-3) {
			toMove.bankRight();
			toMove.moveTimeBased(timeSinceLastTick, movementVector.multiply(-1));
		} else if (direction>3) {
			toMove.bankLeft();
			toMove.moveTimeBased(timeSinceLastTick, movementVector);
		} else {
			toMove.levelOff();
		}
		
		toMove.setRotation(rotation);
		return false;
	}
	
}
