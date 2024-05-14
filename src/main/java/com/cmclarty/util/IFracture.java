package com.cmclarty.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IFracture {
    default ActionResult useOnBlock(ItemUsageContext context) {
        return null;
    }

    default Optional<BlockState> tryFracture(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
        return Optional.empty();
    }

    default Optional<BlockState> getFracturedState(BlockState state) {
        return Optional.empty();
    }
}
