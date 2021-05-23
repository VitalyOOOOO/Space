package ru.myitschool.sundaycolob;

public class SpaceObject {
    float x, y;
    float width, height;
    float dx, dy;
    boolean isAlive = true;

    SpaceObject(float x, float y){
        this.x = x;
        this.y = y;
    }

    void move(){
        x += dx;
        y += dy;
    }

    boolean overlap(SpaceObject o){
        return Math.abs(x-o.x) < width/2+o.width/2 &&
                Math.abs(y-o.y) < height/3+o.height/3;
    }
}
