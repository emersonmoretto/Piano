package br.eng.moretto.piano;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import br.eng.moretto.CustomMapUtil;
import br.eng.moretto.imgproc.FindXY;

public class Main2 {
	
	private final static int sleep = 4200;

	
	public static void main(String[] args) throws Exception {
		
		ImagePlus ipSeg = new ImagePlus("Segmented");
		ImagePlus ipOri = new ImagePlus("Original"); 
		ImagePlus ip = new ImagePlus("Viewer"); // Image processor para exibir a
		ipOri.show();
		ip.show();
		
		
		BufferedImage buffImg = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage buffImgSeg = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
		
		FindXY e = new FindXY();
		
		Rectangle r = new Rectangle();
		Point c = new Point();
		
		//Load and iteract
		File dir = new File(Main2.class.getResource("/images/").getPath());		
		for(File file : dir.listFiles()){
			
			e = new FindXY();
			buffImg = ImageIO.read(file);	
			ip.setImage(buffImg);
			ipOri.setImage(buffImg);
			
			new ImageConverter(ip).convertToGray8();
			
			
			
			//Threshold
			ByteProcessor bp = new ByteProcessor(ip.getImage());
			bp.setThreshold(0, 200, 1);	
			//bp.erode();
			
			ip.setProcessor("Pre Process", bp);
			
			//buffImg = ;
			r = e.run(ip.getProcessor().getBufferedImage());
		
			int radius = 55;
			c.setLocation(r.x + (r.width/2), r.y + (r.height/2));
			buffImg.setRGB(c.x, c.y, 0xffff0000);
			
			System.out.println(c.x+radius);
			if(c.x-radius > 0 && c.y-radius > 0 &&  c.x+radius+(r.width/2)+radius < 640 && c.y+(r.height/2)+radius < 480 ){
				
				buffImgSeg = buffImg.getSubimage(c.x-radius, c.y-radius,  radius*2, radius*2);
			
				/** Awesome fucker algorithm
				 * 
				 * rodar uns 23~35 raios acumulando a qtd de pixel pra cada angulo
				 * depois teremos um acumulador e ver qual angulo possui mais valores
				 * */
				
				Map<Integer, Integer> acc = new HashMap<Integer,Integer>();
								
				for(int length = 25; length < 33 ; length++){
				
					double x, y;
				    float angle = 0f;
				    float angle_stepsize = .0872f; // (0,0872 * 180) / 3,14 = step a cada 5 graus
				    int degrees = 0;
		
				    // go through all angles from 0 to 2 * PI radians
				    while (angle < 2 * Math.PI) 
				    {
				    	degrees = (int) Math.round(angle * 180 / Math.PI);
				    	
				        // calculate x, y from a vector with known length and angle
				        x = (length * Math.cos (angle));
				        y = (length * Math.sin (angle));
		  
				        
				        try{
				        				        
				        	int pixel = buffImg.getRGB(c.x + (int) x, c.y + (int) y);
				        	
				            int red = (pixel >> 16) & 0xff;
				            int green = (pixel >> 8) & 0xff;
				            int blue = (pixel) & 0xff;
				            
				            // RGB
				            //System.out.println("rgb: " +red + ", " + green + ", " + blue);
				            
				            CustomMapUtil.addOrCount(acc, degrees, blue);
				            
				            buffImg.setRGB(c.x+ (int) x, c.y + (int) y, 0xffffff00);
				            
				        }catch(Exception ee){
			        		System.err.println((c.x+x) +" , "+ (c.y+y) );
				        }
				        angle += angle_stepsize;
				    }
				    
				}
			    
				
				// Acc sort by value
			    acc = CustomMapUtil.sortByValue(acc);
			    
			    // Showing top 10 angles
			    int count = 10;
			    for(Integer key : acc.keySet()){
			    	System.out.println(key + "= "+acc.get(key));
			    	count--;
			    	if(count == 0) break;
			    }
			    System.out.println("************************");
			    
			    //Update image
			    ipSeg.setImage(buffImgSeg);
							
			}else{
				ipSeg.restoreRoi();
			}
			
			// IP						
			ipSeg.show();
			ipSeg.repaintWindow();
			ip.show();
			ip.repaintWindow();
			ipOri.show();
			ipOri.repaintWindow();
			
			ipOri.getWindow().setLocation(0, 0);
			ip.getWindow().setLocation(650, 0);
			ipSeg.getWindow().setLocation(650, 520);
			
			Thread.sleep(sleep);
		}
		
		
		
		
	}

}
