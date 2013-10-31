package br.eng.moretto.imgproc;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * THE ELIMINATOR!!! by Emerson G Moretto (emoretto@lsi.usp.br)
 * V. 2.0
 * 
 *  - Este plugin retira os ruídos e falsos candidatos.
 *  - Este código foi projetado (tentado) para ter um menor custo de
 * processamento, portanto haverá algumas desnecessidades aparentes.
 *  - E esse código (junto com alguns outros) me ajudou a tirar nota 10 no projeto de graduação. =]
 * 
 *  - TODO: 
 *  Tentar fazê-lo utilizando pilha ou fila ao invés de recursão
 *  R: Está implementado como uma espécie de pilha com transformada.. ficou excelente.. algo em torno de 30% mais rápido
 * 
 * http://www.moretto.eng.br/emerson
 */

public class Eliminator  {

	// Variáveis declarativas

	public final int pixel_bg = 0xffffffff; // pixel de fundo

	public final int pixel_ob = 0xff000000; // pixel do objeto

	// Variáveis de seleção de candidatos
	// Largura máxima de um falso candidato, ou seja, é um treshold para
	// eliminar o candidato, se o candidato tiver mais de w_max de largura, ele
	// já ta fora

	public int w_max = 200;

	public int w_min = 5;

	public int h_max = 200;

	public int h_min = 5;

	public int qtd_pixel_min = 10;
	
	// Variáveis gerais

	public int width; // Largura da Imagem

	public int height; // Altura da Imagem

	public int vet[], vetTemp[];

	// Variáveis do Algoritmo Recusivo

	private int direita;

	private int esquerda;

	private int cima;

	private int baixo;

	private int baixo_x = 0;  // valor x do ponto mais baixo encontrado

	private int qtd_pixel;


	Point c = new Point();	

	public Eliminator() {
	}

	public Point run(BufferedImage imgBuff) throws Exception {

		// //////Declaração de vars
		int i, j, offset, largura, altura, qtd_pixel_ant = 0;
		
		boolean found = false;
		

		width = imgBuff.getWidth();
		height = imgBuff.getHeight();
		

		vet = new int[width * height];
		vetTemp = new int[width * height];
		vet = (int[]) imgBuff.getRGB(0, 0, width, height, vet, 0, width);

		zera_vetTemp(); // zera o vetor auxiliar, que serve como marcador de
						// lugares onde a busca já passou

		try {

			for (i = 0; i < width; i++)
				for (j = 0; j < height; j++) {
					offset = i + width * j;

					direita = 0;
					esquerda = width;
					cima = height;
					baixo = 0;
					
					baixo_x = 0;
					largura = 0;
					altura = 0;


					if (vet[offset] == pixel_ob && vetTemp[offset] == pixel_bg){

						vetTemp[offset] = pixel_ob;

						qtd_pixel_ant = qtd_pixel;
						qtd_pixel = 1 + buscaPilha(i, j);
						


						largura = direita - esquerda;
						altura = baixo - cima;

						// Se é candidato
						if (largura < w_max && largura > w_min  && altura < h_max && altura > h_min && qtd_pixel > qtd_pixel_min) // verifica se é																// candidato de																// acordo com as																// heurísticas						
						{
							if (qtd_pixel > qtd_pixel_ant) {
								found = true;
								c.y = baixo;
								c.x = baixo_x;
							}
						} else {
							vetTemp[offset] = pixel_ob;
							vet[offset] = pixel_bg;
						}
					}
				}

		} catch (Exception e) {
			System.out.println("Erro NullPointer -> em: (" + direita + ","
					+ esquerda + "," + baixo + "," + cima);
		}
		
		if(found)
			return c;
		else
			return new Point(-1,-1);

	}


