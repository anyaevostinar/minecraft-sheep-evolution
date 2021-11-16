package evolution.mod.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.passive.AnimalEntity;



@Mixin(SheepEntity.class)
public abstract class GrassReproduceMixin extends AnimalEntity{

    private int grassCount;

    protected GrassReproduceMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method="onEatingGrass", at = @At("HEAD"))
    public void onEatingGrass(CallbackInfo info) {

        GrassReproduceMixin thisEntity = (GrassReproduceMixin) (Object) (this);
        this.grassCount++;
        if(grassCount >= 4){
            if(!this.isBaby()) {
                if (!this.world.isClient) {
                    setLoveTicks(60000);
//                    PassiveEntity passiveEntity = this.createChild((ServerWorld) world, this);
//                    if (passiveEntity != null) {
//                        ServerPlayerEntity serverPlayerEntity = this.getLovingPlayer();
//                        if (serverPlayerEntity != null) {
//                            serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
//                            //Normally animalEntity 1 and two are different, but the way we did also works.
//                            Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this, this, passiveEntity);
//                        }
//
//                        this.setBreedingAge(6000);
//                        this.resetLoveTicks();
//                        passiveEntity.setBaby(true);
//                        passiveEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
//                        ((ServerWorld) world).spawnEntityAndPassengers(passiveEntity);
//                        ((ServerWorld) world).sendEntityStatus(this, (byte) 18);
//                        if (((ServerWorld) world).getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
//                            ((ServerWorld) world).spawnEntity(new ExperienceOrbEntity(((ServerWorld) world), this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
//                        }
//
//                    }
                    grassCount = 0;
                }
            }
            //try to create child whenever a sheep eat grass

        }
    }
}
