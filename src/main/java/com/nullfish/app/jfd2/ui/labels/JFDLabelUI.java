package com.nullfish.app.jfd2.ui.labels;

public class JFDLabelUI extends javax.swing.plaf.basic.BasicLabelUI {
    protected String layoutCL(javax.swing.JLabel label, java.awt.FontMetrics fontMetrics,
            String text, javax.swing.Icon icon,
            java.awt.Rectangle viewR, java.awt.Rectangle iconR, java.awt.Rectangle textR ) {
        try{
            int dot = text.lastIndexOf('.');
            // .で始まるor終わるファイルは除外( . とか .. とか .jfd とか)
            if( dot <= 0 || dot == text.length()-1 ){
                return super.layoutCL(label, fontMetrics, text, icon, viewR, iconR, textR);
            }else{
                String ret = super.layoutCL(label, fontMetrics, text, icon, viewR, iconR, textR);
                if( ret.equals(text) ){
                    // 変化がなければ全部表示できる
                    return text;
                }else{
                    // 省略されました
                    String ext  = text.substring(dot+1);  // .を含まない
                    // 拡張子の幅を計算して、文字列計算用の表示領域から引いてやる
                    int width = fontMetrics.stringWidth(ext);
                    //viewR = new java.awt.Rectangle(viewR)
                    viewR.width -= width;  // 使い捨てみたいなので値を直接入れ替えても問題でないような感触
                    if( viewR.width < 1 ){ return ret; } // 極端に狭いときは末尾 ... のままで
                    // layoutCL()を使わないでstringWidth()で自力計算したほうが速いかもしれないが
                    // 十分な速度が出ている感じがするので放置
                    String extRet = super.layoutCL(label, fontMetrics, text, icon, viewR, iconR, textR);
                    return extRet + ext;
                }
            }
        }catch(Exception ex ){
            debug(ex);
            debug("text = ${text}");
            debug("text.length() = ${text.length()}");
        }
        return text;
    }
    /**
     * デバッグ用出力
     */
    private void debug(final Object str ){
        Runnable runnable = new Runnable() {
			
			public void run() {
				com.nullfish.app.jfd2.ext_command.window.ConsoleFrame.getInstance().println(System.currentTimeMillis() + " - " + str);
			}
		};
        javax.swing.SwingUtilities.invokeLater( runnable );
    }
}

