import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;

import VMQ.WindowDimensions;

/**
 * A minimal program that draws with JOGL in a Swing JFrame using the AWT GLJPanel.
 *
 * @author Wade Walker
 */
public class JOGLPanel extends GLJPanel implements GLEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GLU glu;
	private WindowDimensions windowDimensions;
	private Display display;
	private LevelHolder levelHolder;
	
	public JOGLPanel(GLCapabilities glcapabilities,JOGLPanelListener listener,LevelHolder levelHolder,WindowDimensions windowDimensions) {
		super(glcapabilities);
		this.addGLEventListener(this);
		this.addMouseWheelListener(listener);
		this.addKeyListener(listener);
		this.setFocusable(true);
		this.levelHolder = levelHolder;
		this.windowDimensions = windowDimensions;
	}

    @Override
    public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {
        GL3 gl = drawable.getGL().getGL3();  					// get the OpenGL 4 graphics context
	    if (height == 0) height = 1;   							// prevent divide by zero
	    
	    System.out.println("Window dimensions set : "+width+","+height);
	    windowDimensions.setWidth(width);
	    windowDimensions.setHeight(height);
	    windowDimensions.notifyObservers(windowDimensions);
	    
	    // Set the view port (display area) to cover the entire window
	    gl.glViewport(0, 0, width, height);
	 
	    // Setup perspective projection, with aspect ratio matches viewport
	    glu.gluPerspective(windowDimensions.getFovyDeg(), windowDimensions.getAspect(), 0.1, 110.0); 	// fovy, aspect, zNear, zFar
    }
    
    @Override
    public void init( GLAutoDrawable drawable ) {
    	GL3 gl = drawable.getGL().getGL3();      				// get the OpenGL 2 graphics context
		System.out.println("GLSL Version: "+gl.glGetString(GL3.GL_SHADING_LANGUAGE_VERSION));
		glu = new GLU();                         				// get GL Utilities
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); 				// set background (clear) color
	    gl.glClearDepth(1.0f);      							// set clear depth value to farthest
    
	    gl.glEnable(GL3.GL_DEPTH_TEST); 								// enables depth testing
	    gl.glDepthFunc(GL3.GL_LEQUAL);  								// the type of depth test to do
        display = new Display(gl,windowDimensions);						// Create the Display renderer
        
    }		
    
    @Override
    public void dispose( GLAutoDrawable drawable ) {}
    
    @Override
    public void display( GLAutoDrawable drawable ) {
    	GL3 gl = drawable.getGL().getGL3();  							// get the OpenGL 2 graphics context
	    gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT); 	// clear color and depth buffers
    	display.tick(levelHolder.getEnemys(),levelHolder.getEditorObjects());
    	gl.glFlush();
    }
}
