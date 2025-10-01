package org.kpah.minigame;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.kpah.player.Player;
import org.kpah.services.Service;

import lombok.NonNull;
import lombok.Synchronized;

public class VongQuay {

    private final ConcurrentHashMap<Short, Long> playersNormal = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Short, Long> playersVip = new ConcurrentHashMap<>();

    @Synchronized
    public void addPlayerNormal(@NonNull Player player, long xu) throws IOException {
        if (playersVip.containsKey(player.getIdPlayer())) {
            Service.instance.sendLogOut(player.getSession(), "Chỉ có thể đăng ký 1 vòng quay 1 lúc");
            return;
        }
        if (!playersNormal.containsKey(player.getIdPlayer())) {
            playersNormal.put(player.getIdPlayer(), xu);
        } else {
            long xuOld = playersNormal.get(player.getIdPlayer());
            playersNormal.replace(player.getIdPlayer(), xuOld + xu);
        }
    }

    @Synchronized
    public void addPlayerVip(@NonNull Player player, long xu) throws IOException {
        if (playersNormal.containsKey(player.getIdPlayer())) {
            Service.instance.sendLogOut(player.getSession(), "Chỉ có thể đăng ký 1 vòng quay 1 lúc");
            return;
        }
        if (!playersVip.containsKey(player.getIdPlayer())) {
            playersVip.put(player.getIdPlayer(), xu);
        } else {
            long xuOld = playersVip.get(player.getIdPlayer());
            playersVip.replace(player.getIdPlayer(), xuOld + xu);
        }
    }

    @Synchronized
    public void removePlayerNormal(@NonNull Player player) {
        if (playersNormal.containsKey(player.getIdPlayer())) {
            player.getInventory().plusXu(playersNormal.get(player.getIdPlayer()));
            playersNormal.remove(player.getIdPlayer());
        }
    }

    @Synchronized
    public void removePlayerVip(@NonNull Player player) {
        if (playersVip.containsKey(player.getIdPlayer())) {
            player.getInventory().plusXu(playersVip.get(player.getIdPlayer()));
            playersVip.remove(player.getIdPlayer());
        }
    }

    public void disposePlayer(@NonNull Player player) {
        removePlayerNormal(player);
        removePlayerVip(player);
    }
}
