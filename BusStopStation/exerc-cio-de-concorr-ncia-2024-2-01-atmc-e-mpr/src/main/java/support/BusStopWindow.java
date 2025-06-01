package support;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusStopWindow {
    private final JFrame window = new JFrame();
    ImageIcon[] icons = Icons.getIcons24();
    private final ImageIcon busStopIcon;
    private final ImageIcon iconPlay = icons[1];
    private final ImageIcon iconStop = icons[2];
    private final ImageIcon[] busAnimationFrames;
    private final ImageIcon[] busSemaphoreFrames;
    private final JButton miniPlayerPlayButton = new JButton(iconPlay);
    private final JButton miniPlayerStopButton = new JButton(iconStop);

    private final JLabel semaphoreLabel;
    private Timer semaphoreTimer;
    private int semaphoreState = 2; // 0 - green, 1 - yellow, 2 - red
    private final List<Bus> buses = new ArrayList<>();
    private final List<Person> people = new ArrayList<>();

    public BusStopWindow(
            String windowTitle,
            ActionListener buttonListenerPlay,
            ActionListener buttonListenerStop) {

        JPanel miniPlayerPanel = new JPanel();
        JPanel miniPlayerButtons = new JPanel();

        busStopIcon = Icons.getBusStopIcon();
        busAnimationFrames = Icons.getBusAnimationFrames();
        busSemaphoreFrames = Icons.getBusSemaphoreFrames();


        miniPlayerPanel.setLayout(new BoxLayout(miniPlayerPanel, BoxLayout.PAGE_AXIS));
        miniPlayerButtons.setLayout(new BoxLayout(miniPlayerButtons, BoxLayout.X_AXIS));
        miniPlayerPlayButton.setPreferredSize(new Dimension(35, 35));
        miniPlayerStopButton.setPreferredSize(new Dimension(35, 35));

        miniPlayerButtons.add(Box.createHorizontalGlue());
        miniPlayerButtons.add(Box.createRigidArea(new Dimension(5, 0)));
        miniPlayerButtons.add(miniPlayerPlayButton);
        miniPlayerButtons.add(Box.createRigidArea(new Dimension(5, 0)));
        miniPlayerButtons.add(miniPlayerStopButton);
        miniPlayerButtons.add(Box.createHorizontalGlue());

        miniPlayerStopButton.setEnabled(false);

        miniPlayerPanel.add(miniPlayerButtons);

        miniPlayerPlayButton.addActionListener(buttonListenerPlay);
        miniPlayerStopButton.addActionListener(buttonListenerStop);

        window.setLayout(new BorderLayout());
        window.setTitle(windowTitle);
        window.setSize(busStopIcon.getIconWidth(), 500);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating JLayeredPane for overlay
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(window.getWidth(), window.getHeight()));
        layeredPane.setLayout(null); // Null layout for manual positioning

        // Adding the bus stop to JLayeredPane
        JLabel busStopLabel = new JLabel(busStopIcon);
        busStopLabel.setBounds(0, 0, busStopIcon.getIconWidth(), busStopIcon.getIconHeight());
        layeredPane.add(busStopLabel, JLayeredPane.DEFAULT_LAYER);

        // Adding the semaphore to JLayeredPane
        semaphoreLabel = new JLabel(busSemaphoreFrames[2]);
        semaphoreLabel.setBounds(620, 190, busSemaphoreFrames[semaphoreState].getIconWidth(), busSemaphoreFrames[semaphoreState].getIconHeight());
        layeredPane.add(semaphoreLabel, JLayeredPane.PALETTE_LAYER);

        // Adding miniPlayerPanel to JLayeredPane
        miniPlayerPanel.setBounds(0, busStopIcon.getIconHeight(), window.getWidth(), miniPlayerPanel.getPreferredSize().height);
        layeredPane.add(miniPlayerPanel, JLayeredPane.DEFAULT_LAYER);

        window.setContentPane(layeredPane); // Sets the JLayeredPane as the main content

        window.setVisible(true);
        // Start traffic light
        startSemaphore();
    }


    // createBus Function to create a bus
    public Bus createBus(int limitPassengers) {
        int initialY = busStopIcon.getIconHeight() - 50;
        Bus bus = new Bus(window.getLayeredPane(), Icons.getBusIconStatic(), busAnimationFrames, -300, 260, limitPassengers);
        buses.add(bus);
        return bus;
    }

    public void removeBus(Bus bus) {
        bus.removeBus();
        buses.remove(bus);
    }

    // getBuses Function to return the list of buses
    public List<Bus> getBuses() {
        return buses;
    }

    // startSemaphore Function to start the semaphore
    public void startSemaphore() {
        if (semaphoreTimer != null && semaphoreTimer.isRunning()) {
            semaphoreTimer.stop();
        }

        semaphoreTimer = new Timer(3500, e -> {
            // 0 - green, 1 - yellow, 2 - red
            semaphoreState = (semaphoreState + 1) % 3;
            semaphoreLabel.setIcon(busSemaphoreFrames[semaphoreState]);

            // Adjusts the delay according to the current state
            if (semaphoreState == 0) {
                semaphoreTimer.setDelay(2500);
            } else if (semaphoreState == 1) {
                semaphoreTimer.setDelay(250);
            } else if (semaphoreState == 2) {
                semaphoreTimer.setDelay(3500);
            }

            // Reset the timer to apply the new delay immediately
            semaphoreTimer.restart();
        });

        semaphoreTimer.start();
    }

    public String getSemaphoreColor(int semaphoreState) {
        return switch (semaphoreState) {
            case 0 -> "Green";
            case 1 -> "Yellow";
            case 2 -> "Red";
            default -> "Unknown";
        };
    }

    // getSemaphoreState Function to return the semaphore state
    public int getSemaphoreState() {
        return semaphoreState;
    }

    // createPerson Function to create a person
    public Person createPerson() {
        // Load the sprite sheet
        JLabel characterLabel = new JLabel();
        characterLabel.setBounds(760, 230, 16, 32);
        this.window.getLayeredPane().add(characterLabel, JLayeredPane.PALETTE_LAYER);
        Person character = new Person(window.getLayeredPane(), characterLabel, 760, 230);
        people.add(character);
        // Instantiate and return the Person object
        return character;
    }

    public void removePerson(Person person) {
        person.removePerson();
        people.remove(person);
    }

    // getPeople Function to return the list of persons
    public List<Person> getPeople() {
        return people;
    }

    public void setEnabledPlayButton(Boolean enable) {
        miniPlayerPlayButton.setEnabled(enable);
    }

    public void setEnabledStopButton(Boolean enable) {
        miniPlayerStopButton.setEnabled(enable);
    }

    static final class Icons {
        public static ImageIcon[] getIcons24() {
            ImageIcon pause = new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/icons/pause-24.png")));
            ImageIcon play = new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/icons/play-24.png")));
            ImageIcon stop = new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/icons/stop-24.png")));
            return new ImageIcon[]{pause, play, stop};
        }

        // getBusStopIcon Function to return the bus stop icon
        public static ImageIcon getBusStopIcon() {
            return new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/assets/bus_station.png")));
        }

        // getBusIconStatic Function to return the bus icon
        public static ImageIcon getBusIconStatic() {
            return new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/assets/bus_static.png")));
        }

        // getBusAnimationFrames Function to return the bus animation frames
        public static ImageIcon[] getBusAnimationFrames() {
            ImageIcon[] frames = new ImageIcon[6];
            for (int i = 0; i < 6; i++) {
                frames[i] = new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/assets/animated/bus/bus_frame_" + (i + 1) + ".png")));
            }
            return frames;
        }

        // getBusSemaphoreFrames Function to return the bus semaphore frames
        public static ImageIcon[] getBusSemaphoreFrames() {
            ImageIcon[] frames = new ImageIcon[4];
            ImageIcon green = new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/assets/animated/semaphore/green.png")));
            ImageIcon yellow = new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/assets/animated/semaphore/yellow.png")));
            ImageIcon red = new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/assets/animated/semaphore/red.png")));
            // 0 - green, 1 - yellow, 2 - red
            frames[0] = green;
            frames[1] = yellow;
            frames[2] = red;
            return frames;
        }
    }
}
