package util;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;


public class TexturePacker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TexturePacker2.process("./assets/images", "./assets/images/textures", "textures.pack");
	}
}
