package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.cards.gods.God;

import java.util.ArrayList;
import java.util.List;

public class ReducedDeck {

    private List<God> reducedGodList;

    public ReducedDeck(List<God> reducedGodList) {
        this.reducedGodList = new ArrayList<>(reducedGodList);
    }

    public ReducedDeck(){}

    public List<God> getList() {
        return reducedGodList;
    }

    public void setList(List<God> list) {
        this.reducedGodList = list;
    }

    public God getReducedGod(int index) {
        if (index < 0 || index >= reducedGodList.size()) return null;

        return reducedGodList.get(index);
    }

}
