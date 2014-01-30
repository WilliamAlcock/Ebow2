import VMQ.Quaternion;
import VMQ.Vec3;


public class ViewingSquare extends LevelEditorObjects{

	public ViewingSquare(Vec3 position) {
		super(position);
//		this.setRotation(new Quaternion(0.0f,0.0f,0.70710677f,0.70710677f));
		this.getScale().setX(2.0f);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getType() {
		return "ViewingSquare";
	}
}
