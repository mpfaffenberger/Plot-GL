package pfaff.plot;

import java.awt.Font;
import java.nio.FloatBuffer;
import java.util.Random;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

public class Model {
    private int gludrawtype = GLU.GLU_FILL;
    private float[][] table;
    private boolean debug = false;
    private boolean points = false;
    private boolean redrawcanvas = false;
    public static int selection;
    TextRenderer renderer;

    public Model(int numberOfEntities) {
        Random random = new Random();
        int nrows = numberOfEntities;
        int ncol = 6; // x,y,z and r,g,b
        table = new float[nrows][ncol];
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncol-3; j++) {
                table[i][j] = (random.nextFloat() * 1000f) - 500.0f;
            }
            for (int j = 3; j < ncol; j++) {
                table[i][j] = random.nextFloat();
            }
        }
    }

    public Model(float[][] table) {
        this.table = table;
    }

    protected void resize(GL2 gl2) {

        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 24));
        GLU glu = new GLU();
        glu.gluPerspective(60, PlotGL.width / PlotGL.height, .001, 25);

        gl2.glViewport(0, 0, (int) PlotGL.width, (int) PlotGL.height);
        gl2.glEnable(GL.GL_DEPTH_TEST);
        gl2.glEnable(GL2.GL_POINT_SMOOTH);
        gl2.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl2.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_FASTEST);
        gl2.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_FASTEST);

        glu.gluLookAt(PlotGL.camx, PlotGL.camy, PlotGL.camz,
            PlotGL.camx + Math.cos(Math.toRadians(PlotGL.viewAngle)), PlotGL.camy,
            PlotGL.camz - Math.sin(Math.toRadians(PlotGL.viewAngle)), 0, 1, 0);
    }

    protected void setup(GL2 gl2) {
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 24));
        GLU glu = new GLU();
        glu.gluPerspective(60, PlotGL.width / PlotGL.height, .001, 25);
        gl2.glViewport(0, 0, (int) PlotGL.width, (int) PlotGL.height);
        gl2.glEnable(GL.GL_DEPTH_TEST);
        gl2.glEnable(GL2.GL_POINT_SMOOTH);
        gl2.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl2.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
        gl2.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        glu.gluLookAt(PlotGL.camx, PlotGL.camy, PlotGL.camz,
                PlotGL.camx + Math.cos(Math.toRadians(PlotGL.viewAngle)), PlotGL.camy,
                PlotGL.camz - Math.sin(Math.toRadians(PlotGL.viewAngle)), 0, 1, 0);
        GLUquadric gq = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(gq, gludrawtype);
        gl2.glNewList(1, GL2.GL_COMPILE);
        glu.gluSphere(gq, 0.01, 12, 12);
        gl2.glEndList();
        selection = -1;
    }

    protected void render(GL2 gl2, int width, int height, float rx, float ry, float rz, float x, float y, float z) {

        if (redrawcanvas) {
            setup(gl2);
            redrawcanvas = false;
        }

        gl2.glClearColor((float) PlotGL.bgColor.getValue() / 10, (float) PlotGL.bgColor.getValue() / 10, (float) PlotGL.bgColor.getValue() / 10, 0f);
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        GLU glu = new GLU();
        GLUquadric gq = glu.gluNewQuadric();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glPushMatrix();
        gl2.glTranslated(x, y, z);
        gl2.glRotatef(rx, 1f, 0f, 0f);
        gl2.glRotatef(ry, 0f, 1f, 0f);
        gl2.glRotatef(rz, 0f, 0f, 1f);
        glu.gluQuadricDrawStyle(gq, gludrawtype);
        gl2.glPointSize(PlotGL.sphereSize.getValue() + 0.0f);
        if (PlotGL.pointsMode)
            gl2.glBegin(gl2.GL_POINTS);
        for (int i = 0; i < table.length; i++) {
            drawModel(gl2, glu, i, gq);
        }
        if (PlotGL.pointsMode)
            gl2.glEnd();

        if (selection > 0) {
            gl2.glPushMatrix();
            drawCube(gl2, glu, selection, gq);
            gl2.glPopMatrix();
        }

        gl2.glPushMatrix();
        gl2.glTranslated(.5, 0, 0);
        gl2.glScalef((float) PlotGL.sphereSize.getValue() / 10, (float) PlotGL.sphereSize.getValue() / 10, (float) PlotGL.sphereSize.getValue() / 10);
        gl2.glPopMatrix();


        if (debug) {
            double[] modelview = new double[16];
            double[] projection = new double[16];
            int[] viewport = new int[4];
            gl2.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
            gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
            gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
            double[] wcoords = new double[3];

            FloatBuffer de = FloatBuffer.allocate(1);
            gl2.glReadPixels(Controller.currx - 10, (((int) PlotGL.glcanvas.getHeight()) - Controller.curry + 10), 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, de);
            float depth = de.get(0);

            glu.gluUnProject(Controller.currx - 10, (((int) PlotGL.glcanvas.getHeight()) - Controller.curry + 10), de.get(0), modelview, 0, projection, 0, viewport, 0, wcoords, 0);
            renderer.beginRendering((int) PlotGL.glcanvas.getWidth(), PlotGL.glcanvas.getHeight());
            renderer.draw("GL_MODELVIEW_MATRIX", 0, 100);
            renderer.draw((int) (modelview[0]) + " " + (int) (modelview[1]) + " " + (int) (modelview[2]) + " " + (int) (modelview[3]), 110, 75);
            renderer.draw((int) (modelview[4]) + " " + (int) (modelview[5]) + " " + (int) (modelview[6]) + " " + (int) (modelview[7]), 110, 50);
            renderer.draw((int) (modelview[8]) + " " + (int) (modelview[9]) + " " + (int) (modelview[10]) + " " + (int) (modelview[11]), 110, 25);
            renderer.draw((int) (modelview[12]) + " " + (int) (modelview[13]) + " " + (int) (modelview[14]) + " " + (int) (modelview[15]), 110, 0);
            renderer.draw("GL_PROJECTION_MATRIX", 0, 225);
            renderer.draw((int) (100 * projection[0]) + " " + (int) (100 * projection[1]) + " " + (int) (100 * projection[2]) + " " + (int) (100 * projection[3]), 110, 200);
            renderer.draw((int) (100 * projection[4]) + " " + (int) (100 * projection[5]) + " " + (int) (100 * projection[6]) + " " + (int) (100 * projection[7]), 110, 175);
            renderer.draw((int) (100 * projection[8]) + " " + (int) (100 * projection[9]) + " " + (int) (100 * projection[10]) + " " + (int) (100 * projection[11]), 110, 150);
            renderer.draw((int) (projection[12]) + " " + (int) (projection[13]) + " " + (int) (projection[14]) + " " + (int) (projection[15]), 110, 125);
            renderer.draw("Mouse Y: " + (((int) PlotGL.height) - Controller.curry), 350, 100);
            renderer.draw("Mouse X: " + Controller.currx, 350, 75);
            renderer.draw("GL_DEPTH_COMPONENT: " + depth, 500, 5);
            renderer.draw("World X: " + wcoords[2] * 100, 525, 100);
            renderer.draw("World Y: " + wcoords[1] * 100, 525, 75);
            renderer.draw("World Z: " + wcoords[0] * 100, 525, 50);
            renderer.endRendering();
            glu.gluQuadricDrawStyle(gq, GLU.GLU_SILHOUETTE);
            gl2.glPushMatrix();
            gl2.glTranslated(wcoords[0], wcoords[1], wcoords[2]);
            glu.gluSphere(gq, .01, 15, 15);
            gl2.glPopMatrix();
        }
        if (Controller.selection == 1) {
            double[] modelview = new double[16];
            double[] projection = new double[16];
            int[] viewport = new int[4];
            gl2.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
            gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
            gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
            double[] wcoords = new double[3];

            FloatBuffer de = FloatBuffer.allocate(1);
            gl2.glReadPixels(Controller.currx, (((int) PlotGL.height - 40) - Controller.curry), 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, de);
            glu.gluUnProject(Controller.currx, (((int) PlotGL.height - 40) - Controller.curry), de.get(0), modelview, 0, projection, 0, viewport, 0, wcoords, 0);

            glu.gluQuadricDrawStyle(gq, GLU.GLU_SILHOUETTE);
            gl2.glPushMatrix();
            gl2.glTranslated(wcoords[0], wcoords[1], wcoords[2]);
            glu.gluSphere(gq, .01, 15, 15);
            gl2.glPopMatrix();
            wcoords[0] = wcoords[0] + .5;
            wcoords[0] = wcoords[0] * 100;
            wcoords[1] = wcoords[1] * 100;
            wcoords[2] = wcoords[2] * 100;
            float[] wc = new float[3];
            wc[0] = (float) wcoords[0];
            wc[1] = (float) wcoords[1];
            wc[2] = (float) wcoords[2];

            float[] rgb = new float[3];
            Controller.noScroll = 0;
            Controller.selection = 0;
            double distance = 5000000;
            int index = -1;
            for (int i = 0; i < table.length; i++) {
                double euclidean = euclideanDistance3(wcoords[0], wcoords[1], wcoords[2], table[i][0], table[i][1], table[i][2]);
                if (distance > euclidean) {
                    distance = euclidean;
                    rgb[0] = table[i][3];
                    rgb[1] = table[i][4];
                    rgb[2] = table[i][5];
                    index = i;
                }
            }
            selection = index;
        }
        gl2.glPopMatrix();
    }

    protected void drawModel(GL2 gl2, GLU glu, int i, GLUquadric gq) {
        gl2.glPushMatrix();
        gl2.glColor3f((float) table[i][3], (float) table[i][4], (float) table[i][5]);
        gl2.glTranslatef((float) (((float) table[i][0] / 100) - .5), (float) table[i][1] / 100, (float) table[i][2] / 100);
        gl2.glScalef((float) PlotGL.sphereSize.getValue() / 10, (float) PlotGL.sphereSize.getValue() / 10, (float) PlotGL.sphereSize.getValue() / 10);
        if (PlotGL.pointsMode) {
            gl2.glVertex3f((table[i][0] / 100) - .5f, table[i][1] / 100, table[i][2] / 100);
        } else {
            gl2.glCallList(1);
        }
        gl2.glPopMatrix();
    }

    protected void drawCube(GL2 gl2, GLU glu, int i, GLUquadric gq) {
        GLUT glut = new GLUT();
        gl2.glColor4f(.8f, .8f, 1f, .90f);
        gl2.glTranslatef((float) (((float) table[i][0] / 100) - .5), (float) table[i][1] / 100, (float) table[i][2] / 100);
        gl2.glScalef((float) PlotGL.sphereSize.getValue() / 5, (float) PlotGL.sphereSize.getValue() / 5, (float) PlotGL.sphereSize.getValue() / 5);
        glut.glutWireCube(0.02f);
    }

    private double euclideanDistance3(double l, double a, double b, double x, double y, double z) {
        double result = 0;
        double tempx = Math.pow((l - x), 2);
        double tempy = Math.pow((a - y), 2);
        double tempz = Math.pow((b - z), 2);
        double temp = tempx + tempy + tempz;
        result = Math.sqrt(temp);
        return result;
    }

    public boolean isPoints() {
        return points;
    }

    public void setPoints(boolean points) {
        this.points = points;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getGludrawtype() {
        return gludrawtype;
    }

    public void setGludrawtype(int gludrawtype) {
        this.gludrawtype = gludrawtype;
    }

    public boolean getRedrawcanvas() {
        return redrawcanvas;
    }

    public void setRedrawcanvas(boolean redrawcanvas) {
        this.redrawcanvas = redrawcanvas;
    }
}


