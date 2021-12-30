package visitors;
import automaton.JeuDeLaVie;
import cells.Cell;
import commands.*;

/**
 * Implementation of the interface Visitor. Represent classic rules of the Game Of Life.
 * @param game  an instance of JeuDeLaVie to which the visitor is attached.
 * @param instance a unique instance of the visitor.
 */
public class VisitorDayNight implements Visitor{
    protected JeuDeLaVie game;
    private static VisitorDayNight instance;

    /**
     * If a living cell has less than 3 or exactly 5 living neighbours - it will die in the next generation.
     * @param c cell that will be affected by visitor.
     */
    @Override
    public void visitLivingCell(Cell c) {
        if( c.livingNeighbours(game) < 3 || c.livingNeighbours(game) == 5 ){
            game.addCommand(new CommandDie(c));
        }
    }

    /**
     * If a dead cell has exactly 3 living neighbours or more than 5 - it will resurrect in the next generation.
     * @param c cell that will be affected by visitor.
     */
    @Override
    public void visitDeadCell(Cell c) {
        if( c.livingNeighbours(game) == 3 || c.livingNeighbours(game) > 5){
            game.addCommand(new CommandLive(c));
        }
    }

    /**
     * Creates an unique instance of VisitorDayNight.
     * @param j an instance of JeuDeLaVie to which the visitor is attached.
     * @return an unique instnance of VisitorNight.
     */
    public static synchronized VisitorDayNight getInstance(JeuDeLaVie j){
        if(VisitorDayNight.instance==null){
            instance=new VisitorDayNight();
            instance.game=j;

        }
        return instance;
    }

    /**
     * Private constructor needed for implementing Singleton pattern.
     */
    private VisitorDayNight(){super();}
}
