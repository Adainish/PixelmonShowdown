package io.github.adainish.pixelmonshowdown;

import com.pixelmonmod.pixelmon.Pixelmon;
import io.github.adainish.pixelmonshowdown.arenas.ArenaManager;
import io.github.adainish.pixelmonshowdown.commands.ArenaCommand;
import io.github.adainish.pixelmonshowdown.commands.DisplayCommand;
import io.github.adainish.pixelmonshowdown.commands.ShowdownCommand;
import io.github.adainish.pixelmonshowdown.listener.BattleManager;
import io.github.adainish.pixelmonshowdown.queues.QueueManager;
import io.github.adainish.pixelmonshowdown.util.DataManager;
import io.github.adainish.pixelmonshowdown.wrapper.PermissionWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod("pixelmonshowdown")
public class PixelmonShowdown {
    private static PixelmonShowdown instance;
    public static PixelmonShowdown getInstance()
    {
        return instance;
    }
    public final String MOD_NAME = "PixelmonShowdown";
    public final String VERSION = "1.0.0-1.16.5";
    public final String AUTHORS = "LandonJW, Winglet";
    public final String YEAR = "2022";

    public final Logger log = LogManager.getLogger(MOD_NAME);
    public MinecraftServer server;

    public QueueManager queueManager = new QueueManager();
    public ArenaManager arenaManager = new ArenaManager();

    public PermissionWrapper permissionWrapper;

    public File defaultConfigFolder;

    public PixelmonShowdown() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        this.defaultConfigFolder = new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()).toString());
        DataManager.setup();
    }

    @SubscribeEvent
    public void onCommandRegistry(RegisterCommandsEvent event)
    {
        permissionWrapper = new PermissionWrapper();
        event.getDispatcher().register(ShowdownCommand.getCommand());
        event.getDispatcher().register(DisplayCommand.getCommand());
        event.getDispatcher().register(ArenaCommand.getCommand());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        server = ServerLifecycleHooks.getCurrentServer();
        queueManager.loadFromConfig();
        arenaManager.loadArenas();
        DataManager.startAutoSave();
        MinecraftForge.EVENT_BUS.register(new BattleManager());
        Pixelmon.EVENT_BUS.register(new BattleManager());
        log.info("PixelmonShowdown " + VERSION + " Successfully Launched");
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        queueManager.saveAllQueueProfiles();
        DataManager.saveElos();
        DataManager.saveArenas();
    }

    public void reload()
    {
        log.warn("Reloading");
        DataManager.saveElos();
        DataManager.load();
        queueManager.loadFromConfig();
        arenaManager.loadArenas();
    }

}
