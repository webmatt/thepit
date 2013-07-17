package core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.utils.Logger;

import core.controller.DudeController;
import core.controller.AudioController;
import core.model.Level;
import core.model.World;
import core.view.WorldRenderer;

public class FinishScreen implements Screen, InputProcessor {

	private static final Logger logger = new Logger("ThePit", Logger.DEBUG);
	private static final float CLAMP_DELTA = 0.2f;

	private World world;
	private WorldRenderer renderer;
	private DudeController dudeController;
	private AudioController musicController;

	private boolean fadingIn;
	private float alpha;

	@Override
	public void dispose() {
		musicController.dispose();
		dudeController.dispose();
		renderer.dispose();
		world.dispose();
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
		fadingIn = true;
		alpha = 1.0f;

		world = new World(new Level("finish.png", "items_finish.txt"));
		renderer = new WorldRenderer(world, false);
		renderer.setPpu(20);
		renderer.loadBlock("block_finish");
		dudeController = new DudeController(world);
		musicController = new AudioController(world);
		Gdx.input.setInputProcessor(this);
		

		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.95f, 0.95f, 0.95f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Clamp the delta time to prevent the dude falling through blocks when
		// fps drops very low
		delta = Math.min(CLAMP_DELTA, delta);

		if (!fadingIn)
		{
			dudeController.update(delta);
		}
		musicController.update(delta);
		renderer.render();
		if (fadingIn)
		{
			if (alpha <= 0.0f)
			{
				fadingIn = false;
			}
			else
			{
				renderer.renderFade(alpha);
				alpha -= 0.01f;
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		renderer.setSize(width, height);
	}

	// * InputProcessor methods ***************************//

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.LEFT) {
			dudeController.leftPressed();
		}
		if (keycode == Keys.RIGHT) {
			dudeController.rightPressed();
		}
		if (keycode == Keys.SPACE || keycode == Keys.UP) {
			dudeController.jumpPressed();
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.LEFT) {
			dudeController.leftReleased();
		} else if (keycode == Keys.RIGHT) {
			dudeController.rightReleased();
		} else if (keycode == Keys.SPACE || keycode == Keys.UP) {
			dudeController.jumpReleased();
		}

		// Debug keys
		else if (keycode == Keys.NUM_1) {
			renderer.setDebug(!renderer.isDebug());
		} else if (keycode == Keys.NUM_2) {
			logger.debug(dudeController.getDude().toString());
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
//		if (screenY < height / 2)
//		{
//			controller.jumpPressed();
//		}
//		else if (screenX < width / 2)
//		{
//			controller.leftPressed();
//		}
//		else
//		{
//			controller.rightPressed();
//		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//		if (screenY < height / 2)
//		{
//			controller.jumpReleased();
//		}
//		else if (screenX < width / 2)
//		{
//			controller.leftReleased();
//		}
//		else
//		{
//			controller.rightReleased();
//		}
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
