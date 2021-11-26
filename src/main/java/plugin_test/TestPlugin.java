/*
 * Created on 2005/02/03
 *
 */
package plugin_test;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.plugin.AbstractPlugin;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class TestPlugin extends AbstractPlugin {
	/**
	 * システム開始時に呼び出される。
	 * 
	 * @throws VFSException
	 */
	public void systemStarted() {
		System.out.println("systemStarted");
	}
	
	/**
	 * システム終了時に呼び出される。
	 * 
	 * @throws VFSException
	 */
	public void systemExited() {
		System.out.println("systemExited");
	}
	
	/**
	 * JFD2インスタンスが生成された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdCreated(JFD jfd) {
		System.out.println("jfdCreated : " + jfd);
	}
	
	/**
	 * JFD2インスタンスが初期化された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdInited(JFD jfd, VFile baseDir) {
		System.out.println("jfdInited : " + jfd);
	}
	
	/**
	 * JFD2インスタンスが廃棄された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdDisposed(JFD jfd) {
		System.out.println("jfdDisposed : " + jfd);
	}
	
	/**
	 * JFDオーナーインスタンスが生成された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdOwnerCreated(JFDOwner owner) {
		System.out.println("jfdOwnerCreated : " + owner);
	}
	
	/**
	 * JFD2オーナーインスタンスが廃棄された際に呼び出される。
	 * 
	 * @param jfd
	 */
	public void jfdOwnerDisposed(JFDOwner owner) {
		System.out.println("jfdOwnerDisposed : " + owner);
	}
	
	
}
