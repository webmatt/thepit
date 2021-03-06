package core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Logger;

import core.model.Block;
import core.model.Dude;
import core.model.Dude.State;
import core.model.Item;
import core.model.World;

/**
 * 
 * @author Matthias Töws <mat.toews@gmail.com>
 * 
 */
public class WorldRenderer {
	private static final Logger logger = new Logger(
			WorldRenderer.class.getCanonicalName(), Logger.DEBUG);
	private static final float RUNNING_FRAME_DURATION = 0.06f;
	private float ppu = 30f; // Pixel per unit

	private World world;
	private OrthographicCamera cam;

	ShapeRenderer shapeRenderer = new ShapeRenderer();

	/** Textures **/
	private TextureAtlas atlas;
	private TextureRegion dudeIdleLeft;
	private TextureRegion dudeIdleRight;
	private TextureRegion dudeFallLeft;
	private TextureRegion dudeFallRight;
	private TextureRegion blockTexture;
	private TextureRegion itemTexture;
	private TextureRegion dudeFrame;
	private Texture background;
	

	/** Animations **/
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;

	private SpriteBatch spriteBatch;
	private boolean debug = false;
	private int width;
	private int height;
	private float levelHeight;
	private float levelWidth;
	private float minY;
	private float maxY;
	private float camYNormal;

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
	
	public float getPpu()
	{
		return ppu;
	}
	
	public void setPpu(float ppu)
	{
		this.ppu = ppu;
		levelWidth = world.getLevel().getWidth() * ppu;
		levelHeight = world.getLevel().getHeight() * ppu;
	}
	
	public void loadBackground(String path)
	{
		background = new Texture(Gdx.files.internal(path));
	}
	
	public void loadBlock(String regionName)
	{
		TextureRegion temp = atlas.findRegion(regionName);
		if (temp != null)
		{
			blockTexture = temp;
		}
	}

	public WorldRenderer(World world, boolean debug) {
		this.world = world;
		levelWidth = world.getLevel().getWidth() * ppu;
		levelHeight = world.getLevel().getHeight() * ppu;
		this.cam = new OrthographicCamera();
		setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		setDebug(debug);
		spriteBatch = new SpriteBatch();
		loadTextures();
	}
	
	private void loadTextures() {
		atlas = new TextureAtlas(
				Gdx.files.internal("images/textures/textures.pack"));
		dudeIdleRight = atlas.findRegion("dude_idle");
		dudeIdleLeft = new TextureRegion(dudeIdleRight);
		dudeIdleLeft.flip(true, false);

		dudeFallRight = atlas.findRegion("dude_fall");
		dudeFallLeft = new TextureRegion(dudeFallRight);
		dudeFallLeft.flip(true, false);

		blockTexture = atlas.findRegion("block");
		itemTexture = atlas.findRegion("item");

		TextureRegion[] walkRightFrames = new TextureRegion[15];
		for (int i = 0; i < 15; i++) {
			walkRightFrames[i] = atlas.findRegion("dudewalk", i);
		}
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION,
				walkRightFrames);

		TextureRegion[] walkLeftFrames = new TextureRegion[15];

		for (int i = 0; i < 15; i++) {
			walkLeftFrames[i] = new TextureRegion(walkRightFrames[i]);
			walkLeftFrames[i].flip(true, false);
		}
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION,
				walkLeftFrames);

//		background = 
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
	
	public void renderFade(float alpha)
	{
        Gdx.gl.glEnable(GL10.GL_BLEND); 
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(1, 1, 1, alpha);
		
		shapeRenderer.rect(0, 0, width, height);
		
		shapeRenderer.end();
	}

	/**
	 * Updates the cameras position. (Currently focuses on the dude, maybe will
	 * change)
	 */
	private void updateCam() {
		float x = levelWidth / 2.0f;
		float y = cam.position.y;
		// Calculate the dudes position on the screen
		float dudeY = (world.getDude().y * ppu) - y + (height / 2.0f);

		// Check if the dude reaches a boundary (currently 30 % from top resp.
		// bottom9
		if (dudeY < minY) {
			y -= (minY - dudeY);
		}
		if (dudeY > maxY) {
			y += (dudeY - maxY);
		}

		// clamp the camera to bottom resp. top of the level
		y = Math.min(y, levelHeight - height / 2.0f);
		y = Math.max(y, height / 2.0f);

		cam.position.set(x, y, 0);
		cam.update();
		

		// This normalizes the camera y position to a range from 0.0 to 1.0 in
		// the level,
		// i obtained this formula through the last black magic session(just
		// kidding, basic math :) )
		// For detailled information how this works, ask the author.
		camYNormal = (cam.position.y - height / 2.0f)
				/ (levelHeight - height);
	}

	private void drawBackground() {
		if (background == null)
		{
			return;
		}
//		float bgHeight = background.getHeight();
		float bgHeight = levelHeight * 0.8f;
		float offset = bgHeight - levelHeight;
		spriteBatch.draw(background, 0, camYNormal * offset * (-1), levelWidth,
				bgHeight);
	}

	private void drawBlocks() {
		for (Block block : world.getDrawableBlocks((int) (width / ppu),
				(int) (height / ppu))) {
			spriteBatch.draw(blockTexture, block.x * ppu, block.y * ppu,
					block.width * ppu, block.height * ppu);
		}
	}

	private void drawItems() {
		for (Item item : world.getDrawableItems((int) (width / ppu),
				(int) (height / ppu))) {
			spriteBatch.draw(itemTexture, item.x * ppu, item.y * ppu,
					item.width * ppu, item.height * ppu);
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
		float x = (dude.x - 0.2f) * ppu;
		float y = dude.y * ppu;
		float width = (dude.width + 0.45f) * ppu;
		float height = dude.height * ppu;
		spriteBatch.draw(dudeFrame, x, y, width, height);
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
		shapeRenderer.setProjectionMatrix(cam.combined);

		shapeRenderer.begin(ShapeType.Line);

//		// render blocks
//		for (Block block : world.getDrawableBlocks((int) (width / PPU),
//				(int) (height / PPU))) {
//			debugRenderer.setColor(new Color(1, 1, 1, 1));
//			debugRenderer.rect(block.x * PPU, block.y * PPU, block.width * PPU,
//					block.height * PPU);
//		}
//
//		for (Item item : world.getDrawableItems((int) (width / PPU),
//				(int) (height / PPU))) {
//			debugRenderer.setColor(new Color(0, 1, 0, 1));
//			debugRenderer.rect(item.x * PPU, item.y * PPU, item.width * PPU,
//					item.height * PPU);
//		}

		// render The Dude
		Dude dude = world.getDude();
		shapeRenderer.setColor(new Color(1, 0, 0, 1));
		shapeRenderer.rect(dude.x * ppu, dude.y * ppu, dude.width * ppu,
				dude.height * ppu);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeType.Filled);

		// render collision blocks
		shapeRenderer.setColor(1, 1, 1, 1);
		for (Rectangle collRect : world.getCollisionRects()) {
			shapeRenderer.rect(collRect.x * ppu, collRect.y * ppu,
					collRect.width * ppu, collRect.height * ppu);
		}

		// render fps
		shapeRenderer.end();

	}

	public void dispose() {
		atlas.dispose();
		background.dispose();
	}
}
