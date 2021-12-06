package evolution.mod.mixins;

import evolution.mod.WorldExt;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/**
 * @author Silas Zhao
 */
@Mixin(WolfEntity.class)
public abstract class AttackSheepAndBreedMixin extends AnimalEntity implements WorldExt {
    int killSheepCount = 0;
    protected AttackSheepAndBreedMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }
    //kill 3 sheep -> produce one child.
    @Inject(method="tryAttack", at = @At("TAIL"))
    //tryAttack in wolfEntity returns a boolean, so include CallbackInfoReturnable<Boolean> as one of its parameters.
    public void tryAttack(Entity target,  CallbackInfoReturnable<Boolean> cir) {
        //System.out.println("try attack!");
        if(target instanceof SheepEntity) {
            //System.out.println("target is a sheep");
            SheepEntity sheep = (SheepEntity) target;
            if (sheep.getHealth() < 0) {
                System.out.println("kill a sheep!");
                killSheepCount++;
                ((WorldExt)world).addSheep(-1);
                ((WorldExt)world).printAmount();
            }
        }
        if(killSheepCount > 2){
            System.out.println("bread!");

            //update kill count
            killSheepCount = 0;
            //Currently asexual because a wolf want to reproduce offspring natually needs 1, is tamed, 2 have enough meet, and 3 is in love(loveTick > 0).
            //If a wolf is tamed, it will not attack sheep anymore, and we don't know how many meet is needed to let wolf reproduce.
            //This function is modified from breed() in AnimalEntity. This function can be used to create the children of any animals.
            PassiveEntity passiveEntity = this.createChild((ServerWorld) world , this);
            if (passiveEntity != null) {
                ServerPlayerEntity serverPlayerEntity = this.getLovingPlayer();
                if (serverPlayerEntity != null) {
                    serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
                    //Normally animalEntity 1 and two are different, but the way we did also works.
                    Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this, this, passiveEntity);
                }
                this.setBreedingAge(6000);
                this.resetLoveTicks();
                passiveEntity.setBaby(true);
                passiveEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
                ((ServerWorld)world).spawnEntityAndPassengers(passiveEntity);
                ((ServerWorld)world).sendEntityStatus(this, (byte)18);
                if (((ServerWorld)world).getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                    ((ServerWorld)world).spawnEntity(new ExperienceOrbEntity(((ServerWorld)world), this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
                }

            }
        }
    }

}
