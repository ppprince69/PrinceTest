package dog;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener {
    private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;



    final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int MAX_CATS = 12;
    private final int DOG_SPEED = 6;


    private int N_CATS = 6;
    private int lives, score;
    private int mili, second;
    private int[] dx, dy;
    private int[] cat_x, cat_y, cat_dx, cat_dy, catSpeed;

    private Image heart;
    private Image cat;
    Image bone;
    private Image up, down, left, right;

    private int dog_x, dog_y, dogd_x, dogd_y;
    private int req_dx, req_dy;


    final short[] levelData = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 26, 26, 18, 18, 22,     /// 16 dot 0 blueblock 1left border 2 top 4 right 8 border
            17, 16, 16, 16, 16, 24, 16, 16, 16, 20, 0, 0, 17, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 20,  0, 0, 17, 16, 20,
            0, 0, 0, 0, 0, 0, 17, 16, 16, 16,     18, 18, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 26, 26, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            21,  0,  0, 21, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21,  0,  0, 21, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 26, 26, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28,
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 5, 6, 8, 9, 10};
    private final int maxSpeed = 9;
    private int currentSpeed = 3;
    short[] screenData;
    private Timer timer1;

    public Model() {         //contructor
        loadImages();
        initVariavles();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }



    void loadImages() {
        down = new ImageIcon("/Users/princ/IdeaProjects/dog/src/images/down.gif").getImage();
        up = new ImageIcon("/Users/princ/IdeaProjects/dog/src/images/up.gif").getImage();
        left = new ImageIcon("/Users/princ/IdeaProjects/dog/src/images/left.gif").getImage();
        right = new ImageIcon("/Users/princ/IdeaProjects/dog/src/images/right.gif").getImage();
        cat = new ImageIcon("/Users/princ/IdeaProjects/dog/src/images/cat1.gif").getImage();
        heart = new ImageIcon("/Users/princ/IdeaProjects/dog/src/images/heart.png").getImage();
        bone = new ImageIcon("/Users/princ/IdeaProjects/dog/src/images/bone.png").getImage();
    }

    public void showIntroScreen(Graphics2D g2d) {
        String start = "Press SPACE to Start";
        g2d.setColor(Color.YELLOW);
        g2d.drawString(start, SCREEN_SIZE / 4, 150);
    }

    public void drawScore(Graphics2D g2d) {
        g2d.setFont(smallFont);
        g2d.setColor(new Color(5, 151, 79));
        String s = "Score: " + score;
        g2d.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);
        for (int i = 0; i < lives; i++) { //เช็คว่าชีวิตเหลือเท่าไร
            g2d.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, 10, 10, this);
        }
    }
    public void drawTime(Graphics2D g2d){
        g2d.setFont(smallFont);
        g2d.setColor(new Color(5, 151, 79));
        String t = "Time: " + second;
        g2d.drawString(t, SCREEN_SIZE / 2 , SCREEN_SIZE + 16);
    }





    void initVariavles() {
        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(400,400);
        cat_x = new int[MAX_CATS];
        cat_dx = new int[MAX_CATS];
        cat_y = new int[MAX_CATS];
        cat_dy = new int[MAX_CATS];
        catSpeed = new int[MAX_CATS];
        dx = new int[4];
        dy = new int[4];

        timer1 = new Timer(40,this);
        timer1.start();


    }
    void initGame(){
        lives = 3;
        score = 0;
        mili = 0;
        second = 30;

        initLevel();
        N_CATS = 6;
        currentSpeed =3;
    }
    private void initLevel(){
        int i;
        for (i =0; i<N_BLOCKS * N_BLOCKS;i++){
            screenData[i] = levelData[i];
        }
        continueLevel();
    }



    private void playGame(Graphics2D g2d){
        if (dying){
            death();
            second = 30;
        }else {
            moveDog();
            drawDog(g2d);
            moveCat(g2d);
            checkMaze();
            Time();
            initNextLevel();

        }
    }
    public void Time() {
        if(mili<40) {
            mili++;
            if(mili == 40) {
                second -= 1;
                mili = 0;
                if(second == 0){
                    death();
                    second = 30;
                }
            }
        }
    }



    public void moveDog(){
        int pos;
        short ch;

        if(dog_x % BLOCK_SIZE ==0 && dog_y % BLOCK_SIZE ==0){
            pos = dog_x / BLOCK_SIZE + N_BLOCKS * (int) (dog_y / BLOCK_SIZE);
                    ch = screenData[pos];
            if ((ch & 16)!=0){
                screenData[pos] = (short) (ch & 15);
                score++;

            }
            if(req_dx != 0 || req_dy != 0){
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1)!= 0)
                    || (req_dx == 1 && req_dy == 0 && (ch & 4) !=0)
                    || (req_dx == 0 && req_dy == -1 && (ch & 2) !=0)
                    || (req_dx == 0 && req_dy == 1 && (ch & 8) !=0))){
                    dogd_x = req_dx;
                    dogd_y = req_dy;
                }
            }
            if ((dogd_x == -1 && dogd_y == 0 && (ch & 1)!=0) //check for stand still การยืนนิ่ง
            ||(dogd_x == 1 && dogd_y == 0 && (ch & 4)!=0)
            ||(dogd_x == 0 && dogd_y == -1 && (ch & 2)!=0)
            ||(dogd_x == 0 && dogd_y == 1 && (ch & 8)!=0)){
                dogd_x = 0;
                dogd_y = 0;
            }
        }
        dog_x = dog_x + DOG_SPEED * dogd_x;
        dog_y = dog_y + DOG_SPEED * dogd_y;
    }

    public void drawDog(Graphics2D g2d) {
        if (req_dx == -1) {
            g2d.drawImage(left, dog_x + 1, dog_y + 1, 35,35,this);
        } else if (req_dx == 1) {
            g2d.drawImage(right, dog_x + 1, dog_y + 1,35,35, this);
        } else if (req_dy == -1) {
            g2d.drawImage(up, dog_x + 1, dog_y + 1,35,35, this);
        }else {
            g2d.drawImage(down, dog_x + 1, dog_y + 1,35,35, this);
        }
    }

    public void moveCat(Graphics2D g2d){
        int pos;
        int count;
        for (int i=0; i < N_CATS;i++){ //set 6cat
            if (cat_x[i] % BLOCK_SIZE ==0 && cat_y[i] % BLOCK_SIZE ==0) {
                pos = cat_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (cat_y[i] / BLOCK_SIZE); /// ซ้าย 1 บน2 ขวา4 ล่าง 8

                count = 0;
                if((screenData[pos] & 1)== 0 && cat_dx[i] !=1){
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }
                if((screenData[pos] & 2)== 0 && cat_dy[i] !=1){
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }
                if((screenData[pos] & 4)== 0 && cat_dx[i] !=1){
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }
                if((screenData[pos] & 8)== 0 && cat_dx[i] !=1){
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if(count ==0) {
                    if ((screenData[pos] & 15) == 15) {
                        cat_dy[i] = 0;
                        cat_dx[i] = 0;
                    } else {
                        cat_dy[i] = -cat_dy[i];
                        cat_dx[i] = -cat_dx[i];
                    }
                }else {
                        count = (int)(Math.random()*count);
                        if (count > 3){
                            count =3;
                        }
                        cat_dx[i] = dx[count];
                        cat_dy[i] = dy[count];
                    }
                }
            cat_x[i] = cat_x[i] + (cat_dx[i] * catSpeed[i]);
            cat_y[i] = cat_y[i] + (cat_dy[i] * catSpeed[i]);
            drawCat(g2d, cat_x[i] +1, cat_y[i] +1);

            if(dog_x > (cat_x[i] -12) && dog_x < (cat_x[i] +12) /// if dog touching cat die
                && dog_y > (cat_y[i] -12)&& dog_y < (cat_y[i] +12)
                && inGame){
                dying = true;

            }
        }
    }

    public void drawCat(Graphics2D g2d, int x, int y){

        g2d.drawImage(cat, x, y, 35,35,this);
    }

    public void checkMaze(){ // check any point left for the dog to eat
        int i =0;
        boolean finished = true;

        while (i< N_BLOCKS *N_BLOCKS && finished){
            if ((screenData[i]) !=0){
                finished = false;
            }
            i++;
        }
       if (finished){ /// if point r consume we move to the next level or anther case we just restart again
           score +=50 ;
           if(N_CATS < MAX_CATS){
               N_CATS++;
           }
           if (currentSpeed < maxSpeed){ /// if ponit > 50 currentSpeed +1
               currentSpeed++;
           }
            initLevel();
       }
    }

    private void initNextLevel(){
        while (score == 185){
//                Next();
                initGame();

            }
    }

//    private void Next() {
//        new State();
//    }


    private void death(){
        lives--;
        if (lives == 0){ // if dog die countinue
            inGame = false;
        }
        continueLevel();
    }

    void continueLevel(){
        int dx =1;
        int random;
        for(int i = 0;i<N_CATS;i++){
            cat_y[i] = 4*BLOCK_SIZE; //start position
            cat_x[i] = 4*BLOCK_SIZE;
            cat_dy[i] = 0;
            cat_dx[i] = dx;
            dx = -dx;
            random =(int) (Math.random() * (currentSpeed +1));

            if(random > currentSpeed){
                random = currentSpeed;
            }
            catSpeed[i] = validSpeeds[random];
        }
        dog_x = 7 * BLOCK_SIZE; //start position
        dog_y = 11 * BLOCK_SIZE;
        dogd_x = 0; //reset direction move
        dogd_y = 0;
        req_dx = 0;// reset direction controls
        req_dy = 0;
        dying = false;
    }
    public void drawMaze(Graphics2D g2d){
        short i =0;
        int x,y;
        for (y =0;y<SCREEN_SIZE; y += BLOCK_SIZE){
            for (x =0; x<SCREEN_SIZE; x += BLOCK_SIZE){
                    g2d.setColor(new Color(139, 0, 0));
                    g2d.setStroke(new BasicStroke(5));
                if((levelData[i] == 0)){ //พื้นที่ของ block ที่เป็นค่า 0
                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }
                if((screenData[i] & 1) !=0){ //leftBorder
                    g2d.drawLine(x, y, x, y+ BLOCK_SIZE-1);
                }
                if((screenData[i] & 2) !=0){ //topBorder
                    g2d.drawLine(x, y, x+ BLOCK_SIZE-1, y);
                }
                if((screenData[i] & 4) !=0){ //rightBorder
                    g2d.drawLine(x+ BLOCK_SIZE-1, y, x+ BLOCK_SIZE-1, y+ BLOCK_SIZE-1);
                }
                if((screenData[i] & 8) !=0){ //bottonBorder
                    g2d.drawLine(x, y+ BLOCK_SIZE-1, x+ BLOCK_SIZE-1, y+ BLOCK_SIZE-1);
                }
                if((screenData[i] & 16) !=0){ //dot
                    g2d.drawImage(bone, x+10,y+10, 8, 8, this);
                }
                i++;
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0,0,d.width,d.height);

        drawMaze(g2d);
        drawScore(g2d);
        drawTime(g2d);

        if(inGame){
            playGame(g2d);
        }else {
            showIntroScreen(g2d);
        }
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }



    class TAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            int key = e.getKeyCode();
            if (inGame){
                if (key == KeyEvent.VK_LEFT){
                    req_dx = -1;
                    req_dy = 0;
                }
                else if (key == KeyEvent.VK_RIGHT){
                    req_dx = 1;
                    req_dy = 0;
                }
                else if (key == KeyEvent.VK_UP){
                    req_dx = 0;
                    req_dy = -1;
                }
                else if (key == KeyEvent.VK_DOWN){
                    req_dx = 0;
                    req_dy = 1;
                }
                else if (key == KeyEvent.VK_ESCAPE && timer1.isRunning()){
                    inGame = false;
                }
            }else {
                if (key == KeyEvent.VK_SPACE){ ///gamestart
                    inGame = true ;
                    initGame();
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();

    }


}
