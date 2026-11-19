package iboto.ui;

import iboto.model.Ballot;
import iboto.model.Candidate;
import iboto.model.Position;
import iboto.model.Voter;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/** Last chance to check the choices. Submitting records the ballot and locks the voter out. */
public class ReviewScreen extends Screen {
    private final JButton submit;

    public ReviewScreen(IbotoApp app, Voter voter, Map<Position, Candidate> picks) {
        submit = Theme.primary("Submit Vote", () -> submitVote(app, voter, picks));

        add(Theme.title("Review Your Ballot"));
        add(Theme.gap(6));
        add(Theme.text("Check your choices before you submit."));
        add(Theme.gap(16));
        for (Position p : Position.values()) {
            add(card(p.getLabel(), picks.get(p).getName(), picks.get(p).getParty()));
            add(Theme.gap(8));
        }
        add(Theme.gap(6));
        add(Theme.label("You cannot change your vote after you submit.", Font.BOLD, 14, Theme.ORANGE));
        add(Theme.gap(16));
        add(Theme.row(Theme.outline("Back to Ballot", () -> app.showBallot(voter, picks)), submit));
    }

    private void submitVote(IbotoApp app, Voter voter, Map<Position, Candidate> picks) {
        submit.setEnabled(false); // blocks a double click from submitting twice
        try {
            Ballot ballot = new Ballot();
            for (Map.Entry<Position, Candidate> e : picks.entrySet()) ballot.choose(e.getKey(), e.getValue().getId());
            app.service().castBallot(voter, ballot);
            app.show(new StatusScreen(StatusScreen.Kind.SUCCESS, "Your vote has been recorded",
                    "Thank you for voting! Your ballot was saved and the candidate tally has been updated.",
                    "Log Out", app::showHome, null, null));
        } catch (RuntimeException ex) {
            app.show(new StatusScreen(StatusScreen.Kind.ERROR, "Vote could not be recorded",
                    String.valueOf(ex.getMessage()), "Home", app::showHome, null, null));
        }
    }

    private static JPanel card(String position, String name, String party) {
        JPanel p = Theme.column();
        p.setOpaque(true);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        JLabel pos = Theme.label(position, Font.BOLD, 13, Theme.RED);
        JLabel who = Theme.label(name + "  -  " + party, Font.PLAIN, 16, Theme.NAVY);
        pos.setAlignmentX(LEFT_ALIGNMENT);
        who.setAlignmentX(LEFT_ALIGNMENT);
        p.add(pos);
        p.add(who);
        p.setPreferredSize(new Dimension(430, 64));
        p.setMaximumSize(new Dimension(430, 64));
        p.setAlignmentX(CENTER_ALIGNMENT);
        return p;
    }
}
