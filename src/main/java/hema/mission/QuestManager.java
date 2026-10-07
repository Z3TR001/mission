package hema.mission;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public final class QuestManager {


    public static final List<Quest> QUESTS = List.of(

    );

    public record Progress(int quest, int progress) {
        public static final Codec<Progress> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("quest").forGetter(Progress::quest),
                Codec.INT.fieldOf("progress").forGetter(Progress::progress)
                ).apply(i, Progress::new));
    }

    public static final AttachmentType<Progress> DATA = AttachmentRegistry.<Progress>builder()
            .persistent(Progress.CODEC)
            .copyOnDeath()
            .initializer(() -> new Progress(0, 0))
            .buildAndRegister(Identifier.of(Mission.MOD_ID, "progress"));

    public record SyncPayLoad(int quest, int progress, boolean completed) implements CustomPayload {
        public static final CustomPayload.Id<SyncPayLoad> ID =
                new CustomPayload.Id<>(Identifier.of(Mission.MOD_ID, "sync"));
    }

    }

