package mc.rysty.heliosphereminigames.queue;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.MessageUtils;

public class QueueScheduler {

    private static HelioSphereMinigames plugin = HelioSphereMinigames.getInstance();
    private static FileConfiguration queuesFile = HelioSphereMinigames.getQueuesFile().getData();

    private static HashMap<String, Boolean> minigameStartingMap = QueueHelper.minigameStartingMap;
    private static HashMap<String, Boolean> lastMinigameStartingMap = new HashMap<String, Boolean>();
    private static HashMap<String, Integer> minigameQueueCountdownMap = new HashMap<String, Integer>();

    public static void enableScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!minigameStartingMap.isEmpty()) {
                    for (Entry<String, Boolean> mapEntry : minigameStartingMap.entrySet()) {
                        String minigame = mapEntry.getKey();
                        Boolean isMinigameStarting = mapEntry.getValue();

                        if (!isMinigameStarting && lastMinigameStartingMap.get(minigame) == null)
                            lastMinigameStartingMap.put(minigame, false);
                        Boolean wasMinigameStarting = lastMinigameStartingMap.get(minigame);

                        queueCountdown(minigame);

                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayer.getScoreboardTags().contains(minigame + "Queue")) {
                                if (isMinigameStarting)
                                    endQueue(minigame, onlinePlayer);
                                else if (isMinigameStarting != wasMinigameStarting)
                                    MessageUtils.message(onlinePlayer, "&6&l(!)&f Countdown cancelled!");
                            }
                        }
                        lastMinigameStartingMap.put(minigame, isMinigameStarting);
                    }
                }
            }
        }, 0, 20);
    }

    private static void queueCountdown(String minigame) {
        boolean isMinigameStarting = minigameStartingMap.get(minigame);
        boolean wasMinigameStarting = lastMinigameStartingMap.get(minigame);
        int initialCountdown = queuesFile.getInt("queues." + minigame + ".countdown") + 1;

        if (minigameQueueCountdownMap.get(minigame) == null)
            minigameQueueCountdownMap.put(minigame, initialCountdown);

        if (wasMinigameStarting && !isMinigameStarting)
            minigameQueueCountdownMap.put(minigame, initialCountdown);
        int remainingCountdownTime = minigameQueueCountdownMap.get(minigame);

        if (isMinigameStarting)
            if (remainingCountdownTime > 0)
                minigameQueueCountdownMap.put(minigame, minigameQueueCountdownMap.get(minigame) - 1);
            else {
                minigameStartingMap.put(minigame, false);
                lastMinigameStartingMap.put(minigame, false);
                minigameQueueCountdownMap.put(minigame, null);
            }
    }

    private static void endQueue(String minigame, Player player) {
        int remainingCountdownTime = minigameQueueCountdownMap.get(minigame);
        HashMap<Integer, Boolean> countdownIntegerMap = new HashMap<Integer, Boolean>();

        countdownIntegerMap.put(1, true);
        countdownIntegerMap.put(2, true);
        countdownIntegerMap.put(3, true);
        countdownIntegerMap.put(10, false);

        if (remainingCountdownTime > 0) {
            if (countdownIntegerMap.containsKey(remainingCountdownTime)) {
                MessageUtils.message(player, "&6&l(!)&f Game beginning in&b " + remainingCountdownTime + " &fseconds!");

                if (countdownIntegerMap.get(remainingCountdownTime))
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER, 1, 2);
            }
        } else {
            QueueHelper.removePlayerFromQueue(player, false);
            player.getScoreboardTags().add(queuesFile.getString("queues." + minigame + ".gametag"));
            queuesFile.set("queues." + minigame + ".running", false);
            HelioSphereMinigames.getQueuesFile().saveData();

            MessageUtils.message(player, "&6&l(!)&f Game starting...");
        }
    }
}