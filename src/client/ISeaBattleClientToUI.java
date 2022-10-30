package client;

import shared.common.CellState;
import shared.impl.Coordinates;

public interface ISeaBattleClientToUI {
    void handleClickCell(Coordinates coordinate, CellState cellState);
    void handleClickConfigButton();
    void handleCloseFrame();
}
