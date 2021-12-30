package commands;
import cells.Cell;

/**
 * Concrete class CommandDie. An implementation of Command.
 * Used for patter Command.
 */
public class CommandDie extends Command{

    /**
     * Constructor for CommandDie.
     * @param x the cell to which the command will be attached.
     */
    public CommandDie(Cell x){
        super(x);
    }

    /**
     * Command this cell to die.
     */
    @Override
    public void execute(){
        this.c.die();
    }
}
