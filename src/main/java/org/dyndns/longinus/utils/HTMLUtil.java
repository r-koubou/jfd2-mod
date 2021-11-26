/*
 * 2004/10/22 getAnchor, getStripHTMLTagのコードを最適化.
 * 2004/09/24 getStringメソッド追加.
 * 2004/09/09 getReplaceHTMLEntityメソッドを追加.
 * 2004/09/07 convertTagSpaceメソッドを追加.
 * 2004/09/06 getAnchor(str, title, cut)メソッドで, 大文字URLが解釈されない不具合修正.
 * 2004/03/05 getAnchorメソッドでタイトルタグが不正になる不具合修正.
 * 2004/02/20 getLinkAnchorメソッドの正規表現をOROパッケージに変更. 文字列カット機能追加.
 * 2004/02/20 新規作成. 一部メソッドをStringUtilクラスより移動.
 */

package org.dyndns.longinus.utils;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.PatternMatcherInput;

/**
 * HTMLを表示する上で利用できるユーティリティクラス.
 */
public class HTMLUtil extends StringUtil {
    public static final int TAG_TO_SPACE = 1;
    public static final int SPACE_TO_TAG = 0;
    public static final int CONV_HTML_ENTITY= 0;
    public static final int CONV_ESCAPE_ENTITY = 1;

    public static final String REGEXP_VALID_URL = "s?https?:\\/\\/[-_.!~*'()a-zA-Z0-9;\\/?:@&=+$,%#]+";
    public static final String REGEXP_VALID_MAIL = "([a-z0-9_]|\\-|\\.)+@(([a-z0-9_]|\\-)+\\.)+[a-z]{2,4}";

    private static final String REGEXP_STRIP_TEXT = "[^<]*";
    private static final String REGEXP_STRIP_TAG_ = "[^\"'<>]*(?:\"[^\"]*\"[^\"'<>]*|'[^']*'[^\"'<>]*)*(?:>|(?=<)|$(?!\n))";
    private static final String REGEXP_STRIP_COMMENT = "<!(?:--[^-]*-(?:[^-]+-)*?-(?:[^>-]*(?:-[^>-]+)*?)??)*(?:>|$(?!\n)|--.*$)";
    private static final String REGEXP_STRIP_TAG = REGEXP_STRIP_COMMENT + "|<" + REGEXP_STRIP_TAG_;
    private static final String REGEXP_CRLF_SET = "\r\n|\n|\r";
    private static final char CHAR_SPACE = ' ';
    private static final char CHAR_TAB = '\t';

