package client.ui;

import shared.common.CellState;
import shared.ICondition;
import shared.ICoordinates;

import java.awt.*;
import java.io.Serializable;

public interface ISeaBattleUI extends Serializable {
    void showLog(String logText);
    void setConfigButtonText(String text);
    void applyCellState(ICoordinates coordinate, CellState newCellState);
    void applyCellColor(ICoordinates coordinate, Color color);
    void changeVisibleConfigButton(boolean isVisible);
    void enabledContent();
    void disabledContent();
    void updateCellColorByStateCondition(Color newColor, ICondition condition);
    void updateAllCellState(CellState cellState);
    void updateAllCellColor(Color color);
}
