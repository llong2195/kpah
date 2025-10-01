package org.kpah.player;

import java.util.ArrayList;
import java.util.List;

import org.kpah.item.ItemMineral;

import lombok.Data;

@Data
public class Manufacture {

    private byte typeNguyenLieuCreate;
    private byte typeDamageCreate;
    private byte typeArmorCreate;
    private byte colorAnimalArmorCreate;
    private byte classCharCreateEquip;
    private byte selectedItemCreate;
    private short[] nguyenLieuCreate;
    private byte typeCheTao;
    private final List<ItemMineral> itemMineral = new ArrayList<>();

    public void dispose() {
        colorAnimalArmorCreate = -1;
        typeArmorCreate = -1;
        typeDamageCreate = -1;
        typeNguyenLieuCreate = -1;
        classCharCreateEquip = -1;
        selectedItemCreate = -1;
        nguyenLieuCreate = null;
        itemMineral.clear();
    }
}
