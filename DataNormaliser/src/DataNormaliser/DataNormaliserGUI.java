package DataNormaliser;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * Combines wavefront files together
 *
 */
public class DataNormaliserGUI extends JFrame implements Observer{
	
	private static final long serialVersionUID = 1L;
	
	private JLabel mainScreenTitleLabel = new JLabel("Ebow Data Normaliser");
	
	private JList<String> currentObjList = new JList<String>();
	private JScrollPane currentObjScrollPane = new JScrollPane(currentObjList);
	
	private JTextArea infoList = new JTextArea("");
	private JScrollPane infoScrollPane = new JScrollPane(infoList);
	
	private Font buttonFont = new Font("Verdona", Font.BOLD,14);
	
	private JButton addObj;
	private JButton removeObj;
	private JButton renameObj;
	private JButton save;
	private JButton load;
	private JButton refresh;
	private JButton quit;
	
	private JButton scaleObj;
	private JButton scaleOK;
	private JButton scaleCancel;
	private JTextField scaleXaxis;	
	private JTextField scaleYaxis;
	private JTextField scaleZaxis;
	private JLabel scaleXaxisLabel;
	private JLabel scaleYaxisLabel;
	private JLabel scaleZaxisLabel;
	
	private ButtonListener buttonListener;
	private DataHolder dataHolder;
	
	public static DataNormaliserGUI instance = null;
	