    private static final String[][] MINIMUM_SPECIAL_CHARACTERS =
        {
            {"&quot;", "\""}, {"&amp;", "&"}, {"&lt;", "<"}, {"&gt;", ">"}, {"&nbsp;", " "}
        };
    private static final String[][] SPECIAL_CHARACTERS =
        {
            {"&quot;", "\""}, {"&amp;", "&"}, {"&lt;", "<"}, {"&gt;", ">"}, {"&nbsp;", " "}, {"&iexcl;", "!"},
            {"&cent;", "¢"}, {"&pound;", "£"}, {"&curren;", "?"}, {"&yen;", "\\"}, {"&brvbar;", "|"}, {"&sect;", "§"},
            {"&uml;", "¨"}, {"&copy;", "c"}, {"&ordf;", "a"}, {"&laquo;", "≪"}, {"&not;", "¬"}, {"&shy;", "-"},
            {"&reg;", "R"}, {"&macr;", "￣"}, {"&deg;", "°"}, {"&plusmn;", "±"}, {"&sup2;", "2"},
            {"&sup3;", "3"},{"&acute;", "´"}, {"&micro;", "μ"}, {"&para;", "¶"}, {"&middot;", "・"}, {"&cedil;", "，"},
            {"&sup1;", "1"}, {"&ordm;", "o"}, {"&raquo;", "≫"}, {"&frac14;", "?"}, {"&frac12;", "?"}, {"&frac34;", "?"},
            {"&iquest;", "?"}, {"&Agrave;", "A"}, {"&Aacute;", "A"}, {"&Acirc;", "A"}, {"&Atilde;", "A"}, {"&Auml;", "A"},
            {"&Aring;", "A"}, {"&AElig;", "A"}, {"&Ccedil;", "C"}, {"&Egrave;", "E"}, {"&Eacute;", "E"}, {"&Ecirc;", "E"},
            {"&Euml;", "E"}, {"&Igrave;", "I"}, {"&Iacute;", "I"}, {"&Icirc;", "I"}, {"&Iuml;", "I"}, {"&ETH;", "D"},
            {"&Ntilde;", "N"}, {"&Ograve;", "O"}, {"&Oacute;", "O"}, {"&Ocirc;", "O"}, {"&Otilde;", "O"}, {"&Ouml;", "O"},
            {"&times;", "×"}, {"&Oslash;", "O"}, {"&Ugrave;", "U"}, {"&Uacute;", "U"}, {"&Ucirc;", "U"}, {"&Uuml;", "U"},
            {"&Yacute;", "Y"}, {"&THORN;", "T"}, {"&szlig;", "s"}, {"&agrave;", "a"}, {"&aacute;", "a"}, {"&acirc;", "a"},
            {"&atilde;", "a"}, {"&auml;", "a"}, {"&aring;", "a"}, {"&aelig;", "a"}, {"&ccedil;", "c"}, {"&egrave;", "e"},
            {"&eacute;", "e"}, {"&ecirc;", "e"}, {"&euml;", "e"}, {"&igrave;", "i"}, {"&iacute;", "i"}, {"&icirc;", "i"},
            {"&iuml;", "i"}, {"&eth;", "d"}, {"&ntilde;", "n"}, {"&ograve;", "o"}, {"&oacute;", "o"}, {"&ocirc;", "o"},
            {"&otilde;", "o"}, {"&ouml;", "o"}, {"&divide;", "÷"}, {"&oslash;", "o"}, {"&ugrave;", "u"}, {"&uacute;", "u"},
            {"&ucirc;", "u"}, {"&uuml;", "u"}, {"&yacute;", "y"}, {"&thorn;", "t"}, {"&yuml;", "y"}, {"&OElig;", "?"},
            {"&oelig;", "?"}, {"&Scaron;", "?"}, {"&scaron;", "?"}, {"&Yuml;", "?"}, {"&fnof;", "?"}, {"&circ;", "?"},
            {"&tilde;", "?"}, {"&Alpha;", "Α"}, {"&Beta;", "Β"}, {"&Gamma;", "Γ"}, {"&Delta;", "Δ"}, {"&Epsilon;", "Ε"},
            {"&Zeta;", "Ζ"}, {"&Eta;", "Η"}, {"&Theta;", "Θ"}, {"&Iota;", "Ι"}, {"&Kappa;", "Κ"}, {"&Lambda;", "Λ"},
            {"&Mu;", "Μ"}, {"&Nu;", "Ν"}, {"&#xi;", "Ξ"}, {"&Omicron;", "Ο"}, {"&Pi;", "Π"}, {"&Rho;", "Ρ"},
            {"&Sigma;", "Σ"}, {"&Tau;", "Τ"}, {"&Upsilon;", "Υ"}, {"&Phi;", "Φ"}, {"&Chi;", "Χ"}, {"&Psi;", "Ψ"},
            {"&Omega;", "Ω"}, {"&alpha;", "α"}, {"&beta;", "β"}, {"&gamma;", "γ"}, {"&delta;", "δ"}, {"&epsilon;", "ε"},
            {"&zeta;", "ζ"}, {"&eta;", "η"}, {"&theta;", "θ"}, {"&iota;", "ι"}, {"&kappa;", "κ"}, {"&lambda;", "λ"},
            {"&mu;", "μ"}, {"&nu;", "ν"}, {"&#xi;", "ξ"}, {"&omicron;", "ο"}, {"&pi;", "π"}, {"&rho;", "ρ"},
            {"&sigmaf;", "?"}, {"&sigma;", "σ"}, {"&tau;", "τ"}, {"&upsilon;", "υ"}, {"&phi;", "φ"}, {"&chi;", "χ"},
            {"&psi;", "ψ"}, {"&omega;", "ω"}, {"&thetasym;", "?"}, {"&upsih;", "?"}, {"&piv;", "?"}, {"&bull;", "?"},
            {"&hellip;", "…"}, {"&prime;", "′"}, {"&Prime;", "″"}, {"&oline;", "?"}, {"&frasl;", "?"}, {"&weierp;", "?"},
            {"&image;", "?"}, {"&real;", "?"}, {"&trade;", "?"}, {"&alefsym;", "?"}, {"&larr;", "←"}, {"&uarr;", "↑"},
            {"&rarr;", "→"}, {"&darr;", "↓"}, {"&harr;", "?"}, {"&crarr;", "?"}, {"&lArr;", "?"}, {"&uArr;", "?"},
            {"&rArr;", "⇒"}, {"&dArr;", "?"}, {"&hArr;", "⇔"}, {"&forall;", "∀"}, {"&part;", "∂"}, {"&exist;", "∃"},
            {"&empty;", "?"}, {"&nabla;", "∇"}, {"&isin;", "∈"}, {"&notin;", "?"}, {"&ni;", "∋"}, {"&prod;", "?"},
            {"&sum;", "�"}, {"&minus;", "?"}, {"&lowast;", "?"}, {"&radic;", "√"}, {"&prop;", "∝"}, {"&infin;", "∞"},
            {"&ang;", "∠"}, {"&and;", "∧"}, {"&or;", "∨"}, {"&cap;", "∩"}, {"&cup;", "∪"}, {"&int;", "∫"},
            {"&there4;", "∴"}, {"&sim;", "?"}, {"&cong;", "?"}, {"&asymp;", "?"}, {"&ne;", "≠"}, {"&equiv;", "≡"},
            {"&le;", "?"}, {"&ge;", "?"}, {"&sub;", "⊂"}, {"&sup;", "⊃"}, {"&nsub;", "?"}, {"&sube;", "⊆"},
            {"&supe;", "⊇"}, {"&oplus;", "?"}, {"&otimes;", "?"}, {"&perp;", "⊥"}, {"&sdot;", "?"}, {"&lceil;", "?"},
            {"&rceil;", "?"}, {"&lfloor;", "?"}, {"&rfloor;", "?"}, {"&lang;", "?"}, {"&rang;", "?"}, {"&loz;", "?"},
            {"&spades;", "?"}, {"&clubs;", "?"}, {"&hearts;", "?"}, {"&diams;", "?"}, {"&ensp;", "?"}, {"&emsp;", "?"},
            {"&thinsp;", "?"}, {"&zwnj;", "?"}, {"&zwj;", "?"}, {"&lrm;", "?"}, {"&rlm;", "?"}, {"&ndash;", "?"},
            {"&mdash;", "?"}, {"&lsquo;", "‘"}, {"&rsquo;", "’"}, {"&sbquo;", "?"}, {"&ldquo;", "“"}, {"&rdquo;", "”"},
            {"&bdquo;", "?"}, {"&dagger;", "†"}, {"&Dagger;", "‡"}, {"&permil;", "‰"}, {"&lsaquo;", "?"}, {"&rsaquo;", "?"},
            {"&euro;", "?"}
        };

