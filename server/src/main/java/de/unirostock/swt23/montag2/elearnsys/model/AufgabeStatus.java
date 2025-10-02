package de.unirostock.swt23.montag2.elearnsys.model;

public enum AufgabeStatus {

    BEFORE,
    AFTER,
    BETWEEN,
    PRIVATE;

    @Override
    public String toString() {
        switch (this) {
            case BEFORE:
                return "noch nicht bearbeitbar";
            case AFTER:
                return "nicht mehr bearbeitbar";
            case BETWEEN:
                return "bearbeitbar";
            case PRIVATE:
                return "privat";
            default:
                return super.toString();
        }
    }
}
