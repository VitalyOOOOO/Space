package ru.myitschool.sundaycolob;

import com.badlogic.gdx.math.MathUtils;

public class Fragment extends SpaceObject{
    int type;
    int speedRotation, angle; // скорость вращения и угол поворота
    float a, v; // угол вылета обломка, вектор скорости

    Fragment(float x, float y, int type) {
        super(x, y);
        this.type = type;
        width = MathUtils.random(10, 30);
        height = MathUtils.random(10, 30);
        a = MathUtils.random(0f, 360f);
        v = MathUtils.random(1f, 9f);
        dx = MathUtils.sin(a)*v;
        dy = MathUtils.cos(a)*v;
        speedRotation = MathUtils.random(-5, 5);
    }

    @Override
    void move() {
        super.move();
        angle += speedRotation;
        if(x<-width || x>MyGdxGame.SCR_WIDTH+width ||
        y<-height || y>MyGdxGame.SCR_HEIGHT+height) isAlive = false;
    }
}
