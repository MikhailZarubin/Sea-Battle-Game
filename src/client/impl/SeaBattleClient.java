package client.impl;

import shared.common.CellState;
import client.ISeaBattleClientToService;
import client.ISeaBattleClientToUI;
import client.ui.ISeaBattleUI;
import service.ISeaBattleService;
import service.impl.SeaBattleService;
import shared.impl.Coordinates;
import shared.ICoordinates;
import shared.common.AdditionStatus;

import java.awt.*;
import java.io.Serial;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class SeaBattleClient extends UnicastRemoteObject implements ISeaBattleClientToService, ISeaBattleClientToUI {
    @Serial
    private static final long serialVersionUID = 1L;
    private ISeaBattleUI mSeaBattleUI;
    private ISeaBattleService mSeaBattleService;
    private Integer mId;
    private final ArrayList<ICoordinates> mShipPosition;
    private final ArrayList<ICoordinates> mHurtShipPosition;
    private boolean mIsGameStarted;
    private boolean mIsGameFinished;

    public SeaBattleClient(ISeaBattleUI seaBattleUI) throws RemoteException {
        super();
        mId = null;
        mSeaBattleUI = seaBattleUI;
        mShipPosition = new ArrayList<>();
        mHurtShipPosition = new ArrayList<>();
        mIsGameStarted = false;
        mIsGameFinished = false;
        mSeaBattleUI.setConfigButtonText(Constant.ADD_SHIP);
        connectToService();
    }

    private void connectToService() {
        try {
            System.setProperty(SeaBattleService.ConfigParams.RMI_HOST_NAME, SeaBattleService.ConfigParams.LOCAL_HOST);
            mSeaBattleService = (ISeaBattleService) Naming.lookup(SeaBattleService.ConfigParams.PATH);
            if (mSeaBattleService.connect(this)) {
                mSeaBattleUI.showLog(Constant.CONNECTING_SUCCESS);
            } else {
                mSeaBattleUI.showLog(Constant.CONNECTING_ERROR);
            }
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setId(Integer id) throws RemoteException {
        mId = id;
    }

    @Override
    public Integer getId() throws RemoteException {
        return mId;
    }

    @Override
    public void gameStarted() throws RemoteException {
        mIsGameStarted = true;
    }

    @Override
    public void enabledField() throws RemoteException {
        mSeaBattleUI.updateCellColorByStateCondition(Constant.COLOR_ENABLED_FIELD, (cellState -> !cellState.equals(CellState.OPENED)));
        mSeaBattleUI.enabledContent();
    }

    @Override
    public void disabledField() throws RemoteException {
        mSeaBattleUI.updateCellColorByStateCondition(Constant.COLOR_DISABLED_FIELD, (cellState -> !cellState.equals(CellState.OPENED)));
        mSeaBattleUI.disabledContent();
    }

    @Override
    public void youWin() throws RemoteException {
        mIsGameFinished = true;
        mSeaBattleUI.showLog(Constant.YOU_WIN);
        mSeaBattleUI.setConfigButtonText(Constant.RESTORE);
        mSeaBattleUI.changeVisibleConfigButton(true);
        mSeaBattleUI.enabledContent();
        mSeaBattleUI.updateCellColorByStateCondition(Constant.COLOR_SUCCESSFULLY_ADDED_SHIP, (cellState) -> cellState.equals(CellState.SHIP));
    }

    @Override
    public void youLose() throws RemoteException {
        mIsGameFinished = true;
        mSeaBattleUI.showLog(Constant.YOU_LOSE);
        mSeaBattleUI.setConfigButtonText(Constant.RESTORE);
        mSeaBattleUI.changeVisibleConfigButton(true);
        mSeaBattleUI.enabledContent();
    }

    @Override
    public void handleClickCell(Coordinates coordinate, CellState cellState) {
        if (!mIsGameStarted && cellState.equals(CellState.EMPTY)) {
            mShipPosition.add(coordinate);
            mSeaBattleUI.applyCellColor(coordinate, Constant.COLOR_BEING_ADDED_SHIP);
            mSeaBattleUI.applyCellState(coordinate, CellState.SELECTED);
        }
        if (mIsGameStarted && !cellState.equals(CellState.OPENED)) {
            try {
                switch (mSeaBattleService.shot(mId, coordinate)) {
                    case FAIL -> mSeaBattleUI.applyCellColor(coordinate, Constant.COLOR_EMPTY_CELL);

                    case HURT -> {
                        mHurtShipPosition.add(coordinate);
                        mSeaBattleUI.applyCellColor(coordinate, Constant.COLOR_HURT_SHIP);
                    }
                    case KILL, FULL_KILL -> {
                        mHurtShipPosition.add(coordinate);
                        for (ICoordinates _coordinate : mHurtShipPosition) {
                            mSeaBattleUI.applyCellColor(_coordinate, Constant.COLOR_KILLED_SHIP);
                        }
                        mHurtShipPosition.clear();
                    }
                }
                mSeaBattleUI.applyCellState(coordinate, CellState.OPENED);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleClickConfigButton() {
        if (!mIsGameFinished) {
            try {
                switch (mSeaBattleService.addShip(mId, mShipPosition)) {
                    case SUCCESS -> {
                        mSeaBattleUI.showLog(AdditionStatus.SUCCESS.getDescription());
                        for (ICoordinates coordinate : mShipPosition) {
                            mSeaBattleUI.applyCellColor(coordinate, Constant.COLOR_SUCCESSFULLY_ADDED_SHIP);
                            mSeaBattleUI.applyCellState(coordinate, CellState.SHIP);
                        }
                    }
                    case FULL_SUCCESS -> {
                        mSeaBattleUI.showLog(AdditionStatus.FULL_SUCCESS.getDescription());
                        for (ICoordinates coordinate : mShipPosition) {
                            if (!mIsGameStarted) {
                                mSeaBattleUI.applyCellColor(coordinate, Constant.COLOR_SUCCESSFULLY_ADDED_SHIP);
                            }
                            mSeaBattleUI.applyCellState(coordinate, CellState.SHIP);
                        }
                        mSeaBattleUI.changeVisibleConfigButton(false);
                    }
                    case INCORRECT_LENGTH -> {
                        mSeaBattleUI.showLog(AdditionStatus.INCORRECT_LENGTH.getDescription());
                        for (ICoordinates coordinate : mShipPosition) {
                            mSeaBattleUI.applyCellColor(coordinate, Constant.COLOR_EMPTY_CELL);
                            mSeaBattleUI.applyCellState(coordinate, CellState.EMPTY);
                        }
                    }
                    case INVALID_POSITION -> {
                        mSeaBattleUI.showLog(AdditionStatus.INVALID_POSITION.getDescription());
                        for (ICoordinates coordinate : mShipPosition) {
                            mSeaBattleUI.applyCellColor(coordinate, Constant.COLOR_EMPTY_CELL);
                            mSeaBattleUI.applyCellState(coordinate, CellState.EMPTY);
                        }
                    }
                    case EXCEED_COUNT_SHIPS_BY_TYPE -> {
                        mSeaBattleUI.showLog(AdditionStatus.EXCEED_COUNT_SHIPS_BY_TYPE.getDescription());
                        for (ICoordinates coordinate : mShipPosition) {
                            mSeaBattleUI.applyCellColor(coordinate, Constant.COLOR_EMPTY_CELL);
                            mSeaBattleUI.applyCellState(coordinate, CellState.EMPTY);
                        }
                    }
                }
                mShipPosition.clear();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            mIsGameFinished = false;
            mIsGameStarted = false;
            mSeaBattleUI.setConfigButtonText(Constant.ADD_SHIP);
            mSeaBattleUI.updateAllCellColor(Constant.COLOR_EMPTY_CELL);
            mSeaBattleUI.updateAllCellState(CellState.EMPTY);
            try {
                mSeaBattleService.restore(mId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleCloseFrame() {
        try {
            if (mSeaBattleService != null && mSeaBattleService.disconnect(this)) {
                mSeaBattleUI.showLog(Constant.DISCONNECTING_SUCCESS);
            } else {
                mSeaBattleUI.showLog(Constant.DISCONNECTING_ERROR);
            }
            mSeaBattleUI = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static class Constant {
        static final String CONNECTING_SUCCESS = "Connecting success";
        static final String CONNECTING_ERROR = "Connecting error";
        static final String DISCONNECTING_SUCCESS = "Disconnecting success";
        static final String DISCONNECTING_ERROR = "Disconnecting error";
        static final String YOU_WIN = "You winner. Congratulation!";
        static final String YOU_LOSE = "You loser.";
        static final String ADD_SHIP = "Add";
        static final String RESTORE = "Restore";
        static final Color COLOR_DISABLED_FIELD = Color.darkGray;
        static final Color COLOR_ENABLED_FIELD = Color.lightGray;
        static final Color COLOR_HURT_SHIP = Color.red;
        static final Color COLOR_KILLED_SHIP = Color.black;
        static final Color COLOR_BEING_ADDED_SHIP = Color.yellow;
        static final Color COLOR_SUCCESSFULLY_ADDED_SHIP = Color.green;
        static final Color COLOR_EMPTY_CELL = Color.white;
    }
}
