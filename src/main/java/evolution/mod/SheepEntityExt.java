package evolution.mod;

import net.minecraft.block.MaterialColor;
/**
 * @author
 * Silas Zhao
 */
//create the interface to call function.
public interface SheepEntityExt {
    void killSheepBySurroundingColor();
    MaterialColor getSurroundingColor();
    double getDifference();
}
