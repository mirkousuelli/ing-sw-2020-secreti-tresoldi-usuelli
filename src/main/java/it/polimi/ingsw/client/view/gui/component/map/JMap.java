package it.polimi.ingsw.client.view.gui.component.map;

import it.polimi.ingsw.client.view.gui.component.JWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class JMap extends JPanel implements ActionListener {
    public final static int DIM = 5;
    private JCell[][] cellButton;
    private List<JCell> activeCells;
    private JWorker currentWorker;

    public JMap() {
        super(new GridBagLayout());

        setOpaque(false);
        setVisible(true);

        activeCells = new ArrayList<>();
        cellButton = new JCell[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = i;
                c.gridy = DIM - j - 1;
                c.fill = GridBagConstraints.BOTH;
                c.weighty = 1;
                c.weightx = 1;
                cellButton[i][j] = new JBlockDecorator(new JBlock(i, j));
                cellButton[i][j].addActionListener(this);
                add(cellButton[i][j], c);
            }
        }
    }

    public void setCurrentWorker(JWorker worker) {
        this.currentWorker = worker;
    }

    public JWorker getCurrentWorker() {
        return currentWorker;
    }

    public void setAround(List<JCell> where, JCellStatus how) {
        activeCells.clear();
        for (JCell cell : where) {
            activeCells.add(cell);
            ((JBlockDecorator) cell).addDecoration(how);
        }
    }

    public void setPossibleMove(List<JCell> where) {
        setAround(where, JCellStatus.MOVE);
    }

    public void setPossibleBuild(List<JCell> where) {
        setAround(where, JCellStatus.BUILD);
    }

    public void setPossibleUsePower(List<JCell> where) {
        setAround(where, JCellStatus.USE_POWER);
    }

    public void setPossibleMalus(List<JCell> where) {
        setAround(where, JCellStatus.MALUS);
    }

    public void moveWorker(JCell where) {
        currentWorker.setLocation(where);
    }

    public JCell getCell(int x, int y) {
        return cellButton[x][y];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCell src = (JCell) e.getSource();
        switch (src.getName()) {
            case "cell":
                JCellStatus status = ((JBlockDecorator) src).getDecoration();
                if (!status.equals(JCellStatus.NONE)) {

                    for (JCell cell : activeCells)
                            ((JBlockDecorator) cell).clean();

                    if (status.equals(JCellStatus.BUILD))
                        ((JBlockDecorator) src).buildUp();
                    else if (status.equals(JCellStatus.MOVE))
                        moveWorker(src);

                    validate();
                    repaint();
                }
                break;
        }
    }
}
