package service;

import client.ISeaBattleClientToService;
import shared.ICoordinates;
import shared.common.AdditionStatus;
import shared.common.ShotStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ISeaBattleService extends Remote {
    boolean connect(ISeaBattleClientToService connectingClient) throws RemoteException;
    boolean disconnect(ISeaBattleClientToService disconnectingClient) throws RemoteException;
    void restore(Integer clientId) throws RemoteException;
    AdditionStatus addShip(Integer clientId, ArrayList<ICoordinates> coordinates) throws RemoteException;
    ShotStatus shot(Integer clientId, ICoordinates coordinate) throws RemoteException;
}
