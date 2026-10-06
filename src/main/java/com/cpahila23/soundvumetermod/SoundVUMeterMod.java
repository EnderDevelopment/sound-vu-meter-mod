package com.cpahila23.soundvumetermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public
class SoundVUMeterMod implements ModInitializer {
    public static final String MOD_ID = "soundvumetermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final int MAX_VU_HEIGHT = 100;
    private static final int VU_WIDTH = 10;
    private static int currentVUHeight = 0;
    private static long lastSoundTime = 0;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Sound VU Meter Mod");
        registerHudRenderer();
        registerSoundListener();
    }

    private void registerHudRenderer() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            renderVUMeter(matrixStack);
        });
    }

    private void registerSoundListener() {
        MinecraftClient.getInstance().getSoundManager().addListener((soundInstance, soundEvent, soundCategory, x, y, z, volume, pitch, attenuationType, delay) -> {
            if (soundEvent != null) {
                updateVUMeter(volume);
            }
        });
    }

    private void updateVUMeter(float volume) {
        currentVUHeight = (int) (volume * MAX_VU_HEIGHT);
        lastSoundTime = System.currentTimeMillis();
    }

    private void renderVUMeter(MatrixStack matrixStack) {
        if (System.currentTimeMillis() - lastSoundTime > 1000) {
            currentVUHeight = Math.max(0, currentVUHeight - 1);
        }

        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int x = screenWidth - VU_WIDTH - 10;
        int y = screenHeight - currentVUHeight - 10;

        MinecraftClient.getInstance().textRenderer.draw(matrixStack, "VU Meter", x, y - 15, 0xFFFFFF);
        MinecraftClient.getInstance().textRenderer.draw(matrixStack, String.valueOf(currentVUHeight), x, y - 30, 0xFFFFFF);

        for (int i = 0; i < currentVUHeight; i++) {
            MinecraftClient.getInstance().textRenderer.draw(matrixStack, "|", x, y + i, 0x00FF00);
        }
    }
}
