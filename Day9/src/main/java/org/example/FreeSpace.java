package org.example;

import com.google.common.base.Strings;

public class FreeSpace extends FileSpace {

    public FreeSpace(int space) {
        this.space = space;
    }

    @Override
    public String toString() {
        return Strings.repeat(".", space);
    }

}
