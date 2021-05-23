package ru.myitschool.sundaycolob;

public class Shot extends SpaceObject{

    Shot(float x, float y) {
        super(x, y);
        width = height = 100;
        dy = 10;
    }

    @Override
    void move() {
        super.move();
        if(y>MyGdxGame.SCR_HEIGHT+height/2) isAlive=false;
    }
}
