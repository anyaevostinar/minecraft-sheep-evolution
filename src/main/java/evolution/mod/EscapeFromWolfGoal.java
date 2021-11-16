package evolution.mod;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
/*
This class is modified from AvoidLlamaGoal
 */
/**
 * @author
 * Silas Zhao
 */
public class EscapeFromWolfGoal extends Goal {
    protected final PathAwareEntity mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;

    public EscapeFromWolfGoal(PathAwareEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }
    //if encountered a wolf, then start this goal.
    public boolean canStart() {
        return this.mob.getAttacker() instanceof WolfEntity;
    }

    protected boolean findTarget() {
        Vec3d vec3d = TargetFinder.findTarget(this.mob, 5, 4);
        if (vec3d == null) {
            return false;
        } else {
            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;
            return true;
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.active = true;
    }

    public void stop() {
        this.active = false;
    }

    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Nullable
    protected BlockPos locateClosestWater(BlockView blockView, Entity entity, int rangeX, int rangeY) {
        BlockPos blockPos = entity.getBlockPos();
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        float f = (float)(rangeX * rangeX * rangeY * 2);
        BlockPos blockPos2 = null;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int l = i - rangeX; l <= i + rangeX; ++l) {
            for(int m = j - rangeY; m <= j + rangeY; ++m) {
                for(int n = k - rangeX; n <= k + rangeX; ++n) {
                    mutable.set(l, m, n);
                    if (blockView.getFluidState(mutable).isIn(FluidTags.WATER)) {
                        float g = (float)((l - i) * (l - i) + (m - j) * (m - j) + (n - k) * (n - k));
                        if (g < f) {
                            f = g;
                            blockPos2 = new BlockPos(mutable);
                        }
                    }
                }
            }
        }

        return blockPos2;
    }
}

