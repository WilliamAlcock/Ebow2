package GameEngine;
import VMQ.Vec3;

public abstract class Enemy extends InPlayObj{
	
	private int points;
	
	public Enemy(Vec3 position, float speed, float rotSpeed, int health,int damage) {
		super(position, speed, rotSpeed,health,damage);
		this.points = health*5;
	}
	
	public int getPoints() {
		return points;
	}
	
	@Override
	public Explosion getExplosion() {
		return new Explosion(getExplosionPosition(),15,getExplosionDimensions(),true,true,true,true,true,true);
	}
	
	@Override
	public DISPLAYTYPE getDisplayType() {
		return DISPLAYTYPE.SOLID;
	}
	
	@Override
	public String getCategory() {
		return "Enemy";
	}
	
	@Override
	public void handleCollision(InPlayObj objCollidingWith,Vec3 myDimensions,Vec3 hisDimensions) {
		objCollidingWith.decHealth(getDamage());
		this.decHealth(objCollidingWith.getDamage());
		if (objCollidingWith.getHealth()<=0) {
			if (objCollidingWith instanceof Bullet) {
				objCollidingWith.setExplosionPosition(this.getPosition());
				objCollidingWith.setExplosionDimensions(myDimensions);
			} else {
				objCollidingWith.setExplosionPosition(objCollidingWith.getPosition());
				objCollidingWith.setExplosionDimensions(hisDimensions);
			}
		}
		if (getHealth()<=0) {
			
		}
		
	}
	
	@Override
	public void tick(float timeSinceLastTick) {
		processMovement(timeSinceLastTick);
	}
}
