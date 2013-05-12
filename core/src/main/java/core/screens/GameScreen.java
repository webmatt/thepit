package core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.utils.Logger;

import core.controller.DudeController;
import core.model.World;
import core.view.WorldRenderer;

public class GameScreen implements Screen, InputProcessor {

	private static final Logger logger = new Logger("ThePit", Logger.DEBUG);
	private static final float CLAMP_DELTA = 0.2f;

	private World world;
	private WorldRenderer renderer;
	private DudeController controller;

	private int width, height;

	@Override
	public void dispose() {
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		world = new World();
		renderer = new WorldRenderer(world, false);
		controller = new DudeController(world);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Clamp the delta time to prevent the dude falling through blocks when
		// fps drops very low
		delta = Math.min(CLAMP_DELTA, delta);

		controller.update(delta);
		renderer.render();
	}

	@Override
	public void resize(int width, int height) {
		renderer.setSize(width, height);
		this.width = width;
		this.height = height;
	}

	// * InputProcessor methods ***************************//

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.LEFT) {
			controller.leftPressed();
		}
		if (keycode == Keys.RIGHT) {
			controller.rightPressed();
		}
		if (keycode == Keys.SPACE || keycode == Keys.UP) {
			controller.jumpPressed();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.LEFT) {
			controller.leftReleased();
		} else if (keycode == Keys.RIGHT) {
			controller.rightReleased();
		} else if (keycode == Keys.SPACE || keycode == Keys.UP) {
			controller.jumpReleased();
		}

		// Debug keys
		else if (keycode == Keys.NUM_1) {
			renderer.setDebug(!renderer.isDebug());
		} else if (keycode == Keys.NUM_2) {
			logger.debug(controller.getDude().toString());
		} else if (keycode == Keys.NUM_3) {
			world.getDude().x = world.getLevel().getStartPosition().x;
			world.getDude().y = world.getLevel().getStartPosition().y;
		} else if (keycode == Keys.Q) {
			Gdx.app.exit();
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
