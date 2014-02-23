package GameEngine;
import VMQ.Vec3;

public class JetFire extends Particle{

	private InPlayObj ship;
	private Vec3 initialOffset;
	private float initialSpeed;
	private MySwitch killed;
	
	private Vec3 offset;
	
	public JetFire(InPlayObj ship,Vec3 offset,Vec3 gravity,float speed,MySwitch killed) {
		super(ship.getPosition().add(offset));
		this.killed = killed; 
		this.initialOffset = offset;
		this.ship = ship;
		this.initialSpeed = speed;
		// Set Gravity 
		setGravity(gravity);
		// Set slowDown
		setSlowDown(5.0f);
		// Set growthRate
		setGrowthRate(-1f);
		// Set the color change
		setColorChange(new Vec3(0.0f,-1.5f,-0.5f));
		setup();
	}
	
	private void setup() {
		this.offset = initialOffset;
		
		// Random fade speed
		setFade((float)((100*Math.random())+35)/15f+0.003f);
		// Random speed between -2.5 and 2.5 
		getDirection().setX((float) ((initialSpeed * Math.random()) - (0.5*initialSpeed)) );
		getDirection().setY((float) ((initialSpeed * Math.random()) - (0.5*initialSpeed)) );
		getDirection().setZ((float) ((initialSpeed * Math.random()) - (0.5*initialSpeed)) );
		// Set the scale
		setScale(new Vec3(0.9f,0.9f,0.9f));
		// Set the color
		setColor(new Vec3(1.0f,0.1f,0.0f));
		// Set the lift
		setLife(1.0f);
	}
	
	@Override
	public void move(float timeSinceLastTick) {
		// Move by Direction
		Vec3 inc = getDirection().multiply(timeSinceLastTick);
		// Add gravity
		inc = inc.add(getGravity().multiply(timeSinceLastTick));
		// Grow
		Vec3 incScale = getScale().multiply(getGrowthRate()*timeSinceLastTick);
		// Slow Down
		float slowDown = (getSlowDown()*timeSinceLastTick)/1000f;
		inc = inc.sub(new Vec3(slowDown,slowDown,slowDown));
		
		offset = offset.add(inc);
		setLife(getLife() - (getFade()*timeSinceLastTick));
		setScale(getScale().add(incScale));
		
		setColor(getColor().sub(getColorChange().multiply(timeSinceLastTick)));
		if (this.getLife()<=0 && !killed.isOn()) {
			setup();
		}
	}
	
	@Override
	public Vec3 getPosition() {
		Vec3 position = ship.getPosition();
		position = position.add(ship.getRotation().GetXVector().multiply(offset.getX()));
		// offset position along Y vector		
		position = position.add(ship.getRotation().GetYVector().multiply(offset.getY()));
		// offset position along Z vector		
		position = position.add(ship.getRotation().GetZVector().multiply(offset.getZ()));
		
		return position;
	}

	@Override
	public String getType() {
		return "JetFire";
	}
}	
