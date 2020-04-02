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

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.MalusPlayer;

import java.util.List;

public class MalusPower extends Power {

    public MalusPower(/*Card card*/) {
        super(/*card*/);
    }

    public void usePower(List<Player> opponents) {
        MalusPlayer malusPlayer = new MalusPlayer(malus);

        for (Player opponent : opponents)
            opponent.addMalus(malusPlayer);
    }
}
