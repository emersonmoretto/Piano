package br.eng.moretto.imgproc;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.ChannelSplitter;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import br.eng.moretto.CustomMapUtil;
import br.eng.moretto.piano.ImageCalculator;

public class AngleEstimator {
	
	static ImagePlus blue =  new ImagePlus("blue");
	static ImagePlus red =  new ImagePlus("red");
	static ImagePlus seg = new ImagePlus("segmented");
	static ImagePlus res = new ImagePlus("Result");;

	public  static BufferedImage run(BufferedImage buffImg, Point c,  int radius){
		
		BufferedImage buffImgSeg = new BufferedImage(radius*2, radius*2, BufferedImage.TYPE_3BYTE_BGR);
		
		{
			
			buffImgSeg = buffImg.getSubimage(c.x-radius, c.y-radius,  radius*2, radius*2);
		
			/** Awesome fucker algorithm
			 * 
			 * rodar uns 23~35 raios acumulando a qtd de pixel pra cada angulo
			 * depois teremos um acumulador e ver qual angulo possui mais valores
			 * */
			
			// Pre Proc Img Seg
			seg.setImage(buffImgSeg);
			
			
			BufferedImage resultBuff = null;
			
			try{
				
				ImageStack[] stack = ChannelSplitter.splitRGB(seg.getStack(), false);
				
				blue.setStack(stack[2]);
				red.setStack(stack[0]);
				
				ImageCalculator ic = new ImageCalculator();
				res = ic.run("difference create 32-bit", red, blue);
				resultBuff = res.getBufferedImage();
				
				blue.setImage(res.getBufferedImage());
				blue.show();
				blue.getWindow().setLocation(800, 550);
				
		        
			}catch(Exception ee){
				ee.printStackTrace();
			}
			
			
			// Alngle detector
			
			Map<Integer, Integer> acc = new HashMap<Integer,Integer>();
							
			for(int length = 22; length < 28 ; length++){
			
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
			        				        
			        	int pixel = res.getBufferedImage().getRGB(55 + (int) x, 55 + (int) y);
			        	
			            int red = (pixel >> 16) & 0xff;
			            int green = (pixel >> 8) & 0xff;
			            int blue = (pixel) & 0xff;
			            
			            // RGB
			            CustomMapUtil.addOrCount(acc, degrees, blue);
			            
			            // draw arcs
			            //resultBuff.setRGB(radius+ (int) x, radius + (int) y, 0xff00ff00);
			            
			        }catch(Exception ee){
			        	
			        	ee.printStackTrace();
		        		System.err.println((c.x+x) +" , "+ (c.y+y) );
			        }
			        angle += angle_stepsize;
			    }
			}
			
			
			// Acc sort by value
		    acc = CustomMapUtil.sortByValue(acc);
		    
		    // Showing top 10 angles
		    int count = 5;
		    int angleSum = 0;
		    for(Integer key : acc.keySet()){
		    	System.out.println(key + "= "+acc.get(key));
		    	angleSum += key;
		    	count--;
		    	if(count == 0) break;
		    }
		    
		    float angle = angleSum / 5;
		    angle =  (float) ((angle * 3.14) / 180) ;

		    
		    double x, y;
		    
	        // calculate x, y from a vector with known length and angle
	        x = (40 * Math.cos (angle));
	        y = (40 * Math.sin (angle));
		    
	        resultBuff.setRGB(55+ (int) x, 55 + (int) y, 0xffffffff);
	        
		    res.setImage(resultBuff);
			res.show();
			res.getWindow().setLocation(935, 550);
		    
		}
		return buffImgSeg;
	}
	
}
