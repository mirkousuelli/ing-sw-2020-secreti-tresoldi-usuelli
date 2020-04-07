/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.state.Game;

public class WinConditionPower extends Power {

    public WinConditionPower() {
        super();
    }

    public boolean usePower(Game game) {
        switch (allowedWin) {
            case FIVETOWER:
                return true;
            case DOWNTOFROMTWO:
                return game.getCurrentPlayer().getCurrentWorker().getPreviousLocation().getLevel().toInt() -
                       game.getCurrentPlayer().getCurrentWorker().getLocation().getLevel().toInt() >= 2;
            default:
                return false;
        }
    }
}
