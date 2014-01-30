import java.util.ArrayList;
import java.util.HashMap;

import GameEngine.Enemy;


public class Level {

	private float duration;
	private float speed;
	private ArrayList<Enemy> enemys = new ArrayList<Enemy>();
	private HashMap<Enemy,Float> angle = new HashMap<Enemy,Float>();
	
	public Level(float duration,float speed) {
		this.duration = duration;
		this.speed = speed;
	}
	
	public void addEnemy(Enemy enemy) {
		enemys.add(enemy);
	}
	
	public void removeEnemy(Enemy enemy) {
		enemys.remove(enemy);
		
	}
}
