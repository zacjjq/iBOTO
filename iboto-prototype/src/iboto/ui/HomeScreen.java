package iboto.ui;

import java.awt.Font;

public class HomeScreen extends Screen {
    public HomeScreen(IbotoApp app) {
        add(Theme.label("iBOTO!", Font.BOLD, 68, Theme.RED));
        add(Theme.label("Digital Voting System", Font.BOLD, 26, Theme.NAVY));
        add(Theme.gap(10));
        add(Theme.text("Cast your Barangay vote digitally."));
        add(Theme.gap(26));
        add(Theme.row(Theme.primary("I'm a Voter", app::showVoterLogin), Theme.outline("Admin", app::showAdminLogin)));
        add(Theme.gap(34));
        add(Theme.label("Academic prototype - sample data only", Font.PLAIN, 12, Theme.GRAY));
    }
}
