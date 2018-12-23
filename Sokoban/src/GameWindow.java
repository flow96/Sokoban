import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class GameWindow extends JFrame implements KeyListener, ActionListener, LevelCallbacks {

    private Level level;
    private int index, totalSeconds;
    private ArrayList<Level> levels;
    private Label lblLevel, lblTime, lblMoves;
    private Timer timer;
    private boolean timerRunning;
    private GameView gameView;

    public GameWindow(ArrayList<Level> levelList, int index){
        this.level = levelList.get(index).clone();
        this.index = index;
        this.levels = levelList;

        this.level.addLevelFinishedCallback(this);


        setTitle("Sokoban - " + level.toString());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(level.getWidth() * 60, level.getHeight() * 60 + 66);

        setLocationRelativeTo(null);


        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Spiel");
        JMenu levelMenu = new JMenu("Level");

        JMenuItem restart = new JMenuItem("Neustarten");
        JMenuItem save = new JMenuItem("Speichern");
        JMenuItem load = new JMenuItem("Laden");

        JMenuItem undo = new JMenuItem("Rückgängig");
        JMenuItem chooseLevel = new JMenuItem("Level auswählen");

        restart.addActionListener(this);
        save.addActionListener(this);
        load.addActionListener(this);
        undo.addActionListener(this);
        chooseLevel.addActionListener(this);

        gameMenu.add(restart);
        gameMenu.add(save);
        gameMenu.add(load);
        levelMenu.add(undo);
        levelMenu.add(chooseLevel);

        menuBar.add(gameMenu);
        menuBar.add(levelMenu);

        setJMenuBar(menuBar);

        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        infoPanel.setBackground(new Color(64, 64, 64));
        infoPanel.setBorder(new EmptyBorder(2, 1, 2, 1));

        lblLevel = new Label("Level: " + level.toString());
        lblLevel.setAlignment(Label.CENTER);
        lblLevel.setForeground(Color.WHITE);
        lblTime = new Label("00:00");
        lblTime.setAlignment(Label.CENTER);
        lblTime.setForeground(Color.WHITE);
        lblMoves = new Label("Moves: 0");
        lblMoves.setAlignment(Label.CENTER);
        lblMoves.setForeground(Color.WHITE);


        infoPanel.add(lblLevel);
        infoPanel.add(lblTime);
        infoPanel.add(lblMoves);

        gameView = new GameView(level);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(gameView, BorderLayout.CENTER);
        getContentPane().add(infoPanel, BorderLayout.NORTH);

        addKeyListener(this);

        setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(!level.isInputBlocked()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    startTimer();
                    level.moveUp();
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    startTimer();
                    level.moveDown();
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    startTimer();
                    level.moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    startTimer();
                    level.moveRight();
                    break;
                case KeyEvent.VK_Z:
                    level.undoMove();
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Neustarten")){
            level.removeLevelFinishedCallback(this);
            loadLevel(index);
        }
        if(e.getActionCommand().equals("Speichern")){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Sokoban", "sok"));
            int val = fileChooser.showSaveDialog(getParent());
            if(val == JFileChooser.APPROVE_OPTION) {
                try {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if(!path.toLowerCase().endsWith(".sok"))
                        path += ".sok";
                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
                    os.writeInt(index);
                    os.writeObject(level.getFields());
                    os.writeObject(level.getPlayer());
                    os.writeObject(level.getBoxes());
                    os.writeObject(level.getBoxesOnTargets());
                    os.writeInt(level.getMoveCounter());
                    os.writeInt(totalSeconds);
                    os.close();
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        }
        if(e.getActionCommand().equals("Laden")){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Sokoban", "sok"));
            int val = fileChooser.showOpenDialog(getParent());
            if(val == JFileChooser.APPROVE_OPTION) {
                try {
                    if(timer != null && timer.isRunning())
                        timer.stop();
                    level.removeLevelFinishedCallback(this);
                    ObjectInputStream os = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile().getAbsoluteFile()));
                    this.index = os.readInt();

                    this.level = levels.get(index).clone();
                    level.setFields((Field[][])os.readObject());
                    level.setPlayer((Player) os.readObject());
                    level.setBoxes((ArrayList<MoveableObject>) os.readObject());
                    level.setBoxesOnTargets((HashSet<Field>) os.readObject());
                    level.setMoveCounter(os.readInt());

                    this.totalSeconds = os.readInt();
                    this.moveCounterChanged(level.getMoveCounter());
                    this.updateLblTime();
                    os.close();

                    getContentPane().remove(gameView);
                    level.addLevelFinishedCallback(this);
                    gameView = new GameView(level);
                    timerRunning = false;
                    getContentPane().add(gameView, BorderLayout.CENTER);
                    this.revalidate();
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        }
        if(e.getActionCommand().equals("Rückgängig")){
            level.undoMove();
        }
        if(e.getActionCommand().equals("Level auswählen")){
            new StartWindow();
            this.dispose();
        }
    }

    @Override
    public void levelFinished() {
        if(index != levels.size() - 1){
            loadLevel(index + 1);
        }
    }

    private void loadLevel(int index){
        if(timer != null && timer.isRunning())
            timer.stop();
        getContentPane().remove(gameView);
        this.index = index;
        this.level = levels.get(index).clone();
        level.addLevelFinishedCallback(this);
        gameView = new GameView(level);
        timerRunning = false;
        totalSeconds = 0;
        getContentPane().add(gameView, BorderLayout.CENTER);
        this.moveCounterChanged(level.getMoveCounter());
        this.updateLblTime();
        this.revalidate();
    }

    @Override
    public void moveCounterChanged(int counter) {
        lblMoves.setText("Moves: " + counter);
    }

    @Override
    public void repaintView() {}

    private void startTimer(){
        if(!timerRunning) {
            timerRunning = true;
            timer = new Timer(1000, e -> {
                    totalSeconds++;
                    updateLblTime();
            });
            timer.setInitialDelay(1000);
            timer.start();
        }
    }

    private void updateLblTime(){
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        lblTime.setText(String.format("%02d:%02d", mins, secs));
    }
}
