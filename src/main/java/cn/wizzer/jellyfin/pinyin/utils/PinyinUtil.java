package cn.wizzer.jellyfin.pinyin.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author wizzer.cn
 */
public class PinyinUtil {
    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    /**
     * 将汉字转换为全拼
     */
    public static String getPingYin(String name) {
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        char[] charArray = name.toCharArray();
        StringBuilder pinyin = new StringBuilder();
        try {
            for (int i = 0; i < charArray.length; i++) {
                if (Character.toString(charArray[i]).matches("[\\u4E00-\\u9FA5]+")) {
		    String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(charArray[i], format);
                    if (pinyinArr.length > 0) {
                        pinyin.append(pinyinArr[0]);
                    } else {
                        pinyin.append(charArray[i]);
                    }
                } else {
                    pinyin.append(charArray[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return pinyin.toString();
    }

    /**
     * 返回中文的首字母
     *
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {

        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    /**
     * 将字符串转移为ASCII码
     *
     * @param cnStr
     * @return
     */
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }
}
