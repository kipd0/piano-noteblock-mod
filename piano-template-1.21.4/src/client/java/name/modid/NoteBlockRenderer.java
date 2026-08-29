package name.modid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;

public class NoteBlockRenderer {

    public static void register() {

        WorldRenderEvents.AFTER_ENTITIES.register(
                NoteBlockRenderer::render
        );
    }

    private static void render(
            WorldRenderContext context
    ) {

        if (!PianoClient.PLAYER.isPlaying()) {
            return;
        }

        int currentNote =
                PianoClient.PLAYER
                        .getCurrentNote();

        int nextNote =
                PianoClient.PLAYER
                        .getNextNote();

        int repeatCount =
                PianoClient.PLAYER
                        .getRemainingRepeatCount();

        if (currentNote < 1 ||
                currentNote > 24) {
            return;
        }

        PoseStack matrices =
                context.matrixStack();

        if (matrices == null) {
            return;
        }

        var consumers =
                context.consumers();

        if (consumers == null) {
            return;
        }

        var camera =
                context.camera();

        double camX =
                camera.getPosition().x;

        double camY =
                camera.getPosition().y;

        double camZ =
                camera.getPosition().z;

        /*
         * =====================================================
         * BLOCK COLORS
         * =====================================================
         */

        matrices.pushPose();

        matrices.translate(
                -camX,
                -camY,
                -camZ
        );

        VertexConsumer blockVertices =
                consumers.getBuffer(
                        RenderType.debugFilledBox()
                );

        /*
         * CURRENT NOTE
         *
         * Green.
         */
        renderFilledBlock(
                matrices,
                blockVertices,
                currentNote,

                0.25F,
                1.00F,
                0.35F,

                0.45F
        );

        /*
         * NEXT NOTE
         *
         * Blue.
         *
         * If next note is the same block,
         * don't draw blue over it.
         */
        if (nextNote >= 1 &&
                nextNote <= 24 &&
                nextNote != currentNote) {

            renderFilledBlock(
                    matrices,
                    blockVertices,
                    nextNote,

                    0.20F,
                    0.55F,
                    1.00F,

                    0.30F
            );
        }

        matrices.popPose();

        /*
         * =====================================================
         * REPEAT NUMBER
         * =====================================================
         *
         * Only show 2 or higher.
         */
        if (repeatCount > 1) {

            renderNumber(
                    context,
                    currentNote,
                    repeatCount
            );
        }
    }

    /*
     * =========================================================
     * FILLED BLOCK
     * =========================================================
     */

    private static void renderFilledBlock(
            PoseStack matrices,
            VertexConsumer vertices,
            int noteNumber,
            float red,
            float green,
            float blue,
            float alpha
    ) {

        NoteBlockData data =
                PianoClient.CONFIG
                        .getNoteBlock(noteNumber);

        if (data == null) {
            return;
        }

        BlockPos pos =
                new BlockPos(
                        data.x,
                        data.y,
                        data.z
                );

        double expansion =
                0.002;

        ShapeRenderer.addChainedFilledBoxVertices(
                matrices,
                vertices,

                pos.getX() - expansion,
                pos.getY() - expansion,
                pos.getZ() - expansion,

                pos.getX() + 1 + expansion,
                pos.getY() + 1 + expansion,
                pos.getZ() + 1 + expansion,

                red,
                green,
                blue,
                alpha
        );
    }

    /*
     * =========================================================
     * NUMBER RENDERING
     * =========================================================
     *
     * The number is drawn directly on the
     * side of the block facing the player.
     */

    private static void renderNumber(
            WorldRenderContext context,
            int noteNumber,
            int number
    ) {

        NoteBlockData data =
                PianoClient.CONFIG
                        .getNoteBlock(noteNumber);

        if (data == null) {
            return;
        }

        PoseStack matrices =
                context.matrixStack();

        if (matrices == null) {
            return;
        }

        var consumers =
                context.consumers();

        if (consumers == null) {
            return;
        }

        var camera =
                context.camera();

        double blockX =
                data.x + 0.5;

        double blockY =
                data.y + 0.5;

        double blockZ =
                data.z + 0.5;

        double dx =
                camera.getPosition().x -
                        blockX;

        double dz =
                camera.getPosition().z -
                        blockZ;

        double x;
        double y =
                blockY;
        double z;

        float rotationY;

        /*
         * Pick whichever SIDE face is
         * closest to the player.
         */
        if (Math.abs(dx) >
                Math.abs(dz)) {

            if (dx > 0) {

                /*
                 * EAST FACE
                 */
                x =
                        data.x + 1.006;

                z =
                        data.z + 0.5;

                rotationY =
                        90.0F;

            } else {

                /*
                 * WEST FACE
                 */
                x =
                        data.x - 0.006;

                z =
                        data.z + 0.5;

                rotationY =
                        -90.0F;
            }

        } else {

            if (dz > 0) {

                /*
                 * SOUTH FACE
                 */
                x =
                        data.x + 0.5;

                z =
                        data.z + 1.006;

                rotationY =
                        180.0F;

            } else {

                /*
                 * NORTH FACE
                 */
                x =
                        data.x + 0.5;

                z =
                        data.z - 0.006;

                rotationY =
                        0.0F;
            }
        }

        matrices.pushPose();

        matrices.translate(
                x - camera.getPosition().x,
                y - camera.getPosition().y,
                z - camera.getPosition().z
        );

        /*
         * Rotate flat against the block face.
         */
        matrices.mulPose(
                Axis.YP.rotationDegrees(
                        rotationY
                )
        );

        /*
         * Size of the number.
         */
        float scale =
                0.22F;

        matrices.scale(
                scale,
                scale,
                scale
        );

        /*
         * Flip so number reads correctly.
         */
        matrices.scale(
                -1.0F,
                -1.0F,
                1.0F
        );

        VertexConsumer vertices =
                consumers.getBuffer(
                        RenderType.debugFilledBox()
                );

        String text =
                String.valueOf(number);

        float digitSpacing =
                1.30F;

        float totalWidth =
                text.length() *
                        digitSpacing;

        float startX =
                -(totalWidth / 2.0F) +
                        (digitSpacing / 2.0F);

        /*
         * Center vertically on the block face.
         */
        float startY =
                -0.9F;

        for (int i = 0;
             i < text.length();
             i++) {

            int digit =
                    Character.digit(
                            text.charAt(i),
                            10
                    );

            if (digit < 0) {
                continue;
            }

            float digitX =
                    startX +
                            i * digitSpacing;

            drawDigit(
                    matrices,
                    vertices,
                    digit,
                    digitX,
                    startY
            );
        }

        matrices.popPose();
    }

