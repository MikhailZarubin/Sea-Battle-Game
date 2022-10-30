package client.ui.impl;

import client.*;
import client.impl.Cell;
import client.impl.SeaBattleClient;
import client.ui.ISeaBattleUI;
import shared.common.CellState;
import shared.ICondition;
import shared.impl.Coordinates;
import shared.ICoordinates;
import service.impl.FieldShips;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.rmi.RemoteException;

public class SeaBattleUI implements ISeaBattleUI {
    @Serial
    private static final long serialVersionUID = 1L;
    private ISeaBattleClientToUI mSeaBattleClient;
    private final JFrame mFrame;
    private final ICell[][] mCells;
    private final JButton mConfigButton;

    public SeaBattleUI() {
        mFrame = new JFrame();
        mCells = new Cell[FieldShips.Constant.FIELD_SIZE][FieldShips.Constant.FIELD_SIZE];
        mConfigButton = new JButton();
        configureUI();
        try {
            mSeaBattleClient = new SeaBattleClient(this);
        } catch (RemoteException ignored) {
        }
    }

    private void configureUI() {
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.setSize(Constant.FRAME_SIZE, Constant.FRAME_SIZE);
        mFrame.setResizable(false);
        mFrame.setLayout(new GridLayout(FieldShips.Constant.FIELD_SIZE + 1, FieldShips.Constant.FIELD_SIZE));

        for (int i = 0; i < FieldShips.Constant.FIELD_SIZE; i++) {
            for (int j = 0; j < FieldShips.Constant.FIELD_SIZE; j++) {
                Cell cell = new Cell(new Coordinates(i, j));
                cell.setBackground(Color.white);
                mCells[i][j] = cell;
                mFrame.add(cell);
                cell.addActionListener(actionEvent -> mSeaBattleClient.handleClickCell(cell.getCoordinate(), cell.getState()));
            }
        }

        mConfigButton.addActionListener(actionEvent -> mSeaBattleClient.handleClickConfigButton());
        mFrame.add(mConfigButton);

        mFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mSeaBattleClient.handleCloseFrame();
                super.windowClosing(e);
            }
        });
        mFrame.setVisible(true);
    }

    @Override
    public void applyCellState(ICoordinates coordinate, CellState newCellState) {
        mCells[coordinate.getX()][coordinate.getY()].setState(newCellState);
    }

    @Override
    public void applyCellColor(ICoordinates coordinate, Color color) {
        mCells[coordinate.getX()][coordinate.getY()].setColor(color);
    }

    @Override
    public void showLog(String logText) {
        System.out.println(logText);
    }

    @Override
    public void setConfigButtonText(String text) {
        mConfigButton.setText(text);
    }

    @Override
    public void enabledContent() {
        mFrame.setEnabled(true);
    }

    @Override
    public void disabledContent() {
        mFrame.setEnabled(false);
    }

    @Override
    public void changeVisibleConfigButton(boolean isVisible) {
        mConfigButton.setVisible(isVisible);
    }

    @Override
    public void updateCellColorByStateCondition(Color newColor, ICondition condition) {
        for (ICell[] cells : mCells) {
            for (ICell cell : cells) {
                if (condition.checkCondition(cell.getState())) {
                    cell.setColor(newColor);
                }
            }
        }
    }

    @Override
    public void updateAllCellState(CellState cellState) {
        for (ICell[] cells : mCells) {
            for (ICell cell : cells) {
                cell.setState(cellState);
            }
        }
    }

    @Override
    public void updateAllCellColor(Color color) {
        for (ICell[] cells : mCells) {
            for (ICell cell : cells) {
                cell.setColor(color);
            }
        }
    }

    private static class Constant {
        static final int FRAME_SIZE = 600;
    }
}
