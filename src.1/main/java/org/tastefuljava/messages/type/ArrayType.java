package org.tastefuljava.messages.type;

import java.util.Objects;

public class ArrayType implements Type {
    private final Type elemType;

    public ArrayType(Type elemType) {
        this.elemType = elemType;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.elemType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArrayType other = (ArrayType) obj;
        if (!Objects.equals(this.elemType, other.elemType)) {
            return false;
        }
        return true;
    }
}
