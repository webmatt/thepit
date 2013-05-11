package core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

import core.controller.DudeController;
import core.model.World;
import core.view.WorldRenderer;

public class GameScreen implements Screen, InputProcessor {

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
		renderer = new WorldRenderer(world, true);
		controller = new DudeController(world);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

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
		if (keycode == Keys.LEFT)
		{
			controller.leftPressed();
		}
		if (keycode == Keys.RIGHT)
		{
			controller.rightPressed();
		}
		if (keycode == Keys.SPACE || keycode == Keys.UP)
		{
			controller.jumpPressed();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.LEFT)
		{
			controller.leftReleased();
		}
		if (keycode == Keys.RIGHT)
		{
			controller.rightReleased();
		}
		if (keycode == Keys.SPACE || keycode == Keys.UP)
		{
			controller.jumpReleased();
		}
		if (keycode == Keys.D)
		{
			renderer.setDebug(!renderer.isDebug());
		}
		if (keycode == Keys.Q)
		{
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
