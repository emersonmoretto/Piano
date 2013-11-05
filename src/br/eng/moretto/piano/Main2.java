package br.eng.moretto.piano;

import ij.ImagePlus;
import ij.process.ByteProcessor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import br.eng.moretto.imgproc.AngleEstimator;
import br.eng.moretto.imgproc.ConvertTo8bit;
import br.eng.moretto.imgproc.FindXY;

public class Main2 {
	
	private final static int sleep = 100;
	private final static int radius = 55;
   
	public static void main(String[] args) throws Exception {
		
		ImagePlus ipOri = new ImagePlus("Original"); 
		ImagePlus ipSeg = new ImagePlus("Segmented");
		ImagePlus ipProc = new ImagePlus("Viewer"); // Image processor para exibir a
		ipOri.show();
		ipProc.show();
		
		
		BufferedImage buffImg = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
		
		
		FindXY e = new FindXY();
		Rectangle r = new Rectangle();
		Point c = new Point();
		
		//Load and iteract
		File dir = new File(Main2.class.getResource("/images/").getPath());		
		for(File file : dir.listFiles()){
			
			// Capture/read image
			buffImg = ImageIO.read(file);	
			ipOri.setImage(buffImg);
			
			
			/**
			 * IMG PROC
			 * 
			 * Convert to 8-bit -> Threshold & Erode(?) -> Find XY -> Angle Estimator
			 */
			
			// Convert to 8-Bit
			ipProc.setImage(  ConvertTo8bit.convert(buffImg) );
		
			
			
			//Threshold
			ByteProcessor bp = new ByteProcessor(ipProc.getImage());
			bp.setThreshold(0, 200, 1);	
			ipProc.setProcessor("Viewer", bp);
			//bp.erode();
			
						
			
			// Find XY			
			r = e.run(ipProc.getProcessor().getBufferedImage()); // r = rectangle of pointer
			c.setLocation(r.x + (r.width/2), r.y + (r.height/2)); // Calc center
			buffImg.setRGB(c.x, c.y, 0xffff0000); // show center point
			
			
			
			// Angle Estimator
			// if I have center detected
			if(c.x-radius > 0 && c.y-radius > 0 &&  c.x+radius+(r.width/2)+radius < 640 && c.y+(r.height/2)+radius < 480 ){

				ipSeg.setImage(AngleEstimator.run(buffImg, c, radius));
				
			}else{
				ipSeg.restoreRoi();
			}
			
			
			// IP						
			ipSeg.show();
			ipSeg.repaintWindow();
			ipProc.show();
			ipProc.repaintWindow();
			ipOri.show();
			ipOri.repaintWindow();
			
			ipOri.getWindow().setLocation(0, 0);
			ipProc.getWindow().setLocation(650, 0);
			ipSeg.getWindow().setLocation(650, 550);
			
			Thread.sleep(sleep);
		}
		
		
		
		
	}

}
