package ru.myitschool.sundaycolob;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame extends ApplicationAdapter {
	public static final int SCR_WIDTH = 1280, SCR_HEIGHT = 720;
	public static int gameState; // 0-играем, 1-погибли, 2-ждём перезапуск
	public static int gameStateBefore;
	public static final int GAME_PLAY = 0;
	public static final int GAME_OVER = 1;
	public static final int WAIT_GAME_RESTART = 2;
	public static final int GAME_PAUSE = 3;

	SpriteBatch batch;
	OrthographicCamera camera;
	Vector3 touchPos;

	BitmapFont font;
	Texture imgSpaceShip;
	Texture imgStars;
	Texture imgShot;
	Texture imgEnemy[] = new Texture[3];
	Texture imgFragment[] = new Texture[4];
	Texture imgSoundOn, imgSoundOff;
	Sound sndShot;
	Sound sndExplosion;

	Stars stars[] = new Stars[2];
	SpaceShip spaceShip;
	Array<Shot> shot = new Array<>();
	Array<Enemy> enemy = new Array<>();
	Array<Fragment> fragment = new Array<>();

	long timeSpawnEnemy = 1000;
	long timeLastSpawnEnemy;

	int score;
	boolean isSoundOn = true;
	int nPlayers = 11;
	Player player[] = new Player[nPlayers];
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touchPos = new Vector3();

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("acs.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = new Color(0, 1, 0, 1); // rgba от 0 до 1
		parameter.size = 40;
		parameter.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
		font = generator.generateFont(parameter);
		generator.dispose();

		imgSpaceShip = new Texture("spaceship.png");
		imgStars = new Texture("stars.png");
		imgShot = new Texture("shot.png");
		imgEnemy[0] = new Texture("enemy0.png");
		imgEnemy[1] = new Texture("enemy1.png");
		imgEnemy[2] = new Texture("enemy2.png");
		for(int i=0; i<imgFragment.length; i++)
			imgFragment[i] = new Texture("fragment"+i+".png");
		imgSoundOn = new Texture("soundon.png");
		imgSoundOff = new Texture("soundoff.png");
		sndShot = Gdx.audio.newSound(Gdx.files.internal("blaster.mp3"));
		sndExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

		stars[0] = new Stars(0, 0);
		stars[1] = new Stars(0, SCR_HEIGHT);
		spaceShip = new SpaceShip();
		// создаём таблицу рекордов
		for(int i = 0; i<nPlayers; i++) player[i] = new Player("noname", 0);
		loadTableRecords();
	}

	@Override
	public void pause() {
		super.pause();
		gameStateBefore = gameState;
		gameState = GAME_PAUSE;
	}

	@Override
	public void render () {
		// обработка нажатий
		if(Gdx.input.justTouched()){
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);

			float x = touchPos.x-(SCR_WIDTH-35);
			float y = touchPos.y-(SCR_HEIGHT-35);
			float r = 25;
			if(x*x + y*y <= r*r) isSoundOn=!isSoundOn;
			else if(gameState==GAME_PLAY) {
				if (touchPos.x > spaceShip.x - spaceShip.width / 2 &&
						touchPos.x < spaceShip.x + spaceShip.width / 2) {
					shot.add(new Shot(spaceShip.x, spaceShip.y));
					if(isSoundOn) sndShot.play();
				} else spaceShip.dx = (touchPos.x - spaceShip.x) / 50;
			}
			else if(gameState==WAIT_GAME_RESTART){
				gameState=GAME_PLAY;
				for(int i=enemy.size-1; i>=0; i--) enemy.removeIndex(i);
				for(int i=fragment.size-1; i>=0; i--) fragment.removeIndex(i);
				for(int i=shot.size-1; i>=0; i--) shot.removeIndex(i);
				score = 0;
				spaceShip = new SpaceShip();
			}
			else if(gameState == GAME_PAUSE){
				gameState = gameStateBefore;
			}
		}

		// игровые события
		if(gameState!=GAME_PAUSE) {
			for (int i = 0; i < 2; i++) stars[i].move();

			if (TimeUtils.millis() > timeLastSpawnEnemy + timeSpawnEnemy) {
				enemy.add(new Enemy());
				timeLastSpawnEnemy = TimeUtils.millis();
			}

			for (int i = enemy.size - 1; i >= 0; i--) {
				for (int j = 0; j < shot.size; j++)
					if (enemy.get(i).overlap(shot.get(j))) {
						enemy.get(i).isAlive = false;
						shot.get(j).isAlive = false;
						if (isSoundOn) sndExplosion.play();
						for (int k = 0; k < 100; k++)
							fragment.add(new Fragment(enemy.get(i).x, enemy.get(i).y, enemy.get(i).type));
						score++;
					}
				enemy.get(i).move();
				if (!enemy.get(i).isAlive) enemy.removeIndex(i);
			}

			for (int i = shot.size - 1; i >= 0; i--) {
				shot.get(i).move();
				if (!shot.get(i).isAlive) shot.removeIndex(i);
			}

			for (int i = fragment.size - 1; i >= 0; i--) {
				fragment.get(i).move();
				if (!fragment.get(i).isAlive) fragment.removeIndex(i);
			}

			spaceShip.move();
			if (gameState == GAME_OVER) {
				gameState = WAIT_GAME_RESTART; // перешли в ожидание перезапуска
				spaceShip.isAlive = false;
				if (isSoundOn) sndExplosion.play();
				for (int k = 0; k < 100; k++)
					fragment.add(new Fragment(spaceShip.x, spaceShip.y, 3));
				saveTableRecords();
			}
		}

		// отрисовка всего
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(int i=0; i<2; i++)
			batch.draw(imgStars, stars[i].x, stars[i].y, stars[i].width, stars[i].height);

		for(int i=0; i<fragment.size; i++)
			batch.draw(imgFragment[fragment.get(i).type],
					fragment.get(i).x-fragment.get(i).width/2,
					fragment.get(i).y-fragment.get(i).height/2,
					fragment.get(i).width/2, fragment.get(i).height/2,
					fragment.get(i).width, fragment.get(i).height, 1, 1,
					fragment.get(i).angle, 0, 0, 100, 100, false, false);

		for(int i=0; i<enemy.size; i++)
			batch.draw(imgEnemy[enemy.get(i).type], enemy.get(i).x-enemy.get(i).width/2,
					enemy.get(i).y-enemy.get(i).height/2,
					enemy.get(i).width, enemy.get(i).height);

		for(int i=0; i<shot.size; i++)
			batch.draw(imgShot, shot.get(i).x-shot.get(i).width/2,
					shot.get(i).y-shot.get(i).height/2,
					shot.get(i).width, shot.get(i).height);

		if(spaceShip.isAlive)
			batch.draw(imgSpaceShip, spaceShip.x-spaceShip.width/2, spaceShip.y-spaceShip.height/2,
				spaceShip.width, spaceShip.height);

		font.draw(batch, "SCORE: "+score, 10, SCR_HEIGHT-10);
		if(isSoundOn) batch.draw(imgSoundOn, SCR_WIDTH-60, SCR_HEIGHT-60, 50, 50);
		else batch.draw(imgSoundOff, SCR_WIDTH-60, SCR_HEIGHT-60, 50, 50);

		if(gameState==WAIT_GAME_RESTART) {
			font.draw(batch, "GAME OVER", 0, SCR_HEIGHT/4*3, SCR_WIDTH, Align.center, false);
			for(int i=0; i<nPlayers-1; i++)
				font.draw(batch, (i+1)+". "+player[i].name+" - "+player[i].score, 0, SCR_HEIGHT/4*3-50-40*i, SCR_WIDTH, Align.center, false);
		}

		if(gameState==GAME_PAUSE)
			font.draw(batch,"PAUSE", 0, SCR_HEIGHT/2, SCR_WIDTH, Align.center, false);
		batch.end();
	}

	void saveTableRecords(){
		player[nPlayers-1].name = "Player";
		player[nPlayers-1].score = score;
		Player p;
		boolean flag=true;
		while (flag){
			flag = false;
			for (int i=0; i<nPlayers-1; i++)
				if(player[i].score<player[i+1].score){
					p=player[i];
					player[i]=player[i+1];
					player[i+1]=p;
					flag=true;
				}
		}
		Preferences pf = Gdx.app.getPreferences("TableRecords");
		for(int i=0; i<nPlayers; i++) {
			pf.putString("name"+i, player[i].name);
			pf.putInteger("score"+i, player[i].score);
		}
		pf.flush();
	}

	void loadTableRecords(){
		Preferences pf = Gdx.app.getPreferences("TableRecords");
		for(int i=0; i<nPlayers; i++) {
			if(pf.contains("name"+i)) player[i].name = pf.getString("name"+i);
			if(pf.contains("score"+i)) player[i].score = pf.getInteger("score"+i);
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		imgStars.dispose();
		imgSpaceShip.dispose();
		sndShot.dispose();
	}
}

