package com.clarolab.model.manual.types;

public enum PlanStatusType {

    PENDING(0) {
        @Override
        public int getPriority() {
            return 1;
        }
    },
    ALERT(1) {
        @Override
        public int getPriority() {
            return 4;
        }
    },
    UNDEFINED(2) {
        @Override
        public int getPriority() {
            return 6;
        }
    },
    BLOCKED(3) {
        @Override
        public int getPriority() {
            return 5;
        }
    },
    PAUSED(4) {
        @Override
        public int getPriority() {
            return 2;
        }
    },
    COMPLETED(5) {
        @Override
        public int getPriority() {
            return 3;
        }
    },
    NO(6) {
        @Override
        public int getPriority() {
            return 8;
        }
    },
    DEPRECATED(7) {
        @Override
        public int getPriority() {
            return 7;
        }
    };

    private final int type;

    PlanStatusType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public abstract int getPriority();

}