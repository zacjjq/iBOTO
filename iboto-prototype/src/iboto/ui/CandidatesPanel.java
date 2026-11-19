package iboto.ui;

import iboto.model.Candidate;
import iboto.model.Position;
import iboto.service.ElectionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Candidate list with Add and Edit. Candidates cannot be deleted once ballots may refer to them. */
public class CandidatesPanel extends JPanel implements AdminScreen.Refreshable {
    private final ElectionService svc;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Position", "Name", "Party"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final List<Candidate> shown = new ArrayList<>();

    public CandidatesPanel(ElectionService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        table.setRowHeight(30);
        table.setFont(Theme.font(Font.PLAIN, 14));
        table.getTableHeader().setFont(Theme.font(Font.BOLD, 14));
        table.setSelectionBackground(Theme.TINT);
        table.setSelectionForeground(Theme.NAVY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(Theme.primary("+ Add Candidate", () -> showForm(null)));
        buttons.add(Theme.outline("Edit Selected", this::editSelected));

        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    @Override
    public void refresh() {
        model.setRowCount(0);
        shown.clear();
        for (Position p : Position.values()) {
            for (Candidate c : svc.candidatesFor(p)) {
                shown.add(c);
                model.addRow(new Object[]{p.getLabel(), c.getName(), c.getParty()});
            }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a candidate in the list first.", "Edit candidate", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        showForm(shown.get(row));
    }

    private void showForm(Candidate existing) {
        JComboBox<Position> position = new JComboBox<>(Position.values());
        JTextField name = new JTextField(22);
        JTextField party = new JTextField(22);
        if (existing != null) {
            position.setSelectedItem(existing.getPosition());
            position.setEnabled(false); // the position is fixed because votes refer to it
            name.setText(existing.getName());
            party.setText(existing.getParty());
        }
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Position"));
        form.add(position);
        form.add(new JLabel("Full name"));
        form.add(name);
        form.add(new JLabel("Party (leave blank for Independent)"));
        form.add(party);

        while (true) {
            int answer = JOptionPane.showConfirmDialog(this, form, existing == null ? "Add Candidate" : "Edit Candidate",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (answer != JOptionPane.OK_OPTION) return;
            try {
                if (existing == null) {
                    svc.addCandidate((Position) position.getSelectedItem(), name.getText(), party.getText());
                } else {
                    svc.updateCandidate(existing.getId(), name.getText(), party.getText());
                }
                refresh();
                return;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot save candidate", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
