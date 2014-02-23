package GameEngine;

import java.util.HashMap;
import java.util.List;

import VMQ.DLinkedList;
import VMQ.Vec3;

public class CollisionDetector {
	
	private Level level;
	private HashMap<String,Vec3> dimensions;							// Dimensions of each type of obj
	private Player player1;
	
	// need to pass the screen width and height in
	// get the width and height of the player and work out the amount of rows and columns
	public CollisionDetector(HashMap<String,Vec3> dimensions,Player player1,Level level) {
		this.dimensions = dimensions;
		this.player1 = player1;
		this.level = level;
	}
	
	public void tick() {
		if (level.getVisibleObjects().size()>0) {
			check((List<InPlayObj>) player1.getObjects().clone()); 
		}
	}		
	
	/*
	 * checks if an object is colliding with any objects in the grid
	 */
	public void check(List<InPlayObj> toCheck) {
		for (InPlayObj currentObject: toCheck) {
			GridPos objectPos = level.getObjectSquare(currentObject);
			int columnStart = Math.max(objectPos.getColumn()-1,0);
			int columnEnd = Math.min(objectPos.getColumn()+2, level.getColumns());
			int rowStart = Math.max(objectPos.getRow()-1, 0);
			int rowEnd = Math.min(objectPos.getRow()+2, level.getRows()); 
			for (int j=rowStart;j<rowEnd;j++) {
				for (int i=columnStart;i<columnEnd;i++) {
					if (level.getFromGrid(j, i).length()>0) {
						// perform close detect on all objects in the list
						DLinkedList<InPlayObj> currentList = level.getFromGrid(j,i);
						InPlayObj currentEnemy = currentList.getFirst();
						while(currentEnemy!=null) {
							closeDetect(currentObject,currentEnemy);
							currentEnemy = currentList.getNext();
						}
					}
				}
			}
		}
	}
	
	// check if two objects are touching
	// this is done by seeing if the any of the corners of the smaller object are inside the corners of the larger object
	private void closeDetect(InPlayObj object1, InPlayObj object2) {
		float xDist = Math.abs(object1.getPosition().getX() - object2.getPosition().getX());
		if (xDist<(dimensions.get(object1.getType()).getX()+dimensions.get(object2.getType()).getX())) {
			float yDist = Math.abs(object1.getPosition().getZ() - object2.getPosition().getZ());
			if (yDist<(dimensions.get(object1.getType()).getZ()+dimensions.get(object2.getType()).getZ())) {
				object1.handleCollision(object2,dimensions.get(object1.getType()),dimensions.get(object2.getType()));
			}
		}
	}
}