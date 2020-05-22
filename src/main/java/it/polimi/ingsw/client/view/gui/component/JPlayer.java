package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.deck.JGod;
import it.polimi.ingsw.client.view.gui.component.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPlayer implements ActionListener {
    private final String nickname;
    private JGod god;
    private JWorker femaleWorker;
    private JWorker maleWorker;
    private boolean current;
    private boolean choose;

    public JPlayer(String nickname) {
        this.nickname = nickname;
        this.choose = false;
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

        this.femaleWorker.getLocation().addActionListener(this);
        this.maleWorker.getLocation().addActionListener(this);
    }

    public JWorker getFemaleWorker() {
        return this.femaleWorker;
    }

    public JWorker getMaleWorker() {
        return this.maleWorker;
    }

    public void chooseWorker() {
        ((JBlockDecorator) this.maleWorker.getLocation()).addDecoration(JCellStatus.CHOOSE_WORKER);
        ((JBlockDecorator) this.femaleWorker.getLocation()).addDecoration(JCellStatus.CHOOSE_WORKER);
        choose = true;
    }

    public JWorker getCurrentWorker() {
        return (current) ? maleWorker : femaleWorker;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (choose) {
            current = ((JBlockDecorator) e.getSource()).getWorker().equals(maleWorker.getPawn().getDecoration());
            ((JBlockDecorator) this.maleWorker.getLocation()).removeDecoration();
            ((JBlockDecorator) this.femaleWorker.getLocation()).removeDecoration();
            choose = false;
            System.out.println(getCurrentWorker().getPawn().getDecoration().path);
        }
    }
}
