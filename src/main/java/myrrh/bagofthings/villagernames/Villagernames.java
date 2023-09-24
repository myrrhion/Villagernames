package myrrh.bagofthings.villagernames;

import myrrh.bagofthings.villagernames.config.DefaultNames;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class Villagernames implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Villagernames.class);
    public static HashMap<String,ArrayList<String>> NAME_LIST = new HashMap<>();
    public static Path config = FabricLoader.getInstance().getConfigDir().resolve("villagernames");
    public static final GameRules.Key<GameRules.BooleanRule> SHOW_VILLAGER_DEATH =
        GameRuleRegistry.register("showVillagerDeath", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
    protected static final Random random = Random.create();
    //public static boolean USE_COMMON = false;
    @Override
    public void onInitialize() {
        LOGGER.log(Level.INFO,"Loading all known villager types");
        try {
            Files.createDirectory(config);

        } catch (Exception e){

        }
        for (VillagerType villagerType : Registries.VILLAGER_TYPE) {
            NAME_LIST.put(villagerType.toString().toLowerCase(), readNames(villagerType.toString().toLowerCase()));
        }
        TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.BUTCHER).get(4);
    }

    static public ArrayList<String> readNames(String villagerType){
        ArrayList<String> names;
        try{
            names = new ArrayList<>(Files.readAllLines(config.resolve(villagerType + ".txt")));
            LOGGER.log(Level.INFO, "Loaded names for '"+villagerType+"' type villagers.");
        }
        catch (IOException e){
            names = DefaultNames.getDefaults(villagerType);
            StringBuilder output = new StringBuilder();
            for (String name:
                 names) {
                output.append(name).append("\n");
            }
            try {
                Files.writeString(config.resolve(villagerType+".txt"), output.toString());
                LOGGER.log(Level.ERROR, "Couldn't find file for '"+villagerType+"' in config/villagernames, creating from default");

            } catch (Exception exception) {
                LOGGER.log(Level.ERROR, exception);

            }
        }
        return names;
    }

    static public String getRandomName(VillagerType biome){
        return getRandomName(biome.toString().toLowerCase());
    }
    static public String getRandomName(String biome){
        ArrayList<String> nameList = new ArrayList<>();
        if(NAME_LIST.containsKey(biome)){
            nameList.addAll(NAME_LIST.get(biome));
        } else {
            nameList.addAll(NAME_LIST.get("plains"));
        }
        return nameList.get(random.nextInt(nameList.size()));
    }

}
