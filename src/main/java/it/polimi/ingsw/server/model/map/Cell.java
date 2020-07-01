/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.map;

/**
 * Interface that represents the abstract object of the cell, which composes the board game and that is developed deeper by
 * {@link Block}
 */
public interface Cell {
    int getX();

    int getY();

    Level getLevel();

    void setX(int newX);

    void setY(int newY);

    void setLevel(Level newLevel);

    boolean isWalkable();

    boolean isComplete();

    boolean isFree();

    /**
     * Method that tells if the chosen cell is in the perimeter of the board
     *
     * @param cellToUse the cell that gets checked
     * @return {@code true} if the cell is in the perimeter, {@code false} otherwise
     */
    static boolean isPerim(Cell cellToUse) {
        return (cellToUse.getX() == 0 || cellToUse.getY() == 0 || cellToUse.getX() == 4 || cellToUse.getY() == 4);
    }

    void clean();
}
