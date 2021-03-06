package com.brandon3055.brandonscore.lib;

import com.brandon3055.brandonscore.blocks.TileBCBase;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created by brandon3055 on 23/10/2016.
 * A simple implementation of EntityFilter for use by tile entities.
 * Remember to call EntityFilter.receiveConfigFromClient on the filter packetID in TileBCBase.receivePacketFromClient
 */
public class TileEntityFilter extends EntityFilter {

    public byte packetID;
    private TileBCBase tile;
    public boolean isListEnabled;
    public boolean isTypeSelectionEnabled;
    public boolean isOtherSelectorEnabled;

    public TileEntityFilter(TileBCBase tile, byte packetID) {
        this.tile = tile;
        this.packetID = packetID;
    }

    @Override
    public void sendConfigToServer(CompoundNBT compound) {
        tile.sendPacketToServer(output -> output.writeCompoundNBT(compound), packetID);
    }

    @Override
    public void receiveConfigFromClient(CompoundNBT compound) {
        super.receiveConfigFromClient(compound);
        tile.updateBlock();
        tile.setChanged();
    }

    @Override
    public boolean isListEnabled() {
        return isListEnabled;
    }

    @Override
    public boolean isTypeSelectionEnabled() {
        return isTypeSelectionEnabled;
    }

    @Override
    public boolean isOtherSelectorEnabled() {
        return isOtherSelectorEnabled;
    }
}
