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
import java.util.ArrayList;
import java.util.List;

public class AngleEstimator {
	
    static ImagePlus blue = new ImagePlus("blue");
    static ImagePlus red = new ImagePlus("red");
    static ImagePlus seg = new ImagePlus("segmented");
    static ImagePlus res = new ImagePlus("Result");

    static float map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

      /**
     * Awesome fucker algorithm
     *
     * rodar uns 23~35 raios acumulando a qtd de pixel pra cada angulo depois
     * teremos um acumulador e ver qual angulo possui mais valores e depois
     * calcular a distancia do halo ate o centro a partir do angulo encontrado
     * pois isso significa o tanto que a caneta esta inclinada.
     *
     * O angulo estimado é entre 0-360 obviamente O valor da inclinacao é gerado
     * entre 0-100%
     *
     *
     * PIPELINE Proc Img
     *
     * 1 - Split RGB 2 - Diff Red - Blue 3 - Angle estimator 4 - Inclinacao
        *
     */
    public static AngleEstimatorResult run(BufferedImage buffImg, Point c, int radius) {

        AngleEstimatorResult result = new AngleEstimatorResult();
        

        BufferedImage buffImgSeg;// = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_3BYTE_BGR);


        buffImgSeg = buffImg.getSubimage(c.x - radius, c.y - radius, radius * 2, radius * 2);
        result.buffList.add(buffImgSeg);



        // Proc Image
        seg.setImage(buffImgSeg);
        BufferedImage resultBuff = null;

        try {
            // 1 - Split
            ImageStack[] stack = ChannelSplitter.splitRGB(seg.getStack(), false);
            blue.setStack(stack[2]);
            red.setStack(stack[0]);

            // 2 - Red - Blue
            ImageCalculator ic = new ImageCalculator();
            res = ic.run("difference create 32-bit", red, blue);
            resultBuff = res.getBufferedImage();
            result.buffList.add(resultBuff);

            blue.setImage(res.getBufferedImage());

            //blue.show();
            //blue.getWindow().setLocation(800, 550);

        } catch (Exception ee) {
            ee.printStackTrace();
        }



        // 3 - Angle detector
        Map<Integer, Integer> acc = new HashMap<Integer, Integer>();

        for (int length = 19; length < 33; length++) {

            double x, y;
            float angle = 0f;
            float angle_stepsize = .0872f; // (0,0872 * 180) / 3,14 = step a cada 5 graus
            int degrees = 0;

            // go through all angles from 0 to 2 * PI radians
            while (angle < 2 * Math.PI) {
                degrees = (int) Math.round(angle * 180 / Math.PI);

                // calculate x, y from a vector with known length and angle
                x = (length * Math.cos(angle));
                y = (length * Math.sin(angle));

                try {

                    int pixel = res.getBufferedImage().getRGB(55 + (int) x, 55 + (int) y);
                    int blue = (pixel) & 0xff;

                    // RGB
                    CustomMapUtil.addOrCount(acc, degrees, blue);

                    // draw arcs
                    //resultBuff.setRGB(radius+ (int) x, radius + (int) y, 0xff00ff00);

                } catch (Exception ee) {

                    ee.printStackTrace();
                    System.err.println((c.x + x) + " , " + (c.y + y));
                }
                angle += angle_stepsize;
            }
        }

        // Calculando o angulo a partir da media dos 10 angulos com maior acumulo de pixels claros

        acc = CustomMapUtil.sortByValue(acc); // ordenando o acumulador pelo valor
        int count = 10;
        int angleSum = 0;
        for (Integer key : acc.keySet()) {
            angleSum += key;
            count--;
            if (count == 0) {
                break;
            }
        }

        //Pegando a media
        result.angle = new Integer(angleSum / 10);




        // 4 - Inclinacao
        // Procurar, na direcao do angulo medio, a distancia do centro ate o final da luz
        // isso serve para achar a inclinacao na caneta - qto mais inclinado, mais longe do centro
        // o halo fara reflexo
        float angleEstRad = (float) ((result.angle * 3.14) / 180);
        double x, y;
        int j = 0;
        int min = 15; // distancia minima do centro - pois ha uma esfera negra antes do halo
        int max = 40; // distancia maxima do centro para achar o threshold de stop da inclinacao
        int threshold = 120;
        
        BufferedImage resultAngleBuff = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_BYTE_GRAY);
        resultAngleBuff.getGraphics().drawImage(resultBuff, 0, 0, null);
        
        for (; j < max; j++) {
            x = (j * Math.cos(angleEstRad));
            y = (j * Math.sin(angleEstRad));

            int pixel = resultBuff.getRGB(55 + (int) x, 55 + (int) y);
            int value = (pixel) & 0xff;

            if (j > min && value < threshold) {
                break;
            }

            resultAngleBuff.setRGB(55 + (int) x, 55 + (int) y, 0xffffff00);
        }

        result.incl = Math.round(map(j, min, max, 0, 100));
        // Finish!
        System.out.println("Orientação: " + result.angle + " graus - Inclinação: " + result.incl + "%");


        res.setImage(resultAngleBuff);
        result.buffList.add(resultAngleBuff);
        //res.show();
        //res.getWindow().setLocation(935, 550);

        return result;
    }
}
