package hema.mission.quest;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class KillQuest implements Quest {

    private static final Item[][] UPGRADES = {
            {Items.WOODEN_SWORD, Items.STONE_SWORD},
            {Items.WOODEN_PICKAXE, Items.STONE_PICKAXE},
            {Items.WOODEN_AXE, Items.STONE_AXE},
            {Items.WOODEN_SHOVEL, Items.STONE_SHOVEL}
    };

    @Override
    public String title() {
        return "Kill 5 Mobs to Upgrade Your Tools to Stone";
    }

    @Override
    public String note() {
        return "Note: Only Hostile Mobs Count, DONT GO MASSACRE VILLAGERS";
    }

    @Override
    public int goal() {
        return 5;
    }

    @Override
    public String rewardText() {
        return "Your Wooden Tools Were Upgraded To Stone";
    }

    @Override
    public void registerEvents() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayerEntity serverPlayer && killedEntity instanceof Monster) {
                QuestManager.add(serverPlayer, this);
            }
        });
    }

    @Override
    public void reward(ServerPlayerEntity player) {
        PlayerInventory inv = player.getInventory();

        for (Item[] pair : UPGRADES) {
            boolean upgraded = false;

            for (int slot = 0; slot < inv.size(); slot++) {
                if (inv.getStack(slot).isOf(pair[0])) {
                    inv.setStack(slot, new ItemStack(pair[1]));
                    upgraded = true;
                    break;
                }
            }

            if (!upgraded) {
                inv.offerOrDrop(new ItemStack(pair[1]));
            }
        }
    }
}