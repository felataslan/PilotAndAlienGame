package com.keremkulac.pilotandalien;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;


import java.util.Random;

public class PilotAndAlien extends ApplicationAdapter {
	SpriteBatch batch; //Ekrana yansıtılacak grafik öğelerinin çizilmesini sağlar
	Texture backgroundLevel1;
	Texture backgroundLevel2;
	Texture backgroundLevel3;
	Texture backgroundLevel4;
	Texture backgroundLevel5;
	Texture backgroundLevel6;
	Texture pilot;
	Texture alien1;
	Texture alien2;
	Texture alien3;
	public float deviceSizeWith;
	public float deviceSizeHeight;
	float planeXAxis = 0;
	float planeYAxis = 0;
	int gameStatus = 0;
	float velocityPlane = 0; // Uçak hızı
	float velocityAlien = 4; // Uzaylı hızı
	float gravity = 0.25f; // Yer çekimi
	float distance = 0;
	Random random;
	Circle planeCircle;
	ShapeRenderer shapeRenderer;
	int score = 0;
	int level = 1;
	int numberPassedAlien = 0;
	int numberOfAliens = 4;
	float[] alienXAxis = new float[numberOfAliens];
	float[] alienOffset1 = new float[numberOfAliens];
	float[] alienOffset2 = new float[numberOfAliens];
	float[] alienOffset3 = new float[numberOfAliens];
	Circle[] enemyCircles1 = new Circle[numberOfAliens];
	Circle[] enemyCircles2 = new Circle[numberOfAliens];
	Circle[] enemyCircles3 = new Circle[numberOfAliens];
	BitmapFont fontScoreAndLevel;
	BitmapFont fontOver;
	BitmapFont fontEndScore;
	private Sound jumpSound;
	@Override
	// Oyun açıldığında yapılacaklar
	public void create() {
		deviceSizeWith = Gdx.graphics.getWidth();
		deviceSizeHeight = Gdx.graphics.getHeight();
		batch = new SpriteBatch();
		distance = deviceSizeWith / 2;
		random = new Random();
		planeXAxis = deviceSizeWith / 6;
		planeYAxis = deviceSizeHeight / 2;
		planeCircle = new Circle();
		shapeRenderer = new ShapeRenderer();
		texture();
		for (int i = 0; i < numberOfAliens; i++) {
			alienXAxis[i] = deviceSizeWith - alien1.getWidth() / 2 + i * (deviceSizeWith / 2);
			giveAlienOffset(i);
			// Uzaylıların hitbox tanımlamaları
			enemyCircles1[i] = new Circle();
			enemyCircles2[i] = new Circle();
			enemyCircles3[i] = new Circle();
		}
		setFont();
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("click.wav"));
	}

	@Override
	// Ekranda yapılacak işlemler
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)	;
		batch.begin(); // Çizim başlangıç
		// Kullanılan cihazın boyutuna göre arka plan ayarlandı
		batch.draw(backgroundLevel1, 0, 0, deviceSizeWith, deviceSizeHeight);
		if (gameStatus == 1) {
			// Uzaylı setinin x değeri uçağın x değerinden küçükse yani uçak uzaylı setini geçerse skor artar
			if (alienXAxis[numberPassedAlien] < planeXAxis) {
				score++;
				// Score 5 ve 5 in katı ise level artar
				if (score % 5 == 0) {
					level++;
					// Score 5 ve 5 in katı ise uzaylının hızı arttırılara uzaylının x ekseni değeri
					// daha hızlı değişir uzaylıların gelişi hızlanmış olur
					for (int i = 0; i < numberOfAliens; i++) {
						alienXAxis[i] = alienXAxis[i] - velocityAlien * 1.05f;
						velocityAlien *= 1.05f;
					}
				}
				// Geçilen uzaylı seti sayısı küçükse uzaylı seti sayısından geçilen uzaylı seti sayısını arttır arttır değilse 0 yap
				if (numberPassedAlien < numberOfAliens - 1) {
					numberPassedAlien++;
				} else {
					numberPassedAlien = 0;
				}
			}
			// Level a göre arka plan değişir
			changeBackground(level);
			// Score yazısının ekrandaki yeri ve değeri
			fontScoreAndLevel.draw(batch, "Score: ", 100, 1000);
			fontScoreAndLevel.draw(batch, String.valueOf(score), 300, 1000);
			// Level yazısının ekrandaki yeri ve değeri
			fontScoreAndLevel.draw(batch, "Level: ", Gdx.graphics.getWidth()-300, 1000);
			fontScoreAndLevel.draw(batch, String.valueOf(level), Gdx.graphics.getWidth()-125, 1000);
			// Ekrana dokunuldugunda ucak ne kadar hareket edecek
			if (Gdx.input.justTouched()) {
				velocityPlane = -7;
				// Ekrana dokunulduğunda zıplama sesini başlatır
				jumpSound.play();
			}
			for (int i = 0; i < numberOfAliens; i++) {
				// Uzaylıların x değeri küçükse uzaylının genişliğinden yani uzaylı ekranın dışına geçmişse
				if (alienXAxis[i] < -alien1.getWidth()) {
					alienXAxis[i] = alienXAxis[i] + numberOfAliens * distance;
					giveAlienOffset(i);
				} else {
					// Uzaylının x değerini uzaylı hızı kadar azaltarak uzaylının x ekseninde hareket ederek
					// ekranın dışına çıkmasını sağlar
					alienXAxis[i] = alienXAxis[i] - velocityAlien;
				}
				// Uzaylıların başlangıç konumu ve boyutları cihaz boyutuna göre orantılı olarak verilir
				// Uzaylıların x eksenleri aynı y eksenleri farklı verilir bu sayede aynı hizada gelirler
				batch.draw(alien1, alienXAxis[i], deviceSizeHeight / 2 + alienOffset1[i], deviceSizeWith / 12, deviceSizeHeight / 8);
				batch.draw(alien1, alienXAxis[i], deviceSizeHeight / 2 + alienOffset2[i], deviceSizeWith / 12, deviceSizeHeight / 8);
				batch.draw(alien1, alienXAxis[i], deviceSizeHeight / 2 + alienOffset3[i], deviceSizeWith / 12, deviceSizeHeight / 8);
				// Uzaylıların etrafına görünmez hitbox veriyoruz
				enemyCircles1[i] = new Circle(alienXAxis[i] + deviceSizeWith / 24, deviceSizeHeight / 2 + alienOffset1[i] + deviceSizeHeight / 16, deviceSizeWith / 24);
				enemyCircles2[i] = new Circle(alienXAxis[i] + deviceSizeWith / 24, deviceSizeHeight / 2 + alienOffset2[i] + deviceSizeHeight / 16, deviceSizeWith / 24);
				enemyCircles3[i] = new Circle(alienXAxis[i] + deviceSizeWith / 24, deviceSizeHeight / 2 + alienOffset3[i] + deviceSizeHeight / 16, deviceSizeWith / 24);
			}
			// Uçağın y ekseni değeri 0 dan büyük ise aşağıdakiler yapılıyor 0 dan küçük ise
			// uçağın ekrandan aşağı geçmesine izin verilmiyor oyun durumu 2 yapılıyor
			if (planeYAxis > 0) {
				// Uçağın hızına yerçekimini ekleyip(Yer çekimi negatif) uçağın y ekseni değerinden çıkarttık
				// bu sayede uçak sonsuza kadar zıplamıyor
				velocityPlane = velocityPlane + gravity;
				planeYAxis = planeYAxis - velocityPlane;
			} else {
				gameStatus = 2;
				// Uçağın pozisyonu y ekseninin ortasına gelir
				planeYAxis = deviceSizeHeight / 2;
			}
			//  Uçağın y kordinatı değeri ,telefon yüksekliği ile uçak boyutu farkından büyük ise oyun durumu 2 olur
			//  Uçağın ekranın üstünden çıkması engellendi
			if(planeYAxis  > deviceSizeHeight - deviceSizeHeight / 8){
				gameStatus = 2;
				// Uçağın pozisyonu y ekseninin ortasına gelir
				planeYAxis = deviceSizeHeight / 2;
			}

		} else if (gameStatus == 0) {
			if (Gdx.input.justTouched()) { // Ekrana dokunulduğunda oyun durumu 1 e gelir ve oyun durumu 1 deki işlemler gerçekleşir
				gameStatus = 1;
			}
		} else if (gameStatus == 2) {
			fontEndScore.draw(batch,"Your Score ",800,350);
			fontEndScore.draw(batch,String.valueOf(score),970,250);
			// Oyun bittiğinde ekrana yazdırılacak yazı ve yazdıralacağı yerin kordinatları
			fontOver.draw(batch, "GAME OVER! Tap To Play Again!", 375, deviceSizeHeight / 2);
			//Oyun bittiğinde ekrana dokunulduğu zaman oyun durumunu 1 yapar ve uçağı y ekseninin ortasına alır
			if (Gdx.input.justTouched()) {
				gameStatus = 1;
				// Uçağın pozisyonu y ekseninin ortasına gelir
				planeYAxis = deviceSizeHeight / 2;
				// Oyun bittiğinde ve ekrana dokunulduğunda uzaylıların y ve x ekseni kordinatları random belirlenir
				for (int i = 0; i < numberOfAliens; i++) {
					alienXAxis[i] = deviceSizeWith - alien1.getWidth() / 2 + i * (deviceSizeWith / 2);
					giveAlienOffset(i);
					enemyCircles1[i] = new Circle();
					enemyCircles2[i] = new Circle();
					enemyCircles3[i] = new Circle();
				}
				velocityPlane = 0;
				score = 0;
				numberPassedAlien = 0;
				level = 1;
				velocityAlien = 4;
			}
		}
		// Uçağın başlangıç konumu ve boyutları telefon boyutuna göre orantı olarak verilir
		batch.draw(pilot, planeXAxis, planeYAxis, deviceSizeWith / 12, deviceSizeHeight / 8);
		batch.end();  // Çizim bitiş
		// Uçağın görünmez hitbox değerleri verilir
		planeCircle.set(planeXAxis + deviceSizeWith / 24, planeYAxis + deviceSizeHeight / 16, deviceSizeWith / 24);
		// Hitbox ayarları için kullanılan kodlar İçi dolu siyah bir daire
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.circle(planeCircle.x,planeCircle.y,planeCircle.radius);
		for (int i = 0; i < numberOfAliens; i++) {
			// Hitbox konum ve boyut ayarları
			//shapeRenderer.circle(alienXAxis[i]+deviceSizeWith/24,deviceSizeHeight/2+alienOffset1[i]+deviceSizeHeight/16,deviceSizeWith/24);
			//shapeRenderer.circle(alienXAxis[i]+deviceSizeWith/24,deviceSizeHeight/2+alienOffset2[i]+deviceSizeHeight/16,deviceSizeWith/24);
			//shapeRenderer.circle(alienXAxis[i]+deviceSizeWith/24,deviceSizeHeight/2+alienOffset3[i]+deviceSizeHeight/16,deviceSizeWith/24);
			// Eğer uçağın hitboxsı herhangi bir uzaylının hitboxına dokunursa oyun durumu 2 olur oyun durur.
			if (Intersector.overlaps(planeCircle, enemyCircles1[i]) || Intersector.overlaps(planeCircle, enemyCircles2[i]) || Intersector.overlaps(planeCircle, enemyCircles3[i])) {
				gameStatus = 2;
				// Uçağın pozisyonu y ekseninin ortasına gelir
				planeYAxis = deviceSizeHeight / 2;
			}
		}
		shapeRenderer.end();
	}
	public void giveAlienOffset(int i){
		// 0-1 arasında random  bir değer bulup 0.5 den çıkarıyoruz bulup uzaylılar
		// Ekranın ortasına göre konumu ortaya çıkıyor ilk uzaylının
		alienOffset1[i] = (random.nextFloat() - 0.5f) * (deviceSizeHeight - 175);
		// İlk uzaylıya göre y ekseni 200 aşağıda olcak şekilde diğer uzaylılara y ekseni değerlerini veriyoruz
		alienOffset2[i] = alienOffset1[i] - 175;
		alienOffset3[i] = alienOffset2[i] - 175;
	}
	public void changeBackground(int level){
		// Level a göre arka plan değişir ve arka plan cihazın boyutlarına göre ayarlanır
		switch (level) {
			case 2:
				batch.draw(backgroundLevel2, 0, 0, deviceSizeWith, deviceSizeHeight);
				break;
			case 3:
				batch.draw(backgroundLevel3, 0, 0, deviceSizeWith, deviceSizeHeight);
				break;
			case 4:
				batch.draw(backgroundLevel4, 0, 0, deviceSizeWith, deviceSizeHeight);
				break;
			case 5:
				batch.draw(backgroundLevel5, 0, 0, deviceSizeWith, deviceSizeHeight);
				break;
			case 6:
				batch.draw(backgroundLevel6, 0, 0, deviceSizeWith, deviceSizeHeight);
				break;
			default:
		}
	}
	public void setFont(){
		// Ekranın sol altındaki score yazısının boyutu ve renginin ayarlanması
		fontScoreAndLevel = new BitmapFont();
		fontScoreAndLevel.setColor(Color.WHITE);
		fontScoreAndLevel.getData().setScale(4);
		// Oyun bittiğinde ekranda yazan GameOver yazısının rengi ve boyutunun ayarlanması
		fontOver = new BitmapFont();
		fontOver.setColor(Color.WHITE);
		fontOver.getData().setScale(6);
		// Level yazısının boyutu ve renginin ayarlanması
		fontEndScore = new BitmapFont();
		fontEndScore.setColor(Color.WHITE);
		fontEndScore.getData().setScale(5);
	}
	public void texture(){
		// Oyun içindeki resimlerin yolları Textura verilir
		backgroundLevel1 = new Texture("backgroundLevel1.jpg");
		backgroundLevel2 = new Texture("backgroundLevel2.png");
		backgroundLevel3 = new Texture("backgroundLevel3.png");
		backgroundLevel4 = new Texture("backgroundLevel4.png");
		backgroundLevel5 = new Texture("backgroundLevel5.png");
		backgroundLevel6 = new Texture("backgroundLevel6.jpg");
		pilot = new Texture("plane.png");
		alien1 = new Texture("alien.png");
		alien2 = new Texture("alien.png");
		alien3 = new Texture("alien.png");
	}
	@Override
	public void dispose() {
		jumpSound.dispose();
	}

}
