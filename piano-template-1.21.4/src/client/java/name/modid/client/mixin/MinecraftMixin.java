package name.modid.client.mixin;

import name.modid.NoteBlockData;
import name.modid.PianoClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
            method = "startAttack",
            at = @At("HEAD")
    )
    private void piano$onLeftClick(
            CallbackInfoReturnable<Boolean> cir
    ) {

        Minecraft client =
                Minecraft.getInstance();

        if (!PianoClient.PLAYER.isPlaying()) {
            return;
        }

        if (client.player == null) {
            return;
        }

        HitResult hit =
                client.hitResult;

        if (!(hit instanceof BlockHitResult blockHit)) {
            return;
        }

        BlockPos clickedPos =
                blockHit.getBlockPos();

        int clickedNote = -1;

        for (Map.Entry<Integer, NoteBlockData> entry :
                PianoClient.CONFIG.noteBlocks.entrySet()) {

            NoteBlockData data =
                    entry.getValue();

            if (data == null) {
                continue;
            }

            if (data.x == clickedPos.getX() &&
                    data.y == clickedPos.getY() &&
                    data.z == clickedPos.getZ()) {

                clickedNote =
                        entry.getKey();

                break;
            }
        }

        /*
         * DEBUG
         *
         * Shows what the mod detected.
         */
        client.player.displayClientMessage(
                Component.literal(
                        "Piano: clicked=" +
                                clickedNote +
                                " expected=" +
                                PianoClient.PLAYER.getCurrentNote() +
                                " pos=" +
                                clickedPos.getX() + "," +
                                clickedPos.getY() + "," +
                                clickedPos.getZ()
                ),
                false
        );

        if (clickedNote == -1) {
            return;
        }

        PianoClient.PLAYER.clickNote(
                clickedNote
        );
    }
}
