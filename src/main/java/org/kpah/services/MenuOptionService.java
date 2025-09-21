package org.kpah.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kpah.consts.CombineConst;
import org.kpah.consts.Const;
import org.kpah.consts.ItemEquipConst;
import org.kpah.consts.ManufactureConst;
import org.kpah.consts.NpcConst;
import org.kpah.item.Attribute;
import org.kpah.item.ItemAnimal;
import org.kpah.item.ItemEquip;
import org.kpah.item.ItemGem;
import org.kpah.item.ItemPotion;
import lombok.Cleanup;
import lombok.NonNull;
import org.kpah.manager.Manager;
import org.kpah.network.Message;
import org.kpah.player.Player;
import org.kpah.template.HoaTieuTemplate;
import org.kpah.utils.CommandMessage;
import org.kpah.utils.Util;

/**
 *
 * @author ☂️☂️Duy Coder 💖💖
 */
public class MenuOptionService {

    public static final MenuOptionService instance = new MenuOptionService();

    private static final byte MUA_TRANG_BI = 0;
    private static final byte SHOP_HAC_NGUU = 1;
    private static final byte THAY_NGU_HANH = 2;
    private static final byte THO_HOP_THANH_CAO_CAP = 3;
    private static final byte THO_HOP_THANH_SO_CAP = 4;
    private static final byte TONG_QUAN = 5;
    private static final byte THO_REN_THAN_BI = 6;
    private static final byte TONG_TIEU_DAU = 7;
    private static final byte DAU_TRUONG = 8;
    private static final byte TIEN_NU = 9;
    private static final byte HAO_DUYEN = 10;
    private static final byte TAO_THU = 11;
    private static final byte LUYEN_THU = 12;
    private static final byte LUYEN_THU_SPECIAL = 13;
    private static final byte CONG_DICH_CHUYEN = 14;
    private static final byte XA_PHU_NEW = 15;
    private static final byte HOA_TIEU_NEW = 16;
    private static final byte SELECT_HOA_TIEU_MAP = 17;
    private static final byte BANG_TOP = 18;
    private static final byte USE_NHAN = 19;
    private static final byte DOI_NGUYEN_LIEU_THUONG_SO_CAP = 20;
    private static final byte DOI_NGUYEN_LIEU_THUONG_KHOA_SO_CAP = 21;
    private static final byte DOI_NGOC_HUYEN_MINH = 22;
    private static final byte DOI_NGOC_HUYEN_MINH_KHOA = 23;
    private static final byte DOI_BOT = 24;
    private static final byte DOI_BOT_KHOA = 25;
    private static final byte DOI_NGUYEN_LIEU_THUONG_CAO_CAP = 26;
    private static final byte DOI_NGUYEN_LIEU_THUONG_KHOA_CAO_CAP = 27;
    private static final byte DOI_XUONG = 28;
    private static final byte DOI_XUONG_KHOA = 29;
    private static final byte CREATE_WEAPON = 30;
    private static final byte CREATE_ARMOR = 31;
    private static final byte CREATE_ANIMAL_ARMOR = 32;
    private static final byte SELECT_WEAPON_CREATE = 33;
    private static final byte SELECT_ARMOR_CREATE = 34;
    private static final byte SELECT_ANIMAL_ARMOR_CREATE = 35;
    private static final byte SELECT_NGUYEN_LIEU_CREATE = 36;
    private static final byte SELECT_CLASS_CREATE_ARMOR = 37;
    private static final byte SELECT_TYPE_ARMOR_CREATE = 38;
    private static final byte SELECT_COLOR_ANIMAL_ARMOR = 39;
    private static final byte DOI_HE_NGU_HANH = 40;
    private static final byte MENU_VONG_QUAY = 41;
    private static final byte VONG_QUAY_VIP = 42;
    private static final byte VONG_QUAY_THUONG = 43;

