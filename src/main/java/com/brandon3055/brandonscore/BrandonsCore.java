package com.brandon3055.brandonscore;

import com.brandon3055.brandonscore.client.ClientProxy;
import com.brandon3055.brandonscore.config.BCConfig;
import com.brandon3055.brandonscore.utils.LogHelperBC;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Mod(modid = BrandonsCore.MODID,
//        version = BrandonsCore.VERSION,
//        name = BrandonsCore.MODNAME,
//        guiFactory = "com.brandon3055.brandonscore.BCGuiFactory",
//        dependencies = "required-after:codechickenlib@[" + CodeChickenLib.MOD_VERSION + ",);required-after:redstoneflux;")
@Mod(BrandonsCore.MODID)
@EventBusSubscriber(modid = BrandonsCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BrandonsCore {
    public static final String MODNAME = "Brandon's Core";
    public static final String MODID = "brandonscore";
    public static final String VERSION = "${mod_version}";
    public static final String NET_CHANNEL = "BCPCChannel";
    public static CommonProxy proxy;

    public BrandonsCore() {
        Logger deLog = LogManager.getLogger("draconicevolution");
        LogHelperBC.info("Brandon's Core online! Waiting for Draconic Evolution to join the party....");
        if (ModList.get().isLoaded("draconicevolution")) {
            deLog.log(Level.INFO, "Draconic Evolution online!");
            LogHelperBC.info("Hay! There you are! Now lets destroy some worlds!!");
            deLog.log(Level.INFO, "Sounds like fun! Lets get to it!");
        }
        else {
            deLog.log(Level.INFO, "...");
            LogHelperBC.info("Aww... Im sad now...");
        }

        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
//        FMLJavaModLoadingContext.get().getModEventBus().register(this);
//        FMLJavaModLoadingContext.get().getModEventBus().register(new BCConfig());

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, BCConfig.CLIENT_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.SERVER, BCConfig.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, BCConfig.COMMON_SPEC);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.commonSetup(event);

//        ModList.get().getModContainerById(MODID).get().addConfig();
//        ModList.get().getAllScanData()
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        proxy.clientSetup(event);
    }

    @SubscribeEvent
    public void onServerSetup(FMLDedicatedServerSetupEvent event) {

        proxy.serverSetup(event);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
//        event.
    }

//
//    @Mod.EventHandler
//    public void serverStart(FMLServerStartingEvent event) {
//        event.registerServerCommand(new CommandTickTime());
//        event.registerServerCommand(new BCUtilCommands());
//        event.registerServerCommand(new CommandTPX());
//    }
//
//    @Mod.EventHandler
//    public void serverStop(FMLServerStoppingEvent event) {
//        ProcessHandler.clearHandler();
//    }
//
//    @Mod.EventHandler
//    public void preInit(FMLPreInitializationEvent event) {
//        FileHandler.init(event);
//        ModFeatureParser.parseASMData(event.getAsmData());
//        ModConfigParser.parseASMData(event.getAsmData());
//        ModConfigParser.loadConfigs(event);
//        proxy.preInit(event);
//        ProcessHandler.init();
//        proxy.registerPacketHandlers();
//    }

//    public void registerNetwork() {
//        network = NetworkRegistry.INSTANCE.newSimpleChannel("BCoreNet");
//        network.registerMessage(PacketSpawnParticle.Handler.class, PacketSpawnParticle.class, 0, Side.CLIENT);
//        network.registerMessage(PacketUpdateMount.Handler.class, PacketUpdateMount.class, 1, Side.CLIENT);
//        network.registerMessage(PacketUpdateMount.Handler.class, PacketUpdateMount.class, 2, Side.SERVER);
//        network.registerMessage(PacketTickTime.Handler.class, PacketTickTime.class, 3, Side.CLIENT);
//        network.registerMessage(PacketContributor.Handler.class, PacketContributor.class, 4, Side.CLIENT);
//        network.registerMessage(PacketContributor.Handler.class, PacketContributor.class, 5, Side.SERVER);
//    }
}

