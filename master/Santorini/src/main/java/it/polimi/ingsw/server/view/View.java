package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.state.Game;

public abstract class View extends Observable<Message> implements Observer<Answer> {

    private Player player;

    protected View(Player player){
        this.player = player;
    }

    protected Player getPlayer(){
        return player;
    }

    protected void processMessage(Message message){
        //notify(message);
    }

    protected abstract void showModel(Game model);


    @Override
    public void update(Answer answer) {
        showModel(answer);
    }
}
