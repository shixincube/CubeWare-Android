package com.common.utils;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author PengZhenjin
 * @date 2017-1-10
 */
public class StringUtil {

    /**
     * 判断是否为空
     *
     * @param text
     *
     * @return
     */
    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

    /**
     * 获取指定数量空格
     *
     * @param spaces
     *
     * @return
     */
    public static String getBlank(int spaces) {
        String number = spaces <= 0 ? "" : String.valueOf(spaces);
        return String.format("%" + number + "s", "");
    }

    /**
     * 获取百分比形式的字符串
     *
     * @param percent
     *
     * @return
     */
    public static String getPercentString(float percent) {
        return String.format(Locale.US, "%d%%", (int) (percent * 100));
    }

    /**
     * 删除字符串中的空白符
     *
     * @param content
     *
     * @return String
     */
    public static String removeBlanks(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder buff = new StringBuilder();
        buff.append(content);
        for (int i = buff.length() - 1; i >= 0; i--) {
            if (' ' == buff.charAt(i) || ('\n' == buff.charAt(i)) || ('\t' == buff.charAt(i)) || ('\r' == buff.charAt(i))) {
                buff.deleteCharAt(i);
            }
        }
        return buff.toString();
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     *
     * @param str
     *
     * @return
     */
    public static String replaceSpec(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 删除字符串中非数字的所有字符
     *
     * @param content
     *
     * @return
     */
    public static String removeNonNumeric(String content) {
        if (content == null) {
            return null;
        }
        content = content.replaceAll("[^\\d]", "");
        return content;
    }

    /**
     * 删除字符串中的指定字符
     *
     * @param content
     * @param target
     *
     * @return String
     */
    public static String removeDots(String content, char target) {
        String deleteString = "";
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) != target) {
                deleteString += content.charAt(i);
            }
        }
        return deleteString;
    }

    /**
     * 获取32位uuid
     *
     * @return
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成唯一号
     *
     * @return
     */
    public static String get36UUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 生成MD5
     *
     * @param source
     *
     * @return
     */
    public static String makeMd5(String source) {
        return MD5Util.getStringMD5(source);
    }

    /**
     * 计算字符串的char总数量
     *
     * @param str
     *
     * @return
     */
    public static int counterChars(String str) {

        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int tmp = (int) str.charAt(i);
            if (tmp > 0 && tmp < 127) {
                count += 1;
            }
            else {
                count += 1;
            }
        }
        return count;
    }

    /**
     * 修改字符串中的unicode码
     *
     * @param s 源str
     *
     * @return 修改后的str
     */
    public static String decode2(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] == 'u') {
                char cc = 0;
                for (int j = 0; j < 4; j++) {
                    char ch = Character.toLowerCase(chars[i + 2 + j]);
                    if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
                        cc |= (Character.digit(ch, 16) << (3 - j) * 4);
                    }
                    else {
                        cc = 0;
                        break;
                    }
                }
                if (cc > 0) {
                    i += 5;
                    sb.append(cc);
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String encode(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 3);
        for (char c : s.toCharArray()) {
            if (c < 256) {
                sb.append(c);
            }
            else {
                sb.append("\\u");
                sb.append(Character.forDigit((c >>> 12) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 8) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 4) & 0xf, 16));
                sb.append(Character.forDigit((c) & 0xf, 16));
            }
        }
        return sb.toString();
    }

    /**
     * 获取修复后的字符串
     *
     * @param oldStr
     *
     * @return
     */
    public static String getFixStr(String oldStr) {
        String newStr = encode(oldStr);
        if (newStr.contains("\\u202e")) {
            newStr = newStr.replace("\\u202e", "");
            StringBuilder sb = new StringBuilder(decode2(newStr));
            newStr = sb.reverse().toString();
        }
        else {
            return oldStr;
        }
        return newStr;
    }

    /**
     * 获取strings字符串中所有str字符所在的下标
     *
     * @param content 母字符串
     * @param key     子字符串
     *
     * @return 字符串在母字符串中下标集合，如果母字符串中不包含子字符串，集合长度为零
     */
    public static List<Integer> getIndex(String content, String key) {
        List<Integer> indexes = new ArrayList<>();
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(content)) {
            return indexes;
        }
        int len = key.length();
        int index = content.indexOf(key);
        while (index > -1) {
            indexes.add(index);
            index = content.indexOf(key, index + len);
        }
        return indexes;
    }
}
