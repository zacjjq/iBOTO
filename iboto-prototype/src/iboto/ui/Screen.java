package iboto.ui;

import javax.swing.*;

/** Base class for every screen: a transparent panel that stacks its content vertically. */
public abstract class Screen extends JPanel {
    protected Screen() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }
}
