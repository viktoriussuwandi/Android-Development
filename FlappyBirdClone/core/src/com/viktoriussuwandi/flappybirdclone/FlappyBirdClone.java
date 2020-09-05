package com.viktoriussuwandi.flappybirdclone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Shape;

import java.util.Random;

public class FlappyBirdClone extends ApplicationAdapter {
	//--------------------------------------------------------------------------------------------
	//1.Membuat variabel yg dibutuhkan
	//--------------------------------------------------------------------------------------------
	//a.Variabel unt game
	SpriteBatch batch;//unt menggambar element utama
	float velocity = 0;//kecepatan
	float gravity = 2;//posisi bird saat game berjalan
	//game masih berjalan/tidak,
	// Klo gameState = 0 -> game belum berjalan,
	// Klo gameState = 1 -> game berjalan normal
	// Klo gameState = 2 -> game berjalan, tapi sudah gameOver
	int gameState = 0;

	//unt scoring :
	int score = 0; int scoringTube = 0; BitmapFont scoreFont;

	//b.Variabel unt background & element selain character
	//b-1.Variabel unt semua gambar yg akan muncul
	Texture background; Texture topTube; Texture bottomTube;Texture gameOver;

	//b-2.Variabel unt mengatur bagaimana kemunculan gambar
	//-Kemunculan Tube secara Vertical (atas-bawah) dan horisontal
	//-Kemunculan Tube secara Horizontal (dari kanan ke kiri)
	//-Jumlah & jarak antar rangkaian Tube yg muncul

	//Jarak antar tube//
	float gap = 400; //jarak (gap) antara tobTube dan bottomTube
	float maxTubeOffSet; //variabel unt mengatur jarak (gap) maksimal antara tobTube dan bottomTube
	Random randomGenerator; //variabel unt memberi angka random unt jarak antar Tube

	//pergerakan setiap tube & setiap rangkaian Tube//
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float distanceBetweenTubes;

	//panjang'nya Tube
	float [] tubeOffSet = new float[numberOfTubes];//variabel unt mengatur panjang vertical Tube yg tampil
	float [] tubeX = new float[numberOfTubes];//variabel unt mengatur panjang horizontal Tube yg tampil

	//c.Variabel unt character (bird)
	Texture [] birds;
	int flapState = 0;
	float birdY = 0;//posisi vertical

	//d.Variabel unt Collition Detection -> pertemuan 2 elemen pada game
	//Circle -> unt bird, Rectangle -> unt Tubes
	//ShapeRenderer shapeRenderer;//unt menggambar pelengkap elemen utama -> sebagai batas tiap elemen unt mengatur intersection (tabrakan antar elemen)
	Circle birdCircle;
	Rectangle[] topTubeRectangle;
	Rectangle[] bottomTubeRectangle;

	@Override public void create () {
	//--------------------------------------------------------------------------------------------
	// 2.Inisiasi variabel
	//--------------------------------------------------------------------------------------------
		//a.Inisiasi unt variabel game
		batch = new SpriteBatch(  );
		scoreFont = new BitmapFont( ); scoreFont.setColor(Color.WHITE ); scoreFont.getData().setScale( 10 );

		//b.Inisiasi unt Collition Detection : Circle -> unt bird, Rectangle -> unt Tubes
		birdCircle = new Circle();
		//shapeRenderer = new ShapeRenderer();
		topTubeRectangle = new Rectangle[numberOfTubes]; bottomTubeRectangle = new Rectangle[numberOfTubes];

		//c.Inisiasi unt element selain character
		background = new Texture("bg.png" ); gameOver = new Texture( "gameover.png" );
		topTube = new Texture( "toptube.png" ); bottomTube = new Texture( "bottomtube.png" );
		//c.1.Inisiasi tube secara vertical
		maxTubeOffSet = Gdx.graphics.getHeight()/2 - gap/2 - 100; randomGenerator = new Random();
		//c.2.Inisiasi jarak tiap tubes secara horizontal pada suatu rangkaian tube yg muncul
		distanceBetweenTubes = Gdx.graphics.getWidth() * 5/9;
		//c.3.Inisiasi tampilan tube secara horizontal (muncul dalam 1 rangkaian tube -> tidak sendiri-sendiri)
		//    ada di function startGame()

		//d.Inisiasi unt variabel character
		birds = new Texture[2];
		birds[0] = new Texture( "bird.png" );
		birds[1] = new Texture( "bird2.png" );
		//d.1.Posisi awal bird secara vertical (horizontal tetap sama)
		//    ada di function startGame()

		startGame();
	}

