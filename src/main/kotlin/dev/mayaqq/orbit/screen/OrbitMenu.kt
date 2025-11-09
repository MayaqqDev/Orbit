package dev.mayaqq.orbit.screen

import com.mojang.blaze3d.pipeline.RenderPipeline
import dev.mayaqq.orbit.Orbit
import dev.mayaqq.orbit.data.IconType
import dev.mayaqq.orbit.data.OrbitButton
import dev.mayaqq.orbit.utils.McClient
import dev.mayaqq.orbit.utils.Text
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.ui.UIConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.BlitRenderState
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix3x2f
import kotlin.math.*

class OrbitMenu : ControlsPassthroughScreen(Text.EMPTY) {

    var selectedButton: OrbitButton? = null

    val buttonWidgets: List<Button> = List(Orbit.buttons.size) { Widgets.button() }

    override fun init() {
        buttonWidgets.forEachIndexed { index, button ->
            button.setSize(40, 40)
            button.withTexture(UIConstants.BUTTON)
            button.withRenderer(
                WidgetRenderers.layered(
                    WidgetRenderers.sprite(UIConstants.BUTTON),
                    WidgetRenderers.center(40, 40) { gr, ctx, _ ->
                        val button = Orbit.buttons[index]
                        when(button.iconType) {
                            IconType.ITEM -> {
                                val item = button.item()
                                gr.renderItem(item, ctx.x + 12, ctx.y + 12)
                            }
                            IconType.TEXTURE -> {
                                val input = ResourceLocation.parse(button.iconItem)
                                val texture = ResourceLocation.fromNamespaceAndPath(
                                    input.namespace,
                                    buildString {
                                        if (!input.path.startsWith("textures/")) append("textures/")
                                        append(input.path)
                                        if (!input.path.endsWith(".png")) append(".png")
                                    }
                                )
                                gr.pose().pushMatrix()
                                gr.pose().translate(ctx.x + 12F, ctx.y + 12F)
                                gr.guiRenderState.submitGuiElement(
                                    BlitRenderState(
                                        RenderPipelines.GUI_TEXTURED,
                                        TextureSetup.singleTexture(McClient.self.textureManager.getTexture(texture).textureView),
                                        Matrix3x2f(gr.pose()),
                                        0,
                                        0,
                                        16,
                                        16,
                                        0f,
                                        1f,
                                        0f,
                                        1f,
                                        0xFFFFFFFFu.toInt(),
                                        gr.scissorStack.peek(),
                                    ),
                                )
                                gr.pose().popMatrix()
                            }
                        }
                    }
                ))

            val angle = (index * (360.0 / buttonWidgets.size)) - 90.0
            val radius = 100
            val centerX = width / 2.0
            val centerY = height / 2.0
            val x = centerX + radius * cos(Math.toRadians(angle)) - 20.0
            val y = centerY + radius * sin(Math.toRadians(angle)) - 20.0
            button.setPosition(x.roundToInt(), y.roundToInt())

            button.withCallback {
                val button = Orbit.buttons[index]
                if (McClient.self.hasShiftDown()) {
                    McClient.tell { McClient.setScreen(ConfigurationScreen(button)) }
                } else {
                    button.execute()
                    onClose()
                }
            }


            button.withShape { mouseX, mouseY, width, height ->
                val dx = mouseX + x - centerX
                val dy = mouseY + y - centerY

                val distance = sqrt(dx * dx + dy * dy)
                val innerRadius = 10
                val outerRadius = 200

                if (distance.toInt() !in innerRadius..outerRadius) return@withShape false

                val angle = (atan2(dy, dx) + 2 * PI) % (2 * PI) + (PI / 2)

                val correctedAngle = (angle + (PI / buttonWidgets.size)) % (2 * PI)

                val segmentIndex = (correctedAngle / (2 * PI) * buttonWidgets.size).toInt()

                segmentIndex == index
            }


            button.visitWidgets(this::addRenderableWidget)
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, f: Float) {
        if (!Orbit.ORBIT.isDown) McClient.tell { McClient.setScreen(null) }
        val centerX = width / 2
        val centerY = height / 2

        var anySelected = false
        buttonWidgets.forEachIndexed { index, button ->
            if (button.isHoveredOrFocused) {
                anySelected = true
                selectedButton = Orbit.buttons[index]
            }
        }
        if (!anySelected) selectedButton = null

        selectedButton?.let {
            graphics.drawCenteredString(
                McClient.font,
                Text.trans(it.actionString),
                centerX,
                centerY,
                0xFFFFFFFFu.toInt()
            )
        }
        super.render(graphics, mouseX, mouseY, f)
    }

    override fun keyReleased(event: KeyEvent): Boolean {
        if (event.key == Orbit.ORBIT.key.value) {
            buttonWidgets.forEachIndexed { index, button ->
                if (button.isHoveredOrFocused) {
                    Orbit.buttons[index].execute()
                }
            }
            onClose()
        }
        return super.keyReleased(event)
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        McClient.options.keyHotbarSlots.mapIndexed { index, mapping ->
            if (mapping.key.value == event.key) {
                Orbit.buttons[index].execute()
                onClose()
                return true
            }
        }
        return super.keyPressed(event)
    }

    override fun isPauseScreen(): Boolean = false

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {}
}