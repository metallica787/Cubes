import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL2;

public class Cubes {

    private static final int LENGTH = 6;

    private GL2 gl;
    private Cube[][][] cubes = new Cube[LENGTH][LENGTH][LENGTH];
    private float size;
    private int x, y, z;
    private Direction direction;

    private int[][][] bounds = new int[LENGTH][LENGTH][LENGTH];

    public Cubes(float size, GL2 gl) {
        this.size = size;
        this.gl = gl;
        GLUT glut = new GLUT();
        for (int x = 0; x < LENGTH; x++) {
            for (int y = 0; y < LENGTH; y++) {
                for (int z = 0; z < LENGTH; z++) {
                    cubes[x][y][z] = new Cube(size, gl, glut);
                    bounds[x][y][z] = LENGTH - 1;
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void draw() {
        for (int x = 0; x < LENGTH; x++) {
            for (int y = 0; y < LENGTH; y++) {
                for (int z = 0; z < LENGTH; z++) {
                    cubes[x][y][z].draw();
                    gl.glTranslatef(0, 0, size);
                }
                gl.glTranslatef(0, size, -LENGTH * size);
            }
            gl.glTranslatef(size, -LENGTH * size, 0);
        }
    }

    public void explode() {
        cubes[x][y][z].explode();
        // select next cube
//        select(direction);
    }

    public void select(int x, int y, int z) {
        cubes[x][y][z].setSelected(true);
        this.x = x;
        this.y = y;
        this.z = z;

        if (cubes[x][y][z].isDestroyed()) {
            switch (direction) {
                case LEFT:
                    selectLeft();
                    break;
                case RIGHT:
                    selectRight();
                    break;
                case UP:
                    selectUp();
                    break;
                case DOWN:
                    selectDown();
                    break;
            }
        }
    }

    public void select(Direction direction) {
        cubes[x][y][z].setSelected(false);
        this.direction = direction;

        switch (direction) {
            case LEFT:
                selectLeft();
                break;
            case RIGHT:
                selectRight();
                break;
            case UP:
                selectUp();
                break;
            case DOWN:
                selectDown();
                break;
        }
    }

    private void selectLeft() {
        if (x < 5 && z == 0)
            select(x + 1, y, z);
        else if (z < 5 && x == 5)
            select(x, y, z + 1);
        else if (x > 0)
            select(x - 1, y, z);
        else
            select(x, y, z - 1);
    }

    private void selectRight() {
        if (z < 5 && x == 0)
            select(x, y, z + 1);
        else if (x < 5 && z == 5)
            select(x + 1, y, z);
        else if (z > 0)
            select(x, y, z - 1);
        else
            select(x - 1, y, z);
    }

    private void selectUp() {
        if (y < bounds[x][y][z])
            select(x, y + 1, z);
        else
            select(x, y, z);
    }

    private void selectDown() {
        if (y > 0)
            select(x, y - 1, z);
        else
            select(x, y, z);
    }

    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
    }
}
