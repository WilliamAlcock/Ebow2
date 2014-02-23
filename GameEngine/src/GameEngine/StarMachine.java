package GameEngine;
import VMQ.Vec3;
import java.util.Iterator;
import java.util.LinkedList;

public class StarMachine {

	private LinkedList<GameObj> backgroundObjects;
	private Vec3 startPosition;
	private Vec3 screenCenter;
	private Vec3 direction;
	private int numberOfStars;
	private float minSpeed;
	private float maxSpeed;
	private float distanceToTravel;
	
	public StarMachine(Vec3 startPosition,Vec3 screenCenter,Vec3 direction,float minSpeed,float maxSpeed,float distanceToTravel,int numberOfStars,LinkedList<GameObj> backgroundObjects) {
		this.startPosition = startPosition;
		this.screenCenter = screenCenter;
		
		this.direction = direction;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		
		this.distanceToTravel = distanceToTravel;
		this.backgroundObjects = backgroundObjects;
		addStar(numberOfStars,true);
	}
	
	public int getNumberOfStars() {
		return numberOfStars;
	}
	
	public void tick(float timeSinceLastTick) {
		Iterator<GameObj> it = backgroundObjects.iterator();
		int removals=0;
		while (it.hasNext()) {
			GameObj curObj = it.next();
			if (curObj instanceof Star) {
				Star curStar = (Star) curObj;
				curStar.tick(timeSinceLastTick);
				if (curStar.getDistanceTravelled()>distanceToTravel) {
					it.remove();
					removals++;
				}			
			}
		}
		numberOfStars -= removals;
		if (removals>0) addStar(removals,false);
	}
	
	public void addStar(int numberOfStars,boolean randomZ) {
		for (int i=0;i<numberOfStars;i++) {
			// random speed between max and min
			int speed = (int)(((maxSpeed-minSpeed)*Math.random())+minSpeed);
			// random number between -startposition and startposition
			float xpos = (float) ((startPosition.getX()*2*Math.random())-startPosition.getX());
			float zpos;
			if (randomZ) {
				zpos = (float) ((startPosition.getZ()*2*Math.random())-startPosition.getZ());
			} else {
				zpos = startPosition.getZ();
			}
			backgroundObjects.add(new Star(new Vec3(xpos,startPosition.getY(),screenCenter.getZ()+zpos),direction,speed,new Vec3(0.5f,0.5f,0.5f)));			
		}
		this.numberOfStars += numberOfStars;
	}
}
