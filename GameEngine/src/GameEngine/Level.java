package GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import VMQ.DLinkedList;
import VMQ.DLinkedListNode;
import VMQ.Vec3;
import VMQ.WindowDimensions;

import GameEngine.GridPos;
import GameEngine.InPlayObj;

public class Level {

	private ArrayList<Row> levelData = new ArrayList<Row>();
	private HashSet<InPlayObj> visibleObjects = new HashSet<InPlayObj>(); 	
	private HashMap<InPlayObj,DLinkedListNode<InPlayObj>> listNodes;		// List node for each enemy
	private HashMap<InPlayObj,GridPos> positions;							// Position of each enemy in the grid
	private int[][] enemysInSquare;											// Number of objects excluding PowerUps in each grid square
	
	private int offset;	
	private int rows;
	private int visibleRows;
	private int columns;
	private float xMax;
	private float yMax;
	private float boxWidth;
	private float boxHeight;
	private float speed;
	private float distance;
	
	private Player player1;
	private WindowDimensions windowDimensions; 
	private ParticleEngine explosions;
	private HUD hUD;
	
	public Level(int rows,float boxWidth,float boxHeight,float speed,float xMax,float yMax) {
		this.xMax = xMax;
		this.yMax = yMax;
		this.rows = rows;
		this.visibleRows = (int)(yMax/boxHeight)+1;
		this.columns = (int)(xMax/boxWidth)+1;
		this.boxWidth = boxWidth;
		this.boxHeight = boxHeight;
		this.speed = speed;
		offset = 0;
		distance = 0;
		for (int i=0;i<rows;i++) {
			levelData.add(new Row(columns));
		}
		
		enemysInSquare = new int[rows][columns];
		listNodes = new HashMap<InPlayObj,DLinkedListNode<InPlayObj>>();
		positions = new HashMap<InPlayObj,GridPos>();
	}
	
	public void build(ParticleEngine explosions,HUD hUD,WindowDimensions windowDimensions,Player player) {
		this.player1 = player;
		this.windowDimensions = windowDimensions;
		this.explosions = explosions;		
		this.hUD = hUD;
		initLevel();
		for (int i=0;i<visibleRows;i++) {
			for (int j=0;j<levelData.get(i).length();j++) {
				DLinkedList<InPlayObj> currentList = levelData.get(i).get(j);
				InPlayObj currentObject = currentList.getFirst();
				while (currentObject!=null) {
					visibleObjects.add(currentObject);
					currentObject = currentList.getNext();
				}
			}
		}
	}
	
	public void tick(float timeSinceLastTick) {
		this.distance = distance + (speed*timeSinceLastTick);
		int value = (int)(distance/boxHeight);
		int i=0;
		while ((offset+visibleRows)<rows && i<value) {
			
			for (int j=0;j<levelData.get(offset).length();j++) {
				// remove all the objects in the bottom row from the visible objects
				DLinkedList<InPlayObj> currentList = levelData.get(offset).get(j);
				InPlayObj currentObject = currentList.getFirst();
				while (currentObject!=null) {
					currentObject.clearMovement();
					currentObject = currentList.getNext();
				}
				
				// add top row +1 to visible objects
				currentList = levelData.get(offset+visibleRows).get(j);
				currentObject = currentList.getFirst();
				while (currentObject!=null) {
					visibleObjects.add(currentObject);
					currentObject = currentList.getNext();
				}
			}
			i++;
			offset++;
			this.distance = distance - boxHeight;
		}
		
		tickVisibleObjects(timeSinceLastTick);
	}
	
	private void tickVisibleObjects(float timeSinceLastTick) {
		Iterator<InPlayObj> it = visibleObjects.iterator();
		ArrayList<InPlayObj> objectsToAdd = new ArrayList<InPlayObj>();
		ArrayList<InPlayObj> objectsToMove = new ArrayList<InPlayObj>();
		while (it.hasNext()) {
			InPlayObj currentObject = it.next();
			
			currentObject.tick(timeSinceLastTick);
			if (currentObject instanceof Fires) {
				if (Math.abs(windowDimensions.getCenter().getZ()-currentObject.getPosition().getZ())<=yMax) {
					((Fires)currentObject).fireWeapon(timeSinceLastTick,objectsToAdd);
				}
			}
			if (currentObject.isFinished()) {
				removeReferences(currentObject);																		
				it.remove();
			} else if (currentObject.isAlive()) {
				objectsToMove.add(currentObject);																		
			} else {
				removeReferences(currentObject);																		
				it.remove();
				visibleObjects.remove(currentObject);
				if (currentObject.getCategory().equals("Enemy")) hUD.incScore(((Enemy)currentObject).getPoints());
				Explosion explosion = currentObject.getExplosion();
				if (explosion!=null) {
					explosions.add(explosion);
				}
			}
		}
		for (InPlayObj currentObject: objectsToAdd) {
			addObject(currentObject);
			visibleObjects.add(currentObject);
		}
		for (InPlayObj currentObject: objectsToMove) {
			moveObject(currentObject);
		}
	}
	
