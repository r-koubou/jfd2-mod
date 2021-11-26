/*
 * Created on 2004/06/03
 *
 */
package com.nullfish.app.jfd2.command;

import java.text.MessageFormat;

import com.nullfish.lib.vfs.exception.VFSException;

/**
 * jFD用例外
 * 
 * @author shunji
 */
public class JFDException extends VFSException {

	Object[] errorValues;
	
	/**
	 * コンストラクタ
	 * @param message	エラーメッセージ
	 */
	public JFDException(String message) {
		this(null, message, new Object[0]);
	}
	
	/**
	 * コンストラクタ
	 * @param message	エラーメッセージ
	 * @param errorValues	エラー値
	 */
	public JFDException(String message, Object[] errorValues) {
		this(null, message, errorValues);
	}
	
	/**
	 * コンストラクタ
	 * 
	 * @param cause	原因例外
	 * @param message	エラーメッセージ
	 * @param errorValues	エラー値
	 */
	public JFDException(Throwable cause, String message, Object[] errorValues) {
		super(message, cause);
		this.errorValues = errorValues;
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.exception.VFSException#getName()
	 */
	public String getName() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.exception.VFSException#getErrorValues()
	 */
	public Object[] getErrorValues() {
		return errorValues;
	}

	public String getErrorMessage() {
		return MessageFormat.format(getMessage(), getErrorValues());
	}
}
