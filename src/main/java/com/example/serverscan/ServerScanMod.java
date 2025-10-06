package com.example.serverscan;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ServerScanMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // 注册在多人游戏界面添加按钮
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof MultiplayerScreen) {
                // 扫描按钮
                ButtonWidget scanButton = ButtonWidget.builder(Text.of("服务器扫描/导入/自定义"), btn -> {
                    ScanGui.open((MultiplayerScreen)screen);
                }).dimensions(10, 10, 160, 20).build();
                screen.addDrawableChild(scanButton);
            }
        });
    }
}