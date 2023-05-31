/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.types;

    public enum DeducedReasonType {

        DEFAULT(0),

        NEW_PASS_WAS_PERMANENT(1),

        NEW_PASS_WASNT_FLAKY(2),

        FAIL_DEFAULT(3),

        NEW_FAIL_WAS_PASSING(4),

        NEW_FAIL_WAS_NEW_FAIL_NOT_TRIAGED(5),

        FAIL_IS_PERMANENT(6),

        SKIP_DEFAULT(7),

        CANCELLED_DEFAULT(8),

        SAME_ERROR_TRIAGED_BEFORE(9), //this means that the same test with the same (equal) error was previously triagged

        NEW_PASS_WAS_FAIL(10),

        UNDEFINED(99),

        Rule1(11),
        Rule2(12),
        Rule3(13),
        Rule4(14),
        Rule5(15),
        Rule6(16),
        Rule7(17),
        Rule8(18),
        Rule9(19),
        Rule10(20),
        Rule11(21),
        Rule12(22),
        Rule13(23),
        Rule14(24),
        Rule15(25),
        Rule16(26),
        Rule17(27),
        Rule18(28),
        Rule19(29),
        Rule20(30),
        Rule21(31),
        Rule22(32),
        Rule23(33),
        Rule24(34),
        Rule25(35),
        Rule26(36),
        Rule27(37),
        Rule28(38),
        Rule29(39),
        Rule30(40),
        Rule31(41),
        Rule32(42),
        Rule33(43),
        Rule34(44),
        Rule35(45),
        Rule36(46),
        Rule37(47),
        Rule38(48),
        Rule39(49),
        Rule40(50),
        Rule41(51),
        Rule42(52),
        Rule43(53),
        Rule44(54),
        Rule45(55),
        Rule46(56),
        Rule47(57);

        private final int reasonType;

        DeducedReasonType(int reasonType) {
            this.reasonType = reasonType;
        }

        public int getReasonType() {
            return this.reasonType;
        }
}
