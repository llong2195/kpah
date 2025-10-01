package org.kpah.top;

import java.util.List;

import org.kpah.item.ItemFriend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopPlayer {

    private String name;
    private byte head;
    private byte level;
    private short idClan;
    private byte isMaster;
    private List<ItemFriend> items;
    private long xu;
    private int luong;
    private byte nationId;

    public void dispose() {
        items.clear();
    }
}
