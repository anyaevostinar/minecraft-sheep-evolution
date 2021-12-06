
package evolution.mod.mixins;


        import evolution.mod.EscapeFromWolfGoal;
        import evolution.mod.SheepEntityExt;
        import evolution.mod.WorldExt;
        import net.minecraft.entity.EntityType;
        import net.minecraft.entity.passive.AnimalEntity;
        import net.minecraft.entity.passive.SheepEntity;
        import net.minecraft.util.DyeColor;
        import net.minecraft.world.World;
        import org.spongepowered.asm.mixin.Mixin;
        import org.spongepowered.asm.mixin.injection.At;
        import org.spongepowered.asm.mixin.injection.Inject;
        import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/**
 * @author Silas Zhao
 */
@Mixin(SheepEntity.class)
public abstract class EscapeFromWolfMixin extends AnimalEntity {

    protected EscapeFromWolfMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="initGoals", at = @At("HEAD"))
    //pass in CallbackInfo here, since the function "initGoals" in SheepEntity is a void type.
    public void initGoals(CallbackInfo info) {
        //System.out.println("init sheep Goals");
        //lower number have more priority. So 0 represents the highest priority
        this.goalSelector.add(0, new EscapeFromWolfGoal(this, 2.5D));
        //update number of sheep
        DyeColor color = ((SheepEntityInvoker) this).getSheepColor();
        System.out.println("color: " + color.getName());
        if(color.getName().equals("lime") ){
            ((WorldExt) world).addLimeSheep(1);
        }else if(color.getName().equals("green")){
            ((WorldExt) world).addGreenSheep(1);
        }else{
            ((WorldExt) world).addSheep(1);
        }
        ((WorldExt) world).printAmount();
    }
}
