package commands;

import cells.Cell;

/**
 * Concrete class CommandLive. An implementation of Command.
 * Used for patter Command.
 */
public class CommandLive extends Command{
    /**
     * Constructor for CommandLive.
     * @param x the cell to which the command will be attached.
     */
    public CommandLive(Cell x){
        super(x);
    }

    /**
     * Command this cell to live.
     */
    @Override
    public void execute(){
        this.c.live();
    }
}
