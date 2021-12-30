package commands;
import cells.Cell;

/**
 * Abstract class Command. Represents actions that will take place on a cell.
 * @param c cell to which the command is attached.
 */
public abstract class Command {
    protected Cell c;

    /**
     * Constructor for Command.
     * @x cell to which the command is attached.
     */
    public Command(Cell x){
        c=x;
    }

    /**
     * Executes a list of actions on the cell.
     */
    public abstract void execute();
}
