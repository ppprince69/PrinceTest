package dog;

import javax.swing.*;

public class Dog extends JFrame {
    public Dog(){
        initUI();
    }
    private void initUI() {
            add(new Level());
    }


    public static void main(String[] args) {
        Dog doc = new Dog();
        doc.setVisible(true);
        doc.setTitle("Dog and Cat");
        doc.setSize(380,420);
        doc.setDefaultCloseOperation(EXIT_ON_CLOSE);
        doc.setLocationRelativeTo(null);


    }

}
