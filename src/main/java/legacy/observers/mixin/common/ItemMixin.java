package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import legacy.observers.block.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

@Mixin(Item.class)
public class ItemMixin {

	@Shadow private static void register(Block block) { }

	@Inject(
		method = "init",
		at = @At(
			value = "TAIL"
		)
	)
	private static void init(CallbackInfo ci) {
		register(ModBlocks.OBSERVER);
	}
}
