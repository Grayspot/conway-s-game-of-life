package visitors;

import cells.Cell;

/**
 * Interface Visitor used for implementing design pattern visitor.
 */
public interface Visitor {

    /**
     * Visitor visits living cell.
     * @param c cell that will be affected by visitor.
     */
    public void visitLivingCell(Cell c);

    /**
     * Visitor visits dead cell.
     * @param c cell that will be affected by visitor.
     */
    public void visitDeadCell(Cell c);
}
