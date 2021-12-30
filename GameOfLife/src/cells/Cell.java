package cells;

import automaton.JeuDeLaVie;
import visitors.Visitor;

/**
 * Class that represents cell in the grid.
 * @param x coordinate on X axis.
 * @param y coordinate on Y axis.
 * @param state state of the given cell.
 */
public class Cell {
    public int x,y;

    private CellState state;

    /**
     * Resurrects this cell.
     */
    public void live(){ state=state.live(); }

    /**
     * Kills this cell.
     */
    public void die(){ state=state.die(); }

    /**
     * Checks if cell is alive.
     * @return true if alive, false if not.
     */
    public boolean isAlive(){ return state.isAlive(); }

    /**
     * Constructor for cell.
     * @param x its X coordinate.
     * @param y its Y coordinate.
     * @param state its state.
     */
    public Cell(int x, int y, CellState state){
        this.x=x;
        this.y=y;
        this.state=state;
    }

    /**
     * Checks how many living neighbours does the cell have.
     * @param grid the grid in which the cell is placed.
     * @return number of living cells around current one.
     */
    public int livingNeighbours(JeuDeLaVie grid){
        int counter=0;
        for(int i=-1; i<=1; i++){
            for(int j=-1; j<=1; j++){
                if( i!=0 || j != 0){
                    if((x+i)>=0 && (x+i) < grid.getXmax() && (y+j) >=0 && (y+j) < grid.getYmax()){
                        if(grid.getCellXY(x+i,y+j).isAlive()) {
                            counter++;
                        }
                    }
                }
            }
        }
        return counter;
    }


    /*
       ###########################
       # VISITOR IMPLEMENTATION  #
       ###########################
    */

    /**
     * Attaches the visitor to the state of the cell.
     * @param v visitor that will be attached.
     */
    public void accept(Visitor v){ state.accept(v,this); }
}
