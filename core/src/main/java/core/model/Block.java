package core.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block extends Rectangle {

	public static final float SIZE = 1f;

	public Block(Vector2 position) {
		set(position.x, position.y, SIZE, SIZE);
	}
}
