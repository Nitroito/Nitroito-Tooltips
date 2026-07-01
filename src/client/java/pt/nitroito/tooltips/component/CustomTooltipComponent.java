package pt.nitroito.tooltips.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
	default void extractText(@NotNull final GuiGraphicsExtractor graphics, @NotNull final Font font, final int x, final int y) {
    }

    @Override
	default void extractImage(@NotNull final Font font, final int x, final int y, final int w, final int h, @NotNull final GuiGraphicsExtractor graphics) {
	    renderTooltip(graphics, font, x, y, w, h-getHeight(font));
	}

	default void renderTooltip(final GuiGraphicsExtractor graphics, final Font font, final int x, final int y, final int width, final int height){
    }
}
