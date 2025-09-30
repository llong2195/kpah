package org.kpah.services;

import java.io.IOException;
import java.util.List;

import org.kpah.consts.AttributeConst;
import org.kpah.consts.Const;
import org.kpah.consts.ItemEquipConst;
import org.kpah.consts.ManufactureConst;
import org.kpah.item.Attribute;
import org.kpah.item.ItemEquip;
import org.kpah.item.ItemGem;
import org.kpah.item.ItemMineral;
import lombok.NonNull;
import org.kpah.manager.Manager;
import org.kpah.network.Message;
import org.kpah.player.Player;
import org.kpah.template.GemTemplate;
import org.kpah.template.ItemEquipTemplate;
import org.kpah.utils.CommandMessage;
import org.kpah.utils.Printer;
import org.kpah.utils.Util;

public class ManufactureService {

    public static ManufactureService instance = new ManufactureService();

    public void doCheDo(@NonNull Player player, byte type, byte[][] quantity) throws IOException {
        if (!player.getManufacture().getItemMineral().isEmpty()) {
            Service.instance.sendLogOut(player.getSession(), "Không có nguyên liệu");
            return;
        }
        if (player.getInventory().isFullInventory()) {
            Service.instance.sendLogOut(player.getSession(), "Túi đồ đầy");
            return;
        }
        switch (type) {
            case ManufactureConst.CHE_TAO_VU_KHI -> manufactureWeapon(player, quantity);
            case ManufactureConst.CHE_TAO_GIAP -> manufactureArmor(player, quantity);
        }
    }

