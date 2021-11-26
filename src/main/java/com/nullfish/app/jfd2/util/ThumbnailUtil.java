package com.nullfish.app.jfd2.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ThumbnailUtil {
	public static BufferedImage getOptimalScalingImage(BufferedImage inputImage,
			double scaleFactor) {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice d = e.getDefaultScreenDevice();
		GraphicsConfiguration c = d.getDefaultConfiguration();
		
		// 現在のイメージのサイズ
		int currentWidth = inputImage.getWidth();
		int currentHeight = inputImage.getHeight();

		// 最終的なイメージのサイズ
		int endWidth = (int) (currentWidth * scaleFactor);
		int endHeight = (int) (currentHeight * scaleFactor);

		// 現在のイメージ
		BufferedImage currentImage = inputImage;

		// 最終的なサイズと現在のイメージの差
		int delta = currentWidth - endWidth;
		// 次に縮小するサイズ
		int nextPow2 = currentWidth >> 1;

		while (currentWidth > 1) {
			// 最終的なイメージとサイズの差が、次に縮小するサイズよりも
			// 小さいかどうか調べる
			if (delta <= nextPow2) {
				// イメージのサイズの差が小さい場合
				if (currentWidth != endWidth) {
					// 最終的な縮小率が 1/2n にならない場合
					BufferedImage tmpImage = c.createCompatibleImage(endWidth, endHeight);
					Graphics2D g = (Graphics2D) tmpImage.getGraphics();
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.drawImage(currentImage, 0, 0, tmpImage.getWidth(),
							tmpImage.getHeight(), null);
					g.dispose();

					currentImage = tmpImage;
				}

				return currentImage;
			} else {
				// イメージのサイズの差が大きい場合
				// 更に半分に縮小する
				BufferedImage tmpImage = c.createCompatibleImage(currentWidth >> 1, currentHeight >> 1);
				Graphics2D g = (Graphics2D) tmpImage.getGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(currentImage, 0, 0, tmpImage.getWidth(), tmpImage
						.getHeight(), null);
				g.dispose();

				// 変数の更新
				currentImage = tmpImage;
				currentWidth = currentImage.getWidth();
				currentHeight = currentImage.getHeight();
				delta = currentWidth - endWidth;
				nextPow2 = currentWidth >> 1;
			}
		}

		return currentImage;
	}
	
	public static BufferedImage getOptimalScalingImage(Image inputImage,
			double scaleFactor) {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice d = e.getDefaultScreenDevice();
		GraphicsConfiguration c = d.getDefaultConfiguration();
		
		// 現在のイメージのサイズ
		int currentWidth = inputImage.getWidth(null);
		int currentHeight = inputImage.getHeight(null);

		// 最終的なイメージのサイズ
		int endWidth = (int) (currentWidth * scaleFactor);
		int endHeight = (int) (currentHeight * scaleFactor);

		// 現在のイメージ
		Image currentImage = inputImage;

		// 最終的なサイズと現在のイメージの差
		int delta = currentWidth - endWidth;
		// 次に縮小するサイズ
		int nextPow2 = currentWidth >> 1;

		while (currentWidth > 1) {
			// 最終的なイメージとサイズの差が、次に縮小するサイズよりも
			// 小さいかどうか調べる
			if (delta <= nextPow2) {
				// イメージのサイズの差が小さい場合
				if (currentWidth != endWidth) {
					// 最終的な縮小率が 1/2n にならない場合
					BufferedImage tmpImage = c.createCompatibleImage(endWidth, endHeight);
					Graphics2D g = (Graphics2D) tmpImage.getGraphics();
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.drawImage(currentImage, 0, 0, tmpImage.getWidth(),
							tmpImage.getHeight(), null);
					g.dispose();

					currentImage.flush();
					currentImage = tmpImage;
				}

				return image2BufferedImage(currentImage, c);
			} else {
				// イメージのサイズの差が大きい場合
				// 更に半分に縮小する
				BufferedImage tmpImage = c.createCompatibleImage(currentWidth >> 1, currentHeight >> 1);
				Graphics2D g = (Graphics2D) tmpImage.getGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(currentImage, 0, 0, tmpImage.getWidth(), tmpImage
						.getHeight(), null);
				g.dispose();

				currentImage.flush();
				// 変数の更新
				currentImage = tmpImage;
				currentWidth = currentImage.getWidth(null);
				currentHeight = currentImage.getHeight(null);
				delta = currentWidth - endWidth;
				nextPow2 = currentWidth >> 1;
			}
		}

		return image2BufferedImage(currentImage, c);
	}
	
	private static BufferedImage image2BufferedImage(Image image, GraphicsConfiguration c) {
		if(image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		
		BufferedImage rtn = c.createCompatibleImage(image.getWidth(null), image.getHeight(null));
		Graphics2D g =  rtn.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		image.flush();
		return rtn;
	}
}
