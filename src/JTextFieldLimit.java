import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @ClassName: JTextFieldLimit.java
 * @Author: BuShiQiu
 * @Date: Created in 13:42 2021/11/21
 * @Description:
 * @Version: 1.0
 **/

public class JTextFieldLimit extends PlainDocument {
    private int limit;  //限制的长度

    public JTextFieldLimit(int limit) {
        super(); //调用父类构造
        this.limit = limit;
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) return;
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);//调用父类方法
        }
    }
}

