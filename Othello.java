import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.*;

class Othello extends JFrame implements ActionListener{

    static final int WIDTH = 480;   // 画面サイズ（横）
    static final int HEIGHT = 540;  // 画面サイズ（縦）
    static final int SIDE = 8;      // 一辺あたりのマスの数
    final String WHITESTONE = "◯";  // 白石
    final String BLACKSTONE = "●";  // 黒石
    int i, j;                       // カウンタ変数
    int cs = WIDTH / SIDE;          // マスのサイズ
    int cell;                       // セル番号管理配列
    int clickPlace;                 // 選択したセル番号
    String mystone;                 // 自分の石
    String opstone;                 // 相手の石
    int whiteCount, blackCount;     // それぞれの石の数
    int result;                     // 引っくり返す石の数
    int emptyCells = SIDE * SIDE;   // 空セルの数
    boolean playerFlg;              // プレイヤー制御フラグ
    boolean turnFlg;                // ターン制御フラグ
    JButton board[];                // ボード配列（8*8）
    JPanel headP = new JPanel();    // ヘッダーパネルの生成
    JPanel panel = new JPanel();    // メインパネルの生成
    JLabel playerLabel;             // プレイヤー表示ラベル
    JLabel countLabel;              // 石の数の表示
    JButton resetButton;            // リセットボタン

    // CPU操作系
    List<Integer> a = (List<Integer>) Arrays.asList(0, 7, 56, 63);
    List<Integer> b = (List<Integer>) Arrays.asList(2, 5, 16, 18, 21, 23, 40, 42, 45, 47, 58, 61);
    List<Integer> c = (List<Integer>) Arrays.asList(
                            3, 4, 10, 11, 12, 13, 17, 19, 20, 22, 24, 25, 
                            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
                            38, 39, 41, 43, 44, 46, 50, 51, 52, 53, 59, 60);
    List<Integer> d = (List<Integer>) Arrays.asList(1, 6, 8, 15, 48, 55, 57, 62);
    List<Integer> e = (List<Integer>) Arrays.asList(9, 14, 49, 54);
    int cpuScore[];                 // コンピューター用点数配列
    boolean cpuFlg;                 // コンピューターターン制御

    public static void main(String[] args) {
        new Othello("Othello");
    }