    /**
     * 空文字(あるいはnull)をHTMLの空白文字に置き換える.
     *
     * @param str 対象となる文字列.
     * @return 対象文字列が空文字, あるいはnullであった場合, HTMLエンティティの「&amp;nbsp;」を返す.
     */
    public static String getString(String str) {
        if (isString(str)) {
            return str;
        }

        return "&nbsp;";
    }

    /**
     * 対象文字列内のタグとスペース(文字)の置換を行う.
     *
     * @param str 対象となる文字列.
     * @param conv 置換するタイプ. TAG_TO_SPACE, SPACE_TO_TAGのいずれかを指定.
     * @param size 変換するキャラサイズを指定する.
     * @return タグとスペースが置換された文字列を返す.
     */
    public static String convertTagSpace(String str, int conv, int size) {
        StringBuffer sb = new StringBuffer(size);
        String from = null;
        String to = null;

        if (conv == TAG_TO_SPACE) {
            for (int i = 0; i < size; i++) {
                sb.append(CHAR_SPACE);
            }

            from = Character.toString(CHAR_TAB);
            to = sb.toString();

        } else {
            for (int i = 0; i < size; i++) {
                sb.append(CHAR_TAB);
            }

            from = sb.toString();
            to = Character.toString(CHAR_TAB);
        }

        return replaceAll(str, from, to);
    }

