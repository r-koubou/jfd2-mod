/*
 * Created on 2004/08/22
 *
 */
package com.nullfish.app.jfd2.util;

import java.util.ArrayList;
import java.util.List;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.comparator.ExtensionComparator;
import com.nullfish.app.jfd2.comparator.FileComparator;
import com.nullfish.app.jfd2.comparator.FileTypeComparator;
import com.nullfish.app.jfd2.comparator.NameComparator;
import com.nullfish.app.jfd2.comparator.NumberNameComparator;
import com.nullfish.app.jfd2.comparator.ParentNameComparator;
import com.nullfish.app.jfd2.comparator.PermissionComparator;
import com.nullfish.app.jfd2.comparator.SizeComparator;
import com.nullfish.app.jfd2.comparator.TimestampComparator;
import com.nullfish.app.jfd2.config.Configuration;

/**
 * @author shunji
 *
 */
public class SortUtility {

	public static final String CONDITION = "sort-condition";

	public static final String NAME = "name";
	public static final String EXTENSION = "extension";
	public static final String SIZE = "size";
	public static final String DATE = "date";

	public static final String ORDER = "sort-order";
	public static final String ASCEND = "ascend";
	public static final String DESCEND = "descend";

	public static final String SORT_NAME_NUMBER = "sorts_number";
	public static final String NO_DISTINCT_FILE_DIRECTORY = "sort_file_directory_distinction";
	public static final String NO_ATTRIBUTE_SORT = "sort_attribute";
	public static final String DIRECTORY_FIRST = "directory_first";
	public static final String NO_SORT = "no_sort";

	/**
	 * 設定から比較クラスの配列を生成する。
	 * ソートしない場合はnullを返す。
	 * 
	 * @param conditionName
	 * @return
	 */
	public static FileComparator[] createComparators(JFD jfd) {
		Configuration config = jfd.getLocalConfiguration();
		
		Boolean noSort = (Boolean)config.getParam(SortUtility.NO_SORT, Boolean.FALSE);
		if(noSort != null && noSort.booleanValue()) {
			return null;
		}
	
		
		boolean order = true;
		String orderStr = (String)config.getParam(SortUtility.ORDER, ASCEND);
		if(orderStr != null) {
			order = ASCEND.equals(orderStr);
		}
		
		List comparators = new ArrayList();
		
		//	親ディレクトリでコンペア
		if(jfd.showsRelativePath()) {
			comparators.add(new ParentNameComparator(order));
		}
		
		//	ディレクトリ優先
		Boolean dirFirst = (Boolean)config.getParam(SortUtility.DIRECTORY_FIRST, Boolean.FALSE);
		if(dirFirst == null || !dirFirst.booleanValue()) {
			Boolean noFileDirDistinct = (Boolean)config.getParam(SortUtility.NO_DISTINCT_FILE_DIRECTORY, Boolean.FALSE);
			if(noFileDirDistinct == null || !noFileDirDistinct.booleanValue()) {
				comparators.add(new FileTypeComparator());
			}

			Boolean noAttrSort = (Boolean)config.getParam(SortUtility.NO_ATTRIBUTE_SORT, Boolean.FALSE);
			if(noAttrSort == null || !noAttrSort.booleanValue()) {
				comparators.add(new PermissionComparator());
			}
		} else {
			Boolean noAttrSort = (Boolean)config.getParam(SortUtility.NO_ATTRIBUTE_SORT, Boolean.FALSE);
			if(noAttrSort == null || !noAttrSort.booleanValue()) {
				comparators.add(new PermissionComparator());
			}
		
			Boolean noFileDirDistinct = (Boolean)config.getParam(SortUtility.NO_DISTINCT_FILE_DIRECTORY, Boolean.FALSE);
			if(noFileDirDistinct == null || !noFileDirDistinct.booleanValue()) {
				comparators.add(new FileTypeComparator());
			}
		}
		
		FileComparator nameComparator;
		Boolean sortsNameNumber = (Boolean)config.getParam(SortUtility.SORT_NAME_NUMBER, Boolean.FALSE);
		if(sortsNameNumber != null && sortsNameNumber.booleanValue()) {
			nameComparator = new NumberNameComparator(order);
		} else {
			nameComparator = new NameComparator(order);
		}
				
		String conditionName = (String)config.getParam(SortUtility.CONDITION, NAME);
		if (conditionName == null || SortUtility.NAME.equals(conditionName)) {
			comparators.add(nameComparator);
		} else if (SortUtility.EXTENSION.equals(conditionName)) {
			comparators.add(new ExtensionComparator(order));
			comparators.add(nameComparator);
		} else if (SortUtility.SIZE.equals(conditionName)) {
			comparators.add(new SizeComparator(order));
			comparators.add(nameComparator);
		} else {
			comparators.add(new TimestampComparator(order));
			comparators.add(nameComparator);
		}
	
		FileComparator[] rtn = new FileComparator[comparators.size()];
		rtn = (FileComparator[])comparators.toArray(rtn);
		return rtn;
	}

}
