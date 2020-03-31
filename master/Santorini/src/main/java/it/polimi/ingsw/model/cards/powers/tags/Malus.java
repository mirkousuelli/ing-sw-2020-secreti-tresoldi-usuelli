package it.polimi.ingsw.model.cards.powers.tags;

import it.polimi.ingsw.model.cards.MalusPlayer;

import java.util.ArrayList;

public class Malus extends MalusPlayer {

    private boolean personal; // se il malus è su di te o sugli altri

    public Malus() {
        super();
    }

    public Malus(Malus malus) {
        direction = new ArrayList<>();

        malusType = malus.getMalusType();
        permanent = malus.isPermanent();
        personal = malus.isPersonal();
        numberOfTurns = malus.numberOfTurns;
        direction.addAll(malus.direction);
    }

    public boolean isPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }
}
