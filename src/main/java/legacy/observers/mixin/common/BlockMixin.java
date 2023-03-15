package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import legacy.observers.block.ModBlocks;

import net.minecraft.block.Block;

@Mixin(Block.class)
public class BlockMixin {

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "FIELD",
			ordinal = 0,
			target = "Lnet/minecraft/item/Item;BY_ID:[Lnet/minecraft/item/Item;"
		)
	)
	private static void init(CallbackInfo ci) {
		ModBlocks.init();
	}
}
