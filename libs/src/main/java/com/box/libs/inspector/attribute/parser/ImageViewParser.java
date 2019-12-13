package com.box.libs.inspector.attribute.parser;

import android.widget.ImageView;

import com.box.libs.inspector.attribute.IParser;
import com.box.libs.inspector.model.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjiang on 2018/6/19.
 */

public class ImageViewParser implements IParser<ImageView> {

    private static String scaleTypeToStr(ImageView.ScaleType scaleType) {
        switch (scaleType) {
            case CENTER:
                return "CENTER";
            case FIT_XY:
                return "FIT_XY";
            case MATRIX:
                return "MATRIX";
            case FIT_END:
                return "FIT_END";
            case FIT_START:
                return "FIT_START";
            case FIT_CENTER:
                return "FIT_CENTER";
            case CENTER_CROP:
                return "CENTER_CROP";
            case CENTER_INSIDE:
                return "CENTER_INSIDE";
            default:
                return "OTHER";
        }
    }

    @Override
    public List<Attribute> getAttrs(ImageView view) {
        List<Attribute> attributes = new ArrayList<>();
        Attribute scaleTypeAttribute = new Attribute("scaleType",
                scaleTypeToStr(view.getScaleType()), Attribute.Edit.SCALE_TYPE);
        attributes.add(scaleTypeAttribute);
        return attributes;
    }
}
