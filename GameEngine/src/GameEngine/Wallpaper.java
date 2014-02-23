package GameEngine;
import VMQ.Vec3;
import java.util.LinkedList;

public class Wallpaper {

	private Vec3 screenCenter;
	private float length;
	private WallpaperPane bottomPane;
	private WallpaperPane topPane;
	
	public Wallpaper(Vec3 position,Vec3 screenCenter,float length,LinkedList<GameObj> backgroundObjects) {
		this.screenCenter = screenCenter;
		this.length = length;
		bottomPane = new WallpaperPane(position);		
		topPane = new WallpaperPane(new Vec3(position.getX(),position.getY(),position.getZ()-(length*2)));
		backgroundObjects.add(bottomPane);
		backgroundObjects.add(topPane);
	}
	
	public void tick(float timeSinceLastTick) {
		if ((screenCenter.getZ()+(length*2))<bottomPane.getPosition().getZ()) {
			bottomPane.getPosition().setZ(topPane.getPosition().getZ());			
			topPane.getPosition().setZ(bottomPane.getPosition().getZ()-(length*2));
		}
	}
}
