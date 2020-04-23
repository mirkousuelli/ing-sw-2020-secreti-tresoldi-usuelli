package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.message.payload.ReducedWorker;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.God;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CLITest {

    public static void main(String[] args) throws IOException {
        new Test<>().run();
    }

    static class Test<S> {
        void run() throws IOException {
            //initialize objects
            List<ReducedPlayer> reducedPlayers = new ArrayList<>();
            List<Player> players = new ArrayList<>();

            players.add(new Player("Fabio"));
            reducedPlayers.add(new ReducedPlayer(players.get(0), "blue"));

            ClientConnection<S> clientConnection = new ClientConnectionSocket<S>("127.0.0.1", 12345);
            ClientView<S> clientView = new CLI<S>(reducedPlayers.get(0), clientConnection);
            ClientModel<S> clientModel = new ClientModel<S>(clientView.getPlayer());

            //set up observers
            clientConnection.addObserver(clientModel);
            clientModel.addObserver(clientView);
            clientView.addObserver(clientConnection);


            Answer<S> answer;
            //---sim match---
            //start
            players.add(new Player("Febs"));
            players.add(new Player("Fabbbbbbio"));

            reducedPlayers.add(new ReducedPlayer(players.get(1), "yellow"));
            reducedPlayers.add(new ReducedPlayer(players.get(2), "red"));

            answer = (Answer<S>) new Answer<>(AnswerType.START, DemandType.CHOOSE_DECK, reducedPlayers); //start 1-n
            clientConnection.notify(answer);

            List<God> god = new ArrayList<>();
            god.add(God.APOLLO);
            god.add(God.PERSEPHONE);
            god.add(God.HESTIA);
            god.add(God.HEPHAESTUS);

            //choose deck
            answer = (Answer<S>) new Answer<>(AnswerType.SUCCESS, DemandType.CHOOSE_CARD, god); //choose deck 1-1
            clientConnection.notify(answer);

            //choose card
            god = new ArrayList<>();
            god.add(God.APOLLO);
            answer = (Answer<S>) new Answer<>(AnswerType.SUCCESS, DemandType.PLACE_WORKERS, god); //choose card 1-n
            clientConnection.notify(answer);

            //place workers
            List<ReducedWorker> reducedWorkers = new ArrayList<>();
            List<Worker> workers = new ArrayList<>();
            Board board = new Board();

            Block worker1Player2 = (Block) board.getCell(1, 3);
            Block worker1Player3 = (Block) board.getCell(2, 2);

            workers.add(new Worker(players.get(1), worker1Player2));
            workers.add(new Worker(players.get(2), worker1Player3));

            reducedWorkers.add(new ReducedWorker(workers.get(0), players.get(1).nickName));
            reducedWorkers.add(new ReducedWorker(workers.get(1), players.get(2).nickName));


            answer = (Answer<S>) new Answer<>(AnswerType.SUCCESS, DemandType.CHOOSE_WORKER, reducedWorkers); //place workers 1-n
            clientConnection.notify(answer);


            //place workers
            Block worker1Player1 = (Block) board.getCell(0, 0);
            Block worker2Player1 = (Block) board.getCell(0, 1);

            workers.add(new Worker(players.get(0), worker1Player1));
            workers.add(new Worker(players.get(0), worker2Player1));

            reducedWorkers.add(new ReducedWorker(workers.get(2), players.get(0).nickName));
            reducedWorkers.add(new ReducedWorker(workers.get(3), players.get(0).nickName));

            answer = (Answer<S>) new Answer<>(AnswerType.SUCCESS, DemandType.MOVE, reducedWorkers);//choose worker 1-1;
            clientConnection.notify(answer);


            //move
            List<ReducedCell> cells = new ArrayList<>();
            cells.add(new ReducedCell(0,0, Level.GROUND, ReducedAction.DEFAULT, workers.get(2), players.get(0)));
            cells.add(new ReducedCell(0,1, Level.GROUND, ReducedAction.DEFAULT, workers.get(3), players.get(0)));
            cells.add(new ReducedCell(1,0, Level.TOP, ReducedAction.DEFAULT, null, null));
            cells.add(new ReducedCell(1,1, Level.GROUND, ReducedAction.MOVE, null, null));


            answer = (Answer<S>) new Answer<>(AnswerType.SUCCESS, DemandType.BUILD, cells);
            clientConnection.notify(answer);
        }
    }
}