package core.controller;

import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;

import core.model.World;

public class AudioController {
	private static final Logger logger = new Logger(
			AudioController.class.getCanonicalName(), Logger.DEBUG);
	private static final float MUSIC_VOLUME = 0.7f;
	private static final float SOUND_VOLUME = 0.2f;
	private static final float SECTOR_OFFSET = 0.3f;

	private final String[] soundNames = { "freed", "impact", "knock",
			"metallic", "metal_pipe", "running", "sonar", "steel_drop",
			"water", "wind" };

	/** Sounds/Musics **/
	private Array<Music> musics;
	private Sound rustle;
	private Array<Sound> sounds;

//	private int soundIndex;
//	private long lastTime, nextDuration;
	private long time;
	private float lastY = Float.MIN_VALUE;
	private World world;
	private boolean colliding;

	public AudioController(World world) {
		this.world = world;
		time = 0;
		colliding = false;
		loadMusic();
		loadSounds();
	}

	public void update(float delta) {
		time += (int) (delta * 1000);

		updateMusic();
		updateSound();
	}

	private void updateSound() {
		if (world.getCollisionItem() != null) {
			if (!colliding) {
				rustle.play(SOUND_VOLUME);
			}
			colliding = true;
		} else {
			colliding = false;
		}
//
//		if (lastTime + nextDuration >= time) {
//			sounds.get(soundIndex).play(SOUND_VOLUME);
//			nextDuration = MathUtils.random(8000, 13000);
//			soundIndex++;
//			if (soundIndex >= sounds.size)
//			{
//				sounds.shuffle();
//				soundIndex = 0;
//			}
//		}
	}

	private void updateMusic() {

		if (world.getDude().getY() == lastY) {
			return;
		}

		lastY = world.getDude().getY();

		int count = musics.size;
		if (count == 0) {
			return;
		}
		Music m;
		for (int i = 0; i < count; i++) {
			musics.get(i).pause();
		}

		if (count == 1) // Shortcut
		{
			m = musics.first();
			m.setVolume(MUSIC_VOLUME);
			m.play();
		} else {
			// First calculate the sector in which the dude is (sector in terms
			// of music)
			// Then calculate the dudes position in this sector, if the dude
			// happens to be
			// in the sector border offset zones the transition must be
			// calculated
			float sectorRange = (float) world.getLevel().getHeight()
					/ (float) count;
			float sectorOffset = sectorRange * SECTOR_OFFSET;
			int sectorIndex = (int) (world.getDude().getY() / sectorRange);
			float sectorPos = world.getDude().getY()
					- (sectorIndex * sectorRange);
			if (time % 3000 == 0) {
				// logger.debug("World-Height: " +
				// world.getLevel().getHeight());
				// logger.debug("Sector-Count: " + count);
				// logger.debug("Single-Range: " + sectorRange);
				// logger.debug("DudeY: " + world.getDude().getY());
				// logger.debug("DudeSector: " + sectorIndex);
				// logger.debug("DudeSectorPos: " + sectorPos);
			}

			if (sectorIndex >= count) {
				return;
			}

			m = musics.get(sectorIndex);

			float vol1, vol2;
			// Check if the dude is in a sector border zone
			// If yes calculate sound transitions
			if (sectorPos - sectorOffset <= 0) {
				// in lowest sector no transition to sector below
				if (sectorIndex == 0) {
					m.setVolume(MUSIC_VOLUME);
					m.play();
				} else {
					float offsetNormal = sectorPos / sectorOffset;
					vol1 = (offsetNormal * 0.5f + 0.5f);
					m.setVolume(vol1 * MUSIC_VOLUME);
					m.play();
					Music m2 = musics.get(sectorIndex - 1);
					vol2 = (0.5f - offsetNormal * 0.5f);
					m2.setVolume(vol2 * MUSIC_VOLUME);
					m2.play();
					// logger.debug("Above: Vol1: " + vol1 + ", vol2: " + vol2);
				}
			} else if (sectorPos + sectorOffset >= sectorRange) {
				if (sectorIndex == count - 1) {
					m.setVolume(MUSIC_VOLUME);
					m.play();
				} else {
					float offsetNormal = 1 - ((sectorPos + sectorOffset - sectorRange) / sectorOffset);
					vol1 = (offsetNormal * 0.5f + 0.5f);
					m.setVolume(vol1 * MUSIC_VOLUME);
					m.play();
					Music m2 = musics.get(sectorIndex + 1);
					vol2 = (0.5f - offsetNormal * 0.5f);
					m2.setVolume(vol2 * MUSIC_VOLUME);
					m2.play();
					// logger.debug("Below: Vol1: " + vol1 + ", vol2: " + vol2);
				}
			} else {
				m.setVolume(MUSIC_VOLUME);
				m.play();
			}

		}
	}

	private void loadSounds() {
		rustle = Gdx.audio.newSound(Gdx.files.internal("sounds/rustle.wav"));
//		sounds = new Array<Sound>();
//
//		for (String name : soundNames) {
//			sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/" + name
//					+ ".wav")));
//		}
//		sounds.shuffle();
//		soundIndex = 0;
//		lastTime = 0;
//		nextDuration = MathUtils.random(8000, 13000);
	}

	private void loadMusic() {
		musics = new Array<Music>();
		Music m;
		try {
			Scanner s = new Scanner(Gdx.files.internal("music.txt").read());
			while (s.hasNext()) {
				m = Gdx.audio.newMusic(Gdx.files.internal("music/"
						+ s.nextLine()));
				if (m != null) {
					m.setLooping(true);
					musics.add(m);
				}
			}
		} catch (GdxRuntimeException e) {
			logger.error("Cannot find music.txt");
		}
	}

	public void dispose() {
		for (Music m : musics) {
			m.dispose();
		}
//		for (Sound s : sounds)
//		{
//			s.dispose();
//		}
		rustle.dispose();
	}
}
