package shared.impl;

import shared.ICoordinates;

import java.io.Serial;
import java.io.Serializable;

public class Coordinates implements ICoordinates, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int mX;
    private final int mY;

    public Coordinates(int x, int y) {
        mX = x;
        mY = y;
    }

    public Coordinates(Coordinates coordinate) {
        mX = coordinate.mX;
        mY = coordinate.mY;
    }

    @Override
    public int getX() {
        return mX;
    }

    @Override
    public int getY() {
        return mY;
    }
}
