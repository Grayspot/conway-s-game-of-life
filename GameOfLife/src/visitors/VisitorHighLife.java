package visitors;
import automaton.JeuDeLaVie;
import cells.Cell;
import commands.*;

/**
 * Implementation of the interface Visitor. Represent classic rules of the Game Of Life.
 * @param game  an instance of JeuDeLaVie to which the visitor is attached.
 * @param instance a unique instance of the visitor.
 */
public class VisitorHighLife implements Visitor{
    protected JeuDeLaVie game;
    private static VisitorHighLife instance;

    /**
     * If a living cell has less than 2 or more than 3 living neighbours - it will die in the next generation.
     * @param c cell that will be affected by visitor.
     */
    @Override
    public void visitLivingCell(Cell c) {
        if( c.livingNeighbours(game) < 2 || c.livingNeighbours(game) > 3 ){
            game.addCommand(new CommandDie(c));
        }
    }

    /**
     * If a dead cell has exactly 3 or 6 living neighbours - it will resurrect in the next generation.
     * @param c cell that will be affected by visitor.
     */
    @Override
    public void visitDeadCell(Cell c) {
        if( c.livingNeighbours(game) == 3 || c.livingNeighbours(game) ==6){
            game.addCommand(new CommandLive(c));
        }
    }

    /**
     * Creates an unique instance of VisitorHighLife.
     * @param j an instance of JeuDeLaVie to which the visitor is attached.
     * @return an unique instnance of VisitorHighLife.
     */
    public static synchronized VisitorHighLife getInstance(JeuDeLaVie j){
        if(VisitorHighLife.instance==null){
            instance=new VisitorHighLife();
            instance.game=j;

        }
        return instance;
    }

    /**
     * Private constructor needed for implementing Singleton pattern.
     */
    public VisitorHighLife(){super();}
}
