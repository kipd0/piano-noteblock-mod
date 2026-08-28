package name.modid;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class PianoClient implements ClientModInitializer {

    public static KeyMapping OPEN_NOTE_BLOCKS;
    public static KeyMapping OPEN_SONG_EDITOR;
    public static KeyMapping PLAY_SONG;

    public static final PianoConfig CONFIG = new PianoConfig();
    public static final SongPlayer PLAYER = new SongPlayer();

    @Override
    public void onInitializeClient() {

        OPEN_NOTE_BLOCKS = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.piano.note_blocks",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_N,
                        "category.piano"
                )
        );

        OPEN_SONG_EDITOR = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.piano.song_editor",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_M,
                        "category.piano"
                )
        );

        PLAY_SONG = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.piano.play_song",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_P,
                        "category.piano"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (OPEN_NOTE_BLOCKS.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new NoteBlockScreen());
                }
            }

            while (OPEN_SONG_EDITOR.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new SongScreen());
                }
            }

            while (PLAY_SONG.consumeClick()) {
                if (PLAYER.isPlaying()) {
                    PLAYER.stop();
                } else {
                    PLAYER.play(CONFIG.getCurrentSong());
                }
            }

            PLAYER.tick(client);
        });

        NoteBlockRenderer.register();
    }
}
