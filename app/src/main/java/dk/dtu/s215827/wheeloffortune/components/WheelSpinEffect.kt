package dk.dtu.s215827.wheeloffortune.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dk.dtu.s215827.wheeloffortune.GameStatus
import dk.dtu.s215827.wheeloffortune.WheelResult

// Heavily inspired by:
// https://nascimpact.medium.com/jetpack-compose-working-with-rotation-animation-aeddc5899b28
@Composable
fun WheelSpinEffect(
    status: GameStatus,
    wheelPosition: Float,
    wheelResult: WheelResult,
    onRotationChange: (rotation: Float) -> Unit
) {
    // Variables used for the actual animation of the rotation
    var currentRotation by remember { mutableStateOf(0f) }
    val rotation = remember { Animatable(currentRotation) }

    // We perform a launched effect for status, to spin the wheel at WHEEL_SPINNING
    // This has to be run from a composition-aware scope, due to animateTo
    // https://developer.android.com/jetpack/compose/side-effects#launchedeffect
    LaunchedEffect(status) {
        // Check for the WHEEL_SPINNING status
        if (status == GameStatus.WHEEL_SPINNING) {
            rotation.animateTo(
                // Spin from current rotation to reset, then two times around, and to wanted position
                targetValue = currentRotation + (360 - (currentRotation % 360)) + 360 * 2 - wheelPosition,
                animationSpec = tween(
                    durationMillis = 2500,
                    easing = LinearOutSlowInEasing
                )
            ) {
                currentRotation = value
                onRotationChange(currentRotation)
            }

            // Apply the result when the animation is done
            wheelResult.applyResult()
        }
    }
}
