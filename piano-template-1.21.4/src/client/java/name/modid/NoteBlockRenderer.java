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

        /*
         * =====================================================
         * BLOCK OVERLAYS
         * =====================================================
         */

        matrices.pushPose();

        matrices.translate(
                -camera.getPosition().x,
                -camera.getPosition().y,
                -camera.getPosition().z
        );

        VertexConsumer vertices =
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
                vertices,
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
         * Don't show blue when the next
         * note is the same block.
         */
        if (nextNote >= 1 &&
                nextNote <= 24 &&
                nextNote != currentNote) {

            renderFilledBlock(
                    matrices,
                    vertices,
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
         * Example:
         *
         * 6,6,6,6,4
         *
         * 4 -> 3 -> 2 -> no number
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

        /*
         * Tiny expansion prevents the colored
         * overlay from fighting with the block.
         */
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
     * REPEAT NUMBER
     * =========================================================
     *
     * Draws the white number directly in
     * front of the block face closest to
     * the player.
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

        /*
         * Center of the block.
         */
        double blockX =
                data.x + 0.5;

        double blockY =
                data.y + 0.5;

        double blockZ =
                data.z + 0.5;

        /*
         * Find where the player is relative
         * to the block.
         */
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
         * =====================================================
         * PICK THE FACE FACING THE PLAYER
         * =====================================================
         */

        if (Math.abs(dx) >
                Math.abs(dz)) {

            if (dx > 0) {

                /*
                 * EAST FACE
                 *
                 * Number is pushed 0.03 blocks
                 * away from the surface.
                 */
                x =
                        data.x + 1.03;

                z =
                        data.z + 0.5;

                rotationY =
                        90.0F;

            } else {

                /*
                 * WEST FACE
                 */
                x =
                        data.x - 0.03;

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
                        data.z + 1.03;

                rotationY =
                        180.0F;

            } else {

                /*
                 * NORTH FACE
                 */
                x =
                        data.x + 0.5;

                z =
                        data.z - 0.03;

                rotationY =
                        0.0F;
            }
        }

        matrices.pushPose();

        /*
         * Move the number to the selected face.
         */
        matrices.translate(
                x - camera.getPosition().x,
                y - camera.getPosition().y,
                z - camera.getPosition().z
        );

        /*
         * Make the number lie flat against
         * that face.
         */
        matrices.mulPose(
                Axis.YP.rotationDegrees(
                        rotationY
                )
        );

        /*
         * Number size.
         */
        float scale =
                0.22F;

        /*
         * Negative X fixes the mirrored digits.
         *
         * Positive Y keeps them upright.
         */
        matrices.scale(
                -scale,
                scale,
                scale
        );

        VertexConsumer vertices =
                consumers.getBuffer(
                        RenderType.debugFilledBox()
                );

        String text =
                String.valueOf(number);

        /*
         * Supports numbers bigger than 9 too.
         */
        float digitSpacing =
                1.30F;

        float totalWidth =
                text.length() *
                        digitSpacing;

        float startX =
                -(totalWidth / 2.0F) +
                        (digitSpacing / 2.0F);

        /*
         * Vertically center the digits
         * on the block.
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
     * SEVEN SEGMENT DIGITS
     * =========================================================
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
                g = true;
                e = true;
                d = true;
            }

            case 3 -> {
                a = true;
                b = true;
                c = true;
                d = true;
                g = true;
            }

            case 4 -> {
                f = true;
                g = true;
                b = true;
                c = true;
            }

            case 5 -> {
                a = true;
                f = true;
                g = true;
                c = true;
                d = true;
            }

            case 6 -> {
                a = true;
                f = true;
                g = true;
                e = true;
                c = true;
                d = true;
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

        /*
         * TOP
         */
        if (a) {
            horizontalSegment(
                    matrices,
                    vertices,
                    x,
                    y + 1.8F
            );
        }

        /*
         * MIDDLE
         */
        if (g) {
            horizontalSegment(
                    matrices,
                    vertices,
                    x,
                    y + 0.9F
            );
        }

        /*
         * BOTTOM
         */
        if (d) {
            horizontalSegment(
                    matrices,
                    vertices,
                    x,
                    y
            );
        }

        /*
         * TOP LEFT
         */
        if (f) {
            verticalSegment(
                    matrices,
                    vertices,
                    x - 0.5F,
                    y + 1.35F
            );
        }

        /*
         * TOP RIGHT
         */
        if (b) {
            verticalSegment(
                    matrices,
                    vertices,
                    x + 0.5F,
                    y + 1.35F
            );
        }

        /*
         * BOTTOM LEFT
         */
        if (e) {
            verticalSegment(
                    matrices,
                    vertices,
                    x - 0.5F,
                    y + 0.45F
            );
        }

        /*
         * BOTTOM RIGHT
         */
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
     * HORIZONTAL NUMBER SEGMENT
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

        /*
         * Very thin.
         *
         * This prevents the number from
         * clipping into the note block.
         */
        double depth =
                0.005;

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
     * VERTICAL NUMBER SEGMENT
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

        /*
         * Same thin depth as the
         * horizontal pieces.
         */
        double depth =
                0.005;

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
