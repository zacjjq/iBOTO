package iboto.model;

public class Candidate {
    private final String id;
    private final Position position;
    private String name;
    private String party;

    public Candidate(String id, Position position, String name, String party) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.party = party;
    }

    public String getId() { return id; }
    public Position getPosition() { return position; }
    public String getName() { return name; }
    public String getParty() { return party; }
    public void setName(String name) { this.name = name; }
    public void setParty(String party) { this.party = party; }
}
