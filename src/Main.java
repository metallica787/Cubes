import com.jogamp.opengl.util.Animator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args){
        Renderer renderer = new Renderer();
        Frame frame = new Frame();
        frame.add(renderer);
        frame.setSize(renderer.getSize());
        final Animator animator = new Animator(renderer);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(() -> {
                    animator.stop();
                    System.exit(0);
                }).start();
            }
        });
        frame.setVisible(true);
        animator.start();
        renderer.requestFocus();
    }
}
