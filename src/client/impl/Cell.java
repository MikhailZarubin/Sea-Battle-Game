package client.impl;

import shared.common.CellState;
import client.ICell;
import shared.impl.Coordinates;

import javax.swing.*;
import java.awt.*;

public class Cell extends JButton implements ICell {
    private final Coordinates mCoordinate;
    private CellState mCellState = CellState.EMPTY;

    public Cell(Coordinates coordinate) {
        super();
        mCoordinate = new Coordinates(coordinate);
    }

    @Override
    public void setState(CellState cellState) {
        mCellState = cellState;
    }

    @Override
    public CellState getState() {
        return mCellState;
    }

    @Override
    public Coordinates getCoordinate() {
        return mCoordinate;
    }

    @Override
    public void setColor(Color color) {
        setBackground(color);
    }
}
