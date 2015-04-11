import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class WelcomePanel extends JPanel implements ActionListener {

    private Game game;

    private JLabel welcome, enterName;
    private JTextField name;
    PlayerChoicePanel playerPanel;
    private JButton play;

    private ArrayList<Player> players;

	public WelcomePanel(Game game) {
        setLayout(null);
        setSize(game.windowWidth, game.windowHeight);
        setLocation(0, 0);
        setVisible(true);
        this.game = game;

        // JTextArea
        welcome = new JLabel("Welcome to Bubble Trouble!");
        welcome.setSize(400, 20);
        welcome.setLocation(50, 190);
        welcome.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcome);

        playerPanel = new PlayerChoicePanel();
        playerPanel.setLocation(50, 250);
        add(playerPanel);

        play = new JButton("Play");
        play.setSize(300, 30);
        play.setLocation(50, 360);
        play.setHorizontalAlignment(SwingConstants.CENTER);
        play.addActionListener(this);
        add(play);
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        Player player = new Player(playerPanel.getPlayerName(), game.windowWidth / 2, game.windowHeight, 0, game.windowWidth);

        players.add(player);
        return players;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        if (e.getSource()==play) {
            game.play();
        }
	}
}