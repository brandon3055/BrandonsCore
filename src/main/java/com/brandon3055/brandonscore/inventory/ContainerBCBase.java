package com.brandon3055.brandonscore.inventory;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.inventory.ContainerSlotLayout.LayoutFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

/**
 * Created by brandon3055 on 28/3/2016.
 * Base class for all containers. Handles syncing on syncable objects inside an attached TileBCBase.
 */
public class ContainerBCBase<T extends TileBCore> extends Container {

    /**
     * A reference to the attached tile. This may be null if the container is not attached to a tile
     */
    public T tile;
    protected PlayerEntity player;
    protected ContainerSlotLayout slotLayout;

// I want there to ALWAYS be a player
//    public ContainerBCBase() {
//    }
//
//    public ContainerBCBase(T tile) {
//        this.tile = tile;
//    }
//
//    public ContainerBCBase(T tile, LayoutFactory<T> factory) {
//        this(tile);
//        this.slotLayout = factory.buildLayout(null, tile).retrieveSlotsForContainer(this::addSlotToContainer);
//    }

    public ContainerBCBase(PlayerEntity player) {
        this.player = player;
    }

    public ContainerBCBase(PlayerEntity player, T tile) {
        this(player);
        this.tile = tile;
        this.tile.onPlayerOpenContainer(player);
    }

    public ContainerBCBase(PlayerEntity player, T tile, LayoutFactory<T> factory) {
        this(player, tile);
        this.slotLayout = factory.buildLayout(player, tile).retrieveSlotsForContainer(this::addSlotToContainer);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if (tile != null) {
            tile.onPlayerCloseContainer(playerIn);
        }
    }

    public ContainerBCBase addPlayerSlots(int posX, int posY) {
        return addPlayerSlots(posX, posY, 4);
    }

    public ContainerBCBase addPlayerSlots(int posX, int posY, int hotbarSpacing) {
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotCheckValid.IInv(player.inventory, x, posX + 18 * x, posY + 54 + hotbarSpacing));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotCheckValid.IInv(player.inventory, x + y * 9 + 9, posX + 18 * x, posY + y * 18));
            }
        }
        return this;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        tile.detectAndSendChangesToListeners(listeners);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        if (listener instanceof ServerPlayerEntity && tile != null) {
            tile.getDataManager().forcePlayerSync((ServerPlayerEntity) listener);
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        if (tile.getWorld().getTileEntity(tile.getPos()) != tile) {
            return false;
        }
        else {
            return player.getDistanceSq((double) tile.getPos().getX() + 0.5D, (double) tile.getPos().getY() + 0.5D, (double) tile.getPos().getZ() + 0.5D) <= 64.0D;
        }
    }


    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int i) {
        IItemHandler handler = getItemHandler();
        if (handler != null) {
            Slot slot = getSlot(i);

            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                ItemStack result = stack.copy();

                //Transferring from tile to player
                if (i >= 36) {
                    if (!mergeItemStack(stack, 0, 36, false)) {
                        return ItemStack.EMPTY; //Return if failed to merge
                    }
                }
                else {
                    //Transferring from player to tile
                    if (!mergeItemStack(stack, 36, 36 + handler.getSlots(), false)) {
                        return ItemStack.EMPTY;  //Return if failed to merge
                    }
                }

                if (stack.getCount() == 0) {
                    slot.putStack(ItemStack.EMPTY);
                }
                else {
                    slot.onSlotChanged();
                }

                slot.onTake(player, stack);

                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    //The following are some safety checks to handle conditions vanilla normally does not have to deal with.

    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {
        Slot slot = this.getSlot(slotID);
        if (slot != null) {
            slot.putStack(stack);
        }
    }

    @Override
    public Slot getSlot(int slotId) {
        if (slotId < inventorySlots.size() && slotId >= 0) {
            return inventorySlots.get(slotId);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setAll(List<ItemStack> stacks) {
        for (int i = 0; i < stacks.size(); ++i) {
            Slot slot = getSlot(i);
            if (slot != null) {
                slot.putStack(stacks.get(i));
            }
        }
    }

    /**
     * @return the item handler for the tile entity.
     */
    public IItemHandler getItemHandler() {
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public ContainerSlotLayout getSlotLayout() {
        return slotLayout;
    }
}
