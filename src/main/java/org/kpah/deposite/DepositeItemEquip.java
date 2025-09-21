package org.kpah.deposite;

import org.kpah.item.ItemEquip;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author ☂️☂️Duy Coder 💖💖
 */
@Data
@AllArgsConstructor
public class DepositeItemEquip {

    private ItemEquip item;
    private String nameDeposite;
    private int priceDeposite;
    private String playerCanBuy;

    public void dispose() {
        priceDeposite = -1;
        playerCanBuy = "";
        nameDeposite = "";
        item = null;
    }
}
