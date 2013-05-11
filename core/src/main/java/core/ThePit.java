package core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import core.screens.GameScreen;

public class ThePit extends Game {

	@Override
	public void create() {

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		setScreen(new GameScreen());
	}
}
