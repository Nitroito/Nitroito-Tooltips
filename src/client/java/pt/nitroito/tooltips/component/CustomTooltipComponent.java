package pt.nitroito.tooltips.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.NotNull;

public interface CustomTooltipComponent extends ClientTooltipComponent {

    @Override
    default int getHeight(@NotNull Font font) {
        return 0;
    }

    @Override
	default int getWidth(@NotNull final Font font){
        return 0;
    }

    @Override
	default void renderText(@NotNull final GuiGraphics graphics, @NotNull final Font font, final int x, final int y) {
    }

    @Override
	default void renderImage(@NotNull final Font font, final int x, final int y, final int w, final int h, @NotNull final GuiGraphics graphics) {
	    renderTooltip(graphics, font, x, y, w, h-getHeight(font));
	}

	default void renderTooltip(final GuiGraphics graphics, final Font font, final int x, final int y, final int width, final int height){
    }
}
