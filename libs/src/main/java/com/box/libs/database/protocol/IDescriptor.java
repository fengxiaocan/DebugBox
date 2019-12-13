package com.box.libs.database.protocol;

/**
 * Created by linjiang on 29/05/2018.
 * <p>
 * associated with Driver, can be used to carry some information of the database
 */

public interface IDescriptor {
    String name();

    boolean exist();
}
