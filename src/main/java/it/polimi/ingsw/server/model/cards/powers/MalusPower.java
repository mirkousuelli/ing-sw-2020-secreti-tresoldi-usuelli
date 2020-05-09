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

public class MalusPower<S> extends Power<S> {

    public MalusPower() {
        super();
    }

    public boolean usePower(List<Player> opponents) {
        Malus malusPlayer = new Malus((Malus) allowedAction);

        for (Player opponent : opponents)
            opponent.addMalus(malusPlayer);

        return true;
    }
}
