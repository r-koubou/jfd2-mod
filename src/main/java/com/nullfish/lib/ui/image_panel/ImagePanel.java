package com.nullfish.lib.ui.image_panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.ui.ThreadSafeUtilities;

/**
 * @author shunji
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ImagePanel extends JComponent {
	//	表示イメージ
	Image image = null;
	
	//	表示サイズ倍率
	int magnification = 100;
	
	//	角度
	int rotation = 0;
	
	//	左右反転
	boolean hFlip = false;
	
	//	上下反転
	boolean vFlip = false;
	
	//	表示サイズを固定するフラグ
	boolean fixSize = false;
	
	//	表示横幅
	int viewWidth;
	
	//	表示縦幅
	int viewHeight;
	
	/** 右９０度回転 */
	public static final String ROTATE_RIGHT = "rotate_right";
	
	/** 左９０度回転 */
	public static final String ROTATE_LEFT = "rotate_left";
	
	/** 拡大 */
	public static final String ZOOM_IN = "zoom_in";
	
	/** 縮小 */
	public static final String ZOOM_OUT = "zoom_out";
	
	/** 表示のためのアフィン変換 */ 
	AffineTransform transform = AffineTransform.getRotateInstance(0);
	
	java.util.List listeners = new ArrayList();
	
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	public static final int BUFFER_SIZE = 4096; //	バッファのサイズ

	/**
	 * デフォルトコンストラクタ
	 */
	public ImagePanel() {
		initAction();
		initInput();
	}

	public void dispose() {
		if(image != null) {
			image.flush();
		}
	}
	
	/**
	 * Actionを初期化する。
	 */	
	protected void initAction() {
		ActionMap map = getActionMap();
		
		Action rightRotateAction = new RotateAction(90);
		rightRotateAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		map.put(ROTATE_RIGHT, rightRotateAction);

		Action leftRotateAction = new RotateAction(-90);
		leftRotateAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		map.put(ROTATE_LEFT, leftRotateAction);

		Action zoomInAction = new ZoomAction(-25);
		zoomInAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
		map.put(ZOOM_IN, zoomInAction);

		Action zoomOutAction = new ZoomAction(25);
		zoomOutAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		map.put(ZOOM_OUT, zoomOutAction);
	}
	
	/**
	 * キー入力を初期化する。
	 */
	protected void initInput() {
		InputMap map = getInputMap();
		
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_L, 0), ROTATE_LEFT);
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_SEMICOLON, 0), ROTATE_LEFT);
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_EQUALS, 0), ROTATE_LEFT);
		
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_R, 0), ROTATE_RIGHT);
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_COLON, 0), ROTATE_RIGHT);
		
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0), ZOOM_OUT);
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_MINUS, 0), ZOOM_OUT);

		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0), ZOOM_IN);
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_PLUS, 0), ZOOM_IN);

	}
	
	/**
	 * 表示モードを初期化する
	 */
	public void initShowingMode() {
		magnification = 100;
		rotation = 0;
		hFlip = false;
		vFlip = false;
		//fixSize = false;
	}
	
	/**
	 * 表示が更新された際、通知をうけるリスナを追加する。
	 * @param listener
	 */
	public void addImageComponentListener(ImagePanelListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * 各リスナに変化を通知する。
	 */
	private void fireUpdated() {
		for(int i=0; i<listeners.size(); i++) {
			((ImagePanelListener)listeners.get(i)).imageComponentUpdated(this);
		}
	}

	/**
	 * 表示イメージをセットする。
	 * スレッドセーフ。
	 * @param image
	 */
	public synchronized void setImage(final Image image) {
		Runnable runnable = new Runnable() {
			public void run() {
				if(ImagePanel.this.image != null) {
					ImagePanel.this.image.flush();
				}
				
				ImagePanel.this.image = image;
				applyChange();
			}
		};

		ThreadSafeUtilities.executeRunnable(runnable);
	}
	
	protected void applyChange() {
		if(image == null) {
			setPreferredSize( new Dimension(0, 0) );
			fireUpdated();
			RepaintManager.currentManager(this).markCompletelyDirty(this);
			return;
		}
		
		if(fixSize) {
			this.transform = getSizeFixedTransform();
		} else {
			this.transform = getTransform();
		}
		
		fireUpdated();
		
		RepaintManager.currentManager(this).markCompletelyDirty(this);
	}

	/**
	 * 表示モードに合わせてアフィン変換を作成して返す。
	 * @return AffineTransform
	 */
	protected AffineTransform getTransform() {
		AffineTransform newTransform = AffineTransform.getScaleInstance(vFlip ? -1 : 1, hFlip ? -1 : 1 );
		newTransform.concatenate( AffineTransform.getRotateInstance(Math.toRadians(rotation)) );
		double scale = (double)magnification / 100;
		
		newTransform.concatenate(AffineTransform.getScaleInstance(scale, scale));
		
		Point2D[] apexs = {
			new Point(0, 0),
			new Point(0, image.getHeight(this)),
			new Point(image.getWidth(this), 0),
			new Point(image.getWidth(this), image.getHeight(this))
		};
		
		double minX = apexs[0].getX();
		double minY = apexs[0].getY();
		double maxX = apexs[0].getX();
		double maxY = apexs[0].getY();

		for(int i=0; i<apexs.length; i++) {
			newTransform.transform(apexs[i], apexs[i]);
			
			minX = minX < apexs[i].getX() ? minX : apexs[i].getX(); 
			minY = minY < apexs[i].getY() ? minY : apexs[i].getY();
			maxX = maxX > apexs[i].getX() ? maxX : apexs[i].getX();
			maxY = maxY > apexs[i].getY() ? maxY : apexs[i].getY();
		}

		setPreferredSize( new Dimension((int)(maxX - minX), (int)(maxY - minY)) );

		AffineTransform deltaTransform = AffineTransform.getTranslateInstance(minX *(-1), minY *(-1)); 
		deltaTransform.concatenate(newTransform);
		
		return deltaTransform;
	}
	
	/**
	 * 表示サイズに収まるアフィン変換を作成して返す。
	 * @return AffineTransform
	 */
	protected AffineTransform getSizeFixedTransform() {
		AffineTransform newTransform = AffineTransform.getScaleInstance(vFlip ? -1 : 1, hFlip ? -1 : 1 );
		newTransform.concatenate( AffineTransform.getRotateInstance(Math.toRadians(rotation)) );
		
		int imageWidth = image.getWidth(this);
		int imageHeight = image.getHeight(this);
		
		Point2D[] apexs = {
			new Point(0, 0),
			new Point(0, imageHeight),
			new Point(imageWidth, 0),
			new Point(imageWidth, imageHeight)
		};
		
		//	表示サイズ
		double minX = apexs[0].getX();
		double minY = apexs[0].getY();
		double maxX = apexs[0].getX();
		double maxY = apexs[0].getY();

		for(int i=0; i<apexs.length; i++) {
			newTransform.transform(apexs[i], apexs[i]);
			
			minX = minX < apexs[i].getX() ? minX : apexs[i].getX(); 
			minY = minY < apexs[i].getY() ? minY : apexs[i].getY();
			maxX = maxX > apexs[i].getX() ? maxX : apexs[i].getX();
			maxY = maxY > apexs[i].getY() ? maxY : apexs[i].getY();
		}
		
		int width = getWidth();
		int height = getHeight();
		
		double vScale = width < maxX - minX ? width/(maxX - minX) : 1;  
		double hScale = height < maxY - minY ? height/(maxY - minY) : 1;  
		double scale = vScale < hScale ? vScale : hScale;
		
		newTransform.concatenate(AffineTransform.getScaleInstance(scale, scale));
		
		//	中心合わせ
		Point2D imageCenter = new Point(imageWidth / 2, imageHeight / 2);
		newTransform.transform(imageCenter, imageCenter);
		
		AffineTransform deltaTransform = AffineTransform.getTranslateInstance((width/2) - imageCenter.getX(), (height/2) - imageCenter.getY()); 
		deltaTransform.concatenate(newTransform);

		return deltaTransform;
	}
	
	/**
	 * 描画メソッド
	 * @see javax.swing.JComponent#paintComponent(Graphics)
	 */
	public void paintComponent(Graphics g) {
		if (image != null) {
			Graphics2D g2 = (Graphics2D)g;
			AffineTransform oldTransform = g2.getTransform();

			g2.transform(transform);
			g2.drawImage(image, 0, 0, this);

			g2.setTransform(oldTransform);
		}
	}

	/**
	 * 定数に対応するActionを返す。
	 * 
	 * @param key
	 * @return Action
	 */
	public Action getAction(Object key) {
		return getActionMap().get(key);
	}
	
	/**
	 * Returns the fixSize.
	 * @return boolean
	 */
	public boolean isFixSize() {
		return fixSize;
	}

	/**
	 * Returns the hFlip.
	 * @return boolean
	 */
	public boolean isHFlip() {
		return hFlip;
	}

	/**
	 * Returns the image.
	 * @return Image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Returns the magnification.
	 * @return int
	 */
	public int getMagnification() {
		return magnification;
	}

	/**
	 * Returns the rotation.
	 * @return int
	 */
	public int getRotation() {
		return rotation;
	}

	/**
	 * Returns the vFlip.
	 * @return boolean
	 */
	public boolean isVFlip() {
		return vFlip;
	}

	/**
	 * 回転角を0から360の間にする。
	 */
	private void verifyRotation() {
		while(rotation >= 360) {
			rotation -= 360;
		}
		
		while(rotation < 0) {
			rotation += 360;
		}
	}
	
	/**
	 * 画像の回転を行うAction
	 */
	class RotateAction extends AbstractAction {
		int delta;
		
		public RotateAction (int delta) {
			this.delta = delta;
		}
		
		public void actionPerformed(ActionEvent e) {
			rotation +=delta;
			verifyRotation();
			applyChange(); 
		}
	}
	
	/**
	 * 拡大縮小を行うAction
	 * 
 	 */
	class ZoomAction extends AbstractAction {
		int delta;
		
		public ZoomAction(int delta) {
			this.delta = delta;
		}
		
		public void actionPerformed(ActionEvent e) {
			if(magnification + delta > 0) {
				magnification+=delta;
				applyChange();
			}
		}
	}
	/**
	 * Sets the fixSize.
	 * @param fixSize The fixSize to set
	 */
	public void setFixSize(boolean fixSize) {
		this.fixSize = fixSize;
		applyChange();
	}

}
