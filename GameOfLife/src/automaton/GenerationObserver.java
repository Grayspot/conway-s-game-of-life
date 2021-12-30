package automaton;

/**
 * Second observer used to output to terminal current generation number and living/dead cells counter.
 * @param game represents an instance of JeuDeLaVie to which this observer will be attached.
 * @param currGen represents current generation counter.
 */
public class GenerationObserver implements Observer {

    private final JeuDeLaVie game;
    private static int currGen;

    /**
     * Constructor for Generation Observer.
     * @param x is an instance of JeuDeLaVie that this observer will attach to.
     */
    public GenerationObserver(JeuDeLaVie x){
        game=x;
        currGen=0;
    }

    /**
     * Prints in the terminal the number of current generation, living and dead cells.
     */
    public void printStats(){
        System.out.println("Current Generation: "+currGen+"\nLiving cells : "+game.livingCellCounter()+"\nDead cells : "+game.deadCellCounter());
    }

    /**
     * Clears terminal on call.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Resets generation counter.
     */
    public static void reset(){ currGen=0; }

    /**
     * Update method. Increases generation counter, then clears the terminal and prints new info.
     */
    @Override
    public void actualise() {
        currGen+=1;
        clearScreen();
        printStats();
    }
}
