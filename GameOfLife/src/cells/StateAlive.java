package cells;

import visitors.Visitor;

/**
 * Implementation of CellState. Represents a living state.
 */
public class StateAlive implements CellState{
    private static StateAlive instance;

    /**
     * Resurrects the cell.
     * @return an instance of a living cell.
     */
    public CellState live(){
        if(instance==null){
            StateAlive.getInstance();
        }
        return instance;
    }

    /**
     * Kills the cell.
     * @return an instance of a dead cell.
     */
    public CellState die(){ return StateDead.getInstance(); }

    /**
     * Checks if the cell is alive.
     * @return true if alive, false if not.
     */
    public boolean isAlive(){
        return true;
    }

    /*
       #############################
       # SINGLETON IMPLEMENTATION  #
       #############################
    */

    /**
     * Private constructor. Is private for implementation of patter Singleton.
     */
    private StateAlive(){}

    /**
     * Returns unique instance of the object of class StateAlive if it does not exist already.
     * Method is synchronized to take prevent creating 2 objects at the exact same time.
     * @return instance instance of StateAlive.
     */
    public static synchronized StateAlive getInstance(){
        if(StateAlive.instance==null){
            instance=new StateAlive();
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
        v.visitLivingCell(c);
    }
}
