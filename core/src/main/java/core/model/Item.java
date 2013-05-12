package core.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Item extends Rectangle {

	public static final float HEIGHT = 0.4f;
	public static final float WIDTH = 0.4f;
	
	private TextureRegion image;
	
	public TextureRegion getImage() {
		return image;
	}

	public void setImage(TextureRegion image) {
		this.image = image;
	}

	public void setPosition(Vector2 position)
	{
		float x = position.x + 0.5f - (WIDTH / 2);
		set(x, position.y, WIDTH, HEIGHT);
	}

	public Item(Vector2 position, TextureRegion image) {
		setPosition(position);
		this.image = image;
	}
}