	private int buscaPilha(int start_x, int start_y) {

		int current_position = 0, pixels_filled = 1;
		int[] floodfill_x = new int[width * height];
		int[] floodfill_y = new int[width * height];
		baixo = 0;
		
		floodfill_x[0] = start_x;
		floodfill_y[0] = start_y;

		while (current_position < pixels_filled) {
			// remove o primeiro pixel do vetor
			int x = floodfill_x[current_position];
			int y = floodfill_y[current_position];
			current_position++;

			// esquerda
			if (x > 0)
				if (vet[(x - 1) + width * y] == pixel_ob
						&& vetTemp[(x - 1) + width * y] == pixel_bg) {
					// image.setRGB(x-1, y, new_color);
					vetTemp[(x - 1) + width * y] = pixel_ob;
					floodfill_x[pixels_filled] = x - 1;
					floodfill_y[pixels_filled] = y;
					pixels_filled++;
					
					if (esquerda > x - 1)
						esquerda = x - 1;
				}

			// direita
			if (x + 1 < width)
				if (vet[(x + 1) + width * y] == pixel_ob
						&& vetTemp[(x + 1) + width * y] == pixel_bg) {
					vetTemp[(x + 1) + width * y] = pixel_ob;
					floodfill_x[pixels_filled] = x + 1;
					floodfill_y[pixels_filled] = y;
					pixels_filled++;
					
					if (direita < x + 1)
						direita = x + 1;					
				}

			// cima
			if (y > 0)
				if (vet[x + width * (y - 1)] == pixel_ob
						&& vetTemp[x + width * (y - 1)] == pixel_bg) {
					vetTemp[x + width * (y - 1)] = pixel_ob;
					floodfill_x[pixels_filled] = x;
					floodfill_y[pixels_filled] = y - 1;
					pixels_filled++;
					
					if (cima > (y - 1))
						cima = (y - 1);					
				}

			// baixo
			if (y + 1 < height)
				if (vet[x + width * (y + 1)] == pixel_ob
						&& vetTemp[x + width * (y + 1)] == pixel_bg) {
					vetTemp[x + width * (y + 1)] = pixel_ob;
					floodfill_x[pixels_filled] = x;
					floodfill_y[pixels_filled] = y + 1;
					pixels_filled++;

					if (baixo < (y + 1)){
						baixo = (y + 1);
						baixo_x = x;
					}
				}
		}
		return pixels_filled;
	}

	void zera_vetTemp() {
		int i, j;
		for (i = 0; i < width; i++)
			for (j = 0; j < height; j++)
				vetTemp[i + width * j] = pixel_bg;
	}
	
	

	/** DEPRECATED - Fiz uma melhor acima. Está aqui ainda não sei porque  
	 * Essa função é recursiva e busca os 4 vizinhos (tipo carbono) do pixel
	 * 
	 * @param x
	 *            o x
	 * @param y
	 *            o y
	 * @param apaga
	 *            pra dizer se apaga o candidato
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private int busca(int x, int y, boolean apaga) {
		System.out.println("Entrou!!!" + x + "-" + y);
		int conta;
		conta = 0;

		if ((y + 1) < height)
			if (vet[x + width * (y + 1)] == pixel_ob
					&& (vetTemp[x + width * (y + 1)] == pixel_bg || apaga)) // Para
																			// baixo
			{
				vetTemp[x + width * (y + 1)] = pixel_ob;

				if (apaga)
					vet[x + width * (y + 1)] = pixel_bg;

				if (baixo < (y + 1))
					baixo = (y + 1);

				conta += 1 + busca(x, y + 1, apaga);
			}

		if ((x - 1) > 0)
			if (vet[(x - 1) + width * y] == pixel_ob
					&& (vetTemp[(x - 1) + width * y] == pixel_bg || apaga)) // Para
																			// tras
			{
				vetTemp[(x - 1) + width * y] = pixel_ob;

				if (apaga)
					vet[(x - 1) + width * y] = pixel_bg;

				if (esquerda > x - 1)
					esquerda = x - 1;

				conta += 1 + busca(x - 1, y, apaga);
			}

		if ((y - 1) > 0)
			if (vet[x + width * (y - 1)] == pixel_ob
					&& (vetTemp[x + width * (y - 1)] == pixel_bg || apaga)) // Para
																			// cima
			{
				vetTemp[x + width * (y - 1)] = pixel_ob;

				if (apaga)
					vet[x + width * (y - 1)] = pixel_bg;

				if (cima > (y - 1))
					cima = (y - 1);

				conta += 1 + busca(x, y - 1, apaga);
			}

		if ((x + 1) < width)
			if (vet[(x + 1) + width * y] == pixel_ob
					&& (vetTemp[(x + 1) + width * y] == pixel_bg || apaga)) // Para
																			// frente
			{
				vetTemp[(x + 1) + width * y] = pixel_ob;

				if (apaga)
					vet[(x + 1) + width * y] = pixel_bg;

				if (direita < x + 1)
					direita = x + 1;

				conta += 1 + busca(x + 1, y, apaga);
			}

		return conta;

	}


}
