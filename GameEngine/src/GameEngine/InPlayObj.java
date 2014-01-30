package GameEngine;
import VMQ.Quaternion;
import VMQ.Vec3;

public abstract class InPlayObj extends GameObj implements Comparable<InPlayObj>{
	private float banking;
	private float speed;
	private float rotSpeed;
	
	private float activationDistance;
	
	private float curDist;
	private DLinkedList Movement = new DLinkedList();
	
	private int health;
	private int damage;

	public InPlayObj(Vec3 position, float speed, float rotSpeed, float activationDistance,int health,int damage) {
		super(position); 
		this.banking = 0;
		this.speed = speed;
		this.rotSpeed = rotSpeed;
		this.curDist = 0f;
		this.health = health;
		this.damage = damage;
		this.activationDistance = activationDistance;
	}
	
	public void rotate(float deg,Vec3 vector) {
		setRotation(getRotation().normalize());
		setRotation(new Quaternion(deg,vector).multiply(getRotation()));
	}
	
	public void rotateTimeBased(float timeSinceLastTick,Vec3 vector) {
		float distanceToRotate = (rotSpeed*timeSinceLastTick);
		rotate(distanceToRotate,vector);	
	}			
	
	public void moveTimeBased(float timeSinceLastTick,Vec3 vector) {
		float distanceToMove = (speed*timeSinceLastTick);
		setPosition(getPosition().add(vector.multiply(distanceToMove)));
		setCurDist(getCurDist()+distanceToMove);
	}
	
	protected void processMovement(float timeSinceLastTick) {
		if (Movement.getSize()>0) { 
			Movement castMove = (Movement)Movement.getHead().getData();
			if (castMove.move(this,timeSinceLastTick)) {
				Movement.removeFirst();
				curDist=0;
			} 
		}
	}
	
	public float getCurDist() {
		return curDist;
	}
	
	public void setCurDist(float curDist) {
		this.curDist = curDist;
	}
	
	public void addMovement(Movement move) {
		Movement.addLast(move);
	}
	
	public boolean isFinished() {
		return (Movement.getSize()==0);
	}
	
	public boolean isAlive() {
		return health>0;
	}
	
	public void decHealth(int decAmount) {
		if (health>0) {
			health = health - decAmount;
		}
	}
	
	public void incHealth(int incHealth) {
		health = health + incHealth;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void setSpeed(float speed) {
		if (speed<0) {
			this.speed=speed*-1;
		} else {
			this.speed=speed;
		}
	}

	public float getSpeed() {
		return this.speed;
	}
		
	public void setRotSpeed(float speed) {
		if (speed<0) {
			this.rotSpeed=speed*-1;
		} else {
			this.rotSpeed=speed;
		}
	}

	public float getRotSpeed() {
		return this.rotSpeed;
	}
	
	public void bankLeft() {
		if (banking>-30) {
			rotate(-1.0f,getRotation().GetZVector());
			banking -= 1;
		}
	}
	
	public void bankRight() {
		if (banking<30) {
			rotate(1.0f,getRotation().GetZVector());
			banking += 1;
		}
	}
	
	public void levelOff() {
		for (int i=0;i<2;i++) {
			if (banking!=0) {
				if (banking>0) {
					bankLeft();
				} else if (banking<0) {
					bankRight();
				}
			}
		}
	}	
	
	public float getActivationDistance() {
		return activationDistance;
	}
	
	public abstract void tick(float timeSinceLastTick);
	
	public abstract Explosion getExplosion(Vec3 dimensions);
	
	public abstract String getCategory();
	
	public abstract void handleCollision(InPlayObj objCollidingWith);
	
	@Override
	public int compareTo(InPlayObj p) {
		if (getActivationDistance()>p.getActivationDistance()) {
			return 1;
		} else if (getActivationDistance()<p.getActivationDistance()) {
			return -1;
		} else {
			return 0;
		}
    }
}
