package service;

import shared.ICoordinates;
import shared.common.AdditionStatus;
import shared.common.ShotStatus;

import java.util.ArrayList;

public interface IFieldShips {
     AdditionStatus addShip(ArrayList<ICoordinates> coordinates);
     ShotStatus shot(ICoordinates coordinate);
}
