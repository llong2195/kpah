package services;

import java.io.IOException;

import item.ItemGem;
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
            String strInput = chat.replace("m ", "");
            try {
                short val = Short.parseShort(strInput);
                ChangeMapService.instance.changeMap(pl, val, (short) -1, (short) -1);
            } catch (NumberFormatException e) {
                System.out.println("invalid data input:" + strInput);
                return true;
            }
            return true;
        }
        if (chat.startsWith("xu")) {
            String strInput = chat.replace("xu", "");
            try {
                long xu = Long.parseLong(strInput);
                pl.getInventory().plusXu(xu);
                InventoryService.instance.sendItemPotion(pl);
            } catch (NumberFormatException e) {
                System.out.println("invalid data input:" + strInput);
                return true;
            }

            return true;
        }
        if (chat.startsWith("luong")) {
            String strInput = chat.replace("luong", "");
            try {
                int luong = Integer.parseInt(strInput);
                pl.getInventory().plusLuong(luong);
                InventoryService.instance.sendItemPotion(pl);
            } catch (NumberFormatException e) {
                System.out.println("invalid data input:" + strInput);
                return true;
            }
            return true;
        }
        if (chat.startsWith("lk")) {
            String strInput = chat.replace("lk", "");
            try {
                int lk = Integer.parseInt(strInput);
                pl.getInventory().plusLuongKhoa(lk);
                InventoryService.instance.sendItemPotion(pl);
            } catch (NumberFormatException e) {
                System.out.println("invalid data input:" + strInput);
                return true;
            }
            return true;
        }
        if (chat.equals("bxl")) {
            ItemGem item = ItemService.instance.createNewItemGem((short) 249, Short.MAX_VALUE);
            InventoryService.instance.addItemGem(pl, item);
            InventoryService.instance.sendItemGem(pl);
            return true;
        }
        if (chat.startsWith("lv")) {
            String strInput = chat.replace("lv", "");
            try {
                byte exp = Byte.parseByte(strInput);
                pl.getInfo().setLevel(exp);
                pl.getPoint().setExp(Math.max(1, pl.getPoint().getExp() - Util.getExp(pl.getInfo().getLevel())));
                pl.getPoint().plusStrength(exp);
                pl.getPoint().plusHealth(exp);
                pl.getPoint().plusAgility(exp);
                pl.getPoint().plusLuck(exp);
                pl.getPoint().plusSpirit(exp);
                pl.getPoint().plusSkillPoint(exp);
                pl.getPoint().plusBasePoint(5 * exp);
                pl.getPoint().initPoint();
                MapService.instance.onLevelUp(pl);
                Service.instance.sendMainCharInfo(pl);
                MapService.instance.sendInfoMe(pl);
            } catch (NumberFormatException e) {
                System.out.println("invalid data input:" + strInput);
                return true;
            }

            return true;
        }
        return false;
    }
}
