package services;

import item.ItemGem;
import java.io.IOException;
import lombok.NonNull;
import manager.ClientManager;
import network.Message;
import player.Player;
import utils.CommandMessage;
import utils.Util;

/**
 *
 * @author ☂️☂️Duy Coder 💖💖
 */
public class ChatService {

    public static final ChatService instance = new ChatService();

    public void sendChatPrivate(@NonNull Player playerSend, String nameChat, String chat) throws IOException {
        if (playerSend.getName().equals(nameChat)) {
            return;
        }
        Player playerReceive = ClientManager.getPlayer(nameChat);
        if (playerReceive == null) {
            return;
        }
        Message msg = new Message(CommandMessage.MESSAGE_PRIVATE);
        msg.writer().writeByte(1);
        msg.writer().writeUTF(playerSend.getName());
        msg.writer().writeUTF(chat);
        playerReceive.getSession().sendMessage(msg);
    }

    public void sendChatWorld(@NonNull Player pl, String chat) throws IOException {
        Message msg = new Message(CommandMessage.MESSAGE_WORLD);
        msg.writer().writeUTF(String.format("%s: %s", pl.getName(), chat));
        Service.instance.sendAllPlayer(msg);
    }

    public void sendChat(@NonNull Player pl, String chat) throws IOException {
        if (pl.getSession().isAdmin() && processChatAdmin(pl, chat)) {
            return;
        }
        if (!Util.canDoWithTime(pl.getSundry().getLastTimeChat(), 3000)) {
            return;
        }
        pl.getSundry().setLastTimeChat(System.currentTimeMillis());
        Message msg = new Message(CommandMessage.CHAT);
        msg.writer().writeShort(pl.getIdPlayer());
        msg.writer().writeUTF(chat);
        MapService.instance.sendAnotherNotMeInMap(pl, msg);
    }

    public void sendChatDelay(@NonNull Player pl, String s) throws IOException {
        Message msg = new Message(CommandMessage.MESSAGE_DELAY);
        msg.writer().writeUTF(s);
        pl.getSession().sendMessage(msg);
    }

    public void sendChatOnlyMe(@NonNull Player pl, String chat) throws IOException {
        Message msg = new Message(CommandMessage.CHAT);
        msg.writer().writeShort(pl.getIdPlayer());
        msg.writer().writeUTF(chat);
        pl.getSession().sendMessage(msg);
    }

    private boolean processChatAdmin(@NonNull Player pl, String chat) throws IOException {
        if (chat.startsWith("m")) {
            ChangeMapService.instance.changeMap(pl, Short.parseShort(chat.replace("m ", "")), (short) -1, (short) -1);
            return true;
        }
        if (chat.startsWith("xu")) {
            long xu = Long.parseLong(chat.replace("xu", ""));
            pl.getInventory().plusXu(xu);
            InventoryService.instance.sendItemPotion(pl);
            return true;
        }
        if (chat.startsWith("luong")) {
            int luong = Integer.parseInt(chat.replace("luong", ""));
            pl.getInventory().plusLuong(luong);
            InventoryService.instance.sendItemPotion(pl);
            return true;
        }
        if (chat.startsWith("lk")) {
            int luongK = Integer.parseInt(chat.replace("lk", ""));
            pl.getInventory().plusLuongKhoa(luongK);
            InventoryService.instance.sendItemPotion(pl);
            return true;
        }
        if (chat.equals("bxl")) {
            ItemGem item = ItemService.instance.createNewItemGem((short) 249, Short.MAX_VALUE);
            InventoryService.instance.addItemGem(pl, item);
            InventoryService.instance.sendItemGem(pl);
            return true;
        }
        if (chat.startsWith("lv")) {
            byte exp = Byte.parseByte(chat.replace("lv", ""));
            pl.getInfo().setLevel(exp);
            pl.getPoint().setExp(Math.min(1, pl.getPoint().getExp() - Util.getExp(pl.getInfo().getLevel())));
            pl.getPoint().plusStrength(1 * exp);
            pl.getPoint().plusHealth(1 * exp);
            pl.getPoint().plusAgility(1 * exp);
            pl.getPoint().plusLuck(1 * exp);
            pl.getPoint().plusSpirit(1 * exp);
            pl.getPoint().plusSkillPoint(1 * exp);
            pl.getPoint().plusBasePoint(5 * exp);
            pl.getPoint().initPoint();
            MapService.instance.onLevelUp(pl);
            Service.instance.sendMainCharInfo(pl);
            MapService.instance.sendInfoMe(pl);
            return true;
        }
        return false;
    }
}
