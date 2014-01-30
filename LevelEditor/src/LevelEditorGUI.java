import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import VMQ.Vec3;
import VMQ.WindowDimensions;

public class LevelEditorGUI extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static LevelEditorGUI instance = null;
	private Font buttonFont = new Font("Verdona", Font.BOLD,14);
	private ButtonListener buttonListener;
	private JOGLPanelListener joglPanelListener;
	private LevelHolder levelHolder;
	private static final int CANVAS_WIDTH = 600;  				// width of the drawable
	private static final int CANVAS_HEIGHT = 600;				// height of the drawable
	private WindowDimensions windowDimensions = new WindowDimensions(CANVAS_WIDTH,CANVAS_HEIGHT,45,1,100,new Vec3(0,80,0),new Vec3(0,0,0),new Vec3(0,0,1));

	public LevelEditorGUI () {
		super("LevelEditor");
		this.levelHolder = new LevelHolder(windowDimensions);
		this.buttonListener =  new ButtonListener(this);
		this.joglPanelListener = new JOGLPanelListener(this,levelHolder);
		Container contentPane = getContentPane();
		contentPane.removeAll();
		contentPane.add(mainPanel());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);				// Sets the FutoshikiGUI to exit when the window is closed
		setResizable(true);										// Disables resize
		setVisible(true);
		pack();														// Packs the frame and sets it in the center of the screen
		setLocationRelativeTo(null);
	}
	
	public static LevelEditorGUI getInstance() {
		if(instance == null) {
			instance = new LevelEditorGUI();
		}
		return instance;
	}
	
	public JPanel mainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(newEnemyPanel(), BorderLayout.EAST);
		mainPanel.add(currentEnemyPanel(), BorderLayout.WEST);
		mainPanel.add(joglWindow(), BorderLayout.CENTER);
		return mainPanel;
	}
	
	public GLJPanel joglWindow() {
		GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        GLJPanel joglWindow = new JOGLPanel(glcapabilities,joglPanelListener,levelHolder,windowDimensions);
        setSize(joglWindow,new Dimension(600,600));
        return joglWindow;
	}
	
	public JPanel currentEnemyPanel() {
		JPanel currentEnemyPanel = new JPanel();
		JLabel currentEnemyLabel = new JLabel("Enemy Type");
		JLabel currentEnemyMovements = new JLabel("Movements");
		
		currentEnemyPanel.setLayout(new BoxLayout(currentEnemyPanel,BoxLayout.PAGE_AXIS));
		
		currentEnemyPanel.add(currentEnemyLabel);
		currentEnemyPanel.add(currentEnemyMovements);
		setSize(currentEnemyPanel,new Dimension(200,600));
		return currentEnemyPanel;
	}
	
	public JPanel newEnemyPanel() {
		JPanel newEnemyPanel = new JPanel();

		JButton selectEnemy = new JButton("Select Enemy");
		selectEnemy.setActionCommand("SELECT");
		assignButton(selectEnemy);
		
		JButton newEnemy = new JButton("New Enemy");
		newEnemy.setActionCommand("NEW");
		assignButton(newEnemy);

		JButton newLevel = new JButton("Create New Level");
		newLevel.setActionCommand("CREATE LEVEL");
		assignButton(newLevel);
		
		JButton saveLevel = new JButton("Save Level");
		saveLevel.setActionCommand("SAVE LEVEL");
		assignButton(saveLevel);
		
		JButton loadLevel = new JButton("Load Level");
		loadLevel.setActionCommand("LOAD LEVEL");
		assignButton(loadLevel);
		
		JButton quit = new JButton("Quit");
		quit.setActionCommand("QUIT");
		assignButton(quit);
		
		newEnemyPanel.setLayout(new BoxLayout(newEnemyPanel,BoxLayout.PAGE_AXIS));
		
		String[] enemys = { "Alien1", "EnemyShip", "EnemyShip2", "WallGun","SwivleShip"};
		JComboBox enemyBox = new JComboBox(enemys);
		enemyBox.setSelectedIndex(4);
		
		newEnemyPanel.add(glueComponent(selectEnemy));
		
		newEnemyPanel.add(glueComponent(newEnemy));
//		newEnemyPanel.add(glueComponent(enemyBox));
		newEnemyPanel.add(glueComponent(newLevel));
		newEnemyPanel.add(glueComponent(saveLevel));
		newEnemyPanel.add(glueComponent(loadLevel));
		newEnemyPanel.add(glueComponent(quit));
		setSize(newEnemyPanel,new Dimension(200,600));
		return newEnemyPanel;
	}
	
	private void assignButton(JButton button) {
		button.setFocusable(false);
		button.setFont(buttonFont);
		button.addActionListener((ActionListener)buttonListener);
		button.addMouseListener((MouseListener)buttonListener);
	}

	private void setSize(Container c,Dimension size) {
		c.setMinimumSize(size);
		c.setMaximumSize(size);
		c.setPreferredSize(size);
	}
	
	private JPanel glueComponent(JComponent myComponent) {
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new BoxLayout(retPanel,BoxLayout.LINE_AXIS));
		retPanel.add(Box.createHorizontalGlue());
		retPanel.add(myComponent);
		retPanel.add(Box.createHorizontalGlue());
		return retPanel;
	}
	
	public void quit() {
		System.exit(0);
	}
}
