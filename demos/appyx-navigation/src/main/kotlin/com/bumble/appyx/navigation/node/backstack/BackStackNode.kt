package com.bumble.appyx.navigation.node.backstack

import android.os.Parcelable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.ui.TextButton
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.interactions.core.ui.helper.gestureModifier
import kotlinx.parcelize.Parcelize
import kotlin.random.Random


class BackStackNode(
    buildContext: BuildContext,
    motionController: (UiContext) -> MotionController<InteractionTarget, State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, State<InteractionTarget>> = {
        GestureFactory.Noop()
    },
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(),
    private val childSize: ChildSize = ChildSize.DEFAULT,
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.Child(1)),
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = motionController,
        gestureFactory = gestureFactory,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
        gestureSettleConfig = gestureSettleConfig,
    )
) : ParentNode<BackStackNode.InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = backStack,
) {
    enum class ChildSize {
        DEFAULT,
        MAX,
        MAX_WIDTH,
    }
    sealed class InteractionTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.Child -> node(buildContext) {
                val backgroundColor = remember { colors.shuffled().random() }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (childSize == ChildSize.MAX) {
                                Modifier
                            } else {
                                Modifier.clip(RoundedCornerShape(5))
                            }
                        )
                        .background(backgroundColor)
                        .padding(24.dp)
                        .gestureModifier(backStack, interactionTarget.index.toString())
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            backStack.push(InteractionTarget.Child(Random.nextInt(20)))
                        }
                ) {
                    Text(
                        text = interactionTarget.index.toString(),
                        fontSize = 21.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(appyx_dark)
        ) {
            AppyxComponent(
                clipToBounds = true,
                appyxComponent = backStack,
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxSize()
                    .background(appyx_dark)
                    .then(
                        when (childSize) {
                            ChildSize.DEFAULT -> Modifier.padding(16.dp)
                            ChildSize.MAX -> Modifier.padding(bottom = 16.dp)
                            ChildSize.MAX_WIDTH -> Modifier
                        }
                    ),
            )
            Row(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(text = "Push") {
                    backStack.push(InteractionTarget.Child(Random.nextInt(20)))
                }
                TextButton(text = "Pop") {
                    backStack.pop()
                }
                TextButton(text = "Replace") {
                    backStack.replace(InteractionTarget.Child(Random.nextInt(20)))
                }
                TextButton(text = "New root") {
                    backStack.newRoot(InteractionTarget.Child(Random.nextInt(20)))
                }
            }
        }
    }
}

