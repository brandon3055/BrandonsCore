package com.brandon3055.brandonscore.utils;

/**
 * Created by brandon3055 on 24/3/2016.
 * Some general block related helper functions.
 */
public class BlockHelper {

//	public void setBlock(BlockPos pos, )


    /**
     * This method returns the proper name of the block at the given position if it can be found.
     * This method is for client side use only.
     * @return the blocks name.
     */
//    @On(Side.CLIENT)
//    public static String getBlockName(BlockPos pos, World world) {
//        BlockState state = world.getBlockState(pos);
//        ItemStack stack;
//
//        try {
//            RayTraceResult result = new RayTraceResult(RayTraceResult.Type.BLOCK, Vec3d.ZERO, Direction.UP, pos);
//            stack = state.getBlock().getPickBlock(state, result, world, pos, Minecraft.getInstance().player);
//        }
//        catch (Throwable ignored) {
//            stack = state.getBlock().getItem(world, pos, state);
//        }
//
//        String name = "Unknown";
//
//        if (!stack.isEmpty()) {
//            return I18n.format(stack.getUnlocalizedName() + ".name");
//        }
//
//        return name;
//    }
}
