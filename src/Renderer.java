import com.jogamp.opengl.util.awt.TextRenderer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.awt.event.*;

public class Renderer extends GLCanvas implements GLEventListener {

    private static final float FOVY = 60.0f;
    private static final float ZNEAR = 0.1f;
    private static final float ZFAR = 100.0f;

    private int width = 800;
    private int height = 800;

    private float[] cam_pos = new float[]{0.0f, 0.0f, 8.0f};
    private float[] cam_rot = new float[]{30.0f, 135.0f, 0.0f};

    private boolean rotating, panning, scaling;
    private int newX, newY;

    private boolean showCoords = true;

    private TextRenderer textRenderer;
    private Cubes cubes;

    public Renderer() {
        setSize(width, height);
        addGLEventListener(this);
        addKeyListener(new RendererKeyAdapter());
        addMouseListener(new RendererMouseAdapter());
        addMouseMotionListener(new RendererMouseAdapter());
        addMouseWheelListener(new RendererMouseAdapter());
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL2.GL_DEPTH_TEST);

        textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 14));
        cubes = new Cubes(0.5f, gl);
        cubes.select(0, 0, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(cam_pos[0], cam_pos[1], cam_pos[2], cam_pos[0], cam_pos[1], 0, 0, 1, 0);
        gl.glRotatef(cam_rot[0], 1, 0, 0);
        gl.glRotatef(cam_rot[1], 0, 1, 0);
        gl.glRotatef(cam_rot[2], 0, 0, 1);

        drawCoordinateAxes(gl);

        cubes.draw();

        textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
        textRenderer.draw("posX: " + cam_pos[0], 5, drawable.getHeight() - 20);
        textRenderer.draw("posY: " + cam_pos[1], 5, drawable.getHeight() - 40);
        textRenderer.draw("posZ: " + cam_pos[2], 5, drawable.getHeight() - 60);
        textRenderer.draw("rotX: " + cam_rot[0], 5, drawable.getHeight() - 80);
        textRenderer.draw("rotY: " + cam_rot[1], 5, drawable.getHeight() - 100);
        textRenderer.draw("x: " + cubes.getX() + " y: " + cubes.getY() + " z: " + cubes.getZ(), 5, drawable.getHeight() - 120);
        textRenderer.endRendering();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);

        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
        }

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(FOVY, (float) width / (float) height, ZNEAR, ZFAR);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glViewport(0, 0, width, height);
    }

    private void drawCoordinateAxes(GL2 gl) {
        if (!showCoords)
            return;

        gl.glBegin(GL2.GL_LINES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(10, 0, 0);
        gl.glColor3f(0, 1, 0);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 10, 0);
        gl.glColor3f(0, 0, 1);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 0, 10);
        gl.glEnd();
    }

    private class RendererKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_C:
                    showCoords = !showCoords;
                    break;
                case KeyEvent.VK_LEFT:
                    cubes.select(Cubes.Direction.LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    cubes.select(Cubes.Direction.RIGHT);
                    break;
                case KeyEvent.VK_UP:
                    cubes.select(Cubes.Direction.UP);
                    break;
                case KeyEvent.VK_DOWN:
                    cubes.select(Cubes.Direction.DOWN);
                    break;
                case KeyEvent.VK_SPACE:
                    cubes.explode();
                    break;
            }
        }
    }

    private class RendererMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            newX = e.getX();
            newY = e.getY();

            rotating = false;
            panning = false;
            scaling = false;

            int bothMask = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
            if ((e.getModifiersEx() & bothMask) == bothMask)
                scaling = true;
            else if (e.getButton() == MouseEvent.BUTTON1)
                rotating = true;
            else if (e.getButton() == MouseEvent.BUTTON3)
                panning = true;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getWheelRotation() > 0 && cam_pos[2] < ZFAR - 0.1 * cam_pos[2])
                cam_pos[2] += 0.1 * cam_pos[2];
            else if (e.getWheelRotation() < 0 && cam_pos[2] > ZNEAR + 1)
                cam_pos[2] -= 0.1 * cam_pos[2];
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int oldX = newX;
            int oldY = newY;
            newX = e.getX();
            newY = e.getY();

            float rel_x = (newX - oldX) / (float) width;
            float rel_y = (newY - oldY) / (float) height;
            if (rotating) {
                cam_rot[0] += rel_y * 180;
                cam_rot[1] += rel_x * 180;
            } else if (panning) {
                cam_pos[0] -= rel_x * cam_pos[2];
                cam_pos[1] += rel_y * cam_pos[2];
            } else if (scaling)
                cam_pos[2] += rel_y * cam_pos[2];
        }
    }
}
