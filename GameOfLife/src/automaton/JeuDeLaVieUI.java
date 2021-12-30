package automaton;

import visitors.Visitor;
import visitors.VisitorClassic;
import visitors.VisitorDayNight;
import visitors.VisitorHighLife;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Class that represents the Graphic User Interface of the JeuDeLaVie.
 * @param game an instance of JeuDeLaVie to which the GUI will be attached.
 * @param layout represents layout of the main panel.
 * @param mainpanel represents the main JPanel to which other smaller panels are attached/detached.
 * @param simPanel is the central panel that shows the graphical representation of Game of Life.
 * @param setupPanel is the panel that contains settings.
 * @param contentPanel is the panel that contains simPanel and other smaller panels.
 * @param sleep is the value of pauses between each generation.
 * @param cellSize is the size of a single cell in pixels. Needed to implement zoom.
 * @param pause boolean that allows to put the game on pause.
 * @param setup boolean that allows to check if we are in the settings menu.
 * @param zoom represents zoom value.
 * @param pauseHandler is a thread that stops the execution of the main loop when the game is on pause.
 * @param currentVisitor as the name suggests - is the current visitor. This variable allows to change the rules of the game without restarting it.
 * @param paintColor is the color of cells in the SimPanel. It can be altered by ColorChooser.
 */
public class JeuDeLaVieUI extends JFrame implements Observer{
    private final JeuDeLaVie game;
    private final CardLayout layout;
    private JPanel mainPanel, simPanel, setupPanel, contentPanel;
    private int sleep = 150,cellSize;
    private boolean pause=true,setup=true;
    private double zoom;
    private Thread pauseHandler;
    private Visitor currentVisitor;
    private Color paintColor;

    /**
     * Constructor for GUI.
     * @param x is an instance of JeuDeLaVie on which the GUI is based.
     */
    public JeuDeLaVieUI(JeuDeLaVie x){

        // Frame setup
        setResizable(false);
        setTitle("Conway's Game Of Life");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600,600);
        setLocationRelativeTo(null);
        setVisible(true);

        game=x;
        zoom=1;

        // Setting up main panel
        layout = new CardLayout();
        mainPanel = new JPanel();
        mainPanel.setLayout(layout);
        add(mainPanel);

        // Setting up setup panel
        setupPanel = new SetupGrid(game,this);
        setupPanel.setName("setup");
        mainPanel.add(setupPanel.getName(),setupPanel);
        layout.show(mainPanel,setupPanel.getName());

