package gpstrackeditor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * User interface to control the appearance of the displayed track.
 *
 * @author arthu
 */
public class ControlPanel extends JPanel {

    private int WIDTH = 1000;
    private int HEIGHT = 30;

    private MapPanel mapPanel;
    private JPanel topPanel, bottomPanel;

    private String configFilename = "config.txt";

    public ControlPanel(MapPanel newMapPanel) {

        super();

        topPanel = new JPanel();
        bottomPanel = new JPanel();

        mapPanel = newMapPanel;

        KeyAdapter adapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // ControlPanel may process some events and transfer others to the mapPanel
                mapPanel.actionPerformed(e);
            }
        };
        this.addKeyListener(adapter);

        JButton speedButton = new JButton(getButtonTitle());
        speedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.toggleDisplayMode();
                speedButton.setText(mapPanel.getDisplayMode());
            }
        });
        speedButton.addKeyListener(adapter);
        topPanel.add(speedButton);

        JButton loadConfigButton = new JButton("Load config");
        loadConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadConfigFile();
            }
        });
        loadConfigButton.addKeyListener(adapter);
        topPanel.add(loadConfigButton);

        ///////////////////////////
        // Tools to add starts and ends
        JTextField nameTextField = new JTextField(20);
        bottomPanel.add(nameTextField);

        JButton addStartButton = new JButton("Start");
        addStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.setMode(MapPanel.MapPanelMode.ADDING_START);
                mapPanel.setLiftName(nameTextField.getText());
            }
        });
        addStartButton.addKeyListener(adapter);
        topPanel.add(addStartButton);

        JButton addEndButton = new JButton("End");
        addEndButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.setMode(MapPanel.MapPanelMode.ADDING_END);
            }
        });
        addEndButton.addKeyListener(adapter);
        topPanel.add(addEndButton);

        JButton saveLiftsButton = new JButton("Save lifts");
        saveLiftsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.saveLifts();
            }
        });
        saveLiftsButton.addKeyListener(adapter);
        topPanel.add(saveLiftsButton);

        JButton loadLiftsButton = new JButton("Load lifts");
        loadLiftsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.loadLifts();
            }
        });
        loadLiftsButton.addKeyListener(adapter);
        topPanel.add(loadLiftsButton);

        ButtonGroup group = new ButtonGroup();

        JRadioButton isChairliftButton = new JRadioButton("Chairlift");
        isChairliftButton.addActionListener((e) -> {
            mapPanel.setLiftType(Lift.LiftType.CHAIRLIFT);
        });
        bottomPanel.add(isChairliftButton);
        group.add(isChairliftButton);

        JRadioButton isSkiLiftButton = new JRadioButton("Skilift");
        isSkiLiftButton.addActionListener((e) -> {
            mapPanel.setLiftType(Lift.LiftType.SKILIFT);
        });
        bottomPanel.add(isSkiLiftButton);
        group.add(isSkiLiftButton);

        JRadioButton isTelemixButton = new JRadioButton("Telemix");
        isTelemixButton.addActionListener((e) -> {
            mapPanel.setLiftType(Lift.LiftType.TELEMIX);
        });
        bottomPanel.add(isTelemixButton);
        group.add(isTelemixButton);

        JRadioButton isGondolaButton = new JRadioButton("Goldola");
        isGondolaButton.addActionListener((e) -> {
            mapPanel.setLiftType(Lift.LiftType.GONDOLA);
        });
        bottomPanel.add(isGondolaButton);
        group.add(isGondolaButton);

        this.setLayout(new GridLayout(2, 1));
        this.add(topPanel);
        this.add(bottomPanel);

        loadConfigFile();
    }

    private String getButtonTitle() {
        return mapPanel.getDisplayMode();
    }

    private void loadConfigFile() {
        try {
            mapPanel.resetLifts();
            BufferedReader reader = new BufferedReader(new FileReader(new File(configFilename)));
            String line = "";
            line = reader.readLine();
            while (line != null) {
                Lift newLift = new Lift(line);
                mapPanel.addLift(newLift);
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        repaint();
        mapPanel.repaint();
    }
}
