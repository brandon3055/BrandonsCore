package com.brandon3055.brandonscore.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout.LayoutFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 28/3/2016.
 * Base class for all containers. Handles syncing on syncable objects inside an attached TileBCBase.
 */
public class ContainerBCTile<T extends TileBCore> extends ContainerBCore<T> {

    /**
     * A reference to the attached tile. This may be null if the container is not attached to a tile
     */
    public T tile;

    public ContainerBCTile(@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        super(type, windowId, playerInv, extraData);
        this.tile = getClientTile(extraData);
    }

    public ContainerBCTile(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, PacketBuffer extraData, LayoutFactory<T> factory) {
        super(type, windowId, player, extraData, factory);
        this.tile = getClientTile(extraData);
        this.buildSlotLayout();
    }

    public ContainerBCTile(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, T tile) {
        super(type, windowId, player);
        this.tile = tile;
        this.tile.onPlayerOpenContainer(player.player);
    }

    public ContainerBCTile(@Nullable ContainerType<?> type, int windowId, PlayerInventory player, T tile, LayoutFactory<T> factory) {
        super(type, windowId, player, factory);
        this.tile = tile;
        this.tile.onPlayerOpenContainer(player.player);
        this.buildSlotLayout();
    }

    @Override
    protected void buildSlotLayout() {
        if (tile != null){
            this.slotLayout = factory.buildLayout(player, tile).retrieveSlotsForContainer(this::addSlot);
        }
    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        tile.onPlayerCloseContainer(playerIn);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        tile.detectAndSendChangesToListeners(containerListeners);
    }

    @Override
    public void addSlotListener(IContainerListener listener) {
        super.addSlotListener(listener);
        if (listener instanceof ServerPlayerEntity) {
            tile.getDataManager().forcePlayerSync((ServerPlayerEntity) listener);
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        if (tile.getLevel().getBlockEntity(tile.getBlockPos()) != tile) {
            return false;
        } else {
            return player.distanceToSqr((double) tile.getBlockPos().getX() + 0.5D, (double) tile.getBlockPos().getY() + 0.5D, (double) tile.getBlockPos().getZ() + 0.5D) <= tile.getAccessDistanceSq();
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int i) {
        int playerSlots = 36;
        if (slotLayout != null) {
            playerSlots = slotLayout.getPlayerSlotCount();
        }
        LazyOptional<IItemHandler> optional = getItemHandler();
        if (optional.isPresent()) {
            IItemHandler handler = optional.orElse(EmptyHandler.INSTANCE);
            Slot slot = getSlot(i);

            if (slot != null && slot.hasItem()) {
                ItemStack stack = slot.getItem();
                ItemStack result = stack.copy();

                //Transferring from tile to player
                if (i >= playerSlots) {
                    if (!moveItemStackTo(stack, 0, playerSlots, false)) {
                        return ItemStack.EMPTY; //Return if failed to merge
                    }
                } else {
                    //Transferring from player to tile
                    if (!moveItemStackTo(stack, playerSlots, playerSlots + handler.getSlots(), false)) {
                        return ItemStack.EMPTY;  //Return if failed to merge
                    }
                }

                if (stack.getCount() == 0) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                slot.onTake(player, stack);

                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    //The following are some safety checks to handle conditions vanilla normally does not have to deal with.

    @Override
    public void setItem(int slotID, ItemStack stack) {
        Slot slot = this.getSlot(slotID);
        if (slot != null) {
            slot.set(stack);
        }
    }

    @Override
    public Slot getSlot(int slotId) {
        if (slotId < slots.size() && slotId >= 0) {
            return slots.get(slotId);
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setAll(List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            Slot slot = getSlot(i);
            if (slot != null) {
                slot.set(stacks.get(i));
            }
        }
    }

    /**
     * @return the item handler for the tile entity.
     */
    public LazyOptional<IItemHandler> getItemHandler() {
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public ContainerSlotLayout getSlotLayout() {
        return slotLayout;
    }

    protected static <T extends TileEntity> T getClientTile(PacketBuffer extraData) {
        return (T) Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos());
    }
}
