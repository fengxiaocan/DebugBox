package com.box.libs.database;

import com.box.libs.database.protocol.IDescriptor;

import java.io.File;

/**
 * Created by linjiang on 29/05/2018.
 */

public class DatabaseDescriptor implements IDescriptor {
    public final File file;

    public DatabaseDescriptor(File file) {
        this.file = file;
    }

    @Override
    public String name() {
        return file.getName();
    }

    @Override
    public boolean exist() {
        return file.exists();
    }
}
