package myrrh.bagofthings.villagernames;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.VillagerType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class Villagernames implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Villagernames.class);
    public static HashMap<String,ArrayList<String>> NAME_LIST = new HashMap<>();

    protected static final Random random = Random.create();
    //public static boolean USE_COMMON = false;
    @Override
    public void onInitialize() {
        LOGGER.log(Level.INFO,"Loading all know villager types");
        NAME_LIST.put(VillagerType.PLAINS.toString().toLowerCase(), readNames(VillagerType.PLAINS.toString().toLowerCase()));
    }

    static public ArrayList<String> readNames(String villagerType){
        ArrayList<String> names = new ArrayList<>();
        try{
            names.addAll(Files.readAllLines(FabricLoader.getInstance().getConfigDir().resolve("villagernames").resolve(villagerType+".txt")));
            LOGGER.log(Level.INFO, "Loaded names for '"+villagerType+"' type villagers.");
        }
        catch (IOException e){
            LOGGER.log(Level.ERROR, "Couldn't find file for '"+villagerType+"' in config/villagernames");
        }
        return names;
    }

    static public String getRandomName(VillagerType biome){
        return getRandomName(biome.toString());
    }
    static public String getRandomName(String biome){
        String name;
        ArrayList<String> nameList = new ArrayList<>();
        if(NAME_LIST.containsKey(biome)){
            nameList.addAll(NAME_LIST.get(biome));
        } else {
            nameList.addAll(NAME_LIST.get("plains"));
        }
        return nameList.get(random.nextInt(nameList.size()));
    }

}
