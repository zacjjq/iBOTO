package iboto.ui;

import iboto.model.Admin;
import iboto.service.ElectionService;

import javax.swing.*;
import java.awt.*;

/** Admin dashboard: Results, Candidates and Voters tabs. Tabs reload their data when opened. */
public class AdminScreen extends Screen {
    /** Implemented by each tab so it can reload from the service. */
    public interface Refreshable { void refresh(); }

    private final JTabbedPane tabs = new JTabbedPane();

    public AdminScreen(IbotoApp app, Admin admin) {
        ElectionService svc = app.service();
        setLayout(new BorderLayout(0, 14));
        setPreferredSize(new Dimension(880, 520));

        JPanel titles = Theme.column();
        JLabel title = Theme.title("Admin Dashboard");
        JLabel who = Theme.label("Logged in as " + admin.describe(), Font.PLAIN, 14, Theme.GRAY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        who.setAlignmentX(LEFT_ALIGNMENT);
        titles.add(title);
        titles.add(who);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(Theme.outline("Reset Demo Data", () -> resetDemo(svc)));
        buttons.add(Theme.primary("Log Out", app::showHome));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titles, BorderLayout.WEST);
        top.add(buttons, BorderLayout.EAST);

        tabs.setFont(Theme.font(Font.BOLD, 14));
        tabs.addTab("Results", new ResultsPanel(svc));
        tabs.addTab("Candidates", new CandidatesPanel(svc));
        tabs.addTab("Voters", new VotersPanel(svc));
        tabs.addChangeListener(e -> refreshSelected());

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void refreshSelected() {
        ((Refreshable) tabs.getSelectedComponent()).refresh();
    }

    private void resetDemo(ElectionService svc) {
        int choice = JOptionPane.showOptionDialog(this,
                "Reset the demo?\n\nAll ballots recorded so far will be deleted and the sample\nvoters and candidates will be restored. This cannot be undone.",
                "Reset Demo Data", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                new Object[]{"Reset Demo Data", "Cancel"}, "Cancel");
        if (choice == 0) {
            svc.resetDemo();
            refreshSelected();
            JOptionPane.showMessageDialog(this, "Demo data restored.", "Reset complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
