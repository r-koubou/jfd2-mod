/*
 * Created on 2004/08/17
 *
 */
package com.nullfish.lib.ui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * スレッドセーフ関係ユーティリティクラス。
 * 
 * @author shunji
 */
public class ThreadSafeUtilities {
	/**
	 * Runnableを実行する。
	 * Swingのシングルスレッドルールに一致するようにし、
	 * もしもイベントディスパッチスレッドではない場合は同期実行させる。
	 * 
	 * @param runnable
	 */
	public static void executeRunnable(Runnable runnable) {
		executeRunnable(runnable, true);
	}
	
	/**
	 * Runnableを実行する。
	 * Swingのシングルスレッドルールに一致するようにし、
	 * もしもイベントディスパッチスレッドではない場合は同期実行させる。
	 * 
	 * @param runnable
	 * @param waits
	 */
	public static void executeRunnable(Runnable runnable, boolean waits) {
		if(SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			try {
				if(waits) {
					SwingUtilities.invokeAndWait(runnable);
				} else {
					SwingUtilities.invokeLater(runnable);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ReturnableRunnableを実行し、getReturnValueの値を返す。
	 * Swingのシングルスレッドルールに一致するようにし、
	 * もしもイベントディスパッチスレッドではない場合は同期実行させる。
	 * 
	 * @param runnable
	 */
	public static Object executeReturnableRunnable(ReturnableRunnable runnable) {
		if(SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		return runnable.getReturnValue();
	}
}