    /**
     * 対象文字列内のタグとスペース(文字)の置換を行う.
     *
     * @param str 対象となる文字列.
     * @param convert 置換するタイプ. TAG_TO_SPACE, SPACE_TO_TAGのいずれかを指定.
     * @return タグとスペースが置換された文字列を返す.
     */
    public static String convertTagSpace(String str, int convert) {
        return convertTagSpace(str, convert, 2);
    }

    /**
     * プレーンな文字列に埋め込まれているURIに, HTMLタグのアンカーを付与する.
     *
     * @param str 対象となるプレーン文字列.
     * @param title hrefのtitle属性にURIを設定する場合はtrue.
     * @param cut 画面上でアンカー文字列を表示するバイト数を指定.
     * @return アンカー文字列を返す.
     */
    public static String getAnchor(String str) {
        return getAnchor(str, false, -1);
    }

    /**
     * プレーンな文字列に埋め込まれているURIに, HTMLタグのアンカーを付与する.
     *
     * @param str 対象となるプレーン文字列.
     * @param title hrefのtitle属性にURIを設定する場合はtrue.
     * @param cut 画面上でアンカー文字列を表示するバイト数を指定.
     * @return アンカー文字列を返す.
     */
	public static String getAnchor(String str, boolean title, int cut) {
		if (isString(str)) {
			Perl5Util perl = new Perl5Util();
			String ret;
			
			// リンク文字列の切り取り無し(単純置換).
			if (cut == -1) {
				StringBuffer sb;

				// URLのマッチング.
				sb = new StringBuffer();
				sb.append("s/(");
				sb.append(REGEXP_VALID_URL);
				sb.append(")/<a href=\"$1\"");

				if (title) {
					sb.append(" title=\"$1\"");
				}
	
				sb.append(">$1<\\/a>/gi");
				str = perl.substitute(sb.toString(), str);

				// メールアドレスのマッチング.
				sb = new StringBuffer();
				sb.append("s/(");
				sb.append(REGEXP_VALID_MAIL);
				sb.append(")/<a href=\"mailto:$1\"");

				if (title) {
					sb.append(" title=\"$1\"");
				}

				sb.append(">$1<\\/a>/gi");
				ret = perl.substitute(sb.toString(), str);

			// リンク文字列の切り取り有り.
			} else {
				// URLのマッチング
				PatternMatcherInput pmi = new PatternMatcherInput(str);
				StringBuffer sb = new StringBuffer();
				sb.append("/(");
				sb.append(REGEXP_VALID_URL);
				sb.append(")/i");

				String pattern = sb.toString();
				sb = new StringBuffer();
				int offset = 0;
				
				while (perl.match(pattern, pmi)) {
					sb.append(perl.preMatch().substring(offset));
					sb.append("<a href=\"");
					sb.append(perl.group(1));
					sb.append("\"");

					if (title) {
					    sb.append(" title=\"");
					    sb.append(perl.group(1));
					    sb.append("\"");
					}

					sb.append(">");
					sb.append(getCutString(perl.group(1), cut));
					sb.append("</a>");

					offset = perl.endOffset(0);
				}
	
				if (offset > 0) {
				    sb.append(perl.postMatch());
					ret = sb.toString();
				} else {
					ret = str;
				}

				// メールアドレスのマッチング.
				pmi = new PatternMatcherInput(ret);
				sb = new StringBuffer();
				sb.append("/(");
				sb.append(REGEXP_VALID_MAIL);
				sb.append(")/");

				pattern = sb.toString();
				sb = new StringBuffer();
				offset = 0;

				while (perl.match(pattern, pmi)) {
				    sb.append(perl.preMatch().substring(offset));
				    sb.append("<a href=\"mailto:");
				    sb.append(perl.group(1));
				    sb.append("\"");

					if (title) {
					    sb.append(" title=\"");
					    sb.append(perl.group(1));
					    sb.append("\"");
					}

					sb.append(">");
					sb.append(getCutString(perl.group(1), cut));
					sb.append("</a>");
					offset = perl.endOffset(0);
				}

				if (offset > 0) {
				    sb.append(perl.postMatch());
					ret = sb.toString();
				}
				
			}

			return ret;
		}
		
		return str;
	}
    /**
     * 改行コードをHTMLの改行タグに置換する.
     *
     * @param str 対象となる文字列.
     * @return 改行コードをHTMLのBRタグに置換した文字列を返す.
     */
    public static String getLineFeed(String str) {
        return str.replaceAll(REGEXP_CRLF_SET, "<br>");
    }

