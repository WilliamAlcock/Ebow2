import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class JOGLPanelListener implements MouseListener,MouseWheelListener,KeyListener{

	private LevelEditorGUI levelEditorGUI;
	private LevelHolder levelHolder;
	private boolean[] keyBuffer = new boolean[256]; 
	
	public JOGLPanelListener(LevelEditorGUI levelEditorGUI,LevelHolder levelHolder) {
		this.levelEditorGUI = levelEditorGUI;
		this.levelHolder = levelHolder;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (keyBuffer[13]) {
			levelHolder.zoom((float)e.getWheelRotation()/10);
		} else {
			levelHolder.moveViewingSquare((float)e.getWheelRotation()/10);
		}
		levelEditorGUI.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyBuffer[e.getKeyCode()]=true;
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyBuffer[e.getKeyCode()]=false;
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
