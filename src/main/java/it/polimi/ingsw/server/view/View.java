package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.communication.observer.Observer;

public abstract class View extends Observable<ActionToPerformView> implements Observer<Answer>, IView {

    private final String player;

    protected View(String player){
        this.player = player;
    }

    public String getPlayer(){
        return player;
    }

    public void processMessage(Demand demand){
        notify(new ActionToPerformView(player, demand, this));
    }

    protected abstract void showAnswer(Answer answer);

    public void reportError(Answer answer) {
        showAnswer(answer);
    }

    @Override
    public void update(Answer answer) {
        showAnswer(answer);
    }
}
