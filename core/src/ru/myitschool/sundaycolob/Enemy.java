package ru.myitschool.sundaycolob;

import com.badlogic.gdx.math.MathUtils;

public class Enemy extends SpaceObject {
    int type;

    Enemy() {
        super(0, 0);
        width = height = 100;
        y = MyGdxGame.SCR_HEIGHT + height/2;
        x = MathUtils.random(width/2, MyGdxGame.SCR_WIDTH - width/2);
        dy = MathUtils.random(-4, -2);
        type = MathUtils.random(0, 2);
    }

    @Override
    void move() {
        super.move();
        if(y<-height/2) {
            isAlive=false;
            if(MyGdxGame.gameState==MyGdxGame.GAME_PLAY) MyGdxGame.gameState = MyGdxGame.GAME_OVER;
        }
    }
}
