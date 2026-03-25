package gpstrackeditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * User interface to control the appearance of the displayed track.
 *
 * @author arthu
 */
public class ControlPanel extends JPanel {

    private int WIDTH = 1000;
    private int HEIGHT = 30;

    public ControlPanel(MapPanel mapPanel) {
        super();
        JButton speedButton = new JButton("Change mode");
        speedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.toggleDisplayMode();
            }
        });
        this.add(speedButton);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

}
