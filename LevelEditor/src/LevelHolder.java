import java.util.ArrayList;

import VMQ.Vec3;
import VMQ.WindowDimensions;
import GameEngine.Enemy;
import GameEngine.EnemyShip;

public class LevelHolder {

	private Level level;
	private ViewingSquare viewingSquare;
	private ArrayList<LevelEditorObjects> editorObjects = new ArrayList<LevelEditorObjects>();
	private ArrayList<Enemy> enemys = new ArrayList<Enemy>();
	
	public LevelHolder(WindowDimensions windowDimensions) {
		this.viewingSquare = new ViewingSquare(new Vec3(0,75,0));
		editorObjects.add(viewingSquare);
		Enemy newEnemy = new EnemyShip(new Vec3(0,0,0), 0, 0);
		newEnemy.setScale(newEnemy.getScale().multiply(0.5f));
		enemys.add(newEnemy);
	}
	
	public void newLevel(float duration,float speed) {
		this.level = new Level(duration,speed);
	}
	
	public void moveViewingSquare(float distance) {
		viewingSquare.getPosition().setZ(viewingSquare.getPosition().getZ()+distance);
	}
	
	public void zoom(float distance) {
		
	}
	
	public ArrayList<Enemy> getEnemys() {
		return enemys;
	}
	
	public ArrayList<LevelEditorObjects> getEditorObjects() {
		return editorObjects;
	}
}
