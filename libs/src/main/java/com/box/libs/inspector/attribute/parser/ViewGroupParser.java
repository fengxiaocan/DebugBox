package com.box.libs.inspector.attribute.parser;

import android.view.ViewGroup;

import com.box.libs.inspector.attribute.IParser;
import com.box.libs.inspector.model.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2018/6/19.
 */

public class ViewGroupParser implements IParser<ViewGroup> {

    @Override
    public List<Attribute> getAttrs(ViewGroup view) {
        List<Attribute> attributes = new ArrayList<>();

        Attribute childCountDrawAttribute = new Attribute("childCount",
                String.valueOf(view.getChildCount()));
        attributes.add(childCountDrawAttribute);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Attribute clipChildrenDrawAttribute = new Attribute("clipChildren",
                    String.valueOf(view.getClipChildren()));
            attributes.add(clipChildrenDrawAttribute);
        }

        Attribute willNotDrawAttribute = new Attribute("willNotDraw",
                String.valueOf(view.willNotDraw()));
        attributes.add(willNotDrawAttribute);

        return attributes;
    }
}
