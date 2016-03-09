package HW2;

public
    enum ops {
        CONJ('&'),
        DIS('|'),
        NEG('!'),
        IMPL('-');

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

        private ops(char c) {
            this.val = c;
        }

        public char getChar() {
            return this.val;
        }
    }

