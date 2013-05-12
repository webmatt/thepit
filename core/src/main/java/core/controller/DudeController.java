package core.controller;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;

import core.model.Block;
import core.model.Dude;
import core.model.Dude.State;
import core.model.Item;
import core.model.World;

public class DudeController {
	enum Keys {
		LEFT, RIGHT, JUMP
	}

	private static final Logger logger = new Logger("DudeController",
			Logger.DEBUG);
	private static final long LONG_JUMP_PRESS = 150l;
	private static final float ACCELERATION = 30f;
	private static final float GRAVITY = -20f;
	private static final float MAX_JUMP_SPEED = 7f;
	private static final float DAMP = 0.80f;
	private static final float MAX_VEL = 4f;

	// these are temporary
	private static final float WIDTH = 10f;

	private World world;
	private Dude dude;
	private long jumpPressedTime;
	private boolean jumpingPressed;
	private boolean grounded = false;
	private Array<Block> collidable = new Array<Block>();

	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};

	static Map<Keys, Boolean> keys = new HashMap<DudeController.Keys, Boolean>();

	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
	}

	public Dude getDude() {
		return dude;
	}

	public DudeController(World world) {
		this.world = world;
		this.dude = world.getDude();
	}

	// ** Key presses and touches **************** //

	public void leftPressed() {
		keys.get(keys.put(Keys.LEFT, true));
	}

	public void rightPressed() {
		keys.get(keys.put(Keys.RIGHT, true));
	}

	public void jumpPressed() {
		keys.get(keys.put(Keys.JUMP, true));
	}

	public void leftReleased() {
		keys.get(keys.put(Keys.LEFT, false));
	}

	public void rightReleased() {
		keys.get(keys.put(Keys.RIGHT, false));
	}

	public void jumpReleased() {
		keys.get(keys.put(Keys.JUMP, false));
		jumpingPressed = false;
	}

	/** The main update method **/
	public void update(float delta) {
		processInput();

		if (grounded && dude.getState().equals(State.JUMPING)) {
			dude.setState(State.IDLE);
		}

		// Initial vertical acceleration
		dude.getAcceleration().y = GRAVITY;
		dude.getAcceleration().scl(delta);

		// apply acc to vel
		dude.getVelocity().add(dude.getAcceleration().x,
				dude.getAcceleration().y);

		checkCollisionWithBlocks(delta);

		checkCollisionWithItems();

		if (dude.getAcceleration().x == 0) {
			dude.getVelocity().x *= DAMP;
		}
		// apply damping to halt The Dude nicely

		// ensure terminal velocity is not exceeded
		if (dude.getVelocity().x > MAX_VEL) {
			dude.getVelocity().x = MAX_VEL;
		}
		if (dude.getVelocity().x < -MAX_VEL) {
			dude.getVelocity().x = -MAX_VEL;
		}

		// update the dude's state time
		dude.update(delta);

	}

	private void checkCollisionWithBlocks(float delta) {
		// scale velocity to frame units(just for the calculation)
		dude.getVelocity().scl(delta);

		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle dudeRect = rectPool.obtain();
		// set the rectangle to the dude attributes
		dudeRect.set(dude);

		// first check the movement on the x axis
		int startX, endX;
		int startY = (int) dudeRect.y;
		int endY = (int) (dudeRect.y + dudeRect.height);
		// determine the direction our dude is heading which will be the
		// direction to check the collision
		if (dude.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(dudeRect.x + dude.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(dudeRect.x + dudeRect.width
					+ dude.getVelocity().x);
		}

		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate the dudes movement on the X axis
		dudeRect.x += dude.getVelocity().x;

		world.getCollisionRects().clear();

		for (Block block : collidable) {
			if (block == null)
				continue;
			if (dudeRect.overlaps(block)) {
				// if (dude.getVelocity().x < 0)
				// {
				// dude.getPosition().x = (block.getBounds().x +
				// block.getBounds().width + World.DELTA);
				// }
				// else
				// {
				// dude.getPosition().x = (block.getBounds().x -
				// dude.getBounds().width - World.DELTA);
				// }
				dude.getVelocity().x = 0;
				world.getCollisionRects().add(block);
				break;
			}
		}

		// reset the position on the X axis
		dudeRect.x = dude.x;

		// Now the same thing again for the Y axis
		startX = (int) dudeRect.x;
		endX = (int) (dudeRect.x + dudeRect.width);
		if (dude.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(dudeRect.y + dude.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(dudeRect.y + dudeRect.height
					+ dude.getVelocity().y);
		}
		populateCollidableBlocks(startX, startY, endX, endY);

		dudeRect.y += dude.getVelocity().y;

		for (Block block : collidable) {
			if (block == null)
				continue;
			if (dudeRect.overlaps(block)) {
				if (dude.getVelocity().y < 0) {
					grounded = true;
					dude.y = (block.y + block.height);
				} else {
					dude.y = (block.y - dude.height);
				}
				dude.getVelocity().y = 0;
				world.getCollisionRects().add(block);
				break;
			}
		}

		rectPool.free(dudeRect);

		// update the dudes position
		dude.add(dude.getVelocity());
		//
		// unscale velocity
		dude.getVelocity().scl(1 / delta);
	}

	private void checkCollisionWithItems() {
		// Clear (potential) collision item from previous frame
		world.setCollisionItem(null);

		int x = (int) Math.floor(dude.x);
		int y = (int) Math.floor(dude.y);

		if (x < 0 || y < 0) {
			// I don't know why, but if you move the application window around
			// sometimes
			// the dudes y gets below 0
			return;
		}

		// Check if the block the dude is walking in has an item
		Item item = world.getLevel().getItem(x, y);
		if (item != null) {
			// check if they collide
			if (dude.overlaps(item)) {
				world.setCollisionItem(item);
			}
		} else {
			// check item in the next block
			x++;
			Item item2 = world.getLevel().getItem(x, y);
			if (item2 != null) {
				// check if they collide
				if (dude.overlaps(item2)) {
					world.setCollisionItem(item2);
				}
			}
		}
	}

	private void populateCollidableBlocks(int startX, int startY, int endX,
			int endY) {
		collidable.clear();
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				if (x >= 0 && x < world.getLevel().getWidth() && y >= 0
						&& y < world.getLevel().getHeight()) {
					collidable.add(world.getLevel().getBlock(x, y));
				}
			}
		}
	}

	/** Change The Dude's state and parameters based on input controls **/
	private boolean processInput() {
		if (keys.get(Keys.JUMP)) {
			if (!dude.getState().equals(State.JUMPING)) {
				jumpingPressed = true;
				jumpPressedTime = System.currentTimeMillis();
				dude.setState(State.JUMPING);
				dude.getVelocity().y = MAX_JUMP_SPEED;
				grounded = false;
			} else {
				if (jumpingPressed
						&& ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
					jumpingPressed = false;
				} else {
					if (jumpingPressed) {
						dude.getVelocity().y = MAX_JUMP_SPEED;
					}
				}
			}
		}
		if (keys.get(Keys.LEFT)) {
			// left is pressed
			dude.setFacingLeft(true);
			if (!dude.getState().equals(State.JUMPING)) {
				dude.setState(State.WALKING);
			}
			dude.getAcceleration().x = -ACCELERATION;
		} else if (keys.get(Keys.RIGHT)) {
			// left is pressed
			dude.setFacingLeft(false);
			if (!dude.getState().equals(State.JUMPING)) {
				dude.setState(State.WALKING);
			}
			dude.getAcceleration().x = ACCELERATION;
		} else {
			if (!dude.getState().equals(State.JUMPING)) {
				dude.setState(State.IDLE);
			}
			dude.getAcceleration().x = 0;

		}
		return false;
	}
}
