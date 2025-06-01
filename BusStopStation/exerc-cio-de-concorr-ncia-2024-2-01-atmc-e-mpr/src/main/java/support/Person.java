package support;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

public class Person {

    private final ImageIcon[] animationFramesWalking;
    private final ImageIcon[] animationFramesIdle;
    private final JLabel personLabel;
    private boolean walking = false;
    private boolean onBusStop = false;
    private int spriteSheetPathId;
    private String spriteSheetPath;
    private final JLayeredPane window;
    private int currentFrameIndex = 0;
    private final JLabel characterLabel;
    private int x;
    private boolean boarded;
    private final int y; // Starting positions
    private final int speed = 15; // Character movement speed

    // Person constructor
    public Person(JLayeredPane window, JLabel characterLabel, int initialX, int initialY) {
        this.characterLabel = characterLabel;
        this.window = window;
        this.x = initialX;
        this.y = initialY;
        this.boarded = false;
        this.characterLabel.setLocation(x, y);
        setSpriteSheetId();
        setSpriteSheetPath();
        this.animationFramesWalking = defAnimationFrames(2);
        this.animationFramesIdle = defAnimationFrames(1);

        personLabel = new JLabel(this.animationFramesWalking[0]);
        personLabel.setBounds(initialX, initialY, this.animationFramesWalking[0].getIconWidth(), this.animationFramesWalking[0].getIconHeight());

        startAnimation(); // Start the person animation

    }

    // hasBoarded function to return the boarded state
    public boolean hasBoarded() {
        return boarded;
    }

    // board function to set the person`s boarded state to true
    public void board() {
        this.boarded = true;
    }

    // isWalking function to return the walking state
    public boolean isWalking() {
        return this.walking;
    }

    // isOnBusStop function to return the onBusStop state
    public boolean isOnBusStop() {
        return this.onBusStop;
    }

    // checkBusStop function to check if the person is on the bus stop
    public boolean checkBusStop(){
        if (x < 200 && !onBusStop){
            onBusStop = true;
            return true;
        }
        return false;
    }

    // setWalking function to set walking state: true if false; false if true;
    public void setWalking(){
        walking = !walking;
    }

    // removePerson function to remove the person from the screen
    public void removePerson() {
        window.remove(characterLabel);
//        window.repaint();
    }

    // setSpriteSheetId function to set the person`s sprite sheet id
    private void setSpriteSheetId() {
        Random random = new Random();
        this.spriteSheetPathId = random.nextInt(4);
    }

    // setSpriteSheetPath function to set the person`s sprite sheet path
    private void setSpriteSheetPath() {
        String[] spriteSheetIcons = new String[4];
        spriteSheetIcons[0] = "/assets/person1.png";
        spriteSheetIcons[1] = "/assets/person2.png";
        spriteSheetIcons[2] = "/assets/person3.png";
        spriteSheetIcons[3] = "/assets/person4.png";
        this.spriteSheetPath = spriteSheetIcons[this.spriteSheetPathId];
    }

    // defAnimationFrames function to define the animation frames
    private ImageIcon[] defAnimationFrames(int row) {
        int totalFrames = 24;
        int width = 16;
        int height = 32;
        ImageIcon spriteSheetIcon = new ImageIcon(Objects.requireNonNull(Person.class.getResource(this.spriteSheetPath)));
        BufferedImage spriteSheet = toBufferedImage(spriteSheetIcon.getImage());
        ImageIcon[] animationFrames = new ImageIcon[totalFrames];

        for (int i = 0; i < totalFrames; i++) {
            int x = i * width;
            int y = row * height;
            BufferedImage frame = spriteSheet.getSubimage(x, y, width, height);
            animationFrames[i] = new ImageIcon(frame);
        }

        // Set indexes from 18 to last to null
        for (int i = 18; i < totalFrames; i++) {
            animationFrames[i] = null;
        }

        // Remove the first 12 frames
        for (int i = 0; i < 12; i++) {
            animationFrames[i] = null;
        }

        // Copy non-null frames to final array
        ImageIcon[] temp = new ImageIcon[totalFrames];
        int j = 0;
        for (int i = 0; i < totalFrames; i++) {
            if (animationFrames[i] != null) {
                temp[j] = animationFrames[i];
                j++;
            }
        }
        System.arraycopy(temp, 0, animationFrames, 0, j);
        return animationFrames;
    }

    // getCurrentFrame function to get the current frame of the person
    private ImageIcon getCurrentFrame() {
        if (walking) {
            return animationFramesWalking[currentFrameIndex % animationFramesWalking.length];
        } else {
            return animationFramesIdle[currentFrameIndex % animationFramesIdle.length];
        }
    }

    // startAnimation function to start the person animation
    private void startAnimation() {
        int frameDelay = 100; // Delay between frames in milliseconds

        // Update the JLabel with the current frame
        // Move left
        // Returns to the end of the screen if you exit to the left
        Timer animationTimer = new Timer(frameDelay, e -> {
            currentFrameIndex = (currentFrameIndex + 1) % 6;
            // Update the JLabel with the current frame
            if (characterLabel != null) {
                characterLabel.setIcon(getCurrentFrame());
                if (walking && !onBusStop) {
                    x -= speed; // Move left
                    if (x < -characterLabel.getWidth()) {
                        x = characterLabel.getParent().getWidth(); // Returns to the end of the screen if you exit to the left
                    }
                    characterLabel.setLocation(x, y);
                }
            }
        });

        animationTimer.start();
    }

    // toBufferedImage function to convert Image to BufferedImage
    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage image = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = image.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return image;
    }
}