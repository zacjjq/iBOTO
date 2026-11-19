package iboto.model;

/** ABSTRACTION: common data for anyone who uses the system. Subclasses decide their own role. */
public abstract class Person {
    private final String id;
    private final String name;

    protected Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    /** Each subclass answers differently (POLYMORPHISM). */
    public abstract String getRole();

    public String describe() { return name + " (" + getRole() + ")"; }
}
