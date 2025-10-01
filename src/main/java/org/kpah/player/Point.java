package org.kpah.player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.kpah.consts.AttributeConst;
import org.kpah.consts.BuffConst;
import org.kpah.consts.Const;
import org.kpah.manager.Manager;
import org.kpah.services.InventoryService;
import org.kpah.services.MapService;
import org.kpah.services.Service;
import org.kpah.utils.Util;

import lombok.Builder;
import lombok.Data;
import lombok.Synchronized;

@Data
@Builder
public class Point {

    private Player player;

    private int hpMax;
    @Builder.Default
    private int hp = -1;
    private int mpMax;
    @Builder.Default
    private int mp = -1;

    private int attack;
    private int defend, defendMagic;

    private short percentPlusHp;
    private short percentPlusMp;

    private short hutHp;

    private double percentDropXu;
    private double percentDropEquip;

    private short accurate;
    private short dodge;
    private short critical;
    private short x2;
    private short hapThu;

    private short baoKich;
    private short xuyenGiap;
    private short expDonate;
    private short docTinh;
    private short giamStVat;
    private short giamStMa;

    private short strength, agility, spirit, health, luck;
    private short strengthAdd, agilityAdd, spiritAdd, healthAdd;

    private long exp;

    private short basePoint, skillPoint;

    private int dedicationPoint;

    private byte speed;
    private long xuRevive;

    public void increaseSkillPoint(byte type) throws IOException {
        byte levelSkill = player.getSkill().getLevelSkill()[type];
        if (levelSkill == -1 || skillPoint <= 0
                || player.getInfo().getLevel() < Manager.getLevelAddSkill(type, levelSkill)) {
            return;
        }
        if (levelSkill >= 9) {
            Service.instance.sendLogOut(player.getSession(), "Kĩ năng đạt cấp tối đa");
            return;
        }
        skillPoint -= 1;
        player.getSkill().getLevelSkill()[type] += 1;
        Service.instance.sendEndDialog(player);
        initPoint();
        Service.instance.sendMainCharInfo(player);
    }

    public void increaseBasePoint(byte type, short numIncrease) throws IOException {
        if (basePoint == 0 || basePoint - numIncrease < 0 || numIncrease <= 0) {
            return;
        }
        basePoint -= numIncrease;
        switch (type) {
            case 0 -> strength += numIncrease;
            case 1 -> agility += numIncrease;
            case 2 -> spirit += numIncrease;
            case 3 -> health += numIncrease;
            case 4 -> luck += numIncrease;
        }
        Service.instance.sendEndDialog(player);
        initPoint();
        Service.instance.sendMainCharInfo(player);
        MapService.instance.onNewHpMp(player);
    }

    @Synchronized
    public int getDameAttack(boolean miss, boolean isCrit, boolean isBaoKich, boolean isAttackMob) {
        if (miss) {
            return 0;
        }
        int dameAttack = (int) (this.attack * (isAttackMob ? 2.5 : 2));
        dameAttack += dameAttack
                * (Manager.getSkillDamPercent(player.getInfo().getClassPlayer(), player.getSkill().getTypeSkill(),
                        player.getSkill().getLevelSkill()[player.getSkill().getTypeSkill()]) / 100);
        if (isCrit || isBaoKich) {
            dameAttack *= 2;
        }
        dameAttack = Util.nextInt((int) ((float) dameAttack * 0.7), (int) ((float) dameAttack * 0.9));
        if (dameAttack < 0) {
            dameAttack = 1;
        }
        return dameAttack;
    }

