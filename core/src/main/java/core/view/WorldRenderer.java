package core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Logger;

import core.model.Block;
import core.model.Dude;
import core.model.Dude.State;
import core.model.Item;
import core.model.World;

public class WorldRenderer {
	private static final Logger logger = new Logger(
			WorldRenderer.class.getCanonicalName(), Logger.DEBUG);
	private static final float RUNNING_FRAME_DURATION = 0.10f;
	private static final float PPU = 30f; // Pixel per unit

	private World world;
	private OrthographicCamera cam;

	ShapeRenderer debugRenderer = new ShapeRenderer();

	/** Textures **/
	private TextureRegion dudeIdleLeft;
	private TextureRegion dudeIdleRight;
	private TextureRegion dudeFallLeft;
	private TextureRegion dudeFallRight;
	private TextureRegion blockTexture;
	private TextureRegion itemTexture;
	private TextureRegion dudeFrame;
	private TextureRegion background;

	/** Animations **/
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;

	private SpriteBatch spriteBatch;
	private boolean debug = false;
	private int width;
	private int height;
	private float minY;
	private float maxY;

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
		this.minY = height * 0.3f;
		this.maxY = height * 0.7f;
		cam.setToOrtho(false, w, h);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public WorldRenderer(World world, boolean debug) {
		this.world = world;
		this.cam = new OrthographicCamera();
		setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		setDebug(debug);
		spriteBatch = new SpriteBatch();
		loadTextures();
	}

	private void loadTextures() {
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("images/textures/textures.pack"));
		dudeIdleLeft = atlas.findRegion("dude_idle");
		dudeIdleRight = new TextureRegion(dudeIdleLeft);
		dudeIdleRight.flip(true, false);

		dudeFallLeft = atlas.findRegion("dude_fall");
		dudeFallRight = new TextureRegion(dudeFallLeft);
		dudeFallRight.flip(true, false);

		blockTexture = atlas.findRegion("block");
		itemTexture = atlas.findRegion("item");

		TextureRegion[] walkLeftFrames = new TextureRegion[6];
		for (int i = 0; i < 6; i++) {
			walkLeftFrames[i] = atlas.findRegion("dude_walk", i);
		}
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION,
				walkLeftFrames);

		TextureRegion[] walkRightFrames = new TextureRegion[6];

		for (int i = 0; i < 6; i++) {
			walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
			walkRightFrames[i].flip(true, false);
		}
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION,
				walkRightFrames);

		background = atlas.findRegion("background");
	}

	public void render() {
		updateCam();

		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();

		drawBackground();
		drawBlocks();
		drawItems();
		drawDude();
		drawItemImage();

		if (isDebug()) {
			// drawFps();
		}

		spriteBatch.end();

		if (isDebug()) {
			drawDebug();
		}
	}

	/**
	 * Updates the cameras position. (Currently focuses on the dude, maybe will
	 * change)
	 */
	private void updateCam() {
		float x = (world.getLevel().getWidth() * PPU) / 2.0f;
		float y = cam.position.y;
		// Calculate the dudes position on the screen
		float dudeY = (world.getDude().y * PPU) - y + (height / 2.0f);

		// Check if the dude reaches a boundary (currently 30 % from top resp.
		// bottom9
		if (dudeY < minY) {
			y -= (minY - dudeY);
		}
		if (dudeY > maxY) {
			y += (dudeY - maxY);
		}

		// clamp the camera to bottom resp. top of the level
		y = Math.min(y, (world.getLevel().getHeight() * PPU) - height / 2.0f);
		y = Math.max(y, height / 2.0f);

		cam.position.set(x, y, 0);
		cam.update();
	}

	private void drawBackground() {
//		spriteBatch.draw(background, cam.position.x - width / 2.0f,
//				cam.position.y - height / 2.0f, width, height);
	}

	private void drawBlocks() {
		for (Block block : world.getDrawableBlocks((int) (width / PPU),
				(int) (height / PPU))) {
			spriteBatch.draw(blockTexture, block.x * PPU, block.y * PPU,
					block.width * PPU, block.height * PPU);
		}
	}

	private void drawItems() {
		for (Item item : world.getDrawableItems((int) (width / PPU),
				(int) (height / PPU))) {
			spriteBatch.draw(itemTexture, item.x * PPU, item.y * PPU,
					item.width * PPU, item.height * PPU);
		}
	}

	private void drawDude() {
		Dude dude = world.getDude();
		dudeFrame = dude.isFacingLeft() ? dudeIdleLeft : dudeIdleRight;
		if (dude.getState().equals(State.WALKING)) {
			dudeFrame = dude.isFacingLeft() ? walkLeftAnimation.getKeyFrame(
					dude.getStateTime(), true) : walkRightAnimation
					.getKeyFrame(dude.getStateTime(), true);
		} else if (dude.getState().equals(State.JUMPING)) {
			if (dude.getVelocity().y > 0) {
				// TODO: Jumping texture
				dudeFrame = dude.isFacingLeft() ? dudeFallLeft : dudeFallRight;
			} else {
				dudeFrame = dude.isFacingLeft() ? dudeFallLeft : dudeFallRight;
			}
		}
		spriteBatch.draw(dudeFrame, dude.x * PPU, dude.y * PPU, dude.width
				* PPU, dude.height * PPU);
	}

	private void drawItemImage() {
		Item item = world.getCollisionItem();
		if (item != null) {
			float height = this.height * 0.8f;
			TextureRegion texture = item.getImage();
			float ratio = ((float) texture.getRegionWidth())
					/ ((float) texture.getRegionHeight());
			float width = height * ratio;

			float x = cam.position.x - width / 2;
			float y = cam.position.y - height / 2;

			spriteBatch.draw(texture, x, y, width, height);
		}

	}

	// private void drawFps() {
	// BitmapFont font = new BitmapFont();
	// String fps = Gdx.graphics.getFramesPerSecond() + " fps";
	// float width = font.getBounds(fps).width;
	// font.draw(spriteBatch, fps, CAMERA_WIDTH - 1 - width, CAMERA_HEIGHT - 1);
	// }

	private void drawDebug() {
		debugRenderer.setProjectionMatrix(cam.combined);

		debugRenderer.begin(ShapeType.Line);

		// render blocks
		for (Block block : world.getDrawableBlocks((int) (width / PPU),
				(int) (height / PPU))) {
			debugRenderer.setColor(new Color(1, 1, 1, 1));
			debugRenderer.rect(block.x * PPU, block.y * PPU, block.width * PPU,
					block.height * PPU);
		}

		for (Item item : world.getDrawableItems((int) (width / PPU),
				(int) (height / PPU))) {
			debugRenderer.setColor(new Color(0, 1, 0, 1));
			debugRenderer.rect(item.x * PPU, item.y * PPU, item.width * PPU,
					item.height * PPU);
		}

		// render The Dude
		Dude dude = world.getDude();
		debugRenderer.setColor(new Color(1, 0, 0, 1));
		debugRenderer.rect(dude.x * PPU, dude.y * PPU, dude.width * PPU,
				dude.height * PPU);
		debugRenderer.end();

		debugRenderer.begin(ShapeType.Filled);

		// render collision blocks
		debugRenderer.setColor(1, 1, 1, 1);
		for (Rectangle collRect : world.getCollisionRects()) {
			debugRenderer.rect(collRect.x * PPU, collRect.y * PPU,
					collRect.width * PPU, collRect.height * PPU);
		}

		// render fps
		debugRenderer.end();

	}
}
