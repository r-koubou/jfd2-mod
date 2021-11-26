package test;

import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPTest {
	public static void main(String[] args) {
		try {
			FTPClient ftp = new FTPClient();
			ftp.connect("hogehoge.sakura.ne.jp");

			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				System.out.println(reply);
				System.exit(1);
			}

			if (!ftp.login("hogehoge", "tkcstiis")) {
				ftp.disconnect();
				System.exit(1);
			}

			if (!ftp.setFileType(FTPClient.BINARY_FILE_TYPE)) {
				System.exit(1);
			}

			// タイムアウトしないように設定
			ftp.setDataTimeout(Integer.MAX_VALUE);
			ftp.setControlEncoding("EUC-JP");
			ftp.storeFile("www/ikemen/自己紹介.txt", new FileInputStream( "/Volumes/USER/text/自己紹介.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
