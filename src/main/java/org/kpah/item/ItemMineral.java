package org.kpah.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemMineral {

    private short idTemplate;
    private byte level;
    private short quantity;

    public boolean isCaoCap() {
        return (idTemplate >= 103 && idTemplate <= 108)
                || (idTemplate >= 110 && idTemplate <= 115)
                || (idTemplate >= 117 && idTemplate <= 122)
                || (idTemplate >= 124 && idTemplate <= 129)
                || (idTemplate >= 131 && idTemplate <= 136);
    }
}
