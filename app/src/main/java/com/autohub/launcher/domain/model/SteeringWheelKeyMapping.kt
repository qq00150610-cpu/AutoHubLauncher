package com.autohub.launcher.domain.model

data class SteeringWheelKeyMapping(
    val keyCode: Int,
    val name: String,
    val shortPressAction: KeyAction,
    val longPressAction: KeyAction,
    val doubleClickAction: KeyAction
)

sealed class KeyAction {
    data class LaunchApp(val packageName: String) : KeyAction()
    data class MediaControl(val control: MediaControlAction) : KeyAction()
    data class SystemAction(val action: SystemActionType) : KeyAction()
    data class NavigationAction(val action: NavigationActionType) : KeyAction()
    object None : KeyAction()
}

enum class MediaControlAction {
    PLAY_PAUSE,
    NEXT,
    PREVIOUS,
    VOLUME_UP,
    VOLUME_DOWN
}

enum class SystemActionType {
    HOME,
    BACK,
    RECENT_APPS,
    NOTIFICATION_PANEL,
    SETTINGS
}

enum class NavigationActionType {
    START_NAVIGATION,
    STOP_NAVIGATION,
    HOME_NAVIGATION,
    WORK_NAVIGATION
}

data class SteeringWheelProfile(
    val name: String,
    val isDefault: Boolean,
    val mappings: Map<Int, SteeringWheelKeyMapping>
)

object SteeringWheelProfiles {
    val DEFAULT_PROFILE = SteeringWheelProfile(
        name = "默认模式",
        isDefault = true,
        mappings = mapOf(
            android.view.KeyEvent.KEYCODE_VOLUME_UP to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_VOLUME_UP,
                name = "音量+",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.VOLUME_UP),
                longPressAction = KeyAction.MediaControl(MediaControlAction.NEXT),
                doubleClickAction = KeyAction.LaunchApp("")
            ),
            android.view.KeyEvent.KEYCODE_VOLUME_DOWN to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_VOLUME_DOWN,
                name = "音量-",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.VOLUME_DOWN),
                longPressAction = KeyAction.MediaControl(MediaControlAction.PREVIOUS),
                doubleClickAction = KeyAction.LaunchApp("")
            ),
            android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                name = "播放/暂停",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.PLAY_PAUSE),
                longPressAction = KeyAction.None,
                doubleClickAction = KeyAction.None
            ),
            android.view.KeyEvent.KEYCODE_MEDIA_NEXT to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_MEDIA_NEXT,
                name = "下一首",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.NEXT),
                longPressAction = KeyAction.None,
                doubleClickAction = KeyAction.None
            ),
            android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS,
                name = "上一首",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.PREVIOUS),
                longPressAction = KeyAction.None,
                doubleClickAction = KeyAction.None
            )
        )
    )

    val MUSIC_PROFILE = SteeringWheelProfile(
        name = "音乐优先",
        isDefault = false,
        mappings = mapOf(
            android.view.KeyEvent.KEYCODE_VOLUME_UP to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_VOLUME_UP,
                name = "音量+",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.VOLUME_UP),
                longPressAction = KeyAction.MediaControl(MediaControlAction.NEXT),
                doubleClickAction = KeyAction.LaunchApp("")
            ),
            android.view.KeyEvent.KEYCODE_VOLUME_DOWN to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_VOLUME_DOWN,
                name = "音量-",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.VOLUME_DOWN),
                longPressAction = KeyAction.MediaControl(MediaControlAction.PREVIOUS),
                doubleClickAction = KeyAction.LaunchApp("")
            )
        )
    )

    val NAVIGATION_PROFILE = SteeringWheelProfile(
        name = "导航优先",
        isDefault = false,
        mappings = mapOf(
            android.view.KeyEvent.KEYCODE_VOLUME_UP to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_VOLUME_UP,
                name = "音量+",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.VOLUME_UP),
                longPressAction = KeyAction.NavigationAction(NavigationActionType.START_NAVIGATION),
                doubleClickAction = KeyAction.None
            ),
            android.view.KeyEvent.KEYCODE_VOLUME_DOWN to SteeringWheelKeyMapping(
                keyCode = android.view.KeyEvent.KEYCODE_VOLUME_DOWN,
                name = "音量-",
                shortPressAction = KeyAction.MediaControl(MediaControlAction.VOLUME_DOWN),
                longPressAction = KeyAction.NavigationAction(NavigationActionType.STOP_NAVIGATION),
                doubleClickAction = KeyAction.None
            )
        )
    )
}
