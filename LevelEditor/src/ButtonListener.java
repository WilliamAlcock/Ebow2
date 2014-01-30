import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;


public class ButtonListener implements ActionListener,MouseListener{

	private LevelEditorGUI levelEditor;
	
	public ButtonListener(LevelEditorGUI levelEditor) {
		this.levelEditor = levelEditor;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() instanceof JButton) {
			((JButton)e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() instanceof JButton) {
			((JButton)e.getSource()).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
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
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton tempButton = (JButton)e.getSource();
			if (tempButton.getActionCommand().equals("SELECT")) {

			} else if (tempButton.getActionCommand().equals("NEW")) { 

			} else if (tempButton.getActionCommand().equals("CREATE LEVEL")) {

			} else if (tempButton.getActionCommand().equals("SAVE LEVEL")) {

			} else if (tempButton.getActionCommand().equals("LOAD LEVEL")) {

			} else if (tempButton.getActionCommand().equals("QUIT")) {
				levelEditor.quit();
			}
			
		}
	}
}
