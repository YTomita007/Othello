import javax.swing.*;

public class OthelloResult extends JFrame{
    
    JPanel panel = new JPanel();
    JLabel result;

    // 開発用に作成（実際のプレーではインスタンス化して使用するのでここから起動しない）
    // public static void main(String[] args) {
    //     new OthelloResult("result", 2);
    // }

    OthelloResult(String title, int pattern){
        setTitle(title);

        if(pattern == 1){
            result = new JLabel("◯の勝利");    
        }else{
            result = new JLabel("●の勝利");    
        }
    
        getContentPane().add(panel.add(result));
        setSize(300, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
