package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import legacy.observers.world.ModGamerules;
import legacy.observers.world.ModWorld;

import net.minecraft.block.Block;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.World;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

	@Inject(
		method = "neighborChanged",
		at = @At(
			value = "FIELD",
			ordinal = 1,
			shift = Shift.AFTER,
			target = "Lnet/minecraft/block/entity/NoteBlockBlockEntity;powered:Z"
		)
	)
	private void triggerObservers(BlockState state, World world, BlockPos pos, Block neighborBlock, CallbackInfo ci) {
		triggerObservers(world, pos);
	}

	@Inject(
		method = "use",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/block/entity/NoteBlockBlockEntity;tunePitch()V"
		)
	)
	private void triggerObservers(World world, BlockPos pos, BlockState state, PlayerEntity player, InteractionHand hand, ItemStack stack, Direction face, float dx, float dy, float dz, CallbackInfoReturnable<Boolean> cir) {
		triggerObservers(world, pos);
	}

	private void triggerObservers(World world, BlockPos pos) {
		if (ModGamerules.noteBlocksTriggerObservers) {
			((ModWorld)world).updateObservers(pos, (Block)(Object)this);
		}
	}
}
