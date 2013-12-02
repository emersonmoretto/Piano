/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.eng.moretto.imgproc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author emoretto
 */
public class AngleEstimatorResult {
    
    public Integer angle;
    
    public Integer incl;
    
    public ArrayList<BufferedImage> buffList = new ArrayList<BufferedImage>(); 
}
