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

import it.polimi.ingsw.model.exceptions.map.NotValidCellException;
import it.polimi.ingsw.model.exceptions.map.NotValidLevelException;

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
    void setX(int newX) throws NotValidCellException;
    void setY(int newY) throws NotValidCellException;
    void setLevel(Level newLevel) throws NullPointerException, NotValidLevelException;

    /* PREDICATE ------------------------------------------------------------------------------------------------------- */
    boolean isWalkable();
    boolean isComplete();
    boolean isFree();

    /* FUNCTION -------------------------------------------------------------------------------------------------------- */
    void clean();
}
