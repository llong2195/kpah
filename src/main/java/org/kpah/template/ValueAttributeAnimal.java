package org.kpah.template;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValueAttributeAnimal {

    private short id;
    private short[] maxValue;
}
