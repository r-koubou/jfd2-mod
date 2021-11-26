/*
 * 2004/08/19 getMaskFilterStringメソッドを追加.
 * 2004/05/26 trimメソッドを追加.
 * 2004/05/26 toConvertStringメソッドで"ヴ"が相互変換されない不具合修正.
 * 2004/05/26 toConvertStringメソッドで全角英数字の一部が半角に変換されない不具合修正.
 * 2004/04/28 getNumberFormatメソッド追加.
 * 2004/03/31 toConvertString, 日本語記号の変換に対応.
 * 2004/03/30 getReplaceStringメソッドでnullが渡された場合の不具合対応.
 * 2004/03/12 getReplaceString追加
 * 2004/03/05 getCutStringメソッドで引数cutに3以下が指定された場合の不具合修正.
 * 2004/02/20 getStripHTMLTag, getSQLStringメソッドのパフォーマンス改善.
 * 2004/02/07 getStripHTMLTagメソッドで文字列から始まるHTMLを置換できない不具合修正.
 * 2004/02/05 新規作成.
 */

package org.dyndns.longinus.utils;

import java.text.NumberFormat;

/**
 * 文字列を操作するユーティリティクラス.
 */
public class StringUtil {
    /** 英数記号文字を全角文字に置換.*/
    public static final int CONV_AL_FULL_WIDTH = 1;

    /** 英数記号文字を半角文字に置換.*/
    public static final int CONV_AL_HALF_WIDTH = 2;

    /** 半角カナを全角カナに置換.*/
    public static final int CONV_JP_FULL_WIDTH = 4;

    /** 全角カナを半角カナに置換.*/
    public static final int CONV_JP_HALF_WIDTH = 8;

    /** 平仮名を片仮名に置換. */
    public static final int CONV_JP_KATAKANA = 16;

    /** 片仮名を平仮名に置換. */
    public static final int CONV_JP_HIRAGANA = 32;

    /** 日本語半角記号を全角記号に置換. */
    public static final int CONV_JP_CHARS_FULL_WIDTH = 64;

    /** 日本語全角記号を半角記号に置換. */
    public static final int CONV_JP_CHARS_HALF_WIDTH = 128;

    private static final char[][] JP_CHAR_TBL = {
        {0xff61, 0x3002}, {0xff62, 0x300c}, {0xff63, 0x300d}, {0xff64, 0x3001},
        {0xff65, 0x30fb}, {0xff66, 0x30f2}, {0xff67, 0x30a1}, {0xff68, 0x30a3}, {0xff69, 0x30a5},
        {0xff6a, 0x30a7}, {0xff6b, 0x30a9}, {0xff6c, 0x30e3},
        {0xff6d, 0x30e5}, {0xff6e, 0x30e7}, {0xff6f, 0x30c3},

        {0xff70, 0x30fc}, {0xff71, 0x30a2}, {0xff72, 0x30a4}, {0xff73, 0x30a6, 0x30F4}, {0xff74, 0x30a8},
        {0xff75, 0x30aa}, {0xff76, 0x30ab, 0x30ac}, {0xff77, 0x30ad, 0x30ae},{0xff78, 0x30af, 0x30b0}, {0xff79, 0x30b1, 0x30b2},
        {0xff7a, 0x30b3, 0x30b4}, {0xff7b, 0x30b5, 0x30b6}, {0xff7c, 0x30b7, 0x30b8},
        {0xff7d, 0x30b9, 0x30ba}, {0xff7e, 0x30bb, 0x30bc}, {0xff7f, 0x30bd, 0x30be},

        {0xff80, 0x30bf, 0x30c0}, {0xff81, 0x30c1, 0x30c2}, {0xff82, 0x30c4, 0x30c5}, {0xff83, 0x30c6, 0x30c7}, {0xff84, 0x30c8, 0x30c9},
        {0xff85, 0x30ca}, {0xff86, 0x30cb}, {0xff87, 0x30cc}, {0xff88, 0x30cd}, {0xff89, 0x30ce},
        {0xff8a, 0x30cf, 0x30d0, 0x30d1}, {0xff8b, 0x30d2, 0x30d3, 0x30d4}, {0xff8c, 0x30d5, 0x30d6, 0x30d7},
        {0xff8d, 0x30d8, 0x30d9, 0x30da}, {0xff8e, 0x30db, 0x30dc, 0x30dd}, {0xff8f, 0x30de},

        {0xff90, 0x30df}, {0xff91, 0x30e0}, {0xff92, 0x30e1}, {0xff93, 0x30e2}, {0xff94, 0x30e4},
        {0xff95, 0x30e6}, {0xff96, 0x30e8}, {0xff97, 0x30e9}, {0xff98, 0x30ea}, {0xff99, 0x30eb},
        {0xff9a, 0x30ec}, {0xff9b, 0x30ed}, {0xff9c, 0x30ef},
        {0xff9d, 0x30f3}, {0xff9e, 0x309b}, {0xff9f, 0x309c}
    };

