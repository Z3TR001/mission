package hema.mission;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Interface;

public interface QuestInterfacee {
    String title();
    String note();
    int goal();
    String rewardText();
    void reward(ServerPlayerEntity player);
    void registerEvents();
}