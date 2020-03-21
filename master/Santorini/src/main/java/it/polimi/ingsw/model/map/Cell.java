/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.map;

public interface Cell {
    /* @abstractClass
     * it represents the abstract object which composes the board game and which is developed
     * deeper by the Block class
     */

    /* GETTER ---------------------------------------------------------------------------------------------------------- */
    int getX();
    int getY();
    Level getLevel();

    /* SETTER ---------------------------------------------------------------------------------------------------------- */
    void setX(int newX);
    void setY(int newY);
    void setLevel(Level newLevel);
    void setBusy();
    void setFree();

    /* FUNCTION -------------------------------------------------------------------------------------------------------- */
    boolean isWalkable();
    boolean isComplete();
    boolean isBusy();
    void clean();
}