    public void onMenuOption(@NonNull Player player, byte idMenu, byte selected) throws IOException {
        if (selected < 0) {
            return;
        }
        if (player.isDie()) {
            return;
        }
        player.getSundry().setIdOpenMenu(idMenu);
        player.getSundry().setSelectedOption(selected);
        switch (idMenu) {
            case MENU_VONG_QUAY -> {
                switch (selected) {
                    case 0 ->
                        sendMenuVongQuayVip(player);
                    case 1 ->
                        sendMenuVongQuayThuong(player);
                }
            }
            case DOI_HE_NGU_HANH -> {
                if (!player.getInventory().minusLuong(100)) {
                    Service.instance.sendLogOut(player.getSession(), "Không đủ 100 lượng");
                    return;
                }
                InventoryService.instance.sendItemPotion(player);
                ItemEquip item = InventoryService.instance.findItemBody(player,
                        player.getSundry().getIdDoiHe().get(selected));
                if (item != null && item.getHe() != ItemEquipConst.NONE_HE) {
                    item.setHe((byte) Util.nextInt(ItemEquipConst.THUY, ItemEquipConst.KIM));
                    Service.instance.sendLogOut(player.getSession(), String.format("%s đã được đổi sang hệ %s",
                            item.getTemplate().getName(), ItemEquipConst.HE[item.getHe()]));
                    InventoryService.instance.sendItemBody(player);
                    player.getPoint().initPoint();
                    Service.instance.sendMainCharInfo(player);
                }
                player.getSundry().getIdDoiHe().clear();
            }
            case SELECT_COLOR_ANIMAL_ARMOR -> {
                player.getManufacture().setColorAnimalArmorCreate(selected);
                sendMenuSelectNguyenLieu(player);
            }
            case CREATE_ANIMAL_ARMOR -> {
                player.getManufacture().setTypeDamageCreate(
                        selected == 0 ? ItemEquipConst.DAMAGE_MAGIC : ItemEquipConst.DAMAGE_PHYSIC);
                sendMenuSelectColorAnimalArmor(player);
            }
            case SELECT_ANIMAL_ARMOR_CREATE -> {
                player.getManufacture().dispose();
                player.getManufacture().setTypeCheTao(ManufactureConst.CHE_TAO_GIAP_THU);
                player.getManufacture().setSelectedItemCreate(selected);
                sendMenuCreateTypeAnimalArmor(player);
            }
            case SELECT_ARMOR_CREATE -> {
                player.getManufacture().setSelectedItemCreate(selected);
                sendMenuSelectNguyenLieu(player);
            }
            case SELECT_TYPE_ARMOR_CREATE -> {
                player.getManufacture().setTypeArmorCreate(selected);
                sendMenuSelectArmorCreate(player);
            }
            case SELECT_CLASS_CREATE_ARMOR -> {
                player.getManufacture().setClassCharCreateEquip(selected);
                sendMenuTypeArmorCreate(player);
            }
            case CREATE_ARMOR -> {
                player.getManufacture().dispose();
                player.getManufacture().setTypeCheTao(ManufactureConst.CHE_TAO_GIAP);
                player.getManufacture().setTypeDamageCreate(
                        selected == 0 ? ItemEquipConst.DAMAGE_MAGIC : ItemEquipConst.DAMAGE_PHYSIC);
                sendMenuClassCreateArmor(player);
            }
            case SELECT_NGUYEN_LIEU_CREATE -> {
                player.getManufacture().setTypeNguyenLieuCreate(selected);
                switch (player.getManufacture().getTypeCheTao()) {
                    case ManufactureConst.CHE_TAO_GIAP ->
                        ManufactureService.instance.sendManufactureArmor(player);
                    case ManufactureConst.CHE_TAO_VU_KHI ->
                        ManufactureService.instance.sendManufactureWeapon(player);
                    case ManufactureConst.CHE_TAO_GIAP_THU ->
                        ManufactureService.instance.sendManafactureAnimalArmor(player);
                }
            }
            case SELECT_WEAPON_CREATE -> {
                player.getManufacture().setSelectedItemCreate(selected);
                sendMenuSelectNguyenLieu(player);
            }
            case CREATE_WEAPON -> {
                player.getManufacture().dispose();
                player.getManufacture().setTypeCheTao(ManufactureConst.CHE_TAO_VU_KHI);
                player.getManufacture().setClassCharCreateEquip(selected);
                sendMenuSelectWeapon(player, selected);
            }
            case DOI_NGUYEN_LIEU_THUONG_KHOA_CAO_CAP ->
                doiNguyenLieu(player, selected, true, CombineConst.DOI_NGUYEN_LIEU_CAO_CAP);
            case DOI_NGUYEN_LIEU_THUONG_CAO_CAP ->
                doiNguyenLieu(player, selected, false, CombineConst.DOI_NGUYEN_LIEU_CAO_CAP);
            case DOI_XUONG ->
                doiNguyenLieu(player, selected, false, CombineConst.DOI_XUONG);
            case DOI_XUONG_KHOA ->
                doiNguyenLieu(player, selected, true, CombineConst.DOI_XUONG);
            case DOI_NGUYEN_LIEU_THUONG_KHOA_SO_CAP ->
                doiNguyenLieu(player, selected, true, CombineConst.DOI_NGUYEN_LIEU_SO_CAP);
            case DOI_NGUYEN_LIEU_THUONG_SO_CAP ->
                doiNguyenLieu(player, selected, false, CombineConst.DOI_NGUYEN_LIEU_SO_CAP);
            case DOI_NGOC_HUYEN_MINH ->
                doiNguyenLieu(player, selected, false, CombineConst.DOI_NGOC_HUYEN_MINH);
            case DOI_NGOC_HUYEN_MINH_KHOA ->
                doiNguyenLieu(player, selected, true, CombineConst.DOI_NGOC_HUYEN_MINH);
            case DOI_BOT ->
                doiNguyenLieu(player, selected, false, CombineConst.DOI_BOT);
            case DOI_BOT_KHOA ->
                doiNguyenLieu(player, selected, true, CombineConst.DOI_BOT);
            case THO_HOP_THANH_SO_CAP -> {
                switch (selected) {
                    case 0 ->
                        sendMenuDoiNguyenLieuThuongSoCap(player);
                    case 1 ->
                        sendMenuDoiNguyenLieuThuongKhoaSoCap(player);
                    case 2 ->
                        sendMenuDoiNgocHuyenMinh(player);
                    case 3 ->
                        sendMenuDoiNgocHuyenMinhKhoa(player);
                    case 4 ->
                        sendMenuDoiBot(player);
                    case 5 ->
                        sendMenuDoiBotKhoa(player);
                }
            }
            case THO_HOP_THANH_CAO_CAP -> {
                switch (selected) {
                    case 0 ->
                        sendMenuDoiNguyenLieuThuongCaoCap(player);
                    case 1 ->
                        sendMenuDoiNguyenLieuThuongKhoaCaoCap(player);
                    case 2 ->
                        sendMenuDoiXuong(player);
                    case 3 ->
                        sendMenuDoiXuongKhoa(player);
                }
            }
            case THO_REN_THAN_BI -> {
                if (player.getInventory().isFullInventory()) {
                    ChatService.instance.sendChatOnlyMe(player, "Hành trang không đủ ô trống");
                    return;
                }
                switch (selected) {
                    case 0 ->
                        sendMenuCreateWeapon(player);
                    case 1 ->
                        sendMenuCreateArmor(player);
                    case 2 ->
                        sendMenuCreateAnimalArmor(player);
                }
            }
            case USE_NHAN -> {
                byte indexNhan = (byte) (selected == 0 ? 1 : 0);
                if (player.isDie()) {
                    return;
                }
                ItemEquip equipment = InventoryService.instance.findItemBag(player, player.getSundry().getIdItemNhan());
                if (equipment == null) {
                    return;
                }
                if (equipment.getTemplate().getGender() != 0
                        && equipment.getTemplate().getGender() != player.getInfo().getGender()) {
                    Service.instance.sendLogOut(player.getSession(), String.format("Vật phẩm này chỉ dành cho %s.",
                            (equipment.getTemplate().getGender() == Const.MALE ? "nam" : "nữ")));
                    return;
                }
                if (equipment.getClassChar() != -1 && equipment.getClassChar() != player.getInfo().getClassPlayer()) {
                    Service.instance.sendLogOut(player.getSession(), String.format("Vật phẩm này chỉ dành cho %s.",
                            Const.NAME_CLASS_CHAR[equipment.getClassChar()]));
                    return;
                }
                if (equipment.getLevel() > equipment.getLevel()) {
                    Service.instance.sendLogOut(player.getSession(),
                            String.format("Bạn phải đạt cấp %s để có thể dùng.", equipment.getLevel()));
                    return;
                }
                if (equipment.getTemplate().getType() == 8) {
                    ItemEquip hasEquipment = InventoryService.instance.findItemBodyByViTri(player, indexNhan);
                    equipment.setViTriVe(indexNhan);
                    if (hasEquipment != null) {
                        InventoryService.instance.swapItemBagToBody(player, equipment, hasEquipment);
                    } else {
                        InventoryService.instance.removeItemBagEquipment(player, equipment);
                        InventoryService.instance.addItemBodyEquipment(player, equipment);
                    }
                    InventoryService.instance.sendItemBag(player);
                    InventoryService.instance.sendItemBody(player);
                    player.getPoint().initPoint();
                    MapService.instance.onNewHpMp(player);
                }
            }
            case THAY_NGU_HANH -> {
                switch (selected) {
                    case 0 ->
                        sendMenuDoiHeNguHanh(player);
                    case 3 ->
                        sendMenuVongQuay(player);
                    case 4 ->
                        InventoryService.instance.sendItemSold(player);
                }
            }
            case MUA_TRANG_BI -> {
                switch (player.getSundry().getIdNpcOpen()) {
                    case NpcConst.HAC_NGUU ->
                        ShopService.instance.openNpcShop(player, "WEAPON_ANIMAL", selected);
                    case NpcConst.THIET_BI ->
                        ShopService.instance.openNpcShop(player, "ITEM_EQUIPMENT", selected);
                    case NpcConst.GIAP_SU ->
                        ShopService.instance.openNpcShop(player, "GIAP_SU", selected);
                }
            }
            case SHOP_HAC_NGUU -> {
                switch (selected) {
                    case 0 ->
                        ShopService.instance.openNpcShop(player, "WEAPON", ItemEquipConst.DAMAGE_NONE);
                    case 1 ->
                        sendOptionBuyItem(player);
                }
            }
            case HAO_DUYEN -> {
                switch (selected) {
                    case 0 ->
                        sendMenuTaoThu(player);
                    case 1 ->
                        sendMenuLuyenThu(player);
                    case 2 ->
                        upgradeAnimal(player);
                }
            }
            case TAO_THU -> {
                short idItem = -1;
                switch (selected) {
                    case 0, 1, 2, 3, 4 ->
                        idItem = (short) (64 + selected);
                    case 5 ->
                        idItem = 86;
                    case 6 ->
                        idItem = 70;
                    case 7 ->
                        idItem = 74;
                    case 8 ->
                        idItem = 72;
                    case 9 ->
                        idItem = 115;
                }
                if (idItem == -1) {
                    return;
                }
                ItemPotion trung = InventoryService.instance.findItemPotion(player, idItem);
                if (trung == null) {
                    Service.instance.sendLogOut(player.getSession(), String.format("Không tìm thấy trứng %s", Util
                            .capitalizeFirstLetter(Manager.getPotionTemplate(idItem).getName().replace("Trứng ", ""))));
                    return;
                }
                createAnimal(player, idItem);
                InventoryService.instance.minusQuantityItemPotion(player, trung, (short) 1);
                InventoryService.instance.sendItemPotion(player);
                InventoryService.instance.sendItemAnimal(player);
                Service.instance.sendLogOut(player.getSession(), String.format("Tạo thành công linh thú %s",
                        Util.capitalizeFirstLetter(trung.getTemplate().getName().replace("Trứng ", ""))));
            }
            case LUYEN_THU -> {
                switch (selected) {
                    case 0 ->
                        upgradeNormal(player);
                    case 1 ->
                        upgradeTiemNang(player);
                    case 2 ->
                        sendMenuLuyenThuSpecial(player);
                }
            }
            case LUYEN_THU_SPECIAL -> {
                switch (selected) {
                    case 0 ->
                        changeSpecialAttribute(player);
                    case 1 ->
                        upgradeSpecialAttribute(player);
                }
            }
            case CONG_DICH_CHUYEN -> {
                switch (selected) {
                    case 0 -> {
                        byte countryGo = player.getLocation().getInCountry() == Const.THANH_LONG ? Const.HAC_HO
                                : Const.THANH_LONG;
                        player.getLocation().setInCountry(countryGo);
                        ChangeMapService.instance.changeMap(player, (short) 118);
                        MapService.instance.sendLocationServer(player);
                        Service.instance.sendMainCharInfo(player);
                    }
                    case 1 ->
                        ChangeMapService.instance.changeMap(player, (short) 9, (short) 280, (short) 1464);
                }
            }
            case XA_PHU_NEW -> {
                switch (selected) {
                    case 0 -> {
                        if (player.getLocation().getInCountry() != player.getInfo().getIdNation()) {
                            player.getLocation().setInCountry(player.getInfo().getIdNation());
                            MapService.instance.sendLocationServer(player);
                            Service.instance.sendMainCharInfo(player);
                        }
                        ChangeMapService.instance.changeMap(player, player.getLocation().getMapVillage(), (short) 384,
                                (short) 672);
                    }
                    case 1 -> {
                        if (player.getLocation().getInCountry() != player.getInfo().getIdNation()) {
                            player.getLocation().setInCountry(player.getInfo().getIdNation());
                            MapService.instance.sendLocationServer(player);
                            Service.instance.sendMainCharInfo(player);
                        }
                        ChangeMapService.instance.changeMap(player,
                                (short) (player.getInfo().getIdNation() == Const.THANH_LONG ? 1701 : 301), (short) 384,
                                (short) 672);
                    }
                }
            }
            case HOA_TIEU_NEW -> {
                byte idHoaTieu = player.getSundry().getIdHoaTieu();
                HoaTieuTemplate template = Manager.getHoaTieuTemplate(idHoaTieu);
                if (template == null) {
                    return;
                }
                sendMenuHoaTieuMap(player, template, selected);
            }
            case SELECT_HOA_TIEU_MAP -> {
                byte idHoaTieu = player.getSundry().getIdHoaTieu();
                HoaTieuTemplate template = Manager.getHoaTieuTemplate(idHoaTieu);
                if (template == null) {
                    return;
                }
                short mapId = template.getMapId()[player.getSundry().getIndexHoaTieu()][selected];
                short x = template.getX()[player.getSundry().getIndexHoaTieu()];
                short y = template.getY()[player.getSundry().getIndexHoaTieu()];
                ChangeMapService.instance.changeMap(player, mapId, x, y);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Send Menu">
    public void sendMenuVongQuayVip(@NonNull Player player) throws IOException {
        sendOptionMenu(player, VONG_QUAY_VIP, "Thông tin", "Tham gia", "Hướng dẫn");
    }

    public void sendMenuVongQuayThuong(@NonNull Player player) throws IOException {
        sendOptionMenu(player, VONG_QUAY_THUONG, "Thông tin", "Tham gia", "Hướng dẫn");
    }

    public void sendMenuVongQuay(@NonNull Player player) throws IOException {
        sendOptionMenu(player, MENU_VONG_QUAY, "Vòng quay", "Vòng quay thường");
    }

    public void sendMenuDoiHeNguHanh(@NonNull Player player) throws IOException {
        @Cleanup("clear")
        List<String> name = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getItemBody().size(); i++) {
            ItemEquip item = player.getInventory().getItemBody().get(i);
            if (item != null && item.getHe() != ItemEquipConst.NONE_HE) {
                StringBuilder s = new StringBuilder(item.getTemplate().getName());
                s.append(" hệ ").append(ItemEquipConst.HE[item.getHe()]).append(" 100L");
                name.add(s.toString());
                player.getSundry().getIdDoiHe().add(item.getIdItem());
            }
        }
        sendOptionMenu(player, DOI_HE_NGU_HANH, name.toArray(String[]::new));
    }

    public void sendMenuSelectArmorCreate(@NonNull Player player) throws IOException {
        switch (player.getManufacture().getTypeArmorCreate()) {
            case ManufactureConst.AO ->
                sendOptionMenu(player, SELECT_ARMOR_CREATE, "Áo Nữ lv20(Da mềm 1 Da cứng 1)",
                        "Áo Nam lv20(Da mềm 1 Da cứng 1)", "Áo Nữ lv25(Da mềm 2 Da cứng 1)",
                        "Áo Nam lv25(Da mềm 2 Da cứng 1)", "Áo Nữ lv30(Da mềm 3 Da cứng 1)",
                        "Áo Nam lv30(Da mềm 3 Da cứng 1)", "Áo Nữ lv35(Da mềm 4 Da cứng 2)",
                        "Áo Nam lv35(Da mềm 4 Da cứng 2)", "Áo Nữ lv40(Da mềm 4 Da cứng 3)",
                        "Áo Nam lv40(Da mềm 4 Da cứng 3)", "Áo Nữ lv45(Da mềm 5 Da cứng 3)",
                        "Áo Nam lv45(Da mềm 5 Da cứng 3)", "Áo Nữ lv50(Da mềm 5 Da cứng 4)",
                        "Áo Nam lv50(Da mềm 5 Da cứng 4)", "Áo Nữ lv55(Da mềm 5 Da cứng 4)",
                        "Áo Nam lv55(Da mềm 5 Da cứng 4)", "Áo Nữ lv60(Da mềm 5 Da cứng 5)",
                        "Áo Nam lv60(Da mềm 5 Da cứng 5)", "Áo Nữ lv65(Da mềm 7 Da cứng 7)",
                        "Áo Nam lv65(Da mềm 7 Da cứng 7)", "Áo Nữ lv70(Da mềm 9 Da cứng 9)",
                        "Áo Nam lv70(Da mềm 9 Da cứng 9)", "Áo Nữ lv75(Da mềm 11 Da cứng 11)",
                        "Áo Nam lv75(Da mềm 11 Da cứng 11)", "Áo Nữ lv80(Da mềm 13 Da cứng 13)",
                        "Áo Nam lv80(Da mềm 13 Da cứng 13)");
            case ManufactureConst.QUAN ->
                sendOptionMenu(player, SELECT_ARMOR_CREATE, "Quần Nữ lv20(Vải 1 Tơ lụa 1)",
                        "Quần Nam lv20(Vải 1 Tơ lụa 1)", "Quần Nữ lv25(Vải 2 Tơ lụa 1)",
                        "Quần Nam lv25(Vải 2 Tơ lụa 1)", "Quần Nữ lv30(Vải 3 Tơ lụa 1)",
                        "Quần Nam lv30(Vải 3 Tơ lụa 1)", "Quần Nữ lv35(Vải 4 Tơ lụa 2)",
                        "Quần Nam lv35(Vải 4 Tơ lụa 2)", "Quần Nữ lv40(Vải 4 Tơ lụa 3)",
                        "Quần Nam lv40(Vải 4 Tơ lụa 3)", "Quần Nữ lv45(Vải 5 Tơ lụa 3)",
                        "Quần Nam lv45(Vải 5 Tơ lụa 3)", "Quần Nữ lv50(Vải 5 Tơ lụa 4)",
                        "Quần Nam lv50(Vải 5 Tơ lụa 4)", "Quần Nữ lv55(Vải 5 Tơ lụa 4)",
                        "Quần Nam lv55(Vải 5 Tơ lụa 4)", "Quần Nữ lv60(Vải 5 Tơ lụa 5)",
                        "Quần Nam lv60(Vải 5 Tơ lụa 5)", "Quần Nữ lv65(Vải 7 Tơ lụa 7)",
                        "Quần Nam lv65(Vải 7 Tơ lụa 7)", "Quần Nữ lv70(Vải 9 Tơ lụa 9)",
                        "Quần Nam lv70(Vải 9 Tơ lụa 9)", "Quần Nữ lv75(Vải 11 Tơ lụa 11)",
                        "Quần Nam lv75(Vải 11 Tơ lụa 11)", "Quần Nữ lv80(Vải 13 Tơ lụa 13)",
                        "Quần Nam lv80(Vải 13 Tơ lụa 13)");
            case ManufactureConst.NON ->
                sendOptionMenu(player, SELECT_ARMOR_CREATE, "Nón Nữ lv20(Da mềm 1 Da cứng 1)",
                        "Nón Nam lv20(Da mềm 1 Da cứng 1)", "Nón Nữ lv25(Da mềm 2 Da cứng 1)",
                        "Nón Nam lv25(Da mềm 2 Da cứng 1)", "Nón Nữ lv30(Da mềm 3 Da cứng 1)",
                        "Nón Nam lv30(Da mềm 3 Da cứng 1)", "Nón Nữ lv35(Da mềm 4 Da cứng 2)",
                        "Nón Nam lv35(Da mềm 4 Da cứng 2)", "Nón Nữ lv40(Da mềm 4 Da cứng 3)",
                        "Nón Nam lv40(Da mềm 4 Da cứng 3)", "Nón Nữ lv45(Da mềm 5 Da cứng 3)",
                        "Nón Nam lv45(Da mềm 5 Da cứng 3)", "Nón Nữ lv50(Da mềm 5 Da cứng 4)",
                        "Nón Nam lv50(Da mềm 5 Da cứng 4)", "Nón Nữ lv55(Da mềm 5 Da cứng 4)",
                        "Nón Nam lv55(Da mềm 5 Da cứng 4)", "Nón Nữ lv60(Da mềm 5 Da cứng 5)",
                        "Nón Nam lv60(Da mềm 5 Da cứng 5)", "Nón Nữ lv65(Da mềm 7 Da cứng 7)",
                        "Nón Nam lv65(Da mềm 7 Da cứng 7)", "Nón Nữ lv70(Da mềm 9 Da cứng 9)",
                        "Nón Nam lv70(Da mềm 9 Da cứng 9)", "Nón Nữ lv75(Da mềm 11 Da cứng 11)",
                        "Nón Nam lv75(Da mềm 11 Da cứng 11)", "Nón Nữ lv80(Da mềm 13 Da cứng 13)",
                        "Nón Nam lv80(Da mềm 13 Da cứng 13)");
            case ManufactureConst.GIAY ->
                sendOptionMenu(player, SELECT_ARMOR_CREATE, "Giày lv20(Da mềm 1 Da cứng 1)",
                        "Giày lv25(Da mềm 2 Da cứng 1)", "Giày lv30(Da mềm 3 Da cứng 1)",
                        "Giày lv35(Da mềm 4 Da cứng 2)", "Giày lv40(Da mềm 4 Da cứng 3)",
                        "Giày lv45(Da mềm 5 Da cứng 3)", "Giày lv50(Da mềm 5 Da cứng 4)",
                        "Giày lv55(Da mềm 5 Da cứng 4)", "Giày lv60(Da mềm 5 Da cứng 5)",
                        "Giày lv65(Da mềm 7 Da cứng 7)", "Giày lv70(Da mềm 9 Da cứng 9)",
                        "Giày lv75(Da mềm 11 Da cứng 11)", "Giày lv80(Da mềm 13 Da cứng 13)");
            case ManufactureConst.GANG ->
                sendOptionMenu(player, SELECT_ARMOR_CREATE, "Găng lv20(Vải 1 Tơ lụa 1)", "Găng lv25(Vải 2 Tơ lụa 1)",
                        "Găng lv30(Vải 3 Tơ lụa 1)", "Găng lv35(Vải 4 Tơ lụa 2)", "Găng lv40(Vải 4 Tơ lụa 3)",
                        "Găng lv45(Vải 5 Tơ lụa 3)", "Găng lv50(Vải 5 Tơ lụa 4)", "Găng lv55(Vải 5 Tơ lụa 4)",
                        "Găng lv60(Vải 5 Tơ lụa 5)", "Găng lv65(Vải 7 Tơ lụa 7)", "Găng lv70(Vải 9 Tơ lụa 9)",
                        "Găng lv75(Vải 11 Tơ lụa 11)", "Găng lv80(Vải 13 Tơ lụa 13)");
            case ManufactureConst.NHAN ->
                sendOptionMenu(player, SELECT_ARMOR_CREATE, "Nhẫn lv20(Ngọc 1 Thuỷ tinh 1)",
                        "Nhẫn lv25(Ngọc 2 Thuỷ tinh 1)", "Nhẫn lv30(Ngọc 3 Thuỷ tinh 1)",
                        "Nhẫn lv35(Ngọc 4 Thuỷ tinh 2)", "Nhẫn lv40(Ngọc 4 Thuỷ tinh 3)",
                        "Nhẫn lv45(Ngọc 5 Thuỷ tinh 3)", "Nhẫn lv50(Ngọc 5 Thuỷ tinh 4)",
                        "Nhẫn lv55(Ngọc 5 Thuỷ tinh 4)", "Nhẫn lv60(Ngọc 5 Thuỷ tinh 5)",
                        "Nhẫn lv65(Ngọc 7 Thuỷ tinh 7)", "Nhẫn lv70(Ngọc 9 Thuỷ tinh 9)",
                        "Nhẫn lv75(Ngọc 11 Thuỷ tinh 11)", "Nhẫn lv80(Ngọc 13 Thuỷ tinh 13)");
            case ManufactureConst.DAY_CHUYEN ->
                sendOptionMenu(player, SELECT_ARMOR_CREATE, "Dây chuyền lv20(Ngọc 1 Thuỷ tinh 1)",
                        "Dây chuyền lv25(Ngọc 2 Thuỷ tinh 1)", "Dây chuyền lv30(Ngọc 3 Thuỷ tinh 1)",
                        "Dây chuyền lv35(Ngọc 4 Thuỷ tinh 2)", "Dây chuyền lv40(Ngọc 4 Thuỷ tinh 3)",
                        "Dây chuyền lv45(Ngọc 5 Thuỷ tinh 3)", "Dây chuyền lv50(Ngọc 5 Thuỷ tinh 4)",
                        "Dây chuyền lv55(Ngọc 5 Thuỷ tinh 4)", "Dây chuyền lv60(Ngọc 5 Thuỷ tinh 5)",
                        "Dây chuyền lv65(Ngọc 7 Thuỷ tinh 7)", "Dây chuyền lv70(Ngọc 9 Thuỷ tinh 9)",
                        "Dây chuyền lv75(Ngọc 11 Thuỷ tinh 11)", "Dây chuyền lv80(Ngọc 13 Thuỷ tinh 13)");
            case ManufactureConst.NGOC ->
                sendOptionMenu(player, SELECT_ARMOR_CREATE, "Ngọc lv20(Ngọc 1 Thuỷ tinh 1)",
                        "Ngọc lv25(Ngọc 2 Thuỷ tinh 1)", "Ngọc lv30(Ngọc 3 Thuỷ tinh 1)",
                        "Ngọc lv35(Ngọc 4 Thuỷ tinh 2)", "Ngọc lv40(Ngọc 4 Thuỷ tinh 3)",
                        "Ngọc lv45(Ngọc 5 Thuỷ tinh 3)", "Ngọc lv50(Ngọc 5 Thuỷ tinh 4)",
                        "Ngọc lv55(Ngọc 5 Thuỷ tinh 4)", "Ngọc lv60(Ngọc 5 Thuỷ tinh 5)",
                        "Ngọc lv65(Ngọc 7 Thuỷ tinh 7)", "Ngọc lv70(Ngọc 9 Thuỷ tinh 9)",
                        "Ngọc lv75(Ngọc 11 Thuỷ tinh 11)", "Ngọc lv80(Ngọc 13 Thuỷ tinh 13)");
        }
    }

    public void sendMenuTypeArmorCreate(@NonNull Player player) throws IOException {
        String plus = String.format("%s %s", Const.DAMAGE_TYPE[player.getManufacture().getTypeDamageCreate()],
                Const.NAME_CLASS_CHAR[player.getManufacture().getClassCharCreateEquip()]);
        sendOptionMenu(player, SELECT_TYPE_ARMOR_CREATE, String.format("Áo %s", plus), String.format("Quần %s", plus),
                String.format("Nón %s", plus), String.format("Giày %s", plus), String.format("Găng %s", plus),
                String.format("Nhẫn %s", plus), String.format("Dây chuyền %s", plus), String.format("Ngọc %s", plus));
    }

    public void sendMenuClassCreateArmor(@NonNull Player player) throws IOException {
        sendOptionMenu(player, SELECT_CLASS_CREATE_ARMOR, "Kiếm khách", "Chiến binh", "Pháp sư", "Đấu sĩ", "Cung thủ");
    }

    public void sendMenuSelectNguyenLieu(@NonNull Player player) throws IOException {
        sendOptionMenu(player, SELECT_NGUYEN_LIEU_CREATE, "Nguyên liệu thường", "Nguyên liệu khóa",
                "Nguyên liệu tổng hợp");
    }

    public void sendMenuSelectWeapon(@NonNull Player player, byte classChar) throws IOException {
        switch (classChar) {
            case Const.KIEM_KHACH ->
                sendOptionMenu(player, SELECT_WEAPON_CREATE, "Kiếm cấp độ 21(Gỗ thường 1 Bạc 1)",
                        "Kiếm cấp độ 26(Gỗ thường 2 Bạc 1)",
                        "Kiếm cấp độ 31(Gỗ thường 3 Bạc 1)", "Kiếm cấp độ 36(Gỗ thường 4 Bạc 2)",
                        "Kiếm cấp độ 41(Gỗ thường 4 Bạc 3)", "Kiếm cấp độ 46(Gỗ thường 5 Bạc 3)",
                        "Kiếm cấp độ 51(Gỗ thường 5 Bạc 4)", "Kiếm cấp độ 56(Gỗ thường 5 Bạc 4)",
                        "Kiếm cấp độ 61(Gỗ thường 5 Bạc 5)", "Kiếm cấp độ 66(Gỗ thường 7 Bạc 7)",
                        "Kiếm cấp độ 71(Gỗ thường 9 Bạc 9)", "Kiếm cấp độ 76(Gỗ thường 11 Bạc 11)",
                        "Kiếm cấp độ 81(Gỗ thường 13 Bạc 13)");
            case Const.CHIEN_BINH ->
                sendOptionMenu(player, SELECT_WEAPON_CREATE, "Đại đao cấp độ 21(Gỗ thường 1 Bạc 1)",
                        "Đại đao cấp độ 26(Gỗ thường 2 Bạc 1)",
                        "Đại đao cấp độ 31(Gỗ thường 3 Bạc 1)", "Đại đao cấp độ 36(Gỗ thường 4 Bạc 2)",
                        "Đại đao cấp độ 41(Gỗ thường 4 Bạc 3)", "Đại đao cấp độ 46(Gỗ thường 5 Bạc 3)",
                        "Đại đao cấp độ 51(Gỗ thường 5 Bạc 4)", "Đại đao cấp độ 56(Gỗ thường 5 Bạc 4)",
                        "Đại đao cấp độ 61(Gỗ thường 5 Bạc 5)", "Đại đao cấp độ 66(Gỗ thường 7 Bạc 7)",
                        "Đại đao cấp độ 71(Gỗ thường 9 Bạc 9)", "Đại đao cấp độ 76(Gỗ thường 11 Bạc 11)",
                        "Đại đao cấp độ 81(Gỗ thường 13 Bạc 13)");
            case Const.PHAP_SU ->
                sendOptionMenu(player, SELECT_WEAPON_CREATE, "Bút cấp độ 21(Sắt 1 Gỗ sưa 1)",
                        "Bút cấp độ 26(Sắt 2 Gỗ sưa 1)",
                        "Bút cấp độ 31(Sắt 3 Gỗ sưa 1)", "Bút cấp độ 36(Sắt 4 Gỗ sưa 2)",
                        "Bút cấp độ 41(Sắt 4 Gỗ sưa 3)", "Bút cấp độ 46(Sắt 5 Gỗ sưa 3)",
                        "Bút cấp độ 51(Sắt 5 Gỗ sưa 4)", "Bút cấp độ 56(Sắt 5 Gỗ sưa 4)",
                        "Bút cấp độ 61(Sắt 5 Gỗ sưa 5)", "Bút cấp độ 66(Sắt 7 Gỗ sưa 7)",
                        "Bút cấp độ 71(Sắt 9 Gỗ sưa 9)", "Bút cấp độ 76(Sắt 11 Gỗ sưa 11)",
                        "Bút cấp độ 81(Sắt 13 Gỗ sưa 13)");
            case Const.DAU_SI ->
                sendOptionMenu(player, SELECT_WEAPON_CREATE, "Búa cấp độ 21(Gỗ thường 1 Bạc 1)",
                        "Búa cấp độ 26(Gỗ thường 2 Bạc 1)",
                        "Búa cấp độ 31(Gỗ thường 3 Bạc 1)", "Búa cấp độ 36(Gỗ thường 4 Bạc 2)",
                        "Búa cấp độ 41(Gỗ thường 4 Bạc 3)", "Búa cấp độ 46(Gỗ thường 5 Bạc 3)",
                        "Búa cấp độ 51(Gỗ thường 5 Bạc 4)", "Búa cấp độ 56(Gỗ thường 5 Bạc 4)",
                        "Búa cấp độ 61(Gỗ thường 5 Bạc 5)", "Búa cấp độ 66(Gỗ thường 7 Bạc 7)",
                        "Búa cấp độ 71(Gỗ thường 9 Bạc 9)", "Búa cấp độ 76(Gỗ thường 11 Bạc 11)",
                        "Búa cấp độ 81(Gỗ thường 13 Bạc 13)");

            case Const.CUNG_THU ->
                sendOptionMenu(player, SELECT_WEAPON_CREATE, "Cung cấp độ 21(Sắt 1 Gỗ sưa 1)",
                        "Cung cấp độ 26(Sắt 2 Gỗ sưa 1)",
                        "Cung cấp độ 31(Sắt 3 Gỗ sưa 1)", "Cung cấp độ 36(Sắt 4 Gỗ sưa 2)",
                        "Cung cấp độ 41(Sắt 4 Gỗ sưa 3)", "Cung cấp độ 46(Sắt 5 Gỗ sưa 3)",
                        "Cung cấp độ 51(Sắt 5 Gỗ sưa 4)", "Cung cấp độ 56(Sắt 5 Gỗ sưa 4)",
                        "Cung cấp độ 61(Sắt 5 Gỗ sưa 5)", "Cung cấp độ 66(Sắt 7 Gỗ sưa 7)",
                        "Cung cấp độ 71(Sắt 9 Gỗ sưa 9)", "Cung cấp độ 76(Sắt 11 Gỗ sưa 11)",
                        "Cung cấp độ 81(Sắt 13 Gỗ sưa 13)");
        }
    }

    public void sendMenuCreateWeapon(@NonNull Player player) throws IOException {
        sendOptionMenu(player, CREATE_WEAPON, "Kiếm", "Đại đao", "Bút", "Búa", "Cung");
    }

    public void sendMenuCreateArmor(@NonNull Player player) throws IOException {
        sendOptionMenu(player, CREATE_ARMOR, "Trang bị ma pháp", "Trang bị vật lý");
    }

    public void sendMenuCreateTypeAnimalArmor(@NonNull Player player) throws IOException {
        sendOptionMenu(player, CREATE_ANIMAL_ARMOR, "Ma pháp", "Vật lý");
    }

    public void sendMenuCreateAnimalArmor(@NonNull Player player) throws IOException {
        sendOptionMenu(player, SELECT_ANIMAL_ARMOR_CREATE, "Cấp 30", "Cấp 35", "Cấp 40", "Cấp 45", "Cấp 50", "Cấp 55",
                "Cấp 60", "Cấp 65", "Cấp 70", "Cấp 75", "Cấp 80");
    }

    public void sendMenuDoiNguyenLieuThuongCaoCap(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_NGUYEN_LIEU_THUONG_CAO_CAP, "Tơ lụa 1", "Tơ lụa 2", "Tơ lụa 3", "Tơ lụa 4",
                "Tơ lụa 5", "Tơ lụa 6",
                "Bạc cấp 1", "Bạc cấp 2", "Bạc cấp 3", "Bạc cấp 4", "Bạc cấp 5", "Bạc cấp 6",
                "Thủy tinh cấp 1", "Thủy tinh cấp 2", "Thủy tinh cấp 3", "Thủy tinh cấp 4", "Thủy tinh cấp 5",
                "Thủy tinh cấp 6",
                "Gỗ sưa cấp 1", "Gỗ sưa cấp 2", "Gỗ sưa cấp 3", "Gỗ sưa cấp 4", "Gỗ sưa cấp 5", "Gỗ sưa cấp 6",
                "Da cứng cấp 1", "Da cứng cấp 2", "Da cứng cấp 3", "Da cứng cấp 4", "Da cứng cấp 5", "Da cứng cấp 6");
    }

    public void sendMenuDoiNguyenLieuThuongKhoaCaoCap(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_NGUYEN_LIEU_THUONG_KHOA_CAO_CAP, "Tơ lụa 1 khóa", "Tơ lụa 2 khóa", "Tơ lụa 3 khóa",
                "Tơ lụa 4 khóa", "Tơ lụa 5 khóa", "Tơ lụa 6 khóa",
                "Bạc cấp 1 khóa", "Bạc cấp 2 khóa", "Bạc cấp 3 khóa", "Bạc cấp 4 khóa", "Bạc cấp 5 khóa",
                "Bạc cấp 6 khóa",
                "Thủy tinh cấp 1 khóa", "Thủy tinh cấp 2 khóa", "Thủy tinh cấp 3 khóa", "Thủy tinh cấp 4 khóa",
                "Thủy tinh cấp 5 khóa", "Thủy tinh cấp 6 khóa",
                "Gỗ sưa cấp 1 khóa", "Gỗ sưa cấp 2 khóa", "Gỗ sưa cấp 3 khóa", "Gỗ sưa cấp 4 khóa", "Gỗ sưa cấp 5 khóa",
                "Gỗ sưa cấp 6 khóa",
                "Da cứng cấp 1 khóa", "Da cứng cấp 2 khóa", "Da cứng cấp 3 khóa", "Da cứng cấp 4 khóa",
                "Da cứng cấp 5 khóa", "Da cứng cấp 6 khóa");
    }

