package core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import core.screens.FinishScreen;
import core.screens.GameScreen;

public class ThePit extends Game {
	
	Screen gameScreen;
	Screen finishScreen;

	@Override
	public void create() {

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		gameScreen = new GameScreen(this);
		finishScreen = new FinishScreen();
		setScreen(gameScreen);
	}
	
	public void loadFinishScreen()
	{
		setScreen(finishScreen);
	}
}
