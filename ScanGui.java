package com.example.serverscan;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

import java.util.List;

public class ScanGui extends Screen {
    private TextFieldWidget ipRangeField;
    private TextFieldWidget portRangeField;
    private TextFieldWidget apiUrlField;
    private ButtonWidget importButton, exportButton, startScanButton;
    private MultiplayerScreen parent;

    protected ScanGui(MultiplayerScreen parent) {
        super(Text.of("高级服务器扫描"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int y = 40;
        ipRangeField = new TextFieldWidget(textRenderer, width/2-100, y, 200, 20, Text.of("IP/域名范围"));
        ipRangeField.setText("192.168.1.1-192.168.1.100");
        addDrawableChild(ipRangeField);

        portRangeField = new TextFieldWidget(textRenderer, width/2-100, y+=25, 200, 20, Text.of("端口范围"));
        portRangeField.setText("25565-25570");
        addDrawableChild(portRangeField);

        apiUrlField = new TextFieldWidget(textRenderer, width/2-100, y+=25, 200, 20, Text.of("远程服务器API"));
        apiUrlField.setText("https://example.com/mcservers.json");
        addDrawableChild(apiUrlField);

        importButton = ButtonWidget.builder(Text.of("导入列表"), btn -> {
            ServerScanner.importServers();
        }).dimensions(width/2-100, y+=30, 90, 20).build();
        addDrawableChild(importButton);

        exportButton = ButtonWidget.builder(Text.of("导出结果"), btn -> {
            ServerScanner.exportResults();
        }).dimensions(width/2+10, y, 90, 20).build();
        addDrawableChild(exportButton);

        startScanButton = ButtonWidget.builder(Text.of("开始扫描"), btn -> {
            String ipRange = ipRangeField.getText();
            String portRange = portRangeField.getText();
            String apiUrl = apiUrlField.getText();
            ServerScanner.startScan(ipRange, portRange, apiUrl, this);
        }).dimensions(width/2-45, y+=35, 90, 20).build();
        addDrawableChild(startScanButton);
    }

    // 结果展示、进度条等可扩展
    public void showResults(List<ServerScanner.ScanResult> results) {
        // TODO: 展示服务器列表，可筛选、排序、一键收藏
    }

    public static void open(MultiplayerScreen parent) {
        MinecraftClient.getInstance().setScreen(new ScanGui(parent));
    }
}