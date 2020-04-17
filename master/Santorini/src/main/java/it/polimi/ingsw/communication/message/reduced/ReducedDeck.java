package it.polimi.ingsw.communication.message.reduced;

import it.polimi.ingsw.server.model.cards.God;

import java.util.ArrayList;
import java.util.List;

public class ReducedDeck {

    private final List<God> reducedGodList;

    public ReducedDeck(List<God> reducedGodList) {
        this.reducedGodList = new ArrayList<>(reducedGodList);
    }

    public God getReducedGod(int index) {
        if (index < 0 || index >= reducedGodList.size()) return null;

        return reducedGodList.get(index);
    }

}
