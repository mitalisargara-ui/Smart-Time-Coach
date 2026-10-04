import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Smart Time Coach");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("SMART TIME COACH");
        label.setHorizontalAlignment(JLabel.CENTER);

        add(label);
    }

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }
}