package com.serical.util;

import com.serical.common.ImConstants;
import javafx.scene.control.Alert;

/**
 * JavaFX常用方法
 *
 * @author serical 2020-05-02 11:42:32
 */
public class FxUtil {

    /**
     * 公用显示alert方法
     *
     * @param alertType  类型
     * @param headerText 内容
     */
    public static void alert(Alert.AlertType alertType, String headerText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(ImConstants.systemName);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
}
