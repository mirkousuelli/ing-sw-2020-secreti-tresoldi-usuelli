package it.polimi.ingsw.server.model.cards.powers.tags;

import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;

import java.util.ArrayList;
import java.util.List;

public class Malus {

    private MalusType malusType;
    private boolean permanent;
    private int numberOfTurns;
    private final List<MalusLevel> direction;

    public Malus() {
        direction = new ArrayList<>();
    }

    public Malus(Malus malus) {
        direction = new ArrayList<>();

        malusType = malus.getMalusType();
        permanent = malus.isPermanent();
        numberOfTurns = malus.numberOfTurns;
        direction.addAll(malus.direction);
    }

    public MalusType getMalusType() {
        return malusType;
    }

    public void setMalusType(MalusType malusType) {
        this.malusType = malusType;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    public void setNumberOfTurns(int numberOfTurns) {
        this.numberOfTurns = numberOfTurns;
    }

    public List<MalusLevel> getDirection() {

        return new ArrayList<>(direction);
    }

    public void addDirectionElement(MalusLevel malusDirectionElement) {
        direction.add(malusDirectionElement);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Malus)) return false;

        Malus malus = (Malus) obj;

        return malusType.equals(malus.getMalusType()) &&
               permanent == malus.isPermanent() &&
               numberOfTurns == malus.getNumberOfTurns() &&
               direction.containsAll(malus.getDirection());
    }
}
