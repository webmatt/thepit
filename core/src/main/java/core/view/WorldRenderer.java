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
import core.model.World;

public class WorldRenderer {
	private static final Logger logger = new Logger(
			WorldRenderer.class.getCanonicalName(), Logger.DEBUG);
	private static final float CAMERA_WIDTH = 10f;
	private static final float CAMERA_HEIGHT = 7f;
	private static final float RUNNING_FRAME_DURATION = 0.06f;

	private World world;
	private OrthographicCamera cam;

	ShapeRenderer debugRenderer = new ShapeRenderer();

	/** Textures **/
	private TextureRegion dudeIdleLeft;
	private TextureRegion dudeIdleRight;
	private TextureRegion dudeFallLeft;
	private TextureRegion dudeFallRight;
	private TextureRegion blockTexture;
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
		ppuX = (float) width / CAMERA_WIDTH;
		ppuY = (float) height / CAMERA_HEIGHT;
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
		this.cam.position.set(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f, 0);
		this.cam.update();
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

		TextureRegion[] walkLeftFrames = new TextureRegion[6];
		for (int i = 0; i < 6; i++) {
			walkLeftFrames[i] = atlas.findRegion("dude", i);
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
		spriteBatch.begin();
		drawBlocks();
		drawDude();
		spriteBatch.end();
		if (isDebug()) {
			drawDebug();
		}
	}

	private void drawBlocks() {
		for (Block block : world.getDrawableBlocks((int) CAMERA_WIDTH, (int) CAMERA_HEIGHT)) {
			spriteBatch.draw(blockTexture, block.x * ppuX,
					block.y * ppuY, Block.SIZE * ppuX, Block.SIZE
							* ppuY);
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
		spriteBatch
				.draw(dudeFrame, dude.x * ppuX,
						dude.y * ppuY, Dude.SIZE * ppuX,
						Dude.SIZE * ppuY);
	}

	private void drawDebug() {
		debugRenderer.setProjectionMatrix(cam.combined);
		
		debugRenderer.begin(ShapeType.Line);
		
		// render blocks
		for (Block block : world.getDrawableBlocks((int) CAMERA_WIDTH, (int) CAMERA_HEIGHT)) {
			debugRenderer.setColor(new Color(1, 0, 0, 1));
			debugRenderer.rect(block.x, block.y, block.width, block.height);
		}
		
		// render The Dude
		Dude dude = world.getDude();
		debugRenderer.setColor(new Color(0, 1, 0, 1));
		debugRenderer.rect(dude.x, dude.y, dude.width, dude.height);
		debugRenderer.end();
		
		debugRenderer.begin(ShapeType.Filled);
		
		// render collision blocks
		debugRenderer.setColor(1, 1, 1, 1);
		for (Rectangle collRect : world.getCollisionRects())
		{
			debugRenderer.rect(collRect.x, collRect.y, collRect.width, collRect.height);
		}
		debugRenderer.end();
				
	}
}
