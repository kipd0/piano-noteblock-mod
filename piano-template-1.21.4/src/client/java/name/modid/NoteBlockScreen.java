package name.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteBlockScreen extends Screen {

    private EditBox numberBox;
    private EditBox layoutNameBox;

    private String message = "";

    public NoteBlockScreen() {

        super(
                Component.literal(
                        "Note Block Manager"
                )
        );
    }

    @Override
    protected void init() {

        int centerX =
                width / 2;

        /*
         * =====================================================
         * NOTE NUMBER
         * =====================================================
         */

        numberBox =
                new EditBox(
                        font,
                        centerX - 50,
                        70,
                        100,
                        20,
                        Component.literal(
                                "Number"
                        )
                );

        numberBox.setValue("0");

        addRenderableWidget(
                numberBox
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Assign Looking-At Block"
                        ),
                        button -> assign()
                ).bounds(
                        centerX - 100,
                        100,
                        200,
                        20
                ).build()
        );

        /*
         * =====================================================
         * LAYOUT SELECTOR
         * =====================================================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "< Previous Layout"
                        ),
                        button ->
                                previousLayout()
                ).bounds(
                        centerX - 100,
                        140,
                        95,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Next Layout >"
                        ),
                        button ->
                                nextLayout()
                ).bounds(
                        centerX + 5,
                        140,
                        95,
                        20
                ).build()
        );

        /*
         * =====================================================
         * NEW LAYOUT NAME
         * =====================================================
         */

        layoutNameBox =
                new EditBox(
                        font,
                        centerX - 100,
                        175,
                        200,
                        20,
                        Component.literal(
                                "Layout Name"
                        )
                );

        layoutNameBox.setHint(
                Component.literal(
                        "New layout name..."
                )
        );

        addRenderableWidget(
                layoutNameBox
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Create New Layout"
                        ),
                        button ->
                                createLayout()
                ).bounds(
                        centerX - 100,
                        205,
                        200,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Delete Current Layout"
                        ),
                        button ->
                                deleteLayout()
                ).bounds(
                        centerX - 100,
                        235,
                        200,
                        20
                ).build()
        );

        /*
         * =====================================================
         * OTHER SCREENS
         * =====================================================
         */

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Song Editor"
                        ),
                        button -> {

                            Minecraft
                                    .getInstance()
                                    .setScreen(
                                            new SongScreen()
                                    );
                        }
                ).bounds(
                        centerX - 100,
                        275,
                        200,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Done"
                        ),
                        button ->
                                onClose()
                ).bounds(
                        centerX - 100,
                        305,
                        200,
                        20
                ).build()
        );
    }

    /*
     * =========================================================
     * ASSIGN BLOCK
     * =========================================================
     */

    private void assign() {

        Minecraft client =
                Minecraft.getInstance();

        if (!(client.hitResult instanceof
                net.minecraft.world.phys.BlockHitResult hit)) {

            message =
                    "You are not looking at a block.";

            return;
        }

        BlockPos pos =
                hit.getBlockPos();

        if (client.level == null) {
            return;
        }

        if (!client.level
                .getBlockState(pos)
                .is(Blocks.NOTE_BLOCK)) {

            message =
                    "That is not a note block.";

            return;
        }

        int number;

        try {

            number =
                    Integer.parseInt(
                            numberBox
                                    .getValue()
                                    .trim()
                    );

        } catch (NumberFormatException e) {

            message =
                    "Enter a number from 0 to 24.";

            return;
        }

        /*
         * Minecraft note blocks have
         * 25 pitch values:
         *
         * 0 through 24.
         */
        if (number < 0 ||
                number > 24) {

            message =
                    "Number must be 0-24.";

            return;
        }

        PianoClient.CONFIG
                .setNoteBlock(
                        number,
                        new NoteBlockData(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ()
                        )
                );

        message =
                "Saved #" +
                        number +
                        " to " +
                        PianoClient.CONFIG.currentLayout;
    }

    /*
     * =========================================================
     * CREATE LAYOUT
     * =========================================================
     */

    private void createLayout() {

        String name =
                layoutNameBox
                        .getValue()
                        .trim();

        if (name.isEmpty()) {

            message =
                    "Enter a layout name.";

            return;
        }

        if (PianoClient.CONFIG
                .layouts
                .containsKey(name)) {

            message =
                    "That layout already exists.";

            return;
        }

        PianoClient.CONFIG
                .createLayout(name);

        layoutNameBox.setValue("");

        message =
                "Created layout: " +
                        name;
    }

    /*
     * =========================================================
     * DELETE LAYOUT
     * =========================================================
     */

    private void deleteLayout() {

        if (PianoClient.CONFIG
                .layouts
                .size() <= 1) {

            message =
                    "You must keep at least one layout.";

            return;
        }

        String oldName =
                PianoClient.CONFIG
                        .currentLayout;

        PianoClient.CONFIG
                .deleteLayout(
                        oldName
                );

        message =
                "Deleted layout: " +
                        oldName;
    }

    /*
     * =========================================================
     * PREVIOUS LAYOUT
     * =========================================================
     */

    private void previousLayout() {

        List<String> names =
                getLayoutNames();

        if (names.isEmpty()) {
            return;
        }

        int index =
                names.indexOf(
                        PianoClient.CONFIG
                                .currentLayout
                );

        if (index < 0) {
            index = 0;
        }

        index--;

        if (index < 0) {
            index =
                    names.size() - 1;
        }

        PianoClient.CONFIG
                .selectLayout(
                        names.get(index)
                );

        message =
                "Loaded: " +
                        PianoClient.CONFIG
                                .currentLayout;
    }

    /*
     * =========================================================
     * NEXT LAYOUT
     * =========================================================
     */

    private void nextLayout() {

        List<String> names =
                getLayoutNames();

        if (names.isEmpty()) {
            return;
        }

        int index =
                names.indexOf(
                        PianoClient.CONFIG
                                .currentLayout
                );

        if (index < 0) {
            index = 0;
        }

        index++;

        if (index >= names.size()) {
            index = 0;
        }

        PianoClient.CONFIG
                .selectLayout(
                        names.get(index)
                );

        message =
                "Loaded: " +
                        PianoClient.CONFIG
                                .currentLayout;
    }

    /*
     * Alphabetical ordering makes Previous/Next
     * predictable every time the game starts.
     */
    private List<String> getLayoutNames() {

        List<String> names =
                new ArrayList<>(
                        PianoClient.CONFIG
                                .layouts
                                .keySet()
                );

        Collections.sort(
                names,
                String.CASE_INSENSITIVE_ORDER
        );

        return names;
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

        graphics.drawCenteredString(
                font,
                "Note Block Manager",
                width / 2,
                20,
                0xFFFFFF
        );

        graphics.drawCenteredString(
                font,
                "Current Layout: " +
                        PianoClient.CONFIG
                                .currentLayout,
                width / 2,
                35,
                0x55FF55
        );

        NoteBlockLayout layout =
                PianoClient.CONFIG
                        .getCurrentLayout();

        int assigned =
                layout.noteBlocks.size();

        graphics.drawCenteredString(
                font,
                "Assigned: " +
                        assigned +
                        " / 25",
                width / 2,
                48,
                0xAAAAAA
        );

        graphics.drawCenteredString(
                font,
                "Enter 0-24, then look at a note block.",
                width / 2,
                58,
                0xAAAAAA
        );

        if (!message.isEmpty()) {

            graphics.drawCenteredString(
                    font,
                    message,
                    width / 2,
                    340,
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
