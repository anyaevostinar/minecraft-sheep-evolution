package evolution.mod.mixins;

import ca.weblite.objc.Client;
import evolution.mod.WorldExt;
import evolution.mod.utils.DyeUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static evolution.mod.utils.DyeUtils.colorRingOfDyeColors;
@Mixin(World.class)
public abstract class AnimalAmountMixin implements WorldExt {
    Integer sheepAmount = 0;
    Integer greenSheepAmount = 0;
    Integer limeSheepAmount = 0;
    Integer wolfNumber = 0;
    public void addSheep(int i){
        sheepAmount+=i;
    }
    public void addWolf(int i){
        wolfNumber+=i;
    }
    public void addGreenSheep(int i){
        greenSheepAmount+=i;
        addSheep(i);
    }
    public void addLimeSheep(int i){
        limeSheepAmount+=i;
        addSheep(i);
    }
    public void printAmount(){
        System.out.printf("number of wolf: %d%n", wolfNumber);
        System.out.printf("number of sheep: %d%n", sheepAmount);
        System.out.printf("number of green sheep: %d%n", greenSheepAmount);
        System.out.printf("number of lime sheep: %d%n", limeSheepAmount);
    }
}
