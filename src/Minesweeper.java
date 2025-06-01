import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    static int numRows = 9;
    static int numColumns = 9;
    static int mineCount = 10;
    int tileSize = 35;
    int boardWidth = numColumns * tileSize;
    int boardHeight = numRows * tileSize;
    boolean gameOver = false;
    int tilesClicked = 0;
    mineTile[][] mineBoard = new mineTile[numRows][numColumns];
    ArrayList<mineTile> mineList;
    private JLabel safeTileCounter;
    mineTile tile;

    ImageIcon flagIcon = new ImageIcon(getClass().getResource("/resources/Flag.png"));
    ImageIcon bombIcon = new ImageIcon(getClass().getResource("/resources/Bombc.png"));

    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Minesweeper frame = new Minesweeper();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class mineTile extends JButton {

        private static final long serialVersionUID = 1L;
        int r, c;

        public mineTile(int r, int c) {
            this.r = r;
            this.c = c;

            // COLOR NG TILES
            if ((r + c) % 2 == 0) {
                setBackground(Color.decode("#171928"));
            } else {
                setBackground(Color.decode("#1d1f34"));
            }

            // BEVEL
            setOpaque(true);
            setBorder(new BevelBorder(BevelBorder.RAISED));
            setFocusPainted(false);
        }
    }

    public Minesweeper() {

        setResizable(false);
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, boardWidth + 14, boardHeight + 70);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setBounds(0, 0, boardWidth, boardHeight + 35);
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel topPanel = new JPanel();
        topPanel.setBounds(0, 0, boardWidth, 35);
        topPanel.setBackground(Color.DARK_GRAY);
        topPanel.setLayout(null);
        contentPane.add(topPanel);

        safeTileCounter = new JLabel("Tile/s Left: " + (numRows * numColumns - mineCount));
        safeTileCounter.setFont(new Font("Tahoma", Font.BOLD, 11));
        safeTileCounter.setForeground(Color.WHITE);
        safeTileCounter.setBounds(boardWidth - 88, 7, 100, 20);
        topPanel.add(safeTileCounter);

        JPanel boardPanel = new JPanel();
        boardPanel.setBounds(0, 36, boardWidth, boardHeight);
        boardPanel.setLayout(new GridLayout(numRows, numColumns, 0, 0));
        contentPane.add(boardPanel);

        JButton easy = new JButton("Easy");
        easy.setFont(new Font("Tahoma", Font.BOLD, 10));
        easy.setBounds(5, 7, 58, 20);
        easy.setBackground(Color.decode("#3A3D58"));
        easy.setForeground(Color.WHITE);
        easy.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        easy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                numRows = 9;
                numColumns = 9;
                mineCount = 10;
                setVisible(false);
                main(null);
            }
        });
        topPanel.add(easy);

        JButton average = new JButton("Average");
        average.setFont(new Font("Tahoma", Font.BOLD, 10));
        average.setBounds(64, 7, 79, 20);
        average.setBackground(Color.decode("#3A3D58"));
        average.setForeground(Color.WHITE);
        average.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        average.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                numRows = 16;
                numColumns = 16;
                mineCount = 40;
                setVisible(false);
                main(null);
            }
        });
        topPanel.add(average);


        JButton difficult = new JButton("Difficult");
        difficult.setFont(new Font("Tahoma", Font.BOLD, 10));
        difficult.setBounds(145, 7, 73, 20);
        difficult.setBackground(Color.decode("#3A3D58"));
        difficult.setForeground(Color.WHITE);
        difficult.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        difficult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                numRows = 16;
                numColumns = 30;
                mineCount = 99;
                setVisible(false);
                main(null);
            }
        });
        topPanel.add(difficult);
        
        //Button Creation
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
            	tile = new mineTile(r, c);
                mineBoard[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Helvetica", Font.PLAIN, 30));
                tile.setName("");
                tile.setText("");
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        mineTile tile = (mineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                        	if(tile.getName().equals("🚩")) {
                        		return;
                        	}
                            if (tile.getText().equals("")) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getName() == "" && tile.isEnabled()) {
                                tile.setIcon(flagIcon);
                                tile.setName("🚩");
                            } else if (tile.getName() == "🚩") {
                                tile.setName("");
                                tile.setIcon(null);
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        setMines();
        setVisible(true);
    }

    void setMines() {
        mineList = new ArrayList<>();

        int mineLeft = mineCount;
        while (mineLeft > 0) {
            Random rand = new Random();
            int r = rand.nextInt(numRows);
            int c = rand.nextInt(numColumns);

            mineTile tile = mineBoard[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    void revealMines() {
        for (mineTile tile : mineList) {
            tile.setName("💣");
            tile.setIcon(bombIcon);
            tile.setBackground(Color.decode("#8B0000"));
            tile.setBorder(BorderFactory.createEmptyBorder());
            //tile.setEnabled(false);
        }
        gameOver = true;
        JOptionPane.showMessageDialog(this, "Game Over! You hit a bomb!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

   

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numColumns) {
            return;
        }

        mineTile tile = mineBoard[r][c];
        if (!tile.isEnabled()) {
            return;
        }else
        if(!tile.getText().equals("")){
        	return;
        }

        // CLICK COLOR
        tile.setEnabled(false);
        tile.setBackground(Color.decode("#3E4759"));
        tile.setBorder(new BevelBorder(BevelBorder.LOWERED));
        tilesClicked += 1;

        int remainingSafeTiles = (numRows * numColumns - mineCount) - tilesClicked;
        safeTileCounter.setText("Tile/s Left: " + remainingSafeTiles);
        if (remainingSafeTiles == 0) {
        	  JOptionPane.showMessageDialog(null,"Congratulations! You Win!");
        }

        int minesFound = 0;

        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);
        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);
        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
        	tile.setName(minesFound+"");
            tile.setText(minesFound+"");
            tile.setEnabled(true);
            color(tile);
        } else {
            tile.setText("");

            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);
            checkMine(r, c - 1);
            checkMine(r, c + 1);
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        if (tilesClicked == numRows * numColumns - mineList.size()) {
            gameOver = true;
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numColumns) {
            return 0;
        }
        if (mineList.contains(mineBoard[r][c])) {
            return 1;
        }
        return 0;
    }
   
    public void color(Minesweeper.mineTile tile) {
        if(tile.getText().equals("1")) {
            tile.setForeground(new Color(255, 255, 0));
        }else
        if(tile.getText().equals("2")) {
            tile.setForeground(new Color(255, 165, 0));
        }else
        if(tile.getText().equals("3")) {
            tile.setForeground(new Color(255, 90 ,0));
        }else
        if(tile.getText().equals("4")) {
            tile.setForeground(new Color(255, 20, 60));
        }else
        if(tile.getText().equals("5")) {
            tile.setForeground(new Color(255, 0, 0));
        }else
        if(tile.getText().equals("6")) {
           tile.setForeground(new Color(255, 0, 0));
        }else
        if(tile.getText().equals("7")) {
           tile.setForeground(new Color(225, 0, 0));
        }else
        if(tile.getText().equals("8")) {
            tile.setForeground(new Color(255, 0, 0));
        }
    }
}
