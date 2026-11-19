package iboto.model;

/** INHERITANCE: a Voter is a Person who can vote exactly once. */
public class Voter extends Person {
    private boolean voted;

    public Voter(String id, String name, boolean voted) {
        super(id, name);
        this.voted = voted;
    }

    public boolean hasVoted() { return voted; }

    /** ENCAPSULATION: the flag is private; only this method can switch it, and never back. */
    public void markVoted() { voted = true; }

    @Override
    public String getRole() { return "Voter"; }
}
