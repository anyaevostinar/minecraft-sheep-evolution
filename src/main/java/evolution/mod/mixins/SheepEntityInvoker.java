package evolution.mod.mixins;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SheepEntity.class)
public interface SheepEntityInvoker {
    //invoker let you invoke the "getColor()" function in sheepEntity. More detail see the wiki page in readMe.
    @Invoker("getColor")
    DyeColor getSheepColor();
}
