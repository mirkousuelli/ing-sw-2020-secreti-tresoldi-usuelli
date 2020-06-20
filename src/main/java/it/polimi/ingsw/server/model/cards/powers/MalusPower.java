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

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a malus power
 * <p>
 * The only two Gods that can apply a malus to the opponents are Athena and Persephone
 * <p>
 * Their effect becomes active during opponent's turn, when opponents cannot make specific actions or are forced
 * to do something: Persephone, for example, forces the opponents to move one worker up whenever it is possible
 * <p>
 * It extends {@link Power}
 */
public class MalusPower<S> extends Power<S> {

    /**
     * Constructor of the malus power that recalls its super class {@link Power}
     */
    public MalusPower() {
        super();
    }

    /**
     * Method that adds the malus to the opponents
     *
     * @param opponents     list of players that the malus is applied to
     * @param currentPlayer currentPlayer
     * @return {@code true} after the malus is added to the opponents
     */
    public boolean usePower(List<Player> opponents, Player currentPlayer) {
        Malus malusPlayer = new Malus((Malus) allowedAction);

        if (!malusPlayer.isPermanent()) {
            List<Malus> maluses = new ArrayList<>();
            maluses.add(malusPlayer);

            if (ActivePower.verifyMalus(maluses, currentPlayer.getCurrentWorker().getPreviousLocation(), currentPlayer.getCurrentWorker().getLocation()))
                return false;
        }

        for (Player opponent : opponents)
            opponent.addMalus(malusPlayer);

        return true;
    }
}
