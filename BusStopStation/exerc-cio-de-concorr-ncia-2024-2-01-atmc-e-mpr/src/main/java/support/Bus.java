package support;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Bus {

    private final JLabel busLabel;
    private final ImageIcon[] busAnimationFrames;
    private int defLimitPassengers = 0;
    private final JLayeredPane window;
    private final List<Person> passengers = new ArrayList<>();

    private Timer animationTimer;
    private int animationFrameIndex = 0;
    private int stopped = 0;
    private int passedBusStop = 0;

    public Bus(JLayeredPane window, ImageIcon busIconStatic, ImageIcon[] busAnimationFrames, int initialX, int initialY, int limitPassengers) {
        this.window = window;
        this.busAnimationFrames = busAnimationFrames;
        this.defLimitPassengers = limitPassengers;

        busLabel = new JLabel(busIconStatic);
        busLabel.setBounds(initialX, initialY, busIconStatic.getIconWidth(), busIconStatic.getIconHeight());

        window.add(busLabel);
    }

    public void startBus() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationFrameIndex = 0;
        stepBus();

        animationTimer = new Timer(60, e -> {
            stepAnimation();
        });

        animationTimer.start();
    }

    // updates the bus animation frame
    public void stepAnimation() {
        busLabel.setIcon(busAnimationFrames[animationFrameIndex]);
        animationFrameIndex = (animationFrameIndex + 1) % busAnimationFrames.length;
    }


    public void stepBus(){
        int x = busLabel.getX();
        busLabel.setLocation(x + 1, busLabel.getY());
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean reachedEndOfScreen(){
        return busLabel.getX() > window.getWidth();
    }

    //  remove and destroy the bus
    public void removeBus() {
        window.remove(busLabel);
//        window.repaint();
    }

    public boolean checkBusStop() {
        int x = busLabel.getX();
        boolean isOnBusStop = (x == 140 && x < window.getWidth());
        if (isOnBusStop) {
            passedBusStop = 1;
        }
        return isOnBusStop;
    }

    public boolean busPassedBusStop(){
        return passedBusStop == 1;
    }

    public boolean checkBusIsOnSemaphore(){
        int x = busLabel.getX();
        return x >= 450 && x <= 470;
    }

    public void setStopped(){
        stopped = stopped == 0 ? 1 : 0;
    }

    public int getStopped(){
        return stopped;
    }

    public boolean getPassedBusStop(){
        return passedBusStop == 1;
    }

    public int getDefLimitPassengers(){
        return defLimitPassengers;
    }

    public void addPassenger(Person person){
        passengers.add(person);
        person.board();
    }

    public List<Person> getPassengers(){
        return passengers;
    }

    public void removePassenger(Person person){
        passengers.remove(person);
    }

}
