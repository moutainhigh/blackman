
package com.jx.blackmen.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import com.sun.imageio.plugins.jpeg.JPEGImageWriter;

  
/**
 * 图片压缩工具类 提供的方法中可以设定生成的 缩略图片的大小尺寸、压缩尺寸的比例、图片的质量等
 * <pre>
 * 	调用示例：
 * resiz(srcImg, tarDir + "car_1_maxLength_11-220px-hui.jpg", 220, 0.7F);
 * </pre> 
 * 
 * @project haohui-b2b
 * @author cevencheng
 * @create 2012-3-22 下午8:29:01
 */
public class ImageUtil {  
  
    /** 
     * * 图片文件读取 
     *  
     * @param srcImgPath 
     * @return 
     */  
    private static BufferedImage InputImage(InputStream inputStream) throws RuntimeException {  
  
        BufferedImage srcImage = null;
        FileInputStream in = null;
        try {  
            // 构造BufferedImage对象  
            srcImage = ImageIO.read(inputStream);
        } catch (IOException e) {  
        	e.printStackTrace();  
        	throw new RuntimeException("读取图片文件出错！", e);
        } finally {
        	if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException("读取图片文件出错！", e);
				}
			}
        }
        return srcImage;  
    }  
  
    /** 
     * 将图片按照指定的尺寸比例、源图片质量压缩(默认质量为1) 
     *  
     * @param srcImgStream :源图片
     * @param outImgStream :输出的压缩图片
     * @param ratio  :压缩后的图片尺寸比例 
     * @param per :百分比 
     */  
    public static void resize(InputStream srcImgStream, OutputStream outImgStream,  float ratio) {  
        resize(srcImgStream, outImgStream, ratio, 1F);  
    }  
  
    /** 
     * 将图片按照指定长或者宽的最大值来压缩图片(默认质量为1) 
     *  
     * @param srcImgStream :源图片
     * @param outImgStream :输出的压缩图片
     * @param maxLength :长或者宽的最大值 
     * @param per :图片质量 
     */  
    public static void resize(InputStream srcImgStream, OutputStream outImgStream,  int maxLength) {  
        resize(srcImgStream, outImgStream, maxLength, 1F);  
    }  
  
    /** 
     * * 将图片按照指定的图片尺寸、图片质量压缩 
     *  
     * @param srcImgPath  :源图片
     * @param outImgPath :输出的压缩图片
     * @param new_w :压缩后的图片宽 
     * @param new_h :压缩后的图片高 
     * @param per :百分比 
     * @author cevencheng
     */  
    public static void resize(InputStream srcImgStream, OutputStream outImgStream, int new_w, int new_h, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgStream);  
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        // 根据原图的大小生成空白画布  
        BufferedImage tempImg = new BufferedImage(old_w, old_h, BufferedImage.TYPE_INT_RGB);  
        // 在新的画布上生成原图的缩略图  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);  
        // 调用方法输出图片文件  
        outImage(outImgStream, newImg, per);  
    }  
  
    /** 
     * * 将图片按照指定的尺寸比例、图片质量压缩 
     *  
     * @param srcImgStream :源图片
     * @param outImgStream :输出的压缩图片
     * @param ratio :压缩后的图片尺寸比例 
     * @param per :百分比 
     * @author cevencheng
     */  
    public static void resize(InputStream srcImgStream, OutputStream outImgStream, float ratio, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgStream);  
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        int new_w = 0;  
        // 新图的宽  
        int new_h = 0;  
        // 新图的长  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        // 从原图上取颜色绘制新图
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        // 根据图片尺寸压缩比得到新图的尺寸
        new_w = (int) Math.round(old_w * ratio);  
        new_h = (int) Math.round(old_h * ratio);  
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);  
        // 调用方法输出图片文件
        outImage(outImgStream, newImg, per);  
    }  
  
    /** 
     * <b>
     * 指定长或者宽的最大值来压缩图片
     * 	推荐使用此方法 
     * </b>
     * @param srcImgStream :源图片路径 
     * @param outImgStream :输出的压缩图片的路径 
     * @param maxLength :长或者宽的最大值 
     * @param per :图片质量 
     * @author cevencheng
     */  
    public static void resize(InputStream srcImgStream, OutputStream outImgStream, int maxLength, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgStream);  
        if(src == null){
        	return;
        }
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        int new_w = 0;  
        // 新图的宽  
        int new_h = 0;  
        // 新图的长  
        BufferedImage tempImg = new BufferedImage(old_w, old_h, BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        // 从原图上取颜色绘制新图  
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        // 根据图片尺寸压缩比得到新图的尺寸  
        if (old_w > old_h) {  
            // 图片要缩放的比例  
            new_w = maxLength;  
            new_h = (int) Math.round(old_h * ((float) maxLength / old_w));  
        } else {  
            new_w = (int) Math.round(old_w * ((float) maxLength / old_h));  
            new_h = maxLength;  
        }  
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);  
        // 调用方法输出图片文件  
        outImage(outImgStream, newImg, per);  
    }  
    
    /**
     * 将图片压缩成指定宽度， 高度等比例缩放
     * 
     * @param srcImgStream
     * @param outImgStream
     * @param width
     * @param per
     */
    public static void resizeFixedWidth(InputStream srcImgStream, OutputStream outImgStream, int width, float per) {  
    	// 得到图片  
    	BufferedImage src = InputImage(srcImgStream);  
    	int old_w = src.getWidth();  
    	// 得到源图宽  
    	int old_h = src.getHeight();  
    	// 得到源图长  
    	int new_w = 0;  
    	// 新图的宽  
    	int new_h = 0;  
    	// 新图的长  
    	BufferedImage tempImg = new BufferedImage(old_w, old_h, BufferedImage.TYPE_INT_RGB);  
    	Graphics2D g = tempImg.createGraphics();  
    	g.setColor(Color.white);  
    	// 从原图上取颜色绘制新图  
    	g.fillRect(0, 0, old_w, old_h);  
    	g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
    	g.dispose();  
    	// 根据图片尺寸压缩比得到新图的尺寸  
    	if (old_w > old_h) {  
    		// 图片要缩放的比例  
    		new_w = width;  
    		new_h = (int) Math.round(old_h * ((float) width / old_w));  
    	} else {  
    		new_w = (int) Math.round(old_w * ((float) width / old_h));  
    		new_h = width;  
    	}  
    	BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);  
    	newImg.getGraphics().drawImage(tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);  
    	// 调用方法输出图片文件  
    	outImage(outImgStream, newImg, per);  
    }  
  
    /** 
     * 图片输出
     *  
     * @param outImgStream 
     * @param newImg 
     * @param per 
     * @author cevencheng
     */  
