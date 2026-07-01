package pt.nitroito.tooltips.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class UtilsGraphics {
    public static final int RED = DyeColor.RED.getTextColor();
    public static final int ORANGE = 0xFFFF8000;
    public static final int GREEN = 0xFF00FF00;
    public static final int DARK_GREEN = 0xFF008000;
    public static final int LIGHT_BLACK = 0xFF222222;


    public static void horizontalGradient(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color1, int color2, int color3) {
        for (int i = 0; i < width; i++) {
            float step = (float) i / (width - 1);
            int gradientColor = getGradientColor(step, color1, color2, color3);
            graphics.fill(x + i, y, x + i + 1, y + height, gradientColor);
        }
    }

    public static void renderEntityModel(GuiGraphicsExtractor graphics, Entity model, float scale, Quaternionf rotation, boolean useAnimation, int x, int y, int w, int h){
        model.setYRot(0);
        model.setYBodyRot(0);
        model.setYHeadRot(0);
        float ticks = useAnimation ? (System.currentTimeMillis() % 100000L)/50.0f : 0;
        model.setId(-1);
        EntityRenderState renderState = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(model).createRenderState(model, ticks);
        graphics.entity(renderState, scale, new Vector3f(), rotation, null, x, y, x+w, y+h);
    }

    private static int getGradientColor(float step, int color1, int color2, int color3) {
        if (step < 0.15f) return UtilsGraphics.lerpColor(color1, color2, (step-0.00f)/0.15f);
        if (step < 1.00f) return UtilsGraphics.lerpColor(color2, color3, (step-0.15f)/0.85f);
        return color3;
    }

    private static int lerpColor(int color1, int color2, float step) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int r = (int)(r1 + (r2 - r1) * step);
        int g = (int)(g1 + (g2 - g1) * step);
        int b = (int)(b1 + (b2 - b1) * step);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