    public Othello(String title){   // コンストラクタ
        setTitle(title);
        getContentPane().setLayout(new FlowLayout());
        playerLabel = new JLabel("現在のプレーヤー");
        headP.add(playerLabel, BorderLayout.CENTER);
        countLabel = new JLabel("石の数");
        headP.add(countLabel, BorderLayout.CENTER);
        getContentPane().add(headP, BorderLayout.CENTER);

        resetButton = new JButton();
        resetButton.setText("reset");
        resetButton.addActionListener(this);
        resetButton.setActionCommand(String.valueOf("reset"));
        getContentPane().add(resetButton, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        getContentPane().add(panel, BorderLayout.CENTER);

        initBoard();
        pack();

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        changePlayer();
    }

    public void initBoard(){    // ボード初期化
        panel.setLayout(null);
        board = new JButton[SIDE*SIDE];
        cpuFlg = true;

        for(i=0; i<SIDE; i++){
            for(j=0;j<SIDE;j++){
                cell = i * SIDE + j;
                board[cell] = new JButton();
                board[cell].setOpaque(true);    // セルの透明性の有効
                // board[cell].setBorderPainted(false);  // セルの枠線を消す
                board[cell].setBackground(Color.GREEN);
                board[cell].setBounds(i * cs, j * cs, cs, cs);
                board[cell].addActionListener(this);
                board[cell].setActionCommand(String.valueOf(cell));

                panel.add(board[cell]);
            }
        }
        mystone = BLACKSTONE;   // デバッグ用（実際のプレーでは白か黒を選択できるようにする）

        // 初期配置
        board[27].setText(WHITESTONE);
        board[27].setEnabled(false);
        board[36].setText(WHITESTONE);
        board[36].setEnabled(false);
        board[28].setText(BLACKSTONE);
        board[28].setEnabled(false);
        board[35].setText(BLACKSTONE);
        board[35].setEnabled(false);
        emptyCells = emptyCells - 4;
        cpuInitilizer();
    }

    public void resetBoard(){
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
        initBoard();
        changePlayer();
    }

    public void changePlayer(){
        if(mystone == WHITESTONE){
            mystone = BLACKSTONE;
        }else{
            mystone = WHITESTONE;
        }
        playerLabel.setText("現在のプレーヤーは" + (mystone) + "です");
        checkCount();
        if(cpuFlg){
            cpuFlg = false;
        }else{
            cpuFlg = true;
            cpuAlgorithm();
            changePlayer();
        }
    }

    public void checkCount(){
        whiteCount = 0;
        blackCount = 0;
        for(i=0;i<board.length;i++){
            if(board[i].getText().equals(WHITESTONE)){
                whiteCount++;
                continue;
            }
            if(board[i].getText().equals(BLACKSTONE)){
                blackCount++;
            }
        }
        countLabel.setText("石の数：白は" + whiteCount + "黒は" + blackCount);
    }

    public void checkAvailable(int clickNum, String mystone){
        if(mystone == WHITESTONE){
            opstone = BLACKSTONE;
        }else{
            opstone = WHITESTONE;
        }
        BoardFlipLine(clickNum, -SIDE - 1);
        BoardFlipLine(clickNum, -SIDE);
        BoardFlipLine(clickNum, -SIDE + 1);
        BoardFlipLine(clickNum, -1);
        BoardFlipLine(clickNum, 1);
        BoardFlipLine(clickNum, SIDE - 1);
        BoardFlipLine(clickNum, SIDE);
        BoardFlipLine(clickNum, SIDE + 1);
    }

    public boolean BoardFlipLine(int clickNum, int directLine){    // clickNumを起点にdirectLine方向にチェック
        result = 0;
        int addLine = directLine;   // directLine方向に検証を進めるための変数
        while(clickNum + addLine > 0 && clickNum + addLine < board.length){
            if(board[clickNum + addLine].getText().equals(opstone)){
                result++;
            }else if(board[clickNum + addLine].getText().equals(mystone)){
                break;
            }else{
                result = 0;
                break;
            }
            addLine = addLine + directLine;
        }
        if(result != 0){    // resultが1以上であれば相手の石を変更させることができる
            board[clickNum].setText(mystone);
            board[clickNum].setEnabled(false);
            for(i=0;i<result;i++){
                board[clickNum + (directLine * i + directLine)].setText(mystone);
                board[clickNum + (directLine * i + directLine)].setEnabled(false);
            }
            turnFlg = true;
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e){     // クリックアクションメソッド
        turnFlg = false;        // 石を置くことができるか判断するフラグ（falseが初期値）
        if(e.getActionCommand().equals("reset")){
            resetBoard();
        }else{
            clickPlace = Integer.parseInt(e.getActionCommand());
            checkAvailable(clickPlace, mystone);
            System.out.println(clickPlace); // デバッグ用
            if(turnFlg){            // 石を置くことができない場合はturnFlgはfalse
                changePlayer();     // Playerを交代するメソッド（石の色を交代する）
            }else{
                System.out.println("そこには置けません");
            }
        }
    }

    public void cpuInitilizer(){
        Collections.shuffle(a);         // 各配列をシャッフル
        Collections.shuffle(b);
        Collections.shuffle(c);
        Collections.shuffle(d);
        Collections.shuffle(e);
        cpuScore = new int[SIDE*SIDE];  // コンピューターのスコア配列生成
        for(i=0;i<board.length;i++){
            switch(i){
                case 0: case 7: case 56: case 63:
                    cpuScore[i] = 10;
                    break;
                case 1: case 6: case 8: case 15: case 48: case 55: case 57: case 62:
                    cpuScore[i] = -5;
                    break;
                case 9: case 14: case 49: case 54:
                    cpuScore[i] = -10;
                    break;
                case 2: case 5: case 16: case 18: case 21: case 23:
                case 40: case 42: case 45: case 47: case 58: case 61:
                    cpuScore[i] = 5;
                    break;
                default:
                    cpuScore[i] = 0;
            }
        }
    }

    // コンピューターの評価計算メソッド
    public void cpuAlgorithm(){
        // try {    // sleepコマンドでCPUの実行をわざと遅くする
        //     Thread.sleep(1000);
        // } catch (InterruptedException e1) {
        //     e1.printStackTrace();
        // } 
        turnFlg = false;        // 石を置くことができるか判断するフラグ（falseが初期値）
        for(i=0;i<a.size();i++){
            checkAvailable(a.get(i), mystone);
            if(turnFlg){
                return;
            }
        }
        for(i=0;i<b.size();i++){
            checkAvailable(b.get(i), mystone);
            if(turnFlg){
                return;
            }
        }
        for(i=0;i<c.size();i++){
            checkAvailable(c.get(i), mystone);
            if(turnFlg){
                return;
            }
        }
        for(i=0;i<d.size();i++){
            checkAvailable(d.get(i), mystone);
            if(turnFlg){
                return;
            }
            System.out.println(d.get(i));
        }
        for(i=0;i<e.size();i++){
            checkAvailable(e.get(i), mystone);
            if(turnFlg){
                return;
            }
            System.out.println(e.get(i));
        }

        if(!turnFlg){
            System.out.println("CPUは置けません");
            turnFlg = true;
        }
    }
}