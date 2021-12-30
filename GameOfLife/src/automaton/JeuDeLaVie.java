package automaton;
import cells.*;
import commands.Command;
import visitors.Visitor;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * One of the main classes of Game Of Life. This class is responsible of algorithmic part of the game.
 * @param observers list of observers.
 * @param commands list of commands.
 * @param visitor attached visitor.
 * @param xMax maximal size of the grid on X axis.
 * @param yMax maximal size of the grid on Y axis.
 * @param density density of living cells. Double between 0 and 1.
 * @param grid main grid of the game.
 */
public class JeuDeLaVie implements Observed{

    private List<Observer> observers;
    private List<Command> commands;
    private Visitor visitor;
    private int xMax,yMax;
    private double density;
    private Cell[][] grid;

    /**
     * Getter on xMax.
     * @return xMax.
     */
    public int getXmax(){ return xMax; }

    /**
     * Getter on yMax.
     * @return yMax.
     */
    public int getYmax(){ return yMax; }

    /**
     * Setter for dimensions.
     */
    public void setDimensions(int dim){
        xMax=dim;
        yMax=dim;
    }

    /**
     * Setter for density.
     */
    public void setDensity(double den){ density=den; }


    /**
     * Constructor for JeuDeLaVie.
     */
    public JeuDeLaVie(){
        density=0.5;
        xMax=500;
        yMax=500;
        grid=new Cell[yMax][xMax];
        observers = new ArrayList<>();
        commands = new ArrayList<>();
    }

    /**
     * Initializes grid with given density. Uses math.random to determine if the current cell will be dead or alive.
     */
    public void initializeGrid(){
        for(int i=0; i<yMax; i++){
            for(int j=0; j<xMax; j++){
                if(Math.random() < density){
                    grid[i][j]=new Cell(j,i, StateAlive.getInstance());
                }else{
                    grid[i][j]=new Cell(j,i, StateDead.getInstance());
                }
            }
        }
    }

    /**
     * Resets grid by killing all cells.
     */
    public void resetGrid(){
        for(int i=0; i<yMax; i++){
            for(int j=0; j<xMax; j++){
                grid[i][j].die();
            }
        }
    }

    /**
     * Returns number of living cells.
     * @return cpt - cell counter.
     */
    public int livingCellCounter(){
        int cpt=0;
        for(int i=0; i<yMax; i++){
            for(int j=0; j<xMax; j++){
                if(grid[i][j].isAlive()) {
                    cpt+=1;
                }
            }
        }
        return cpt;
    }

    /**
     * Returns number of dead cells.
     * @return cpt - cell counter.
     */
    public int deadCellCounter(){
        int cpt=0;
        for(int i=0; i<yMax; i++){
            for(int j=0; j<xMax; j++){
                if(!grid[i][j].isAlive()) {
                    cpt+=1;
                }
            }
        }
        return cpt;
    }

    /**
     * Returns cell from given coordinates.
     * @return null if coordinates are outside of the grid, cell if cell exists.
     */
    public Cell getCellXY(int x, int y){
        if(x<=xMax && y<=yMax){
            return grid[y][x];
        }else{
            System.out.println("Out of bounds.");
            return null;
        }
    }

    /**
     * Set of actions taken to calculate next generation.
     */
    public void calculateNextGen(){
        distributeVisitors();
        executeCommands();
        notifyObservers();
    }


    /*
       ####################################
       # OBSERVER METHODS IMPLEMENTATION  #
       ####################################
    */

    /**
     * Adds observer to the list of observers.
     * @param x the observer that will be added.
     */
    public void addObserver(Observer x){ this.observers.add(x); }

    /**
     * Removes observer to the list of observers.
     * @param x the observer that will be removed.
     */
    public void removeObserver(Observer x){ this.observers.remove(x); }

    /**
     * Notifies all observers from list of observers so that they take action.
     */
    public void notifyObservers(){
        for(Observer x: observers){
            x.actualise();
        }
    }

    /*
       ###################################
       # COMMAND METHODS IMPLEMENTATION  #
       ###################################
    */

    /**
     * Adds a command to the list of commands.
     * @param x command that will be added.
     */
    public void addCommand(Command x){ commands.add(x); }

    /**
     * Executes all commands from the list of commands the empties the list.
     */
    public void executeCommands(){
        for(Command x: commands){
            x.execute();
        }
        commands.clear();
    }

    /*
       ###########################
       # VISITOR IMPLEMENTATION  #
       ###########################
    */

    /**
     * Sets current visitor.
     */
    public void setVisitor(Visitor v){ visitor=v; }

    /**
     * Getter for visitor.
     * @return current visitor.
     */
    public Visitor getVisitor(){ return visitor; }

    /**
     * Distributes visitor to every cell in the grid.
     */
    public void distributeVisitors() {
        for(int i = 0;i < xMax;i++) {
            for(int j = 0; j < yMax;j++) {
                grid[i][j].accept(visitor);
            }
        }
    }

    /**
     * Allows to load specific pattern from a text file.
     * @param number index of pattern that will be loaded.
     */
    public void loadPattern(char number) throws IOException, URISyntaxException {

        InputStream in = getClass().getResourceAsStream("/patterns.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        int c,i=0,j=0;
        char character;
        while((c = reader.read()) != -1) {
            character = (char) c;
            if(character==number){
                while(character != '!'){
                    if(character=='.'){
                        grid[i][j].die();
                        j++;
                    }else if(character=='x'){
                        grid[i][j].live();
                        j++;
                    }
                    if(character=='\n'){
                        System.out.print('\n');
                        i++;
                        j=0;
                    }
                    c = reader.read();
                    character = (char)c;
                }
            }
        }
    }
}
