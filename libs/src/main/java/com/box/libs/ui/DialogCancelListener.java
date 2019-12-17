package com.box.libs.ui;

import android.content.DialogInterface;

/**
 * @name： BaseApp
 * @package： com.fxc.base.dialog
 * @author: Noah.冯 QQ:1066537317
 * @time: 20:15
 * @version: 1.1
 * @desc： TODO
 */

public class DialogCancelListener implements IDialogListener{

    @Override
    public void onClick(DialogInterface dialog){
        dialog.dismiss();
    }
}
