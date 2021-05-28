package com.myflapbird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class jogo extends ApplicationAdapter {

	//texturas do game
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoTopo;
	private Texture canoBaixo;

	//Movimentação do passaro
	private int movimentaY = 0;
	private int movimentaX = 0;

	private int pontos = 0;

	//tamanho do celular/emulador para o simulador
	private float larguraDispositivo;
	private float alturaDispositivo;

	//mudança de sprite do passaro no jogo
	private float variacao = 0;

	//gravidade durante o jogo
	private int gravidade = 0;

	//posicionamento inicial do passaro
	private float posicaoInicialVerticalPassaro = 0;

	//cano horizontal no jogo
	private float posicaoCanohorizontal;

	//posicionamento de um cano e outro cano
	private float espacoEntreCanos;

	//posicao do cano vertical
	private float posicaoCanoVertical;

	//valor aleatorio
	private Random random;

	//texto de pontuação
	BitmapFont textoPontuacao;

	//estado do passaro se passou entre os canos
	private boolean passouCano = false;

	private ShapeRenderer shapeRenderer;

	//Circulo de colider
	private Circle circuloPassaro;

	//Retangulo para o colider
	private Rectangle retanguloCanoCima;

	//Retangulo para o colider
	private Rectangle retanguloCanoBaixo;

	@Override
	public void create () {

		inicializarTexturas();
		inicializarObjetos();

	}

	private void inicializarTexturas() {

		//objetos das texturas
		batch = new SpriteBatch();
		random = new Random();

		//Textura para o passaro no jogo
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		//Textura de background do jogo
		fundo = new Texture("fundo.png");

		//Textura dos canos
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
	}

	private void inicializarObjetos() {


		//passa a referência do tamanho da tela
		larguraDispositivo = Gdx.graphics.getWidth();
		alturaDispositivo = Gdx.graphics.getHeight();

		//Posição do passaro na metade ta tela
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;

		//Posição dos canos no canto direito
		posicaoCanohorizontal = larguraDispositivo;
		espacoEntreCanos = 350;

		//Posição do texto de pontuação no jogo
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);

	}

	@Override
	public void render () {

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisao();

	}

	private void detectarColisao() {

		//Colider do cano topo
		retanguloCanoCima.set(posicaoCanohorizontal, alturaDispositivo / 2 - canoTopo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight());

		//Circulo colider do passaro
		circuloPassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);

		//Colider do cano baixo
		retanguloCanoBaixo.set(posicaoCanohorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight());

		boolean colisaoCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

	}

	private void verificarEstadoJogo() {

		//Movimentação dos canos horizontal
		posicaoCanohorizontal -= Gdx.graphics.getDeltaTime() * 200;
		if(posicaoCanohorizontal < - canoBaixo.getWidth()){
			posicaoCanohorizontal = larguraDispositivo;
			posicaoCanohorizontal = random.nextInt(400) - 200;
			passouCano = false;
		}

		//Toque na tela
		boolean toqueTela = Gdx.input.justTouched();

		//salto na vertical do passaro
		if(Gdx.input.justTouched()) {
			gravidade = -25;
		}
		//a função da gravidade no passaro
		if(posicaoInicialVerticalPassaro > 0 || toqueTela)
			posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;

		//passaro (animação)
		variacao += Gdx.graphics.getDeltaTime() * 10;

		gravidade++;
		//Realiza o aumento da variavel para a movimentação
		movimentaX++;

		//Limitando a variação do passaro
		if(variacao > 3)
			variacao = 0;
	}

	private void validarPontos() {
		//Ganho de pontos
		if(posicaoCanohorizontal < 50 - passaros[0].getWidth()) {
			if(!passouCano) {
				pontos++;
				passouCano = true;
			}
		}
	}

	private void desenharTexturas() {
		//Inicio da Renderização
		batch.begin();

		//desenho da posição no dispositivo
		batch.draw(fundo, 0,0, larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[(int) variacao], 50, posicaoInicialVerticalPassaro);

		//desenho dos canos
		batch.draw(canoBaixo, posicaoCanohorizontal - 100, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanohorizontal - 100, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);

		//desenho dos pontos
		textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 100);

		//fim da renderização
		batch.end();
	}

	@Override
	public void dispose () {

	}
}
