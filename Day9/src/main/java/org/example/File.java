package org.example;

import com.google.common.base.Strings;

public class File extends FileSpace {

    int id;

    public File(int id, int space) {
        this.id = id;
        this.space = space;
    }

    @Override
    public String toString() {
        return Strings.repeat(String.valueOf(id), space);
    }
}
