package core.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class World {
	
//	public static float DELTA = 0.0001f;

	/** Our protagonist (aka *The Dude*) */
	private Dude dude;
	/** The worlds level */
	private Level level;
	
	/** The collision boxes(just for debug) */
	private Array<Rectangle> collisionRects = new Array<Rectangle>();
	
	private Item collisionItem;

	public Dude getDude() {
		return dude;
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	public Item getCollisionItem() {
		return collisionItem;
	}

	public void setCollisionItem(Item collisionItem) {
		this.collisionItem = collisionItem;
	}

	public Array<Rectangle> getCollisionRects()
	{
		return collisionRects; 
	}
	
	public List<Block> getDrawableBlocks(int width, int height)
	{
		int x = (int) dude.x - width;
		int y = (int) dude.y - height;
		if (x < 0)
		{
			x = 0;
		}
		if (y < 0)
		{
			y = 0;
		}
		int x2 = x + 2 * width;
		int y2 = y + 2 * width;
		if (x2 >= level.getWidth())
		{
			x2 = level.getWidth() - 1;
		}
		if (y2 >= level.getHeight())
		{
			y2 = level.getHeight() - 1;
		}
		
		List<Block> blocks = new ArrayList<Block>();
		Block b;
		for (int col = x; col <= x2; col++)
		{
			for (int row = y; row <= y2; row++)
			{
				b = level.getBlock(col, row);
				if (b != null)
				{
					blocks.add(b);
				}
			}
		}
		return blocks;
	}
	

	public List<Item> getDrawableItems(int width, int height)
	{
		int x = (int) dude.x - width;
		int y = (int) dude.y - height;
		if (x < 0)
		{
			x = 0;
		}
		if (y < 0)
		{
			y = 0;
		}
		int x2 = x + 2 * width;
		int y2 = y + 2 * width;
		if (x2 >= level.getWidth())
		{
			x2 = level.getWidth() - 1;
		}
		if (y2 >= level.getHeight())
		{
			y2 = level.getHeight() - 1;
		}
		
		List<Item> items = new ArrayList<Item>();
		Item i;
		for (int col = x; col <= x2; col++)
		{
			for (int row = y; row <= y2; row++)
			{
				i = level.getItem(col, row);
				if (i != null)
				{
					items.add(i);
				}
			}
		}
		return items;
	}

	public World(Level level) {
		this.level = level;
		dude = new Dude(level.getStartPosition());
	}

	public void dispose() {
		level.dispose();
	}

}
