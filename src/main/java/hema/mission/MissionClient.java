package hema.mission;

import hema.mission.quest.Quest;
import hema.mission.quest.QuestManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

public class MissionClient implements ClientModInitializer {

    private static final int ORANGE      = 0xFFFFAA00;
    private static final int WHITE       = 0xFFFFFFFF;
    private static final int GRAY        = 0xFFAAAAAA;
    private static final int DARK_GRAY   = 0xFF303030;
    private static final int BLACK       = 0xFF000000;
    private static final int GREEN       = 0xFF00C800;
    private static final int LIGHT_GREEN = 0xFF55FF55;

    private static final int BAR_WIDTH = 120;
    private static final int BAR_HEIGHT = 10;

    private static int quest = 0;
    private static int progress = 0;

    private static long flashStart = -1;
    private static String flashText = "";


    private static int withAlpha(int color, int alpha) {

        return (alpha << 24) | (color & 0x00FFFFFF);

    }

    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(QuestManager.SyncPayload.ID, ((payload, context) -> {
            quest = payload.quest();
            progress = payload.progress();


            if (payload.completed() && quest > 0 && quest <= QuestManager.QUESTS.size()) {
                flashText = QuestManager.QUESTS.get(quest - 1).rewardText();
                flashStart = System.currentTimeMillis();
            }
        }));

        HudRenderCallback.EVENT.register(MissionClient::render);
    }

    private static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden)

            return;

        TextRenderer font = mc.textRenderer;
        int w = ctx.getScaledWindowWidth();
        int h = ctx.getScaledWindowHeight();
        int cx = w / 2;

        if (quest < QuestManager.QUESTS.size()) {
            Quest q = QuestManager.QUESTS.get(quest);

            ctx.drawCenteredTextWithShadow(font, Text.literal(q.title()), cx, 8, ORANGE);

            int barX = cx - BAR_WIDTH / 2;
            int barY = 22;
            ctx.fill(barX - 1, barY - 1, barX + BAR_WIDTH + 1, barY + BAR_HEIGHT + 1, BLACK);
            ctx.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, DARK_GRAY);
            ctx.fill(barX, barY, barX + BAR_WIDTH * progress / q.goal(), barY + BAR_HEIGHT, GREEN);
            ctx.drawCenteredTextWithShadow(font, Text.literal(progress + "/" + q.goal()), cx, barY + 1, WHITE);
            ctx.drawCenteredTextWithShadow(font, Text.literal(q.note()), cx, barY + BAR_HEIGHT + 5, GRAY);

        }

        if (flashStart > 0) {
            long t = System.currentTimeMillis() - flashStart;

            if ( t > 3000) {
                flashStart = -1;

                return;
            }

            float fade = t < 2000 ? 1f : 1f - (t - 2000) / 1000f;
            int alpha = (int) (fade * 255);
            if (alpha <= 8)
                return;


            ctx.fill(0, 0 , w , h , withAlpha(GREEN, (int) (alpha * 0.5f)));

            MatrixStack m = ctx.getMatrices();
            m.push();
            m.scale(3f, 3f, 1f);

            ctx.drawCenteredTextWithShadow(font, Text.literal("QUEST COMPLETED"), cx / 3 ,
                    (h / 2 - 30) / 3, withAlpha(LIGHT_GREEN, alpha));
            m.pop();

            ctx.drawCenteredTextWithShadow(font, Text.literal(flashText),
                    cx, h / 2 + 5, withAlpha(WHITE, alpha));
        }

    }
}
