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
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    /*
     * OLD STORAGE
     *
     * Keep this here so existing piano.json files
     * can automatically be migrated.
     */
    public Map<Integer, NoteBlockData> noteBlocks =
            new HashMap<>();

    /*
     * NEW LAYOUT STORAGE
     */
    public Map<String, NoteBlockLayout> layouts =
            new HashMap<>();

    public String currentLayout =
            "Default";

    /*
     * SONG STORAGE
     */
    public Map<String, Song> songs =
            new HashMap<>();

    public String currentSong =
            "My Song";

    public static PianoConfig load() {

        Path path =
                getPath();

        PianoConfig config = null;

        try {

            if (Files.exists(path)) {

                String json =
                        Files.readString(path);

                config =
                        GSON.fromJson(
                                json,
                                PianoConfig.class
                        );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (config == null) {
            config = new PianoConfig();
        }

        /*
         * Repair old or incomplete config files.
         */
        if (config.noteBlocks == null) {
            config.noteBlocks =
                    new HashMap<>();
        }

        if (config.layouts == null) {
            config.layouts =
                    new HashMap<>();
        }

        if (config.songs == null) {
            config.songs =
                    new HashMap<>();
        }

        if (config.currentLayout == null ||
                config.currentLayout.isBlank()) {

            config.currentLayout =
                    "Default";
        }

        if (config.currentSong == null ||
                config.currentSong.isBlank()) {

            config.currentSong =
                    "My Song";
        }

        /*
         * =====================================================
         * MIGRATE OLD NOTE BLOCKS
         * =====================================================
         *
         * If you already assigned note blocks before this
         * update, copy them into the Default layout.
         */

        if (config.layouts.isEmpty()) {

            NoteBlockLayout defaultLayout =
                    new NoteBlockLayout(
                            "Default"
                    );

            defaultLayout.noteBlocks.putAll(
                    config.noteBlocks
            );

            config.layouts.put(
                    "Default",
                    defaultLayout
            );

            config.currentLayout =
                    "Default";
        }

        /*
         * Make sure the selected layout exists.
         */
        if (!config.layouts.containsKey(
                config.currentLayout
        )) {

            config.currentLayout =
                    config.layouts
                            .keySet()
                            .iterator()
                            .next();
        }

        /*
         * Repair layout data if necessary.
         */
        for (Map.Entry<String, NoteBlockLayout> entry :
                config.layouts.entrySet()) {

            NoteBlockLayout layout =
                    entry.getValue();

            if (layout == null) {

                layout =
                        new NoteBlockLayout(
                                entry.getKey()
                        );

                entry.setValue(layout);
            }

            if (layout.name == null ||
                    layout.name.isBlank()) {

                layout.name =
                        entry.getKey();
            }

            if (layout.noteBlocks == null) {

                layout.noteBlocks =
                        new HashMap<>();
            }
        }

        /*
         * Make sure at least one song exists.
         */
        if (!config.songs.containsKey(
                config.currentSong
        )) {

            config.songs.put(
                    config.currentSong,
                    new Song(
                            config.currentSong
                    )
            );
        }

        config.save();

        return config;
    }

    public void save() {

        try {

            Path path =
                    getPath();

            Files.createDirectories(
                    path.getParent()
            );

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
                .resolve(
                        "piano.json"
                );
    }

    /*
     * =========================================================
     * LAYOUTS
     * =========================================================
     */

    public NoteBlockLayout getCurrentLayout() {

        NoteBlockLayout layout =
                layouts.get(
                        currentLayout
                );

        if (layout == null) {

            layout =
                    new NoteBlockLayout(
                            currentLayout
                    );

            layouts.put(
                    currentLayout,
                    layout
            );

            save();
        }

        return layout;
    }

    public void createLayout(
            String name
    ) {

        if (name == null) {
            return;
        }

        name =
                name.trim();

        if (name.isEmpty()) {
            return;
        }

        if (!layouts.containsKey(name)) {

            layouts.put(
                    name,
                    new NoteBlockLayout(name)
            );
        }

        currentLayout =
                name;

        save();
    }

    public void selectLayout(
            String name
    ) {

        if (!layouts.containsKey(name)) {
            return;
        }

        currentLayout =
                name;

        save();
    }

    public void deleteLayout(
            String name
    ) {

        if (layouts.size() <= 1) {
            return;
        }

        layouts.remove(name);

        if (name.equals(currentLayout)) {

            currentLayout =
                    layouts
                            .keySet()
                            .iterator()
                            .next();
        }

        save();
    }

    /*
     * =========================================================
     * NOTE BLOCKS
     * =========================================================
     */

    public NoteBlockData getNoteBlock(
            int number
    ) {

        return getCurrentLayout()
                .noteBlocks
                .get(number);
    }

    public void setNoteBlock(
            int number,
            NoteBlockData data
    ) {

        getCurrentLayout()
                .noteBlocks
                .put(
                        number,
                        data
                );

        save();
    }

    /*
     * =========================================================
     * SONGS
     * =========================================================
     */

    public Song getCurrentSong() {

        Song song =
                songs.get(
                        currentSong
                );

        if (song == null) {

            song =
                    new Song(
                            currentSong
                    );

            songs.put(
                    currentSong,
                    song
            );

            save();
        }

        return song;
    }
}
