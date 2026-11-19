package iboto.model;

/** INHERITANCE: an Admin is a Person with a login. Demo credentials only, not real security. */
public class Admin extends Person {
    private final String username;
    private final String password;

    public Admin(String id, String name, String username, String password) {
        super(id, name);
        this.username = username;
        this.password = password;
    }

    public boolean authenticate(String user, String pass) {
        return username.equals(user) && password.equals(pass);
    }

    @Override
    public String getRole() { return "Administrator"; }
}
