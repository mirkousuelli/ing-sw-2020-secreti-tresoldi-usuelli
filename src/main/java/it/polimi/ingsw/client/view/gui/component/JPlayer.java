package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.deck.JGod;

public class JPlayer {
    private final String nickname;
    private JGod god;
    private JWorker femaleWorker;
    private JWorker maleWorker;

    public JPlayer(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setGod(JGod god) {
        this.god = god;
    }

    public JGod getGod() {
        return this.god;
    }

    public void setWorkers(JWorker female, JWorker male) {
        this.femaleWorker = female;
        this.maleWorker = male;
    }

    public JWorker getFemaleWorker() {
        return this.femaleWorker;
    }

    public JWorker getMaleWorker() {
        return this.maleWorker;
    }
}
