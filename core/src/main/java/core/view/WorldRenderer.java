package core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;

import core.model.Block;
import core.model.Dude;
import core.model.Dude.State;
import core.model.Item;
import core.model.World;

public class WorldRenderer {
	private static final Logger logger = new Logger(
			WorldRenderer.class.getCanonicalName(), Logger.DEBUG);
	private static final float CAMERA_WIDTH = 16f;
	private static final float CAMERA_HEIGHT = 16f;
	private static final float RUNNING_FRAME_DURATION = 0.10f;

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

	/** Animations **/
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;

	private SpriteBatch spriteBatch;
	private boolean debug = false;
	private int width;
	private int height;
	private float ppuX; // pixels per unit on the X axis
	private float ppuY; // pixels per unit on the Y axis

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
		ppuX = (float) w / CAMERA_WIDTH;
		ppuY = (float) h / CAMERA_HEIGHT;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public WorldRenderer(World world, boolean debug) {
		this.world = world;
		this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
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
	}

	public void render() {
		updateCam();

		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();
		
		drawBlocks();
		drawItems();
		drawDude();
		drawItemImage();
		
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
		float x = world.getDude().x;
		float y = world.getDude().y;
		float width_half = (CAMERA_WIDTH / 2);
		float height_half = (CAMERA_HEIGHT / 2);
		if ((x - width_half) < 0) {
			x = 0 + width_half;
		} else if ((x + width_half) > world.getLevel().getWidth()) {
			x = world.getLevel().getWidth() - width_half;
		}
		if ((y - height_half) < 0) {
			y = 0 + height_half;
		} else if ((y + height_half) > world.getLevel().getHeight()) {
			y = world.getLevel().getHeight() - height_half;
		}

		cam.position.set(x, y, 0);
		cam.update();
	}

	private void drawBlocks() {
		for (Block block : world.getDrawableBlocks((int) CAMERA_WIDTH,
				(int) CAMERA_HEIGHT)) {
			spriteBatch.draw(blockTexture, block.x, block.y, block.width,
					block.height);
		}
	}

	private void drawItems() {
		for (Item item : world.getDrawableItems((int) CAMERA_WIDTH,
				(int) CAMERA_HEIGHT)) {
			spriteBatch.draw(itemTexture, item.x, item.y, item.width,
					item.height);
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
		spriteBatch.draw(dudeFrame, dude.x, dude.y, dude.width, dude.height);
	}

	private void drawItemImage() {
		Item item = world.getCollisionItem();
		if (item != null) {
			float width = CAMERA_WIDTH / 2; // Half the screen width
			TextureRegion texture = item.getImage();
			float ratio = ((float) texture.getRegionHeight())
					/ ((float) texture.getRegionWidth());
			float height = width * ratio;

			float x = cam.position.x - width / 2;
			float y = cam.position.y - height / 2;

			spriteBatch.draw(texture, x, y, width, height);
		}

	}

	private void drawFps()
	{
//		BitmapFont font = new BitmapFont();
//		String fps = Gdx.graphics.getFramesPerSecond() + " fps";
//		float width = font.getBounds(fps).width;
//		font.draw(spriteBatch, fps, CAMERA_WIDTH - 1 - width, CAMERA_HEIGHT - 1);
	}

	private void drawDebug() {
		debugRenderer.setProjectionMatrix(cam.combined);

		debugRenderer.begin(ShapeType.Line);

		// render blocks
		for (Block block : world.getDrawableBlocks((int) CAMERA_WIDTH,
				(int) CAMERA_HEIGHT)) {
			debugRenderer.setColor(new Color(1, 1, 1, 1));
			debugRenderer.rect(block.x, block.y, block.width, block.height);
		}

		for (Item item : world.getDrawableItems((int) CAMERA_WIDTH,
				(int) CAMERA_HEIGHT)) {
			debugRenderer.setColor(new Color(0, 1, 0, 1));
			debugRenderer.rect(item.x, item.y, item.width, item.height);
		}

		// render The Dude
		Dude dude = world.getDude();
		debugRenderer.setColor(new Color(1, 0, 0, 1));
		debugRenderer.rect(dude.x, dude.y, dude.width, dude.height);
		debugRenderer.end();

		debugRenderer.begin(ShapeType.Filled);

		// render collision blocks
		debugRenderer.setColor(1, 1, 1, 1);
		for (Rectangle collRect : world.getCollisionRects()) {
			debugRenderer.rect(collRect.x, collRect.y, collRect.width,
					collRect.height);
		}

		// render fps
		debugRenderer.end();

	}
}
