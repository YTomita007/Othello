import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import javax.print.attribute.standard.Sides;
import javax.swing.*;

class Othello extends JFrame implements ActionListener{

    static final int WIDTH = 480;   // 画面サイズ（横）
    static final int HEIGHT = 540;  // 画面サイズ（縦）
    static final int SIDE = 8;      // 一辺あたりのマスの数
    final String WHITESTONE = "◯";  // 白石
    final String BLACKSTONE = "●";  // 黒石
    int i, j, k, l;                 // カウンタ変数
    int cs = WIDTH / SIDE;          // マスのサイズ
    int cell;                       // セル番号管理配列
    int clickPlace;                 // 選択したセル番号
    String mystone;                 // 自分の石
    String opstone;                 // 相手の石
    int whiteCount, blackCount;     // それぞれの石の数
    int ableCount;                  // 引っくり返す石の数
    int emptyCells = SIDE * SIDE;   // 空セルの数
    boolean playerFlg;              // プレイヤー制御フラグ
    boolean turnFlg;                // ターン制御フラグ
    JButton board[];                // ボード配列（8*8）
    JPanel headP = new JPanel();    // ヘッダーパネルの生成
    JPanel panel = new JPanel();    // メインパネルの生成
    JLabel playerLabel;             // プレイヤー表示ラベル
    JLabel countLabel;              // 石の数の表示
    JButton passButton;             // パスボタン
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