    private void setStrength() {
        strengthAdd += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_SUC_MANH);
        if (player.getHorse().getImageHorse() == 1) {
            strengthAdd += 3;
        }
        if (player.getHorse().getAnimalUse() != null) {
            strengthAdd += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_SUC_MANH);
            strengthAdd += player.getHorse().getAnimalUse()
                    .sumAttributeValueForId(AttributeConst.TANG_SUC_MANH);
        }
    }

    private void setAgility() {
        agilityAdd += InventoryService.instance.sumAttributeValueForId(player,
                AttributeConst.TANG_NHANH_NHEN);
        if (player.getHorse().getImageHorse() == 1) {
            agilityAdd += 3;
        }
        if (player.getHorse().getAnimalUse() != null) {
            agilityAdd += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.CONG_KHEO_LEO);
            agilityAdd += player.getHorse().getAnimalUse()
                    .sumAttributeValueForId(AttributeConst.TANG_NHANH_NHEN);
        }
    }

    private void setSpirit() {
        int spiritPercent = 0;
        spiritAdd += InventoryService.instance.sumAttributeValueForId(player,
                AttributeConst.TANG_TINH_THAN);
        if (player.getInfo().getClassPlayer() == Const.PHAP_SU) {
            spiritPercent += Manager.getSkillDamPercent(player.getInfo().getClassPlayer(), (byte) 5,
                    player.getSkill().getLevelSkill()[5]);
        }
        if (player.getHorse().getImageHorse() == 1) {
            spiritAdd += 3;
        }
        if (player.getHorse().getAnimalUse() != null) {
            spiritAdd += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_TINH_THAN);
            spiritAdd += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_TINH_THAN);
        }
        spiritAdd += spiritAdd * spiritPercent / 100;
    }

    private void setHealth() {
        healthAdd += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_SUC_KHOE);
        healthAdd += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.SUC_KHOE);
        if (player.getHorse().getImageHorse() == 1) {
            healthAdd += 3;
        }
        if (player.getHorse().getAnimalUse() != null) {
            healthAdd += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_SUC_KHOE);
            healthAdd += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_SUC_KHOE);
        }
    }

    private void setAttack() {
        switch (player.getInfo().getClassPlayer()) {
            case Const.KIEM_KHACH, Const.CHIEN_BINH, Const.DAU_SI -> attack += strength + strengthAdd;
            case Const.PHAP_SU -> attack += (spirit + spiritAdd) * 2;
            case Const.CUNG_THU -> attack += (agility + agilityAdd) * 1.8;
        }
        attack += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TAN_CONG);

        int attackPercent = InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_CONG);
        attackPercent += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_TAN_CONG);

        if (player.getInfo().getClassPlayer() == Const.CHIEN_BINH) {
            attackPercent += Manager.getSkillDamPercent(player.getInfo().getClassPlayer(), (byte) 5,
                    player.getSkill().getLevelSkill()[5]);
        }
        if (player.getHorse().getAnimalUse() != null) {
            attack += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TAN_CONG);
            attackPercent += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_TAN_CONG);
            attackPercent += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_CONG);
        }
        attack += attack * attackPercent / 100;
    }

    private void setDefend() {
        defend += agility + agilityAdd;
        defend += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.THU_VAT);

        int defPercent = 0;
        if (player.getInfo().getClassPlayer() == Const.DAU_SI) {
            defPercent += Manager.getSkillDamPercent(player.getInfo().getClassPlayer(), (byte) 5,
                    player.getSkill().getLevelSkill()[5]);
        }
        defPercent += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_THU_VAT);
        defPercent += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_THU_VAT_TRANG_BI);
        defPercent += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_THU_TRANG_BI);
        if (player.getHorse().getAnimalUse() != null) {
            defPercent += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_THU_VAT);
            defPercent += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_THU_VAT_TRANG_BI);
            defPercent += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_THU_TRANG_BI);
        }
        defend += defend * defPercent / 100;
    }

    private void setDefendMagic() {
        defendMagic += agility + agilityAdd;
        defendMagic += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.THU_MA);

        int defPercent = 0;

        if (player.getInfo().getClassPlayer() == Const.DAU_SI) {
            defPercent += Manager.getSkillDamPercent(player.getInfo().getClassPlayer(), (byte) 5,
                    player.getSkill().getLevelSkill()[5]);
        }
        defPercent += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_THU_MA);
        defPercent += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_THU_MA_TRANG_BI);
        defPercent += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_THU_TRANG_BI);
        if (player.getHorse().getAnimalUse() != null) {
            defPercent += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_THU_MA);
            defPercent += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_THU_MA_TRANG_BI);
            defPercent += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_THU_TRANG_BI);
        }
        defendMagic += defendMagic * defPercent / 100;
    }

    private void setAccurate() {
        switch (player.getInfo().getClassPlayer()) {
            case Const.CUNG_THU -> accurate += agility + agilityAdd;
        }
        accurate += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.CHINH_XAC);
        if (player.getHorse().getAnimalUse() != null) {
            accurate += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.CHINH_XAC);
        }
    }

    private void setDodge() {
        switch (player.getInfo().getClassPlayer()) {
            case Const.CUNG_THU -> dodge += (agility + agilityAdd) * 0.5;
        }
        dodge += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.NE_TRANH);
        if (player.getHorse().getAnimalUse() != null) {
            dodge += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.NE_TRANH);
        }
    }

    private void setCrit() {
        critical += luck / 20;
        critical += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_CHI_MANG);
        critical += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.CHI_MANG);
        if (player.getHorse().getAnimalUse() != null) {
            critical += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_CHI_MANG);
            critical += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.CHI_MANG);
        }
    }

    private void setBaoKich() {
        baoKich += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_X2_MOI_LAN_DANH);
        if (player.getHorse().getAnimalUse() != null) {
            baoKich += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_X2_MOI_LAN_DANH);
        }
    }

    private void setDocTinh() {
        docTinh += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TRUNG_DOC);
        if (player.getInfo().getClassPlayer() == Const.CUNG_THU) {
            docTinh += Manager.getSkillDamPercent(player.getInfo().getClassPlayer(), (byte) 5,
                    player.getSkill().getLevelSkill()[5]);
        }
    }

    private void setXuyenGiap() {
        xuyenGiap += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.XUYEN_GIAP);
        if (player.getInfo().getClassPlayer() == Const.KIEM_KHACH) {
            xuyenGiap += Manager.getSkillDamPercent(player.getInfo().getClassPlayer(), (byte) 4,
                    player.getSkill().getLevelSkill()[4]);
        }
        if (player.getHorse().getAnimalUse() != null) {
            xuyenGiap += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.XUYEN_GIAP);
        }
    }

    private void setExpDonate() {
        expDonate += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_EXP);
        if (player.getHorse().getAnimalUse() != null) {
            expDonate += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_EXP);
        }
    }

    private void setPercentPlusHp() {
        if (player.getInfo().getClassPlayer() == Const.PHAP_SU) {
            if (player.getSkillBuff().isExistBuff(BuffConst.HOI_CONG_LUC_DAN)) {
                percentPlusHp = player.getSkillBuff().getPercentDame(BuffConst.HOI_CONG_LUC_DAN);
            }
        }
    }

    private void setPercentPlusMp() {
        if (player.getInfo().getClassPlayer() == Const.PHAP_SU) {
            if (player.getSkillBuff().isExistBuff(BuffConst.HOI_CONG_LUC_DAN)) {
                percentPlusMp = player.getSkillBuff().getPercentDame(BuffConst.HOI_CONG_LUC_DAN);
            }
        }
    }

    private void setHpMax() {
        switch (player.getInfo().getClassPlayer()) {
            case Const.KIEM_KHACH -> hpMax += (health + healthAdd) * 80;
            case Const.DAU_SI, Const.CHIEN_BINH -> hpMax += (health + healthAdd) * 70;
            case Const.PHAP_SU, Const.CUNG_THU -> hpMax += (health + healthAdd) * 60;
        }

        hpMax += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_HP) * 1000;
        switch (player.getHorse().getImageHorse()) {
            case 0 -> hpMax += 700;
            case 1 -> hpMax += 1000;
            default -> {
                if (player.getHorse().getAnimalUse() != null) {
                    hpMax += player.getHorse().getAnimalUse().getValue((byte) AttributeConst.TANG_HP);
                }
            }
        }
        hpMax += hpMax * percentPlusHp / 100;
    }

    private void setMpMax() {
        switch (player.getInfo().getClassPlayer()) {
            case Const.KIEM_KHACH, Const.CHIEN_BINH, Const.DAU_SI, Const.CUNG_THU -> mpMax += (spirit + spiritAdd) * 20;
            case Const.PHAP_SU -> mpMax += (spirit + spiritAdd) * 52;
        }

        mpMax += InventoryService.instance.sumAttributeValueForId(player, (byte) AttributeConst.TANG_MP) * 1000;
        switch (player.getHorse().getImageHorse()) {
            case 0 -> mpMax += 700;
            case 1 -> mpMax += 1000;
            default -> {
                if (player.getHorse().getAnimalUse() != null) {
                    mpMax += player.getHorse().getAnimalUse().getValue((byte) AttributeConst.TANG_MP);
                }
            }
        }
        mpMax += mpMax * percentPlusMp / 100;
    }

    private void setHp() {
        if (hp == -1) {
            hp = hpMax;
        } else if (hp == 0) {
            hp = 1;
        }
    }

    private void setMp() {
        if (mp == -1) {
            mp = mpMax;
        } else if (mp == 0) {
            mp = 1;
        }
    }

    public void plusHp(int hp) {
        if (hp < 0) {
            return;
        }
        if (this.hp + hp >= hpMax) {
            this.hp = hpMax;
        } else {
            this.hp += hp;
        }
    }

    public void plusMp(int mp) {
        if (mp < 0) {
            return;
        }
        if (this.mp + mp >= mpMax) {
            this.mp = mpMax;
        } else {
            this.mp += mp;
        }
    }

    public void minusHp(int hp) {
        if (hp < 0) {
            return;
        }
        if (this.hp >= hp) {
            this.hp -= hp;
        } else {
            this.hp = 0;
        }
    }

    public void minusMp(int mp) {
        if (mp < 0) {
            return;
        }
        if (this.mp >= mp) {
            this.mp -= mp;
        } else {
            this.mp = 0;
        }
    }

    public void plusExp(int exp) {
        if (exp < 0) {
            return;
        }
        if (this.exp < 0) {
            this.exp = 0;
        }
        this.exp += exp;
    }

    public void plusStrength(int point) {
        if (point < 0) {
            return;
        }
        this.strength += point;
    }

    public void plusAgility(int point) {
        if (point < 0) {
            return;
        }
        this.agility += point;
    }

    public void plusSpirit(int point) {
        if (point < 0) {
            return;
        }
        this.spirit += point;
    }

    public void plusHealth(int point) {
        if (point < 0) {
            return;
        }
        this.health += point;
    }

    public void plusLuck(int point) {
        if (point < 0) {
            return;
        }
        this.luck += point;
    }

    public void plusBasePoint(int point) {
        if (point < 0) {
            return;
        }
        this.basePoint += point;
    }

    public void plusSkillPoint(int point) {
        if (point < 0) {
            return;
        }
        this.skillPoint += point;
    }

    public boolean isFullHp() {
        return hp >= hpMax;
    }

    public boolean isFullMp() {
        return mp >= mpMax;
    }

    private void setSpeed() {
        this.speed = Const.SPEED;
        this.speed += player.getHorse().getAnimalUse() == null ? 1 : 2;
    }

    private void setX2() {
        x2 += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_X2_MOI_LAN_DANH);
        if (player.getHorse().getAnimalUse() != null) {
            x2 += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_X2_MOI_LAN_DANH);
        }
    }

    private void setHapThu() {
        hapThu += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.HAP_THU_SAT_THUONG);
        if (player.getHorse().getAnimalUse() != null) {
            hapThu += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.HAP_THU_SAT_THUONG);
        }
    }

    private void setGiamStVat() {
        giamStVat += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.GIAM_ST_VAT);
        if (player.getHorse().getAnimalUse() != null) {
            giamStVat += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.GIAM_ST_VAT);
        }
    }

    private void setGiamStMa() {
        giamStMa += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.GIAM_ST_MA);
        if (player.getHorse().getAnimalUse() != null) {
            giamStMa += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.GIAM_ST_MA);
        }
    }

    private void setXuRevive() {
        int x = player.getInfo().getLevel();
        int minValue = 10000, maxValue = 500000, numSteps = 10;
        int stepSize = 100 / numSteps;
        int stepIndex = (x - 1) / stepSize;
        int increment = (maxValue - minValue) / (numSteps - 1);
        xuRevive = Math.round((minValue + stepIndex * increment) / 1000f) * 1000;
    }

    private void setHutHp() {
        hutHp += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.HUT_MAU);
        hutHp += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.HUT_HP);
        if (player.getHorse().getAnimalUse() != null) {
            hutHp += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.HUT_MAU);
            hutHp += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.HUT_HP);
        }
    }

    private void setPercentDropXu() {
        percentDropXu += InventoryService.instance.sumAttributeValueForId(player, AttributeConst.TANG_TY_LE_ROT_XU);
        if (player.getHorse().getAnimalUse() != null) {
            percentDropXu += player.getHorse().getAnimalUse().sumAttributeValueForId(AttributeConst.TANG_TY_LE_ROT_XU);
        }
    }

    private void setPercentDropEquip() {
        percentDropEquip += InventoryService.instance.sumAttributeValueForId(player,
                AttributeConst.TANG_TY_LE_ROT_VAT_PHAM);
        if (player.getHorse().getAnimalUse() != null) {
            percentDropEquip += player.getHorse().getAnimalUse()
                    .sumAttributeValueForId(AttributeConst.TANG_TY_LE_ROT_VAT_PHAM);
        }
    }

    private void setSkillLevel() {
        List<Short> attSkills = Arrays.asList(
                AttributeConst.KY_NANG_1_CONG_THEM,
                AttributeConst.KY_NANG_2_CONG_THEM,
                AttributeConst.KY_NANG_3_CONG_THEM,
                AttributeConst.KY_NANG_4_CONG_THEM,
                AttributeConst.KY_NANG_5_CONG_THEM,
                AttributeConst.KY_NANG_6_CONG_THEM,
                AttributeConst.KY_NANG_7_CONG_THEM,
                AttributeConst.KY_NANG_8_CONG_THEM,
                AttributeConst.KY_NANG_9_CONG_THEM,
                AttributeConst.KY_NANG_10_CONG_THEM,
                AttributeConst.KY_NANG_11_CONG_THEM,
                AttributeConst.KY_NANG_12_CONG_THEM,
                AttributeConst.KY_NANG_13_CONG_THEM,
                AttributeConst.KY_NANG_14_CONG_THEM,
                AttributeConst.KY_NANG_15_CONG_THEM);

        for (int i = 0; i < attSkills.size(); i++) {
            short attSkillId = attSkills.get(i);
            int levelSkill = InventoryService.instance.sumAttributeValueForId(player, attSkillId);

            if (player.getHorse().getAnimalUse() != null) {
                levelSkill += player.getHorse().getAnimalUse().sumAttributeValueForId(attSkillId);
            }

            if (player.getSkill().getLevelSkill().length > i) {
                player.getSkill().getLevelSkill()[i] += (byte) levelSkill;
            }
        }

    }

    public void initPoint() {
        resetPoint();
        setPercentDropXu();
        setPercentDropEquip();
        setHutHp();
        setHapThu();
        setXuRevive();
        setSpeed();
        setGiamStMa();
        setGiamStVat();
        setX2();
        setPercentPlusHp();
        setPercentPlusMp();
        setXuyenGiap();
        setDocTinh();
        setExpDonate();
        setStrength();
        setHealth();
        setAgility();
        setSpirit();
        setHpMax();
        setMpMax();
        setAttack();
        setDefend();
        setDefendMagic();
        setAccurate();
        setDodge();
        setCrit();
        setBaoKich();
        setHp();
        setMp();
        // setSkillLevel();
    }

    private void resetPoint() {
        hpMax = 0;
        mpMax = 0;
        strengthAdd = 0;
        healthAdd = 0;
        agilityAdd = 0;
        spiritAdd = 0;
        attack = 0;
        defend = 0;
        defendMagic = 0;
        accurate = 0;
        dodge = 0;
        critical = 0;
        baoKich = 0;
        xuyenGiap = 0;
        expDonate = 0;
        docTinh = 0;
        percentPlusHp = 0;
        percentPlusMp = 0;
        speed = 0;
        x2 = 0;
        hapThu = 0;
        giamStMa = 0;
        giamStVat = 0;
        xuRevive = 0;
        hutHp = 0;
        percentDropXu = 0;
        percentDropEquip = 0;
    }

    @Override
    public String toString() {
        JSONArray point = new JSONArray();
        point.put(hp);
        point.put(mp);
        point.put(strength);
        point.put(agility);
        point.put(spirit);
        point.put(health);
        point.put(luck);
        point.put(basePoint);
        point.put(skillPoint);
        point.put(dedicationPoint);
        point.put(baoKich);
        point.put(exp);
        return point.toString();
    }
}
