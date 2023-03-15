package legacy.observers.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import legacy.observers.block.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.crafting.CraftingManager;
import net.minecraft.crafting.recipe.ShapedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(CraftingManager.class)
public class CraftingManagerMixin {

	@Shadow private ShapedRecipe registerShaped(ItemStack result, Object... args) { return null; }

	@Inject(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Collections;sort(Ljava/util/List;Ljava/util/Comparator;)V"
		)
	)
	private void init(CallbackInfo ci) {
		registerShaped(new ItemStack(ModBlocks.OBSERVER, 1), "###", "RRQ", "###", '#', Block.COBBLESTONE, 'R', Item.REDSTONE, 'Q', Item.QUARTZ);
	}
}
