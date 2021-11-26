/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.monazilla.migemo.Migemo;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.comparator.JFDComparator;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.dialog.ConfigurationInfo;
import com.nullfish.app.jfd2.dialog.DialogUtilities;
import com.nullfish.app.jfd2.dialog.JFDDialog;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.app.jfd2.util.MigemoInfo;
import com.nullfish.app.jfd2.util.SortUtility;
import com.nullfish.app.jfd2.util.StringHistory;
import com.nullfish.app.jfd2.util.WildCardUtil;
import com.nullfish.lib.vfs.Manipulation;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;
import com.nullfish.lib.vfs.search.FileMatcher;
import com.nullfish.lib.vfs.search.SearchManipulation;

/**
 * ファイル名検索コマンド
 * 
 * @author shunji
 */
public class FindCommand extends Command {
	/**
	 * ファイル名コンボボックス
	 */
	public static final String FILE_NAME = "file_name";

	/**
	 * 除外ファイル名コンボボックス
	 */
	public static final String FILE_NAME_EXCEPT = "file_name_exce";

	/**
	 * ディレクトリ検索チェックボックス
	 */
	public static final String SEARCH_DIRECTORY = "find_search_directory";

	/**
	 * 大文字小文字区別チェックボックス
	 */
	public static final String CASE_SENSITIVE = "find_case_sensitive";

	/**
	 * 正規表現使用チェックボックス
	 */
	public static final String USE_REGEX = "find_use_regex";

	/**
	 * migemoチェックボックス
	 */
	public static final String USE_MIGEMO = "find_use_migemo";

	/**
	 * サブディレクトリ検索チェックボックス
	 */
	public static final String SEARCH_SUB_DIRECTORY = "find_sub_directory";

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
			
			dialog.setTitle(JFDResource.LABELS.getString("title_find"));

			dialog.addMessage(JFDResource.MESSAGES.getString("message_find"));
			StringHistory history = (StringHistory) jfd.getLocalConfiguration()
					.getParam("find_history", null);
			if (history == null) {
				history = new StringHistory(50, true);
				jfd.getLocalConfiguration().setParam("find_history", history);
			}
			dialog.addComboBox(FILE_NAME, history.toArray(), null, true, true, JFDResource.LABELS.getString("find_condition"));
			StringHistory exceptHistory = (StringHistory) jfd
					.getLocalConfiguration().getParam("not_find_history", null);
			if (exceptHistory == null) {
				exceptHistory = new StringHistory(50, true);
				jfd.getLocalConfiguration().setParam("not_find_history",
						exceptHistory);
			}
			dialog.addComboBox(FILE_NAME_EXCEPT, exceptHistory.toArray(), null, true, true, JFDResource.LABELS.getString("not_find_condition"));

			dialog
					.addCheckBox(
							SEARCH_DIRECTORY,
							JFDResource.LABELS.getString("search_directory"),
							'd',
							false,
							new ConfigurationInfo(localConfig, SEARCH_DIRECTORY),
							false);
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
			String fileName = dialog.getTextFieldAnswer(FILE_NAME);
			fileName = fileName != null ? fileName : "";
			String fileNameExcept = dialog.getTextFieldAnswer(FILE_NAME_EXCEPT);
			history.add(fileName);
			exceptHistory.add(fileNameExcept);

			showProgress(1000);

			boolean searchesSubdirectory = dialog
					.isChecked(SEARCH_SUB_DIRECTORY);
			boolean searchesDirectory = dialog.isChecked(SEARCH_DIRECTORY);
			boolean caseSensitive = dialog.isChecked(CASE_SENSITIVE);
			boolean useRegex = dialog.isChecked(USE_REGEX);
			boolean useMigemo = dialog.isChecked(USE_MIGEMO);

			int mode;

			if (searchesDirectory) {
				mode = FindCommandMatcher.DIRECTORY_CHECK;
			} else {
				if (searchesSubdirectory) {
					mode = FindCommandMatcher.DIRECTORY_FAIL;
				} else {
					mode = FindCommandMatcher.DIRECTORY_PASS;
				}
			}

			FileMatcher matcher;
			
