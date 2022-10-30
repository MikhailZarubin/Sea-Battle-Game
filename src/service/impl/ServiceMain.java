package service.impl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServiceMain {
    public static void main(String[] args) {
        try {
            System.setProperty(SeaBattleService.ConfigParams.RMI_HOST_NAME, SeaBattleService.ConfigParams.LOCAL_HOST);
            SeaBattleService seaBattleService = new SeaBattleService();

            Registry registry = LocateRegistry.createRegistry(SeaBattleService.ConfigParams.PORT);
            registry.rebind(SeaBattleService.ConfigParams.SERVICE_NAME, seaBattleService);

            System.out.println("Start " + SeaBattleService.ConfigParams.SERVICE_NAME);
        } catch (RemoteException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}