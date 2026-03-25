package gpstrackeditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * User interface to control the appearance of the displayed track.
 *
 * @author arthu
 */
public class ControlPanel extends JPanel {

    private int WIDTH = 1000;
    private int HEIGHT = 30;

    private MapPanel mapPanel;

    public ControlPanel(MapPanel newMapPanel) {

        super();

        mapPanel = newMapPanel;

        JButton speedButton = new JButton(getButtonTitle());
        speedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.toggleDisplayMode();
                speedButton.setText(mapPanel.getDisplayMode());
            }
        });
        this.add(speedButton);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        KeyAdapter adapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // ControlPanel may process some events and transfer others to the mapPanel
                mapPanel.actionPerformed(e);
            }
        };
        this.addKeyListener(adapter);
        speedButton.addKeyListener(adapter);
    }

    private String getButtonTitle() {
        return mapPanel.getDisplayMode();
    }

}
