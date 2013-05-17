package core.model;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;

public class Level {

	static int EMPTY = 0x00000000;
	static int START_POSITION = 0xff0000ff; // RED
	static int ITEM = 0x00ff00ff; // GREEN
	static int BLOCK = 0xffffffff; // WHITE

	private static final Logger logger = new Logger("Level", Logger.DEBUG);
	private int width;
	private int height;
	private Vector2 startPosition;
	private Block[][] blocks;
	private Item[][] items;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Block[][] getBlocks() {
		return blocks;
	}

	public void setBlocks(Block[][] blocks) {
		this.blocks = blocks;
	}

	public Block getBlock(int x, int y) {
		return blocks[x][y];
	}
	
	public Item[][] getitems() {
		return items;
	}

	public void setItems(Item[][] items) {
		this.items = items;
	}

	public Item getItem(int x, int y) {
		return items[x][y];
	}

	public Vector2 getStartPosition() {
		return startPosition;
	}

	public Level() {
		// loadDemoWorld();
		loadImageWorld();
	}

	private void loadImageWorld() {
		

		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("images/textures/textures.pack"));
		TextureRegion itemImage = atlas.findRegion("item_dummy");
		
		Pixmap pm = new Pixmap(Gdx.files.internal("maps/02.png"));
		width = pm.getWidth();
		height = pm.getHeight();
		logger.debug("Map width: " + width);
		logger.debug("Map height: " + height);
		blocks = new Block[width][height];
		items = new Item[width][height];
		int flipY, color;
		Set<Integer> colors = new HashSet<Integer>();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				color = pm.getPixel(x, y);
				colors.add(color);
				if (color != EMPTY) {
					flipY = height - 1 - y; // Pixmap starts topleft, our cam starts bottom left, so we gotta flip it
					if (color == START_POSITION)
					{
						if (startPosition != null)
						{
							logger.error("Start position already set, but found another one (why?)");
						}
						else
						{
							startPosition = new Vector2(x, flipY);
						}
					}
					else if (color == ITEM)
					{
						items[x][flipY] = new Item(new Vector2(x, flipY), itemImage);
					}
					else if (color == BLOCK)
					{
						blocks[x][flipY] = new Block(new Vector2(x, flipY));
					}
				}
			}
		}

		if (getStartPosition() == null) {
			logger.error("No start position found in map");
			startPosition = new Vector2(0,0);
//			Gdx.app.exit();
		}

		String temp = "Different colors in map: ";
		for (Integer c : colors) {
			temp += "0x" + Integer.toHexString(c) + " ";
		}
		logger.debug(temp);
	}

	private void loadDemoWorld() {
		width = 10;
		height = 7;
		blocks = new Block[width][height];
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				blocks[col][row] = null;
			}
		}

		for (int col = 0; col < 10; col++) {
			blocks[col][0] = new Block(new Vector2(col, 0));
			blocks[col][6] = new Block(new Vector2(col, 6));
			if (col > 2) {
				blocks[col][1] = new Block(new Vector2(col, 1));
			}
		}
		blocks[9][2] = new Block(new Vector2(9, 2));
		blocks[9][3] = new Block(new Vector2(9, 3));
		blocks[9][4] = new Block(new Vector2(9, 4));
		blocks[9][5] = new Block(new Vector2(9, 5));

		blocks[6][3] = new Block(new Vector2(6, 3));
		blocks[6][4] = new Block(new Vector2(6, 4));
		blocks[6][5] = new Block(new Vector2(6, 5));
	}

}
