import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Othello extends JFrame implements ActionListener{

    static final int WIDTH = 480;   // 画面サイズ（横）
    static final int HEIGHT = 540;  // 画面サイズ（縦）
    final String WHITESTONE = "◯";  // 白石
    final String BLACKSTONE = "●";  // 黒石
    int i, j, k, side=8;            // カウンタ変数
    int cs = WIDTH / side;          // マスのサイズ
    int cell;                       // セル番号管理配列
    int clickPlace;                 // 選択したセル番号
    String mystone;                 // 自分の石
    String opstone;                 // 相手の石
    int result;                     // 引っくり返す石の数
    boolean playerFlg;              // プレイヤー制御フラグ
    boolean turnFlg;                // ターン制御フラグ
    JButton board[] = new JButton[side*side];   // ボード配列（8*8）
    JPanel panel = new JPanel();

    public static void main(String[] args) {
        new Othello("Othello");
    }

    public Othello(String title){   // コンストラクタ
        setTitle(title);
        getContentPane().setLayout(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        initBoard();

        getContentPane().add(panel);
        pack();

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        mystone = WHITESTONE;   // デバッグ用
    }

    public void initBoard(){    // ボード初期化
        panel.setLayout(null);

        for(i=0; i<8; i++){
            for(j=0;j<8;j++){
                cell = i*8+j;
                board[cell] = new JButton();
                board[cell].setOpaque(true);    // セルの透明性の有効
                // board[cell].setBorderPainted(false);  // セルの枠線を消す
                board[cell].setBackground(Color.GREEN);
                board[cell].setBounds(i*cs, (j+1)*cs, cs, cs);
                board[cell].addActionListener(this);
                board[cell].setActionCommand(String.valueOf(cell));

                panel.add(board[cell]);
            }
        }

        // 初期配置
        board[27].setText(WHITESTONE);
        board[27].setEnabled(false);
        board[36].setText(WHITESTONE);
        board[36].setEnabled(false);
        board[28].setText(BLACKSTONE);
        board[28].setEnabled(false);
        board[35].setText(BLACKSTONE);
        board[35].setEnabled(false);
    }

    public void changePlayer(){
        if(mystone == WHITESTONE){
            mystone = BLACKSTONE;
        }else{
            mystone = WHITESTONE;
        }
    }

    public void checkAvailable(int clickNum, String mystone){
        if(mystone == WHITESTONE){
            opstone = BLACKSTONE;
        }else{
            opstone = WHITESTONE;
        }
        BoardFlipLine(clickNum, -side - 1);
        BoardFlipLine(clickNum, -side);
        BoardFlipLine(clickNum, -side + 1);
        BoardFlipLine(clickNum, -1);
        BoardFlipLine(clickNum, 1);
        BoardFlipLine(clickNum, side - 1);
        BoardFlipLine(clickNum, side);
        BoardFlipLine(clickNum, side + 1);
    }

    public void BoardFlipLine(int clickNum, int directLine){    // clickNumを起点にdirectLine方向にチェック
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
        }
    }

    public void actionPerformed(ActionEvent e){     // クリックアクションメソッド
        turnFlg = false;    // 石を置くことができるか判断するフラグ
        clickPlace = Integer.parseInt(e.getActionCommand());
        System.out.println(clickPlace);
        checkAvailable(clickPlace, mystone);
        if(!turnFlg)        // 石を置くことができない場合はturnFlgはfalse
            System.out.println("そこには置けません");
        changePlayer();     // Playerを交代するメソッド（石の色を交代する）
    }

    // コンピューターの評価計算メソッド
    public void cpuAlgorithm(){

    }
}