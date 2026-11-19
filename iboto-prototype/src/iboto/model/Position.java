package iboto.model;

/** The elected positions on the ballot. */
public enum Position {
    CAPTAIN("Barangay Captain"),
    KAGAWAD("Kagawad"),
    SK_CHAIR("SK Chairperson");

    private final String label;

    Position(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
