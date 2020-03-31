/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.powers.tags.Malus;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusType;

import java.util.ArrayList;
import java.util.List;

public class MalusPlayer {

    protected MalusType malusType;
    protected boolean permanent;
    protected int numberOfTurns;
    protected List<MalusLevel> direction;

    public MalusPlayer() {
        direction = new ArrayList<>();
    }

    public MalusPlayer(Malus malus) {
        direction = new ArrayList<>();

        malusType = malus.malusType;
        permanent = malus.permanent;
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
        return direction;
    }

    public void addDirectionElement(MalusLevel malusDirectionElement) {
        direction.add(malusDirectionElement);
    }
}
