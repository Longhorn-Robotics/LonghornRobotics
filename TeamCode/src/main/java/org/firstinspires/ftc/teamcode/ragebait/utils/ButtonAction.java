package org.firstinspires.ftc.teamcode.ragebait.utils;

import java.util.function.BooleanSupplier;

public class ButtonAction {
    BooleanSupplier condition;
    Boolean lastState;
    Runnable action;

    public ButtonAction(BooleanSupplier buttonCondition, Runnable buttonAction) {
        condition = buttonCondition;
        lastState = false;
        action = buttonAction;
    }

    public void run() {
        boolean was = lastState;
        boolean is = condition.getAsBoolean();
        if (!was && is) {
            action.run();
        }
        lastState = is;
    }
    public static void doActions(ButtonAction[] buttonActions) {
        for (ButtonAction action : buttonActions) action.run();
    }
}