	public ArrayList<InPlayObj> getVisibleObjects() {
		return new ArrayList<InPlayObj>(visibleObjects);
	}				
	
	public int getRows() {
		return rows;
	}		
	
	public int getColumns() {
		return columns;
	}		
	
	public float getBoxWidth() {
		return boxWidth;
	}		
	
	public float getBoxHeight() {
		return boxHeight;
	}		
	
	public float getSpeed() {
		return speed;
	}		
	
	/*
	 * returns the current square an object is in
	 */
	public GridPos getObjectCurrentSquare(InPlayObj object) {
		return positions.get(object);
	}
	
	/*
	 * returns which square an object should be in from its screen position
	 */
	public GridPos getObjectSquare(InPlayObj object) {
		Vec3 position = formatPosition(object.getPosition());
		GridPos gridPosition = new GridPos(Math.min((int)(position.getZ()/boxHeight),rows-1),Math.min((int)(position.getX()/boxWidth),columns-1));
		if (gridPosition.getRow()<0 || gridPosition.getRow()>=rows) throw new IndexOutOfBoundsException("Object "+object+" Row(Z) "+gridPosition.getRow()+" out of grid bounds Position "+position+" original Position "+object.getPosition());
		if (gridPosition.getColumn()<0 || gridPosition.getColumn()>=columns) throw new IndexOutOfBoundsException("Object "+object+" Column(X) "+gridPosition.getRow()+" out of grid bounds");
		return gridPosition;
	}
	
	/* 
	 * iterates through each square and finds the nearest square with an object in
	 * returns null if there are none
	 */
	public InPlayObj getClosestObject(InPlayObj object) {
		GridPos currentPosition = getObjectSquare(object);
		// make sure the object is in the visible screen
		if (currentPosition.getRow()<offset || currentPosition.getRow()>=(offset+visibleRows)) return null;
		if (currentPosition.getColumn()<0 || currentPosition.getColumn()>=columns) throw new IndexOutOfBoundsException("Object Row out of bounds");

		float minDistance = 10000;
		InPlayObj closestObject = null;
		for (InPlayObj currentObject: visibleObjects) {
			float distance = currentObject.getPosition().sub(object.getPosition()).getMagnitude();
			if (distance < minDistance) {
				closestObject = currentObject;
				minDistance = distance;
			}
		}
		return closestObject;			
	}
	
	public DLinkedList<InPlayObj> getFromGrid(int row,int column) {
		if (row<0 || row>=rows) throw new IndexOutOfBoundsException("Invalid Row");
		if (column<0 || column>=columns) throw new IndexOutOfBoundsException("Invalid Column");
		return levelData.get(row).get(column);
	}		
	
	/*
	 * Formats a position so 0,0 is moved from the middle of the screen to the bottom left
	 */
	private Vec3 formatPosition(Vec3 position) {
		Vec3 newPosition = new Vec3();
		
		// format x position
		float totalWidth = boxWidth*columns;
		float totalHeight = windowDimensions.getMaxYPos(windowDimensions.getEye().getY());
		
		if (position.getX()<0.0f) {
			newPosition.setX(Math.max((totalWidth/2)+position.getX(),0));
		} else if (position.getX()>0.0f) {
			newPosition.setX(Math.min((totalWidth/2)+position.getX(),totalWidth));
		} else {
			newPosition.setX(totalWidth/2);
		}
		if (position.getZ()<0.0f) {
			newPosition.setZ((totalHeight)-position.getZ());
		} else if (position.getZ()>0.0f) {
			newPosition.setZ(Math.max((totalHeight)-position.getZ(),0));
		} else {
			newPosition.setZ(totalHeight);
		}
		return newPosition;
	}
	
