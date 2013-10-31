package br.eng.moretto.imgproc;


public class Convert {

		
	static public int[] RGB2BW(int[] img){
		
		int blue, green, red, pixel_temp;
		int [] pixels = new int [img.length];
		
		
        for(int i = 0 ; i <  img.length ; i++) {
            
            blue = img[i] & 0xff;
            green = (img[i]>> 8) & 0xff;
            red = (img[i] >> 16) & 0xff;
            
            pixel_temp = 0;
            pixel_temp += (blue * 0.11);
            pixel_temp += (red * 0.3);
            pixel_temp += (green * 0.59);
            
            pixels[i] = pixel_temp;
            
        }        
        return pixels;
		
	}
	
}
