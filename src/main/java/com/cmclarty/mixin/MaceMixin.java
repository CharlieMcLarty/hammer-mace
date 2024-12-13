package com.cmclarty.mixin;

import com.cmclarty.util.IFracture;
import com.google.common.collect.ImmutableMap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Map;
import java.util.Optional;

@Mixin(MaceItem.class)
public abstract class MaceMixin extends Item implements IFracture {
	private static final Map<Block, Block> FRACTURE_BLOCKS = new ImmutableMap.Builder<Block, Block>()
			.put(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS)
			.put(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS)
			.put(Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES)
			.put(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS)
			.put(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS).build();

	public MaceMixin(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity;
		BlockPos blockPos;
		World world = context.getWorld();
		Optional<BlockState> optional = this.tryFracture(world, blockPos = context.getBlockPos(), playerEntity = context.getPlayer(), world.getBlockState(blockPos));
		if (optional.isEmpty()) {
			return ActionResult.PASS;
		}
		ItemStack itemStack = context.getStack();
		if (playerEntity instanceof ServerPlayerEntity) {
			Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
		}
		world.setBlockState(blockPos, optional.get(), Block.NOTIFY_ALL_AND_REDRAW);
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, optional.get()));
		if (playerEntity != null) {
			itemStack.damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public Optional<BlockState> tryFracture(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
		Optional<BlockState> optional = this.getFracturedState(state);
		if (optional.isPresent()) {
			world.playSound(player, pos, SoundEvents.BLOCK_ANCIENT_DEBRIS_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
			return optional;
		}
		return Optional.empty();
	}

	@Override
	public Optional<BlockState> getFracturedState(BlockState state) {
		return Optional.ofNullable(FRACTURE_BLOCKS.get(state.getBlock())).map(block -> (BlockState)block.getDefaultState());
	}
}