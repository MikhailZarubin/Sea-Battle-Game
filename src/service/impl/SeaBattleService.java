package service.impl;

import client.ISeaBattleClientToService;
import service.ISeaBattleService;
import shared.ICoordinates;
import shared.common.AdditionStatus;
import service.IFieldShips;
import shared.common.ShotStatus;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SeaBattleService extends UnicastRemoteObject
        implements ISeaBattleService, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Map<Integer, ISeaBattleClientToService> mClientIdToClient = new HashMap<>();
    private final Map<Integer, IFieldShips> mClientIdToFieldMapper = new HashMap<>();
    private int mReadyClientCount = 0;
    private Integer mActiveClientId;
    private Integer mInactiveClientId;
    private boolean mIsGameFinished = false;

    public SeaBattleService() throws RemoteException {
        super();
    }

    @Override
    public boolean connect(ISeaBattleClientToService connectingClient) throws RemoteException {
        if (getPlayerCount() < Constants.PLAYER_COUNT) {
            int clientId ;

            if (mActiveClientId == null && mInactiveClientId == null) {
                clientId = 0;
                mActiveClientId = clientId;
            } else {
                if (mActiveClientId != null) {
                    clientId = 1 - mActiveClientId;
                    mInactiveClientId = clientId;
                } else {
                    clientId = 1 - mInactiveClientId;
                    mActiveClientId = clientId;
                }
            }

            System.out.println(Constants.CONNECTING_CLIENT + clientId);
            connectingClient.setId(clientId);

            mClientIdToFieldMapper.put(clientId, new FieldShips());
            mClientIdToClient.put(clientId, connectingClient);

            return true;
        }
        return false;
    }

    @Override
    public boolean disconnect(ISeaBattleClientToService disconnectingClient) throws RemoteException {
        if (disconnectingClient.getId() != null) {
            if (!mIsGameFinished) {
                mClientIdToClient.forEach((id, client) -> {
                    try {
                        if (!client.equals(disconnectingClient)) {
                            client.youWin();
                            System.out.println(Constants.GAME_FINISHED + client.getId());
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
            mReadyClientCount = 0;
            Integer clientId = disconnectingClient.getId();
            if (Objects.equals(mActiveClientId, disconnectingClient.getId())) {
                mActiveClientId = null;
            } else {
                mInactiveClientId = null;
            }
            System.out.println(Constants.DISCONNECTING_CLIENT + clientId);
            return mClientIdToClient.remove(clientId) != null &&
                    mClientIdToFieldMapper.remove(clientId) != null;
        } else {
            return false;
        }
    }

    @Override
    public AdditionStatus addShip(Integer clientId, ArrayList<ICoordinates> coordinates) throws RemoteException {
        AdditionStatus additionStatus = mClientIdToFieldMapper.get(clientId).addShip(coordinates);
        if (additionStatus.equals(AdditionStatus.FULL_SUCCESS)) {
            mReadyClientCount++;
            if (mReadyClientCount == Constants.PLAYER_COUNT) {
                configureGame();
            }
        }
        return additionStatus;
    }

    @Override
    public ShotStatus shot(Integer clientId, ICoordinates coordinate) throws RemoteException {
        ShotStatus shotStatus = mClientIdToFieldMapper.get(clientId).shot(coordinate);
        ISeaBattleClientToService activeClient = mClientIdToClient.get(mActiveClientId);
        ISeaBattleClientToService inactiveClient = mClientIdToClient.get(mInactiveClientId);

        if (shotStatus.equals(ShotStatus.FAIL)) {
            activeClient.enabledField();
            inactiveClient.disabledField();
            mActiveClientId = mActiveClientId ^ mInactiveClientId ^ (mInactiveClientId = mActiveClientId);
        }
        if (shotStatus.equals(ShotStatus.FULL_KILL)) {
            activeClient.youWin();
            inactiveClient.youLose();
            mReadyClientCount = 0;
            mIsGameFinished = true;
            System.out.println(Constants.GAME_FINISHED + mActiveClientId);
        }

        return shotStatus;
    }

    @Override
    public void restore(Integer clientId) throws RemoteException {
        mIsGameFinished = false;
        mClientIdToFieldMapper.remove(clientId);
        mClientIdToFieldMapper.put(clientId, new FieldShips());
    }

    private int getPlayerCount() {
        return mClientIdToFieldMapper.size();
    }

    private void configureGame() throws RemoteException {
        ISeaBattleClientToService activeClient = mClientIdToClient.get(mActiveClientId);
        ISeaBattleClientToService inactiveClient = mClientIdToClient.get(mInactiveClientId);
        activeClient.gameStarted();
        activeClient.disabledField();
        inactiveClient.gameStarted();
        inactiveClient.enabledField();
        System.out.println(Constants.GAME_STARTED);
    }

    private static class Constants {
        static final int PLAYER_COUNT = 2;
        static final String CONNECTING_CLIENT = "Connecting client ";
        static final String DISCONNECTING_CLIENT = "Disconnecting client ";
        static final String GAME_STARTED = "Game started";
        static final String GAME_FINISHED = "Game finished. Winner player ";
    }

    public static class ConfigParams {
        public static final String SERVICE_NAME = "SeaBattleService";
        public static final String LOCAL_HOST = "127.0.0.1";
        public static final String RMI_HOST_NAME = "java.rmi.server.hostname";
        public static final String PATH = "rmi://localhost/" + SERVICE_NAME;
        public static final int PORT = 1099;
    }
}
