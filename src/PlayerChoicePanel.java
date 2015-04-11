import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlayerChoicePanel extends JPanel implements FocusListener {

    private JTextField name;

    public PlayerChoicePanel() {
        setLayout(null);
        setSize(300, 30);
        setVisible(true);

        // Name of the player
        JLabel enterName = new JLabel("Name:");
        enterName.setSize(50, 30);
        enterName.setLocation(0, 0);
        add(enterName);

        name = new JTextField("Anonymous");
        name.setSize(240, 30);
        name.setLocation(60, 0);
        name.setHorizontalAlignment(SwingConstants.LEFT);
        name.addFocusListener(this);
        add(name);
    }

    public String getPlayerName() {
        return name.getText().trim();
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource()==name && name.getText().equals("Anonymous")) {
            name.setText("");
        }

    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource()==name && name.getText().equals("")) {
            name.setText("Anonymous");
        }
    }
}
