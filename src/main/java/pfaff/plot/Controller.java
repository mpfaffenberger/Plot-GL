package pfaff.plot;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class Controller implements GLEventListener, KeyListener, MouseWheelListener, MouseMotionListener, java.awt.event.MouseListener {
    float tx = 0.0f;
    float ty = 0.0f;
    float tz = 0.0f;
    float lastX;
    float lastY;
    public float rotationx = 0;
    public float rotationy = 0;
    public float rotationz = 0;
    public float notches = 0;
    public static int mouseButton;
    public static int selection = 0;
    int v = 10;
    int c = 10;
    int h = 15;
    public static int currx;
    boolean zoom = false;
    public static int curry;
    public static int noScroll = 0;
    public Model ot;

    public Controller() {
        ot = new Model(10000000);
    }

    public Controller(float[][] table) {
        ot = new Model(table);
    }

    public Controller(Model oneT) {
        this.ot = oneT;
    }

    @Override
    public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
        ot.resize(glautodrawable.getGL().getGL2());
    }

    @Override
    public void init(GLAutoDrawable glautodrawable) {
        ot.setup(glautodrawable.getGL().getGL2());
    }

    @Override
    public void dispose(GLAutoDrawable glautodrawable) {
    }

    @Override
    public void display(GLAutoDrawable glautodrawable) {
        ot.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight(), rotationx, rotationy, rotationz, notches, ty, tz);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
            zoom = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
        if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
            zoom = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        notches -= ((float) e.getWheelRotation() * .1);
    }

    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {

        if (noScroll != 1)
            if (mouseButton == 1 && !zoom) {
                ty -= (e.getY() - lastY) / 500;
                tz -= (e.getX() - lastX) / 500;
            } else if ((zoom && mouseButton == 1)) {
                rotationy += (e.getX() - lastX) / 5;
                rotationz -= (e.getY() - lastY) / 5;
            } else {
                rotationy += (e.getX() - lastX) / 5;
                rotationz -= (e.getY() - lastY) / 5;
            }

        lastY = e.getY();
        lastX = e.getX();
    }


    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
        currx = e.getX();
        curry = e.getY();

    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub

        lastX = e.getX();
        lastY = e.getY();
        mouseButton = e.getButton();

        if (noScroll == 1) selection = 1;
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
        lastX = e.getX();
        lastY = e.getY();
        mouseButton = e.getButton();
        if (noScroll == 1) selection = 1;
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
    }
}
