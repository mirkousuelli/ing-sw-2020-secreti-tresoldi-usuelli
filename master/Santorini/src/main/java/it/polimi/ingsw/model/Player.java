package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Worker;

public class Player {
    public final String nickName;
    public final Worker[] worker;
    private Card card;
    private Worker currentWorker;

    public Player(String nickName) {
        /*
         *
         * */
        this.nickName = nickName;
        worker = new Worker[2];
    }

    public boolean move(Cell cell) {
        /*
         *
         * */
        return true;
    }

    public boolean build(Cell cell) {
        /*
         *
         * */
        return true;
    }

    public boolean useCard(Cell cell) {
        /*
         *
         * */
        return true;
    }

    public Worker getCurrentWorker() {
        return currentWorker;
    }

    public void setCurrentWorker(Worker currentWorker) {
        this.currentWorker = currentWorker;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) { this.card = card; }
}