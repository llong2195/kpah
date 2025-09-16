package consts;

/**
 *
 * @author ☂️☂️Duy Coder 💖💖
 */
public class ItemEquipConst {
        public static final byte NONE_RANK = 0;
        public static final byte NHAT_PHAM = 1;
        public static final byte NHI_PHAM = 2;
        public static final byte TAM_PHAM = 3;
        public static final byte TU_PHAM = 4;
        public static final byte NGU_PHAM = 5;

        public static final byte NONE_HE = -1;
        public static final byte THUY = 0;
        public static final byte MOC = 1;
        public static final byte HOA = 2;
        public static final byte THO = 3;
        public static final byte KIM = 4;

        public static final byte DAMAGE_MAGIC = 0;
        public static final byte DAMAGE_PHYSIC = 1;
        public static final byte DAMAGE_NONE = 2;

        public static final byte NONE_COLOR = 0;
        public static final byte GREEN_COLOR = 1;
        public static final byte RED_COLOR = 2;
        public static final byte BLUE_COLOR = 3;
        public static final byte PURPLE_COLOR = 4;
        public static final byte YELLOW_COLOR = 5;

        public static final byte ANIMAL_FOREVER = 0;
        public static final byte ANIMAL_EXPIRY = 1;

        public static final byte REPAIR_WEAPON = 0;
        public static final byte REPAIR_EQUIP = 1;
        public static final byte REPAIR_ALL = 2;

        public static final byte MAX_LEVEL_ANIMAL = 4;
        public static final short[] ATTRIBUTE_DEFAULT_ANIMAL = new short[] { 56, 60, 30 };
        public static final short[] ATTRIBUTE_RANDOM_ANIMAL = new short[] { 10, 13, 12, 36 };
        public static final short[] ATTRIBUTE_RANDOM_ANIMAL_SPECIAL = new short[] { 26, 40, 2, 81, 28 };

        public static final String[] HE = new String[] { "thủy", "mộc", "hỏa", "thổ", "kim" };

        public static final byte AO = 0;
        public static final byte QUAN = 1;
        public static final byte MU = 2;
        public static final byte VU_KHI_KIEM = 3;
        public static final byte VU_KHI_DAO = 4;
        public static final byte VU_KHI_BUT = 5;
        public static final byte VU_KHI_BUA = 6;
        public static final byte VU_KHI_CUNG = 7;
        public static final byte NHAN = 8;
        public static final byte DAY_CHUYEN = 9;
        public static final byte GIAY = 10;
        public static final byte GANG = 11;
        public static final byte NGOC = 12;
        public static final byte CUOC = 13;
        public static final byte ANIMAL_GIAP = 14;
        public static final byte ANIMAL_HO_UYEN = 15;
        public static final byte ANIMAL_NON = 16;
        public static final byte ANIMAL_BAN_DAP = 17;
        public static final byte ANIMAL_YEN = 18;
        public static final byte ANIMAL_PHI_LONG = 19;

        public static final String[][] GROUP_KICH = new String[][] {
                        new String[] { "1_0", "8_0" },
                        new String[] { "12_0", "11_0" },
                        new String[] { "0_0", "9_0" },
                        new String[] { "0_0", "9_0" },
                        new String[] { "0_0", "9_0" },
                        new String[] { "0_0", "9_0" },
                        new String[] { "0_0", "9_0" },
                        new String[] { "0_0", "9_0" },
                        new String[] { "2_0", "3_0", "12_0", "11_0" },
                        new String[] { "8_0", "1_0" },
                        new String[] { "2_0", "3_0" },
                        new String[] { "10_0", "8_1" },
                        new String[] { "10_0", "8_1" }
        };

        public static final byte[] KICH_HE = new byte[] {
                        KIM,
                        THUY,
                        MOC,
                        HOA,
                        THO
        };

        public static final byte[] GROUP_KICH_ANIMAL = new byte[] {
                        16,
                        17,
                        15,
                        18,
                        14
        };

        public static final short[] ATTRIBUTE_RANDOM_TIEN_GIAI = new short[] { 83, 84, 85, 86, 87, 92, 78, 79, 88 };
        public static final short[] ATTRIBUTE_RANDOM_TIEN_GIAI_VU_KHI = new short[] { 80, 82, 109 };
        public static final short[] ATTRIBUTE_RANDOM_DONG_AN = new short[] { 28, 29, 30, 31, 32 };
}
