package ru.myitschool.sundaycolob;

public class Stars extends SpaceObject {

	Stars(float x, float y){
		super(x, y);
		width = MyGdxGame.SCR_WIDTH;
		height = MyGdxGame.SCR_HEIGHT;
		dy = -0.5f;
	}

	@Override
	void move(){
		super.move();
		if(y<-MyGdxGame.SCR_HEIGHT) y = MyGdxGame.SCR_HEIGHT;
	}
}
