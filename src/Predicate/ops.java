package Predicate;

public
    enum ops {
        CONJ('&'),
        DIS('|'),
        NEG('!'),
        IMPL('-'),
        PRED('0'),
        EXISTS('?'),
        ANY('@');

        public ops next() {
            switch (this) {
                case DIS:
                    return CONJ;
                case CONJ:
                    return NEG;
                case NEG:
                    return IMPL;
                case IMPL:
                    return DIS;
                default:
                    return null;
            }
        }
        private char val;

        ops(char c) {
            this.val = c;
        }

        public static ops fromChar(char c) {
            for (ops a: ops.values()) {
            if (c == a.getChar())
                return a;
            }
            assert false: c;
            return null;
        }

        public char getChar() {
            return this.val;
        }
    }


