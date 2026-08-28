package name.modid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

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

        int number =
                PianoClient.PLAYER
                        .getHighlightedNote();

        if (number < 1 || number > 24) {
            return;
        }

        NoteBlockData data =
                PianoClient.CONFIG
                        .getNoteBlock(number);

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

        Minecraft client =
                Minecraft.getInstance();

        if (client.gameRenderer == null) {
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

        BlockPos pos =
                new BlockPos(
                        data.x,
                        data.y,
                        data.z
                );

        AABB box =
                new AABB(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        pos.getX() + 1,
                        pos.getY() + 1,
                        pos.getZ() + 1
                );

        matrices.pushPose();

        matrices.translate(
                -camX,
                -camY,
                -camZ
        );

        VertexConsumer vertices =
                consumers.getBuffer(
                        RenderType.lines()
                );

        ShapeRenderer.renderLineBox(
                matrices,
                vertices,
                box.inflate(0.05),
                1.0F,
                1.0F,
                0.0F,
                1.0F
        );

        matrices.popPose();
    }
}
