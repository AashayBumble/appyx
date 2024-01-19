package com.bumble.appyx.navigation.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.AppyxInteractionsContainer
import com.bumble.appyx.interactions.core.gesture.GestureValidator
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.navigation.integration.LocalScreenSize
import com.bumble.appyx.navigation.node.LocalNode
import com.bumble.appyx.navigation.node.Node
import kotlin.math.roundToInt


internal val defaultExtraTouch = 48.dp

@Suppress("UNCHECKED_CAST")
@Composable
fun <NavTarget : Any, ModelState : Any> AppyxNavigationContainer(
    appyxComponent: BaseAppyxComponent<NavTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    gestureValidator: GestureValidator = GestureValidator.defaultValidator,
    gestureExtraTouchArea: Dp = defaultExtraTouch,
    decorator: (@Composable (
        child: ChildRenderer,
        elementUiModel: ElementUiModel<NavTarget>
    ) -> Unit) = { child, _ -> child() }
) {
    val density = LocalDensity.current
    val screenWidthPx = (LocalScreenSize.current.widthDp * density.density).value.roundToInt()
    val screenHeightPx = (LocalScreenSize.current.heightDp * density.density).value.roundToInt()
    val node = LocalNode.current as? Node<NavTarget>
        ?: error("AppyxNavigationContainer called from outside the expected Node tree;" +
            "LocalNode.current=${LocalNode.current}")

        AppyxInteractionsContainer(
            appyxComponent,
            screenWidthPx,
            screenHeightPx,
            modifier,
            clipToBounds,
            gestureValidator,
            gestureExtraTouchArea
        ) { elementUiModel ->
            with(node) {
                Child(elementUiModel, decorator)
            }
        }
}
