package evolution.mod.mixins;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static evolution.mod.utils.DyeUtils.DyeColors;
/**
 * @author
 * eliarbogast
 * Silas Zhao
 */
@Mixin(SheepEntity.class)
public class SheepColorMixin  {

    @Inject(method = "getChildColor", at = @At("RETURN"), cancellable = true)
    private void modifyChildColor(AnimalEntity firstParent, AnimalEntity secondParent, CallbackInfoReturnable<DyeColor> cir) {
        DyeColor current = cir.getReturnValue();
        //if % chance that a mutation occurs, set dye value to slightly different color
            //define list  of colors in rainbow order, search list for current color and pick adjacent color
        //list of names from dyeColor.java, cir.getName() -> look up in list of names, then grab adjacent then do
        //dyeColor.byName which will give an object that can be set to the return value
        double mutDouble = Math.random();
        boolean mutate;
        //System.out.println("yes!! inside modifyChildColor!");
        //mutate rate
        mutate = mutDouble < 1.0;
        if (mutate) {

            double randDouble = Math.random();
            int randAdd = 0;
            if(randDouble > 0.5){
                double rand = Math.random();
                randAdd = (int)( 6 * Math.random());//add one to increase the mutation rate between generations
            }
            else{
                double rand = Math.random();
                randAdd = (int)(-6 * Math.random());//add one to increase the mutation rate between generations
            }
            System.out.println("randAdd = " + randAdd);

            for (int i=0; i<DyeColors.length; i++){
                if(DyeColors[i].equals(current.getName())){
                    //System.out.println("oldColor: "+ i + " oldColorName: " + DyeColors[i]);
                    int newColor = i + randAdd;
                    //check the color ring
                    if(newColor > 15){
                        newColor = newColor - 15;
                    }else if(newColor < 0){
                        newColor += 15;
                    }
                    //System.out.println("newColor: "+ newColor + " newColorName: " + DyeColors[newColor]);
                    DyeColor tempColor = DyeColor.byName(DyeColors[newColor], current);
                    cir.setReturnValue(tempColor);
                }
            }

        }
    }

}






