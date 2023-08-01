package com.bumble.appyx.components.backstack.ui.modal

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.components.backstack.operation.Pop
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.Drag
import com.bumble.appyx.interactions.core.ui.gesture.Gesture
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.dragVerticalDirection
import com.bumble.appyx.interactions.core.ui.property.impl.ColorOverlay
import com.bumble.appyx.interactions.core.ui.property.impl.Position
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController

class BackStackModal<InteractionTarget : Any>(
    uiContext: UiContext,
) : BaseMotionController<InteractionTarget, State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
) {
    private val height = uiContext.transitionBounds.heightDp

    private val topMost: TargetUiState =
        TargetUiState(
            position = Position.Target(value = Position.Value(offset = DpOffset(0f.dp, 48.dp)), easing = LinearOutSlowInEasing),
            scale = Scale.Target(
                value = 1f,
                origin = TransformOrigin(0.5f, 0.0f)
            ),
        )

    private val single: TargetUiState =
        TargetUiState(
            position = Position.Target(DpOffset(0.dp, 0.dp)),
            scale = Scale.Target(1f),
        )

    private val incoming: TargetUiState =
        TargetUiState(
            position = Position.Target(DpOffset(0f.dp, height)),
            scale = Scale.Target(
                value = 1f,
                origin = TransformOrigin(0.5f, 0.0f)
            ),
        )

    private fun stacked(isSingleItemStack: Boolean): TargetUiState =
        TargetUiState(
            position = Position.Target(DpOffset(0.dp, if (isSingleItemStack) (4).dp else 34.dp)),
            scale = Scale.Target(
                value = if (isSingleItemStack) 0.93f else 0.90f,
                origin = TransformOrigin(0.5f, if (isSingleItemStack) 0.2f else 0.0f)
            ),
            colorOverlay = ColorOverlay.Target(0.3f),
        )

    override fun State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        stashed.map { element ->
            MatchedTargetUiState(
                element = element,
                targetUiState = stacked(isSingleItemStack = stashed.size == 1)
            )
        } + listOf(active).map { MatchedTargetUiState(it, if (stashed.isEmpty()) single else topMost) } +
                (created + destroyed).mapIndexed { _, element -> MatchedTargetUiState(element, incoming) }

    override fun mutableUiStateFor(uiContext: UiContext, targetUiState: TargetUiState): MutableUiState =
        targetUiState.toMutableState(uiContext)

    class Gestures<InteractionTarget : Any>(
        transitionBounds: TransitionBounds,
    ) : GestureFactory<InteractionTarget, State<InteractionTarget>> {
        private val height = transitionBounds.screenHeightDp
        override fun createGesture(
            state: State<InteractionTarget>,
            delta: Offset,
            density: Density,
        ): Gesture<InteractionTarget, State<InteractionTarget>> {
            val heightInPx = with(density) { height.toPx() }

            return if (dragVerticalDirection(delta) == Drag.VerticalDirection.DOWN) {
                Gesture(
                    operation = Pop(),
                    completeAt = Offset(x = 0f, y = heightInPx)
                )
            } else {
                Gesture.Noop()
            }
        }
    }
}
