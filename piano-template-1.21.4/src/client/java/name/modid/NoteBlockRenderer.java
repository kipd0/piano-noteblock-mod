package name.modid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
                PianoClient.PLAYER.getCurrentNote();

        int nextNote =
                PianoClient.PLAYER.getNextNote();

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
         * BLOCK OVERLAYS
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
         * If next is the SAME block,
         * don't draw the blue overlay.
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
         * This does NOT use Minecraft's font renderer.
         *
         * It draws the number from small white rectangles,
         * so it uses the same world-rendering method that
         * already works for your colored blocks.
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
         * We draw the number floating just
         * above the block.
         */

        double x =
                data.x + 0.5;

        double y =
                data.y + 1.15;

        double z =
                data.z + 0.5;

        matrices.pushPose();

        matrices.translate(
                x - camera.getPosition().x,
                y - camera.getPosition().y,
                z - camera.getPosition().z
        );

        /*
         * Make the digital number always
         * face the camera.
         */

        Quaternionf rotation =
                new Quaternionf(
                        camera.rotation()
                );

        matrices.mulPose(rotation);

        /*
         * Width/height of one digit.
         */

        float scale =
                0.18F;

        matrices.scale(
                scale,
                scale,
                scale
        );

        /*
         * Flip it so it reads correctly
         * toward the player.
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
                    0.0F
            );
        }

        matrices.popPose();
    }

    /*
     * =========================================================
     * DIGITAL DIGIT
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

        /*
         * PURE WHITE
         *
         * Every rectangle uses:
         *
         * R = 1
         * G = 1
         * B = 1
         * A = 1
         */

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
     * HORIZONTAL WHITE BAR
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
                0.035;

        ShapeRenderer.addChainedFilledBoxVertices(
                matrices,
                vertices,

                x - halfWidth,
                y - halfHeight,
                -depth,

                x + halfWidth,
                y + halfHeight,
                depth,

                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }

    /*
     * =========================================================
     * VERTICAL WHITE BAR
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
                0.035;

        ShapeRenderer.addChainedFilledBoxVertices(
                matrices,
                vertices,

                x - halfWidth,
                y - halfHeight,
                -depth,

                x + halfWidth,
                y + halfHeight,
                depth,

                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }
}
