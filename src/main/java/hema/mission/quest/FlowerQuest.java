package hema.mission.quest;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class FlowerQuest implements Quest {

    private static final Item[][] UPGRADES = {
            {Items.STONE_SWORD, Items.IRON_SWORD},
            {Items.STONE_PICKAXE, Items.IRON_PICKAXE},
            {Items.STONE_AXE, Items.IRON_AXE},
            {Items.STONE_SHOVEL, Items.IRON_SHOVEL}
    };

    @Override
    public String title() {
        return " Kill an Iron Golem to Start the Iron Era";
    }

    @Override
    public String note() {
        return "Note: You prob need to find a Village First";
    }

    @Override
    public int goal() {

        return 1;
    }

    @Override
    public String rewardText() {
        return "Full Iron Armor + Your Stone Tools Upgraded to Iron!";
    }

    @Override
    public void registerEvents() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayerEntity serverPlayer && killedEntity instanceof IronGolemEntity) {
                QuestManager.add(serverPlayer, this);
            }
        }));
    }


    @Override
    public void reward(ServerPlayerEntity player ) {
        PlayerInventory inv = player.getInventory();


        List.of(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS)
                .forEach(item -> inv.offerOrDrop(new ItemStack(item)));


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
