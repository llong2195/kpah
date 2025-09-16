package item;

import consts.ItemEquipConst;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import player.Player;
import services.InventoryService;
import template.ItemEquipTemplate;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ☂️☂️Duy Coder 💖💖
 */
@Data
@Builder
public class ItemEquip {

    private short idItem;
    private ItemEquipTemplate template;
    private byte plusTemplate;
    private byte classChar;
    private boolean isLock;
    private short durable;
    private short mDurable;
    private byte level;
    private byte colorName;
    private byte viTriVe;
    private byte he;
    private byte rank;
    private byte damageType;
    private String nameCharSeal;
    private List<Attribute> itemAttributes;
    private int dayUse;
    private long timeCreateItem;
    private boolean isKichNguHanh;

    public void minusDurable() {
        mDurable--;
        if (mDurable % 10 == 0) {
            durable--;
        }
    }

    public boolean isKichNguHanh(@NonNull Player player) {
        if (he == ItemEquipConst.NONE_HE) {
            return isKichNguHanh = false;
        }
        byte heKich = ItemEquipConst.KICH_HE[he];
        if (rank > 0 && rank < 5 && template.getType() < 13 && template.getType() != 8) {
            String[] groupKich2 = ItemEquipConst.GROUP_KICH[template.getType()][viTriVe != 1 && template.getType() == 8
                    ? 3
                    : 1].split("_");
            String[] groupKich1 = ItemEquipConst.GROUP_KICH[template.getType()][viTriVe != 1 && template.getType() == 8
                    ? 2
                    : 0].split("_");
            byte typeKich1 = Byte.parseByte(groupKich1[0]);
            byte viTri1 = Byte.parseByte(groupKich1[1]);
            byte typeKich2 = (byte) (Byte.parseByte(groupKich2[0]) == 3 ? 3 + player.getInfo().getClassPlayer()
                    : Byte.parseByte(groupKich2[0]));
            byte viTri2 = Byte.parseByte(groupKich2[1]);
            ItemEquip item = InventoryService.instance.findItemBodyByTypeHe(player, typeKich1, heKich, viTri1);
            ItemEquip item2 = InventoryService.instance.findItemBodyByTypeHe(player, typeKich2, heKich, viTri2);
            return isKichNguHanh = item != null || item2 != null;
        } else if (rank > 0 && rank < 5 && isAnimalArmor()) {
            byte typeKich = ItemEquipConst.GROUP_KICH_ANIMAL[template.getType() - 14];
            ItemEquip item = InventoryService.instance.findItemBodyAnimalByHe(player, typeKich, heKich);
            return isKichNguHanh = item != null;
        }
        return false;
    }

    public boolean isJewelry() {
        return Arrays.asList(ItemEquipConst.NHAN, ItemEquipConst.DAY_CHUYEN, ItemEquipConst.NGOC)
                .contains(template.getType());
    }

    public boolean isArmor() {
        return Arrays.asList(ItemEquipConst.AO, ItemEquipConst.QUAN, ItemEquipConst.MU, ItemEquipConst.GIAY,
                ItemEquipConst.GANG).contains(template.getType());
    }

    public boolean isWeapon() {
        return Arrays.asList(ItemEquipConst.VU_KHI_KIEM, ItemEquipConst.VU_KHI_DAO, ItemEquipConst.VU_KHI_BUT,
                ItemEquipConst.VU_KHI_BUA, ItemEquipConst.VU_KHI_CUNG).contains(template.getType());
    }

    public boolean isAnimalArmor() {
        return Arrays.asList(ItemEquipConst.ANIMAL_GIAP, ItemEquipConst.ANIMAL_HO_UYEN, ItemEquipConst.ANIMAL_NON,
                ItemEquipConst.ANIMAL_BAN_DAP, ItemEquipConst.ANIMAL_YEN).contains(template.getType());
    }

    public void subDefend() {
        List<Byte> equipTypeDefs = Arrays.asList(ItemEquipConst.AO, ItemEquipConst.QUAN, ItemEquipConst.GIAY,
                ItemEquipConst.GANG);

        // (template.getType() < 2) || (template.getType() == 10) || (template.getType()
        // == 11) || isAnimalArmor()
        if (equipTypeDefs.contains(template.getType()) || isAnimalArmor()) {
            short thuVat = getValue((byte) 1);
            if (damageType == ItemEquipConst.DAMAGE_MAGIC) {
                setValue((byte) 6, thuVat);
                setValue((byte) 1, (short) (thuVat / 10));
            } else if (damageType == ItemEquipConst.DAMAGE_PHYSIC) {
                setValue((byte) 6, (short) (thuVat / 10));
            }
        }
    }

    public short getValue(short idAtt) {
        for (Attribute attribute : itemAttributes) {
            System.out.println(attribute.getInfo());
            if (attribute.getTemplate().getId() == idAtt) {
                if (attribute.getTemplate().getColorPaint() == 1 && !isKichNguHanh) {
                    return 0;
                }
                if (attribute.getTemplate().getIsPercent() == 2) {
                    return (short) (attribute.getValue() / 10);
                }
                return attribute.getValue();
            }
        }
        return 0;
    }

    public void setValue(byte idAtt, short valueNew) {
        for (Attribute attribute : itemAttributes) {
            if (attribute != null && attribute.getTemplate().getId() == idAtt) {
                attribute.setValue(valueNew);
                break;
            }
        }
    }

    public void dispose() {
        template = null;
        if (itemAttributes != null) {
            for (Attribute att : itemAttributes) {
                att.dispose();
            }
            itemAttributes.clear();
        }
        itemAttributes = null;
    }

    @Override
    public String toString() {
        JSONArray arr = new JSONArray();
        arr.put(idItem);
        arr.put(template.getId());
        arr.put(classChar);
        arr.put(level);
        arr.put(plusTemplate);
        arr.put(colorName);
        arr.put(isLock);
        arr.put(mDurable);
        arr.put(durable);
        arr.put(viTriVe);
        arr.put(rank);
        arr.put(damageType);
        arr.put(nameCharSeal);
        arr.put(dayUse);
        arr.put(timeCreateItem);
        arr.put(he);
        try {
            arr.put(new JSONArray(itemAttributes.toString()));
        } catch (JSONException e) {
        }
        return arr.toString();
    }
}
