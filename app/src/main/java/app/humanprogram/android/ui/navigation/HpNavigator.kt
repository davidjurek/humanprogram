package app.humanprogram.android.ui

internal data class HpNavigatorState(
    val route: HpRoute = HpRoute.TODAY,
    val selectedProject: String = "",
    val settingsDetail: SettingsDetail? = null
) {
    val isRoot: Boolean
        get() = route == HpRoute.TODAY
}

internal data class ScreenChromeState(
    val routeTitle: String,
    val subtitle: String?,
    val mode: HpMode,
    val primaryActionLabel: String?,
    val canEdit: Boolean,
    val canUndo: Boolean,
    val canRedo: Boolean
)