    /*
     * =========================================================
     * DIGITAL NUMBER
     * =========================================================
     *
     * Seven-segment style:
     *
     *       A
     *     -----
     *    |     |
     *  F |     | B
     *    |  G  |
     *     -----
     *    |     |
     *  E |     | C
     *    |     |
     *     -----
     *       D
     */

    private static void drawDigit(
            PoseStack matrices,
            VertexConsumer vertices,
            int digit,
            float x,
            float y
    ) {

        boolean a = false;
        boolean b = false;
        boolean c = false;
        boolean d = false;
        boolean e = false;
        boolean f = false;
        boolean g = false;

        switch (digit) {

            case 0 -> {
                a = true;
                b = true;
                c = true;
                d = true;
                e = true;
                f = true;
            }

            case 1 -> {
                b = true;
                c = true;
            }

            case 2 -> {
                a = true;
                b = true;
                d = true;
                e = true;
                g = true;
            }

            case 3 -> {
                a = true;
                b = true;
                c = true;
                d = true;
                g = true;
            }

            case 4 -> {
                b = true;
                c = true;
                f = true;
                g = true;
            }

            case 5 -> {
                a = true;
                c = true;
                d = true;
                f = true;
                g = true;
            }

            case 6 -> {
                a = true;
                c = true;
                d = true;
                e = true;
                f = true;
                g = true;
            }

            case 7 -> {
                a = true;
                b = true;
                c = true;
            }

            case 8 -> {
                a = true;
                b = true;
                c = true;
                d = true;
                e = true;
                f = true;
                g = true;
            }

            case 9 -> {
                a = true;
                b = true;
                c = true;
                d = true;
                f = true;
                g = true;
            }
        }

        if (a) {
            horizontalSegment(
                    matrices,
                    vertices,
                    x,
                    y + 1.8F
            );
        }

        if (g) {
            horizontalSegment(
                    matrices,
                    vertices,
                    x,
                    y + 0.9F
            );
        }

        if (d) {
            horizontalSegment(
                    matrices,
                    vertices,
                    x,
                    y
            );
        }

        if (f) {
            verticalSegment(
                    matrices,
                    vertices,
                    x - 0.5F,
                    y + 1.35F
            );
        }

        if (b) {
            verticalSegment(
                    matrices,
                    vertices,
                    x + 0.5F,
                    y + 1.35F
            );
        }

        if (e) {
            verticalSegment(
                    matrices,
                    vertices,
                    x - 0.5F,
                    y + 0.45F
            );
        }

        if (c) {
            verticalSegment(
                    matrices,
                    vertices,
                    x + 0.5F,
                    y + 0.45F
            );
        }
    }

    /*
     * =========================================================
     * WHITE HORIZONTAL BAR
     * =========================================================
     */

    private static void horizontalSegment(
            PoseStack matrices,
            VertexConsumer vertices,
            float x,
            float y
    ) {

        double halfWidth =
                0.48;

        double halfHeight =
                0.10;

        double depth =
                0.025;

        ShapeRenderer.addChainedFilledBoxVertices(
                matrices,
                vertices,

                x - halfWidth,
                y - halfHeight,
                -depth,

                x + halfWidth,
                y + halfHeight,
                depth,

                /*
                 * PURE WHITE
                 * FULL OPACITY
                 */
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }

    /*
     * =========================================================
     * WHITE VERTICAL BAR
     * =========================================================
     */

    private static void verticalSegment(
            PoseStack matrices,
            VertexConsumer vertices,
            float x,
            float y
    ) {

        double halfWidth =
                0.10;

        double halfHeight =
                0.40;

        double depth =
                0.025;

        ShapeRenderer.addChainedFilledBoxVertices(
                matrices,
                vertices,

                x - halfWidth,
                y - halfHeight,
                -depth,

                x + halfWidth,
                y + halfHeight,
                depth,

                /*
                 * PURE WHITE
                 * FULL OPACITY
                 */
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }
}
