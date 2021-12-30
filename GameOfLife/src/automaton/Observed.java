package automaton;

/**
 * Observed interface used for implementation of design pattern Observer.
 */
public interface Observed {

    /**
     * Adds observer to the list of observers.
     * @param x observer that will be added.
     */
    public void addObserver(Observer x);

    /**
     * Adds observer to the list of observers.
     * @param x observer that will be removed.
     */
    public void removeObserver(Observer x);

    /**
     * Notifies all observers so that they do what they are supposed to do.
     */
    public void notifyObservers();
}
