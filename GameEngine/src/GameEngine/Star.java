package GameEngine;
import VMQ.Vec3;

public class Star extends BackgroundObjects{

	private float speed;
	private Vec3 direction;
	private float distanceTravelled;
	
	public Star(Vec3 position,Vec3 direction,float speed,Vec3 scale) {
		super(position);
		setScale(scale);
		this.speed = speed;
		this.direction = direction.normalize();
		this.distanceTravelled = 0;
	}
	
	public void tick(float timeSinceLastTick) {
		float distance = timeSinceLastTick*speed;
		distanceTravelled = distanceTravelled + distance;
		setPosition(getPosition().add(direction.multiply(distance)));
	}
	
	public float getDistanceTravelled() {
		return distanceTravelled;
	}
	
	public String getType() {
		return "Star";
	}
	
	public DISPLAYTYPE getDisplayType() {
		return DISPLAYTYPE.TRANSPARENT_ONE;
	}
}