        passButton = new JButton();
        passButton.setText("pass");
        passButton.addActionListener(this);
        passButton.setActionCommand(String.valueOf("pass"));
        getContentPane().add(passButton, BorderLayout.CENTER);

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
        changeColor();
    }

    public void initBoard(){    // ボード初期化
        panel.setLayout(null);
        board = new JButton[SIDE*SIDE];

        for(i=0; i<SIDE; i++){
            for(j=0;j<SIDE;j++){
                cell = i * SIDE + j;
                board[cell] = new JButton();
                board[cell].setOpaque(true);    // セルの透明性の有効
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
        changeColor();
    }

    public void changePlayer(){
        changeColor();
        if(cpuFlg){
            cpuAlgorithm();
            cpuFlg = false;
        }
        changeColor();  // ①CPU機能を使う場合はここをコメントアウト解除（コメントアウトする場所は2箇所ある）
    }

    public void changeColor(){
        if(mystone == WHITESTONE){
            mystone = BLACKSTONE;
            opstone = WHITESTONE;
        }else{
            mystone = WHITESTONE;
            opstone = BLACKSTONE;
        }
        playerLabel.setText("現在のプレーヤーは" + (mystone) + "です");
        checkCount();
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

    public void checkAvailable(int clickNum, String mystone){   // ひっくり返せるか確認メソッド
        BoardFlipLine(clickNum, -SIDE - 1);     // 左上
        BoardFlipLine(clickNum, -SIDE);         // 左
        BoardFlipLine(clickNum, -SIDE + 1);     // 左下
        BoardFlipLine(clickNum, -1);            // 上
        BoardFlipLine(clickNum, 1); // 下
        BoardFlipLine(clickNum, SIDE - 1);      // 右上
        BoardFlipLine(clickNum, SIDE);          // 右
        BoardFlipLine(clickNum, SIDE + 1);      // 右下
        if(turnFlg){
            board[clickNum].setEnabled(false);
        }
    }

    public void BoardFlipLine(int baseNum, int directLine){    // clickNumを起点にdirectLine方向にチェック
        ableCount = 0;              // ひっくり返せる可能性のある数カウント初期化
        int addLine = directLine;   // directLine方向に検証を進めるための変数
        boolean ableFlg = false;    // ひっくり返せるフラグの初期化
        if(board[baseNum].isEnabled()){
            findingStones:
                while(baseNum + addLine > 0 && baseNum + addLine < board.length){
                    if(board[baseNum + addLine].getText().equals(opstone)){
                        ableCount++;
                    }else if(board[baseNum + addLine].getText().equals(mystone)){
                        ableFlg = true; // ひっくり返せるフラグ
                        break findingStones;
                    }else{
                        ableCount = 0;
                        break findingStones;
                    }
                    if(directLine != SIDE && directLine != -SIDE){  // 縦のボード場外へチェックへ行かないように制御
                        if(((baseNum + addLine) % SIDE == 0 && ableFlg == false)
                        || ((baseNum + addLine) % SIDE == SIDE - 1 && ableFlg == false )){
                            break findingStones;
                        }
                    }
                    addLine = addLine + directLine; // directLine方向に検証変数addLineに加算
                }
            if(ableCount != 0 && ableFlg){    // ableCountが1以上、かつableFlgがtrueであること
                board[baseNum].setText(mystone);
                for(i=0;i<ableCount;i++){
                    board[baseNum + (directLine * i + directLine)].setText(mystone);
                    board[baseNum + (directLine * i + directLine)].setEnabled(false);
                }
                turnFlg = true;
                // デバッグ用
                // System.out.println("可能時の番号：" + baseNum);
                // System.out.println("可能時の方向：" + directLine);
                // System.out.println("計算結果：" + (baseNum + addLine) % SIDE);
            }
        }
    }

    public void actionPerformed(ActionEvent e){     // クリックアクションメソッド
        turnFlg = false;        // 石を置くことができるか判断するフラグ（falseが初期値）
        if(e.getActionCommand().equals("reset")){
            resetBoard();
        }else if(e.getActionCommand().equals("pass")){
            changePlayer();
        }else{
            clickPlace = Integer.parseInt(e.getActionCommand());
            checkAvailable(clickPlace, mystone);
            System.out.println(clickPlace); // デバッグ用
            if(turnFlg){            // 石を置くことができない場合はturnFlgはfalse
                cpuFlg = true;      // ②CPU機能を使う場合はここをコメントアウト解除（コメントアウトする場所は2箇所ある）
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
        // 現在未使用
        // cpuScore = new int[SIDE*SIDE];  // コンピューターのスコア配列生成
        // for(k=0;k<board.length;k++){
        //     switch(k){
        //         case 0: case 7: case 56: case 63:
        //             cpuScore[k] = 10;
        //             break;
        //         case 1: case 6: case 8: case 15: case 48: case 55: case 57: case 62:
        //             cpuScore[k] = -5;
        //             break;
        //         case 9: case 14: case 49: case 54:
        //             cpuScore[k] = -10;
        //             break;
        //         case 2: case 5: case 16: case 18: case 21: case 23:
        //         case 40: case 42: case 45: case 47: case 58: case 61:
        //             cpuScore[k] = 5;
        //             break;
        //         default:
        //             cpuScore[k] = 0;
        //     }
        // }
        cpuFlg = false;
    }

    // コンピューターの評価計算メソッド
    public void cpuAlgorithm(){
        turnFlg = false;        // 石を置くことができるか判断するフラグ（falseが初期値）
        for(k=0;k<a.size();k++){
            checkAvailable(a.get(k), mystone);
            if(turnFlg){
                return;
            }
        }
        for(k=0;k<b.size();k++){
            checkAvailable(b.get(k), mystone);
            if(turnFlg){
                return;
            }
        }
        for(k=0;k<c.size();k++){
            checkAvailable(c.get(k), mystone);
            if(turnFlg){
                return;
            }
        }
        for(k=0;k<d.size();k++){
            checkAvailable(d.get(k), mystone);
            if(turnFlg){
                return;
            }
        }
        for(k=0;k<e.size();k++){
            checkAvailable(e.get(k), mystone);
            if(turnFlg){
                return;
            }
        }
        if(!turnFlg){
            System.out.println("CPUは置けません");
            turnFlg = true;
            changePlayer();
        }
    }
}