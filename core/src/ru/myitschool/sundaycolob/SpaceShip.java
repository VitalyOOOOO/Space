package ru.myitschool.sundaycolob;

public class SpaceShip extends SpaceObject {
    SpaceShip() {
        super(0, 0);
        width = height = 100;
        x = MyGdxGame.SCR_WIDTH/2;
        y = height/2+20;
    }

    @Override
    void move() {
        super.move();
        if(x<width/2) {
            dx = 0;
            x = width/2;
        }
        if(x>MyGdxGame.SCR_WIDTH-width/2){
            dx = 0;
            x = MyGdxGame.SCR_WIDTH-width/2;
        }
    }
}
