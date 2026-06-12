package com.example.addon.modules;

import com.example.addon.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

public class AutoCraft extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Item> mineBlock = sgGeneral.add(new ItemSetting.Builder()
        .name("mine-block")
        .description("Block to mine")
        .defaultValue(Items.DIAMOND_ORE)
        .build()
    );

    private final Setting<String> craftCommand = sgGeneral.add(new StringSetting.Builder()
        .name("craft-command")
        .description("Craft command")
        .defaultValue("craft")
        .build()
    );

    private final Setting<Integer> craftDelay = sgGeneral.add(new IntSetting.Builder()
        .name("craft-delay")
        .description("Delay in ticks")
        .defaultValue(40)
        .min(10)
        .max(200)
        .build()
    );

    private int timer = 0;
    private boolean crafting = false;

    public AutoCraft() {
        super(Addon.CATEGORY, "auto-craft", "Auto mine and craft");
    }

    @Override
    public void onActivate() {
        timer = 0;
        crafting = false;
        ChatUtils.info("AutoCraft ON");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || !mc.player.isAlive()) return;

        timer++;

        if (!crafting) {
            int filled = 0;
            for (int i = 0; i < 36; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack != null && !stack.isEmpty()) filled++;
            }

            if (timer % 100 == 0) {
                ChatUtils.info("Inventory: " + filled + "/36");
            }

            if (filled >= 35) {
                crafting = true;
                timer = 0;
                ChatUtils.info("Full! Crafting...");
            }
        } else {
            if (timer == 10) {
                mc.player.networkHandler.sendChatCommand(craftCommand.get());
                ChatUtils.info("Command: /" + craftCommand.get());
            }

            if (timer >= craftDelay.get()) {
                crafting = false;
                timer = 0;
                ChatUtils.info("Done!");
            }
        }
    }
}
