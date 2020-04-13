/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.ArrayList;
import java.util.List;

public class Player {
    /*@class
     * it is used to represent a player, it contains a reference to the player's card and one to its workers
     */

    public final String nickName;
    private final List<Worker> worker;
    private Card card;
    private Worker currentWorker;
    private List<Malus> malusList;

    public Player(String nickName) {
        /*@constructor
         * it sets the name of the player and creates its 2 workers
         */

        this.nickName = nickName;
        worker = new ArrayList<>(2);
        card = null;
        currentWorker = null;
        malusList = new ArrayList<>();
    }

    public String getNickName() {
        return nickName;
    }

    public boolean initializeWorkerPosition(int id, Block position) {
        /*@function
         * it sets the initial position of the current worker
         */

        if (!position.isFree()) return false;
        if (id != 1 && id != 2) return false;

        worker.add(new Worker(this, position));

        return true;
    }

    public List<Worker> getWorkers() {
        /*@getter
         * it returns player's workers
         */

        return new ArrayList<>(worker);
    }

    public Worker getCurrentWorker() {
        /*@getter
         * it gets the current worker
         */

        return currentWorker;
    }

    public void setCurrentWorker(Worker currentWorker) {
        /*@setter
         * it sets the current worker to the designated one
         */

        this.currentWorker = currentWorker;
    }

    public Card getCard() {
        /*@getter
         * it gets the player's card
         */

        return card;
    }

    public void setCard(Card card) {
        /*@setter
         * it sets the player's card
         */

        this.card = card;
    }

    public void addMalus(Malus malusPlayer) {
        Malus found = malusList.stream()
                .filter(m -> m.equals(malusPlayer))
                .reduce(null, (m1, m2) -> m1 != null ? m1 : m2);

        if (found == null)
            malusList.add(malusPlayer);
    }

    public List<Malus> getMalusList() {
        return new ArrayList<>(malusList);
    }

    public void removeMalus() {
        malusList.removeIf(m -> !m.isPermanent() && m.getNumberOfTurns() == 0);
    }
}