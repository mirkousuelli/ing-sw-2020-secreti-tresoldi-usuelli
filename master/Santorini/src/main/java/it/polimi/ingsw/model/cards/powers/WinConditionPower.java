/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.map.Board;

public class WinConditionPower extends Power {

    public WinConditionPower(Card card) {
        super(card);
    }

    public boolean usePower(Board board) {
        switch (allowedWin) {
            case FIVETOWER:
            case DOWNTOFROMTWO:
                return true;
            default:
                return false;
        }
    }
}
