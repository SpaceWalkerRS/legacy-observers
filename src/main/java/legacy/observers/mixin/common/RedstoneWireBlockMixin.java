package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import legacy.observers.block.ModBlocks;
import legacy.observers.block.ObserverBlock;

import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.Direction;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {

	@Inject(
		method = "shouldConnectTo(Lnet/minecraft/block/state/BlockState;Lnet/minecraft/util/math/Direction;)Z",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static void shouldConnectTo(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() == ModBlocks.OBSERVER) {
			cir.setReturnValue(dir == state.get(ObserverBlock.FACING));
		}
	}
}
