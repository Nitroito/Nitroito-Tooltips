package pt.nitroito.tooltips.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import pt.nitroito.tooltips.TooltipsConfig;
import pt.nitroito.tooltips.TooltipsGlobals;
import pt.nitroito.tooltips.utils.UtilsGraphics;


public enum BucketEntityModel {
    AXOLOTL (4, 0.75f, 1.80f, 1.50f),
    COD (3, 1.00f, 1.40f, 1.00f),
    PUFFERFISH (4, 0.80f, 1.20f, 1.80f),
    SALMON (4, 0.90f, 2.00f, 1.10f),
    TADPOLE (3, 1.00f, 1.00f, 1.50f),
    TROPICAL_FISH_LARGE (4, 1.00f, 1.20f, 1.20f),
    TROPICAL_FISH_SMALL (3, 1.00f, 1.20f, 1.00f);

    private static final float MODEL_DEFAULT_SCALE = 50;
    private static final float MILLISECONDS_PER_ROTATION = 8000;
    private static final float ZERO = 0.0f;
    private static final float PI = (float)Math.PI;
    private static final float TWO_PI = (float)Math.TAU;
    private static final float HALF_PI = (float)Math.PI/2.0f;

    private final int tooltipLines;
    private final float scale;
    private final float viewportWidthFactor;
    private final float viewportHeightFactor;

    public float scale(){return this.scale;}
    public int tooltipHeight(){return this.tooltipLines * (Minecraft.getInstance().font.lineHeight+1);}
    public int viewportWidth(){return Math.round(this.viewportWidthFactor * MODEL_DEFAULT_SCALE);}
    public int viewportHeight(){return Math.round(this.viewportHeightFactor * this.tooltipHeight());}


    BucketEntityModel(int tooltipLines, float scale, float viewportWidthFactor, float viewportHeightFactor){
        this.scale = MODEL_DEFAULT_SCALE * scale;
        this.tooltipLines = tooltipLines;
        this.viewportWidthFactor = viewportWidthFactor;
        this.viewportHeightFactor = viewportHeightFactor;
    }

    public Quaternionf staticPose(){
        Vector3f angles = this.isSchoolingFish() ? new Vector3f(HALF_PI,HALF_PI,PI) : new Vector3f(PI,HALF_PI,ZERO);
        return new Quaternionf().rotationXYZ(angles.x(), angles.y(), angles.z());
    }

    public Quaternionf rotatedPose(long referenceTimeMillis) {
        float elapsedMillis = (System.currentTimeMillis()-referenceTimeMillis) % MILLISECONDS_PER_ROTATION;
        float radians = (elapsedMillis * TWO_PI) / MILLISECONDS_PER_ROTATION;
        return this.isSchoolingFish() ? staticPose().rotateX(radians) : staticPose().rotateY(radians);
    }

    public boolean isTropicalFish(){
        return (this==TROPICAL_FISH_LARGE || this==TROPICAL_FISH_SMALL);
    }

    public boolean isSchoolingFish(){
        return (this.isTropicalFish() || this==COD || this==SALMON);
    }

    public static BucketEntityModel from(ItemStack stack) {
        if (Minecraft.getInstance().level==null || !(stack.getItem() instanceof MobBucketItem bucket)) return null;
        Entity entity = bucket.type.create(Minecraft.getInstance().level, EntitySpawnReason.BUCKET);
        if (entity instanceof Mob entityMob){
            entity.applyComponentsFromItemStack(stack);
            switch (entityMob) {
                case Axolotl ignored -> {return AXOLOTL;}
                case Cod ignored -> {return COD;}
                case Pufferfish ignored-> {return PUFFERFISH;}
                case Salmon ignored -> {return SALMON;}
                case Tadpole ignored-> {return TADPOLE;}
                case TropicalFish ignored -> {
                    TropicalFish.Pattern fishPattern = stack.get(DataComponents.TROPICAL_FISH_PATTERN);
                    if (fishPattern==null) return null;
                    if (fishPattern.base().equals(TropicalFish.Base.LARGE))
                        return  TROPICAL_FISH_LARGE;
                    else
                        return TROPICAL_FISH_SMALL;
                }
                default -> throw new IllegalStateException("Unexpected value: " + entityMob);
            }
        }
        return null;
    }

    public void renderModel(GuiGraphicsExtractor graphics, ItemStack stack, boolean isBaby, long referenceTimeMillis, int x, int y){
        Entity entityModel = this.createModel(stack);
        if (entityModel instanceof Mob mob) mob.setBaby(isBaby);
        Quaternionf modelRotation = TooltipsConfig.bucketEntityRotate ? this.rotatedPose(referenceTimeMillis) : this.staticPose();
        boolean useAnimation =  TooltipsConfig.bucketEntityAnimate && (this.isSchoolingFish() || this==BucketEntityModel.TADPOLE);

        UtilsGraphics.renderEntityModel(graphics, entityModel, this.scale(), modelRotation, useAnimation, x, y, this.viewportWidth(), this.viewportHeight());
    }

    private Entity createModel(ItemStack itemStack){
        if (itemStack.getItem() instanceof MobBucketItem bucket){
            Entity result = bucket.type.create(TooltipsGlobals.getLevel(), EntitySpawnReason.BUCKET);
            if (result !=null){
                result.applyComponentsFromItemStack(itemStack);
                if (this==BucketEntityModel.SALMON)
                    result.setComponent(DataComponents.SALMON_SIZE, Salmon.Variant.MEDIUM);
                if (this==BucketEntityModel.PUFFERFISH)
                    result.getEntityData().set(Pufferfish.PUFF_STATE, Pufferfish.STATE_FULL);
            }
            return result;
        }
        return null;
    }
}
