/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.eng.moretto.view;

import br.eng.moretto.imgproc.AngleEstimator;
import br.eng.moretto.imgproc.AngleEstimatorResult;
import br.eng.moretto.imgproc.ConvertTo8bit;
import br.eng.moretto.imgproc.FindXY;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

/**
 *
 * @author emoretto
 */
public class Main extends javax.swing.JFrame {

     
    private final static int sleep = 1000;
    private final static int radius = 55;
    
    private static File[] fileList;
    private static int fileIndex = 0;
   
    private void findXY(ImagePlus ip){
        
        try {
            FindXY e = new FindXY();

            
              Rectangle r = new Rectangle();
                Point c = new Point();

                
               // Find XY			
                r = e.run(ip.getProcessor().getBufferedImage()); // r = rectangle of pointer
                c.setLocation(r.x + (r.width/2), r.y + (r.height/2)); // Calc center
                //buffImg.setRGB(c.x, c.y, 0xffff0000); // show center point
                labelXY.setText(c.x + "," + c.y);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
        
    }
    ImagePlus ipOri = new ImagePlus("Original"); 
         
    
    public void processImage() throws Exception {

        BufferedImage buffImg = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
        FindXY e = new FindXY();

        labelIndex.setText(fileIndex + "/" + fileList.length);
        
        //Load and iteract
        
        /**
         * PIPELINE
         */

        // Capture/read image
        buffImg = ImageIO.read(fileList[fileIndex]);
        ipOri.setImage(buffImg);
        panelImagem.getGraphics().drawImage(buffImg, 0, 0, null);

        // 8-Bit
        ipOri.setImage(ConvertTo8bit.convert(buffImg));

        //Threshold
        ByteProcessor bp = new ByteProcessor(ipOri.getImage());
        bp.setThreshold(0, 200, 1);
        ipOri.setProcessor("Viewer", bp);

        // Find XY			
        Rectangle r = e.run(ipOri.getProcessor().getBufferedImage()); // r = rectangle of pointer
        Point c = new Point(r.x + (r.width / 2), r.y + (r.height / 2)); // Calc center
        buffImg.setRGB(c.x, c.y, 0xffff0000); // show center point
//        panelImagem.getGraphics().drawOval(c.x-5, c.y-5, 10, 10);
        panelImagem.getGraphics().drawLine(c.x-5, c.y-5, c.x+5, c.y+5);
        panelImagem.getGraphics().drawLine(c.x-5, c.y+5, c.x+5, c.y-5);
        labelXY.setText(c.x + "," + c.y);

        
        if(c.x-radius > 0 && c.y-radius > 0 &&  c.x+radius+(r.width/2)+radius < 640 && c.y+(r.height/2)+radius < 480 ){

            AngleEstimatorResult result = AngleEstimator.run(buffImg, c, radius);
            
            panel1.getGraphics().drawImage( result.buffList.get(0) ,0,0,null);
            panel2.getGraphics().drawImage( result.buffList.get(1) ,0,0,null);
            panel3.getGraphics().drawImage( result.buffList.get(2) ,0,0,null);
            
            labelAngulo.setText(result.angle+"°");
            labelInclinacao.setText(result.incl+"%");
        }else{
            fileIndex++;
            processImage();
            return;
             //labelAngulo.setText("°");
//            labelInclinacao.setText("%");
        }
        
        fileIndex++;

    }
    public void processImage2() throws Exception {
		
            ImagePlus ipOri = new ImagePlus("Original"); 
            ImagePlus ipSeg = new ImagePlus("Segmented");
            ImagePlus ipProc = new ImagePlus("Viewer"); // Image processor para exibir a
            //ipOri.show();
            //ipProc.show();


            BufferedImage buffImg = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);


            
            Rectangle r = new Rectangle();
            Point c = new Point();

            //Load and iteract
            File dir = new File(Main.class.getResource("/images/").getPath());		
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
                     FindXY e = new FindXY();

                    r = e.run(ipProc.getProcessor().getBufferedImage()); // r = rectangle of pointer
                    c.setLocation(r.x + (r.width/2), r.y + (r.height/2)); // Calc center
                    buffImg.setRGB(c.x, c.y, 0xffff0000); // show center point
                    labelXY.setText(c.x + "," + c.y);



                    // Angle Estimator
                    // if I have center detected
                    
                    if(c.x-radius > 0 && c.y-radius > 0 &&  c.x+radius+(r.width/2)+radius < 640 && c.y+(r.height/2)+radius < 480 ){

                            Float angle = null;
                           // ipSeg.setImage(AngleEstimator.run(buffImg, c, radius, angle));
                            labelInclinacao.setText(angle+"o");

                    }else{
                            ipSeg.restoreRoi();
                    }


                    // IP		
                    panel2.getGraphics().drawImage(ipSeg.getImage(), 0, 0, null);
                    panel2.repaint();
                    
                    panelImagem.getGraphics().drawImage(ipOri.getImage(), 0, 0, null);
                    panelImagem.repaint();
                    
                    panel1.getGraphics().drawImage(ipOri.getImage(), 0, 0, null);
                    panel1.repaint();
                    
                    //ipSeg.show();
                    //ipSeg.repaintWindow();
                    //ipProc.show();
                    //ipProc.repaintWindow();
                    //ipOri.show();
                    //ipOri.repaintWindow();

                    //ipOri.getWindow().setLocation(0, 0);
                    //ipProc.getWindow().setLocation(650, 0);
                    //ipSeg.getWindow().setLocation(650, 550);

                    Thread.sleep(sleep);
            }
    }
    
    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        
        fileList = new File(Main.class.getResource("/images/").getPath()).listFiles();		
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelImagem = new javax.swing.JPanel();
        labelXY = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        panel1 = new javax.swing.JPanel();
        panel2 = new javax.swing.JPanel();
        panel3 = new javax.swing.JPanel();
        labelInclinacao = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        labelAngulo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelIndex = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelImagem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));

        org.jdesktop.layout.GroupLayout panelImagemLayout = new org.jdesktop.layout.GroupLayout(panelImagem);
        panelImagem.setLayout(panelImagemLayout);
        panelImagemLayout.setHorizontalGroup(
            panelImagemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 638, Short.MAX_VALUE)
        );
        panelImagemLayout.setVerticalGroup(
            panelImagemLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 478, Short.MAX_VALUE)
        );

        labelXY.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel1.setText("Ângulo estimado");

        jButton1.setText("Next");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));

        org.jdesktop.layout.GroupLayout panel1Layout = new org.jdesktop.layout.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 108, Short.MAX_VALUE)
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 108, Short.MAX_VALUE)
        );

        panel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));

        org.jdesktop.layout.GroupLayout panel2Layout = new org.jdesktop.layout.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 108, Short.MAX_VALUE)
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 108, Short.MAX_VALUE)
        );

        panel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));

        org.jdesktop.layout.GroupLayout panel3Layout = new org.jdesktop.layout.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 108, Short.MAX_VALUE)
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 108, Short.MAX_VALUE)
        );

        labelInclinacao.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel2.setText("Posição");

        labelAngulo.setFont(new java.awt.Font("Tw Cen MT", 1, 48)); // NOI18N

        jLabel3.setText("Inclinação estimada");

        jLabel4.setText("Halo segmentado");

        jLabel5.setText("Conversão p/ 8bit");

        jLabel6.setText("Subtração Red-Blue");

        jLabel7.setText("Estimador de ângulo");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(panelImagem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, labelAngulo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, labelIndex, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, labelXY, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(labelInclinacao, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4))
                        .add(109, 109, 109)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel6)
                                .add(0, 0, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel5)
                                    .add(panel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(113, 113, 113)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(panel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel7)
                                        .add(0, 0, Short.MAX_VALUE)))))
                        .add(148, 148, 148)
                        .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(panelImagem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(labelXY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)
                        .add(18, 18, 18)
                        .add(labelAngulo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1)
                        .add(18, 18, 18)
                        .add(labelInclinacao, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(labelIndex, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(panel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(panel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(jLabel4)
                                .add(jLabel5))
                            .add(jLabel7))
                        .add(0, 39, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel6))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
        SwingWorker worker = new SwingWorker() {
                    
        @Override
        protected Object doInBackground() throws Exception {
            try {
                processImage();
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "";
          }
        };
         worker.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel labelAngulo;
    private javax.swing.JLabel labelInclinacao;
    private javax.swing.JLabel labelIndex;
    private javax.swing.JLabel labelXY;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel panel3;
    private javax.swing.JPanel panelImagem;
    // End of variables declaration//GEN-END:variables
}
