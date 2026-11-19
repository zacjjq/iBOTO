package iboto.ui;

import iboto.model.Position;
import iboto.service.ElectionService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Ranked vote counts per position, calculated from the recorded ballots. */
public class ResultsPanel extends JPanel implements AdminScreen.Refreshable {
    private final ElectionService svc;
    private int selectedTab = 0;

    public ResultsPanel(ElectionService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        int cast = svc.ballotsCast();
        int registered = svc.registeredVoters();
        int pct = registered == 0 ? 0 : Math.round(100f * cast / registered);
        JLabel summary = new JLabel("Ballots cast: " + cast + " of " + registered + " registered voters (" + pct + "%)");
        summary.setFont(Theme.font(Font.BOLD, 15));
        summary.setForeground(Theme.NAVY);

        JTabbedPane positions = new JTabbedPane();
        positions.setFont(Theme.font(Font.PLAIN, 14));
        for (Position p : Position.values()) positions.addTab(p.getLabel(), positionPanel(p));
        positions.setSelectedIndex(selectedTab);
        positions.addChangeListener(e -> selectedTab = positions.getSelectedIndex());

        add(summary, BorderLayout.NORTH);
        add(positions, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel positionPanel(Position position) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(Color.WHITE);
        col.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));

        List<ElectionService.Result> ranked = svc.results(position);
        boolean tie = svc.isTie(ranked);
        int top = ranked.isEmpty() ? 0 : ranked.get(0).votes;
        for (int i = 0; i < ranked.size(); i++) {
            ElectionService.Result r = ranked.get(i);
            JLabel name = new JLabel("#" + (i + 1) + "   " + r.candidate.getName());
            name.setFont(Theme.font(Font.BOLD, 15));
            name.setForeground(i == 0 ? Theme.RED : Theme.NAVY);
            name.setPreferredSize(new Dimension(230, 28));

            JLabel votes = new JLabel(r.votes + (r.votes == 1 ? " vote" : " votes"), SwingConstants.RIGHT);
            votes.setFont(Theme.font(Font.PLAIN, 15));
            votes.setForeground(Theme.NAVY);
            JLabel badge = new JLabel("", SwingConstants.CENTER);
            if (i == 0 && r.votes > 0) {
                badge.setText(tie ? "Tied" : "Leading");
                badge.setOpaque(true);
                badge.setBackground(tie ? Theme.ORANGE : Theme.GREEN);
                badge.setForeground(Color.WHITE);
                badge.setFont(Theme.font(Font.BOLD, 12));
            }
            JPanel east = new JPanel(new GridLayout(1, 2, 10, 4));
            east.setOpaque(false);
            east.setPreferredSize(new Dimension(190, 28));
            east.add(votes);
            east.add(badge);

            JPanel row = new JPanel(new BorderLayout(14, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            row.add(name, BorderLayout.WEST);
            row.add(new Bar(top == 0 ? 0 : (double) r.votes / top, i == 0 ? Theme.RED : Theme.NAVY), BorderLayout.CENTER);
            row.add(east, BorderLayout.EAST);
            col.add(row);
            col.add(Box.createVerticalStrut(14));
        }
        col.add(Box.createVerticalGlue());
        JLabel note = new JLabel(tie ? "Tie for first place - a tie-break is needed." : "Counts come from the saved ballots.");
        note.setFont(Theme.font(Font.PLAIN, 13));
        note.setForeground(Theme.GRAY);
        col.add(note);
        return col;
    }

    private static class Bar extends JComponent {
        private final double fraction;
        private final Color color;

        Bar(double fraction, Color color) {
            this.fraction = fraction;
            this.color = color;
            setPreferredSize(new Dimension(200, 28));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = 14;
            int y = (getHeight() - h) / 2;
            g2.setColor(new Color(0xDCDFEE));
            g2.fillRoundRect(0, y, getWidth(), h, h, h);
            if (fraction > 0) {
                g2.setColor(color);
                g2.fillRoundRect(0, y, Math.max(h, (int) (getWidth() * fraction)), h, h, h);
            }
            g2.dispose();
        }
    }
}
