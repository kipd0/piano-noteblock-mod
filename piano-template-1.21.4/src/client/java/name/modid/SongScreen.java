package name.modid;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SongScreen extends Screen {

    private EditBox songNameBox;
    private EditBox noteBox;
    private EditBox timeBox;

    private String message = "";

    public SongScreen() {
        super(Component.literal("Song Editor"));
    }

    @Override
    protected void init() {

        int centerX = width / 2;

        songNameBox =
                new EditBox(
                        font,
                        centerX - 100,
                        50,
                        200,
                        20,
                        Component.literal("Song Name")
                );

        songNameBox.setValue(
                PianoClient.CONFIG.currentSong
        );

        noteBox =
                new EditBox(
                        font,
                        centerX - 100,
                        100,
                        80,
                        20,
                        Component.literal("Note")
                );

        timeBox =
                new EditBox(
                        font,
                        centerX + 20,
                        100,
                        80,
                        20,
                        Component.literal("Time")
                );

        noteBox.setValue("1");
        timeBox.setValue("0.5");

        addRenderableWidget(songNameBox);
        addRenderableWidget(noteBox);
        addRenderableWidget(timeBox);

        addRenderableWidget(
                Button.builder(
                        Component.literal("New Song"),
                        button -> newSong()
                ).bounds(
                        centerX - 100,
                        130,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Save Song"),
                        button -> saveSong()
                ).bounds(
                        centerX + 5,
                        130,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Load Song"),
                        button -> loadSong()
                ).bounds(
                        centerX - 100,
                        160,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Delete Song"),
                        button -> deleteSong()
                ).bounds(
                        centerX + 5,
                        160,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Add Note"),
                        button -> addNote()
                ).bounds(
                        centerX - 100,
                        190,
                        200,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Play"),
                        button -> {

                            PianoClient.PLAYER.play(
                                    PianoClient.CONFIG
                                            .getCurrentSong()
                            );

                        }
                ).bounds(
                        centerX - 100,
                        220,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Stop"),
                        button ->
                                PianoClient.PLAYER.stop()
                ).bounds(
                        centerX + 5,
                        220,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Clear Current Song"),
                        button -> {

                            PianoClient.PLAYER.stop();

                            PianoClient.CONFIG
                                    .getCurrentSong()
                                    .notes
                                    .clear();

                            PianoClient.CONFIG.save();

                            message =
                                    "Current song cleared.";

                        }
                ).bounds(
                        centerX - 100,
                        250,
                        200,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        button -> onClose()
                ).bounds(
                        centerX - 100,
                        280,
                        200,
                        20
                ).build()
        );
    }

    private void newSong() {

        PianoClient.PLAYER.stop();

        String name =
                songNameBox
                        .getValue()
                        .trim();

        if (name.isEmpty()) {

            message =
                    "Enter a song name first.";

            return;
        }

        if (PianoClient.CONFIG
                .songs
                .containsKey(name)) {

            message =
                    "That song already exists.";

            return;
        }

        Song song =
                new Song(name);

        PianoClient.CONFIG
                .songs
                .put(name, song);

        PianoClient.CONFIG.currentSong =
                name;

        PianoClient.CONFIG.save();

        message =
                "Created song: " + name;
    }

    private void saveSong() {

        String newName =
                songNameBox
                        .getValue()
                        .trim();

        if (newName.isEmpty()) {

            message =
                    "Enter a song name.";

            return;
        }

        String oldName =
                PianoClient.CONFIG.currentSong;

        Song song =
                PianoClient.CONFIG
                        .getCurrentSong();

        /*
         * Rename current song if the text box
         * contains a different name.
         */
        if (!newName.equals(oldName)) {

            PianoClient.CONFIG
                    .songs
                    .remove(oldName);

            song.name = newName;

            PianoClient.CONFIG
                    .songs
                    .put(newName, song);

            PianoClient.CONFIG.currentSong =
                    newName;
        }

        PianoClient.CONFIG.save();

        message =
                "Saved song: " + newName;
    }

    private void loadSong() {

        PianoClient.PLAYER.stop();

        String name =
                songNameBox
                        .getValue()
                        .trim();

        if (!PianoClient.CONFIG
                .songs
                .containsKey(name)) {

            message =
                    "Song not found: " + name;

            return;
        }

        PianoClient.CONFIG.currentSong =
                name;

        PianoClient.CONFIG.save();

        message =
                "Loaded song: " + name;
    }

    private void deleteSong() {

        PianoClient.PLAYER.stop();

        String name =
                songNameBox
                        .getValue()
                        .trim();

        if (!PianoClient.CONFIG
                .songs
                .containsKey(name)) {

            message =
                    "Song not found.";

            return;
        }

        PianoClient.CONFIG
                .songs
                .remove(name);

        if (PianoClient.CONFIG
                .songs
                .isEmpty()) {

            Song replacement =
                    new Song("My Song");

            PianoClient.CONFIG
                    .songs
                    .put(
                            "My Song",
                            replacement
                    );

            PianoClient.CONFIG.currentSong =
                    "My Song";

        } else {

            String nextSong =
                    PianoClient.CONFIG
                            .songs
                            .keySet()
                            .iterator()
                            .next();

            PianoClient.CONFIG.currentSong =
                    nextSong;
        }

        songNameBox.setValue(
                PianoClient.CONFIG.currentSong
        );

        PianoClient.CONFIG.save();

        message =
                "Deleted song: " + name;
    }

    private void addNote() {

        int note;
        double seconds;

        try {

            note =
                    Integer.parseInt(
                            noteBox.getValue()
                    );

            seconds =
                    Double.parseDouble(
                            timeBox.getValue()
                    );

        } catch (NumberFormatException e) {

            message =
                    "Invalid number.";

            return;
        }

        if (note < 1 || note > 24) {

            message =
                    "Note must be 1-24.";

            return;
        }

        if (seconds <= 0) {

            message =
                    "Time must be greater than 0.";

            return;
        }

        long milliseconds =
                Math.round(
                        seconds * 1000.0
                );

        PianoClient.CONFIG
                .getCurrentSong()
                .notes
                .add(
                        new SongNote(
                                note,
                                milliseconds
                        )
                );

        PianoClient.CONFIG.save();

        message =
                "Added #" +
                        note +
                        " for " +
                        seconds +
                        " seconds.";
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {

        renderBackground(
                graphics,
                mouseX,
                mouseY,
                delta
        );

        int centerX = width / 2;

        graphics.drawCenteredString(
                font,
                "Song Editor",
                centerX,
                20,
                0xFFFFFF
        );

        graphics.drawString(
                font,
                "Song Name",
                centerX - 100,
                35,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Note #",
                centerX - 100,
                85,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Seconds",
                centerX + 20,
                85,
                0xAAAAAA
        );

        graphics.drawCenteredString(
                font,
                "Current: " +
                        PianoClient.CONFIG.currentSong,
                centerX,
                310,
                0xFFFF55
        );

        Song song =
                PianoClient.CONFIG
                        .getCurrentSong();

        int y = 335;

        for (int i = 0;
             i < song.notes.size();
             i++) {

            SongNote note =
                    song.notes.get(i);

            graphics.drawString(
                    font,
                    (i + 1) +
                            ".  #" +
                            note.noteBlock +
                            "    " +
                            (note.durationMs / 1000.0) +
                            "s",
                    centerX - 100,
                    y,
                    0xFFFFFF
            );

            y += 12;

            if (y > height - 15) {
                break;
            }
        }

        if (!message.isEmpty()) {

            graphics.drawCenteredString(
                    font,
                    message,
                    centerX,
                    320,
                    0xFFFFFF
            );
        }

        super.render(
                graphics,
                mouseX,
                mouseY,
                delta
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
