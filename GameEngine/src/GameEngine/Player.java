package GameEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import VMQ.WindowDimensions;
import VMQ.Vec3;

public class Player {

	private LinkedList<InPlayObj> myObjects = new LinkedList<InPlayObj>();
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private Ship ship;
	private Engine leftRearEngine,rightRearEngine,leftFrontEngine,rightFrontEngine;
	private Vec3 scrollingVector;
	
	private WindowDimensions windowDimensions;
	
	private ParticleEngine particles;
	private HashMap<String,Vec3> dimensions;
	
	private boolean[] nextDirection = new boolean[6];
	private float reversePower = 2f;
	private float rateOfFire = 0.2f;
	
	public Player(Vec3 position,float speed,WindowDimensions windowDimensions,HashMap<String,Vec3> dimensions,ParticleEngine particles,Vec3 scrollingVector) {
		this.scrollingVector = scrollingVector;
		ship = new Ship(position,speed,this);
		ship.setRateOfFire(rateOfFire);
		this.windowDimensions = windowDimensions;
		this.dimensions = dimensions;
		this.particles = particles;
		leftRearEngine = new Engine(ship.getPosition(),new Vec3(-1.3f,0,-5f),new Vec3(0,0,-1),30,1,10);
		rightRearEngine = new Engine(ship.getPosition(),new Vec3(1.3f,0,-5f),new Vec3(0,0,-1),30,1,10);
		leftFrontEngine = new Engine(ship.getPosition(),new Vec3(1.3f,0,1f),new Vec3(1,0,1),30,0,10);
		rightFrontEngine = new Engine(ship.getPosition(),new Vec3(-1.3f,0,1f),new Vec3(-1,0,1),30,0,10);
		myObjects.add(ship);
	}
	
	public float getRateOfFire(){
		return rateOfFire;
	}
	
	public void setRateOfFire(float rateOfFire) {
		ship.setRateOfFire(rateOfFire);
		for (Weapon curWeapon: weapons) {
			if (curWeapon instanceof Fires)
			((Fires)curWeapon).setRateOfFire(rateOfFire);
		}
	}
	
	public Ship getShip() {
		return ship;
	}
	
	public void setNextMove(int direction) {
		if ((direction>=0) && (direction<6)) {
			nextDirection[direction] = true;
		}
	}
	
	// Add a weapon
	public void addWeapon(Weapon weapon) {
		if (!weaponExists(weapon)) {
			weapons.add(weapon);
			myObjects.add(weapon);
			if (weapon instanceof Fires) ((Fires)weapon).setRateOfFire(rateOfFire);		// Set the rate of fire
		}
		if (weapon.getType().equals("DualShot")) {
			ship.setFires(false);
		}
		if (weapon instanceof hasComponents) {
			for (InPlayObj curWeapon: ((hasComponents)weapon).getComponentObjects()) {
				((Weapon)curWeapon).fit(this);
			}
		}
	}
	
	private boolean weaponExists(Weapon weapon) {
		for (Weapon curWeapon: weapons) {
			if (curWeapon.getType().equals(weapon.getType())) return true;
		}
		return false;
	}
	
