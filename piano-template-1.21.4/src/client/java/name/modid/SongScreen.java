package name.modid;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongScreen extends Screen {

    private EditBox songNameBox;
    private EditBox importBox;
    private EditBox entryBox;
    private EditBox noteBox;

    private String message = "";

    private String selectedSongName = null;

    private int songPage = 0;

    private static final int SONGS_PER_PAGE = 5;

    private final List<Button> songButtons =
            new ArrayList<>();

    private Button previousSongPageButton;
    private Button nextSongPageButton;

    public SongScreen() {
        super(Component.literal("Song Editor"));
    }

    @Override
    protected void init() {

        int centerX = width / 2;

        if (selectedSongName == null) {
            selectedSongName =
                    PianoClient.CONFIG.currentSong;
        }

        /*
         * =========================
         * SONG NAME
         * =========================
         */

        songNameBox =
                new EditBox(
                        font,
                        centerX - 100,
                        45,
                        200,
                        20,
                        Component.literal("Song Name")
                );

        songNameBox.setValue(
                PianoClient.CONFIG.currentSong
        );

        addRenderableWidget(songNameBox);

        addRenderableWidget(
                Button.builder(
                        Component.literal("New Song"),
                        button -> newSong()
                ).bounds(
                        centerX - 100,
                        70,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Save / Rename"),
                        button -> saveSong()
                ).bounds(
                        centerX + 5,
                        70,
                        95,
                        20
                ).build()
        );

        /*
         * =========================
         * IMPORT SONG
         * =========================
         */

        importBox =
                new EditBox(
                        font,
                        centerX - 100,
                        110,
                        200,
                        20,
                        Component.literal("Import Song")
                );

        importBox.setMaxLength(10000);

        addRenderableWidget(importBox);

        addRenderableWidget(
                Button.builder(
                        Component.literal("Import Song"),
                        button -> importSong()
                ).bounds(
                        centerX - 100,
                        135,
                        200,
                        20
                ).build()
        );

        /*
         * =========================
         * NOTE EDITOR
         * =========================
         */

        entryBox =
                new EditBox(
                        font,
                        centerX - 100,
                        185,
                        80,
                        20,
                        Component.literal("Entry")
                );

        noteBox =
                new EditBox(
                        font,
                        centerX + 20,
                        185,
                        80,
                        20,
                        Component.literal("Note")
                );

        entryBox.setValue("1");
        noteBox.setValue("1");

        addRenderableWidget(entryBox);
        addRenderableWidget(noteBox);

        addRenderableWidget(
                Button.builder(
                        Component.literal("Add Note"),
                        button -> addNote()
                ).bounds(
                        centerX - 100,
                        210,
                        200,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Load Entry"),
                        button -> loadEntry()
                ).bounds(
                        centerX - 100,
                        235,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Update Entry"),
                        button -> updateEntry()
                ).bounds(
                        centerX + 5,
                        235,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Delete Entry"),
                        button -> deleteEntry()
                ).bounds(
                        centerX - 100,
                        260,
                        200,
                        20
                ).build()
        );

        /*
         * =========================
         * PLAY / STOP
         * =========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal("Play"),
                        button ->
                                PianoClient.PLAYER.play(
                                        PianoClient.CONFIG
                                                .getCurrentSong()
                                )
                ).bounds(
                        centerX - 100,
                        285,
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
                        285,
                        95,
                        20
                ).build()
        );

        /*
         * =========================
         * CLEAR SONG
         * =========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Clear Current Song"
                        ),
                        button -> clearSong()
                ).bounds(
                        centerX - 100,
                        310,
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
                        335,
                        200,
                        20
                ).build()
        );

        /*
         * =========================
         * HUD POSITION SLIDERS
         * =========================
         */

        addRenderableWidget(
                new AbstractSliderButton(
                        centerX - 100,
                        365,
                        95,
                        20,
                        Component.literal(
                                "HUD X: " +
                                        (int) (
                                                PianoClient.CONFIG.hudX *
                                                        100
                                        ) +
                                        "%"
                        ),
                        PianoClient.CONFIG.hudX
                ) {

                    @Override
                    protected void updateMessage() {

                        setMessage(
                                Component.literal(
                                        "HUD X: " +
                                                (int) (
                                                        value *
                                                                100
                                                ) +
                                                "%"
                                )
                        );
                    }

                    @Override
                    protected void applyValue() {

                        PianoClient.CONFIG.hudX =
                                value;

                        PianoClient.CONFIG.save();
                    }
                }
        );

        addRenderableWidget(
                new AbstractSliderButton(
                        centerX + 5,
                        365,
                        95,
                        20,
                        Component.literal(
                                "HUD Y: " +
                                        (int) (
                                                PianoClient.CONFIG.hudY *
                                                        100
                                        ) +
                                        "%"
                        ),
                        PianoClient.CONFIG.hudY
                ) {

                    @Override
                    protected void updateMessage() {

                        setMessage(
                                Component.literal(
                                        "HUD Y: " +
                                                (int) (
                                                        value *
                                                                100
                                                ) +
                                                "%"
                                )
                        );
                    }

                    @Override
                    protected void applyValue() {

                        PianoClient.CONFIG.hudY =
                                value;

                        PianoClient.CONFIG.save();
                    }
                }
        );

        /*
         * =========================
         * SAVED SONG LIST
         * =========================
         */

        int listX =
                centerX - 300;

        int listY = 60;

        songButtons.clear();

        for (int i = 0;
             i < SONGS_PER_PAGE;
             i++) {

            final int slot = i;

            Button songButton =
                    Button.builder(
                            Component.literal("-"),
                            button ->
                                    selectSongSlot(slot)
                    ).bounds(
                            listX,
                            listY + (i * 25),
                            150,
                            20
                    ).build();

            songButtons.add(songButton);

            addRenderableWidget(songButton);
        }

        previousSongPageButton =
                Button.builder(
                        Component.literal("<"),
                        button -> {

                            if (songPage > 0) {
                                songPage--;
                                refreshSongList();
                            }

                        }
                ).bounds(
                        listX,
                        listY + 130,
                        45,
                        20
                ).build();

        addRenderableWidget(
                previousSongPageButton
        );

        nextSongPageButton =
                Button.builder(
                        Component.literal(">"),
                        button -> {

                            int pages =
                                    getSongPageCount();

                            if (songPage < pages - 1) {
                                songPage++;
                                refreshSongList();
                            }

                        }
                ).bounds(
                        listX + 105,
                        listY + 130,
                        45,
                        20
                ).build();

        addRenderableWidget(
                nextSongPageButton
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Load Selected"
                        ),
                        button ->
                                loadSelectedSong()
                ).bounds(
                        listX,
                        listY + 160,
                        150,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Delete Selected"
                        ),
                        button ->
                                deleteSelectedSong()
                ).bounds(
                        listX,
                        listY + 185,
                        150,
                        20
                ).build()
        );

        refreshSongList();
    }

    /*
     * =========================================================
     * IMPORT SONG
     * =========================================================
     *
     * New format:
     *
     * 4,6,7,9,6,6,4
     */

    private void importSong() {

        PianoClient.PLAYER.stop();

        String text =
                importBox
                        .getValue()
                        .trim();

        if (text.isEmpty()) {

            message =
                    "Paste a song first.";

            return;
        }

        String[] entries =
                text.split(",");

        List<SongNote> importedNotes =
                new ArrayList<>();

        for (int i = 0;
             i < entries.length;
             i++) {

            String entry =
                    entries[i].trim();

            if (entry.isEmpty()) {
                continue;
            }

            int note;

            try {

                note =
                        Integer.parseInt(
                                entry
                        );

            } catch (NumberFormatException e) {

                message =
                        "Bad note at entry " +
                                (i + 1);

                return;
            }

            if (note < 1 ||
                    note > 24) {

                message =
                        "Note must be 1-24 at entry " +
                                (i + 1);

                return;
            }

            importedNotes.add(
                    new SongNote(note)
            );
        }

        if (importedNotes.isEmpty()) {

            message =
                    "No notes found.";

            return;
        }

        Song currentSong =
                PianoClient.CONFIG
                        .getCurrentSong();

        currentSong.notes.clear();

        currentSong.notes.addAll(
                importedNotes
        );

        PianoClient.CONFIG.save();

        entryBox.setValue("1");

        message =
                "Imported " +
                        importedNotes.size() +
                        " notes.";
    }

    /*
     * =========================================================
     * SONG LIST
     * =========================================================
     */

    private List<String> getSongNames() {

        List<String> names =
                new ArrayList<>(
                        PianoClient.CONFIG
                                .songs
                                .keySet()
                );

        Collections.sort(
                names,
                String.CASE_INSENSITIVE_ORDER
        );

        return names;
    }

    private int getSongPageCount() {

        int count =
                getSongNames().size();

        return Math.max(
                1,
                (count + SONGS_PER_PAGE - 1)
                        / SONGS_PER_PAGE
        );
    }

    private void refreshSongList() {

        List<String> names =
                getSongNames();

        int pageCount =
                getSongPageCount();

        if (songPage >= pageCount) {
            songPage = pageCount - 1;
        }

        if (songPage < 0) {
            songPage = 0;
        }

        int start =
                songPage *
                        SONGS_PER_PAGE;

        for (int i = 0;
             i < songButtons.size();
             i++) {

            Button button =
                    songButtons.get(i);

            int index =
                    start + i;

            if (index < names.size()) {

                String name =
                        names.get(index);

                String prefix = "";

                if (name.equals(
                        selectedSongName)) {

                    prefix += "> ";
                }

                if (name.equals(
                        PianoClient.CONFIG
                                .currentSong)) {

                    prefix += "* ";
                }

                button.setMessage(
                        Component.literal(
                                prefix + name
                        )
                );

                button.active = true;

            } else {

                button.setMessage(
                        Component.literal("-")
                );

                button.active = false;
            }
        }

        previousSongPageButton.active =
                songPage > 0;

        nextSongPageButton.active =
                songPage < pageCount - 1;
    }

    private void selectSongSlot(
            int slot
    ) {

        List<String> names =
                getSongNames();

        int index =
                songPage *
                        SONGS_PER_PAGE +
                        slot;

        if (index < 0 ||
                index >= names.size()) {

            return;
        }

        selectedSongName =
                names.get(index);

        songNameBox.setValue(
                selectedSongName
        );

        message =
                "Selected: " +
                        selectedSongName;

        refreshSongList();
    }

    private void loadSelectedSong() {

        PianoClient.PLAYER.stop();

        if (selectedSongName == null) {

            message =
                    "Select a song first.";

            return;
        }

        if (!PianoClient.CONFIG
                .songs
                .containsKey(
                        selectedSongName
                )) {

            message =
                    "Song no longer exists.";

            return;
        }

        PianoClient.CONFIG.currentSong =
                selectedSongName;

        songNameBox.setValue(
                selectedSongName
        );

        PianoClient.CONFIG.save();

        entryBox.setValue("1");

        message =
                "Loaded: " +
                        selectedSongName;

        refreshSongList();
    }

    /*
     * =========================================================
     * NEW SONG
     * =========================================================
     */

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
                .put(
                        name,
                        song
                );

        PianoClient.CONFIG.currentSong =
                name;

        selectedSongName =
                name;

        PianoClient.CONFIG.save();

        movePageToSong(name);

        message =
                "Created: " + name;

        refreshSongList();
    }

    /*
     * =========================================================
     * SAVE / RENAME
     * =========================================================
     */

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
                PianoClient.CONFIG
                        .currentSong;

        Song song =
                PianoClient.CONFIG
                        .getCurrentSong();

        if (!newName.equals(oldName)) {

            if (PianoClient.CONFIG
                    .songs
                    .containsKey(newName)) {

                message =
                        "That song already exists.";

                return;
            }

            PianoClient.CONFIG
                    .songs
                    .remove(oldName);

            song.name =
                    newName;

            PianoClient.CONFIG
                    .songs
                    .put(
                            newName,
                            song
                    );

            PianoClient.CONFIG.currentSong =
                    newName;

            selectedSongName =
                    newName;
        }

        PianoClient.CONFIG.save();

        movePageToSong(newName);

        message =
                "Saved: " +
                        newName;

        refreshSongList();
    }

    /*
     * =========================================================
     * DELETE SONG
     * =========================================================
     */

    private void deleteSelectedSong() {

        PianoClient.PLAYER.stop();

        if (selectedSongName == null) {

            message =
                    "Select a song first.";

            return;
        }

        if (!PianoClient.CONFIG
                .songs
                .containsKey(
                        selectedSongName
                )) {

            message =
                    "Song not found.";

            return;
        }

        String deletedName =
                selectedSongName;

        boolean deletingCurrent =
                deletedName.equals(
                        PianoClient.CONFIG
                                .currentSong
                );

        PianoClient.CONFIG
                .songs
                .remove(
                        deletedName
                );

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

            selectedSongName =
                    "My Song";

        } else {

            List<String> remaining =
                    getSongNames();

            selectedSongName =
                    remaining.get(0);

            if (deletingCurrent) {

                PianoClient.CONFIG.currentSong =
                        selectedSongName;
            }
        }

        songNameBox.setValue(
                PianoClient.CONFIG
                        .currentSong
        );

        PianoClient.CONFIG.save();

        songPage = 0;

        message =
                "Deleted: " +
                        deletedName;

        refreshSongList();
    }

    private void movePageToSong(
            String name
    ) {

        List<String> names =
                getSongNames();

        int index =
                names.indexOf(name);

        if (index >= 0) {

            songPage =
                    index /
                            SONGS_PER_PAGE;
        }
    }

    /*
     * =========================================================
     * ADD NOTE
     * =========================================================
     */

    private void addNote() {

        Integer note =
                readNote();

        if (note == null) {
            return;
        }

        PianoClient.CONFIG
                .getCurrentSong()
                .notes
                .add(
                        new SongNote(note)
                );

        PianoClient.CONFIG.save();

        int newEntry =
                PianoClient.CONFIG
                        .getCurrentSong()
                        .notes
                        .size();

        entryBox.setValue(
                String.valueOf(
                        newEntry
                )
        );

        message =
                "Added entry " +
                        newEntry +
                        ": #" +
                        note;
    }

    /*
     * =========================================================
     * LOAD ENTRY
     * =========================================================
     */

    private void loadEntry() {

        Integer index =
                readEntryIndex();

        if (index == null) {
            return;
        }

        SongNote note =
                PianoClient.CONFIG
                        .getCurrentSong()
                        .notes
                        .get(index);

        noteBox.setValue(
                String.valueOf(
                        note.noteBlock
                )
        );

        message =
                "Loaded entry " +
                        (index + 1);
    }

    /*
     * =========================================================
     * UPDATE ENTRY
     * =========================================================
     */

    private void updateEntry() {

        Integer index =
                readEntryIndex();

        if (index == null) {
            return;
        }

        Integer note =
                readNote();

        if (note == null) {
            return;
        }

        SongNote existing =
                PianoClient.CONFIG
                        .getCurrentSong()
                        .notes
                        .get(index);

        existing.noteBlock =
                note;

        PianoClient.CONFIG.save();

        message =
                "Updated entry " +
                        (index + 1) +
                        " to #" +
                        note;
    }

    /*
     * =========================================================
     * DELETE ENTRY
     * =========================================================
     */

    private void deleteEntry() {

        Integer index =
                readEntryIndex();

        if (index == null) {
            return;
        }

        PianoClient.PLAYER.stop();

        PianoClient.CONFIG
                .getCurrentSong()
                .notes
                .remove(
                        (int) index
                );

        PianoClient.CONFIG.save();

        message =
                "Deleted entry " +
                        (index + 1);
    }

    /*
     * =========================================================
     * CLEAR SONG
     * =========================================================
     */

    private void clearSong() {

        PianoClient.PLAYER.stop();

        String currentName =
                PianoClient.CONFIG.currentSong;

        Song currentSong =
                PianoClient.CONFIG
                        .songs
                        .get(currentName);

        if (currentSong == null) {

            message =
                    "Current song not found.";

            return;
        }

        currentSong.notes.clear();

        PianoClient.CONFIG.save();

        message =
                "Cleared: " +
                        currentName;
    }

    /*
     * =========================================================
     * INPUT HELPERS
     * =========================================================
     */

    private Integer readEntryIndex() {

        int entry;

        try {

            entry =
                    Integer.parseInt(
                            entryBox
                                    .getValue()
                    );

        } catch (NumberFormatException e) {

            message =
                    "Invalid entry number.";

            return null;
        }

        Song song =
                PianoClient.CONFIG
                        .getCurrentSong();

        if (entry < 1 ||
                entry > song.notes.size()) {

            message =
                    "Entry must be 1-" +
                            song.notes.size();

            return null;
        }

        return entry - 1;
    }

    private Integer readNote() {

        int note;

        try {

            note =
                    Integer.parseInt(
                            noteBox
                                    .getValue()
                    );

        } catch (NumberFormatException e) {

            message =
                    "Invalid note number.";

            return null;
        }

        if (note < 1 ||
                note > 24) {

            message =
                    "Note must be 1-24.";

            return null;
        }

        return note;
    }

    /*
     * =========================================================
     * RENDER
     * =========================================================
     */

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

        int centerX =
                width / 2;

        int listX =
                centerX - 300;

        graphics.drawCenteredString(
                font,
                "Song Editor",
                centerX,
                15,
                0xFFFFFF
        );

        graphics.drawString(
                font,
                "Saved Songs",
                listX,
                40,
                0xFFFF55
        );

        graphics.drawString(
                font,
                "Song Name",
                centerX - 100,
                32,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Import: 4,6,7,9,6,6,4",
                centerX - 100,
                97,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Entry",
                centerX - 100,
                172,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Note #",
                centerX + 20,
                172,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Page " +
                        (songPage + 1) +
                        "/" +
                        getSongPageCount(),
                listX + 50,
                195,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "> selected",
                listX,
                270,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "* loaded",
                listX,
                282,
                0xAAAAAA
        );

        graphics.drawCenteredString(
                font,
                "HUD Position",
                centerX,
                352,
                0xAAAAAA
        );

        graphics.drawCenteredString(
                font,
                "Current: " +
                        PianoClient.CONFIG
                                .currentSong,
                centerX,
                395,
                0xFFFF55
        );

        if (!message.isEmpty()) {

            graphics.drawCenteredString(
                    font,
                    message,
                    centerX,
                    410,
                    0xFFFFFF
            );
        }

        Song song =
                PianoClient.CONFIG
                        .getCurrentSong();

        int y = 430;

        for (int i = 0;
             i < song.notes.size();
             i++) {

            SongNote note =
                    song.notes.get(i);

            int color =
                    0xFFFFFF;

            try {

                int selectedEntry =
                        Integer.parseInt(
                                entryBox.getValue()
                        );

                if (selectedEntry ==
                        i + 1) {

                    color =
                            0xFFFF55;
                }

            } catch (
                    NumberFormatException ignored
            ) {
            }

            graphics.drawString(
                    font,
                    (i + 1) +
                            ".  #" +
                            note.noteBlock,
                    centerX - 100,
                    y,
                    color
            );

            y += 12;

            if (y > height - 15) {
                break;
            }
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
