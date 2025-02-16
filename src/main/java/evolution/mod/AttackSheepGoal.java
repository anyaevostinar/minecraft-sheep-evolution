package evolution.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;

import java.util.EnumSet;
/*
This class is modified from MeleeAttack goal
 * @author
 * Silas Zhao
 */
public class AttackSheepGoal extends Goal {
    protected final PathAwareEntity mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private int field_24667;
    private final int attackIntervalTicks = 20;
    private long lastUpdateTime;

    private int failAttackCount = 0;
    public AttackSheepGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public boolean canStart() {
        long l = this.mob.world.getTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        } else {
            this.lastUpdateTime = l;
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().findPathTo((Entity)livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isIdle();
        } else if (!this.mob.isInWalkTargetRange(livingEntity.getBlockPos())) {
            return false;
        } else {
            return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative();
        }
    }

    public void start() {
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
        this.updateCountdownTicks = 0;
        this.field_24667 = 0;
    }

    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget((LivingEntity)null);
        }

        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
    }

    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
        double d = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
        if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
            this.targetX = livingEntity.getX();
            this.targetY = livingEntity.getY();
            this.targetZ = livingEntity.getZ();
            this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
            if (d > 1024.0D) {
                this.updateCountdownTicks += 10;
            } else if (d > 256.0D) {
                this.updateCountdownTicks += 5;
            }

            if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
                this.updateCountdownTicks += 15;
            }
        }

        this.field_24667 = Math.max(this.field_24667 - 1, 0);
        this.attack(livingEntity, d);
    }
    /*
    if the target is a sheep, then based on the difference between the color of its skin and the surrounding, we shrink the max attack distance and
    reduce the wolf's successful attack sheep rate.
    If a wolf failed to attack sheep for more than 10 times, it will be dead.
     */
    protected void attack(LivingEntity target, double squaredDistance) {

        double d = this.getSquaredMaxAttackDistance(target);
        double difference = 1;
        //this means the wolf encountered a sheep
        if(target instanceof SheepEntity){
            //System.out.println("found an sheep entity");
            SheepEntityExt sheep = (SheepEntityExt)target;
            difference = sheep.getDifference() * 2; //difference can be 0 - 4/3
            //System.out.println("previous SquaredMaxAttackDistance is: " + d);
            //System.out.println("getSquaredMaxAttackDistance is: " + d);
        }
        if (squaredDistance <= d && this.field_24667 <= 0) {
            this.method_28346();
            this.mob.swingHand(Hand.MAIN_HAND);
            if(Math.random() <= difference) {
                //System.out.println("attacked the sheep.");
                this.mob.tryAttack(target);
                //change an attacking target.
                this.mob.getNavigation().stop();
            }else {
                //change an attacking target.
                this.mob.getNavigation().stop();
                failAttackCount++;
                //if the amount of failure attacking is larger than 10, the wolf dead.
                if(failAttackCount > 3){
                    this.mob.onDeath(DamageSource.OUT_OF_WORLD);
                    System.out.println("wolf dead.");
                    //decrement the number of wolf
                    ((WorldExt)this.mob.world).addWolf(-1);
                    ((WorldExt)this.mob.world).printAmount();
                }
                //System.out.println("didn't attack sheep.");
            }
        }

    }

    protected void method_28346() {
        this.field_24667 = 20;
    }

    protected boolean method_28347() {
        return this.field_24667 <= 0;
    }

    protected int method_28348() {
        return this.field_24667;
    }

    protected int method_28349() {
        return 20;
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double)(this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F + entity.getWidth());
    }
}
