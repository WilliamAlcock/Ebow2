import VMQ.Vec3;
import GameEngine.GameObj;

public abstract class LevelEditorObjects extends GameObj{

	public LevelEditorObjects(Vec3 position) {
		super(position);
	}

	@Override
	public DISPLAYTYPE getDisplayType() {
		return DISPLAYTYPE.SOLID;
	}
}
