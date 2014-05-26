import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL2;

public class Cube {

    private GL2 gl;
    private GLUT glut;
    private float size;
    private float scale = size;
    private boolean up;

    private float rate = 0.0f;

    private boolean isSelected;
    private boolean isExploding;
    private boolean isDestroyed;

    public Cube(float size, GL2 gl, GLUT glut) {
        this.size = size;
        this.gl = gl;
        this.glut = glut;
    }

    public boolean isDestroyed() {
        return isDestroyed || isExploding;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void explode() {
        isExploding = true;
    }

    private void pulse() {
        if (up)
            scale += 0.01;
        else
            scale -= 0.01;

        if (scale <= 0.2)
            up = true;
        if (scale >= size)
            up = false;
    }

    public void draw() {
        if (!isDestroyed) {
            if (isExploding) {
                gl.glColor3f(1, 0, 0);

                gl.glTranslatef(rate, rate, rate);
                gl.glTranslatef(-size / 4, -size / 4, -size / 4);
                glut.glutSolidCube(size / 2);
                gl.glTranslatef(-rate, -rate, -rate);

                gl.glTranslatef(-rate, rate, rate);
                gl.glTranslatef(size / 2, 0, 0);
                glut.glutSolidCube(size / 2);
                gl.glTranslatef(rate, -rate, -rate);

                gl.glTranslatef(rate, rate, -rate);
                gl.glTranslatef(-size / 2, 0, size / 2);
                glut.glutSolidCube(size / 2);
                gl.glTranslatef(-rate, -rate, rate);

                gl.glTranslatef(-rate, rate, -rate);
                gl.glTranslatef(size / 2, 0, 0);
                glut.glutSolidCube(size / 2);
                gl.glTranslatef(rate, -rate, rate);

                gl.glTranslatef(rate, -rate, rate);
                gl.glTranslatef(-size / 2, size / 2, -size / 2);
                glut.glutSolidCube(size / 2);
                gl.glTranslatef(-rate, rate, -rate);

                gl.glTranslatef(-rate, -rate, rate);
                gl.glTranslatef(size / 2, 0, 0);
                glut.glutSolidCube(size / 2);
                gl.glTranslatef(rate, rate, -rate);

                gl.glTranslatef(rate, -rate, -rate);
                gl.glTranslatef(-size / 2, 0, size / 2);
                glut.glutSolidCube(size / 2);
                gl.glTranslatef(-rate, rate, rate);

                gl.glTranslatef(-rate, -rate, -rate);
                gl.glTranslatef(size / 2, 0, 0);
                glut.glutSolidCube(size / 2);
                gl.glTranslatef(rate, rate, rate);

                gl.glTranslatef(-size / 4, -size / 4, -size / 4);

                rate -= 0.1;
                if (rate < -20)
                    isDestroyed = true;
            } else if (isSelected) {
                gl.glColor3f(1, 0, 0);
                glut.glutSolidCube(scale);
                gl.glColor3f(0.2f, 0.2f, 0.2f);
                glut.glutWireCube(scale);
                pulse();
            } else {
                scale = size;
                gl.glColor3f(0.4f, 0.4f, 0.4f);
                glut.glutSolidCube(size);
                gl.glColor3f(0.2f, 0.2f, 0.2f);
                glut.glutWireCube(size);
            }
        }
    }
}
