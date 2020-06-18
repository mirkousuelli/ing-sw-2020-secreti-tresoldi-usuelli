package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.cards.gods.God;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the reduced version of the deck, which contains a list of Gods
 */
public class ReducedDeck {
    private List<God> reducedGodList;

    /**
     * Constructor of the reduced deck that is created from the list of Gods
     *
     * @param reducedGodList list of Gods which de reduced version of the deck is obtained from
     */
    public ReducedDeck(List<God> reducedGodList) {
        this.reducedGodList = new ArrayList<>(reducedGodList);
    }

    public ReducedDeck() {

    }

    public God getReducedGod(int index) {
        if (index < 0 || index >= reducedGodList.size()) return null;

        return reducedGodList.get(index);
    }

    public List<God> getReducedGodList() {
        return reducedGodList;
    }

    public void setReducedGodList(List<God> reducedGodList) {
        this.reducedGodList = reducedGodList;
    }
}
