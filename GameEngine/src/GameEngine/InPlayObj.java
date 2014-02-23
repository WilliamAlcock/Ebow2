package GameEngine;
import VMQ.DLinkedList;
import VMQ.Quaternion;
import VMQ.Vec3;

public abstract class InPlayObj extends GameObj{
	private float banking;
	private float speed;
	private float rotSpeed;
	
	private float curDist;
	private DLinkedList<Movement> movement = new DLinkedList<Movement>();
	
	private int health;
	private int damage;
	
	private Vec3 explosionPosition = new Vec3();
	private Vec3 explosionDimensions = new Vec3();

	public InPlayObj(Vec3 position, float speed, float rotSpeed,int health,int damage) {
		super(position); 
		this.banking = 0;
		this.speed = speed;
		this.rotSpeed = rotSpeed;
		this.curDist = 0f;
		this.health = health;
		this.damage = damage;
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
		if (movement.length()>0) { 
			Movement castMove = movement.getFirst();
			if (castMove.move(this,timeSinceLastTick)) {
				movement.removeFirst();
				curDist=0;
			} 
		}
	}
	
	public void setExplosionPosition(Vec3 explosionPosition) {
		this.explosionPosition = explosionPosition;
	}
	
	public Vec3 getExplosionPosition() {
		return this.explosionPosition;
	}
	
	public void setExplosionDimensions(Vec3 explosionDimensions) {
		this.explosionDimensions = explosionDimensions;
	}
	
	public Vec3 getExplosionDimensions() {
		return this.explosionDimensions;
	}
	
	public float getCurDist() {
		return curDist;
	}
	
	public void setCurDist(float curDist) {
		this.curDist = curDist;
	}
	
	public void setMovement(DLinkedList<Movement> movement) {
		this.movement = movement;
	}		
	
	public void addMovement(Movement move) {
		movement.addLast(move);
	}		
	
	public void clearMovement() {
		movement.clear(); 
	}
	
	public boolean isFinished() {
		return (movement.length()==0);
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
	
	public abstract void tick(float timeSinceLastTick);
	
	public abstract Explosion getExplosion();
	
	public abstract String getCategory();
	
	public abstract void handleCollision(InPlayObj objCollidingWith, Vec3 myDimensions, Vec3 hisDimensions);
}
