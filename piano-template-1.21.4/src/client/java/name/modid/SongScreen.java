package name.modid;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongScreen extends Screen {

    private EditBox songNameBox;
    private EditBox noteBox;
    private EditBox timeBox;
    private EditBox entryBox;

    private String message = "";

    /*
     * Song selected from the list.
     * Selecting does NOT immediately load it.
     * Press "Load Selected".
     */
    private String selectedSongName = null;

    /*
     * Song-list page.
     * 5 songs are displayed at once.
     */
    private int songPage = 0;

    private static final int SONGS_PER_PAGE = 5;

    private final List<Button> songButtons =
            new ArrayList<>();

    private Button previousSongPageButton;
    private Button nextSongPageButton;

    public SongScreen() {

        super(
                Component.literal(
                        "Song Editor"
                )
        );
    }

    @Override
    protected void init() {

        int centerX = width / 2;

        /*
         * If nothing is selected yet,
         * select the currently loaded song.
         */
        if (selectedSongName == null) {

            selectedSongName =
                    PianoClient.CONFIG.currentSong;
        }

        /*
         * ==========================
         * SONG NAME
         * ==========================
         */

        songNameBox =
                new EditBox(
                        font,
                        centerX - 100,
                        45,
                        200,
                        20,
                        Component.literal(
                                "Song Name"
                        )
                );

        songNameBox.setValue(
                PianoClient.CONFIG.currentSong
        );

        addRenderableWidget(
                songNameBox
        );

        /*
         * ==========================
         * NEW / SAVE
         * ==========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "New Song"
                        ),
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
                        Component.literal(
                                "Save / Rename"
                        ),
                        button -> saveSong()
                ).bounds(
                        centerX + 5,
                        70,
                        95,
                        20
                ).build()
        );

        /*
         * ==========================
         * NOTE INPUT
         * ==========================
         */

        entryBox =
                new EditBox(
                        font,
                        centerX - 100,
                        120,
                        55,
                        20,
                        Component.literal(
                                "Entry"
                        )
                );

        noteBox =
                new EditBox(
                        font,
                        centerX - 30,
                        120,
                        55,
                        20,
                        Component.literal(
                                "Note"
                        )
                );

        timeBox =
                new EditBox(
                        font,
                        centerX + 40,
                        120,
                        60,
                        20,
                        Component.literal(
                                "Time"
                        )
                );

        entryBox.setValue("1");
        noteBox.setValue("1");
        timeBox.setValue("0.5");

        addRenderableWidget(
                entryBox
        );

        addRenderableWidget(
                noteBox
        );

        addRenderableWidget(
                timeBox
        );

        /*
         * ==========================
         * ADD NOTE
         * ==========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Add Note"
                        ),
                        button -> addNote()
                ).bounds(
                        centerX - 100,
                        145,
                        200,
                        20
                ).build()
        );

        /*
         * ==========================
         * LOAD / UPDATE ENTRY
         * ==========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Load Entry"
                        ),
                        button -> loadEntry()
                ).bounds(
                        centerX - 100,
                        170,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Update Entry"
                        ),
                        button -> updateEntry()
                ).bounds(
                        centerX + 5,
                        170,
                        95,
                        20
                ).build()
        );

        /*
         * ==========================
         * DELETE ENTRY
         * ==========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Delete Entry"
                        ),
                        button -> deleteEntry()
                ).bounds(
                        centerX - 100,
                        195,
                        200,
                        20
                ).build()
        );

        /*
         * ==========================
         * PLAY / STOP
         * ==========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Play"
                        ),
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
                        Component.literal(
                                "Stop"
                        ),
                        button ->
                                PianoClient.PLAYER.stop()
                ).bounds(
                        centerX + 5,
                        220,
                        95,
                        20
                ).build()
        );

        /*
         * ==========================
         * CLEAR SONG
         * ==========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Clear Current Song"
                        ),
                        button -> clearSong()
                ).bounds(
                        centerX - 100,
                        245,
                        200,
                        20
                ).build()
        );

        /*
         * ==========================
         * DONE
         * ==========================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Done"
                        ),
                        button -> onClose()
                ).bounds(
                        centerX - 100,
                        270,
                        200,
                        20
                ).build()
        );

        /*
         * ==========================
         * SAVED SONG LIST
         * ==========================
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
                            Component.literal(
                                    "-"
                            ),
                            button ->
                                    selectSongSlot(
                                            slot
                                    )
                    ).bounds(
                            listX,
                            listY + (i * 25),
                            150,
                            20
                    ).build();

            songButtons.add(
                    songButton
            );

            addRenderableWidget(
                    songButton
            );
        }

        /*
         * Previous page
         */

        previousSongPageButton =
                Button.builder(
                        Component.literal(
                                "<"
                        ),
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

        /*
         * Next page
         */

        nextSongPageButton =
                Button.builder(
                        Component.literal(
                                ">"
                        ),
                        button -> {

                            int pages =
                                    getSongPageCount();

                            if (songPage <
                                    pages - 1) {

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

        /*
         * ==========================
         * LOAD SELECTED SONG
         * ==========================
         */

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

        /*
         * ==========================
         * DELETE SELECTED SONG
         * ==========================
         */

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
     * ============================================================
     * SONG LIST
     * ============================================================
     */

    private List<String> getSongNames() {

        List<String> names =
                new ArrayList<>(
                        PianoClient.CONFIG
                                .songs
                                .keySet()
                );

        /*
         * Keep the list in alphabetical order.
         */
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

            songPage =
                    pageCount - 1;
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

                /*
                 * Add markers:
                 *
                 * > = selected
                 * * = currently loaded
                 */

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
                        Component.literal(
                                "-"
                        )
                );

                button.active = false;
            }
        }

        previousSongPageButton.active =
                songPage > 0;

        nextSongPageButton.active =
                songPage <
                        pageCount - 1;
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

        /*
         * Put selected song's name
         * into the name box too.
         */
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

        message =
                "Loaded: " +
                        selectedSongName;

        refreshSongList();
    }

    /*
     * ============================================================
     * NEW SONG
     * ============================================================
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

        movePageToSong(
                name
        );

        message =
                "Created: " + name;

        refreshSongList();
    }

    /*
     * ============================================================
     * SAVE / RENAME SONG
     * ============================================================
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

        /*
         * If we're renaming, don't accidentally
         * overwrite another saved song.
         */
        if (!newName.equals(oldName)) {

            if (PianoClient.CONFIG
                    .songs
                    .containsKey(newName)) {

                message =
                        "A song with that name already exists.";

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

        movePageToSong(
                newName
        );

        message =
                "Saved: " +
                        newName;

        refreshSongList();
    }

    /*
     * ============================================================
     * DELETE SELECTED SONG
     * ============================================================
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

        /*
         * There must always be at least one song.
         */
        if (PianoClient.CONFIG
                .songs
                .isEmpty()) {

            Song replacement =
                    new Song(
                            "My Song"
                    );

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

    /*
     * Put the list on the page containing
     * the specified song.
     */
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
     * ============================================================
     * ADD NOTE
     * ============================================================
     */

    private void addNote() {

        Integer note =
                readNote();

        if (note == null) {
            return;
        }

        Long milliseconds =
                readTime();

        if (milliseconds == null) {
            return;
        }

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
                        note +
                        " for " +
                        milliseconds / 1000.0 +
                        "s";
    }

    /*
     * ============================================================
     * LOAD EXISTING ENTRY
     * ============================================================
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

        timeBox.setValue(
                String.valueOf(
                        note.durationMs
                                / 1000.0
                )
        );

        message =
                "Loaded entry " +
                        (index + 1);
    }

    /*
     * ============================================================
     * UPDATE EXISTING ENTRY
     * ============================================================
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

        Long milliseconds =
                readTime();

        if (milliseconds == null) {
            return;
        }

        SongNote existing =
                PianoClient.CONFIG
                        .getCurrentSong()
                        .notes
                        .get(index);

        existing.noteBlock =
                note;

        existing.durationMs =
                milliseconds;

        PianoClient.CONFIG.save();

        message =
                "Updated entry " +
                        (index + 1) +
                        " to #" +
                        note +
                        " / " +
                        milliseconds / 1000.0 +
                        "s";
    }

    /*
     * ============================================================
     * DELETE ENTRY
     * ============================================================
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
     * ============================================================
     * CLEAR CURRENT SONG
     * ============================================================
     */

    private void clearSong() {

        PianoClient.PLAYER.stop();

        PianoClient.CONFIG
                .getCurrentSong()
                .notes
                .clear();

        PianoClient.CONFIG.save();

        message =
                "Current song cleared.";
    }

    /*
     * ============================================================
     * INPUT HELPERS
     * ============================================================
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
                entry >
                        song.notes.size()) {

            message =
                    "Entry must be 1-" +
                            song.notes.size();

            return null;
        }

        /*
         * Screen uses entry #1.
         * Java List uses index 0.
         */
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

    private Long readTime() {

        double seconds;

        try {

            seconds =
                    Double.parseDouble(
                            timeBox
                                    .getValue()
                    );

        } catch (NumberFormatException e) {

            message =
                    "Invalid time.";

            return null;
        }

        if (seconds <= 0) {

            message =
                    "Time must be greater than 0.";

            return null;
        }

        return Math.round(
                seconds *
                        1000.0
        );
    }

    /*
     * ============================================================
     * RENDER
     * ============================================================
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

        /*
         * Title
         */
        graphics.drawCenteredString(
                font,
                "Song Editor",
                centerX,
                15,
                0xFFFFFF
        );

        /*
         * Song list title
         */
        graphics.drawString(
                font,
                "Saved Songs",
                listX,
                40,
                0xFFFF55
        );

        /*
         * Page number
         */
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

        /*
         * Markers explanation
         */
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

        /*
         * Input labels
         */
        graphics.drawString(
                font,
                "Song Name",
                centerX - 100,
                32,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Entry",
                centerX - 100,
                105,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Note #",
                centerX - 30,
                105,
                0xAAAAAA
        );

        graphics.drawString(
                font,
                "Seconds",
                centerX + 40,
                105,
                0xAAAAAA
        );

        /*
         * Current song
         */
        graphics.drawCenteredString(
                font,
                "Current: " +
                        PianoClient.CONFIG
                                .currentSong,
                centerX,
                300,
                0xFFFF55
        );

        /*
         * Current song's notes
         */
        Song song =
                PianoClient.CONFIG
                        .getCurrentSong();

        int y = 325;

        for (int i = 0;
             i < song.notes.size();
             i++) {

            SongNote note =
                    song.notes.get(i);

            int color =
                    0xFFFFFF;

            /*
             * Show the currently entered
             * entry number in yellow.
             */
            try {

                int selectedEntry =
                        Integer.parseInt(
                                entryBox
                                        .getValue()
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
                            note.noteBlock +
                            "    " +
                            (note.durationMs
                                    / 1000.0) +
                            "s",
                    centerX - 100,
                    y,
                    color
            );

            y += 12;

            if (y >
                    height - 15) {

                break;
            }
        }

        /*
         * Status/error message
         */
        if (!message.isEmpty()) {

            graphics.drawCenteredString(
                    font,
                    message,
                    centerX,
                    290,
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
