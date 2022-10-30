package service.impl;

import shared.common.*;
import shared.ICondition;
import service.IFieldShips;
import shared.ICoordinates;
import shared.impl.Coordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FieldShips implements IFieldShips {
    private final CellState[][] mFieldShips = new CellState[Constant.FIELD_SIZE][Constant.FIELD_SIZE];
    private int mCurrentShipCount = 0;
    private final Map<ShipType, Integer> mCurrentShipsCountByType = new HashMap<>();

    public FieldShips() {
        mCurrentShipsCountByType.put(ShipType.ONE_DECK_SHIPS_TYPE, 0);
        mCurrentShipsCountByType.put(ShipType.TWO_DECK_SHIPS_TYPE, 0);
        mCurrentShipsCountByType.put(ShipType.THREE_DECK_SHIPS_TYPE, 0);
        mCurrentShipsCountByType.put(ShipType.FOUR_DECK_SHIPS_TYPE, 0);

        for (int i = 0; i < Constant.FIELD_SIZE; i++) {
            for (int j = 0; j < Constant.FIELD_SIZE; j++) {
                mFieldShips[i][j] = CellState.EMPTY;
            }
        }
    }

    @Override
    public AdditionStatus addShip(ArrayList<ICoordinates> coordinates) {
        ShipType shipType = Util.mapShipTypeFromLength(coordinates.size());
        if (shipType != null) {
            if (!checkValidCountShipsByType(shipType)) {
                return AdditionStatus.EXCEED_COUNT_SHIPS_BY_TYPE;
            }

            if (!checkValidCoordinates(coordinates) || !checkValidPosition(coordinates)) {
                return AdditionStatus.INVALID_POSITION;
            }
        } else {
            return AdditionStatus.INCORRECT_LENGTH;
        }

        addShipOnField(coordinates);
        mCurrentShipsCountByType.put(shipType, mCurrentShipsCountByType.get(shipType) + 1);
        mCurrentShipCount++;
        return mCurrentShipCount == Util.getMaxShipCount() ? AdditionStatus.FULL_SUCCESS : AdditionStatus.SUCCESS;
    }


    @Override
    public ShotStatus shot(ICoordinates coordinate) {
        if (checkValidPoint(coordinate) && mFieldShips[coordinate.getX()][coordinate.getY()].equals(CellState.SHIP)) {
            mFieldShips[coordinate.getX()][coordinate.getY()] = CellState.HURT_SHIP;
            if (checkAliveShip(coordinate)) {
                return ShotStatus.HURT;
            } else {
                killShip(coordinate);
                mCurrentShipCount--;
                return mCurrentShipCount == 0 ? ShotStatus.FULL_KILL : ShotStatus.KILL;
            }
        }

        return ShotStatus.FAIL;
    }

    private void killShip(ICoordinates coordinate) {
        ICoordinates startPointShip = getStartPointShip(coordinate);
        if (checkHorizontalOrientationShip(coordinate)) {
            int startPointX = startPointShip.getX();
            for (; startPointX < Constant.FIELD_SIZE && !mFieldShips[startPointX][coordinate.getY()].equals(CellState.EMPTY); startPointX++) {
                mFieldShips[startPointX][coordinate.getY()] = CellState.KILLED_SHIP;
            }
        } else {
            int startPointY = startPointShip.getY();

            for (; startPointY < Constant.FIELD_SIZE && !mFieldShips[coordinate.getX()][startPointY].equals(CellState.EMPTY); startPointY++) {
                mFieldShips[coordinate.getX()][startPointY] = CellState.KILLED_SHIP;
            }
        }
    }

    private boolean checkAliveShip(ICoordinates coordinate) {
        ICoordinates startPointShip = getStartPointShip(coordinate);
        if (checkHorizontalOrientationShip(coordinate)) {
            int startPointX = startPointShip.getX();
            for (; startPointX < Constant.FIELD_SIZE; startPointX++) {
                if (mFieldShips[startPointX][coordinate.getY()].equals(CellState.EMPTY)) {
                    return false;
                }
                if (mFieldShips[startPointX][coordinate.getY()].equals(CellState.SHIP)) {
                    return true;
                }
            }
        } else {
            int startPointY = startPointShip.getY();

            for (; startPointY < Constant.FIELD_SIZE; startPointY++) {
                if (mFieldShips[coordinate.getX()][startPointY].equals(CellState.EMPTY)) {
                    return false;
                }
                if (mFieldShips[coordinate.getX()][startPointY].equals(CellState.SHIP)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkHorizontalOrientationShip(ICoordinates coordinate) {
        ICoordinates startPointShip = getStartPointShip(coordinate);

        if (startPointShip.getX() == coordinate.getX() && startPointShip.getY() == coordinate.getY()) {
            int rightX = coordinate.getX() + 1;
            return rightX < Constant.FIELD_SIZE &&
                    (mFieldShips[rightX][coordinate.getY()].equals(CellState.SHIP) || mFieldShips[rightX][coordinate.getY()].equals(CellState.HURT_SHIP));
        } else {
            return startPointShip.getX() < coordinate.getX();
        }
    }

    private ICoordinates getStartPointShip(ICoordinates coordinate) {
        int startPointX = coordinate.getX();
        while (startPointX - 1 >= 0 &&
                (mFieldShips[startPointX - 1][coordinate.getY()].equals(CellState.SHIP) || mFieldShips[startPointX - 1][coordinate.getY()].equals(CellState.HURT_SHIP))) {
            startPointX--;
        }

        int startPointY = coordinate.getY();
        while (startPointY - 1 >= 0 &&
                (mFieldShips[coordinate.getX()][startPointY - 1].equals(CellState.SHIP) || mFieldShips[coordinate.getX()][startPointY - 1].equals(CellState.HURT_SHIP))) {
            startPointY--;
        }

        return new Coordinates(startPointX, startPointY);
    }

    private boolean checkValidCountShipsByType(ShipType shipType) {
        return mCurrentShipsCountByType.get(shipType) != Util.getMaxCountShipByType(shipType);
    }

    private boolean checkValidCoordinates(ArrayList<ICoordinates> coordinates) {
        for (ICoordinates point : coordinates) {
            if (!checkValidPoint(point) || checkNeighborhoodByCondition(point, (cellState -> cellState.equals(CellState.SHIP)))) {
                return false;
            }
        }
        return true;
    }

    private boolean checkValidPoint(ICoordinates coordinate) {
        return coordinate.getX() >= 0 && coordinate.getX() < Constant.FIELD_SIZE &&
                coordinate.getY() >= 0 && coordinate.getY() < Constant.FIELD_SIZE;
    }

    private boolean checkValidPosition(ArrayList<ICoordinates> coordinates) {
        boolean valid = true;
        for (int i = 1; i < coordinates.size(); i++) {
            if ((coordinates.get(i).getX() != coordinates.get(i - 1).getX() && coordinates.get(i).getY() != coordinates.get(i - 1).getY()) ||
                    Math.abs(coordinates.get(i).getX() - coordinates.get(i - 1).getX()) > coordinates.size() - 1 ||
                    Math.abs(coordinates.get(i).getY() - coordinates.get(i - 1).getY()) > coordinates.size() - 1) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    private boolean checkNeighborhoodByCondition(ICoordinates coordinate, ICondition condition) {
        for (int x = coordinate.getX() - 1; x <= coordinate.getX() + 1; x++) {
            if (x >= 0 && x < Constant.FIELD_SIZE) {
                for (int y = coordinate.getY() - 1; y <= coordinate.getY() + 1; y++) {
                    if (y >= 0 && y < Constant.FIELD_SIZE) {
                        if (condition.checkCondition(mFieldShips[x][y])) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void addShipOnField(ArrayList<ICoordinates> coordinates) {
        for (ICoordinates coordinate : coordinates) {
            mFieldShips[coordinate.getX()][coordinate.getY()] = CellState.SHIP;
        }
    }

    public static class Constant {
        public static final int FIELD_SIZE = 10;
    }
}