	private void addObject(InPlayObj object) {
		GridPos position = getObjectSquare(object);
		DLinkedListNode<InPlayObj> objectNode = levelData.get(position.getRow()).get(position.getColumn()).addLast(object);
		positions.put(object, position);
		listNodes.put(object, objectNode);
		if (!(object.getCategory().equals("PowerUp"))) {
			enemysInSquare[position.getRow()][position.getColumn()]++;
		}
	}	
	
	private void moveObject(InPlayObj object) {
		GridPos newPos = getObjectSquare(object);
		GridPos oldPos = getObjectCurrentSquare(object);
		
		if (oldPos==null) throw new NullPointerException("Object not in grid");									// Object is not in the level so cannot be moved
		
		if ((newPos.getRow()!=oldPos.getRow()) || (newPos.getColumn()!=oldPos.getColumn())) {
			levelData.get(oldPos.getRow()).get(oldPos.getColumn()).removeNode(listNodes.get(object));
			levelData.get(newPos.getRow()).get(newPos.getColumn()).addNodeLast(listNodes.get(object));
			positions.put(object, newPos);
			if (!(object.getCategory().equals("PowerUp"))) {
				enemysInSquare[newPos.getRow()][newPos.getColumn()]++;
				enemysInSquare[oldPos.getRow()][oldPos.getColumn()]--;
			}
			
			if (newPos.getRow()>=offset) {
				visibleObjects.add(object);
			}
		} 
	}		
	
	private void removeReferences(InPlayObj object) {
		GridPos curPos = positions.get(object);
		if (curPos==null) {
			throw new NullPointerException("Object "+object+ "not in grid");			// This should not happen
		} else {
			if (!(object.getCategory().equals("PowerUp"))) {
				enemysInSquare[curPos.getRow()][curPos.getColumn()]--;
			}
			levelData.get(curPos.getRow()).get(curPos.getColumn()).removeNode(listNodes.get(object));
			positions.remove(object);
			listNodes.remove(object);
		}
	}		
	
//**********************************************************************************************************************************************
	
	private void addAtStart(InPlayObj object) {
		addObject(object);
		if (object instanceof hasComponents) {
			for (InPlayObj currentComponent: ((hasComponents) object).getComponentObjects()) {
				addAtStart(currentComponent);
			}
		}
	}
	
	private void initLevel() {
		float xMax  = windowDimensions.getMaxXPos(windowDimensions.getEye().getY())-(boxWidth/2);
//		addAtStart(new WallGun(new Vec3(-xMax,0,-56),player1.getShip(),20,true));
//		addAtStart(new WallGun(new Vec3(xMax,0,-56),player1.getShip(),20,false));
		addAtStart(new WallGun(new Vec3(-xMax,0,-136),player1.getShip(),20,true));
		addAtStart(new WallGun(new Vec3(xMax,0,-136),player1.getShip(),20,false));
		addAtStart(new WallGun(new Vec3(-xMax,0,-186),player1.getShip(),20,true));
		addAtStart(new WallGun(new Vec3(xMax,0,-186),player1.getShip(),20,false));
		
		for (Alien1 curAlien: addSnake(new Vec3(-xMax*2/3,0,-60),15,150,5)) {
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+8), new Vec3(0,-1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+24), new Vec3(0,1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+40), new Vec3(0,-1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+56), new Vec3(0,1,0),180.0f));
		}
		
		for (Alien1 curAlien: addSnake(new Vec3(xMax*2/3,0,-60),15,150,5)) {
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+8), new Vec3(0,1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+24), new Vec3(0,-1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+40), new Vec3(0,1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+56), new Vec3(0,-1,0),180.0f));
		}		
		
		for (Alien1 curAlien: addSnake(new Vec3(0,0,-75),15,150,5)) {
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+8), new Vec3(0,1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+24), new Vec3(0,-1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+40), new Vec3(0,1,0),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(curAlien.getPosition().getX(),0,curAlien.getPosition().getZ()+56), new Vec3(0,-1,0),180.0f));
		}
		
		int i=0;
		for (Alien1 curAlien: addSnake(new Vec3(-xMax,0,-95),20,150,10)) {
			curAlien.addMovement(new MovementLinear(new Vec3(1,0,0.1f),60-i));
			curAlien.addMovement(new MovementLinear(new Vec3(0,0,1f),40f));
			i += 5;
		}
		
		i=0;
		for (Alien1 curAlien: addSnake(new Vec3(xMax,0,-95),20,150,10)) {
			curAlien.addMovement(new MovementLinear(new Vec3(-1,0,0.1f),60-i));
			curAlien.addMovement(new MovementLinear(new Vec3(0,0,1f),40f));
			i += 5;
		}
		
		EnemyShip2 enemyShip2 = new EnemyShip2(new Vec3 (-xMax/2,0,-120),15, 150);
		enemyShip2.addMovement(new MovementMirror(player1.getShip(),new Vec3(0,0,1f),new Vec3(0,1,0)));
		addAtStart(enemyShip2);
		
		enemyShip2 = new EnemyShip2(new Vec3 (xMax/2,0,-140),15, 150);
		enemyShip2.addMovement(new MovementMirror(player1.getShip(),new Vec3(0,0,1f),new Vec3(0,1,0)));
		addAtStart(enemyShip2);
		
		enemyShip2 = new EnemyShip2(new Vec3 (-xMax/2,0,-160),15, 150);
		enemyShip2.addMovement(new MovementMirror(player1.getShip(),new Vec3(0,0,1f),new Vec3(0,1,0)));
		addAtStart(enemyShip2);
		
		float posInc = xMax/2;
		for (i=0;i<5;i++) {
			SwivleShip swivleShip = new SwivleShip(new Vec3 (-xMax+(i*posInc),0,-210), 5,player1.getShip(),100);
			swivleShip.addMovement(new MovementLinear(new Vec3(0,0,1),60));
			addAtStart(swivleShip);
		}

		// Power up - Missile Launcher
