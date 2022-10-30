package client;

import shared.common.CellState;
import shared.impl.Coordinates;

import java.awt.*;

public interface ICell {
    void setState(CellState cellState);
    CellState getState();
    Coordinates getCoordinate();
    void setColor(Color color);
}
