package org.firstinspires.ftc.teamcode.ragebait.systems.core

import  com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.ragebait.systems.core.utils.EnumBucket
import org.firstinspires.ftc.teamcode.ragebait.systems.core.utils.ExhaustiveEnumMap
import org.firstinspires.ftc.teamcode.ragebait.systems.core.utils.enumBucket
import org.firstinspires.ftc.teamcode.ragebait.systems.core.utils.exhaustiveEnumMap

class GamepadBinder(
    val gamepad: Gamepad
) {
    enum class Button {
        cross,
        square,
        circle,
        triangle,
        right_bumper,
        left_bumper,
        dpad_up,
        dpad_down,
        dpad_left,
        dpad_right,
        share,
        option,
        guide,
        left_stick_button,
        right_stick_button,
        left_trigger,
        right_trigger,
        // TODO: (maybe) aliases
    }

    private val toggleBinds: EnumBucket<Button, Action> = enumBucket()
    private val invertedToggleBinds: EnumBucket<Button, Action> = enumBucket()
    private val fullBinds: EnumBucket<Button, Action> = enumBucket()
    private val invertedFullBinds: EnumBucket<Button, Action> = enumBucket()
    private val pressBinds: EnumBucket<Button, Action> = enumBucket()
    private val releaseBinds: EnumBucket<Button, Action> = enumBucket()

    /**
     * Binds an action to a button press intuitively
     * Makes the action start on the rising edge, interrupt on the
     * falling edge. Can be inverted with `invert`
     * */
    fun bind_button_full(button: Button, invert: Boolean, action: Action): GamepadBinder {
        if (!invert) {
            fullBinds[button].add(action)
        } else {
            invertedFullBinds[button].add(action)
        }
        return this
    }

    /**
     * Binds an action to be toggled by a button press
     * I.E. starts when first pressed,  cancels on next press
     * Can be inverted to start on init.
     * Currently does not support toggling on the falling edge (I'm genuinely curious as to if that's
     * ever desired behavior)
     * */
    fun bind_button_toggle(button: Button, invert: Boolean, action: Action): GamepadBinder {
        if (!invert) {
            toggleBinds[button].add(action)
        } else {
            invertedToggleBinds[button].add(action)
        }
        return this
    }

    /**
     * Binds an action to the rising edge of a button press
     * Does not cancel the action if button released
     * Beware of restarting actions, currently undefined behavior
     * */
    fun bind_button_press(button: Button, action: Action): GamepadBinder {
        pressBinds[button].add(action)
        return this
    }

    /**
     * Binds an action to the falling edge of a button press
     * Does not cancel the action if button released
     * */
    fun bind_button_release(button: Button, action: Action): GamepadBinder {
        releaseBinds[button].add(action)
        return this
    }

    enum class Analog {
        left_stick_x,
        left_stick_y,
        right_stick_x,
        right_stick_y,
        left_trigger,
        right_trigger,
    }

    fun getAnalog(analog: Analog): Float = when (analog) {
        Analog.left_stick_x -> gamepad.left_stick_x
        Analog.left_stick_y -> gamepad.left_stick_y
        Analog.right_stick_x -> gamepad.right_stick_x
        Analog.right_stick_y -> gamepad.right_stick_y
        Analog.left_trigger -> gamepad.left_trigger
        Analog.right_trigger -> gamepad.right_trigger
    }

    private val analogBinds: EnumBucket<Analog, Pair<(Float) -> Unit, (Float) -> Float>> = enumBucket()

    /**
     * Analog bindings don't directly use actions; they simply run a consumer for when the value
     * gets updated.
     * For example, you could bind that setter for a property in a subsystem to control via a stick
     * */
    fun bind_analog(analog: Analog, consumer: (Float) -> Unit, processor: (Float) -> Float = { it }) {
        analogBinds[analog].add(Pair(consumer, processor))
    }

    private val triggerActuationValue = 0.6
    private val triggerDeActuationValue = 0.4

    private fun handleActuatedTrigger(button: Button, curValue: Float): Boolean =
        if (buttonCurrentStates[button]) {
            curValue > triggerDeActuationValue
        } else {
            curValue > triggerActuationValue
        }

    fun getButtonValue(button: Button): Boolean = when (button) {
            Button.cross -> gamepad.cross
            Button.square -> gamepad.square
            Button.circle -> gamepad.circle
            Button.triangle -> gamepad.triangle
            Button.right_bumper -> gamepad.right_bumper
            Button.left_bumper -> gamepad.left_bumper
            Button.dpad_up -> gamepad.dpad_up
            Button.dpad_down -> gamepad.dpad_down
            Button.dpad_left -> gamepad.dpad_left
            Button.dpad_right -> gamepad.dpad_right
            Button.share -> gamepad.share
            Button.option -> gamepad.options
            Button.guide -> gamepad.guide
            Button.left_stick_button -> gamepad.left_stick_button
            Button.right_stick_button -> gamepad.right_stick_button
            Button.left_trigger -> handleActuatedTrigger(Button.left_trigger, gamepad.left_trigger)
            Button.right_trigger -> handleActuatedTrigger(Button.right_trigger, gamepad.right_trigger)
    }

    private val buttonLastStates: ExhaustiveEnumMap<Button, Boolean> = exhaustiveEnumMap { false }
    private val buttonCurrentStates: ExhaustiveEnumMap<Button, Boolean> = exhaustiveEnumMap { false }

    private fun updatePressStates() {
        Button.entries.forEach {
            // Move old current to last states
            buttonLastStates[it] = buttonCurrentStates[it]
            buttonCurrentStates[it] = getButtonValue(it)
        }
    }

    private fun handleFullBinds() {
        Button.entries.forEach { button ->
            val down = buttonCurrentStates[button]
            val last = buttonLastStates[button]
            val pressed = down && !last
            val released = !down && last

            if (pressed) {
                fullBinds[button].forEach { it.start() }
                invertedFullBinds[button].forEach { it.interrupt() }
            } else if (released) {
                fullBinds[button].forEach { it.interrupt() }
                invertedFullBinds[button].forEach { it.start() }
            }
        }
    }

    private var togglesOn = false
    private fun handleToggleBinds() {
        Button.entries.forEach { button ->
            val pressed = buttonCurrentStates[button] && !buttonLastStates[button]
            if (pressed) {
                if (!togglesOn) {
                    toggleBinds[button].forEach { it.start() }
                    invertedToggleBinds[button].forEach { it.interrupt() }
                } else {
                    toggleBinds[button].forEach { it.interrupt() }
                    invertedToggleBinds[button].forEach { it.start() }
                }
                togglesOn = !togglesOn
            }
        }
    }

    private fun handlePressBinds() {
        Button.entries.forEach {  button ->
            val pressed = buttonCurrentStates[button] && !buttonLastStates[button]
            if (pressed) {
                pressBinds[button].forEach { it.start() }
            }
        }
    }

    private fun handleReleaseBinds() {
        Button.entries.forEach {  button ->
            val released = !buttonCurrentStates[button] && buttonLastStates[button]
            if (released) {
                releaseBinds[button].forEach { it.start() }
            }
        }
    }

    private val analogLastValues: ExhaustiveEnumMap<Analog, Float> = exhaustiveEnumMap { 0.0f }
    private fun handleAnalogBinds() {
        Analog.entries.forEach { analog ->
            val value = getAnalog(analog)
            if (value == analogLastValues[analog]) return
            analogLastValues[analog] = value
            analogBinds[analog].forEach { it.first(it.second(value)) }
        }
    }

    /** Call during loop to handle all the actions
     * Does *not* call loop on the actions themselves (i.e. only schedules and interrupts) */
    fun loop() {
        updatePressStates()
        handleFullBinds()
        handleToggleBinds()
        handlePressBinds()
        handleReleaseBinds()
        handleAnalogBinds()
    }
}