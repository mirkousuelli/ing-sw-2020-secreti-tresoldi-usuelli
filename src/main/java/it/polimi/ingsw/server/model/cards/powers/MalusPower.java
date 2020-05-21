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

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;

import java.util.List;

/**
 * Class that represents a malus power
 * It extends the class Power
 * <p>
 *     The only two Gods that can apply a malus to the opponents are Athena and Persephone
 *     Their effect becomes active during opponent's turn, when opponents cannot make specific actions or are forced
 *     to do something: Persephone, for example, forces the opponents to move one worker up whenever it is possible
 */
public class MalusPower<S> extends Power<S> {

    /**
     * Constructor of the malus power that recalls its super class
     */
    public MalusPower() {
        super();
    }

    /**
     * Method that adds the malus to the opponents
     *
     * @param opponents list of players that the malus is applied to
     * @return {@code true} after the malus is added to the opponents
     */
    public boolean usePower(List<Player> opponents) {
        Malus malusPlayer = new Malus((Malus) allowedAction);

        for (Player opponent : opponents)
            opponent.addMalus(malusPlayer);

        return true;
    }
}