//        PowerUpWeapon powerUp2 = new PowerUpWeapon(new Vec3(-xMax,0,-45),5.0f,new MissileLauncher(this));
//        powerUp2.addMovement(new MovementLinear(new Vec3(1,0,0),100));
//		addAtStart(powerUp2);
		
		// Power up - Rear Gun
        PowerUpWeapon powerUp3 = new PowerUpWeapon(new Vec3(-xMax,0,-25),10.0f,new DualShot());
        powerUp3.addMovement(new MovementLinear(new Vec3(1,0,0),100));
		addAtStart(powerUp3);
		
		// Power up - Dual Shot
		PowerUpWeapon powerUp1 = new PowerUpWeapon(new Vec3(-xMax,0,-190),5.0f,new RearGun());
       	powerUp1.addMovement(new MovementLinear(new Vec3(1,0,0),100));
		addAtStart(powerUp1);
		
		// Power up Health
		PowerUpHealth powerUp4 = new PowerUpHealth(new Vec3(-xMax,0,-70),10.0f,25);
		powerUp4.addMovement(new MovementLinear(new Vec3(1,0,0),100));
		addAtStart(powerUp4);		
	}
	
	private Alien1[] addSnake(Vec3 position,  float speed, float rotSpeed,int amount) {
		Alien1[] alienList = new Alien1[amount];
		for (int i=0;i<amount;i++) {
			alienList[i] = new Alien1(new Vec3 (position.getX(),position.getY(),position.getZ()),speed,rotSpeed);		//-(gap*i)
			alienList[i].addMovement(new MovementWait(0.5f*i)); //MovementLinear(new Vec3(0,0,1),gap*i));
			addAtStart(alienList[i]);
		}
		return alienList;
	}
	
