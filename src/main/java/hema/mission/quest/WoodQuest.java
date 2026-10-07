package hema.mission.quest;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class WoodQuest implements Quest{

    @Override
    public String title() {
        return "Mine 3 Oak Logs to get a Full Set of Wooden Tools!";
    }

    @Override
    public String note() {
        return "Note : take the 3 oak logs :) ";
    }

    @Override
    public int goal() {
        return 3;
    }

    @Override
    public String rewardText() {
        return "You received a full set of wooden tools";
    }

    @Override
    public void registerEvents() {
        PlayerBlockBreakEvents.AFTER.register(((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer && state.isOf(Blocks.OAK_LOG)) {
                QuestManager.add(serverPlayer, this);

            }
        }));
    }

    @Override
    public void reward(ServerPlayerEntity player) {
        List.of(Items.WOODEN_SWORD, Items.WOODEN_PICKAXE, Items.WOODEN_AXE,
                Items.WOODEN_SHOVEL, Items.WOODEN_HOE)
                .forEach(item -> player.getInventory().offerOrDrop(new ItemStack(item)));
    }
}