    /** マスクする文字. */
    private static final char MASK_CHAR = '*';

    /***
     * 対象となる文字列にマスクをかける.
     *
     * @param str 対象となる文字列.
     * @param c マスク文字.
     * @return マスクでフィルタリングされた文字列を返す.
     */
    public static String getMaskFilterString(String str, char c) {
        if (isString(str)) {
            int len = str.length();
            StringBuffer sb = new StringBuffer(len);

            for (int i = 0; i < len; i++) {
                sb.append(c);
            }

            return sb.toString();
        }

        return null;
    }

    /**
     * 対象文字列にマスクをかける.
     *
     * @param str 対象となる文字列.
     * @return マスクでフィルタリングされた文字列を返す.
     */
    public static String getMaskFilterString(String str) {
        return getMaskFilterString(str, MASK_CHAR);
    }

    /**
     * 文字列内に含まれる余分な空白(全角含む)の排除, 全角(半角)文字列の統一を行う.
     * 文字列と文字列の間に1つ以上の空白があった場合, 一つの半角スペースとして扱われる.
     *
     * @param str 対象となる文字列.
     * @param conv 変換する文字フォーマットの指定.
     * @return 正規化された文字列を返す. 結果文字列がnull文字列の場合はnullを返す.
     */
    public static String trim(String str, int conv) {
        if (isString(str)) {
            String out = StringUtil.replaceAll(str, "　", " ");

            out = out.replaceAll("\\s+", " ");
            out = StringUtil.toConvertString(out, conv);
            out = out.trim();

            return out;
        }

        return null;
    }

    /**
     * 数値データをカンマ区切りにする.
     *
     * @param num 対象となる数値.
     * @return 置換後の数字文字列.
     */
    public static String getNumberFormat(int num) {
        return NumberFormat.getInstance().format(num);
    }

    /**
     * 渡された文字列がnull, または空文字(半角空白を含む)の場合, 第2引数に指定した置換文字列を返す.
     * また文字列が指定されていた場合は, 渡された文字列をそのまま返す.
     *
     * @param str 対象となる文字列.
     * @param to 置換する文字列.
     * @return 置換後の文字列.
     */
    public static String getReplaceString(String str, String to) {
        if (str == null || (str != null && str.trim().length() == 0)) {
            return to;
        }

        return str;
    }

    /**
     * クォート文字へのバックスラッシュの付加.
     *
     * @param input 対象となる文字列.
     * @return バックスラッシュが付加された文字列.
    */
    public static String getSQLString(String str) {
        if (StringUtil.isString(str)) {
            str = replaceAll(str, "'", "''");
            str = replaceAll(str, "\\\\", "\\\\\\\\");
        }

        return str;
    }

    /**
     * 文字列の置換を行う (String#replaceAllよりも高速).
     *
     * @param input 対象となる文字列.
     * @param from 置換する文字列.
     * @param to 置換文字列.
     * @return 指定した文字で置換された文字列を返す.
     */
    public static String replaceAll(String str, String from, String to) {
        int lenFrom;

        if (str == null) {
            return null;
        } else if (from == null || (lenFrom = from.length()) == 0 || to == null) {
            return str;
        }

        StringBuffer buf = new StringBuffer(str);
        int lenTo = to.length();
        int pos = buf.indexOf(from);

        while (pos >= 0) {
            buf.replace(pos, pos + lenFrom, to);
            pos = buf.indexOf(from, pos + lenTo);
        }

        return buf.toString();
    }

