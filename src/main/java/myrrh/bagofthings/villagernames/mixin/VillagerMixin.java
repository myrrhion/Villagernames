package myrrh.bagofthings.villagernames.mixin;

import myrrh.bagofthings.villagernames.Villagernames;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerData;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerMixin extends MerchantEntity {

    @Shadow public abstract VillagerData getVillagerData();

    public VillagerMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at=@At("RETURN"))
    public void nameTheVillager(NbtCompound nbt, CallbackInfo ci){
        Villagernames.LOGGER.log(Level.INFO,"This was called");
        if(!this.hasCustomName()){
            this.getVillagerData().getType().toString().toLowerCase();
            this.setCustomName(Text.literal("ASasas"));
        }
    }

    @Inject(method = "setAttacker", at=@At("RETURN"))
    public void tacky(LivingEntity attacker, CallbackInfo ci){
        Villagernames.LOGGER.log(Level.INFO, "TACKED!");
    }
}
