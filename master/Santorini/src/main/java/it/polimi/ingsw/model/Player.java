/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.exceptions.cards.WrongWorkerException;
import it.polimi.ingsw.model.exceptions.map.OccupiedCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Worker;

import java.util.ArrayList;
import java.util.List;

public class Player {
    /*@class
     * it is used to represent a player, it contains a reference to the player's card and one to its workers
     */

    public final String nickName;
    public final List<Worker> worker;
    private Card card;
    private Worker currentWorker;
    private boolean mustMoveUp;
    private boolean cannotMoveUp;

    public Player(String nickName) {
        /*@constructor
         * it sets the name of the player and creates its 2 workers
         */

        this.nickName = nickName;
        worker = new ArrayList<>(2);
        card = null;
        currentWorker = null;
        mustMoveUp = false;
        cannotMoveUp = false;
    }

    public void move(Cell cell) throws Exception {
        /*@function
         * wrapper of worker.moveTo(Cell)
         */

        currentWorker.moveTo(cell);
    }

    public void build(Cell cell) throws Exception {
        /*@function
         * wrapper of worker.build(Cell)
         */

        currentWorker.build(cell);
    }

    public void initializeWorkerPosition(int id, Block position) throws Exception/*,OccupiedCellException, WrongWorkerException*/ {
        /*@function
         * it sets the initial position of the current worker
         */

        if (!position.isFree()) throw new OccupiedCellException("Selected cell is occupied!");
        if (id != 1 && id != 2) throw new WrongWorkerException("It must be 1 or 2!");

        worker.add(new Worker(this, position));
    }

    public List<Worker> getWorkers() {
        /*@getter
         * it returns player's workers
         */

        return worker;
    }

    public void addMustMoveUpMalus() {
        /*@function
         * it adds mustMoveUp malus
         */
        mustMoveUp = true;
    }

    public void removeMustMoveUpMalus() {
        /*@function
         * it removes mustMoveUp malus
         */
        mustMoveUp = false;
    }

    public void addCannotMoveUpMalus() {
        /*@function
         * it adds cannotMoveUp malus
         */
        cannotMoveUp = true;
    }

    public void removeCannotMoveUpMalus() {
        /*@function
         * it removes cannotMoveUp malus
         */
        cannotMoveUp = false;
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

    public boolean isMustMoveUpActive() {
        /*@predicate
         * it asks if mustMoveUp malus is active
         */
        return mustMoveUp;
    }

    public boolean isCannotMoveUpActive() {
        /*@predicate
         * it asks if cannotMoveUp malus is active
         */
        return cannotMoveUp;
    }
}