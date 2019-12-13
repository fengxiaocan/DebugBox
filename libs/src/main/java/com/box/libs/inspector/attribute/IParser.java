package com.box.libs.inspector.attribute;

import android.view.View;

import com.box.libs.inspector.model.Attribute;

import java.util.List;

/**
 * Created by linjiang on 2018/6/19.
 * <p>
 * T is the supported View type and its subclasses are also parsed
 */

public interface IParser<T extends View> {
    List<Attribute> getAttrs(T view);

}