	// tick
	public boolean tick(float timeSinceLastTick) {
		ship.tick(timeSinceLastTick);
		// forward
		if (nextDirection[0]) {
			ship.forward(timeSinceLastTick);
			leftRearEngine.increasePower(1);
			rightRearEngine.increasePower(1);
			nextDirection[0] = false;
		} else {
			leftRearEngine.decreasePower(1);
			rightRearEngine.decreasePower(1);
		}
		// reverse
		if (nextDirection[1]) {
			ship.reverse(timeSinceLastTick);
			leftFrontEngine.increasePower(1);
			rightFrontEngine.increasePower(1);
			nextDirection[1] = false;
		} else {
			leftFrontEngine.decreasePower(1);
			rightFrontEngine.decreasePower(1);
		}
		// right
		if (nextDirection[2]) {
			ship.bankRight(timeSinceLastTick);
			rightFrontEngine.decreasePower(2);
			rightFrontEngine.off();
			nextDirection[2] = false;
		} else {
			rightFrontEngine.on();
		}
		// left
		if (nextDirection[3]) {
			ship.bankLeft(timeSinceLastTick);
			leftFrontEngine.decreasePower(2);
			leftFrontEngine.off();
			nextDirection[3] = false;
		} else {
			leftFrontEngine.on();
		}
		// neither left nor right
		if (nextDirection[4]) {
			ship.levelOff();
			nextDirection[4] = false;
		}
		// firing
		if (nextDirection[5]) {
			nextDirection[5] = false;
			
			// fire the ships gun
			ship.fireWeapon(timeSinceLastTick, myObjects);
			
			// fire all the weapons
			for (Weapon curWeapon: weapons) {
				if (curWeapon instanceof Fires) {
					((Fires)curWeapon).fireWeapon(timeSinceLastTick, myObjects);
				}
			}
		}
		adjustPosition(timeSinceLastTick);
		
		Iterator<InPlayObj> it = myObjects.iterator();
		while (it.hasNext()) {
			InPlayObj curObj = it.next();
			curObj.tick(timeSinceLastTick);
			if (curObj.isFinished()) {
				it.remove();
				if (curObj instanceof Weapon) weapons.remove(curObj);
			} else if (!curObj.isAlive()) {
				particles.add(curObj.getExplosion(dimensions.get(curObj.getType())));					
				it.remove();
				if (curObj instanceof Weapon) weapons.remove(curObj);
				if (curObj instanceof Ship) {
					leftRearEngine.kill();
					rightRearEngine.kill();
					leftFrontEngine.kill();
					rightFrontEngine.kill();
					for (Weapon curWeapon: weapons) {
						curWeapon.decHealth(curWeapon.getHealth());
					}
					return false;
				}
			}
		}
		return true;
	}
	
	// Engines
	private void adjustPosition(float timeSinceLastTick) {
		float maxX = windowDimensions.getMaxXPos(-windowDimensions.getEye().getY())-dimensions.get("Ship").getX();
		float maxY = windowDimensions.getMaxYPos(-windowDimensions.getEye().getY())-dimensions.get("Ship").getZ();
		Vec3 newPos = ship.getPosition().add(scrollingVector.multiply(timeSinceLastTick));
		ship.getPosition().setX(newPos.getX());
		ship.getPosition().setY(newPos.getY());
		ship.getPosition().setZ(newPos.getZ());
		if (ship.getPosition().getX()<maxX) {
			ship.getPosition().setX(maxX);
		} else if (ship.getPosition().getX()>-maxX) {
			ship.getPosition().setX(-maxX);
		}
		if (ship.getPosition().getZ()<(maxY+windowDimensions.getEye().getZ())) {
			ship.getPosition().setZ((maxY+windowDimensions.getEye().getZ()));
//			reverseEngine(timeSinceLastTick);
		} else {
//			rechargeEngine(timeSinceLastTick);
			if (ship.getPosition().getZ()>-(maxY+windowDimensions.getEye().getZ())) {
				ship.getPosition().setZ(-(maxY+windowDimensions.getEye().getZ()));
			}
		}
		System.out.println(ship.getPosition()+" - Max - "+maxX+","+maxY);
	}
	
	// Reverse Engines
/*	private void reverseEngine(float timeSinceLastTick) {
		if (reversePower>0) {
			if (scrollingVector.getY()>-1)  scrollingVector.setY(scrollingVector.getY()-(timeSinceLastTick*2));
			reversePower -= timeSinceLastTick;
		} else {
			rechargeEngine(timeSinceLastTick);
		}
	}		*/
	
	// Recharge Engines
/*	private void rechargeEngine(float timeSinceLastTick) {
		if (scrollingVector.getY()<1) scrollingVector.setY(scrollingVector.getY()+(timeSinceLastTick*4));
		if (reversePower<2) reversePower += timeSinceLastTick;
	}		*/
	
	// Return a list of the objects
	public LinkedList<InPlayObj> getObjects() {
		return myObjects;
	}			
	
	// Return a lit of the particles
	public ParticleGenerator[] getEngines() {
		return new ParticleGenerator[] {leftRearEngine,rightRearEngine,leftFrontEngine,rightFrontEngine};
	}	
}