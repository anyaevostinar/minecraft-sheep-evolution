package evolution.mod.mixins;

import evolution.mod.SheepEntityExt;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

import static evolution.mod.utils.DyeUtils.colorRingOfDyeColors;
import static evolution.mod.utils.DyeUtils.colorRingOfMaterials;
/**
 * @author
 * Silas Zhao
 */
@Mixin(SheepEntity.class)
public abstract class SheepLifeCycleMixin extends AnimalEntity implements SheepEntityExt,SheepEntityInvoker{
    public SheepLifeCycleMixin(EntityType<? extends SheepEntity> entityType, World world) {
        super(entityType, world);
    }

    //@Inject(method="onEatingGrass", at = @At("HEAD"))
    public void onEatingGrass(CallbackInfo info) {
        //System.out.println("onEatingGrass");
        ((SheepEntityExt)this).killSheepBySurroundingColor();
    }
    //this function can return the most appeared surrounding color.
    public MaterialColor getSurroundingColor() {
        BlockPos blockPos = this.getBlockPos();
        List<MaterialColor> colorList = new ArrayList<>();
        //search radius 5*5, elements in colorList are the color of surrounding area (5 * 5)
        for(int i = -2; i < 3; i++){
            for(int j = -2; j < 3; j++){
                Material material = this.world.getBlockState(blockPos.add(i,j,0)).getMaterial();
                if(material.isSolid()) {
                    colorList.add(material.getColor());
                }
            }
        }
        //get the most frequent appeared surrounding color.
        if(colorList.size()!=0){
            Map<MaterialColor,Long> map = colorList.stream().collect(Collectors.groupingBy(materialColor -> materialColor, Collectors.counting()));
            MaterialColor key = null;
            int count = 0;
            for(Map.Entry<MaterialColor,Long> item:map.entrySet()){
                if(item.getValue().intValue() > count){
                    key = item.getKey();
                }
            }
            //System.out.println(key.id);
            return key;
        }
        return null;
    }
    //this is the function that can kill sheep.
    public void killSheepBySurroundingColor(){
        if(Random(getDifference())){
            //System.out.println("dead shee color: " + ((SheepEntityInvoker)this).getSheepColor().name());
            //kill the sheep directly by calling onDeath
            this.onDeath(DamageSource.OUT_OF_WORLD);
        }
    }

    private Boolean Random(double d){
        double rand = Math.random();
        return rand <= d;
    }
    //This function is to detect the difference between color of sheep skin and the surrounding color.
    public double getDifference(){
        int colorOfSurroundings = colorRingOfMaterials.get(((SheepEntityExt)this).getSurroundingColor().id);
        //System.out.println("((SheepEntityExt)this).getSurroundingColor().color: "+((SheepEntityExt)this).getSurroundingColor().id);
        int sheepColor = colorRingOfDyeColors.get(((SheepEntityInvoker)this).getSheepColor().name().toLowerCase());
        //System.out.println("((SheepEntityInvoker)this).getSheepColor().name(): " + ((SheepEntityInvoker)this).getSheepColor().name());
        double difference = 0;
        /* compare the difference by the contrast of the sheep, max = 8 */
        //This is to use dyeColor as a color ring. more details in the readMe.txt
        if(colorOfSurroundings > sheepColor){
            difference = Math.min(colorOfSurroundings - sheepColor, sheepColor + 15 - colorOfSurroundings);
        }else{
            difference = Math.min(sheepColor - colorOfSurroundings, colorOfSurroundings + 15 - sheepColor);
        }//mutation amount
        //4% on average to create child after eating grass once.
        //max contrast is 8
        //if it is baby, decrease the difference by one half
        difference = isBaby() ? difference / 2: difference;
        //if the sheep has a similar color with its surrounding, namely difference < 2, decrease the difference by one half.
        difference = difference < 2? (difference / 25):(difference / 12);
        //System.out.println("difference: " + difference);//adding comment here and readME.
        //difference is between 0- 0.666
        return difference;
    }
}