    /**
     * HTMLエンティティをエスケープする.
     *
     * @param str 対象となる文字列.
     * @return HTMLエンティティをエスケープした文字列を返す.
     */
    public static String getHTMLEscape(String str) {
        if (isString(str)) {
            StringBuffer buf = new StringBuffer();
            String entity = null;

            char[] c = str.toCharArray();
            int i, last;
            int j = c.length;

            for (i = 0, last = 0; i < j; i++) {
                switch (c[i]) {
                    case '<':
                        entity = "&lt;";
                        break;
                    case '>':
                        entity = "&gt;";
                        break;
                    case '&':
                        entity = "&amp;";
                        break;
                    case '"':
                        entity = "&quot;";
                        break;
                }

                if (entity != null) {
                    buf.append(c, last, i - last);
                    buf.append(entity);

                    entity = null;
                    last = i + 1;
                }
            }

            if (last < j) {
                buf.append(c, last, i - last);
            }

            return buf.toString();

        } else {
            return str;
        }
    }

    /**
     * 指定された文字列からHTMLタグを取り除く.
     * (正規表現パターンについてはhttp://www.din.or.jp/~ohzaki/regex.htmのPerlソースから移植)
     *
     * @param input HTMLタグを含む文字列
     * @return タグを除去した文字列
     */
    public static String getStripHTMLTag(String str) {
        if (isString(str)) {
            Perl5Util perl = new Perl5Util();
            PatternMatcherInput pmi = new PatternMatcherInput(str);

            StringBuffer sb = new StringBuffer();
            sb.append("m/(");
            sb.append(REGEXP_STRIP_TEXT);
            sb.append(")(");
            sb.append(REGEXP_STRIP_TAG);
            sb.append(")?/s");

            String[] group = new String[2];
            String pattern = sb.toString();
            sb = new StringBuffer();

            while(perl.match(pattern, pmi)){
                group[0] = perl.group(1);
                group[1] = perl.group(2);

                if (group[0] == null) {
                    group[0] = "";
                }

                if (group[1] == null) {
                    group[1] = "";
                }

                sb.append(group[0]);
                PatternMatcherInput pPmi = new PatternMatcherInput(group[1]);

                if (perl.match("/^<(XMP|PLAINTEXT|SCRIPT)(?![0-9A-Za-z])/i", pPmi)) {
                    StringBuffer buf = new StringBuffer();
                    buf.append("/(.*?)(?:</?");
                    buf.append(perl.group(1));
                    buf.append("(?![0-9A-Za-z])");
                    buf.append(REGEXP_STRIP_TAG);
                    buf.append("|$)/si");

                    perl.match(pattern.toString(), pmi);
                    sb.append(perl.group(1));
                }
            }

            return getReplaceHTMLEntity(sb.toString(), CONV_ESCAPE_ENTITY, false);
        }

        return str;
    }

    /**
     * HTMLの特殊文字をエスケープ文字に変換, またはその逆を行う.
     *
     * @param str 対象となる文字列.
     * @param type 変換するタイプ. CONVERT_HTML_ENTITY, CONVERT_ESCAPE_ENTITYのいずれかを指定.
     * @param scan 全ての特殊文字を変換する場合はtrue, 最小限の文字のみを変換する場合はfalseを指定.
     * @return HTMLの特殊文字を変換した結果文字列を返す.
     */
    public static String getReplaceHTMLEntity(String str, int type, boolean scan) {
        if (isString(str)) {
            String[][] array;
            int from, to;

            if (type == CONV_HTML_ENTITY) {
                from = 1;
                to = 0;
            } else {
                from = 0;
                to = 1;
            }

            if (scan) {
                array = SPECIAL_CHARACTERS;
            } else {
                array = MINIMUM_SPECIAL_CHARACTERS;
            }

            int len = array.length;
            for (int i = 0; i < len; i++) {
                str = replaceAll(str, array[i][from], array[i][to]);
            }
        }

        return str;
    }
}
