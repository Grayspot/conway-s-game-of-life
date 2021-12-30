package cells;

import visitors.Visitor;

/**
 * Implementation of CellState. Represents a dead state.
 */
public class StateDead implements CellState{
    private static StateDead instance;


    /**
     * Resurrects the cell.
     * @return an instance of a living cell.
     */
    public CellState live(){ return StateAlive.getInstance(); }

    /**
     * Kills the cell.
     * @return an instance of a dead cell.
     */
    public CellState die(){
        if(instance==null){
            StateDead.getInstance();
        }
        return instance;
    }

    /**
     * Checks if the cell is alive.
     * @return true if alive, false if not.
     */
    public boolean isAlive(){ return false; }

    /*
       #############################
       # SINGLETON IMPLEMENTATION  #
       #############################
    */

    /**
     * Private constructor. Is private for implementation of patter Singleton.
     */
    private StateDead(){}

    /**
     * Returns unique instance of the object of class StateDead if it does not exist already.
     * Method is synchronized to take prevent creating 2 objects at the exact same time.
     * @return instance instance of StateDead.
     */
    public static synchronized StateDead getInstance(){
        if(StateDead.instance==null){
            instance=new StateDead();
        }
        return instance;
    }

    /*
       ###########################
       # VISITOR IMPLEMENTATION  #
       ###########################
    */

    /**
     * Attaches visitor to a cell.
     * @param v visitor that will be attached.
     * @param c cell that will get a visitor attached.
     */
    public void accept(Visitor v, Cell c){
        v.visitDeadCell(c);
    }
}
