/*
 * Created on 2004/08/29
 *
 */
package com.nullfish.lib.ui;

/**
 * @author shunji
 *
 */
public interface ReturnableRunnable extends Runnable {
	public Object getReturnValue();
}
