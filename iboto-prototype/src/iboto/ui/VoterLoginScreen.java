package iboto.ui;

import iboto.service.ElectionService;

import javax.swing.*;
import java.awt.Font;

/** Step 1: check that the Barangay ID is registered and has not voted yet. */
public class VoterLoginScreen extends Screen {
    public VoterLoginScreen(IbotoApp app) {
        ElectionService svc = app.service();
        JTextField id = Theme.field();
        JLabel error = Theme.label(" ", Font.PLAIN, 13, Theme.RED);

        Runnable verify = () -> {
            String value = id.getText().trim();
            if (value.isEmpty()) {
                error.setText("Please enter your Barangay ID.");
                return;
            }
            switch (svc.verify(value)) {
                case ELIGIBLE:
                    app.showFace(svc.findVoter(value));
                    break;
                case ALREADY_VOTED:
                    app.show(new StatusScreen(StatusScreen.Kind.WARNING, "You have already voted",
                            "Our records show this Barangay ID has already cast a ballot. Each voter can only vote once, so you cannot log in again.",
                            "Back to Home", app::showHome, null, null));
                    break;
                default:
                    app.show(new StatusScreen(StatusScreen.Kind.ERROR, "Barangay ID not eligible",
                            "We could not find this ID in the voter list for this Barangay. Please check the number or ask the election staff for help.",
                            "Try Again", app::showVoterLogin, "Home", app::showHome));
            }
        };
        id.setAlignmentX(LEFT_ALIGNMENT);
        id.addActionListener(e -> verify.run());

        JLabel fieldLabel = Theme.label("Barangay ID", Font.PLAIN, 13, Theme.NAVY);
        fieldLabel.setAlignmentX(LEFT_ALIGNMENT);
        JPanel form = Theme.column();
        form.add(fieldLabel);
        form.add(Theme.gap(6));
        form.add(id);

        add(Theme.title("Voter Log In"));
        add(Theme.gap(8));
        add(Theme.text("Step 1 of 2: Enter your Barangay ID to verify that you are eligible to vote."));
        add(Theme.gap(20));
        add(form);
        add(Theme.gap(6));
        add(error);
        add(Theme.gap(12));
        add(Theme.row(Theme.primary("Verify ID", verify), Theme.outline("Back", app::showHome)));
        add(Theme.gap(18));
        add(Theme.row(Theme.label("Demo:", Font.PLAIN, 13, Theme.GRAY),
                Theme.link("Eligible voter", () -> id.setText(svc.sampleVoterId(false))),
                Theme.link("Already voted", () -> id.setText(svc.sampleVoterId(true))),
                Theme.link("Not eligible", () -> id.setText("BRGY-2026-99999"))));
    }
}
