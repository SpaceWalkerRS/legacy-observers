package legacy.observers.block;

import java.util.Random;

import legacy.observers.world.SetBlockFlags;

import net.minecraft.block.Block;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.texture.ISprite;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.Directions;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock extends Block {

	public static final int RENDER_TYPE = 42;

	private static final int FACING_MASK  = 0b0111;
	private static final int POWERED_MASK = 0b1000;

	public static boolean defaultRenderType = false;

	public ISprite sideSprite;
	public ISprite topSprite;
	public ISprite frontSprite;
	public ISprite backSprite;
	public ISprite backLitSprite;

	public ObserverBlock(int id) {
		super(id, Material.STONE);

		setItemGroup(ItemGroup.REDSTONE);
	}

	public Block strength(float strength) {
		return setStrength(strength);
	}

	public ObserverBlock spriteId(String spriteId) {
		return (ObserverBlock)super.setSpriteId(spriteId);
	}

	@Override
	public ISprite getSprite(int face, int metadata) {
		int facing = getFacing(metadata);

		if (face == facing) {
			return frontSprite;
		}
		if (face == Directions.OPPOSITE[facing]) {
			return getPowered(metadata) ? backLitSprite : backSprite;
		}

		if (face < 2) {
			return topSprite;
		}
		if (face > 3) {
			return sideSprite;
		}

		return facing < 2 ? topSprite : sideSprite;
	}

	@Override
	public void loadSprites(SpriteLoader spriteLoader) {
		this.sideSprite = spriteLoader.addSpriteToLoad(getSpriteId() + "_" + "side");
		this.topSprite = spriteLoader.addSpriteToLoad(getSpriteId() + "_" + "top");
		this.frontSprite = spriteLoader.addSpriteToLoad(getSpriteId() + "_" + "front");
		this.backSprite = spriteLoader.addSpriteToLoad(getSpriteId() + "_" + "back");
		this.backLitSprite = spriteLoader.addSpriteToLoad(getSpriteId() + "_" + "back" + "_" + "lit");
	}

	@Override
	public int getRenderType() {
		return defaultRenderType ? super.getRenderType() : RENDER_TYPE;
	}

	@Override
	public void tick(World world, int x, int y, int z, Random random) {
		int metadata = world.getBlockMetadata(x, y, z);

		if (getPowered(metadata)) {
			world.setBlockMetadata(x, y, z, setPowered(metadata, false), SetBlockFlags.UPDATE_CLIENTS);
		} else {
			world.setBlockMetadata(x, y, z, setPowered(metadata, true), SetBlockFlags.UPDATE_CLIENTS);
			world.scheduleTick(x, y, z, rawId, 2);
		}

		updateNeighbors(world, x, y, z, metadata);
	}

	public void update(World world, int x, int y, int z, int neighborBlockId, int neighborX, int neighborY, int neighborZ) {
		if (!world.isClient) {
			int metadata = world.getBlockMetadata(x, y, z);
			int facing = getFacing(metadata);
			int behindX = x + Directions.X_OFFSET[facing];
			int behindY = y + Directions.Y_OFFSET[facing];
			int behindZ = z + Directions.Z_OFFSET[facing];

			if (neighborX == behindX && neighborY == behindY && neighborZ == behindZ) {
				updatePowered(world, x, y, z, metadata);
			}
		}
	}

	private void updatePowered(World world, int x, int y, int z, int metadata) {
		if (!getPowered(metadata)) {
			world.scheduleTick(x, y, z, rawId, 2);
		}
	}

	protected void updateNeighbors(World world, int x, int y, int z, int metadata) {
		int facing = getFacing(metadata);
		int frontX = x - Directions.X_OFFSET[facing];
		int frontY = y - Directions.Y_OFFSET[facing];
		int frontZ = z - Directions.Z_OFFSET[facing];

		world.updateBlock(frontX, frontY, frontZ, rawId);
		world.updateNeighborsExcept(frontX, frontY, frontZ, rawId, facing);
	}

	@Override
	public boolean isPowerSource() {
		return true;
	}

	@Override
	public int getEmittedStrongPower(IWorld world, int x, int y, int z, int dir) {
		return getEmittedWeakPower(world, x, y, z, dir);
	}

	@Override
	public int getEmittedWeakPower(IWorld world, int x, int y, int z, int dir) {
		int metadata = world.getBlockMetadata(x, y, z);
		return getPowered(metadata) && getFacing(metadata) == dir ? 15 : 0;
	}

	@Override
	public void onAdded(World world, int x, int y, int z) {
		if (!world.isClient) {
			int metadata = world.getBlockMetadata(x, y, z);

			if (getPowered(metadata)) {
				tick(world, x, y, z, world.random);
			}

			updatePowered(world, x, y, z, metadata);
		}
	}

	@Override
	public void onRemoved(World world, int x, int y, int z, int blockId, int metadata) {
		if (getPowered(metadata)) {
			updateNeighbors(world, x, y, z, metadata);
		}
	}

	@Override
	public void onPlaced(World world, int x, int y, int z, LivingEntity entity, ItemStack stack) {
		int metadata = world.getBlockMetadata(x, y, z);
		int facing = PistonBaseBlock.getFacingForPlacement(world, x, y, z, entity);

		facing = Directions.OPPOSITE[facing];
		metadata = setFacing(metadata, facing);

		world.setBlockMetadata(x, y, z, metadata, SetBlockFlags.UPDATE_CLIENTS);
	}

	public static int getFacing(int metadata) {
		return metadata & FACING_MASK;
	}

	public static int setFacing(int metadata, int facing) {
		return (metadata & ~FACING_MASK) | (facing & FACING_MASK);
	}

	public static boolean getPowered(int metadata) {
		return (metadata & POWERED_MASK) != 0;
	}

	public static int setPowered(int metadata, boolean powered) {
		return (metadata & ~POWERED_MASK) | (powered ? POWERED_MASK : 0);
	}
}
