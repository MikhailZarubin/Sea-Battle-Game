package shared;

import shared.common.CellState;

public interface ICondition {
    boolean checkCondition(CellState cellState);
}
