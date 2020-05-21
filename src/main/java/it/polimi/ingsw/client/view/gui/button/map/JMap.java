package it.polimi.ingsw.client.view.gui.button.map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class JMap extends JPanel implements ActionListener {
    public final static int DIM = 5;
    private JCell[][] cellButton;

    public JMap() {
        super(new GridBagLayout());

        setOpaque(false);
        setVisible(true);

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
                //cellButton[i][j].addActionListener(e -> ((JBlockDecorator)e.getSource()).buildUp());
                cellButton[i][j].addActionListener(this);
                add(cellButton[i][j], c);
            }
        }
    }

    public void setAround(List<JCell> where, JCellStatus how) {
        for (JCell cell : where) {
            ((JBlockDecorator) cell).addDecoration(how);
        }
    }

    public void possibleMove(List<JCell> where) {
        setAround(where, JCellStatus.MOVE);
    }

    public void possibleBuild(List<JCell> where) {
        setAround(where, JCellStatus.BUILD);
    }

    public void possibleUsePower(List<JCell> where) {
        setAround(where, JCellStatus.USE_POWER);
    }

    public void possibleMalus(List<JCell> where) {
        setAround(where, JCellStatus.MALUS);
    }

    public void setPlayer(JCell where, JCellStatus who) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (((JBlockDecorator) cellButton[i][j]).containsDecoration(who)) {
                    ((JBlockDecorator) cellButton[i][j]).removeDecoration(who);
                }
            }
        }
        ((JBlockDecorator) where).addDecoration(who);
    }

    public JCell getCell(int x, int y) {
        return cellButton[x][y];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCell src = (JCell) e.getSource();
        switch (src.getName()) {
            case "cell":
                JCellStatus status = ((JBlockDecorator) src).getCurrentDecoration();
                if (!status.equals(JCellStatus.NONE)) {
                    for (int i = 0; i < DIM; i++)
                        for (int j = 0; j < DIM; j++)
                            ((JBlockDecorator) cellButton[i][j]).clean();

                    if (status.equals(JCellStatus.BUILD))
                        ((JBlockDecorator) src).buildUp();
                    else if (status.equals(JCellStatus.MOVE))
                        setPlayer(src, JCellStatus.PLAYER_3_FEMALE);
                }
                break;
        }
    }
}