/*	private void level1() {
		float yTop = windowDimensions.getMaxYPos(-80)*-1;
		float yBottom = windowDimensions.getMaxYPos(-80);
		float xLeft = windowDimensions.getMaxXPos(-80);
		float xRight = windowDimensions.getMaxXPos(-80)*-1;
		System.out.println("Level dimensions x: "+xLeft+","+xRight+" y: "+yTop+","+yBottom);

		// Power up - dual Shot
        PowerUpWeapon powerUp1 = new PowerUpWeapon(new Vec3(-50,20,-80),5.0f,0.0f,new MissileLauncher(collider));
        powerUp1.addMovement(new MovementLinear(new Vec3(1,0,0),100));
		idleEnemys.addLast(powerUp1);

		PowerUpWeapon powerUp2 = new PowerUpWeapon(new Vec3(-50,20,-80),5.0f,5.0f,new DualShot());
        powerUp2.addMovement(new MovementLinear(new Vec3(1,0,0),100));
		idleEnemys.addLast(powerUp2);

		PowerUpWeapon powerUp3 = new PowerUpWeapon(new Vec3(-50,20,-80),5.0f,10.0f,new RearGun());
        powerUp3.addMovement(new MovementLinear(new Vec3(1,0,0),100));
		idleEnemys.addLast(powerUp3);

		// first alien, does a figure of 8 to the left
		// speed 15, rotate 150, activationDistance 5, number 5
		for (Alien1 curAlien : addSnake1(new Vec3(0,yTop,-80),15,150,5,5)) {
			curAlien.addMovement(new MovementCircular(new Vec3(0,yTop-10,-80), new Vec3(0,0,1),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(0,yTop-30,-80), new Vec3(0,0,-1),180.0f));
			curAlien.addMovement(new MovementLinear(new Vec3(-1,-1,0).normalize(),50));
			idleEnemys.addLast(curAlien);
		}

		// second alien, does a figure of 8 to the right
		// speed 15, rotate 150, activationDistance 10, number 5
		for (Alien1 curAlien : addSnake1(new Vec3(0,yTop,-80),15,150,10,5)) {
			curAlien.addMovement(new MovementCircular(new Vec3(0,yTop-10,-80), new Vec3(0,0,-1),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(0,yTop-30,-80), new Vec3(0,0,1),180.0f));
			curAlien.addMovement(new MovementLinear(new Vec3(1,-1,0).normalize(),50));
			idleEnemys.addLast(curAlien);
		}
		// third alien, comes in from the left and then drops in a row
		// speed 15, rotate 150, activationDistance 15, number 8
		int i=0;
		for (Alien1 curAlien : addSnake1(new Vec3(xLeft,yTop-20,-80),15,150,15,8)) {
			curAlien.addMovement(new MovementLinear(new Vec3(1,0,0),80-(i*10)));
			curAlien.addMovement(new MovementLinear(new Vec3(0,-1,0),50));
			idleEnemys.addLast(curAlien);
			i++;
		}
		// enemy mirror ship, top of the screen 
		// speed 15, rotate 150, activationDistance 15
		addEnemyShip2(new Vec3 (0,yTop,-80),15, 150,15,player1.getShip());
		// 2 swivle ships descend from the sides 
		// speed 15, activationDistance 20, trackspeed 100
		SwivleShip swivleShip = new SwivleShip(new Vec3 (xLeft/2,yTop,-80), 15,20,player1.getShip(),100);
		swivleShip.addMovement(new MovementLinear(new Vec3(0,-1,0),20));
        swivleShip.addMovement(new MovementCircular(new Vec3(xLeft/2,yTop-30,-80), new Vec3(0,0,1),1800));
		swivleShip.addMovement(new MovementLinear(new Vec3(0,-1,0),30));
		idleEnemys.addLast(swivleShip);
		swivleShip = new SwivleShip(new Vec3 (xRight/2,yTop,-80), 15,20,player1.getShip(),100);
		swivleShip.addMovement(new MovementLinear(new Vec3(0,-1,0),20));
        swivleShip.addMovement(new MovementCircular(new Vec3(xRight/2,yTop-30,-80), new Vec3(0,0,-1),1800));
        swivleShip.addMovement(new MovementLinear(new Vec3(0,-1,0),30));
        idleEnemys.addLast(swivleShip);
        // 3 Enemy follow ships
        // speed 15, rotation speed 150, activationDistance 30
        addEnemyShip(new Vec3(xLeft/2,yTop,-80),15, 150,30,player1.getShip());
        addEnemyShip(new Vec3(xRight/2,yTop,-80),15, 150,30,player1.getShip());
        addEnemyShip(new Vec3(0,yTop,-80),15, 150,30,player1.getShip());
        
        // Wall guns either side of the screen
    	addWallGun(new Vec3(xLeft+5,yTop,-80), 0, 20,false);
    	addWallGun(new Vec3(xRight-5,yTop,-80), 0, 20,true);
	}


/*
 * *********************************************************************************	
 */

	// This will be replaced by loading a level
