package it.polimi.ingsw.server.model.cards.powers.tags;

import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a complementary malus
 * It contains the type of malus (it can be a move malus or a build one), the number of turns it lasts,
 * the directions which the malus is applied to and whether it is permanent or limited to a specific number of turns
 */
public class Malus {

    private MalusType malusType;
    private boolean permanent;
    private int numberOfTurns;
    private final List<MalusLevel> direction;

    public Malus() {
        direction = new ArrayList<>();
    }

    /**
     * Constructor of the malus
     *
     * @param malus the actual malus
     */
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

    /**
     * Method that adds new level to the directions that the malus is applied to
     *
     * @param malusDirectionElement the level that is added to the malus
     */
    public void addDirectionElement(MalusLevel malusDirectionElement) {
        direction.add(malusDirectionElement);
    }


    /**
     * Method that checks if the malus passed as parameter is equal to the current one
     * To do so it checks that all the attributes of the malus are actually the same
     *
     * @param obj Malus that wants to be checked
     * @return {@code true} if malus is equal, {@code false} if it is ot or if the object isn't a malus
     */
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
