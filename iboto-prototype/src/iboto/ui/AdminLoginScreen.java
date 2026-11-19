package iboto.ui;

import iboto.model.Admin;
import iboto.service.ElectionService;

import javax.swing.*;
import java.awt.Font;

public class AdminLoginScreen extends Screen {
    public AdminLoginScreen(IbotoApp app) {
        ElectionService svc = app.service();
        JTextField user = Theme.field();
        JPasswordField pass = Theme.password();
        JLabel error = Theme.label(" ", Font.PLAIN, 13, Theme.RED);

        Runnable login = () -> {
            Admin admin = svc.login(user.getText(), new String(pass.getPassword()));
            if (admin == null) {
                error.setText("Incorrect username or password.");
                pass.setText("");
            } else {
                app.showAdmin(admin);
            }
        };
        user.setAlignmentX(LEFT_ALIGNMENT);
        pass.setAlignmentX(LEFT_ALIGNMENT);
        user.addActionListener(e -> pass.requestFocusInWindow());
        pass.addActionListener(e -> login.run());

        JPanel form = Theme.column();
        form.add(small("Username"));
        form.add(Theme.gap(6));
        form.add(user);
        form.add(Theme.gap(14));
        form.add(small("Password"));
        form.add(Theme.gap(6));
        form.add(pass);

        add(Theme.title("Admin Log In"));
        add(Theme.gap(8));
        add(Theme.text("Authorized personnel only."));
        add(Theme.gap(20));
        add(form);
        add(Theme.gap(6));
        add(error);
        add(Theme.gap(12));
        add(Theme.row(Theme.primary("Log In", login), Theme.outline("Back", app::showHome)));
        add(Theme.gap(18));
        add(Theme.label("Demo account: admin / admin123", Font.PLAIN, 13, Theme.GRAY));
    }

    private static JLabel small(String text) {
        JLabel l = Theme.label(text, Font.PLAIN, 13, Theme.NAVY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}