    public void sendMenuDoiXuong(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_XUONG, "Xương cấp 2", "Xương cấp 3", "Xương cấp 4", "Xương cấp 5", "Xương cấp 6");
    }

    public void sendMenuDoiXuongKhoa(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_XUONG_KHOA, "Xương cấp 2 khóa", "Xương cấp 3 khóa", "Xương cấp 4 khóa",
                "Xương cấp 5 khóa", "Xương cấp 6 khóa");
    }

    public void sendMenuDoiNguyenLieuThuongSoCap(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_NGUYEN_LIEU_THUONG_SO_CAP, "Vải", "Vải 2", "Vải 3", "Vải 4", "Vải 5", "Vải 6",
                "Sắt cấp 1", "Sắt cấp 2", "Sắt cấp 3", "Sắt cấp 4", "Sắt cấp 5", "Sắt cấp 6",
                "Ngọc cấp 1", "Ngọc cấp 2", "Ngọc cấp 3", "Ngọc cấp 4", "Ngọc cấp 5", "Ngọc cấp 6",
                "Gỗ thường cấp 1", "Gỗ thường cấp 2", "Gỗ thường cấp 3", "Gỗ thường cấp 4", "Gỗ thường cấp 5",
                "Gỗ thường cấp 6",
                "Da mềm cấp 1", "Da mềm cấp 2", "Da mềm cấp 3", "Da mềm cấp 4", "Da mềm cấp 5", "Da mềm cấp 6");
    }

    public void sendMenuDoiNguyenLieuThuongKhoaSoCap(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_NGUYEN_LIEU_THUONG_KHOA_SO_CAP, "Vải khóa", "Vải 2 khóa", "Vải 3 khóa", "Vải 4 khóa",
                "Vải 5 khóa", "Vải 6 khóa",
                "Sắt cấp 1 khóa", "Sắt cấp 2 khóa", "Sắt cấp 3 khóa", "Sắt cấp 4 khóa", "Sắt cấp 5 khóa",
                "Sắt cấp 6 khóa",
                "Ngọc cấp 1 khóa", "Ngọc cấp 2 khóa", "Ngọc cấp 3 khóa", "Ngọc cấp 4 khóa", "Ngọc cấp 5 khóa",
                "Ngọc cấp 6 khóa",
                "Gỗ thường cấp 1 khóa", "Gỗ thường cấp 2 khóa", "Gỗ thường cấp 3 khóa", "Gỗ thường cấp 4 khóa",
                "Gỗ thường cấp 5 khóa", "Gỗ thường cấp 6 khóa",
                "Da mềm cấp 1 khóa", "Da mềm cấp 2 khóa", "Da mềm cấp 3 khóa", "Da mềm cấp 4 khóa", "Da mềm cấp 5 khóa",
                "Da mềm cấp 6 khóa");
    }

    public void sendMenuDoiNgocHuyenMinh(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_NGOC_HUYEN_MINH, "Ngọc huyền minh cấp 2", "Ngọc huyền minh cấp 3",
                "Ngọc huyền minh cấp 4", "Ngọc huyền minh cấp 5", "Ngọc huyền minh cấp 6");
    }

    public void sendMenuDoiNgocHuyenMinhKhoa(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_NGOC_HUYEN_MINH_KHOA, "Ngọc huyền minh cấp 2 khóa", "Ngọc huyền minh cấp 3 khóa",
                "Ngọc huyền minh cấp 4 khóa", "Ngọc huyền minh cấp 5 khóa", "Ngọc huyền minh cấp 6 khóa");
    }

    public void sendMenuDoiBot(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_BOT, "Bột xanh", "Bột đỏ", "Bột xanh lá");
    }

    public void sendMenuDoiBotKhoa(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DOI_BOT_KHOA, "Bột xanh khóa", "Bột đỏ khóa", "Bột xanh lá khóa");
    }

    public void sendMenuLuyenThuSpecial(@NonNull Player player) throws IOException {
        ItemAnimal animal = player.getHorse().getAnimalUse();
        if (animal == null) {
            Service.instance.sendLogOut(player.getSession(), "Bạn phải cưỡi linh thú mà bạn muốn luyện");
            return;
        }
        sendOptionMenu(player, LUYEN_THU_SPECIAL,
                String.format("Thay đổi (%s lượng)", Manager.ANIMAL_CHANGE_SPECIAL_ATTRIBUTE_PRICE),
                String.format("Nâng cấp (%s lượng)", Manager.getAnimalTrainPrice(animal.getLevel())));
    }

    public void sendMenuLuyenThu(@NonNull Player player) throws IOException {
        ItemAnimal animal = player.getHorse().getAnimalUse();
        if (animal == null) {
            Service.instance.sendLogOut(player.getSession(), "Bạn phải cưỡi linh thú mà bạn muốn luyện");
            return;
        }
        sendOptionMenu(player, LUYEN_THU,
                String.format("Cơ bản (%s lượng)", Manager.getAnimalTrainPrice(animal.getLevel())),
                String.format("Tiềm năng (%s lượng)", Manager.getAnimalTrainPrice(animal.getLevel())), "Đặc biệt");
    }

    public void sendMenuSelectColorAnimalArmor(@NonNull Player player) throws IOException {
        sendOptionMenu(player, SELECT_COLOR_ANIMAL_ARMOR, "Trang bị xanh dương", "Trang bị đỏ", "Trang bị hoàn mỹ");
    }

    public void sendMenuBangTop(@NonNull Player player) throws IOException {
        sendOptionMenu(player, BANG_TOP, "Top cao thủ Thanh long", "Top cao thủ Hắc hổ", "Top công trạng Thanh long",
                "Top công trạng Hắc hổ", "Top liên trảm Thanh long", "Top liên trảm Hắc hổ");
    }

    public void sendMenuTaoThu(@NonNull Player player) throws IOException {
        sendOptionMenu(player, TAO_THU, "Hắc ngưu", "Mãnh hỗ", "Sói xám", "Tiên hạc", "Bạch mã", "Phượng hoàng",
                "Phượng hoàng băng", "Bạch cốt", "Đương khang", "Lân sư tử");
    }

    public void sendMenuHaoDuyen(@NonNull Player player) throws IOException {
        ItemAnimal animal = player.getHorse().getAnimalUse();
        if (animal == null || animal.getLevel() >= ItemEquipConst.MAX_LEVEL_ANIMAL) {
            sendOptionMenu(player, HAO_DUYEN, "Tạo thú", "Luyện thú");
            return;
        }
        sendOptionMenu(player, HAO_DUYEN, "Tạo thú", "Luyện thú",
                String.format("Nâng cấp thú (%s lượng)", Manager.getPriceUpgradeAnimal(animal.getLevel())));
    }

    public void sendMenuHoaTieuMap(@NonNull Player player, @NonNull HoaTieuTemplate template, byte index)
            throws IOException {
        player.getSundry().setIndexHoaTieu(index);
        sendOptionMenu(player, SELECT_HOA_TIEU_MAP, template.getNameMapChild()[index]);
    }

    public void sendMenuHoaTieu2(@NonNull Player player) throws IOException {
        byte idHoaTieu = player.getLocation().getZone().getMap().getMapData().getIdXaPhu();
        HoaTieuTemplate template = Manager.getHoaTieuTemplate(idHoaTieu);
        if (template == null) {
            return;
        }
        player.getSundry().setIdHoaTieu(idHoaTieu);
        sendOptionMenu(player, HOA_TIEU_NEW, template.getNameMap());
    }

    public void sendMenuHoaTieu(@NonNull Player player) throws IOException {
        byte idHoaTieu = player.getLocation().getZone().getMap().getMapData().getIdHoaTieu();
        HoaTieuTemplate template = Manager.getHoaTieuTemplate(idHoaTieu);
        if (template == null) {
            return;
        }
        player.getSundry().setIdHoaTieu(idHoaTieu);
        sendOptionMenu(player, HOA_TIEU_NEW, template.getNameMap());
    }

    public void sendMenuXaPhuNew(@NonNull Player player) throws IOException {
        if (player.getInfo().getIdNation() == Const.THANH_LONG) {
            sendOptionMenu(player, XA_PHU_NEW, "Dương đông", "Đông Dương đông");
        } else {
            sendOptionMenu(player, XA_PHU_NEW, "Sơn nam", "Đông Sơn nam");
        }
    }

    public void sendMenuCongDichChuyen(@NonNull Player player) throws IOException {
        sendOptionMenu(player, CONG_DICH_CHUYEN,
                String.format("Đến biên giới %s",
                        player.getLocation().getInCountry() == Const.THANH_LONG ? "Hắc hổ" : "Thanh long"),
                "Trường giang");
    }

    public void sendMenuDauTruong(@NonNull Player player) throws IOException {
        sendOptionMenu(player, DAU_TRUONG, "Thách đấu cá nhân", "Trấn yêu trận", "Đăng kí chiến trường",
                "Nhận quà khu liên đấu", "Nhận quà liên trảm", "Nhận quà top trụ", "Núi châu báu",
                "Hủy đăng ký liên đấu");
    }

    public void sendMenuTongTieuDau(@NonNull Player player) throws IOException {
        sendOptionMenu(player, TONG_TIEU_DAU, "Xuống ngựa", "Đổi danh hiệu", "Đăng ký lôi đài", "Vào sảnh chờ",
                "Xem lôi đài");
    }

    public void sendMenuThoRenThanBi(@NonNull Player player) throws IOException {
        sendOptionMenu(player, THO_REN_THAN_BI, "Vũ khí", "Trang bị", "Trang bị thú");
    }

    public void sendMenuUseNhan(@NonNull Player player) throws IOException {
        sendOptionMenu(player, USE_NHAN, "Nhẫn trên", "Nhẫn dưới");
    }

    public void sendMenuTongQuan(@NonNull Player player) throws IOException {
        sendOptionMenu(player, TONG_QUAN, "Hợp đục", "Mở rộng hành trang(150L)", "Thử vận may", "Chuyển lãnh thổ(300L)",
                "Nhận quà Giftcode", "Đăng ký liên đấu");
    }

    public void sendMenuThoHopThanhSoCap(@NonNull Player player) throws IOException {
        sendOptionMenu(player, THO_HOP_THANH_SO_CAP, "Nguyên liệu thường", "Nguyên liệu khóa", "Ngọc huyền minh",
                "Ngọc huyền minh khóa", "Bột thường", "Bột khóa");
    }

    public void sendMenuThoHopThanhCaoCap(@NonNull Player player) throws IOException {
        sendOptionMenu(player, THO_HOP_THANH_CAO_CAP, "Nguyên liệu thường", "Nguyên liệu khóa", "Xương không khóa",
                "Xương khóa");
    }

    public void sendMenuThayNguHanh(@NonNull Player player) throws IOException {
        sendOptionMenu(player, THAY_NGU_HANH, "Đổi hệ ngũ hành", "Tháo ngọc khảm", "Đặt mật khẩu rương", "Vòng quay",
                "Đồ đã bán");
    }

    public void sendOptionShopHacNguu(@NonNull Player player) throws IOException {
        sendOptionMenu(player, SHOP_HAC_NGUU, "Mua vũ khí", "Mua trang bị thú");
    }

    public void sendOptionBuyItem(@NonNull Player player) throws IOException {
        sendOptionMenu(player, MUA_TRANG_BI, "Trang bị ma pháp", "Trang bị vật lý");
    }

    private void sendOptionMenu(@NonNull Player player, byte idMenu, String... text) throws IOException {
        Message msg = new Message(CommandMessage.MENU_OPTION);
        msg.writer().writeShort(player.getIdPlayer());
        msg.writer().writeByte(idMenu);
        msg.writer().writeByte(text.length);
        for (String s : text) {
            msg.writer().writeUTF(s);
        }
        player.getSession().sendMessage(msg);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Functions Hop Thanh">
    public void doiNguyenLieu(@NonNull Player player, byte index, boolean isLock, byte type) throws IOException {
        short idItemGenerate = -1;
        short quantityRequired = -1;
        int xu = -1;
        switch (type) {
            case CombineConst.DOI_NGUYEN_LIEU_SO_CAP -> {
                idItemGenerate = CombineConst.ID_NGUYEN_LIEU_THUONG[index][0];
                quantityRequired = CombineConst.ID_NGUYEN_LIEU_THUONG[index][1];
                xu = CombineConst.ID_NGUYEN_LIEU_THUONG[index][2];
            }
            case CombineConst.DOI_NGUYEN_LIEU_CAO_CAP -> {
                idItemGenerate = CombineConst.ID_NGUYEN_LIEU_CAO_CAP[index][0];
                quantityRequired = CombineConst.ID_NGUYEN_LIEU_CAO_CAP[index][1];
                xu = CombineConst.ID_NGUYEN_LIEU_CAO_CAP[index][2];
            }
            case CombineConst.DOI_BOT -> {
                idItemGenerate = CombineConst.ID_BOT[index][0];
                quantityRequired = CombineConst.ID_BOT[index][1];
                xu = CombineConst.ID_BOT[index][2];
            }
            case CombineConst.DOI_XUONG -> {
                idItemGenerate = CombineConst.ID_XUONG[index][0];
                quantityRequired = CombineConst.ID_XUONG[index][1];
                xu = CombineConst.ID_XUONG[index][2];
            }
            case CombineConst.DOI_NGOC_HUYEN_MINH -> {
                idItemGenerate = CombineConst.ID_NGOC_HUYEN_MINH[index][0];
                quantityRequired = CombineConst.ID_NGOC_HUYEN_MINH[index][1];
                xu = CombineConst.ID_NGOC_HUYEN_MINH[index][2];
            }
        }
        if (idItemGenerate == -1) {
            return;
        }
        if (!player.getInventory().minusXu(xu)) {
            Service.instance.sendLogOut(player.getSession(), String.format("Không đủ %s xu", Util.formatNumber(xu)));
            return;
        }
        short idItemRequired = (short) (idItemGenerate - 1);
        ItemGem gemRequired = isLock ? InventoryService.instance.findItemGemLock(player, idItemRequired)
                : InventoryService.instance.findItemGem(player, idItemRequired);
        if (gemRequired == null) {
            Service.instance.sendLogOut(player.getSession(), "Không tìm thấy nguyên liệu");
            return;
        }
        if (gemRequired.getQuantity() < quantityRequired) {
            Service.instance.sendLogOut(player.getSession(),
                    String.format("Không đủ %s %s", quantityRequired, gemRequired.getTemplate().getName()));
            return;
        }
        int quantityCreate = (int) Math.floor(gemRequired.getQuantity() / quantityRequired);
        if (isLock) {
            InventoryService.instance.minusQuantityItemGemLock(player, gemRequired,
                    (short) (quantityCreate * quantityRequired));
        } else {
            InventoryService.instance.minusQuantityItemGem(player, gemRequired,
                    (short) (quantityCreate * quantityRequired));
        }
        ItemGem gemGenerate = ItemService.instance.createNewItemGem(idItemGenerate, (short) quantityCreate);
        ChatService.instance.sendChatOnlyMe(player, String.format("Tạo thành công %s %s %s",
                Util.formatNumber(quantityCreate), gemGenerate.getTemplate().getName(), isLock ? " khóa" : ""));
        if (isLock) {
            InventoryService.instance.addItemGemLock(player, gemGenerate);
            InventoryService.instance.sendItemGemLock(player);
        } else {
            InventoryService.instance.addItemGem(player, gemGenerate);
            InventoryService.instance.sendItemGem(player);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Functions Animal">
    private void createAnimal(@NonNull Player player, short idItem) {
        ItemAnimal animal = ItemService.instance.createNewItemAnimal(idItem);
        if (animal == null) {
            return;
        }
        if (idItem >= 64 && idItem <= 68) {
            animal.setItemBody(new ArrayList<>());
            animal.setMinutes(-1);
            animal.setTimeStart(System.currentTimeMillis());
            byte level = 1;
            for (int i = 0; i < ItemEquipConst.ATTRIBUTE_DEFAULT_ANIMAL.length; i++) {
                short id = ItemEquipConst.ATTRIBUTE_DEFAULT_ANIMAL[i];
                short value = (short) (Util.nextInt(1, Manager.getMaxValueAttributeAnimal((byte) 56, level)) >= 2 ? 2
                        : 1);
                animal.getAttributes().add(new Attribute(id, value));
            }
            animal.getAttributes()
                    .add(new Attribute((short) 33, (short) Manager.getMaxValueAttributeAnimal((byte) 33, level)));
            animal.getAttributes()
                    .add(new Attribute((short) 34, (short) Manager.getMaxValueAttributeAnimal((byte) 34, level)));
            short idRandom = ItemEquipConst.ATTRIBUTE_RANDOM_ANIMAL[Util.nextInt(0,
                    ItemEquipConst.ATTRIBUTE_RANDOM_ANIMAL.length - 1)];
            animal.getAttributes().add(new Attribute(idRandom,
                    (short) (Util.nextInt(1, Manager.getMaxValueAttributeAnimal(idRandom, level)) >= 2 ? 2 : 1)));
            idRandom = ItemEquipConst.ATTRIBUTE_RANDOM_ANIMAL_SPECIAL[Util.nextInt(0,
                    ItemEquipConst.ATTRIBUTE_RANDOM_ANIMAL_SPECIAL.length - 1)];
            animal.getAttributes().add(new Attribute(idRandom,
                    (short) (Util.nextInt(1, Manager.getMaxValueAttributeAnimal(idRandom, level)) >= 2 ? 2 : 1)));
        }
        InventoryService.instance.addItemAnimal(player, animal);
    }

    private void upgradeAnimal(@NonNull Player player) throws IOException {
        ItemAnimal animal = player.getHorse().getAnimalUse();
        if (animal == null) {
            Service.instance.sendLogOut(player.getSession(), "Bạn phải cưỡi linh thú mà bạn muốn luyện");
            return;
        }
        if (animal.getLevel() >= ItemEquipConst.MAX_LEVEL_ANIMAL) {
            Service.instance.sendLogOut(player.getSession(), "Linh thú đạt cấp tối đa");
            return;
        }
        if (!animal.getAttributes().stream().allMatch(att -> att != null && att.getValue() >= Manager
                .getMaxValueAttributeAnimal(att.getTemplate().getId(), animal.getLevel()))) {
            Service.instance.sendLogOut(player.getSession(), "Linh thú chưa đủ điều kiện để nâng cấp");
            return;
        }
        int price = Manager.getPriceUpgradeAnimal(animal.getLevel());
        if (!player.getInventory().minusLuong(price)) {
            Service.instance.sendLogOut(player.getSession(),
                    String.format("Không đủ %s lượng", Util.formatNumber(price)));
            return;
        }
        animal.plusLevel((byte) 1);
        for (short i = 3; i < 5; i++) {
            Attribute att = animal.getAttributes().get(i);
            if (att != null && att.getValue() < Manager.getMaxValueAttributeAnimal(att.getTemplate().getId(),
                    animal.getLevel())) {
                att.setValue(Manager.getMaxValueAttributeAnimal(att.getTemplate().getId(), animal.getLevel()));
            }
        }
        @Cleanup("clear")
        List<Short> existingAttributeIds = new ArrayList<>();
        for (Attribute att : animal.getAttributes()) {
            if (att != null) {
                existingAttributeIds.add(att.getTemplate().getId());
            }
        }
        short idRandom;
        do {
            idRandom = ItemEquipConst.ATTRIBUTE_RANDOM_ANIMAL[Util.nextInt(0,
                    ItemEquipConst.ATTRIBUTE_RANDOM_ANIMAL.length - 1)];
        } while (existingAttributeIds.contains(idRandom));
        animal.getAttributes().add(animal.getAttributes().size() - 1,
                new Attribute(idRandom,
                        (short) (Util.nextInt(1, Manager.getMaxValueAttributeAnimal(idRandom, animal.getLevel())) >= 2
                                ? 2
                                : 1)));
        player.getPoint().initPoint();
        InventoryService.instance.sendItemBody(player);
        InventoryService.instance.sendItemAnimal(player);
        Service.instance.sendMainCharInfo(player);
    }

    private void changeSpecialAttribute(@NonNull Player player) throws IOException {
        ItemAnimal animal = player.getHorse().getAnimalUse();
        if (animal == null) {
            Service.instance.sendLogOut(player.getSession(), "Bạn phải cưỡi linh thú mà bạn muốn luyện");
            return;
        }
        int price = Manager.ANIMAL_CHANGE_SPECIAL_ATTRIBUTE_PRICE;
        if (!player.getInventory().minusLuong(price)) {
            Service.instance.sendLogOut(player.getSession(),
                    String.format("Không đủ %s lượng", Util.formatNumber(price)));
            return;
        }
        Attribute attributeToChange = animal.getAttributes().get(animal.getAttributes().size() - 1);
        if (attributeToChange == null) {
            return;
        }
        animal.getAttributes().remove(attributeToChange);
        short idRandom = ItemEquipConst.ATTRIBUTE_RANDOM_ANIMAL_SPECIAL[Util.nextInt(0,
                ItemEquipConst.ATTRIBUTE_RANDOM_ANIMAL_SPECIAL.length - 1)];
        animal.getAttributes()
                .add(new Attribute(idRandom,
                        (short) (Util.nextInt(1, Manager.getMaxValueAttributeAnimal(idRandom, animal.getLevel())) >= 2
                                ? 2
                                : 1)));
        player.getPoint().initPoint();
        InventoryService.instance.sendItemBody(player);
        InventoryService.instance.sendItemAnimal(player);
        Service.instance.sendMainCharInfo(player);
    }

    private void upgradeSpecialAttribute(@NonNull Player player) throws IOException {
        ItemAnimal animal = player.getHorse().getAnimalUse();
        if (animal == null) {
            Service.instance.sendLogOut(player.getSession(), "Bạn phải cưỡi linh thú mà bạn muốn luyện");
            return;
        }
        short price = Manager.getAnimalTrainPrice(animal.getLevel());
        if (!player.getInventory().minusLuong(price)) {
            Service.instance.sendLogOut(player.getSession(),
                    String.format("Không đủ %s lượng", Util.formatNumber(price)));
            return;
        }
        Attribute attributeToUpgrade = animal.getAttributes().get(animal.getAttributes().size() - 1);
        if (attributeToUpgrade == null) {
            return;
        }
        if (attributeToUpgrade.getValue() < Manager.getMaxValueAttributeAnimal(attributeToUpgrade.getTemplate().getId(),
                animal.getLevel())) {
            attributeToUpgrade.plusValue((short) 1);
            player.getPoint().initPoint();
            InventoryService.instance.sendItemBody(player);
            InventoryService.instance.sendItemAnimal(player);
            Service.instance.sendMainCharInfo(player);
        } else {
            Service.instance.sendLogOut(player.getSession(), "Đã nâng cấp tối đa");
        }
    }

    private void upgradeTiemNang(@NonNull Player player) throws IOException {
        ItemAnimal animal = player.getHorse().getAnimalUse();
        if (animal == null) {
            Service.instance.sendLogOut(player.getSession(), "Bạn phải cưỡi linh thú mà bạn muốn luyện");
            return;
        }
        short price = Manager.getAnimalTrainPrice(animal.getLevel());
        if (!player.getInventory().minusLuong(price)) {
            Service.instance.sendLogOut(player.getSession(),
                    String.format("Không đủ %s lượng", Util.formatNumber(price)));
            return;
        }
        Attribute attributeToUpgrade = null;
        for (short i = 5; i < animal.getAttributes().size() - 1; i++) {
            Attribute att = animal.getAttributes().get(i);
            if (att != null) {
                short valueSpecial = animal.getTemplate().getMaxValueSpecial(att.getTemplate().getId());
                short maxValue = Manager.getMaxValueAttributeAnimal(att.getTemplate().getId(), animal.getLevel());
                if (shouldUpgradeAttribute(animal.getLevel(), valueSpecial, att.getValue(), maxValue)) {
                    attributeToUpgrade = att;
                    break;
                }
            }
        }
        if (attributeToUpgrade == null) {
            Service.instance.sendLogOut(player.getSession(), "Đã nâng cấp tối đa");
            return;
        }
        attributeToUpgrade.plusValue((short) 1);
        player.getPoint().initPoint();
        InventoryService.instance.sendItemBody(player);
        InventoryService.instance.sendItemAnimal(player);
        Service.instance.sendMainCharInfo(player);
    }

    private boolean shouldUpgradeAttribute(byte level, short valueSpecial, short attributeValue, short maxValue) {
        if (level >= ItemEquipConst.MAX_LEVEL_ANIMAL) {
            if (valueSpecial == -1 && attributeValue < maxValue) {
                return true;
            }
            return valueSpecial != -1 && attributeValue < valueSpecial;
        } else {
            return attributeValue < maxValue;
        }
    }

    private void upgradeNormal(@NonNull Player player) throws IOException {
        ItemAnimal animal = player.getHorse().getAnimalUse();
        if (animal == null) {
            Service.instance.sendLogOut(player.getSession(), "Bạn phải cưỡi linh thú mà bạn muốn luyện");
            return;
        }
        short price = Manager.getAnimalTrainPrice(animal.getLevel());
        if (!player.getInventory().minusLuong(price)) {
            Service.instance.sendLogOut(player.getSession(),
                    String.format("Không đủ %s lượng", Util.formatNumber(price)));
            return;
        }
        Attribute attributeToUpgrade = null;
        for (short i = 0; i < 3; i++) {
            Attribute att = animal.getAttributes().get(i);
            if (att != null && att.getValue() < Manager.getMaxValueAttributeAnimal(att.getTemplate().getId(),
                    animal.getLevel())) {
                attributeToUpgrade = att;
                break;
            }
        }
        if (attributeToUpgrade == null) {
            Service.instance.sendLogOut(player.getSession(), "Đã nâng cấp tối đa");
            return;
        }
        attributeToUpgrade.plusValue((short) 1);
        player.getPoint().initPoint();
        InventoryService.instance.sendItemBody(player);
        InventoryService.instance.sendItemAnimal(player);
        Service.instance.sendMainCharInfo(player);
    }
    // </editor-fold>
}
