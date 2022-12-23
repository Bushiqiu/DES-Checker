/**
 * @ClassName: DESAlgorithm.java
 * @Author: BuShiQiu
 * @Date: Created in 21:16 2021/11/14
 * @Description: DES算法源码
 * @Version: 1.0
 **/

public class DESAlgorithm {
    /**
     * 接收8位数的明文或密钥,转换成对应的ASCII码,并得到64位二进制数据
     *
     * @param input 8位数字符串
     * @return String 64位二进制字符串
     */
    public String convertToBin(String input) {
        StringBuilder temp = new StringBuilder();
        if (input == null) return null;
        // 遍历字符串每一个字符
        for (int i = 0; i < input.length(); i++) {
            // 单个字符转化成二进制字符串
            StringBuilder bin = new StringBuilder(Integer.toBinaryString(input.charAt(i)));
            // 二进制字符串长度不足8位,在前面用"0"补齐
            while (bin.length() < 8) {
                bin.insert(0, "0");
            }
            temp.append(bin);
        }
        return temp.toString();
    }

    /**
     * 循环左移方法
     *
     * @param str   字符串
     * @param times 左移位数
     * @return String
     */
    public String pushLeft(String str, int times) {
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(Table.LOOP_TABLE[times - 1]));
        sb.append(str, 0, Table.LOOP_TABLE[times - 1]);
        return sb.toString();
    }

    /**
     * 处理密钥
     *
     * @param key 64位二进制密钥
     * @return String[] 16个子密钥的字符串集合
     */
    public String[] getSubKey(String key) {
        // 64位密钥经过PM_TABLE1置换只取其中的56位,然后分上下两半,每半各28位,得到C0,D0
        StringBuilder temp = new StringBuilder();
        // 进行置换,得到重新排列的58位密钥
        for (char c : Table.PM_TABLE1) {
            temp.append(key.charAt(c - 1));
        }
        // 分开得到C0,D0
        String C0 = temp.substring(0, 28);
        String D0 = temp.substring(28);
        // 初始化子密钥集合,和子密钥
        String[] subKeys = new String[16];
        StringBuilder subKey = new StringBuilder();
        // 循环16次获得子密钥集合
        for (int i = 0; i < 16; i++) {
            // 调用循环左移方法得到C1,D1
            String C1 = pushLeft(C0, i + 1);
            String D1 = pushLeft(D0, i + 1);
            String tempKey = C1 + D1;
            for (char c : Table.PM_TABLE2) {
                subKey.append(tempKey.charAt(c - 1));
            }
            subKeys[i] = subKey.toString();
            // C1,D1的值赋给C0,D0 为下一轮做准备工作
            C0 = C1;
            D0 = D1;
            // 清空subKey
            subKey.delete(0, subKey.length());
        }
        return subKeys;
    }

    /**
     * 加密!处理明文,加密得到密文( 主流程 )
     *
     * @param plaintext 明文
     * @param key       密钥
     * @return String 密文
     */
    public String getCiphertext(String plaintext, String key) {
        // 转换成64位二进制
        plaintext = convertToBin(plaintext);
        key = convertToBin(key);
        // 得到子密钥集合
        String[] subKeys = getSubKey(key);
        // 64位明文经过IP_TABLE置换,然后分左右两半,每半各32位,得到L0,R0
        StringBuilder temp = new StringBuilder();
        StringBuilder eR0 = new StringBuilder();
        // 进行置换,得到重新排列的64位密钥
        for (char c : Table.IP_TABLE) {
            temp.append(plaintext.charAt(c - 1));
        }
        // 分开得到L0,R0
        String L0 = temp.substring(0, 32);
        String R0 = temp.substring(32);
        // 初始化R1,L1,sBox
        String L1 = "";
        String R1 = "";
        String sBox;
        // 迭代16次得到L16,R16
        for (int i = 0; i < 16; i++) {
            // R0的值赋给L1
            L1 = R0;
            // R0经过E扩展函数表得到E(R0)
            for (char c : Table.EM_TABLE) {
                eR0.append(R0.charAt(c - 1));
            }
            // E(R0)和子密钥进行按位异或运算得到S盒(48bit)
            sBox = stringXOROperation(eR0.toString(), subKeys[i]);
            // S盒处理后得到输出,经过PMP_TABLE置换重排,得到F(R0,K1)
            // L0与F(R0,K1)进行按位异或运算得到R1
            temp.delete(0, temp.length());// 清空temp,用于存放F(R0,K1)
            for (char c : Table.PMP_TABLE) {
                temp.append(sBoxOutPut(sBox).charAt(c - 1));
            }
            R1 = stringXOROperation(temp.toString(), L0);
            eR0.delete(0, eR0.length());// 清空E(R0)
            // 覆盖进行下一轮
            R0 = R1;
            L0 = L1;
        }
        // 初始化密文
        StringBuilder ciphertext = new StringBuilder();
        // 交换L16,R16位置
        String RL = R1 + L1;
        // RL经过逆初始置换得到64位二进制密文
        for (char c : Table.ReIP_TABLE) {
            ciphertext.append(RL.charAt(c - 1));
        }
        // 二进制密文转成8位的密文,每8位转成一个字符
        for (int i = 0; i < 64; i += 8) {
            int c = Integer.valueOf(ciphertext.substring(i, i + 8), 2);
            ciphertext.append((char) c);
        }
        ciphertext.delete(0, 64);
        return ciphertext.toString();
    }

    /**
     * 两个字符串进行按位异或运算,结果返回String
     *
     * @param str1 运算符前字符串
     * @param str2 运算符后字符串
     * @return String 相同长度的字符串
     */
    public String stringXOROperation(String str1, String str2) {
        // 将两个字符串转成int
        long s1 = Long.parseLong(str1, 2);
        long s2 = Long.parseLong(str2, 2);
        // 进行按位异或运算
        long result = s1 ^ s2;
        // 结果转换成二进制字符串,当长度不一致时,在字符串前面补0
        StringBuilder resultBin = new StringBuilder(Long.toBinaryString(result));
        while (resultBin.length() < str1.length()) {
            resultBin.insert(0, "0");
        }
        return resultBin.toString();
    }

    /**
     * 得到S盒的输出
     *
     * @param sBox 48位的大S盒
     * @return String S盒的输出
     */
    public String sBoxOutPut(String sBox) {
        StringBuilder sbOutPut = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        // 分成8个小S盒,每个6位
        String[] sBoxes = new String[8];
        for (int i = 0, j = 0; i < 8; i++, j += 6) {
            sBoxes[i] = sBox.substring(j, j + 6);
        }
        // 得到每一个小S盒的输出
        // 第一位和最后一位组成二进制,转成十进制就是行数
        for (int i = 0; i < 8; i++) {
            sb.append(sBoxes[i].charAt(0)).append(sBoxes[i].charAt(5));
            int row = Integer.parseInt(sb.toString(), 2);
            // 中间四位二进制数转成十进制就是列数
            sb.delete(0, sb.length());// 清空
            sb.append(sBoxes[i], 1, 5);
            int col = Integer.parseInt(sb.toString(), 2);
            String sBox_OutPut = Integer.toBinaryString(Table.S_BOX[i][row][col]);
            // 位数不够4位的在前面用零补齐
            sbOutPut.append("0".repeat(Math.max(0, 4 - sBox_OutPut.length())));
            sbOutPut.append(sBox_OutPut);
            sb.delete(0, sb.length());// 清空
        }
        return sbOutPut.toString();
    }

    /**
     * 解密!处理密文,解密得到明文
     *
     * @param ciphertext 密文
     * @param key        密钥
     * @return String 明文
     */
    public String decryptCiphertext(String ciphertext, String key) {
        // 转换成64位二进制
        ciphertext = convertToBin(ciphertext);
        key = convertToBin(key);
        // 得到子密钥集合
        String[] subKeys = getSubKey(key);
        // 64位明文经过IP_TABLE置换,然后分左右两半,每半各32位,得到L0,R0
        StringBuilder temp = new StringBuilder();
        StringBuilder eR0 = new StringBuilder();
        // 进行置换,得到重新排列的64位密钥
        for (char c : Table.IP_TABLE) {
            temp.append(ciphertext.charAt(c - 1));
        }
        // 分开得到L0,R0
        String L0 = temp.substring(0, 32);
        String R0 = temp.substring(32);
        // 初始化R1,L1,sBox
        String L1 = "";
        String R1 = "";
        String sBox;
        // 迭代16次得到L16,R16
        for (int i = 0; i < 16; i++) {
            // R0的值赋给L1
            L1 = R0;
            // R0经过E扩展函数表得到E(R0)
            for (char c : Table.EM_TABLE) {
                eR0.append(R0.charAt(c - 1));
            }
            // E(R0)和子密钥进行按位异或运算得到S盒(48bit)
            // 解密时,子密钥使用的顺序为倒序
            sBox = stringXOROperation(eR0.toString(), subKeys[15 - i]);
            // S盒处理后得到输出,经过PMP_TABLE置换重排,得到F(R0,K1)
            // L0与F(R0,K1)进行按位异或运算得到R1
            temp.delete(0, temp.length());// 清空temp,用于存放F(R0,K1)
            for (char c : Table.PMP_TABLE) {
                temp.append(sBoxOutPut(sBox).charAt(c - 1));
            }
            R1 = stringXOROperation(temp.toString(), L0);
            eR0.delete(0, eR0.length());// 清空E(R0)
            // 覆盖进行下一轮
            R0 = R1;
            L0 = L1;
        }
        // 初始化明文
        StringBuilder plaintext = new StringBuilder();
        // 交换L16,R16位置
        String RL = R1 + L1;
        // RL经过逆初始置换得到64位二进制明文
        for (char c : Table.ReIP_TABLE) {
            plaintext.append(RL.charAt(c - 1));
        }
        // 二进制明文转成8位的明文,每8位转成一个字符
        for (int i = 0; i < 64; i += 8) {
            int c = Integer.valueOf(plaintext.substring(i, i + 8), 2);
            plaintext.append((char) c);
        }
        plaintext.delete(0, 64);
        return plaintext.toString();
    }
}