	//Function untuk startGame
	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 -birds[0].getHeight()/2; //Posisi awal bird secara vertical (horizontal tetap sama)
		for (int i = 0; i < numberOfTubes ; i++){
			//-Atur tube secara vertical : create random number between -0.5 and 0.5 -> harus diatur di render(), agar muncul urutan tinggi yg berbeda pada tiap rangkaian Tubes
			//tubeOffSet[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			//-Atur tube secara horizontal
			// tubeX [i]= Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + i*distanceBetweenTubes; //dimulai dari tengah layar
			tubeX [i]= Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i*distanceBetweenTubes;//dimulai dari sebelah paling kanan layar
			//-Atur Collition Detection unt Rectangle Tubes'nya
			topTubeRectangle[i] = new Rectangle(); bottomTubeRectangle[i] = new Rectangle();
		}
	}

	//Function yg akan dijalankan terus-menerus
	@Override public void render () {
	//--------------------------------------------------------------------------------------------
	// 3.Mengatur game saat dijalankan
	//--------------------------------------------------------------------------------------------
		//Menggambar elemen general (baik ketika game berjalan ataupun ketika game tidak berjalan)
		//Background harus digambar di awal game, agar tidak menimpa gambar lainya
		batch.begin();
		batch.draw( background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );

		//---------------------------------------------------------------------------------------------------
		//gameState = 1 -> Saat game berjalan normal
		//---------------------------------------------------------------------------------------------------
		if(gameState == 1){
			//Scoring
			//-Memberi tanda Tube mana yg akan menjadi scoringTube
			//-Setiap bird melewati scoringTube, maka score akan bertambah
			if( tubeX[scoringTube] < Gdx.graphics.getWidth()/2 ) {
				score++; Gdx.app.log( "Score", String.valueOf( score ) );
				if(scoringTube < numberOfTubes-1) { scoringTube++; }
				else { scoringTube = 0; }
			}

			//Bird akan bergerak naik ketika user tapping
			if(Gdx.input.justTouched()){ velocity = -18; }

			//Menggambar elemen ketika game berjalan
			for(int i = 0; i < numberOfTubes; i++){

				//Menggambar rangkaian Tubes baru ketika TopTube sudah mencapai batas paling kiri layar
				if(tubeX[i] < -topTube.getWidth()){
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffSet[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}
				else {
					tubeX[i] = tubeX[i] - tubeVelocity;//Agar tube bergerak dari kanan ke kiri
				}

				//Tubes
				batch.draw( topTube, tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffSet[i]);
				batch.draw( bottomTube,tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffSet[i]);

				//Collition Detection unt Tubes
				topTubeRectangle[i] = new Rectangle( tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffSet[i], topTube.getWidth(),topTube.getHeight() );
				bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffSet[i], bottomTube.getWidth(),bottomTube.getHeight());
			}

			//Selama bird berada di atas posisi minimal (terendah)
			if(birdY > 0 ){
				birdY -= velocity;//agar posisi vertical bird bergerak menurun (klo user ga tapping layar'nya)
				velocity = velocity + gravity; //agar kecepatan terus bertambah seiring bird bergerak turun
			}else {
				gameState = 2;//Game over -> Klo bird menyentuh bagian layar paling bawah
			}
		}
		//---------------------------------------------------------------------------------------------------
		//gameState = 0 -> Saat game belum berjalan
		//---------------------------------------------------------------------------------------------------
		else if(gameState == 0){
			//Event saat user tap -> game akan berjalan
			if(Gdx.input.justTouched()){ gameState = 1; }
		//---------------------------------------------------------------------------------------------------
		//gameState = 2 -> Saat game Over
		//---------------------------------------------------------------------------------------------------
		}else if(gameState == 2){
			batch.draw( gameOver,Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2,Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2 );
			//Game akan restart saat user tapping :
			//-Kembalikan semua variabel ke nilai awal
			//-Jalankan game'nya lagi
			if(Gdx.input.justTouched()){ gameState = 1; score = 0; scoringTube = 0; velocity = 0; startGame(); }
		}

		if (flapState == 0){ flapState = 1; }else { flapState = 0; } //Membuat animasi bird -> flapState berguna agar gambar bird terus menerus berubah
		//Score & Character (Bird) digambar terakhir, agar tidak tertimpa gambar lainya
		batch.draw( birds[flapState],Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2,birdY); //agar posisi bird tepat di tengah bird & tengah layar
		scoreFont.draw (batch,String.valueOf( score ),100,200); //Menggambar scoring
		batch.end();

		//Menggambar collition detection : Circle -> unt bird, Rectangle -> unt Tubes
		birdCircle.set(Gdx.graphics.getWidth()/2,birdY + birds[flapState].getHeight()/2,birds[flapState].getWidth()/2 );
		//shapeRenderer.begin( ShapeRenderer.ShapeType.Filled );
		//shapeRenderer.setColor( Color.BLUE );

		//Unt bird
		//shapeRenderer.circle( birdCircle.x,birdCircle.y,birdCircle.radius );
		//Unt Tubes
		for(int i = 0; i < numberOfTubes; i++){
			//shapeRenderer.rect( tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffSet[i], topTube.getWidth(),topTube.getHeight() );
			//shapeRenderer.rect( tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffSet[i], bottomTube.getWidth(),bottomTube.getHeight() );

			//Cek saat bird menabrak Tubes (intersector) -> game over
			if( Intersector.overlaps( birdCircle,topTubeRectangle[i] ) || Intersector.overlaps( birdCircle,bottomTubeRectangle[i]) ){ gameState = 2; }
		}
		//shapeRenderer.end();
	}
	
	@Override public void dispose () { }
}

//Gdx.app.log( "Touched","Yep" );
//batch.dispose(); img.dispose();
//batch = new SpriteBatch(); img = new Texture("badlogic.jpg");
//Gdx.gl.glClearColor(1, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); batch.begin(); batch.draw(img, 0, 0); batch.end();