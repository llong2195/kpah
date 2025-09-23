package org.kpah.template;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeEquipTemplate {

    private byte isPercent;

    private byte colorPaint;

    private String name;

    private short id;
}
