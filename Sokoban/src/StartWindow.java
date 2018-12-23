
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class StartWindow extends JFrame {

    private ArrayList<Level> levels;
    private JList list;


    public StartWindow(){
        super("Sokoban - Level auswählen");

        setSize(550, 510);
        setResizable(false);

        MusicManager.getInstance();

        setLayout(new FlowLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Center of screen
        setLocationRelativeTo(null);

        levels = new ArrayList<>();

        // Count files in relative '/levels' folder
        try {
            LevelLoader.load("/levels/minicosmos.txt", levels);
            LevelLoader.load("/levels/nabokosmos.txt", levels);
            LevelLoader.load("/levels/yoshiomurase.txt", levels);
        }catch (Exception e){e.printStackTrace();}

        list = new JList(levels.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setSelectedIndex(0);
        list.setCellRenderer(new LevelPreviewRenderer());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // On double click
                if(e.getClickCount() == 2){
                    startGame(list.getSelectedIndex());
                }
            }
        });
        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    startGame(list.getSelectedIndex());
                }
            }
        });


        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(550, 420));
        listScroller.setBorder(null);


        JButton btnStartGame = new JButton("Start");
        btnStartGame.setBackground(new Color(64, 64, 64));
        btnStartGame.setContentAreaFilled(false);
        btnStartGame.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        btnStartGame.setForeground(Color.WHITE);

        btnStartGame.addActionListener(e -> {
            startGame(list.getSelectedIndex());
        });

        JLabel lblChooseLvl = new JLabel("Level auswählen:");
        lblChooseLvl.setForeground(Color.WHITE);

        getContentPane().setBackground(new Color(64, 64,64));
        getContentPane().add(lblChooseLvl);
        getContentPane().add(listScroller);
        getContentPane().add(btnStartGame);

        setVisible(true);

    }

    private void startGame(int index){
        new GameWindow(levels, index);
        dispose();
    }


    public static void main(String[] args){
        new StartWindow();
    }


    public class LevelPreviewRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return new PreviewLevel((Level)value, isSelected);
        }

    }
}
