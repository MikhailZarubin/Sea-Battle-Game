package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISeaBattleClientToService extends Remote {
    void setId(Integer clientId) throws RemoteException;
    Integer getId() throws RemoteException;
    void gameStarted() throws RemoteException;
    void enabledField() throws RemoteException;
    void disabledField() throws RemoteException;
    void youWin() throws RemoteException;
    void youLose() throws RemoteException;
}