			if(MigemoInfo.usesMigemo() && useMigemo) {
				matcher = new RegexMatcher(Migemo.lookup(fileName), fileNameExcept, mode,
						false);
			} else if(useRegex) {
				matcher = new RegexMatcher(fileName, fileNameExcept, mode,
						caseSensitive);
			} else {
				if(fileName.indexOf("*") >= 0) {
					fileName = WildCardUtil.wildCard2Regex(fileName);
					matcher = new RegexMatcher(fileName, fileNameExcept, mode,
							caseSensitive);
				} else {
					matcher = new NormalMatcher(fileName, fileNameExcept, mode,
							caseSensitive);
				}
			}
			
			VFile[] files = model.getMarkedFiles();
			if(files == null || files.length == 0) {
				files = model.getFiles();
			}
							
			SearchManipulation searchManipulation = new SearchManipulation(
					files, searchesSubdirectory, matcher);
			searchManipulation.setParentManipulation(this);

			Manipulation[] children = { searchManipulation };
			super.setChildManipulations(children);

			searchManipulation.start();

			model.setComparator(new JFDComparator(SortUtility.createComparators(jfd)));

			VFile[] matchedFilesArray = searchManipulation.getMatchedFiles();
			model.setFiles(model.getCurrentDirectory(), matchedFilesArray,
					model.getSelectedFile());
		} finally {
			if (dialog != null) {
				dialog.dispose();
			}
		}
	}

	private abstract class FindCommandMatcher implements FileMatcher {
		private int mode;

		private boolean caseSensitive = false;

		public static final int DIRECTORY_FAIL = 0;

		public static final int DIRECTORY_PASS = 1;

		public static final int DIRECTORY_CHECK = 2;

		public FindCommandMatcher(int mode, boolean caseSensitive) {
			this.mode = mode;
			this.caseSensitive = caseSensitive;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.nullfish.lib.vfs.search.FileMatcher#matches(com.nullfish.lib.vfs.VFile)
		 */
		public final boolean matches(VFile file) {
			try {
				if (file.isDirectory(FindCommand.this)) {
					switch (mode) {
					case DIRECTORY_FAIL:
						return false;
					case DIRECTORY_PASS:
						return true;
					default:
					}
				}
				String fileName = file.getName();
				if (!caseSensitive) {
					fileName = fileName.toLowerCase();
				}

				return check(fileName);
			} catch (VFSException e) {
				return false;
			}
		}

		public abstract boolean check(String fileName);
	}

	private class NormalMatcher extends FindCommandMatcher {
		String condition;

		String conditionExcept;
		
		/**
		 * @param mode
		 * @param caseSensitive
		 */
		public NormalMatcher(String condition, String conditionExcept, int mode, boolean caseSensitive) {
			super(mode, caseSensitive);
			condition = condition  != null ? condition : "";
			conditionExcept = conditionExcept  != null ? conditionExcept : "";
			if (caseSensitive) {
				this.condition = condition;
				this.conditionExcept = conditionExcept;
			} else {
				this.condition = condition.toLowerCase();
				this.conditionExcept = conditionExcept;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.nullfish.app.jfd2.command.embed.FindCommand.FindCommandMatcher#check(java.lang.String)
		 */
		public boolean check(String fileName) {
			if(conditionExcept != null && conditionExcept.length() > 0 && fileName.indexOf(conditionExcept) != -1) {
				return false;
			}
			return fileName.indexOf(condition) != -1;
		}
	}

	private class RegexMatcher extends FindCommandMatcher {
		Pattern pattern;

		Pattern patternExcept;
		
		/**
		 * @param mode
		 * @param caseSensitive
		 */
		public RegexMatcher(String condition, String conditionExcept, int mode, boolean caseSensitive) {
			super(mode, caseSensitive);
			condition = condition  != null ? condition : "";
			conditionExcept = conditionExcept  != null ? conditionExcept : "";
			if (!caseSensitive) {
				condition = condition.toLowerCase();
				conditionExcept = conditionExcept.toLowerCase();
			}	
			try {
				pattern = Pattern.compile(condition);
			} catch (Exception e) {
			}
			try {
				if(conditionExcept.length() > 0) {
					patternExcept = Pattern.compile(conditionExcept);
				}
			} catch (Exception e) {
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.nullfish.app.jfd2.command.embed.FindCommand.FindCommandMatcher#check(java.lang.String)
		 */
		public boolean check(String fileName) {
			if(pattern == null) {
				return false;
			}
			if(patternExcept != null) {
				Matcher matcher = patternExcept.matcher(fileName);
				if(matcher.find()) {
					return false;
				}
			}
			Matcher matcher = pattern.matcher(fileName);
			return matcher.find();
		}
	}


	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return true;
	}
}
