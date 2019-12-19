package com.brandon3055.brandonscore;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.brandonscore.integration.ModHelperBC;
import com.brandon3055.brandonscore.network.BCoreNetwork;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

/**
 * Created by Brandon on 14/5/2015.
 */
public class CommonProxy {

    public void commonSetup(FMLCommonSetupEvent event) {
        BCoreNetwork.init();
        //Switched to annotation
//        MinecraftForge.EVENT_BUS.register(new BCEventHandler());
//        MinecraftForge.EVENT_BUS.register(new ModFeatureParser());
        ModHelperBC.init();
        CapabilityOP.register();
    }

    public void clientSetup(FMLClientSetupEvent event) {}

    public void serverSetup(FMLDedicatedServerSetupEvent event) {

    }

//    public MinecraftServer getMCServer() {
//        return FMLCommonHandler.instance().getMinecraftServerInstance();
//    }

    public World getClientWorld() {
        return null;
    }

//    public boolean isOp(String paramString) {
//        MinecraftServer localMinecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
//        paramString = paramString.trim();
//        for (String str : localMinecraftServer.getPlayerList().getOppedPlayerNames()) {
//            if (paramString.equalsIgnoreCase(str)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean isJumpKeyDown() {
        return false;
    }

    public boolean isSprintKeyDown() {
        return false;
    }

    public boolean isSneakKeyDown() {
        return false;
    }

    public boolean isCTRLKeyDown() {
        return false;
    }

    public PlayerEntity getClientPlayer() {
        return null;
    }

    public void setChatAtIndex(ITextComponent chat, int index) {

    }

    public void addProcess(IProcess iProcess) {
        ProcessHandler.addProcess(iProcess);
    }

//    public void registerModFeatures(String modid) {
//        ModFeatureParser.registerMod(modid);
//    }

    public void runSidedProcess(IProcess process) {
        ProcessHandler.addProcess(process);
    }

    public void resetEffectRenderer(World world) {

    }

    public IAnimationStateMachine loadASM(ResourceLocation location, ImmutableMap<String, ITimeValue> customParameters) {
        return null;
    }
}
