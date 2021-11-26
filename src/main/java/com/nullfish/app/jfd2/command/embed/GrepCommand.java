/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.monazilla.migemo.Migemo;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.dialog.ConfigurationInfo;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.MigemoInfo;
import com.nullfish.app.jfd2.util.StringHistory;
import com.nullfish.lib.EncodeDetector;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.search.FileMatcher;
import com.nullfish.lib.vfs.search.SearchManipulation;

/**
 * Grep検索コマンド
 * 
 * @author shunji
 */
public class GrepCommand extends Command {
	/**
	 * 検索条件コンボボックス
	 */
	public static final String CONDITION = "condition";

	/**
	 * エンコードコンボボックス
	 */
	public static final String ENCODE = "grep_encode";

	/**
	 * 大文字小文字区別チェックボックス
	 */
	public static final String CASE_SENSITIVE = "grep_case_sensitive";

	/**
	 * 正規表現使用チェックボックス
	 */
	public static final String USE_REGEX = "grep_use_regex";

	/**
	 * サブディレクトリ検索チェックボックス
	 */
	public static final String SEARCH_SUB_DIRECTORY = "find_sub_directory";

	/**
	 * 正規表現使用チェックボックス
	 */
	public static final String USE_MIGEMO = "grep_use_migemo";

	private VFile checking;
	
	private String encode;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDDialog dialog = null;
		try {
			JFD jfd = getJFD();
			JFDModel model = jfd.getModel();
			Configuration localConfig = jfd.getLocalConfiguration();

			dialog = DialogUtilities.createOkCancelDialog(jfd);

			dialog.setTitle(JFDResource.LABELS.getString("title_grep"));
			
			dialog.addMessage(JFDResource.MESSAGES.getString("message_grep"));
			StringHistory history = (StringHistory) jfd.getLocalConfiguration()
					.getParam("grep_history", null);
			if (history == null) {
				history = new StringHistory(50, true);
				jfd.getLocalConfiguration().setParam("grep_history", history);
			}
			dialog.addComboBox(CONDITION, history.toArray(), null, true, true, null);

			List encodeList = (List)jfd.getCommonConfiguration().getParam("grep_encode_all", null);
			dialog.addComboBox(ENCODE, encodeList, (String)jfd.getCommonConfiguration().getParam(ENCODE, "UTF-8"), false, false, null);
			
			dialog.addCheckBox(CASE_SENSITIVE, JFDResource.LABELS
					.getString("case_sensitive"), 's', false,
					new ConfigurationInfo(localConfig, CASE_SENSITIVE), false);
			dialog.addCheckBox(USE_MIGEMO, JFDResource.LABELS
					.getString("use_migemo"), 'm', false, new ConfigurationInfo(
					localConfig, USE_MIGEMO), false);
			dialog.addCheckBox(USE_REGEX, JFDResource.LABELS
					.getString("use_regex"), 'r', false, new ConfigurationInfo(
					localConfig, USE_REGEX), false);
			dialog.addCheckBox(SEARCH_SUB_DIRECTORY, JFDResource.LABELS
					.getString("search_subdirectory"), 'u', true,
					new ConfigurationInfo(localConfig, SEARCH_SUB_DIRECTORY),
					false);

			dialog.pack();
			dialog.setVisible(true);

			String answer = dialog.getButtonAnswer();
			if (answer == null || JFDDialog.CANCEL.equals(answer)) {
				return;
			}

			dialog.applyConfig();
			String condition = dialog.getTextFieldAnswer(CONDITION);
			history.add(condition);
			
			encode = dialog.getTextFieldAnswer(ENCODE);
			jfd.getCommonConfiguration().setParam(ENCODE, encode);

			showProgress(1000);

			boolean searchesSubdirectory = dialog
					.isChecked(SEARCH_SUB_DIRECTORY);
			boolean caseSensitive = dialog.isChecked(CASE_SENSITIVE);
			boolean useRegex = dialog.isChecked(USE_REGEX);
			boolean useMigemo = dialog.isChecked(USE_MIGEMO);

			FileMatcher matcher;

			if (MigemoInfo.usesMigemo() && useMigemo) {
				matcher = new RegexMatcher(Migemo.lookup(condition), false);
			} else if (useRegex) {
				matcher = new RegexMatcher(condition, caseSensitive);
			} else {
				matcher = new NormalMatcher(condition, caseSensitive);
			}

			SearchManipulation searchManipulation = new SearchManipulation(
					model.getFiles(), searchesSubdirectory, matcher);
			searchManipulation.setParentManipulation(this);

			Manipulation[] children = { searchManipulation };
			super.setChildManipulations(children);

			searchManipulation.start();

			VFile[] matchedFilesArray = searchManipulation.getMatchedFiles();
			model.setFiles(model.getCurrentDirectory(), matchedFilesArray,
					model.getSelectedFile());
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}

	private abstract class GrepCommandMatcher implements FileMatcher {
		private boolean caseSensitive = false;

		public GrepCommandMatcher(boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.nullfish.lib.vfs.search.FileMatcher#matches(com.nullfish.lib.vfs.VFile)
		 */
		public final boolean matches(VFile file) {
			checking = file;
			BufferedReader reader = null;
			try {
				if (file.isDirectory(GrepCommand.this)) {
					return false;
				}

				BufferedInputStream is = new BufferedInputStream(file.getInputStream(GrepCommand.this));
				String detectedEncode = EncodeDetector.detectEncoding(is);
				if(detectedEncode != null) {
					reader = new BufferedReader(new InputStreamReader(is, detectedEncode));
				} else {
					reader = new BufferedReader(new InputStreamReader(is, encode));
				}

				String line = null;
				while (!isStopped() && (line = reader.readLine()) != null) {
					if (!caseSensitive) {
						line = line.toLowerCase();
					}

					if (check(line)) {
						return true;
					}
				}

				return false;
			} catch (IOException e) {
				return false;
			} catch (VFSException e) {
				return false;
			} finally {
				checking = null;
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		}

		public abstract boolean check(String line);
	}

	private class NormalMatcher extends GrepCommandMatcher {
		String condition;

		/**
		 * @param mode
		 * @param caseSensitive
		 */
		public NormalMatcher(String condition, boolean caseSensitive) {
			super(caseSensitive);
			if (caseSensitive) {
				this.condition = condition;
			} else {
				this.condition = condition.toLowerCase();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.nullfish.app.jfd2.command.embed.FindCommand.FindCommandMatcher#check(java.lang.String)
		 */
		public boolean check(String line) {
			return line.indexOf(condition) != -1;
		}
	}

	private class RegexMatcher extends GrepCommandMatcher {
		Pattern pattern;

		/**
		 * @param mode
		 * @param caseSensitive
		 */
		public RegexMatcher(String condition, boolean caseSensitive) {
			super(caseSensitive);
			if (!caseSensitive) {
				condition = condition.toLowerCase();
			}
			try {
				pattern = Pattern.compile(condition);
			} catch (Exception e) {
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.nullfish.app.jfd2.command.embed.FindCommand.FindCommandMatcher#check(java.lang.String)
		 */
		public boolean check(String line) {
			Matcher matcher = pattern.matcher(line);
			return matcher.find();
		}
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
	
	/**
	 * 作業経過メッセージを取得する。
	 * 
	 * @return
	 */
	public String getProgressMessage() {
		VFile file = checking;
		if(file != null) {
			return file.getName();
		} else {
			return "";
		}
	}

	public long getProgress() {
		return PROGRESS_INDETERMINED;
	}
}
