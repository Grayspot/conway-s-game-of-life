package cells;

import visitors.Visitor;

/**
 * Interface that represents the state of the cell.
 */
public interface CellState {

    /**
     * Resurrects the cell.
     * @return an instance of a living cell.
     */
    public CellState live();

    /**
     * Kills the cell.
     * @return an instance of a dead cell.
     */
    public CellState die();

    /**
     * Checks if the cell is alive.
     * @return true if alive, false if not.
     */
    public boolean isAlive();

    /**
     * Attaches visitor to a cell.
     * @param v visitor that will be attached.
     * @param c cell that will get a visitor attached.
     */
    public void accept(Visitor v, Cell c);
}
