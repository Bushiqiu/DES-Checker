import javax.swing.*;
import java.awt.*;

/**
 * @ClassName: MyFrame.java
 * @Author: BuShiQiu
 * @Date: Created in 17:21 2021/11/14
 * @Description: 程序主窗口
 * @Version: 1.0
 **/

public class MyFrame extends JFrame {
    // 定义组件
    // 4个JPanel,4个标签,4个文本框,2个按钮
    JPanel panel1, panel2, panel3, panel4;
    JLabel label1, label2, label3, label4;
    JTextField textField1, textField2, textField3, textField4;
    JButton button1, button2;

    public static void main(String[] args) {
        MyFrame myFrame = new MyFrame();
        myFrame.setTitle("DES算法加密工具");
    }

    public MyFrame() {
        // 初始化4个JPanel
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        panel4 = new JPanel();
        // 初始化4个标签
        label1 = new JLabel("明文");
        label2 = new JLabel("密钥");
        label3 = new JLabel("密文");
        label4 = new JLabel("解密");
        // 初始化2个按钮
        button1 = new JButton("加密");
        button2 = new JButton("解密");
        // 设置解密按钮不可点击
        button2.setEnabled(false);
        // 添加(加密)按钮动作事件监听器
        button1.addActionListener(e -> {
            // 获取用户输入的明文和密钥
            String plaintext = textField1.getText();
            String key = textField2.getText();
            // 验证明文和密钥数据
            if (plaintext.length() != 8 || !(plaintext.matches("[A-Za-z0-9]+")) || key.length() != 8 || !(key.matches("[A-Za-z0-9]+"))) {
                JOptionPane.showMessageDialog(MyFrame.this, "输入的明文或密钥有错!", "格式错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 初始化算法
            DESAlgorithm desAlgorithm = new DESAlgorithm();
            // 加密
            String ciphertext = desAlgorithm.getCiphertext(plaintext, key);
            // 将密文显示到文本框3
            textField3.setText(ciphertext);
            // 设置解密按钮为可点击
            button2.setEnabled(true);
        });
        // 添加(解密)按钮动作事件监听器
        button2.addActionListener(e -> {
            // 获取用户输入的密文和密钥
            String ciphertext = textField3.getText();
            String key = textField2.getText();
            // 初始化算法
            DESAlgorithm desAlgorithm = new DESAlgorithm();
            // 解密
            String plaintext = desAlgorithm.decryptCiphertext(ciphertext, key);
            // 将解密后的明文显示到文本框4
            textField4.setText(plaintext);
            // 设置解密按钮为不可点击
            button2.setEnabled(false);
        });
        // 初始化文本框,宽度为15
        // 明文
        textField1 = new JTextField(15);
        // 密钥
        textField2 = new JTextField(15);
        // 密文
        textField3 = new JTextField(15);
        // 解密
        textField4 = new JTextField(15);
        // 设置3,4文本框为只读
        textField3.setEditable(false);
        textField4.setEditable(false);
        // 添加文本框监听
        textField1.addFocusListener(new JTextFieldHintListener(textField1, "请输入八位数字或字母"));
        textField2.addFocusListener(new JTextFieldHintListener(textField2, "请输入八位数字或字母"));
        // 限制文本框长度为8
        textField1.setDocument(new JTextFieldLimit(8));
        textField2.setDocument(new JTextFieldLimit(8));
        // 设置布局管理器
        this.setLayout(new GridLayout(4, 1));
        // 加入各个组件
        panel1.add(label1);
        panel1.add(textField1);

        panel2.add(label2);
        panel2.add(textField2);

        panel3.add(button1);
        panel3.add(label3);
        panel3.add(textField3);

        panel4.add(button2);
        panel4.add(label4);
        panel4.add(textField4);
        // 添加面板到窗口中
        this.add(panel1);
        this.add(panel2);
        this.add(panel3);
        this.add(panel4);
        //设置窗口大小,退出关闭程序键,可见性
        this.setSize(300, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
