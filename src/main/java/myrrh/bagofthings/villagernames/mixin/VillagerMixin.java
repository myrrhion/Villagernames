package myrrh.bagofthings.villagernames.mixin;

import myrrh.bagofthings.villagernames.Villagernames;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(VillagerEntity.class)
public abstract class VillagerMixin extends MerchantEntity {

    @Shadow public abstract VillagerData getVillagerData();

    @Shadow public abstract boolean isClient();

    @Shadow public abstract void tick();

    public VillagerMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initialize", at=@At("RETURN"))
    public void nameTheVillager(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir){
        Villagernames.LOGGER.log(Level.INFO,"Naming a villager");
        if(!this.hasCustomName()){
            this.setCustomName(Text.literal(Villagernames.getRandomName(this.getVillagerData().getType())));
        }

    }

    @Inject(method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/VillagerEntity;", at=@At("RETURN"), cancellable = true)
    public void babu(ServerWorld serverWorld, PassiveEntity otherParent, CallbackInfoReturnable<VillagerEntity> cir){
        if(this.isClient())
            return;
        VillagerEntity vil = cir.getReturnValue();
        String name = vil.getCustomName().getString();
        name = String.format("%s %sbur",name, this.getCustomName().getString().split(" ")[0]);
        vil.setCustomName(Text.of(name));
        Villagernames.LOGGER.log(Level.INFO,name);
        cir.setReturnValue(vil);

    }
    @Inject(method = "onDeath", at=@At("RETURN"))
    public void tacky(DamageSource damageSource, CallbackInfo ci){
        if(!Objects.requireNonNull(this.getServer()).getOverworld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)){
            return;
        };
        VillagerProfession job = this.getVillagerData().getProfession();
        String title = job.toString();
        title = title.substring(0, 1).toUpperCase() + title.substring(1);
        if(job == VillagerProfession.NONE){
            title = this.getType().toString();
        }
        String format = String.format("%s %s", Text.translatable(title).getString(),  (Object)damageSource.getDeathMessage(this).getString());
        if(this.isClient()){
            return;
        }
        Objects.requireNonNull(this.getServer()).getPlayerManager().broadcast(Text.translatable(format),false);
    }

    @Inject(method = "readCustomDataFromNbt", at=@At("RETURN"))
    public void read(NbtCompound nbt, CallbackInfo ci){
        if(!this.hasCustomName()){
            this.setCustomName(Text.literal(Villagernames.getRandomName(this.getVillagerData().getType())));
        }
    }
    @Inject(method = "wakeUp", at = @At("RETURN"))
    public void wakey(CallbackInfo ci){
        this.heal(1);
    }
}
