package GameEngine;
import java.util.HashMap;
import java.util.List;

import VMQ.WindowDimensions;
import VMQ.Vec3;

public class GameEngine {
	private Clock gameClock;											// Clock
	private Player player1;												// Player
	
	private Level level;												// The level
	
	private WindowDimensions windowDimensions;
	private CollisionDetector collider;									// Collision detector
	private Background background;										// Background
	private ParticleEngine particles;									// Explosions etc
	private HUD hUD;													// Head up display
	
	public GameEngine(HashMap<String,Vec3> dimensions,WindowDimensions windowDimensions) {
		this.windowDimensions = windowDimensions;
		gameClock = new Clock();
		// this will be loaded from file eventually
		level = new Level(60,dimensions.get("Ship").getX()*2.0f,dimensions.get("Ship").getZ()*2.0f,5.0f,
				windowDimensions.getMaxXPos(windowDimensions.getEye().getY())*2,windowDimensions.getMaxYPos(windowDimensions.getEye().getY())*2);
		
		background = new Background(dimensions.get("Wallpaper").getX(),dimensions.get("Wallpaper").getZ(),windowDimensions.getEye());
		hUD = new HUD(windowDimensions,dimensions);
		
		// rows,columns,boxWidth,boxHeight,speed - this level will be loaded from synchronized file

		particles = new ParticleEngine();
		player1 = new Player(new Vec3(0.0f, 0.0f, 0.0f),25.0f,windowDimensions,dimensions,particles,level.getSpeed());
		// add the ships engines to the particle engine
		for (ParticleGenerator curEngine: player1.getEngines()) {
			particles.add(curEngine);
		}
		// dimensions,player1,level
		collider = new CollisionDetector(dimensions,player1,level);
		
//		windowDimensions.addObserver(hUD);
//		player1.getShip().addObserver(hUD);
		
		level.build(particles, hUD, windowDimensions,player1);
	}
	
	public boolean tick() {
		gameClock.tick();
		windowDimensions.getEye().setZ(windowDimensions.getEye().getZ()-(gameClock.getTimeSinceLastTick()*level.getSpeed()));
		windowDimensions.getCenter().setZ(windowDimensions.getCenter().getZ()-(gameClock.getTimeSinceLastTick()*level.getSpeed()));
		windowDimensions.setViewMatrix();
		background.tick(gameClock.getTimeSinceLastTick());
		level.tick(gameClock.getTimeSinceLastTick());
		player1.tick(gameClock.getTimeSinceLastTick());
		particles.tick(gameClock.getTimeSinceLastTick());
		collider.tick();
		return true;				
//				hUD.tick(gameClock.getTimeSinceLastTick());
	}
	
	/*
	 * returns a list of all the enemy Objects
	 */
	public List<InPlayObj> getEnemysToDisplay() {
		return level.getVisibleObjects();
	}
	
	/*
	 * returns a list of all the player Objects
	 */
	public List<InPlayObj> getPlayersToDisplay() {
		return player1.getObjects();
	}
	
	/*
	 * returns a list of all the particles
	 */
	public List<Particle> getParticlesToDisplay() {
		return particles.getParticles();
	}
	
	/*
	 * returns a list off all the Background Objects
	 */
	public List<GameObj> getBackgroundToDisplay() {
		return background.getObjects();
	}
	
	/*
	 * returns a list off all the HUD Objects
	 */
/*	public List<GameObj> getHUDObjects() {
		return hUD.getObjects();
	}	*/
	
	public void setPlayerMove(int direction) {
		player1.setNextMove(direction);
	}
}
	