	private DataNormaliserGUI(DataHolder dataHolder) {
		super("Data Normaliser");
		this.dataHolder = dataHolder;
		buttonListener = new ButtonListener(this);
		dataHolder.addObserver(this);
		Container contentPane = getContentPane();
		contentPane.removeAll();
		contentPane.add(makeMainScreen());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);				// Sets the FutoshikiGUI to exit when the window is closed
		setResizable(false);										// Disables resize
		setVisible(true);
		pack();														// Packs the frame and sets it in the center of the screen
		setLocationRelativeTo(null);
	}
	
	public static DataNormaliserGUI getInstance() {
		if(instance == null) {
			instance = new DataNormaliserGUI(new DataHolder());
		}
		return instance;
	}
	
	private void setSize(Container c,Dimension size) {
		c.setMinimumSize(size);
		c.setMaximumSize(size);
		c.setPreferredSize(size);
	}
	
	private JPanel makeMainScreen() {
		JPanel mainScreen = new JPanel(new BorderLayout());
		mainScreen.add(makeButtonPanel(), BorderLayout.EAST);
		mainScreen.add(makeCurrentObjectPanel(),BorderLayout.WEST);
		mainScreen.add(makeListPanel(),BorderLayout.CENTER);
		mainScreen.add(makeInfoPanel(),BorderLayout.SOUTH);
		mainScreen.add(makeTitlePanel(),BorderLayout.NORTH);
		return mainScreen;
	}
	
	private JPanel makeCurrentObjectPanel() {
		JPanel currentObjectPanel = new JPanel();
		
		scaleObj = new JButton ("Scale Object");											// Creates the buttons and adds the listeners
		scaleObj.setActionCommand("SCALE");
		scaleObj.setFocusable(false);
		scaleObj.setFont(buttonFont);
		scaleObj.addActionListener((ActionListener)buttonListener);
		scaleObj.addMouseListener((MouseListener)buttonListener);
		
		scaleOK = new JButton ("OK");										// Creates the buttons and adds the listeners
		scaleOK.setActionCommand("SCALEOK");
		scaleOK.setFocusable(false);
		scaleOK.setFont(buttonFont);
		scaleOK.addActionListener((ActionListener)buttonListener);
		scaleOK.addMouseListener((MouseListener)buttonListener);
		
		scaleCancel = new JButton ("Cancel");								// Creates the buttons and adds the listeners
		scaleCancel.setActionCommand("SCALECANCEL");
		scaleCancel.setFocusable(false);
		scaleCancel.setFont(buttonFont);
		scaleCancel.addActionListener((ActionListener)buttonListener);
		scaleCancel.addMouseListener((MouseListener)buttonListener);
		
		scaleXaxis = new JTextField();
		scaleXaxis.setText("1.0");
		scaleXaxisLabel = new JLabel("X Axis");
		
		scaleYaxis = new JTextField();
		scaleYaxis.setText("1.0");
		scaleYaxisLabel = new JLabel("Y Axis");
		
		scaleZaxis = new JTextField();
		scaleZaxis.setText("1.0");
		scaleZaxisLabel = new JLabel("Z Axis");
		
		scaleCancel();
		
		JPanel topLine = new JPanel();														// Adds the buttons to the panel
		topLine.setLayout(new BoxLayout(topLine,BoxLayout.PAGE_AXIS));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(scaleObj));
		topLine.add(Box.createRigidArea(new Dimension(10,10)));
		topLine.add(glueComponent(new JComponent[] {scaleXaxisLabel,(JComponent) Box.createRigidArea(new Dimension(10,10)),scaleXaxis}));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(new JComponent[] {scaleYaxisLabel,(JComponent) Box.createRigidArea(new Dimension(10,10)),scaleYaxis}));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(new JComponent[] {scaleZaxisLabel,(JComponent) Box.createRigidArea(new Dimension(10,10)),scaleZaxis}));
		topLine.add(Box.createRigidArea(new Dimension(10,10)));
		topLine.add(glueComponent(new JComponent[] {scaleOK,(JComponent) Box.createRigidArea(new Dimension(10,10)),scaleCancel}));
		
		currentObjectPanel.add(topLine);
		currentObjectPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		setSize(currentObjectPanel,new Dimension(200,400));
		return currentObjectPanel;
	}
	
	private JPanel makeTitlePanel() {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel,BoxLayout.LINE_AXIS));
		mainScreenTitleLabel.setFont(new Font("Verdona", Font.BOLD,24));
		listPanel.add(glueComponent(mainScreenTitleLabel));
		listPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		return listPanel;
	}
	
	private JPanel makeListPanel() {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel,BoxLayout.PAGE_AXIS));
		
		JPanel bottomLine = new JPanel();																// Creates the scroll pane
		bottomLine.setLayout(new BoxLayout(bottomLine,BoxLayout.LINE_AXIS));						
		currentObjScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setSize(currentObjScrollPane,new Dimension(450,400));
		currentObjList.setFont(new Font("Verdona", Font.BOLD,12));
		currentObjList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		bottomLine.add(Box.createRigidArea(new Dimension(5,5)));
		bottomLine.add(currentObjScrollPane);
		bottomLine.add(Box.createRigidArea(new Dimension(5,5)));
		
		listPanel.add(bottomLine);
		listPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		return listPanel;
	}
	
	private JPanel makeInfoPanel() {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.PAGE_AXIS));
		
		JPanel bottomLine = new JPanel();																// Creates the scroll pane
		bottomLine.setLayout(new BoxLayout(bottomLine,BoxLayout.LINE_AXIS));
		infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		setSize(infoScrollPane,new Dimension(600,150));
		infoList.setEditable(false);
		infoList.setFont(new Font("Verdona", Font.BOLD,12));
		bottomLine.add(Box.createRigidArea(new Dimension(5,5)));
		bottomLine.add(infoScrollPane);
		bottomLine.add(Box.createRigidArea(new Dimension(5,5)));
		
		infoPanel.add(bottomLine);
		infoPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		return infoPanel;
	}
	
	private JPanel makeButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.PAGE_AXIS));
		
		addObj = new JButton ("Add Object");												// Creates the buttons and adds the listeners
		addObj.setActionCommand("ADD");
		addObj.setFocusable(false);
		addObj.setFont(buttonFont);
		addObj.addActionListener((ActionListener)buttonListener);
		addObj.addMouseListener((MouseListener)buttonListener);
		removeObj = new JButton ("Remove Object");
		removeObj.setActionCommand("REMOVE");
		removeObj.setFocusable(false);
		removeObj.setFont(buttonFont);
		removeObj.addActionListener((ActionListener)buttonListener);
		removeObj.addMouseListener((MouseListener)buttonListener);
		renameObj = new JButton ("Rename Object");
		renameObj.setActionCommand("RENAME");
		renameObj.setFocusable(false);
		renameObj.setFont(buttonFont);
		renameObj.addActionListener((ActionListener)buttonListener);
		renameObj.addMouseListener((MouseListener)buttonListener);
		save = new JButton ("Save List");
		save.setActionCommand("SAVE");
		save.setFocusable(false);
		save.setFont(buttonFont);
		save.addActionListener((ActionListener)buttonListener);
		save.addMouseListener((MouseListener)buttonListener);
		load = new JButton ("Load List");
		load.setActionCommand("LOAD");
		load.setFocusable(false);
		load.setFont(buttonFont);
		load.addActionListener((ActionListener)buttonListener);
		load.addMouseListener((MouseListener)buttonListener);
		refresh = new JButton("Refresh List");
		refresh.setActionCommand("REFRESH");
		refresh.setFocusable(false);
		refresh.setFont(buttonFont);
		refresh.addActionListener((ActionListener)buttonListener);
		refresh.addMouseListener((MouseListener)buttonListener);
		quit = new JButton ("Quit");
		quit.setActionCommand("QUIT");
		quit.setFocusable(false);
		quit.setFont(buttonFont);
		quit.addActionListener((ActionListener)buttonListener);
		quit.addMouseListener((MouseListener)buttonListener);
		
		JPanel topLine = new JPanel();														// Adds the buttons to the panel
		topLine.setLayout(new BoxLayout(topLine,BoxLayout.PAGE_AXIS));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(addObj));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(removeObj));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(renameObj));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(save));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(load));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(refresh));
		topLine.add(Box.createVerticalGlue());
		topLine.add(glueComponent(quit));
		topLine.add(Box.createVerticalGlue());
		
		buttonPanel.add(topLine);
		buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		setSize(buttonPanel,new Dimension(150,400));
		return buttonPanel;
	}
	
	private void glueComponent(JComponent myButton,JPanel myPanel) {
		myPanel.add(Box.createHorizontalGlue());
		myPanel.add(myButton);
		myPanel.add(Box.createHorizontalGlue());
	}
	
	private JPanel glueComponent(JComponent myButton) {
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new BoxLayout(retPanel,BoxLayout.LINE_AXIS));
		glueComponent(myButton,retPanel);
		return retPanel;
	}
	
	private JPanel glueComponent(JComponent[] myButtons) {
		JPanel retPanel = new JPanel();
		retPanel.setLayout(new BoxLayout(retPanel,BoxLayout.LINE_AXIS));
		for (JComponent curButton: myButtons) {
			glueComponent(curButton,retPanel);
		}
		return retPanel;
	}
	
	public void add() {
		JFileChooser fileChooser = new JFileChooser();														// Sets up the chooser with the .obj filter
		FileNameExtensionFilter filter = new FileNameExtensionFilter("OBJ - Wavefront Object File", "obj");
		fileChooser.setFileFilter(filter);
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			for (File file: fileChooser.getSelectedFiles()) {
				int dotPosition = file.getName().lastIndexOf(".");												// adds .obj file extension if it is not already present
				String extension = "";
				if (dotPosition != -1) {
				    extension = file.getName().substring(dotPosition);
				}
				if (extension.toUpperCase().equals(".OBJ")) {
					String objectName = JOptionPane.showInputDialog(this, "Enter Name For Object To Add From "+file.toString()+" :","New Object Name", JOptionPane.QUESTION_MESSAGE);
					if (objectName!=null) {
						dataHolder.add(file.toString(), objectName);
					}
				} else {
			    	JOptionPane.showMessageDialog(this, "Wrong File Extension, must be .OBJ","Wavefront Object File",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	public void remove() {
		List<String> objectNames = currentObjList.getSelectedValuesList();
		if (objectNames!=null) {
			for (String objectName: objectNames) {
				dataHolder.remove(objectName);
			}
		}
	}
	
	public void rename() {
		List<String> oldObjectNames = currentObjList.getSelectedValuesList();
		if (oldObjectNames!=null) {
			for (String oldObjectName: oldObjectNames) {
				String newObjectName = JOptionPane.showInputDialog(this, "Enter New Name For Object \""+oldObjectName+"\" :","Rename Object "+oldObjectName, JOptionPane.QUESTION_MESSAGE);
				if (newObjectName!=null) {
					dataHolder.rename(oldObjectName,newObjectName);
				}
			}
		}
	}
	
	public void save() {
		JFileChooser fileChooser = new JFileChooser();														// Sets up the chooser with the .egf filter
		FileNameExtensionFilter filter = new FileNameExtensionFilter("EGF - Ebow Graphics File", "egf");
		fileChooser.setFileFilter(filter);
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			int dotPosition = file.getName().lastIndexOf(".");												// adds .egf file extension if it is not already present
			String extension = "";
			if (dotPosition != -1) {
			    extension = file.getName().substring(dotPosition);
			} else {
				file = new File(file+".egf");
				extension = ".EGF";
			}
			if (extension.toUpperCase().equals(".EGF")) {
				int overwrite = JOptionPane.YES_OPTION;
				if (file.exists()) {																		// Shows a warning if the file already exists
					overwrite = JOptionPane.showConfirmDialog(this, "Are you sure you want to overwrite "+file,"File Already Exists",JOptionPane.YES_NO_OPTION);
				}
				if (overwrite==JOptionPane.YES_OPTION) {
					dataHolder.save(file,infoList.getText());
				}
			} else {
		    	JOptionPane.showMessageDialog(this, "Wrong File Extension, must be .EGF","Ebow Graphics File",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void load() {
		JFileChooser fileChooser = new JFileChooser();														// Sets up the chooser with the .egf filter
		FileNameExtensionFilter filter = new FileNameExtensionFilter("EGF - Ebow Graphics File", "egf");
		fileChooser.setFileFilter(filter);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			int dotPosition = file.getName().lastIndexOf(".");												// adds .egf file extension if it is not already present
			String extension = "";
			if (dotPosition != -1) {
			    extension = file.getName().substring(dotPosition);
			}
			if (extension.toUpperCase().equals(".EGF")) {	
				dataHolder.load(file);
			} else {
		    	JOptionPane.showMessageDialog(this, "Wrong File Extension, must be .EGF","Ebow Graphics File",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void refresh() {
		dataHolder.refreshFromFiles();
	}
	
	public void quit() {
		if (JOptionPane.showConfirmDialog(this, "Are you sure you want to quit","Quit",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
	public void scale() {
		scaleOK.setVisible(true);
		scaleCancel.setVisible(true);
		scaleXaxis.setVisible(true);
		scaleXaxisLabel.setVisible(true);
		scaleYaxis.setVisible(true);
		scaleYaxisLabel.setVisible(true);
		scaleZaxis.setVisible(true);
		scaleZaxisLabel.setVisible(true);
	}
	
	public void scaleCancel() {
		scaleOK.setVisible(false);
		scaleCancel.setVisible(false);
		scaleXaxis.setVisible(false);
		scaleXaxisLabel.setVisible(false);
		scaleYaxis.setVisible(false);
		scaleYaxisLabel.setVisible(false);
		scaleZaxis.setVisible(false);
		scaleZaxisLabel.setVisible(false);
	}
	
	private boolean isFloat(String text,float myFloat) {
		try {
			myFloat = Float.parseFloat(text);
		} catch (Exception ex) {
		    return false;
		}
		return true;
	}
	
/*	public void scaleOK() {
		float x = 0.0f;
		float y = 0.0f;
		float z = 0.0f;
		if (!isFloat(scaleXaxis.getText(),x)) {
	    	JOptionPane.showMessageDialog(this, "Value for X axis must be float","Ebow Graphics File",JOptionPane.ERROR_MESSAGE);
		} else if (!isFloat(scaleYaxis.getText(),y)) {
			JOptionPane.showMessageDialog(this, "Value for Y axis must be float","Ebow Graphics File",JOptionPane.ERROR_MESSAGE);
		} else if (!isFloat(scaleZaxis.getText(),z)) {
			JOptionPane.showMessageDialog(this, "Value for Z axis must be float","Ebow Graphics File",JOptionPane.ERROR_MESSAGE);
		} else {
			List<String> objectNames = currentObjList.getSelectedValuesList();
			if (objectNames!=null) {
				for (String curObjectName: objectNames) {
					dataHolder.scale(curObjectName,x,y,z);
				}
			}	
		}
	}			*/
	
	@Override
	public void update(Observable model, Object report) {
		Vector<String> listData = new Vector<String>(((DataHolder)model).getList());
		Collections.sort(listData);
		System.out.println(listData);
		currentObjList.setListData(listData);
		currentObjList.repaint();
		infoList.append((String)report+"\n");
		infoList.repaint();
	}
}
