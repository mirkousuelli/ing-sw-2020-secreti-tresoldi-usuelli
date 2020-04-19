package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Cell;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CLIPrinterTest {

    @Test
    void testPrint() {
        SantoriniPrintStream out = new SantoriniPrintStream(System.out);

        //-----define objects------
        //^^^^^view^^^^^
        ReducedCell[][] reducedBoard = new ReducedCell[5][5];
        List<ReducedPlayer> reducedPlayerList = new ArrayList<>();
        List<ReducedPlayer> opponents = new ArrayList<>();


        //^^^^^^model^^^^^^
        List<Player> playerList = new ArrayList<>();
        Board board = new Board();

        //Pl1
        playerList.add(new Player("Pl1"));
        //Pl2
        playerList.add(new Player("Pl2"));
        //Pl3
        playerList.add(new Player("Pl3"));
        //-----end define objects------



        //-----initialize values-----
        //^^^^model^^^^^
        //define initial worker position
        //Pl1
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block worker2Player1 = (Block) board.getCell(3, 1);

        //Pl2
        Block worker1Player2 = (Block) board.getCell(2, 2);
        Block worker2Player2 = (Block) board.getCell(3, 3);

        //Pl3
        Block worker1Player3 = (Block) board.getCell(4, 1);
        Block worker2Player3 = (Block) board.getCell(0, 0);

        //set initial worker position
        //Pl1
        playerList.get(0).initializeWorkerPosition(1, worker1Player1);
        playerList.get(0).initializeWorkerPosition(2, worker2Player1);

        //Pl2
        playerList.get(1).initializeWorkerPosition(1, worker1Player2);
        playerList.get(1).initializeWorkerPosition(2, worker2Player2);

        //Pl3
        playerList.get(2).initializeWorkerPosition(1, worker1Player3);
        playerList.get(2).initializeWorkerPosition(2, worker2Player3);

        //set current worker
        //Pl1
        playerList.get(0).setCurrentWorker(playerList.get(0).getWorkers().get(0));
        //Pl2
        playerList.get(1).setCurrentWorker(playerList.get(1).getWorkers().get(1));
        //PL3
        playerList.get(2).setCurrentWorker(playerList.get(2).getWorkers().get(0));
        //^^^^end model^^^^^


        //^^^^view^^^^^
        //initialize reduce players and their colors
        //Pl1
        reducedPlayerList.add(new ReducedPlayer(playerList.get(0), "Red"));
        //Pl2
        reducedPlayerList.add(new ReducedPlayer(playerList.get(1), "yellow"));
        //Pl3
        reducedPlayerList.add(new ReducedPlayer(playerList.get(2), "GrEeN"));

        //initialize reduce board
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                reducedBoard[i][j] = new ReducedCell(board.getCell(i, j).getX(), board.getCell(i, j).getY());
                reducedBoard[i][j].setLevel(ReducedLevel.GROUND);
                reducedBoard[i][j].setAction(ReducedAction.DEFAULT);
            }
        }

        //initialize reduce workers
        //Pl1
        reducedBoard[worker1Player1.getX()][worker1Player1.getY()].setWorker(new ReducedWorker(playerList.get(0).getWorkers().get(0), playerList.get(0).nickName));
        reducedBoard[worker2Player1.getX()][worker2Player1.getY()].setWorker(new ReducedWorker(playerList.get(0).getWorkers().get(1), playerList.get(0).nickName));

        //Pl2
        reducedBoard[worker1Player2.getX()][worker1Player2.getY()].setWorker(new ReducedWorker(playerList.get(1).getWorkers().get(0), playerList.get(1).nickName));
        reducedBoard[worker2Player2.getX()][worker2Player2.getY()].setWorker(new ReducedWorker(playerList.get(1).getWorkers().get(1), playerList.get(1).nickName));

        //Pl3
        reducedBoard[worker1Player3.getX()][worker1Player3.getY()].setWorker(new ReducedWorker(playerList.get(2).getWorkers().get(0), playerList.get(2).nickName));
        reducedBoard[worker2Player3.getX()][worker2Player3.getY()].setWorker(new ReducedWorker(playerList.get(2).getWorkers().get(1), playerList.get(2).nickName));

        //initialize currentPlayer ==> Pl1 as current
        //set up action around chosen Pl1 worker ==> worker1Player1 ==> cell (1,1) ==> around = {(0,0) , (0,1), (0,2), (1,0), (1,2), (2,0), (2,1), (2,2)}
        //Hp: move not permitted in:
        //      (2,2) and (0,0) ==> occupied
        //      (0,1) ==> dome
        //      others ==> ok to move to
        //Hp: where permitted move but one usePower in (1,0)

        List<Cell> around = board.getAround(worker1Player1);
        for (Cell c : around) {
            if (c.getX() == 0 && c.getY() == 1)
                reducedBoard[0][1].setLevel(ReducedLevel.DOME);
            else if (c.getX() == 1 && c.getY() == 0) {
                reducedBoard[1][0].setAction(ReducedAction.USEPOWER);
            }
            else
                reducedBoard[c.getX()][c.getY()].setAction(ReducedAction.MOVE);
        }

        //initialize opponents list for Pl1
        for (ReducedPlayer p : reducedPlayerList) {
            if (!p.equals(reducedPlayerList.get(0)))
                opponents.add(p);
        }
        //-----end initialize values-----

        //----test static methods----
        CLIPrinter.printLogo(out);
        CLIPrinter.printString(out, "newLine false ", true);
        CLIPrinter.printString(out, "newLine true", true);
        CLIPrinter.printBoard(out, reducedBoard, reducedPlayerList);
        CLIPrinter.printOpponents(out, opponents);
        CLIPrinter.printPossibleActions(out, reducedBoard);
        //----end test static methods----
    }
}