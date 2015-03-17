package pfaff.plot;

import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.BorderUIResource;

import com.jogamp.opengl.util.FPSAnimator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("deprecation")
public class PlotGL {

    public static double camx = 2;
    public static double camy = 0;
    public static double camz = 0;
    public static double viewAngle = 180;
    public static double width = 900;
    public static double height = 600;
    public static boolean pointsMode = false;
    public static JSlider bgColor;

    public static JButton gearbutton;
    public static JButton itemPicker;

    public static TranslucentPopupMenu menu;
    public static JMenuItem settings = new JMenuItem("Settings");
    public static JMenuItem resetview = new JMenuItem("Reset View");
    public static GLJPanel glcanvas;
    static Font f2 = new Font("calibri", Font.BOLD, 24);
    static Color transparent = new Color(0f, 0f, .2f, .75f);
    static JLabel bgtext = new JLabel("Background Color: 10");

    static JCheckBox debug = new JCheckBox("Debug");
    static JCheckBox points = new JCheckBox("Points");

    static JCheckBox showall = new JCheckBox("Display Entire Model");
    static JCheckBox wireframe = new JCheckBox("Wireframe");
    static JLabel aboutpanel = new JLabel();
    static JSlider sphereSize = new JSlider();
    static JLabel sphsi = new JLabel("Sphere Size: 10");
    public static JPanel settingspanel = new JPanel();
    public static JPanel storyPanel = new JPanel();
    public static JFrame jframe;
    static JButton resetrestrictions = new JButton();
    static Model model;
    static JButton aboutclose = new JButton();
    public static String[] sArray;
    public static JTextArea story = new JTextArea();

    static {
        GLProfile.initSingleton();
    }

