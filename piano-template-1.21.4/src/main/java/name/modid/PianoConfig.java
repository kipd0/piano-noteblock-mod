package name.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PianoConfig {

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();
p

        try {

            if (Files.exists(path)) {

                String json = Files.readString(path);

                PianoConfig config =
                        GSON.fromJson(json, PianoConfig.class);

                if (config != null) {
                    return config;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        PianoConfig config = new PianoConfig();

        config.songs.put(
                "My Song",
                new Song("My Song")
        );

        config.save();

        return config;
    }

    public void save() {

        try {

            Path path = getPath();

            Files.createDirectories(path.getParent());

            Files.writeString(
                    path,
                    GSON.toJson(this)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getPath() {

        return FabricLoader
                .getInstance()
                .getConfigDir()
                .resolve("piano.json");
    }

    public NoteBlockData getNoteBlock(int number) {

        return noteBlocks.get(number);
    }

    public void setNoteBlock(
            int number,
            NoteBlockData data
    ) {

        noteBlocks.put(number, data);

        save();
    }

    public Song getCurrentSong() {

        Song song = songs.get(currentSong);

        if (song == null) {

            song = new Song(currentSong);

            songs.put(
                    currentSong,
                    song
            );

            save();
        }

        return song;
    }
}
