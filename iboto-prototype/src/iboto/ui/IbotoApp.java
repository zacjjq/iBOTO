package iboto.ui;

import iboto.data.FileDataStore;
import iboto.model.Admin;
import iboto.model.Candidate;
import iboto.model.Position;
import iboto.model.Voter;
import iboto.service.ElectionService;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.Map;

/** Main window: a red header plus one screen at a time. Screens ask this class to navigate. */
public class IbotoApp extends JFrame {
    private final ElectionService service;
    private final JPanel body = new JPanel(new GridBagLayout());

    public IbotoApp(ElectionService service) {
        super("iBOTO! Barangay Digital Voting System");
        Theme.install();
        this.service = service;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.RED);
        header.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        JLabel logo = new JLabel("iBOTO!");
        logo.setFont(Theme.font(Font.BOLD, 26));
        logo.setForeground(Color.WHITE);
        JLabel tagline = new JLabel("Barangay Digital Voting System");
        tagline.setFont(Theme.font(Font.PLAIN, 15));
        tagline.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.WEST);
        header.add(tagline, BorderLayout.EAST);

        body.setBackground(Color.WHITE);
        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
        setMinimumSize(new Dimension(940, 660));
        setSize(1100, 720);
        setLocationRelativeTo(null);
        showHome();
    }

    public ElectionService service() { return service; }

    public void show(JComponent screen) {
        body.removeAll();
        body.add(screen);
        body.revalidate();
        body.repaint();
    }

    public void showHome() { show(new HomeScreen(this)); }
    public void showVoterLogin() { show(new VoterLoginScreen(this)); }
    public void showFace(Voter voter) { show(new FaceScreen(this, voter)); }
    public void showBallot(Voter voter, Map<Position, Candidate> preset) { show(new BallotScreen(this, voter, preset)); }
    public void showReview(Voter voter, Map<Position, Candidate> picks) { show(new ReviewScreen(this, voter, picks)); }
    public void showAdminLogin() { show(new AdminLoginScreen(this)); }
    public void showAdmin(Admin admin) { show(new AdminScreen(this, admin)); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FileDataStore store = new FileDataStore(Paths.get("data"));
                ElectionService service;
                try {
                    service = new ElectionService(store);
                } catch (RuntimeException damaged) {
                    // a data file was edited by hand and can no longer be read: restore the sample data
                    store.resetToSampleData();
                    service = new ElectionService(store);
                }
                new IbotoApp(service).setVisible(true);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null, "iBOTO! could not start: " + e.getMessage(),
                        "Startup error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
