package shared.common;

public class Util {
    public static int getMaxShipCount() {
        return ShipCount.ONE_DECK_SHIPS_COUNT + ShipCount.TWO_DECK_SHIPS_COUNT + ShipCount.THREE_DECK_SHIPS_COUNT + ShipCount.FOUR_DECK_SHIPS_COUNT;
    }

    public static ShipType mapShipTypeFromLength(int shipLength) {
        return switch (shipLength) {
            case (ShipLength.ONE_DECK_SHIPS_LENGTH) -> ShipType.ONE_DECK_SHIPS_TYPE;
            case (ShipLength.TWO_DECK_SHIPS_LENGTH) -> ShipType.TWO_DECK_SHIPS_TYPE;
            case (ShipLength.THREE_DECK_SHIPS_LENGTH) -> ShipType.THREE_DECK_SHIPS_TYPE;
            case (ShipLength.FOUR_DECK_SHIPS_LENGTH) -> ShipType.FOUR_DECK_SHIPS_TYPE;
            default -> null;
        };
    }

    public static int getMaxCountShipByType(ShipType shipType) {
        return switch (shipType) {
            case ONE_DECK_SHIPS_TYPE -> ShipCount.ONE_DECK_SHIPS_COUNT;
            case TWO_DECK_SHIPS_TYPE -> ShipCount.TWO_DECK_SHIPS_COUNT;
            case THREE_DECK_SHIPS_TYPE -> ShipCount.THREE_DECK_SHIPS_COUNT;
            case FOUR_DECK_SHIPS_TYPE -> ShipCount.FOUR_DECK_SHIPS_COUNT;
        };
    }

    private static class ShipLength {
        static final int ONE_DECK_SHIPS_LENGTH = 1;
        static final int TWO_DECK_SHIPS_LENGTH = 2;
        static final int THREE_DECK_SHIPS_LENGTH = 3;
        static final int FOUR_DECK_SHIPS_LENGTH = 4;
    }

    private static class ShipCount {
        static final int FOUR_DECK_SHIPS_COUNT = 1;
        static final int THREE_DECK_SHIPS_COUNT = 2;
        static final int TWO_DECK_SHIPS_COUNT = 3;
        static final int ONE_DECK_SHIPS_COUNT = 4;
    }
}