        launchSimulation();

    }

    /**
     * Method that resets game settings when user goes back to the main menu.
     */
    public void resetGame(){
        game.resetGrid();
        setSpeed(150);

    }

    /**
     * Setter for color.
      * @param x is the color that will be used in paint method. Always originates from ColorChooser.
     */
    public void setColor(Color x){ paintColor=x; }

    /**
     * Getter for sleep value.
     * @return sleep value.
     */
    public int getSpeed(){ return sleep; }

    /**
     * Setter for currentVisitor. Needed to change the visitor without restarting the game.
     * Visitor's rules will be applied the next generation.
     * @param currentVisitor the visitor that will be affected.
     */
    public void setCurrentVisitor(Visitor currentVisitor) { this.currentVisitor = currentVisitor; }

    /**
     * Setter for pause.
     * @param value boolean to put or remove pause.
     */
    public void setPause(boolean value) { pause=value; }

    /**
     * Resets cellSize to their original size depending on the screen size.
     */
    public void resetCellSize(){ cellSize=400/game.getXmax(); }

    /**
     * Getter for zoom value.
     * @return zoom value.
     */
    public double getZoom(){ return zoom; }

    /**
     * Setter for zoom value.
     * @param value value that will be set.
     */
    public void setZoom(double value){ zoom=value; }

    /**
     * Sets custom cellSize. Debugging tool.
     * @param value new cellSize.
     */
    public void setCellSize(int value){ cellSize=value; }

    /**
     * Getter for cellSize. Used in different calculations (mousetracking, zoom).
     * @return cellSize value.
     */
    public int getCellSize(){ return cellSize; }

    /**
     * Setter for sleep value.
     * @param value new sleep value.
     */
    public void setSpeed(int value){ sleep=value; }

    /**
     * Pause/continue imitator. If the game was paused - starts it. If the game is running - puts it on pause.
     */
    public void pause(){
        if(pause){
            pause=false;
        }else {
            pause = true;
        }
    }

    /**
     * Getter for pause boolean.
     * @return boolean indicating if the game is paused or not.
     */
    public boolean getPause(){ return pause; }

    /**
     * Calculates next generation one time.
     * Used by button with the same name.
     */
    public void step(){ game.calculateNextGen(); }

    /**
     * Creates JPanel that will have graphical representation of the JeuDeLaVie in it.
     */
    public void createSimPanel(){ simPanel = new SimPanel(); }

    /**
     * Resets the grid.
     */
    public void reset(){ game.initializeGrid(); }

    /**
     * Main panel have 2 panels attached to it - content panel and setup panel.
     * This method allows to switch between them without creating new frames.
     */
    public void switchPanel(){
        if(setup){
            setup=false;
            contentPanel = new MainGrid(this);
            contentPanel.setName("main");
            mainPanel.remove(setupPanel);
            mainPanel.add(contentPanel.getName(),contentPanel);
            layout.show(mainPanel,"main");
        }else{
            setup=true;
            setupPanel = new SetupGrid(game,this);
            setupPanel.setName("setup");
            mainPanel.remove(contentPanel);
            mainPanel.add(setupPanel.getName(),setupPanel);
            layout.show(mainPanel,"setup");
        }
        launchSimulation();
    }

    /**
     * Kills pause handling thread.
     */
    public void killPauseHandler(){ pauseHandler.interrupt(); }

    /**
     * Main loop that calculates next generations.
     */
    public void launchSimulation(){
        game.setVisitor(currentVisitor);
        this.setVisible(true);
        game.addObserver(this);
        game.addObserver(new GenerationObserver(game));

        if(pauseHandler==null){
            pauseHandler = new Thread(() -> {
                while(true) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                    }
                    if(!getPause()){
                        game.calculateNextGen();
                    }
                }
            });
            pauseHandler.start();
        }
    }

    /**
     * Repaints the simPanel. Observer method.
     */
    public void actualise(){ simPanel.repaint(); }

    /**
     * Inner class SimPanel. The only difference with base JPanel is redefined paintComponent method.
     */
    public class SimPanel extends JPanel{
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            g.setColor(paintColor);

            Graphics2D g2 = (Graphics2D)g;

            AffineTransform tx = new AffineTransform();
            tx.translate(200,200);
            tx.scale(getZoom(),getZoom());
            tx.translate(-200,-200);
            g2.setTransform(tx);

            for(int x=0; x<game.getXmax();x++){
                for(int y=0; y<game.getYmax();y++){
                    if(game.getCellXY(x,y).isAlive()){
                        g.fillRect(x*getCellSize()+((400-game.getXmax()*getCellSize())/2),y*getCellSize()+((400-game.getXmax()*getCellSize())/2),getCellSize(),getCellSize());
                    }
                }
            }
        }
    }

    /**
     * Inner class of JeuDeLaVie.
     * Customized JPanel that represents a grid, containing all of the elements of the content panel:
     * - Speed slider.
     * - Navigation buttons.
     * - Zoom buttons.
     * - Color picker.
     * - Rulesets.
     * - SimPanel.
     * @param gbc is a GridBagConstraint that is used to position different panels of the grid.
     * @param tmenu top menu.
     * @param bmenu bottom menu.
     * @param lmenu left menu.
     * @param colorPanel color picker panel.
     * @param start start button.
     * @param step step button.
     * @param reset reset button.
     * @param zIn zoom in button.
     * @param zOut zoom out button.
     * @param zoom 1:1 ration button.
     * @param tryButton button that allows user to try different patters and rulesets.
     * @param colorButton color picker button.
     * @param parent is the parent of MainGrid - instance of JeuDeLaVieUI.
     * @param speedSlider is a slider that allows to regulate speed of exection.
     * @param ruleset is a list of rulesets.
     * @param resume resume button icon.
     * @param pause pause button icon.
     * @param rules rules label.
     * @param lfigures labels for list of figures.
     * @param logout logout panel.
     * @param logbutton logout button that allows to go back to the main menu.
     * @param colors frame that will display color picker.
     * @param colorpicker well, it's a color picker.
     * @param patterns a list of patterns of Game Of Life.
     */
    public class MainGrid extends JPanel{
        GridBagConstraints gbc;
        JPanel tmenu,bmenu,lmenu,colorPanel,logout;
        JButton start,step,reset,zIn,zoom,zOut,tryButton,colorbutton,logbutton;
        JeuDeLaVieUI parent;
        JSlider speedSlider;
        JComboBox ruleset;
        ImageIcon resume;
        ImageIcon pause;
        JLabel rules, lfigures;
        JFrame colors;
        JColorChooser colorpicker;
        JComboBox patterns;

        /**
         * Constructor of the MainGrid.
         * @param x instance of JeuDeLaVie which is the parent of the main grid.
         */
        public MainGrid(JeuDeLaVieUI x) {

            //Base initialization
            setLayout(new GridBagLayout());

            parent = x;

            resume = new ImageIcon(getClass().getClassLoader().getResource("resume.png"));
            pause = new ImageIcon(getClass().getClassLoader().getResource("pause.png"));

            // Setting layout of the panel
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            // Logout panel that contains logout button.
            logout = new JPanel();
            logout.setBorder(BorderFactory.createTitledBorder("Home"));
            logbutton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("home.png")));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            logout.add(logbutton);
            add(logout,gbc);


            //Color picker panel
            colorPanel = new JPanel();
            colorPanel.setBorder(BorderFactory.createTitledBorder("Colors"));
            colorbutton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("colorpicker.png")));
            colorpicker = new JColorChooser(colorbutton.getForeground()); // PROBLEM
            colors = new JFrame();
            colors.setLocationRelativeTo(null);
            colors.add(colorpicker,BorderLayout.PAGE_END);
            colors.setSize(new Dimension(450,350));
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            colorPanel.add(colorbutton);
            add(colorPanel,gbc);

            //Upper menu containing grid options and patterns to test.
            tmenu = new JPanel();
            tmenu.setBorder(BorderFactory.createTitledBorder("Grid Options"));
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 1;

            // Rulesets (aka Visitors)
            String[] options = {"Classic", "Day & Night", "HighLife"};
            rules = new JLabel("Ruleset : ");
            ruleset = new JComboBox(options);

            if (game.getVisitor() instanceof VisitorClassic) {
                ruleset.setSelectedIndex(0);
            } else if (game.getVisitor() instanceof VisitorDayNight){
                ruleset.setSelectedIndex(1);
            }else{
                ruleset.setSelectedIndex(2);
            }

            String[] pattern = {"Classic","Snails","Replicators","Glider Gun"};
            patterns = new JComboBox(pattern);
            lfigures = new JLabel("Patterns : ");
            patterns.setSelectedIndex(0);
            gbc.gridx=1;
            gbc.gridy=0;
            tmenu.add(lfigures,gbc);
            tmenu.add(patterns,gbc);
            tmenu.add(rules);
            tmenu.add(ruleset);
            tryButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("try.png")));
            tmenu.add(tryButton,gbc);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(tmenu, gbc);

            //Left menu with zoom options
            lmenu = new JPanel();
            lmenu.setLayout(new GridLayout(3,1));
            lmenu.setBorder(BorderFactory.createTitledBorder("Zoom"));
            zIn = new JButton(new ImageIcon(getClass().getClassLoader().getResource("zoomin.png")));
            zoom = new JButton(new ImageIcon(getClass().getClassLoader().getResource("zoom.png")));
            zOut = new JButton(new ImageIcon(getClass().getClassLoader().getResource("zoomout.png")));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.BOTH;
            lmenu.add(zIn);
            lmenu.add(zoom);
            lmenu.add(zOut);
            add(lmenu, gbc);

            //Right menu with the speed slider
            speedSlider = new JSlider(JSlider.VERTICAL, 0, 300, 150);
            speedSlider.setMinorTickSpacing(50);
            speedSlider.setMajorTickSpacing(100);
            speedSlider.setPaintTicks(true);
            speedSlider.setPaintLabels(true);
            speedSlider.setBorder(BorderFactory.createTitledBorder("Speed"));
            gbc.gridx = 2;
            gbc.gridy = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.VERTICAL;
            add(speedSlider, gbc);


            //Simpanel - central panel which gives visual representation of the grid and it's progression
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridheight = 1;
            gbc.fill = GridBagConstraints.VERTICAL;
            simPanel.setPreferredSize(new Dimension(400, 400));
            simPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            add(simPanel, gbc);

            //Bottom menu containing 3 buttons - play/pause, step, reset
            bmenu = new JPanel();
            bmenu.setBorder(BorderFactory.createTitledBorder("Navigation"));
            start = new JButton(pause);
            step = new JButton(new ImageIcon(getClass().getClassLoader().getResource("step.png")));
            reset = new JButton(new ImageIcon(getClass().getClassLoader().getResource("reset.png")));
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 5;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            bmenu.add(start);
            bmenu.add(step);
            bmenu.add(reset);
            add(bmenu,gbc);


            /////////////////////////////////////////
            // ACTION LISTENERS AND EVENT HANDLERS //
            /////////////////////////////////////////

            // Sets a color picked by user
            colorbutton.addActionListener(e -> { colors.setVisible(true);});

            colorpicker.getSelectionModel().addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    parent.setColor(colorpicker.getColor());
                }
            });

            //Zooms in or out depending on mouse wheel rotation
            simPanel.addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int rotation = e.getWheelRotation();
                    if (rotation < 0) {
                        zIn.doClick();
                    } else {
                        zOut.doClick();
                    }
                }
            });

            // Speed Slider listener - changes speed of execution
            speedSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent event) {
                    int value = speedSlider.getValue();
                    setSpeed(300 - value);
                }
            });

            // Try button. Allows to change current ruleset.
            // Also supposed to show different patterns, but I doubt I will have time to implement it.
            tryButton.addActionListener (e -> {
                parent.setPause(true);
                try {
                    Thread.sleep(speedSlider.getValue());
                } catch (InterruptedException x1) {
                }

                try {
                    if(patterns.getSelectedIndex()==0){
                        parent.reset();
                        GenerationObserver.reset();
                        parent.actualise();
                    }else if(patterns.getSelectedIndex()==1){
                        game.loadPattern('1');
                        parent.actualise();
                    }else if(patterns.getSelectedIndex()==2){
                        game.loadPattern('2');
                        parent.actualise();
                    }else if(patterns.getSelectedIndex()==3){
                        game.loadPattern('3');
                        parent.actualise();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                }

                if(ruleset.getSelectedIndex()==0){
                    game.setVisitor(VisitorClassic.getInstance(game));
                    game.distributeVisitors();
                }else if(ruleset.getSelectedIndex()==1){
                    game.setVisitor(VisitorDayNight.getInstance(game));
                    game.distributeVisitors();
                }else{
                    game.setVisitor(VisitorHighLife.getInstance(game));
                    game.distributeVisitors();
                }
                parent.setPause(false);
            });

            // Start button listener
            start.addActionListener(e -> {
                parent.pause();
                if(parent.getPause()){
                    start.setIcon(resume);
                }else{
                    start.setIcon(pause);
                }
            });

            // Step button listener
            step.addActionListener(e -> {
                if(parent.getPause()){
                    parent.step();
                } });

            // Reset button listener
            reset.addActionListener(e -> {
                try {
                    if(patterns.getSelectedIndex()==0){
                        parent.reset();
                        GenerationObserver.reset();
                        parent.actualise();
                    }else if(patterns.getSelectedIndex()==1){
                        game.loadPattern('1');
                        parent.actualise();
                    }else if(patterns.getSelectedIndex()==2){
                        game.loadPattern('2');
                        parent.actualise();
                    }else if(patterns.getSelectedIndex()==3){
                        game.loadPattern('3');
                        parent.actualise();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                } });

            // Home button listener - allows to go back to the main menu
            logbutton.addActionListener(e -> {
                parent.killPauseHandler();
                parent.setPause(true);
                parent.resetGame();
                switchPanel();
            });

            // Zoom in button listener
            zIn.addActionListener(e -> {
                parent.setZoom(parent.getZoom() + 0.25);
                parent.actualise();
            });

            // Zoom out button listener
            zOut.addActionListener(e -> {
                if (parent.getZoom() > 0.25) {
                    parent.setZoom(parent.getZoom() - 0.25);
                    parent.actualise();
                }
            });

            // Zoom reset button listener
            zoom.addActionListener(e -> {
                parent.setZoom(1);
                parent.actualise();
            });

            // Tracks mouse movement in the simpanel
            // If dimensions of the game are 50x50, allows to create living/dead cells on click.
            simPanel.addMouseListener(new MouseListener() {
                int cellsize;

                @Override
                public void mouseClicked(MouseEvent e) {
                    cellsize = getCellSize();
                    if (game.getCellXY(e.getX() / cellsize, e.getY() / cellsize).isAlive()) {
                        game.getCellXY(e.getX() / cellsize, e.getY() / cellsize).die();
                    } else {
                        game.getCellXY(e.getX() / cellsize, e.getY() / cellsize).live();
                    }
                    parent.actualise();
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
        }
    }

    /**
     * Inner class of JeuDeLaVieUI.
     * Represents customized JPanel that contains the main menu of the game that allows to change some settings of the game.
     * @param go start button.
     * @param gbc GridBagLayout variable needed for good positioning of different elements of the panel.
     * @param dimSlider dimensions slider. Allows to change dimensions of the grid.
     * @param denSlider density slider. Defines density of living cells.
     * @param ruleset list of rulesets (visitors).
     * @param game instance of JeuDeLaVie to which the panel is attached.
     * @param parent instance of JeuDeLaVieUI that is a parent of the panel.
     */
    public class SetupGrid extends JPanel{
        JButton go;
        GridBagConstraints gbc;
        JSlider dimSlider,denSlider;
        JComboBox ruleset;
        JeuDeLaVie game;
        JeuDeLaVieUI parent;

        /**
         * Constructor of the setup grid.
         * @param x instance of JeuDeLaVie to which the panel is attached.
         * @param parent instance of JeuDeLaVieUI that is a parent of the panel.
         */
        public SetupGrid(JeuDeLaVie x, JeuDeLaVieUI parent){

            this.parent=parent;
            game=x;
            go=new JButton("GO");

            //Layout setup
            gbc=new GridBagConstraints();
            gbc.insets=new Insets(5,5,5,5);
            setLayout(new GridBagLayout());


            JLabel logo = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("smologo.png")));
            JLabel subtitle = new JLabel("Initial configuration");
            subtitle.setFont(new Font("Arial", Font.BOLD, 18));
            gbc.gridx=0;
            gbc.gridy=0;
            gbc.gridwidth=4;
            gbc.fill=GridBagConstraints.HORIZONTAL;
            add(logo,gbc);
            gbc.gridy=1;
            add(subtitle,gbc);

            //Dimensions slider
            dimSlider = new JSlider(JSlider.HORIZONTAL, 0, 400, 50);
            dimSlider.setMinorTickSpacing(50);
            dimSlider.setMajorTickSpacing(100);
            dimSlider.setPaintTicks(true);
            dimSlider.setPaintLabels(true);
            dimSlider.setBorder(BorderFactory.createTitledBorder("Dimensions (NxN) :"));
            gbc.gridx=1;
            gbc.gridy=2;
            gbc.gridwidth=4;
            gbc.fill=GridBagConstraints.HORIZONTAL;
            add(dimSlider,gbc);

            //Density slider
            denSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
            denSlider.setMinorTickSpacing(5);
            denSlider.setMajorTickSpacing(25);
            denSlider.setPaintTicks(true);
            denSlider.setPaintLabels(true);
            denSlider.setBorder(BorderFactory.createTitledBorder("Density of living cells (in %) :"));
            gbc.gridx=1;
            gbc.gridy=3;
            gbc.gridwidth=4;
            gbc.fill=GridBagConstraints.HORIZONTAL;
            add(denSlider,gbc);

            //Initial rulesets
            String[] options = {"Classic","Day & Night","HighLife"};
            ruleset = new JComboBox(options);
            ruleset.setBorder(BorderFactory.createTitledBorder("Initial ruleset :"));
            ruleset.setSelectedIndex(0);
            gbc.gridx=1;
            gbc.gridy=4;
            add(ruleset,gbc);

            //Go button
            gbc.gridx=1;
            gbc.gridy=5;
            add(go,gbc);

            //Go button action listener
            go.addActionListener( e -> {

                game.setDimensions(dimSlider.getValue());
                game.setDensity((double)(denSlider.getValue()*0.01));
                game.initializeGrid();

                if(ruleset.getSelectedIndex()==0){
                    setCurrentVisitor(VisitorClassic.getInstance(game));
                    game.setVisitor(currentVisitor);
                    game.distributeVisitors();
                }else if(ruleset.getSelectedIndex()==1){
                    setCurrentVisitor(VisitorDayNight.getInstance(game));
                    game.setVisitor(currentVisitor);
                    game.distributeVisitors();
                }else{
                    setCurrentVisitor(VisitorHighLife.getInstance(game));
                    game.setVisitor(currentVisitor);
                    game.distributeVisitors();
                }

                parent.resetCellSize();
                parent.createSimPanel();
                parent.switchPanel();
                parent.setPause(false);

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException y) {
                }

            });
        }
    }
}
