package com.example.supermariorun;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle; //Pastikan rectangle berasal dari gdx (bukan awt)

//import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class SuperMarioRun extends ApplicationAdapter {
	SpriteBatch batch;
	//Texture img;
	Texture background; Texture[] man;

	//Posisi Awal
	int manState = 0; int pause = 0;

	//Gerak Naik-Turun
	float gravity  = 0.2f; float velocity = 0; int manY = 0 ; Rectangle manRectangle;

	//Coins
	ArrayList<Integer> coinXs = new ArrayList<Integer>(  );
	ArrayList<Integer> coinYs = new ArrayList<Integer>(  );
	ArrayList<Rectangle> coinRectangles  = new ArrayList<Rectangle>(  );
	Texture coin;
	int cointCount;
	Random random;

	//Bomb (sama persis seperti Coins)
	ArrayList<Integer> bombXs = new ArrayList<Integer>(  );
	ArrayList<Integer> bombYs = new ArrayList<Integer>(  );
	ArrayList<Rectangle> bombRectangles  = new ArrayList<Rectangle>(  );
	Texture bomb;
	int bombCount;

	//Score
	int score = 0;
	BitmapFont font;
	int gameState = 0;

	//Dizzy karakter
	Texture dizzy;

	@Override
	public void create () {
		batch = new SpriteBatch();
		//img = new Texture("badlogic.jpg");
		background = new Texture( "bg.png");

		//Karakter
		man = new Texture[4];
		man[0] = new Texture( "frame-1.png" );
		man[1] = new Texture( "frame-2.png" );
		man[2] = new Texture( "frame-3.png" );
		man[3] = new Texture( "frame-4.png" );
		manY = Gdx.graphics.getHeight()/2;//posisi awal karakter secara vertical

		//Coin
		coin = new Texture( "coin.png" );
		random = new Random(  );

		//Bomb
		bomb = new Texture( "bomb.png" );

		//score
		font = new BitmapFont(  );
		font.setColor( Color.WHITE );
		font.getData().setScale( 10 );

		//Dizzy karakter
		dizzy = new Texture( "dizzy-1.png" );

	}

	//Function unt membuat Coin muncul di ketinggian yg berbeda
	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add( (int) height );
		coinXs.add( Gdx.graphics.getWidth() );
	}

	//Function unt membuat Bomb muncul di ketinggian yg berbeda
	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add( (int) height );
		bombXs.add( Gdx.graphics.getWidth() );
	}


	//Function selama game berjalan
	@Override
	public void render () {
		//Gdx.gl.glClearColor(1, 0, 0, 1);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//batch.begin();
		//batch.draw(img, 0, 0);
		//batch.end();
		batch.begin();

		//1.Memunculkan Background agar sesuai device
		batch.draw( background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight() );

		//Saat berada dalam Game (Game State)
		if(gameState == 1){
			//GAME IS LIVE

			//Coin
			//1.Memunculkan coin
			if(cointCount < 100){cointCount++;} else{cointCount=0;makeCoin();}
			//2.Menggambar coin
			coinRectangles.clear();
			for(int i = 0; i < coinXs.size(); i++){
				batch.draw( coin, coinXs.get( i ), coinYs.get( i ) );
				coinXs.set( i,coinXs.get( i ) - 4 );
				coinRectangles.add( new Rectangle( coinXs.get( i ), coinYs.get( i ),coin.getWidth(), coin.getHeight() ) );
			}

			//Bomb
			//1.Memunculkan bomb
			bombRectangles.clear();
			if(bombCount < 250){bombCount++;} else{bombCount=0;makeBomb();}
			//2.Menggambar coin
			for(int i = 0; i < bombXs.size(); i++){
				batch.draw( bomb, bombXs.get( i ), bombYs.get( i ) );
				bombXs.set( i,bombXs.get( i ) - 8 );
				bombRectangles.add( new Rectangle( bombXs.get( i ), bombYs.get( i ),bomb.getWidth(), bomb.getHeight() ) );
			}

			//Karakter
			//1.Animasi Karakter turun-naik
			velocity += gravity; manY -= velocity;
			//2.Animasi Karakter bergerak
			if(pause < 8){ pause++; } else{ pause = 0; if(manState < 3){ manState++; } else { manState = 0; }}
			//3.Menahan agar karakter tdk keluar layar
			if (manY <= 0 ){ manY = 0; }
			//4.Animasi Karakter loncat (saat di-sentuh)
			if(Gdx.input.justTouched()){ velocity = -10; }


		}else if(gameState == 0){
			//GAME RESTART
			if(Gdx.input.justTouched()){ gameState = 1; }

		}else if(gameState == 2){
			//GAME OVER
			if(Gdx.input.justTouched()){
				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;//posisi awal karakter secara vertical
				score = 0; velocity = 0;
				coinXs.clear(); coinYs.clear(); coinRectangles.clear(); cointCount = 0;
				bombXs.clear(); bombYs.clear(); bombRectangles.clear(); bombCount = 0;
			}
		}

		//5.Menggambar karakter
		if (gameState == 2){
			//Dizzy dude
			batch.draw( dizzy,Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY );
		}else{
			batch.draw( man[manState],Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY);
		}

		manRectangle = new Rectangle( Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight() );

		//Saat karakter & coin bertemu / bertabrakan
		for(int i = 0; i < coinRectangles.size(); i++){
			if(Intersector.overlaps( manRectangle,coinRectangles.get( i ) )){
				//Gdx.app.log( "Coin!","Coin Collision!" );
				score++;

				//Menghilangkan coin (saat bertabrakan dgn karakter)
				coinRectangles.remove( i ); coinXs.remove( i ); coinYs.remove( i ); break;
			}
		}

		//Saat karakter & bomb bertemu / bertabrakan
		for(int i = 0; i < bombRectangles.size(); i++){
			if(Intersector.overlaps( manRectangle,bombRectangles.get( i ) )){
				//Gdx.app.log( "Bomb!","Bomb Collision!" );
				gameState = 2;
			}
		}

		//Score
		font.draw( batch,String.valueOf( score ),100,200 );

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//img.dispose();
	}
}

//bugs :
//1.Ketinggian karakter bisa over device size