/*    private static void outImage(OutputStream outImgStream, BufferedImage newImg, float per) {  
        // 输出到文件流
        try {  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outImgStream);  
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(newImg);  
            // 压缩质量  
            jep.setQuality(per, true);  
            encoder.encode(newImg, jep);  
        } catch (Exception e) { 
        	throw new RuntimeException(e);
        }
    }*/
    /**
     * 以JPEG编码保存图片
     * 
     * @param dpi
     *            分辨率
     * @param image_to_save
     *            要处理的图像图片
     * @param JPEGcompression
     *            压缩比
     * @param fos
     *            文件输出流
     * @throws IOException
     */
    private static void outImage(OutputStream outImgStream, BufferedImage newImg, float per){
        // Image writer
        JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpg").next();
        ImageOutputStream ios = null;
		try {
			ios = ImageIO.createImageOutputStream(outImgStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(ios == null){
			return;
		}
        imageWriter.setOutput(ios);
        // and metadata
        IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(newImg), null);
        if (per >= 0 && per <= 1f) {
            // new Compression
            JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
            jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(per);
        }
        try {
			imageWriter.write(imageMetaData, new IIOImage(newImg, null, null), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
        if(ios != null){
            try {
    			ios.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
        if(imageWriter != null){
        	imageWriter.dispose();
        }
    }
}  

	    			