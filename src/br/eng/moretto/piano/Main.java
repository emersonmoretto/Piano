package br.eng.moretto.piano;

import ij.ImagePlus;

import java.awt.Point;
import java.awt.image.BufferedImage;

import br.eng.moretto.imgproc.Eliminator;
import br.eng.moretto.imgproc.SkinExtractor;
import br.eng.moretto.io.Camera;

public class Main {

	private final static int sleep = 50;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		boolean show = true; // true para exibir a imagem da webcam na tela
		Camera camera = new Camera(2000);
		ImagePlus ip = new ImagePlus("Viewer"); // Image processor para exibir a
		// imagem capturada

		BufferedImage buffImg;
		BufferedImage tecla1, tecla2, tecla3, tecla4, tecla5, tecla6, tecla7;

		try {
			camera.startCamera();
			// System.out.println("INFO: Started");
		} catch (Exception e) {
			camera.stopCamera();
			System.out.println("N�o foi poss�vel inicializar a c�mera" + e);
		}finally{
			camera.stopCamera();
		}

		// Extrator de pele
		SkinExtractor extractor = new SkinExtractor();
		// THE ELIMINATOR!!! Elimina ru�dos e seleciona o ponto de maior Y da
		// maior regi�o agrupada
		Eliminator e = new Eliminator();
		Point c1 = new Point();
		Point c2 = new Point();
		Point c3 = new Point();
		Point c4 = new Point();
		Point c5 = new Point();
		Point c6 = new Point();
		Point c7 = new Point();
		
		
		try {

			for (int i = 0; i < 500; i++) {

				// captura a imagem
				buffImg = camera.captureCamera();
				//System.out.println("Size "+buffImg.getWidth()+"x"+buffImg.getHeight());
				
				//buffImg = buffImg.getSubimage(0,0,320,40);
				tecla1 = buffImg.getSubimage(0,0,45,30);
				tecla2 = buffImg.getSubimage(45,0,45,30);
				tecla3 = buffImg.getSubimage(90,0,45,30);
				tecla4 = buffImg.getSubimage(135,0,45,30);
				tecla5 = buffImg.getSubimage(180,0,45,30);
				tecla6 = buffImg.getSubimage(225,0,45,30);
				tecla7 = buffImg.getSubimage(270,0,50,30);
				
				//buffImg.setRGB(0, 0, 320, 40, extractor.extract(buffImg), 0,320);
				
				tecla1.setRGB(0, 0, 45, 30, extractor.extract(tecla1), 0,45);
				tecla2.setRGB(0, 0, 45, 30, extractor.extract(tecla2), 0,45);
				tecla3.setRGB(0, 0, 45, 30, extractor.extract(tecla3), 0,45);
				tecla4.setRGB(0, 0, 45, 30, extractor.extract(tecla4), 0,45);
				tecla5.setRGB(0, 0, 45, 30, extractor.extract(tecla5), 0,45);
				tecla6.setRGB(0, 0, 45, 30, extractor.extract(tecla6), 0,45);
				tecla7.setRGB(0, 0, 50, 30, extractor.extract(tecla7), 0,50);
				
				//buffImg.setRGB(0, 0, 45, 40, extractor.extract(tecla1), 0,45);
				//buffImg.setRGB(48, 0, 45, 40, extractor.extract(tecla2), 0,45);
				//buffImg.setRGB(0, 0, 45, 40, extractor.extract(tecla3), 0,45);
				//buffImg.setRGB(0, 0, 45, 40, extractor.extract(tecla4), 0,45);
				
				c1 = e.run(tecla1);
				c2 = e.run(tecla2);
				c3 = e.run(tecla3);
				c4 = e.run(tecla4);
				c5 = e.run(tecla5);
				c6 = e.run(tecla6);
				c7 = e.run(tecla7);
				
				//cAnt = c;
				//c = e.run(buffImg);
				//System.out.println("Ponto ("+c.x+","+c.y+") Ant ("+cAnt.x+","+cAnt.y+")");
				/*if (c.x < buffImg.getWidth() + 5
						&& c.y < buffImg.getHeight() + 5 && c.x > 5 && c.y > 5) {
					buffImg.setRGB(c.x, c.y, 0xffff0000);
					buffImg.setRGB(c.x + 1, c.y + 1, 0xffff0000);
					buffImg.setRGB(c.x - 1, c.y - 1, 0xffff0000);
					buffImg.setRGB(c.x + 1, c.y, 0xffff0000);
					buffImg.setRGB(c.x + 2, c.y, 0xffff0000);
					buffImg.setRGB(c.x + 3, c.y, 0xffff0000);
					buffImg.setRGB(c.x, c.y + 1, 0xffff0000);
					buffImg.setRGB(c.x, c.y + 2, 0xffff0000);
					buffImg.setRGB(c.x, c.y + 3, 0xffff0000);
				}
*/

			
				System.out.println("------------");

				if (show) {
					ip.setImage(buffImg);
					ip.draw();
					if (i == 0)
						ip.show();
				}
				Thread.sleep(sleep);

			}
			System.out.println("Finish");
			
		} catch (Exception e1) {
			System.out.println("Exception.. Stoping Camera \n\n"+e1.getMessage());
			camera.stopCamera();
		}

	}
}
