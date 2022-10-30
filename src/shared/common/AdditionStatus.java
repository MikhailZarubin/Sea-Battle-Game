package shared.common;

import java.io.Serializable;

public enum AdditionStatus implements Serializable {
    FULL_SUCCESS("Ship added success. Field filled"),
    SUCCESS("Ship added success"),
    INVALID_POSITION("Incorrect position of the ship"),
    INCORRECT_LENGTH("Incorrect length of the ship"),
    EXCEED_COUNT_SHIPS_BY_TYPE("Max count of ships this type has been reached");

    private final String mDescriptionStatus;
    AdditionStatus(String descriptionStatus) {
        mDescriptionStatus = descriptionStatus;
    }
    public String getDescription() {
        return mDescriptionStatus;
    }
}