    /**
     * 文字列を指定した容量にカットし, サフィックス文字列(...)を付加する.
     *
     * @param str 対象となる文字列.
     * @param cut 切り取る文字数 (+3文字のサフィックス文字列が付加される).
     * @return 指定したサイズにカットされた文字列を返す.
     */
    public static String getCutString(String str, int cut) {
        int len;
        if (str == null || (len = str.length()) == 0 || len < cut) {
            return str;
        }

        return str.substring(0, cut) + "...";
    }

    /**
     * 文字列の左端に指定文字列を指定文字数分で埋める.
     * (OracleのLPAD関数をエミュレート)
     *
     * @param val 対象となる文字列.
     * @param n 置換する文字数.
     * @param s 置換する文字列.
     * @return
     */
    static public String getLPad(String str, int n, String to) {
        int len = str.length();
        if (len < n) {
            for (int i = len; i < n; i++) {
                str = to.concat(str);
            }
        }

        return str;
    }

    /**
     * 指定された値が, 文字列であるかどうかを判定する.
     *
     * @param str 対象となる文字列.
     * @return 1文字以上からなる文字列であればtrue, null, あるいは空白文字であればfalseを返す.
     */
    static public boolean isString(String str) {
        if (str != null && str.length() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 文字列を指定された形式に変換する.
     *
     * @param input 対象となる文字列.
     * @param type 変換する文字フォーマットの指定.
     * @return 変換された文字列.
    */
    public static String toConvertString(String input, final int type) {
        StringBuffer sb = new StringBuffer();
        char c;
        int m = type;
        int len = input.length();

        // 日本語全角記号を半角記号に置換.
        if ((m & 128) == 128) {
            for (int i = 0; i < len; i++) {
                c = input.charAt(i);

                switch (c) {
                    // "「"
                    case 0x300C:
                        sb.append((char) 0xFF62);
                        break;
                    // "」"
                    case 0x300D:
                        sb.append((char) 0xFF63);
                        break;
                    // "。"
                    case 0x3002:
                        sb.append((char) 0xFF61);
                        break;
                    // "、"
                    case 0x3001:
                        sb.append((char) 0xFF64);
                        break;
                    // "・"
                    case 0x30FB:
                        sb.append((char) 0xFF65);
                        break;
                    // "ー"
                    case 0x30FC:
                        sb.append((char) 0xFF70);
                        break;
                    // "゛"
                    case 0x309B:
                        sb.append((char) 0xFF9E);
                        break;
                    // "゜"
                    case 0x309C:
                        sb.append((char) 0xFF9F);
                        break;
                    default:
                        sb.append(c);
                }
            }

            m -=128;
        }

        // 日本語半角記号を全角記号に置換.
        if ((m & 64) == 64) {
            if (sb.length() > 0) {
                input = sb.toString();
                sb = sb.delete(0, len = sb.length());
            }

            for (int i = 0; i < len; i++) {
                c = input.charAt(i);

                switch (c) {
                    // "｢"
                    case 0xFF62:
                        sb.append((char) 0x300C);
                        break;
                    // "｣"
                    case 0xFF63:
                        sb.append((char) 0x300D);
                        break;
                    // "｡"
                    case 0xFF61:
                        sb.append((char) 0x3002);
                        break;
                    // "､"
                    case 0xFF64:
                        sb.append((char) 0x3001);
                        break;
                    // "･"
                    case 0xFF65:
                        sb.append((char) 0x30FB);
                        break;
                    // "-"
                    case 0xFF70:
                        sb.append((char) 0x30FC);
                        break;
                    // "ﾞ"
                    case 0xFF9E:
                        sb.append((char) 0x309B);
                        break;
                    // "ﾟ"
                    case 0xFF9F:
                        sb.append((char) 0x309C);
                        break;
                    default:
                        sb.append(c);
                }
            }

            m -=64;
        }

        // 片仮名を平仮名に置換.
        if ((m & 32) == 32) {
            if (sb.length() > 0) {
                input = sb.toString();
                sb = sb.delete(0, len = sb.length());
            }

            for (int i = 0; i < len; i++) {
                c = input.charAt(i);
                sb = (c >= 0x30a1 && c <= 0x30f3) ? sb.append((char) (c - 0x60)) : sb.append(c);
            }

            m -= 32;
        }

        // 平仮名を片仮名に置換.
        if ((m & 16) == 16) {
            if (sb.length() > 0) {
                input = sb.toString();
                sb = sb.delete(0, len = sb.length());
            }

            for (int i = 0; i < len; i++) {
                c = input.charAt(i);
                sb = (c >= 0x3041 && c <= 0x3093) ? sb.append((char) (c + 0x60)) : sb.append(c);
            }

            m -= 16;
        }

        // 全角カナを半角カナに置換.
        if ((m & 8) == 8) {
            int x;

            if (sb.length() > 0) {
                input = sb.toString();
                sb = sb.delete(0, sb.length());
            }

            for (int i = 0; i < len; i++) {
                c = input.charAt(i);

                if (c >= 0x30a1 && c <= 0x30ef || c >= 0x30f2 && c <= 0x30f4) {
                    nextChar:
                    for (int j = 0; j < 63; j++) {
                        x = JP_CHAR_TBL[j].length;

                        for (int k = 0; k < x; k++) {
                            if (c == JP_CHAR_TBL[j][k]) {
                                switch (k) {
                                    case 1:
                                        sb = sb.append(JP_CHAR_TBL[j][0]);
                                        break;
                                    case 2:
                                        sb = sb.append(JP_CHAR_TBL[j][0]).append((char) 0xff9e);
                                        break;
                                    case 3:
                                        sb = sb.append(JP_CHAR_TBL[j][0]).append((char) 0xff9f);
                                        break;
                                }

                                break nextChar;
                            }
                        }
                    }
                } else {
                    sb = sb.append(c);
                }
            }

            m -= 8;
        }

        // 半角カナを全角カナに置換.
        if ((m & 4) == 4) {
            int p = 0;
            int x;

            if (sb.length() > 0) {
                input = sb.toString();
                sb = sb.delete(0, len = sb.length());
            }

            for (int i = 0; i < len; i++) {
                c = input.charAt(i);
                x = JP_CHAR_TBL[p].length;

                if (c == 0xff9e && x >= 3) {
                    sb = sb.deleteCharAt(sb.length() - 1);
                    sb = sb.append(JP_CHAR_TBL[p][2]);
                } else if (c == 0xff9f && x == 4) {
                    sb = sb.deleteCharAt(sb.length() - 1);
                    sb = sb.append(JP_CHAR_TBL[p][3]);
                } else if (c >= 0xff60 && c <= 0xff9f) {
                    for (int j = 0; j < 63; j++) {
                        if (JP_CHAR_TBL[j][0] == c) {
                            sb.append(JP_CHAR_TBL[p = j][1]);
                            break;
                        }
                    }
                } else {
                    sb = sb.append(c);
                }
            }

            // 半角拗音文字を全角に置換.
            if ((type & 32) == 32) {
                input = sb.toString();
                sb = sb.delete(0, len = sb.length());

                for (int i = 0; i < len; i++) {
                    c = input.charAt(i);
                    sb = (c >= 0x30a1 && c <= 0x30f3) ? sb.append((char) (c - 0x60)) : sb.append(c);
                }
            }

            m -= 4;
        }

        // 英数記号文字を半角文字に置換.
        if ((m & 2) == 2) {
            if (sb.length() > 0) {
                input = sb.toString();
                sb = sb.delete(0, len = sb.length());
            }

            for (int i = 0; i < len; i++) {
                c = input.charAt(i);
                sb = (c >= 0xff01 && c <= 0xFF5A) ? sb.append((char) (c - 0xfee0)) : sb.append(c);
            }

            m -= 2;
        }

        // 片仮名を平仮名に置換
        if ((m & 1) == 1) {
            if (sb.length() > 0) {
                input = sb.toString();
                sb = sb.delete(0, len = sb.length());
            }

            for (int i = 0; i < len; i++) {
                c = input.charAt(i);
                sb = (c >= 0x21 && c <= 0x7e) ? sb.append((char) (c + 0xfee0)) : sb.append(c);
            }

            m -= 1;
        }

        return sb.toString();
    }
}
