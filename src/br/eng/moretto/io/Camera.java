package br.eng.moretto.io;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

public class Camera {

	private Player player = null;

	private int sleep = 0;

	private boolean flagLigada = false; // essa serve para dizer se a camera foi
										// inicializada ou nao..

	public Camera(int sleep) {
		this.sleep = sleep;
	}

	public void startCamera() throws Exception {

		// Localizador de Dispositivos de vídeo no formato RGB
		//CaptureDeviceManager bossMan = new CaptureDeviceManager();
		Vector list = CaptureDeviceManager.getDeviceList(new RGBFormat());
		CaptureDeviceInfo device = (CaptureDeviceInfo) list.elementAt(0);

		MediaLocator loc = device.getLocator();
		
		
		this.player = Manager.createRealizedPlayer(loc);
		this.player.start();

		// Espera por alguns segundos para inicializar a câmera 
		Thread.sleep(this.sleep);
		flagLigada = true;
	}

	public void stopCamera() {
		this.player.close();
		this.player.deallocate();
		flagLigada = false;
	}

	public boolean Ligada() {
		return flagLigada;
	}

	public BufferedImage captureCamera() {
		// Grab a frame from the capture device
		FrameGrabbingControl frameGrabber = (FrameGrabbingControl) this.player
				.getControl("javax.media.control.FrameGrabbingControl");
		Buffer buf = frameGrabber.grabFrame();

		// Convert frame to an buffered image so it can be processed and saved
		Image img = (new BufferToImage((VideoFormat) buf.getFormat())
				.createImage(buf));
		BufferedImage buffImg = new BufferedImage(img.getWidth(null), img
				.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		g.drawImage(img, null, null);
		return (buffImg);
	}
}
