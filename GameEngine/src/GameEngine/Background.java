package GameEngine;

import java.util.LinkedList;

import VMQ.Vec3;

public class Background {

	private Wallpaper wallpaper;
	private StarMachine starMachine;
	private LinkedList<GameObj> backgroundObjects = new LinkedList<GameObj>();
	
	public Background(float wallpaperWidth,float wallpaperLength,Vec3 screenCenter) {
		this.wallpaper = new Wallpaper(new Vec3(0,-10,0),screenCenter,wallpaperLength,backgroundObjects); 
		this.starMachine = new StarMachine(new Vec3(wallpaperWidth,-5,-wallpaperLength),screenCenter,new Vec3(0,0,2),2,20,wallpaperLength*2,100,backgroundObjects);
	}
	
	public void tick(float timeSinceLastTick) {
		wallpaper.tick(timeSinceLastTick);
		starMachine.tick(timeSinceLastTick);
	}
	
	public LinkedList<GameObj> getObjects() {
		return backgroundObjects;
	}
}
