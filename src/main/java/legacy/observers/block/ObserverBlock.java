package legacy.observers.block;

import java.util.Random;

import legacy.observers.world.SetBlockFlags;

import net.minecraft.block.Block;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.CreativeModeTab;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock extends Block {

	public static final DirectionProperty FACING = DirectionProperty.of("facing");
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");

	public ObserverBlock() {
		super(Material.STONE);

		setDefaultState(stateDefinition.any().
			set(FACING, Direction.SOUTH).
			set(POWERED, false));
		setCreativeModeTab(CreativeModeTab.REDSTONE);
	}

	public Block strength(float strength) {
		return setStrength(strength);
	}

	@Override
	protected StateDefinition createStateDefinition() {
		return new StateDefinition(this, FACING, POWERED);
	}

	@Override
	public void tick(World world, BlockPos pos, BlockState state, Random random) {
		if (state.get(POWERED)) {
			world.setBlockState(pos, state.set(POWERED, false), SetBlockFlags.UPDATE_CLIENTS);
		} else {
			world.setBlockState(pos, state.set(POWERED, true), SetBlockFlags.UPDATE_CLIENTS);
			world.scheduleTick(pos, this, 2);
		}

		updateNeighbors(world, pos, state);
	}

	public void update(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
		if (!world.isClient && pos.offset(state.get(FACING)).equals(neighborPos)) {
			update(state, world, pos);
		}
	}

	private void update(BlockState state, World world, BlockPos pos) {
		if (!state.get(POWERED)) {
			world.scheduleTick(pos, this, 2);
		}
	}

	protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
		Direction facing = state.get(FACING);
		BlockPos behind = pos.offset(facing.getOpposite());

		world.updateBlock(behind, this);
		world.updateNeighborsExcept(behind, this, facing);
	}

	@Override
	public boolean isPowerSource(BlockState state) {
		return true;
	}

	@Override
	public int getEmittedStrongPower(BlockState state, IWorld world, BlockPos pos, Direction dir) {
		return state.getEmittedWeakPower(world, pos, dir);
	}

	@Override
	public int getEmittedWeakPower(BlockState state, IWorld world, BlockPos pos, Direction dir) {
		return state.get(POWERED) && state.get(FACING) == dir ? 15 : 0;
	}

	@Override
	public void onAdded(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			if (state.get(POWERED)) {
				tick(world, pos, state, world.random);
			}

			update(state, world, pos);
		}
	}

	@Override
	public void onRemoved(World world, BlockPos pos, BlockState state) {
		if (state.get(POWERED)) {
			updateNeighbors(world, pos, state.set(POWERED, false));
		}
	}

	@Override
	public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
		return defaultState().set(FACING, PistonBaseBlock.getFacingForPlacement(pos, entity).getOpposite());
	}

	@Override
	public int getMetadataFromState(BlockState state) {
		int metadata = state.get(FACING).getId();
		if (state.get(POWERED))
			metadata |= 8;

		return metadata;
	}

	@Override
	public BlockState getStateFromMetadata(int metadata) {
		return defaultState().set(FACING, Direction.byId(metadata & 7));
	}
}