    public void manufactureAnimalArmor(@NonNull Player player, short[] idItem, byte[] idMaterials) throws IOException {
        if (idItem.length != 5 || idMaterials.length != 6) {
            Service.instance.sendLogOut(player.getSession(), "Nguyên liệu không phù hợp");
            return;
        }
        if (player == null || player.getManufacture() == null) {
            Service.instance.sendLogOut(player.getSession(), "Có lỗi xảy ra");
            Printer.printYellow("ManufactureService.manufactureAnimalArmor: player or manufacture is null");
            return;
        }
        short idMaterial = ManufactureConst.MATERIAL_CREATE_ANIMAL_ARMOR[player.getManufacture()
                .getColorAnimalArmorCreate()];
        byte quantityMaterial = ManufactureConst.QUANTITY_MATERIAL_ANIMAL_ARMOR[player.getManufacture()
                .getSelectedItemCreate()];
        byte typeDamage = player.getManufacture().getTypeDamageCreate();
        byte typeNguyenLieu = player.getManufacture().getTypeNguyenLieuCreate();
        byte colorCreate = player.getManufacture().getColorAnimalArmorCreate();
        colorCreate = colorCreate == 3 ? ItemEquipConst.GREEN_COLOR
                : colorCreate == 2 ? ItemEquipConst.RED_COLOR : ItemEquipConst.BLUE_COLOR;
        byte colorRequest = colorCreate == ItemEquipConst.GREEN_COLOR ? ItemEquipConst.RED_COLOR
                : colorCreate == ItemEquipConst.RED_COLOR ? ItemEquipConst.BLUE_COLOR : ItemEquipConst.NONE_COLOR;
        byte levelArmorCreate = ManufactureConst.LEVEL_ANIMAL_ARMOR[player.getManufacture().getSelectedItemCreate()];
        byte numMaterial = -1;
        byte indexMaterial = -1;
        for (byte i = 0; i < idMaterials.length; i++) {
            if (idMaterials[i] > 0) {
                numMaterial = idMaterials[i];
                idMaterial += i;
                indexMaterial = i;
                break;
            }
        }
        if (numMaterial == -1 || typeDamage == -1 || typeNguyenLieu == -1 || colorCreate == -1 || indexMaterial == -1) {
            Service.instance.sendLogOut(player.getSession(), "Không tìm thấy nguyên liệu");
            return;
        }
        ItemGem gem = typeNguyenLieu == 0 ? InventoryService.instance.findItemGem(player, idMaterial)
                : typeNguyenLieu == 1 ? InventoryService.instance.findItemGemLock(player, idMaterial)
                        : InventoryService.instance.findAllItemGem(player, idMaterial);
        if (gem == null || gem.getQuantity() < quantityMaterial) {
            Service.instance.sendLogOut(player.getSession(), "Không tìm thấy nguyên liệu");
            sendManufactureArmor(player);
            return;
        }
        byte typeItemAnimalArmor = -1;
        for (int i = 0; i < idItem.length; i++) {
            ItemEquip item = InventoryService.instance.findItemBag(player, idItem[i]);
            if (item == null || !item.isAnimalArmor() || item.getLevel() < 30 || item.getLevel() > levelArmorCreate + 5
                    || item.getLevel() < levelArmorCreate - 5 || item.getColorName() != colorRequest) {
                Service.instance.sendLogOut(player.getSession(), "Trang bị không phù hợp");
                return;
            }
            if (i == 0) {
                typeItemAnimalArmor = item.getTemplate().getType();
            }
            InventoryService.instance.removeItemBagEquipment(player, item);
        }
        InventoryService.instance.minusQuantityItemGem(player, gem, quantityMaterial);
        ItemEquipTemplate template = Manager.getItemEquipment(typeItemAnimalArmor, levelArmorCreate);
        if (typeItemAnimalArmor == -1 || template == null) {
            Service.instance.sendLogOut(player.getSession(), "Có lỗi xảy ra");
            return;
        }
        // animal armor chỉ có 3 phẩm
        byte rankItem = getRankItem(indexMaterial);

        ItemEquip itemAdd = ItemService.instance.createNewItemEquipment(template.getId(), template.getClassChar());
        itemAdd.getItemAttributes().clear();
        itemAdd.setNameCharSeal(player.getName());
        itemAdd.setRank(rankItem);
        itemAdd.setColorName(colorCreate);
        itemAdd.setHe((byte) Util.nextInt(Const.THUY, Const.KIM));
        itemAdd.setLock(gem.isLock());
        itemAdd.setDamageType(typeDamage);
        switch (colorCreate) {
            case ItemEquipConst.BLUE_COLOR -> {
                switch (template.getType()) {
                    case ItemEquipConst.ANIMAL_QUAN -> {
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.TAN_CONG,
                                        (short) (ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR * (indexMaterial * 10)
                                                        / 100)));
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.CHINH_XAC,
                                        (short) (ManufactureConst.ATTRIBUTE_ACCURATE_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ACCURATE_ANIMAL_ARMOR
                                                        * (indexMaterial * 10) / 100)));
                    }
                    case ItemEquipConst.ANIMAL_BAN_DAP -> {
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.TAN_CONG,
                                        (short) (ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR * (indexMaterial * 10)
                                                        / 100)));
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.CHI_MANG,
                                        ManufactureConst.ATTRIBUTE_CRIT_ANIMAL_ARMOR));
                    }
                    default -> {
                        short def = (short) (ManufactureConst.ATTRIBUTE_DEF_ANIMAL_ARMOR
                                + ManufactureConst.ATTRIBUTE_DEF_ANIMAL_ARMOR * (indexMaterial * 10) / 100);
                        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.THU_VAT, def));
                        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.THU_MA, def));
                    }
                }
            }
            case ItemEquipConst.RED_COLOR -> {
                switch (template.getType()) {
                    case ItemEquipConst.ANIMAL_QUAN -> {
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.TAN_CONG,
                                        (short) (ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR * (indexMaterial * 13)
                                                        / 100)));
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.CHINH_XAC,
                                        (short) (ManufactureConst.ATTRIBUTE_ACCURATE_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ACCURATE_ANIMAL_ARMOR
                                                        * (indexMaterial * 13) / 100)));
                    }
                    case ItemEquipConst.ANIMAL_BAN_DAP -> {
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.TAN_CONG,
                                        (short) (ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR * (indexMaterial * 13)
                                                        / 100)));
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.CHI_MANG,
                                        ManufactureConst.ATTRIBUTE_CRIT_ANIMAL_ARMOR));
                    }
                    default -> {
                        short def = (short) (ManufactureConst.ATTRIBUTE_DEF_ANIMAL_ARMOR
                                + ManufactureConst.ATTRIBUTE_DEF_ANIMAL_ARMOR * (indexMaterial * 13) / 100);
                        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.THU_VAT, def));
                        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.THU_MA, def));
                    }
                }
            }
            case ItemEquipConst.GREEN_COLOR -> {
                switch (template.getType()) {
                    case ItemEquipConst.ANIMAL_QUAN -> {
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.TAN_CONG,
                                        (short) (ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR
                                                        * ((indexMaterial + 1) * 20) / 100)));
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.CHINH_XAC,
                                        (short) (ManufactureConst.ATTRIBUTE_ACCURATE_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ACCURATE_ANIMAL_ARMOR
                                                        * ((indexMaterial + 1) * 20) / 100)));
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.TANG_CONG, (short) Util.nextInt(1, 10)));
                        if (itemAdd.getColorName() != ItemEquipConst.NONE_COLOR) {
                            itemAdd.getItemAttributes()
                                    .add(new Attribute((short) Util.getOne(AttributeConst.BO_QUA_TC_MA,
                                            AttributeConst.BO_QUA_TC_VAT), (short) Util.nextInt(1, 3)));
                        }
                    }
                    case ItemEquipConst.ANIMAL_BAN_DAP -> {
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.TAN_CONG,
                                        (short) (ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_ATTACK_ANIMAL_ARMOR
                                                        * ((indexMaterial + 1) * 20) / 100)));
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.CHINH_XAC,
                                        (short) (ManufactureConst.ATTRIBUTE_CRIT_ANIMAL_ARMOR
                                                + ManufactureConst.ATTRIBUTE_CRIT_ANIMAL_ARMOR
                                                        * ((indexMaterial + 1) * 20) / 100)));
                        itemAdd.getItemAttributes()
                                .add(new Attribute(AttributeConst.TANG_CONG, (short) Util.nextInt(1, 10)));
                        if (itemAdd.getColorName() != ItemEquipConst.NONE_COLOR) {
                            itemAdd.getItemAttributes()
                                    .add(new Attribute((short) Util.getOne(AttributeConst.BO_QUA_TC_MA,
                                            AttributeConst.BO_QUA_TC_VAT), (short) Util.nextInt(1, 3)));
                        }
                    }
                    default -> {
                        short def = (short) (ManufactureConst.ATTRIBUTE_DEF_ANIMAL_ARMOR
                                + ManufactureConst.ATTRIBUTE_DEF_ANIMAL_ARMOR * ((indexMaterial + 1) * 20) / 100);
                        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.THU_VAT, def));
                        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.THU_MA, def));
                        short defPercent = (short) Util.nextInt(10, 20);
                        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_MA, defPercent));
                        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_VAT, defPercent));
                        if (itemAdd.getColorName() != ItemEquipConst.NONE_COLOR) {
                            itemAdd.getItemAttributes()
                                    .add(new Attribute(
                                            (short) Util.getOne(AttributeConst.BO_QUA_TC_MA,
                                                    AttributeConst.BO_QUA_TC_VAT),
                                            (short) Util.nextInt(1, 3)));
                        }
                    }
                }
            }
        }
        itemAdd.subDefend();
        InventoryService.instance.addItemBagEquipment(player, itemAdd);
        Service.instance.sendLogOut(player.getSession(),
                String.format("Tạo thành công %s %s", template.getName(), Util.getColor(colorCreate)));
        InventoryService.instance.sendItemGem(player);
        InventoryService.instance.sendItemBag(player);
    }

    public void manufactureArmor(@NonNull Player player, byte[][] quantity) throws IOException {
        byte typeClassChar = player.getManufacture().getClassCharCreateEquip();
        byte typeArmor = player.getManufacture().getTypeArmorCreate();
        byte typeDamage = player.getManufacture().getTypeDamageCreate();
        short selectedItem = player.getManufacture().getSelectedItemCreate();
        if (typeArmor == -1 || selectedItem == -1 || typeClassChar == -1) {
            return;
        }
        short idItem = ManufactureConst.ITEM_ARMOR_CREATE[typeArmor][selectedItem];

        short minIdItem = ManufactureConst.ITEM_ARMOR_CREATE[typeClassChar][0];
        short maxIdItem = ManufactureConst.ITEM_ARMOR_CREATE[typeClassChar][ManufactureConst.ITEM_ARMOR_CREATE[typeClassChar].length
                - 1];
        byte maxLevelIdItem = (byte) (maxIdItem - minIdItem);
        byte levelIdItem = (byte) (idItem - minIdItem);

        short[] nguyenLieu = ManufactureConst.MATERIAL_CREATE_ARMOR[typeArmor][typeArmor >= ManufactureConst.AO
                && typeArmor <= ManufactureConst.NON ? selectedItem / 2 : selectedItem];
        byte typeNguyenLieu = player.getManufacture().getTypeNguyenLieuCreate();
        ItemEquipTemplate template = Manager.getItemEquipment(idItem);
        if (template == null) {
            return;
        }
        if (quantity.length > nguyenLieu.length / 2) {
            Service.instance.sendLogOut(player.getSession(), "Nguyên liệu không phù hợp");
            return;
        }
        List<ItemMineral> itemMinerals = player.getManufacture().getItemMineral();
        for (int i = 0; i < quantity.length; i++) {
            for (int j = 0; j < quantity[i].length; j++) {
                if (quantity[i][j] == nguyenLieu[i * 2 + 1]) {
                    short idMaterial = (short) (nguyenLieu[i * 2] + j);
                    ItemGem gem = typeNguyenLieu == 0 ? InventoryService.instance.findItemGem(player, idMaterial)
                            : typeNguyenLieu == 1 ? InventoryService.instance.findItemGemLock(player, idMaterial)
                                    : InventoryService.instance.findAllItemGem(player, idMaterial);
                    if (gem == null || gem.getQuantity() < nguyenLieu[i * 2 + 1]) {
                        Service.instance.sendLogOut(player.getSession(), "Không tìm thấy nguyên liệu");
                        sendManufactureArmor(player);
                        return;
                    }
                    ItemMineral item = new ItemMineral(idMaterial, (byte) (j + 1), nguyenLieu[i * 2 + 1]);
                    itemMinerals.add(item);
                }
            }
        }
        if (itemMinerals.size() != nguyenLieu.length / 2) {
            Service.instance.sendLogOut(player.getSession(), "Không đủ nguyên liệu");
            sendManufactureArmor(player);
            return;
        }
        boolean isLockItem = false;
        for (ItemMineral item : player.getManufacture().getItemMineral()) {
            if (item != null) {
                ItemGem gem = typeNguyenLieu == 0 ? InventoryService.instance.findItemGem(player, item.getIdTemplate())
                        : typeNguyenLieu == 1 ? InventoryService.instance.findItemGemLock(player, item.getIdTemplate())
                                : InventoryService.instance.findAllItemGem(player, item.getIdTemplate());
                if (!isLockItem && gem.isLock()) {
                    isLockItem = true;
                }
                InventoryService.instance.minusQuantityItemGem(player, gem, item.getQuantity());
            }
        }
        byte levelCaoCap = itemMinerals.stream().filter(it -> it != null && it.isCaoCap()).findFirst().orElse(null)
                .getLevel();
        byte levelSoCap = itemMinerals.stream().filter(it -> it != null && !it.isCaoCap()).findFirst().orElse(null)
                .getLevel();

        byte rankItem = getRankItem(levelSoCap);
        byte colorItem = getColorItem(levelCaoCap);

        ItemEquip itemAdd = ItemService.instance.createNewItemEquipment(idItem, typeClassChar);
        itemAdd.getItemAttributes().clear();
        itemAdd.setNameCharSeal(player.getName());
        itemAdd.setRank(rankItem);
        itemAdd.setColorName(colorItem);
        itemAdd.setHe((byte) Util.nextInt(Const.THUY, Const.KIM));
        itemAdd.setLock(isLockItem);
        itemAdd.setDamageType(typeDamage);
        short defPercent = (short) getValueAttribute(3, 20, levelSoCap, levelCaoCap);
        switch (typeArmor) {
            case ManufactureConst.AO, ManufactureConst.NON, ManufactureConst.GIAY, ManufactureConst.QUAN,
                    ManufactureConst.GANG -> {
                int rangeValueDef[] = getRangeByLevel(300, 1500, levelSoCap, maxLevelIdItem);
                short def = (short) Util.nextInt(rangeValueDef[0], rangeValueDef[1]);
                itemAdd.getItemAttributes().add(new Attribute(AttributeConst.THU_VAT, def));
                itemAdd.getItemAttributes().add(new Attribute(AttributeConst.THU_MA, def));
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.CHINH_XAC, (short) Util.nextInt(5, 80)));
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.CHI_MANG, (short) Util.nextInt(5, 80)));
                addSpecialAttribute(itemAdd, levelSoCap, levelCaoCap);
                if (typeDamage == ItemEquipConst.DAMAGE_PHYSIC) {
                    itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_MA, (short) 3));
                    itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_VAT, defPercent));
                } else if (typeDamage == ItemEquipConst.DAMAGE_MAGIC) {
                    itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_MA, defPercent));
                    itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_VAT, (short) 3));
                }
            }
            case ManufactureConst.NHAN -> {
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.TAN_CONG, (short) Util.nextInt(130, 150)));
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.CHINH_XAC, (short) Util.nextInt(5, 80)));
                addSpecialAttribute(itemAdd, levelSoCap, levelCaoCap);
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.TANG_CONG, (short) Util.nextInt(1, 10)));
            }
            case ManufactureConst.DAY_CHUYEN -> {
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.TAN_CONG, (short) Util.nextInt(130, 150)));
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.CHI_MANG, (short) Util.nextInt(5, 80)));
                addSpecialAttribute(itemAdd, levelSoCap, levelCaoCap);
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.TANG_CONG, (short) Util.nextInt(1, 10)));
            }
            case ManufactureConst.NGOC -> {
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.CHINH_XAC, (short) Util.nextInt(5, 80)));
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.CHI_MANG, (short) Util.nextInt(5, 80)));
                itemAdd.getItemAttributes()
                        .add(new Attribute(AttributeConst.SUC_KHOE, (short) Util.nextInt(150, 160)));
                addSpecialAttribute(itemAdd, levelSoCap, levelCaoCap);
                if (typeDamage == ItemEquipConst.DAMAGE_PHYSIC) {
                    itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_MA, (short) 3));
                    itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_VAT, defPercent));
                } else if (typeDamage == ItemEquipConst.DAMAGE_MAGIC) {
                    itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_MA, defPercent));
                    itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_THU_VAT, (short) 3));
                }
            }
        }
        itemAdd.subDefend();
        Service.instance.sendLogOut(player.getSession(), String.format("Tạo thành công %s %s %s",
                itemAdd.getTemplate().getName(), Util.getPham(rankItem), Util.getColor(colorItem)));
        InventoryService.instance.addItemBagEquipment(player, itemAdd);
        InventoryService.instance.sendItemBag(player);
        InventoryService.instance.sendItemGem(player);
        sendManufactureArmor(player);
    }

    public void manufactureWeapon(@NonNull Player player, byte[][] quantity) throws IOException {
        byte typeClassChar = player.getManufacture().getClassCharCreateEquip();
        short selectedItem = player.getManufacture().getSelectedItemCreate();
        if (typeClassChar == -1 || selectedItem == -1) {
            return;
        }
        short idItem = ManufactureConst.ITEM_WEAPON_CREATE[typeClassChar][selectedItem];

        short minIdItem = ManufactureConst.ITEM_WEAPON_CREATE[typeClassChar][0];
        short maxIdItem = ManufactureConst.ITEM_WEAPON_CREATE[typeClassChar][ManufactureConst.ITEM_WEAPON_CREATE[typeClassChar].length
                - 1];
        byte maxLevelIdItem = (byte) (maxIdItem - minIdItem);
        byte levelIdItem = (byte) (idItem - minIdItem);

        short[] nguyenLieu = ManufactureConst.MATERIAL_CREATE_WEAPON[typeClassChar][selectedItem];
        byte typeNguyenLieu = player.getManufacture().getTypeNguyenLieuCreate();
        ItemEquipTemplate template = Manager.getItemEquipment(idItem);
        if (template == null) {
            return;
        }
        if (quantity.length > nguyenLieu.length / 2) {
            Service.instance.sendLogOut(player.getSession(), "Nguyên liệu không phù hợp");
            return;
        }
        List<ItemMineral> itemMinerals = player.getManufacture().getItemMineral();
        for (int i = 0; i < quantity.length; i++) {
            for (int j = 0; j < quantity[i].length; j++) {
                if (quantity[i][j] == nguyenLieu[i * 2 + 1]) {
                    short idMaterial = (short) (nguyenLieu[i * 2] + j);
                    ItemGem gem = typeNguyenLieu == 0 ? InventoryService.instance.findItemGem(player, idMaterial)
                            : typeNguyenLieu == 1 ? InventoryService.instance.findItemGemLock(player, idMaterial)
                                    : InventoryService.instance.findAllItemGem(player, idMaterial);
                    if (gem == null || gem.getQuantity() < nguyenLieu[i * 2 + 1]) {
                        Service.instance.sendLogOut(player.getSession(), "Không tìm thấy nguyên liệu");
                        sendManufactureWeapon(player);
                        return;
                    }
                    ItemMineral item = new ItemMineral(idMaterial, (byte) (j + 1), nguyenLieu[i * 2 + 1]);
                    itemMinerals.add(item);
                }
            }
        }
        if (itemMinerals.size() != nguyenLieu.length / 2) {
            Service.instance.sendLogOut(player.getSession(), "Không đủ nguyên liệu");
            sendManufactureWeapon(player);
            return;
        }
        boolean isLockItem = false;
        for (ItemMineral item : player.getManufacture().getItemMineral()) {
            if (item != null) {
                ItemGem gem = typeNguyenLieu == 0 ? InventoryService.instance.findItemGem(player, item.getIdTemplate())
                        : typeNguyenLieu == 1 ? InventoryService.instance.findItemGemLock(player, item.getIdTemplate())
                                : InventoryService.instance.findAllItemGem(player, item.getIdTemplate());
                if (!isLockItem && gem.isLock()) {
                    isLockItem = true;
                }
                InventoryService.instance.minusQuantityItemGem(player, gem, item.getQuantity());
            }
        }
        byte levelCaoCap = itemMinerals.stream().filter(it -> it != null && it.isCaoCap()).findFirst().orElse(null)
                .getLevel();
        byte levelSoCap = itemMinerals.stream().filter(it -> it != null && !it.isCaoCap()).findFirst().orElse(null)
                .getLevel();

        byte rankItem = getRankItem(levelSoCap);
        byte colorItem = getColorItem(levelCaoCap);

        ItemEquip itemAdd = ItemService.instance.createNewItemEquipment(idItem, typeClassChar);
        itemAdd.getItemAttributes().clear();
        itemAdd.setNameCharSeal(player.getName());
        itemAdd.setRank(rankItem);
        itemAdd.setColorName(colorItem);
        itemAdd.setHe((byte) Util.nextInt(Const.THUY, Const.KIM));
        itemAdd.setLock(isLockItem);

        // Tăng tấn công // Tăng tấn công theo cấp độ vũ khí
        int rangeValueTanCong[] = getRangeByLevel(500, 2500, levelSoCap, maxLevelIdItem);
        int valueTanCong = getValueAttribute(rangeValueTanCong[0], rangeValueTanCong[1], levelSoCap, levelCaoCap);
        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TAN_CONG, (short) valueTanCong));

        // Tăng chính xác
        int valueChinhXac = getValueAttribute(5, 80, levelSoCap, levelCaoCap);
        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.CHINH_XAC, (short) valueChinhXac));
        // Tăng chí mạng
        int valueChiMang = getValueAttribute(5, 80, levelSoCap, levelCaoCap);
        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.CHI_MANG, (short) valueChiMang));
        // Tăng % công
        int valueTangCong = getValueAttribute(1, 10, levelSoCap, levelCaoCap);
        itemAdd.getItemAttributes().add(new Attribute(AttributeConst.TANG_CONG, (short) valueTangCong));

        // add special attribute
        addSpecialAttribute(itemAdd, levelSoCap, levelCaoCap);

        Service.instance.sendLogOut(player.getSession(), String.format("Tạo thành công %s %s %s",
                itemAdd.getTemplate().getName(), Util.getPham(rankItem), Util.getColor(colorItem)));
        InventoryService.instance.addItemBagEquipment(player, itemAdd);
        InventoryService.instance.sendItemBag(player);
        InventoryService.instance.sendItemGem(player);
        sendManufactureWeapon(player);
    }

    private void addSpecialAttribute(@NonNull ItemEquip item, byte levelSoCap, byte levelCaoCap) {
        if (item.getColorName() != ItemEquipConst.NONE_COLOR) {
            short valueAn = 1;
            short idAttribute = (short) Util.nextInt(AttributeConst.GIAM_ST_VAT, AttributeConst.PHAN_ST);
            if (idAttribute == AttributeConst.XUYEN_GIAP && levelSoCap == 1) {
                do {
                    idAttribute = (short) Util.nextInt(AttributeConst.GIAM_ST_VAT, AttributeConst.PHAN_ST);
                } while (idAttribute == AttributeConst.XUYEN_GIAP);
            }
            valueAn = (short) getValueAttribute(1, 10, levelSoCap, levelCaoCap);

            if (idAttribute == AttributeConst.XUYEN_GIAP) {
                valueAn = (short) (levelSoCap - 1);
            }
            item.getItemAttributes().add(new Attribute(idAttribute, valueAn));
        }

        item.getItemAttributes().add(new Attribute((short) Util.getOne(AttributeConst.TANG_HP, AttributeConst.TANG_MP),
                levelCaoCap == 6 ? 9 : levelCaoCap));
        short idRandom = ManufactureConst.ATTRIBUTE_DEFAULT_WEAPON[Util.nextInt(0,
                ManufactureConst.ATTRIBUTE_DEFAULT_WEAPON.length - 1)];
        item.getItemAttributes().add(new Attribute(idRandom, (short) Util.nextInt(10, 30)));
        if (Util.isTrue(60.9, 100.0)) {
            short idRandom2;
            do {
                idRandom2 = ManufactureConst.ATTRIBUTE_DEFAULT_WEAPON[Util.nextInt(0,
                        ManufactureConst.ATTRIBUTE_DEFAULT_WEAPON.length - 1)];
            } while (idRandom2 == idRandom);
            item.getItemAttributes()
                    .add(new Attribute(idRandom2, (short) (Util.nextInt(5, 10))));
        }
        item.getItemAttributes().add(
                new Attribute((short) Util.nextInt(AttributeConst.KY_NANG_1_CONG_THEM,
                        item.getClassChar() == Const.PHAP_SU ? AttributeConst.KY_NANG_15_CONG_THEM
                                : AttributeConst.KY_NANG_13_CONG_THEM),
                        (short) 1));

    }

    public void sendManufactureArmor(@NonNull Player player) throws IOException {
        byte typeArmor = player.getManufacture().getTypeArmorCreate();
        byte selectedItem = player.getManufacture().getSelectedItemCreate();
        short idItem = ManufactureConst.ITEM_ARMOR_CREATE[typeArmor][selectedItem];
        short[] nguyenLieu = ManufactureConst.MATERIAL_CREATE_ARMOR[typeArmor][typeArmor >= ManufactureConst.AO
                && typeArmor <= ManufactureConst.NON ? selectedItem / 2 : selectedItem];
        ItemEquipTemplate template = Manager.getItemEquipment(idItem);
        if (template == null) {
            return;
        }
        player.getManufacture().setNguyenLieuCreate(nguyenLieu);
        sendManufacture(player, ManufactureConst.CHE_TAO_GIAP, player.getManufacture().getTypeNguyenLieuCreate(),
                nguyenLieu, template);
    }

    public void sendManufactureWeapon(@NonNull Player player) throws IOException {
        short typeWeapon = player.getManufacture().getClassCharCreateEquip();
        short idItem = ManufactureConst.ITEM_WEAPON_CREATE[typeWeapon][player.getManufacture().getSelectedItemCreate()];
        short[] nguyenLieu = ManufactureConst.MATERIAL_CREATE_WEAPON[typeWeapon][player.getManufacture()
                .getSelectedItemCreate()];
        ItemEquipTemplate template = Manager.getItemEquipment(idItem);
        if (template == null) {
            return;
        }
        player.getManufacture().setNguyenLieuCreate(nguyenLieu);
        sendManufacture(player, ManufactureConst.CHE_TAO_VU_KHI, player.getManufacture().getTypeNguyenLieuCreate(),
                nguyenLieu, template);
    }

    public void sendManafactureAnimalArmor(@NonNull Player player) throws IOException {
        Message msg = new Message(CommandMessage.ANIMAL_COMBINED);
        msg.writer()
                .writeByte(player.getManufacture().getColorAnimalArmorCreate() == 3 ? 2
                        : player.getManufacture().getColorAnimalArmorCreate() == 2 ? 3
                                : player.getManufacture().getColorAnimalArmorCreate());
        msg.writer().writeByte(ManufactureConst.LEVEL_ANIMAL_ARMOR[player.getManufacture().getSelectedItemCreate()]);
        msg.writer().writeByte(
                ManufactureConst.QUANTITY_MATERIAL_ANIMAL_ARMOR[player.getManufacture().getSelectedItemCreate()]);
        msg.writer().writeShort(
                ManufactureConst.MATERIAL_CREATE_ANIMAL_ARMOR[player.getManufacture().getColorAnimalArmorCreate()]);
        msg.writer().writeByte(player.getManufacture().getTypeDamageCreate());
        msg.writer().writeByte(player.getManufacture().getTypeNguyenLieuCreate());
        player.getSession().sendMessage(msg);
    }

    private void sendManufacture(@NonNull Player player, byte typeCheDo, byte typeNguyenLieu, short[] nguyenLieu,
            ItemEquipTemplate itemTemplate) throws IOException {
        player.getManufacture().getItemMineral().clear();
        Message msg = new Message(CommandMessage.CHE_DO);
        msg.writer().writeUTF(itemTemplate.getName());
        msg.writer().writeShort(itemTemplate.getId());
        msg.writer().writeByte(nguyenLieu.length / 2);
        for (short i = 0; i < nguyenLieu.length / 2; i++) {
            GemTemplate gem = Manager.getGemTemplate(nguyenLieu[i * 2]);
            if (gem.getName().contains("cấp")) {
                msg.writer().writeUTF(gem.getName().substring(0, gem.getName().indexOf("cấp")));
            } else {
                msg.writer().writeUTF(gem.getName());
            }
            msg.writer().writeShort(gem.getId());
            msg.writer().writeByte(nguyenLieu[i * 2 + 1]);
        }
        msg.writer().writeByte(typeCheDo);
        msg.writer().writeByte(typeNguyenLieu);
        player.getSession().sendMessage(msg);
    }

    private int getValueAttribute(int minValue, int maxValue, byte levelSoCap, byte levelCaoCap) {
        short percentBuffLevelSoCap = 1; // Default buff level so cap 1%

        short rank = (short) Math.max(levelCaoCap, percentBuffLevelSoCap);

        switch (rank) {
            case ItemEquipConst.NONE_RANK -> percentBuffLevelSoCap = (short) Util.nextInt(1, 10);
            case ItemEquipConst.NGU_PHAM -> percentBuffLevelSoCap = (short) Util.nextInt(10, 20);
            case ItemEquipConst.TU_PHAM -> percentBuffLevelSoCap = (short) Util.nextInt(20, 40);
            case ItemEquipConst.TAM_PHAM -> percentBuffLevelSoCap = (short) Util.nextInt(40, 70);
            case ItemEquipConst.NHI_PHAM -> percentBuffLevelSoCap = (short) Util.getOne(80, 90);
            case ItemEquipConst.NHAT_PHAM -> percentBuffLevelSoCap = (short) Util.getOne(80, 100);
            default -> percentBuffLevelSoCap = (short) Util.getOne(80, 100);
        }
        int diffValue = maxValue - minValue;

        int value = minValue + diffValue * percentBuffLevelSoCap / 100;
        return value;
    }

    private byte getColorItem(short levelCaoCap) {
        byte colorItem = ItemEquipConst.NONE_COLOR;
        switch (levelCaoCap) {
            case 1, 2, 3 -> {
                colorItem = (byte) Util.getOne(ItemEquipConst.NONE_COLOR, ItemEquipConst.BLUE_COLOR);
            }
            case 4 -> {
                colorItem = (byte) Util.getOne(ItemEquipConst.BLUE_COLOR, ItemEquipConst.RED_COLOR);
            }
            case 5 -> {
                colorItem = (byte) Util.getOne(ItemEquipConst.BLUE_COLOR, ItemEquipConst.RED_COLOR);
                if (Util.isTrue(2.3, 1000.0)) {
                    colorItem = ItemEquipConst.GREEN_COLOR;
                }
            }
            default -> {
                colorItem = ItemEquipConst.GREEN_COLOR;
            }
        }
        return colorItem;
    }

    private byte getRankItem(short levelSoCap) {
        byte rankItem = ItemEquipConst.NGU_PHAM;
        switch (levelSoCap) {
            case 1, 2 -> {
                rankItem = ItemEquipConst.NGU_PHAM;
            }
            case 3 -> {
                rankItem = ItemEquipConst.TU_PHAM;
            }
            case 4 -> {
                rankItem = ItemEquipConst.TAM_PHAM;
            }
            case 5 -> {
                rankItem = ItemEquipConst.NHI_PHAM;
                if (Util.isTrue(2.3, 1000.0)) {
                    rankItem = ItemEquipConst.NHAT_PHAM;
                }
            }
            default -> {
                rankItem = ItemEquipConst.NHAT_PHAM;
            }
        }
        return rankItem;
    }

    private int[] getRangeByLevel(int minValue, int maxValue, byte level, byte maxLevel) {
        int baseValue = (maxValue - minValue) / maxLevel;
        int minBasevalue = baseValue * level;
        int maxBasevalue = baseValue * (level + 1);
        return new int[] { minValue + minBasevalue, minValue + maxBasevalue };
    }
}
