package evolution.mod.mixins;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class AngerTimeMixin extends AnimalEntity {

    protected AngerTimeMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    //@Inject(method="setAngerTime", at = @At("HEAD"))
    public void setAngerTime(int ticks, CallbackInfo info) {
        ticks = ticks /10;
    }
}
