package com.bumble.appyx.components.backstack.ui.modal

import com.bumble.appyx.interactions.core.ui.property.impl.ColorOverlay
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs

@Suppress("unused")
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val scale: Scale.Target,
    val colorOverlay: ColorOverlay.Target = ColorOverlay.Target(0f),
)
