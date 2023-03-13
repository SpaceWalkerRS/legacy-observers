package legacy.observers.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import legacy.observers.block.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Shadow private void registerModel(Block block, String id) { }

	@Inject(
		method = "registerGuiModels",
		at = @At(
			value = "TAIL"
		)
	)
	private void registerGuiModels(CallbackInfo ci) {
		registerModel(ModBlocks.OBSERVER, "observer");
	}
}
