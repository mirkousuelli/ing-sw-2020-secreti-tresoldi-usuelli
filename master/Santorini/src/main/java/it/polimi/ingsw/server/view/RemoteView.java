package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.state.Game;

public class RemoteView extends View {

    private class MessageReceiver implements Observer<Message> {

        @Override
        public void update(Message message) {
            System.out.println("Received: " + message);
            try{
                //Choice choice = Choice.parseInput(message.getMessage());
                //processChoice(choice);
                processMessage(message);
            } catch (IllegalArgumentException e) {
                connection.send(new Message("Error! Make your move"));
            }
        }
    }

    private Connection connection;

    public RemoteView(Player player, String opponent, Connection c){
        super(player);
        this.connection = c;
        c.addObserver(new MessageReceiver());
        //c.asyncSend("Your opponent is: " + opponent + "\tMake your move");
        c.send(new Message("Your opponent is: " + opponent + "\tMake your move"));
    }

    @Override
    protected void showModel(Game model) {

    }
}
