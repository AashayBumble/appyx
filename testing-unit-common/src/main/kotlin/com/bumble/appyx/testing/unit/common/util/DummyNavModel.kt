package com.bumble.appyx.testing.unit.common.util

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver

class DummyNavModel<Routing : Any, State> : BaseNavModel<Routing, State>(
    savedStateMap = null,
    finalState = null,
    screenResolver = object : OnScreenStateResolver<State> {
        override fun isOnScreen(state: State) = true
    }
) {
    override val initialElements: NavElements<Routing, State>
        get() = emptyList()

}
