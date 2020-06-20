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

import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.WinType;
import it.polimi.ingsw.server.model.game.Game;

/**
 * Class that represents a win condition power
 * <p>
 * It extends the class {@link Power}
 * <p>
 * The only two Gods that have an additional win condition are Chronus, that allows the player to win if there are
 * at least five complete towers, and Pan, that is activated when a player's worker moves down two or more levels
 * Every other God has just the regular win condition
 *
 * <p>
 * This new win conditions do not replace the regular one (that is moving up to a third level), but they are just
 * added as an additional win condition: if a player, for example, has Chronus as God, he can both win by moving
 * to a third level or by having five complete towers built on the board
 */
public class WinConditionPower<S> extends Power<S> {

    /**
     * Constructor of the win condition power that recalls its super class {@link Power}
     */
    public WinConditionPower() {
        super();
    }

    /**
     * Method that checks if any additional win condition is verified
     *
     * @return {@code true} if any win condition is verified, {@code false} if not
     */
    public boolean usePower(Game game) {
        switch ((WinType) getAllowedAction()) {
            case FIVE_TOWER:
                return game.getNumberOfCompleteTower() == 5;

            case DOWN_FROM_TWO:
                return game.getCurrentPlayer().getCurrentWorker().getPreviousLocation().getLevel().toInt() -
                        game.getCurrentPlayer().getCurrentWorker().getLocation().getLevel().toInt() >= 2;

            default:
                return false;
        }
    }
}
