package services;

import java.io.IOException;

import consts.CombineConst;
import consts.ItemEquipConst;
import item.Attribute;
import item.ItemEquip;
import item.ItemGem;
import lombok.NonNull;
import network.Message;
import player.Player;
import utils.CommandMessage;
import utils.Util;

/**
 *
 * @author ☂️☂️Duy Coder 💖💖
 */
public class CombineService {

    public static final CombineService instance = new CombineService();

    public void doThemDong(@NonNull Player player, short indexItem, short idMaterial, boolean isLockMaterial,
            short idDaTienGiai, boolean isLockDaTienGiai) throws IOException {
        ItemEquip itemBag = InventoryService.instance.findItemBag(player, indexItem);
        if (itemBag == null || itemBag.getTemplate().getNdayLoan() != 0) {
            return;
        }
        if (itemBag.getColorName() != ItemEquipConst.GREEN_COLOR || itemBag.getRank() != ItemEquipConst.NHAT_PHAM
                || itemBag.getLevel() < 50) {
            Service.instance.sendLogOut(player.getSession(),
                    "Cần trang bị nhất phẩm hoàn mỹ level 50 trở lên - [Khóa]");
            return;
        }
        if (itemBag.getItemAttributes().stream().filter(i -> i != null && i.getTemplate().getColorPaint() == 1)
                .count() >= 2) {
            Service.instance.sendLogOut(player.getSession(), "Trang bị đã thêm tối đa dòng");
            return;
        }
        ItemGem botXanhLa = isLockMaterial ? InventoryService.instance.findItemGemLock(player, idMaterial)
                : InventoryService.instance.findItemGem(player, idMaterial);
        if (botXanhLa == null || botXanhLa.getTemplate().getId() != 249) {
            Service.instance.sendLogOut(player.getSession(), "Không tìm thấy bột xanh lá");
            return;
        }
        ItemGem daTienGiai = isLockDaTienGiai ? InventoryService.instance.findItemGemLock(player, idDaTienGiai)
                : InventoryService.instance.findItemGem(player, idDaTienGiai);
        if (daTienGiai == null || daTienGiai.getTemplate().getId() != 250) {
            Service.instance.sendLogOut(player.getSession(), "Không tìm thấy đã tiến giai");
            return;
        }
        if (isLockDaTienGiai) {
            InventoryService.instance.minusQuantityItemGem(player, daTienGiai, (short) 1);
        } else {
            InventoryService.instance.minusQuantityItemGemLock(player, daTienGiai, (short) 1);
        }
        if (isLockMaterial) {
            InventoryService.instance.minusQuantityItemGem(player, botXanhLa, (short) 1);
        } else {
            InventoryService.instance.minusQuantityItemGemLock(player, botXanhLa, (short) 1);
        }
        if (Util.isTrue(2.6, 200.0)) {
            Attribute dongAn1 = itemBag.getItemAttributes().stream()
                    .filter(att -> att != null && att.getTemplate().getColorPaint() == 1).findFirst().orElse(null);
            short valueAn = 1;
            short idAttribute;
            do {
                idAttribute = ItemEquipConst.ATTRIBUTE_RANDOM_DONG_AN[Util.nextInt(0,
                        ItemEquipConst.ATTRIBUTE_RANDOM_DONG_AN.length - 1)];
            } while (idAttribute == dongAn1.getTemplate().getId());
            switch (itemBag.getRank()) {
                case ItemEquipConst.NHAT_PHAM ->
                    valueAn = (short) Util.nextInt(8, 9);
                case ItemEquipConst.NHI_PHAM ->
                    valueAn = (short) Util.nextInt(4, 7);
                case ItemEquipConst.TAM_PHAM ->
                    valueAn = (short) Util.nextInt(4, 6);
                case ItemEquipConst.TU_PHAM ->
                    valueAn = (short) Util.nextInt(2, 4);
            }
            if (idAttribute == 31) {
                valueAn = (short) Util.nextInt(3, 5);
            }
            Attribute attributeAdd = new Attribute(idAttribute, valueAn);
            itemBag.getItemAttributes().add(attributeAdd);
            Service.instance.sendLogOut(player.getSession(), String.format("Chúc mừng bạn đã mở được %s %s%s",
                    attributeAdd.getTemplate().getName(), valueAn, "%"));
        } else {
            if (itemBag.isWeapon() || itemBag.isJewelry()) {
                Attribute attribute = itemBag.getItemAttributes().stream()
                        .filter(att -> att != null && Util.binarySearch(
                                ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI_VU_KHI, att.getTemplate().getId()))
                        .findFirst().orElse(null);
                if (attribute != null) {
                    short attributeId;
                    do {
                        attributeId = ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI_VU_KHI[Util.nextInt(0,
                                ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI_VU_KHI.length - 1)];
                    } while (attributeId == attribute.getTemplate().getId());
                    short value = 0;
                    if (attributeId == 109) {
                        value = (short) Util.nextInt(200, 500);
                    }
                    itemBag.getItemAttributes().remove(attribute);
                    Attribute attributeAdd = new Attribute(attributeId, value);
                    itemBag.getItemAttributes().add(attributeAdd);
                    Service.instance.sendLogOut(player.getSession(),
                            String.format("Chúc mừng bạn đã mở được %s", attributeAdd.getInfo()));
                } else {
                    short attributeId = ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI_VU_KHI[Util.nextInt(0,
                            ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI_VU_KHI.length - 1)];
                    short value = 0;
                    if (attributeId == 109) {
                        value = (short) Util.nextInt(1000, 3000);
                    }
                    Attribute attributeAdd = new Attribute(attributeId, value);
                    itemBag.getItemAttributes().add(attributeAdd);
                    Service.instance.sendLogOut(player.getSession(),
                            String.format("Chúc mừng bạn đã mở được %s", attributeAdd.getInfo()));
                }
            } else {
                Attribute attribute = itemBag
                        .getItemAttributes().stream().filter(att -> att != null && Util
                                .binarySearch(ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI, att.getTemplate().getId()))
                        .findFirst().orElse(null);
                if (attribute != null) {
                    byte quantityAttributeNew = (byte) Util.nextInt(1, (int) itemBag
                            .getItemAttributes().stream().filter(att -> att != null && Util
                                    .binarySearch(ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI, att.getTemplate().getId()))
                            .count());
                    StringBuilder builder = new StringBuilder();
                    for (int i = itemBag.getItemAttributes().size() - quantityAttributeNew; i < itemBag
                            .getItemAttributes().size(); i++) {
                        Attribute attributeOld = itemBag.getItemAttributes().get(i);
                        short attributeId;
                        short value;
                        if (Util.isTrue(60.0, 100.0)) {
                            attributeId = ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI[Util.getOne(0, 1)];
                        } else if (Util.isTrue(34.0, 100.0)) {
                            attributeId = ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI[Util.nextInt(2, 5)];
                        } else {
                            attributeId = ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI[Util.nextInt(6, 8)];
                        }
                        if (attributeId == 78 || attributeId == 79) {
                            value = (short) Util.nextInt(1000, 5000);
                        } else {
                            value = (short) Util.nextInt(20, 100);
                        }
                        itemBag.getItemAttributes().remove(attributeOld);
                        Attribute attributeAdd = new Attribute(attributeId, value);
                        builder.append(attributeAdd.getInfo()).append(", ");
                        itemBag.getItemAttributes().add(attributeAdd);
                    }
                    if (builder.length() > 0) {
                        builder.setLength(builder.length() - 2);
                    }
                    Service.instance.sendLogOut(player.getSession(),
                            String.format("Chúc mừng bạn đã mở được %s", builder.toString()));
                } else {
                    byte quantityAttributeNew = (byte) Util.nextInt(1, 2);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < quantityAttributeNew; i++) {
                        short attributeId;
                        short value;
                        if (Util.isTrue(60.0, 100.0)) {
                            attributeId = ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI[Util.getOne(0, 1)];
                        } else if (Util.isTrue(34.0, 100.0)) {
                            attributeId = ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI[Util.nextInt(2, 5)];
                        } else {
                            attributeId = ItemEquipConst.ATTRIBUTE_RANDOM_TIEN_GIAI[Util.nextInt(6, 8)];
                        }
                        if (attributeId == 78 || attributeId == 79) {
                            value = (short) Util.nextInt(1000, 5000);
                        } else {
                            value = (short) Util.nextInt(20, 100);
                        }
                        Attribute attributeAdd = new Attribute(attributeId, value);
                        builder.append(attributeAdd.getInfo()).append(", ");
                        itemBag.getItemAttributes().add(attributeAdd);
                    }
                    if (builder.length() > 0) {
                        builder.setLength(builder.length() - 2);
                    }
                    Service.instance.sendLogOut(player.getSession(),
                            String.format("Chúc mừng bạn đã mở được %s", builder.toString()));
                }
            }
        }
        InventoryService.instance.sendItemBag(player);
    }

    public void doTachNguyenLieu(@NonNull Player player, byte type, short indexItem, short idMaterial, boolean isLock)
            throws IOException {
        ItemEquip itemBag = InventoryService.instance.findItemBag(player, indexItem);
        if (itemBag == null || itemBag.getTemplate().getNdayLoan() != 0) {
            return;
        }
        switch (type) {
            case CombineConst.NGUYEN_BOT -> {
                if (!Util.isNullOrEmpty(itemBag.getNameCharSeal())
                        || itemBag.getColorName() == ItemEquipConst.NONE_COLOR) {
                    Service.instance.sendLogOut(player.getSession(),
                            "Chỉ có thể nghiền trang bị rơi từ quái hoặc trang bị màu");
                    return;
                }
                if (!player.getInventory().minusXu(2000)) {
                    Service.instance.sendLogOut(player.getSession(), "Không đủ 2,000 xu");
                    return;
                }
                short quantityBot = 1;
                switch (itemBag.getColorName()) {
                    case ItemEquipConst.BLUE_COLOR ->
                        quantityBot = (short) Util.nextInt(2, 3);
                    case ItemEquipConst.RED_COLOR ->
                        quantityBot = (short) Util.nextInt(2, 5);
                    case ItemEquipConst.GREEN_COLOR ->
                        quantityBot = (short) Util.nextInt(2, 7);
                }
                boolean isLockItemE = itemBag.isLock();
                InventoryService.instance.removeItemBagEquipment(player, itemBag);
                itemBag.dispose();
                ItemGem bot = ItemService.instance.createNewItemGem((short) 246, quantityBot);
                sendSuccessNghienBot(player, bot, type);
                if (isLockItemE) {
                    InventoryService.instance.addItemGemLock(player, bot);
                    InventoryService.instance.sendItemGemLock(player);
                } else {
                    InventoryService.instance.addItemGem(player, bot);
                    InventoryService.instance.sendItemGem(player);
                }
                InventoryService.instance.sendItemPotion(player);
                InventoryService.instance.sendItemBag(player);
            }
        }
    }

    private void sendSuccessNghienBot(@NonNull Player player, @NonNull ItemGem bot, byte type) throws IOException {
        Message msg = new Message(CommandMessage.TACH_NGUYEN_LIEU);
        msg.writer().writeByte(type);
        if (type == CombineConst.NGUYEN_BOT) {
            msg.writer().writeShort(bot.getTemplate().getIdImage());
            msg.writer().writeByte(1);
            msg.writer().writeByte(1);
        }
        player.getSession().sendMessage(msg);
    }
}
