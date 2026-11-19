package iboto.ui;

import iboto.model.Candidate;
import iboto.model.Position;
import iboto.model.Voter;
import iboto.service.ElectionService;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/** One column per position; the voter picks exactly one candidate in each. */
public class BallotScreen extends Screen {
    private final Map<Position, Candidate> selection = new EnumMap<>(Position.class);

    public BallotScreen(IbotoApp app, Voter voter, Map<Position, Candidate> preset) {
        if (preset != null) selection.putAll(preset);
        ElectionService svc = app.service();
        JLabel error = Theme.label(" ", Font.PLAIN, 13, Theme.RED);

        JPanel columns = new JPanel(new GridLayout(1, 3, 20, 0));
        columns.setOpaque(false);
        columns.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (Position p : Position.values()) columns.add(positionColumn(svc, p));

        add(Theme.title("Official Ballot"));
        add(Theme.gap(6));
        add(Theme.text("Voting as <b>" + Theme.esc(voter.getName()) + "</b>. Select one candidate per position."));
        add(Theme.gap(16));
        add(columns);
        add(Theme.gap(10));
        add(error);
        add(Theme.gap(8));
        add(Theme.row(
                Theme.primary("Review Ballot", () -> {
                    if (selection.size() < Position.values().length) {
                        error.setText("Please choose one candidate for every position.");
                    } else {
                        app.showReview(voter, new EnumMap<>(selection));
                    }
                }),
                Theme.outline("Cancel", app::showHome)));
    }

    private JPanel positionColumn(ElectionService svc, Position position) {
        JPanel col = Theme.column();
        JLabel head = Theme.label(position.getLabel(), Font.BOLD, 18, Theme.RED);
        head.setAlignmentX(LEFT_ALIGNMENT);
        col.add(head);
        col.add(Theme.gap(8));
        ButtonGroup group = new ButtonGroup();
        for (Candidate c : svc.candidatesFor(position)) {
            JRadioButton card = new JRadioButton("<html><b>" + Theme.esc(c.getName()) + "</b><br><font color='#7D819B'>"
                    + Theme.esc(c.getParty()) + "</font></html>");
            card.setFont(Theme.font(Font.PLAIN, 15));
            card.setForeground(Theme.NAVY);
            card.setOpaque(true);
            card.setFocusPainted(false);
            card.setBorderPainted(true); // radio buttons hide their border by default
            card.setAlignmentX(LEFT_ALIGNMENT);
            card.setPreferredSize(new Dimension(250, 64));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
            group.add(card);
            Candidate chosen = selection.get(position);
            card.setSelected(chosen != null && chosen.getId().equals(c.getId()));
            restyle(card);
            card.addItemListener(e -> {
                restyle(card);
                if (card.isSelected()) selection.put(position, c);
            });
            col.add(card);
            col.add(Theme.gap(8));
        }
        return col;
    }

    private static void restyle(JRadioButton card) {
        boolean on = card.isSelected();
        card.setBackground(on ? Theme.TINT : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(on ? Theme.RED : Theme.BORDER, on ? 2 : 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }
}