/*	private void addPowerUps() {
		Movement powerUpMovement = new MovementLinear(new Vec3(1,0,0),100);
		PowerUpWeapon powerUp1 = new PowerUpWeapon(new Vec3(-50,0,-80),5.0f,0.0f,new MissileLauncher(collider)); 
        powerUp1.addMovement(powerUpMovement);
        PowerUpWeapon powerUp2 = new PowerUpWeapon(new Vec3(-50,-10,-80),5.0f,0.0f,new RearGun());
        powerUp2.addMovement(powerUpMovement);
        PowerUpWeapon powerUp3 = new PowerUpWeapon(new Vec3(-50,10,-80),5.0f,0.0f,new FlameGun());
        powerUp3.addMovement(powerUpMovement);
        PowerUpWeapon powerUp4 = new PowerUpWeapon(new Vec3(-50,20,-80),5.0f,0.0f,new DualShot());
        powerUp4.addMovement(powerUpMovement);
		idleEnemys.addLast(powerUp1);
		idleEnemys.addLast(powerUp2);
		idleEnemys.addLast(powerUp3);
		idleEnemys.addLast(powerUp4);
	}



	// This will be replaced by loading a level
	// Needs fixing
	private ArrayList<Alien1> addSnake1(Vec3 position,  float speed, float rotSpeed,float activationDistance,int amount) {
		ArrayList<Alien1> alienList = new ArrayList<Alien1>();
		for (int i=0;i<amount;i++) {
			float tempActivationTimeFromStart=activationDistance+(i*5/speed);
			Alien1 alien1 = new Alien1(new Vec3 (position.getX(),position.getY(),position.getZ()),speed,rotSpeed,tempActivationTimeFromStart);
			alienList.add(alien1);
		}
		return alienList;
	}

	// This will be replaced by loading a level
	// Needs fixing
	private void addWallGun(Vec3 position, float activationDistance, float trackSpeed,boolean facing) {
		idleEnemys.addLast(new WallGun(position,activationDistance,player1.getShip(),trackSpeed,facing));
	}

	// This will be replaced by loading a level
	private void addSwivleShip(Vec3 position, float speed,float activationDistance, float trackSpeed) {
		SwivleShip swivleShip = new SwivleShip(position,speed,activationDistance,player1.getShip(),trackSpeed);
		swivleShip.addMovement(new MovementCircular(new Vec3(10.0f,10.0f,-80.0f), new Vec3(0,0,1),720.0f));
        swivleShip.addMovement(new MovementCircular(new Vec3(-10.0f,-10.0f,-80.0f), new Vec3(0,0,1),720.0f));
		idleEnemys.addLast(swivleShip);
	}

	// This will be replaced by loading a level
	private void addEnemyShip(Vec3 position,float speed, float rotSpeed,float activationDistance,InPlayObj toTrack) {
		EnemyShip enemyShip = new EnemyShip(new Vec3(position.getX(),position.getY(),position.getZ()),
				speed,rotSpeed,activationDistance);
		enemyShip.addMovement(new MovementFollow(toTrack,0.0f,0.0f));
		idleEnemys.addLast(enemyShip);
	}

	// This will be replaced by loading a level
	private void addEnemyShip2(Vec3 position,float speed, float rotSpeed,float activationDistance,InPlayObj toMirror) {
		EnemyShip2 enemyShip2 = new EnemyShip2(position,speed,rotSpeed,activationDistance);
		enemyShip2.addMovement(new MovementMirror(toMirror,new Vec3 (0.0f,-0.1f,0)));
		idleEnemys.addLast(enemyShip2);
	}

	// This will be replaced by loading a level
/*	private void addSnakes() {
		for (Alien1 curAlien: addSnake1(new Vec3(0.0f,10.0f,-80.0f),1000.0f,150.0f,5,5)) {
			curAlien.addMovement(new MovementCircular(new Vec3(10.0f,10.0f,-80.0f), new Vec3(0,0,1),10000.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(-10.0f,-10.0f,-80.0f), new Vec3(0,0,1),720.0f));
			idleEnemys.addLast(curAlien);
		}

        for (Alien1 curAlien: addSnake1(new Vec3(-10.0f,10.0f,-80.0f),1000.0f,150.0f,10,5)) {
			curAlien.addMovement(new MovementCircular(new Vec3(10.0f,10.0f,-80.0f), new Vec3(0.7f,0.7f,0.0f),180.0f));
			curAlien.addMovement(new MovementCircular(new Vec3(10.0f,-10.0f,-80.0f),new Vec3(0,0,1),720.0f));
			idleEnemys.addLast(curAlien);
		}		
  	}		*/

	// This will be replaced by loading a level
/*	private void addChars() {
		addSnakes();
		addPowerUps();
 
		addEnemyShip2(new Vec3(0.0f,20.0f,-80.0f),5.0f,150.0f,0,player1.getShip());
		addEnemyShip(new Vec3(-10.0f,30.0f,-80.0f),10.0f,150.0f,0,player1.getShip()); 		
		addSwivleShip(new Vec3(0.0f,20.0f,-80.0f),1000.0f,0, 100.0f);							
		Collections.sort(idleEnemys);	
//		addWallGun(new Vector3(30.0f,10.0f,-80.0f),0.0f,10.0f);					
	}			*/	
}
