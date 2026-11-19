package iboto.ui;

import iboto.model.Voter;
import iboto.service.ElectionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Who has voted and who has not. It never shows how anyone voted. */
public class VotersPanel extends JPanel implements AdminScreen.Refreshable {
    private final ElectionService svc;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Barangay ID", "Name", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JLabel summary = new JLabel();

    public VotersPanel(ElectionService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        summary.setFont(Theme.font(Font.BOLD, 15));
        summary.setForeground(Theme.NAVY);

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(Theme.font(Font.PLAIN, 14));
        table.getTableHeader().setFont(Theme.font(Font.BOLD, 14));

        add(summary, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    @Override
    public void refresh() {
        model.setRowCount(0);
        int voted = 0;
        for (Voter v : svc.getVoters()) {
            if (v.hasVoted()) voted++;
            model.addRow(new Object[]{v.getId(), v.getName(), v.hasVoted() ? "Voted" : "Not yet voted"});
        }
        summary.setText(voted + " voted  |  " + (svc.registeredVoters() - voted) + " not yet voted");
    }
}
