package com.nullfish.app.jfd2.viewer.graphicviewer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.VolatileImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import com.nullfish.lib.keymap.KeyStrokeMap;
import com.nullfish.lib.ui.ThreadSafeUtilities;
import com.nullfish.lib.vfs.VFile;

public class ImageComponent extends JComponent implements Scrollable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7314369743230014072L;

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
	
	// 拡大フラグ
	boolean expands = false;
	
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
	
	private ImageLoaderThread loader;
	
	private synchronized void setLoaderThread(ImageLoaderThread loader) {
		if(this.loader != null) {
			this.loader.setStopped();
		}
		this.loader = loader;
		loader.start();
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public ImageComponent() {
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
		rightRotateAction.putValue(Action.NAME, "rotate right" + "(R) R,:");
		rightRotateAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		map.put(ROTATE_RIGHT, rightRotateAction);

		Action leftRotateAction = new RotateAction(-90);
		leftRotateAction.putValue(Action.NAME, "rotate_left" + "(L) L,;");
		leftRotateAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		map.put(ROTATE_LEFT, leftRotateAction);

		Action zoomInAction = new ZoomAction(20);
		zoomInAction.putValue(Action.NAME, "to_big" + "(B) ]");
		zoomInAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
		map.put(ZOOM_IN, zoomInAction);

		Action zoomOutAction = new ZoomAction(-20);
		zoomOutAction.putValue(Action.NAME, "to_small" + "(L) [");
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
		
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0), ZOOM_IN);
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_MINUS, 0), ZOOM_IN);

		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0), ZOOM_OUT);
		map.put(KeyStrokeMap.getKeyStroke(KeyEvent.VK_PLUS, 0), ZOOM_OUT);

	}
	
	/**
	 * 表示モードを初期化する
	 */
	public void initShowingMode() {
		magnification = 100;
		rotation = 0;
		hFlip = false;
		vFlip = false;
	}
	
	/**
	 * 表示が更新された際、通知をうけるリスナを追加する。
	 * @param listener
	 */
	public void addImageComponentListener(ImageComponentListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * 各リスナに変化を通知する。
	 */
	private void fireUpdated() {
		for(int i=0; i<listeners.size(); i++) {
			((ImageComponentListener)listeners.get(i)).imageComponentUpdated(this);
		}
	}

	/**
	 * 表示イメージをセットする。
	 * スレッドセーフ
	 * @param image
	 */
	public void setImage(final Image image) {
		Runnable runnable = new Runnable() {
			public void run() {
				if(ImageComponent.this.image != null) {
					ImageComponent.this.image.flush();
				}
				
				ImageComponent.this.image = image;
				
				applyChange();
				
				ImageComponent.this.revalidate();
				repaint();
			}
		};
		
		ThreadSafeUtilities.executeRunnable(runnable);
	}
	
	/**
	 * 表示イメージをセットする。
	 * @param image
	 */
	public synchronized void setImage(VFile file) {
		if(file != null) {
			ImageLoaderThread loader = new ImageLoaderThread(file);
			setLoaderThread(loader);
		} else {
			setImage((Image)null);
		}
	}
	
	protected void applyChange() {
		if(image == null) {
			setPreferredSize( new Dimension(0, 0) );
			repaint();
			fireUpdated();
			return;
		}
		
		if(fixSize) {
			this.setPreferredSize(getMaximumSize());
			this.transform = getSizeFixedTransform(expands);
		} else {
			this.transform = getTransform();
		}
		
		repaint();

		fireUpdated();
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
	 * @param expands 拡大を行う場合true
	 * @return AffineTransform
	 */
	protected AffineTransform getSizeFixedTransform(boolean expands) {
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
		
		Dimension maxSize = getMaximumSize();
		int width = maxSize.width;
		int height = maxSize.height;
			
		double vScale = width < maxX - minX ? width/(maxX - minX) : 1;  
		double hScale = height < maxY - minY ? height/(maxY - minY) : 1;  
		double scale = vScale < hScale ? vScale : hScale;
		
		if(expands && width > maxX - minX && height > maxY - minY) {
			double expandVScale = width > maxX - minX ? width/(maxX - minX) : 1;  
			double expandHScale = height > maxY - minY ? height/(maxY - minY) : 1;  
			scale = expandVScale < expandHScale ? expandVScale : expandHScale;
		}
		
		newTransform.concatenate(AffineTransform.getScaleInstance(scale, scale));
		
		//	中心合わせ
		Point2D imageCenter = new Point(imageWidth / 2, imageHeight / 2);
		newTransform.transform(imageCenter, imageCenter);
		
		AffineTransform deltaTransform = AffineTransform.getTranslateInstance((width/2) - imageCenter.getX(), (height/2) - imageCenter.getY()); 
		deltaTransform.concatenate(newTransform);

		return deltaTransform;
	}
	
	int c = 0;
	/**
	 * 描画メソッド
	 * @see javax.swing.JComponent#paintComponent(Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
            
		if (image != null) {
			AffineTransform oldTransform = g2.getTransform();

			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

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
	 * 画像の回転を行うAction
	 */
	class RotateAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int delta;
		
		public RotateAction (int delta) {
			this.delta = delta;
		}
		
		public void actionPerformed(ActionEvent e) {
			rotation +=delta;
			while(rotation >= 360) {
				rotation -= 360;
			}
			
			while(rotation < 0) {
				rotation += 360;
			}
			applyChange(); 
		}
	}
	
	/**
	 * 拡大縮小を行うAction
	 * 
 	 */
	class ZoomAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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


	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 100;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if(orientation == SwingConstants.VERTICAL) {
			return visibleRect.height;
		} else {
			return visibleRect.width;
		}
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public void fixSize(Dimension size) {
		setMaximumSize(size);
		fixSize = true;
		applyChange();
	}
	
	public void unfixSize() {
		setMaximumSize(null);
		fixSize = false;
		applyChange();
	}
	
	private class ImageLoaderThread extends Thread {
		VFile file;
		
		private boolean running;
		public ImageLoaderThread(VFile file) {
			this.file = file;
		}
		
		public void setStopped() {
			synchronized (ImageComponent.this) {
				running = false;
			}
		}
		
		private boolean isRunning() {
			synchronized (ImageComponent.this) {
				return running;
			}
		}
		
		public void run() {
			running = true;
			BufferedInputStream is = null;
			ByteArrayOutputStream bos = null;
			try {
				int l = 0;
				bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				is = new BufferedInputStream(file.getInputStream());
				while(isRunning()) {
					l = is.read(buffer);
					if(l <= 0) {
						break;
					}
					
					bos.write(buffer, 0, l);
				}
				
				bos.flush();
				
				if(!isRunning()) {
					return;
				}
				
				Image image = null;
				String extension = file.getFileName().getExtension().toLowerCase();
				if("jpg".equals(extension)
						|| "jpeg".equals(extension)
						|| "gif".equals(extension)
						|| "png".equals(extension)) {
					image = Toolkit.getDefaultToolkit().createImage(bos.toByteArray());
					MediaTracker tracker = new MediaTracker(ImageComponent.this);
					tracker.addImage(image, 0);
					try {
						tracker.waitForID(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					image = ImageIO.read(new ByteArrayInputStream(bos.toByteArray()));
				}
				
				if(!running) {
					image.flush();
					return;
				}
				
				setImage(image);
			} catch (Exception e) {
				if(running) {
					setImage((Image)null);
				}
			} finally {
				try {
					bos.close();
				} catch (Exception e) {}
				
				try {
					is.close();
				} catch (Exception e) {}
			}
		}
	}
	public boolean isExpands() {
		return expands;
	}

	public void setExpands(boolean expands) {
		this.expands = expands;
		applyChange();
	}
	
}
