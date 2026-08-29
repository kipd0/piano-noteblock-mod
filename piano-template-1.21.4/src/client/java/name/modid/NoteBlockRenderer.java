package name.modid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
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

        MultiBufferSource consumers =
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

        VertexConsumer vertices =
                consumers.getBuffer(
                        RenderType.debugFilledBox()
                );

        /*
         * CURRENT BLOCK
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
         * NEXT BLOCK
         *
         * Blue.
         *
         * Don't draw blue when the
         * next note is the same block.
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
         */

        if (repeatCount > 1) {

            renderRepeatNumber(
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
     * REPEAT NUMBER
     * =========================================================
     *
     * Example:
     *
     * 6,6,6,6,4
     *
     * First click:
     *      4
     *
     * Then:
     *      3
     *
     * Then:
     *      2
     *
     * Final #6 has no number.
     */

    private static void renderRepeatNumber(
            WorldRenderContext context,
            int noteNumber,
            int repeatCount
    ) {

        NoteBlockData data =
                PianoClient.CONFIG
                        .getNoteBlock(noteNumber);

        if (data == null) {
            return;
        }

        Minecraft client =
                Minecraft.getInstance();

        Font font =
                client.font;

        PoseStack matrices =
                context.matrixStack();

        MultiBufferSource consumers =
                context.consumers();

        if (matrices == null ||
                consumers == null) {
            return;
        }

        var camera =
                context.camera();

        /*
         * Put the number ABOVE the block.
         *
         * Previously it was inside the block,
         * so Minecraft's depth rendering
         * could hide it completely.
         */

        double x =
                data.x + 0.5;

        double y =
                data.y + 1.20;

        double z =
                data.z + 0.5;

        String text =
                String.valueOf(repeatCount);

        matrices.pushPose();

        matrices.translate(
                x - camera.getPosition().x,
                y - camera.getPosition().y,
                z - camera.getPosition().z
        );

        /*
         * Always face the player.
         */

        matrices.mulPose(
                camera.rotation()
        );

        /*
         * Make it large enough
         * to clearly read.
         */

        float scale =
                0.045F;

        matrices.scale(
                -scale,
                -scale,
                scale
        );

        float textX =
                -font.width(text) / 2.0F;

        float textY =
                -font.lineHeight / 2.0F;

        /*
         * PURE WHITE
         *
         * Full opacity.
         */

        int pureWhite =
                0xFFFFFFFF;

        font.drawInBatch(
                text,
                textX,
                textY,
                pureWhite,

                false,

                matrices
                        .last()
                        .pose(),

                consumers,

                /*
                 * Important:
                 *
                 * SEE_THROUGH prevents the
                 * block from hiding the number.
                 */
                Font.DisplayMode.SEE_THROUGH,

                0,

                15728880
        );

        matrices.popPose();
    }
}