    public static void createPlot(float[][] table, String[] storiesArray) {
        int ssx = (int) (.6 * width);
        int ssy = (int) (.6 * height);
        sArray = storiesArray;
        Font font = new Font("calibri", Font.BOLD, 18);
        settings.setFont(font);
        resetview.setFont(font);
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        glcanvas = new GLJPanel();
        final Controller cc = new Controller(table);
        final JPanel baseBallCard = new JPanel();
        model = cc.ot;
        glcanvas.addMouseListener(cc);
        glcanvas.addGLEventListener(cc);
        glcanvas.addKeyListener(cc);
        itemPicker = new JButton();
        gearbutton = new JButton();
        glcanvas.addMouseWheelListener(cc);
        glcanvas.addMouseMotionListener(cc);
        FPSAnimator animator = new FPSAnimator(glcanvas, 60);
        animator.start();
        settingspanel.setLayout(null);
        storyPanel.setLayout(null);

        bgColor = new JSlider();
        bgColor.setValue(0);
        bgColor.setMinimum(0);
        bgColor.setMaximum(10);
        bgColor.setBackground(new Color(.8f, .8f, 1f, 0f));
        settingspanel.add(bgColor);

        bgColor.setSize((int) (.6 * ssx), (int) (.2 * ssy));
        bgColor.setLocation((int) (.2 * ssx), (int) (.2 * ssy));
        jframe = new JFrame("Pfaff Plot-GL");
        final JButton close = new JButton();
        close.setSize(16, 16);
        close.setBackground((new Color(.8f, .8f, 1f, 0f)));
        close.setIcon(new ImageIcon("x.png"));
        settingspanel.add(close);
        final JButton closestory = new JButton();
        closestory.setSize(16, 16);
        closestory.setBackground((new Color(.8f, .8f, 1f, .75f)));
        story.setBackground((new Color(.8f, .8f, 1f, .75f)));
        story.setFont(font);
        storyPanel.setVisible(false);
        closestory.setIcon(new ImageIcon("x.png"));
        storyPanel.add(closestory);
        closestory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                storyPanel.setVisible(false);
            }
        });
        wireframe.setFont(font);
        wireframe.setSize(200, 35);
        wireframe.setBackground(new Color(0, 0, 0, 0));
        settingspanel.add(debug);
        settingspanel.add(points);
        bgtext.setSize(200, 20);
        bgtext.setFont(font);


        //SlideListe
        debug.setBackground(new Color(0, 0, 0, 0));
        points.setBackground(new Color(0, 0, 0, 0));

        showall.setFont(font);
        debug.setFont(font);
        points.setFont(font);
        showall.setSize(200, 35);
        debug.setSize(200, 35);
        points.setSize(200, 35);

        wireframe.setFont(font);
        wireframe.setSize(200, 35);
        wireframe.setBackground(new Color(0, 0, 0, 0));

        settingspanel.add(wireframe);

        sphereSize.setValue(15);
        sphereSize.setMinimum(1);
        sphereSize.setMaximum(25);
        sphereSize.setBackground(new Color(0, 0, 0, 0));
        settingspanel.add(sphereSize);
        sphsi.setBackground(new Color(0, 0, 0, 0));
        sphsi.setFont(font);
        sphsi.setSize(200, 20);

        settingspanel.add(sphsi);
        settingspanel.add(bgtext);

        storyPanel.add(story);
        glcanvas.add(storyPanel);
        aboutpanel.setBackground(new Color(.8f, .8f, 1f, .75f));
        glcanvas.add(aboutpanel);

        jframe.getContentPane().add(glcanvas, BorderLayout.CENTER);
        jframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                jframe.dispose();
                System.exit(0);
            }
        });
        jframe.addComponentListener(new ComponentListener() {

            @Override
            public void componentHidden(ComponentEvent arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void componentResized(ComponentEvent arg0) {
                // TODO Auto-generated method stub
                width = jframe.getSize().getWidth();
                height = jframe.getSize().getHeight();
                baseBallCard.setLocation(0, (int) height - 175);
                gearbutton.setLocation((int) width - 79, (int) height - 99);
                settingspanel.setSize(new Dimension((int) (.6 * width), (int) (.6 * height)));
                settingspanel.setLocation((int) (.2 * width), (int) (.15 * height));
                int sx = (int) (.6 * width);
                int sy = (int) (.6 * height);
                storyPanel.setSize(new Dimension((int) (.6 * width), (int) (.6 * height)));
                storyPanel.setLocation((int) (.2 * width), (int) (.15 * height));
                storyPanel.setBackground(new Color(.8f, .8f, 1f, .75f));
                story.setSize(new Dimension(storyPanel.getWidth()-50, storyPanel.getHeight()-50));
                story.setBackground(new Color(.8f, .8f, 1f, .75f));
                story.setLocation(25,25);
                sphereSize.setSize((int) (.3 * sx), 20);
                bgColor.setSize((int) (.3 * sx), 20);
                sphereSize.setLocation((int) (.1 * sx), (int) (.25 * sy));
                bgColor.setLocation((int) (.1 * sx), (int) (.35 * sy));
                bgtext.setLocation((int) (.5 * sx), (int) (.35 * sy));
                sphsi.setLocation((int) (.5 * sx), (int) (.25 * sy));
                showall.setLocation((int) (.5 * sx), (int) (.8 * sy));
                points.setLocation((int) (.175 * sx), (int) (.7 * sy));
                debug.setLocation((int) (.175 * sx), (int) (.8 * sy));
                wireframe.setLocation((int) (.175 * sx), (int) (.9 * sy));
                close.setLocation(sx - 30, sy - (sy - 10));
                aboutpanel.setSize(new Dimension(461, 347));
                aboutpanel.setLocation((int) (.3 * width), (int) (.3 * height));
                aboutpanel.add(aboutclose);
                aboutclose.setLocation(440, 10);
                resetrestrictions.setLocation((int) (.025 * sx), 25);
                glcanvas.requestFocusInWindow();
            }

            @Override
            public void componentShown(ComponentEvent arg0) {
                // TODO Auto-generated method stub

            }

        });

        jframe.setSize(new Dimension((int) width, (int) height));


        itemPicker.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Controller.noScroll = 1;
                glcanvas.setCursor(new Cursor(Cursor.HAND_CURSOR));
                glcanvas.requestFocusInWindow();

            }

        });
        gearbutton.setSize(64, 64);
        gearbutton.setIcon(new ImageIcon("Gear-64.png"));
        baseBallCard.setSize(64, 64);
        gearbutton.setBackground(new Color(.8f, .8f, 1f, .75f));
        glcanvas.setLayout(null);
        menu = new TranslucentPopupMenu();
        menu.add(resetview);
        menu.add(settings);
        settings.setBackground(transparent);
        resetview.setBackground(transparent);

        // menu.setOpaque(true);
        menu.setBorderPainted(false);
        menu.setOpaque(true);
        menu.setBorder(BorderFactory.createEmptyBorder());
        baseBallCard.setLayout(null);
        itemPicker.setLocation(0, 0);
        itemPicker.setSize(64, 64);
        baseBallCard.add(itemPicker);
        itemPicker.setIcon(new ImageIcon("eye.png"));
        JLabel clabel = new JLabel("Color");


        settingspanel.setVisible(false);
        settingspanel.setBackground(new Color(.8f, .8f, 1f, .75f));
        baseBallCard.setBackground(new Color(.8f, .8f, 1f, .75f));

        JLabel settingLabel = new JLabel("Settings");
        settingLabel.setFont(f2);
        settingLabel.setSize(150, 25);
        settingLabel.setLocation(25, 25);
        settingspanel.add(settingLabel);

        glcanvas.add(settingspanel);
        baseBallCard.add(clabel);
        glcanvas.add(baseBallCard);
        glcanvas.add(gearbutton);
        aboutclose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                aboutpanel.setVisible(false);
                glcanvas.requestFocusInWindow();
            }

        });
        gearbutton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                menu.show(glcanvas, (int) width - 149, (int) height - 160);
                glcanvas.requestFocusInWindow();

            }

        });

        settings.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                settingspanel.setVisible(true);
                glcanvas.requestFocusInWindow();

            }

        });
        close.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                settingspanel.setVisible(false);
                glcanvas.requestFocusInWindow();

            }

        });

        resetview.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                cc.tx = 0;
                cc.ty = 0;
                cc.tz = 0;
                cc.rotationx = 0;
                cc.rotationy = 0;
                cc.rotationz = 0;
                cc.notches = 0;
                camx = 2;
                camy = 0;
                camz = 0;
                viewAngle = 180;
                glcanvas.requestFocusInWindow();
            }

        });
        bgColor.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                bgtext.setText("Background Color: " + bgColor.getValue());
                glcanvas.requestFocusInWindow();

            }

        });
        points.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                pointsMode = points.isSelected();
                glcanvas.requestFocusInWindow();
            }
        });
        debug.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                model.setDebug(debug.isSelected());
                glcanvas.requestFocusInWindow();
            }
        });

        wireframe.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (wireframe.isSelected())
                    model.setGludrawtype(GLU.GLU_SILHOUETTE);
                else model.setGludrawtype(GLU.GLU_FILL);
                model.setRedrawcanvas(true);
                glcanvas.requestFocusInWindow();
            }

        });

        sphereSize.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                sphsi.setText("Sphere Size: " + sphereSize.getValue());
                glcanvas.requestFocusInWindow();
            }

        });
        aboutpanel.setVisible(false);
        jframe.setVisible(true);

        glcanvas.requestFocusInWindow();
    }
}

class TranslucentPopupMenu extends JPopupMenu {
    private static final Color ALPHA_ZERO = new Color(0, true);
    private static final Color POPUP_BACK = new Color(.8f, .8f, 1f, .6f);
    private static final Color POPUP_LEFT = new Color(.8f, .8f, 1f, .6f);
    private static final int LEFT_WIDTH = 24;

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        boolean isNimbus = UIManager.getBorder("PopupMenu.border") == null;
        if (isNimbus) {
            setBorder(new BorderUIResource(BorderFactory.createEmptyBorder()));
        }
    }

    @Override
    public JMenuItem add(JMenuItem menuItem) {
        menuItem.setOpaque(false);
        return super.add(menuItem);
    }

    @Override
    public void show(Component c, int x, int y) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            }
        });
        super.show(c, x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(POPUP_LEFT);
        g2.fillRect(0, 0, LEFT_WIDTH, getHeight());
        g2.setPaint(POPUP_BACK);
        g2.fillRect(LEFT_WIDTH, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
