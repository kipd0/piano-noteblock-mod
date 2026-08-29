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

        var consumers =
                context.consumers();

        if (consumers == null) {
            return;
        }

        var camera =
                context.camera();

        /*
         * =====================================================
         * FILLED BLOCK COLORS
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
         * CURRENT
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
         * NEXT
         *
         * Blue.
         *
         * If next is the same note,
         * don't put blue over it.
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

        if (matrices == null) {
            return;
        }

        var camera =
                context.camera();

        /*
         * IMPORTANT:
         *
         * Use Minecraft's main text buffer instead
         * of the WorldRenderContext buffer.
         */

        MultiBufferSource.BufferSource textBuffer =
                client.renderBuffers()
                        .bufferSource();

        String text =
                String.valueOf(repeatCount);

        /*
         * Position:
         *
         * Centered horizontally over the block,
         * slightly above the top face.
         */

        double x =
                data.x + 0.5;

        double y =
                data.y + 1.35;

        double z =
                data.z + 0.5;

        matrices.pushPose();

        /*
         * Move from camera to block.
         */

        matrices.translate(
                x - camera.getPosition().x,
                y - camera.getPosition().y,
                z - camera.getPosition().z
        );

        /*
         * Billboard.
         *
         * Always faces the player's screen.
         */

        matrices.mulPose(
                camera.rotation()
        );

        /*
         * BIGGER than before.
         *
         * This should be very obvious while
         * we're testing it.
         */

        float scale =
                0.060F;

        matrices.scale(
                -scale,
                -scale,
                scale
        );

        float textX =
                -font.width(text) /
                        2.0F;

        float textY =
                -font.lineHeight /
                        2.0F;

        /*
         * Pure white.
         *
         * FF alpha
         * FF red
         * FF green
         * FF blue
         */

        int pureWhite =
                0xFFFFFFFF;

        /*
         * Draw a SEE_THROUGH copy.
         *
         * This means terrain cannot hide it.
         */

        font.drawInBatch(
                text,
                textX,
                textY,
                pureWhite,
                false,
                matrices.last().pose(),
                textBuffer,
                Font.DisplayMode.SEE_THROUGH,
                0,
                15728880
        );

        /*
         * Explicitly flush the text buffer.
         *
         * This is the important change.
         */

        textBuffer.endBatch();

        matrices.popPose();
    }
}
