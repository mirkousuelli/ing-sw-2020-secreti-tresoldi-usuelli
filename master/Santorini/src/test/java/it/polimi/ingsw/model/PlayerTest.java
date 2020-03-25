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

import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    //@Test
    void testInitializeWorkerPosition() {
        Player Player1Test = new Player("Pl1");
        Player Player2Test = new Player("Pl2");
        Board boardTest = new Board();


        Player1Test.initializeWorkerPosition(1, (Block) boardTest.getCell(0, 0));
        Player1Test.initializeWorkerPosition(2, (Block) boardTest.getCell(5, 0));

        Player2Test.initializeWorkerPosition(1, (Block) boardTest.getCell(3, 2));
        Player2Test.initializeWorkerPosition(2, (Block) boardTest.getCell(4, 0));
    }

    //@Test
    void testBuild() {
    }
}