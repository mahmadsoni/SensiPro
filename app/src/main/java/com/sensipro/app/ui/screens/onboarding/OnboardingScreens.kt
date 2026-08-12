package com.sensipro.app.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sensipro.app.R
import com.sensipro.app.ui.components.NeonButton
import com.sensipro.app.ui.theme.NeonCyan
import com.sensipro.app.ui.theme.TextPrimary
import com.sensipro.app.ui.theme.TextSecondary

@Composable
fun OnboardingWelcomeScreen(onNext: () -> Unit) {
    OnboardingScaffold(
        icon = Icons.Filled.SportsEsports,
        title = stringRes(R.string.onboarding_welcome_title),
        body = stringRes(R.string.onboarding_welcome_subtitle),
        primaryLabel = stringRes(R.string.onboarding_next),
        onPrimary = onNext
    )
}

@Composable
fun OnboardingInfoScreen(onNext: () -> Unit) {
    OnboardingScaffold(
        icon = Icons.Filled.Insights,
        title = stringRes(R.string.onboarding_info_title),
        body = stringRes(R.string.onboarding_info_body),
        primaryLabel = stringRes(R.string.onboarding_next),
        onPrimary = onNext
    )
}

@Composable
fun OnboardingPrivacyScreen(onNext: () -> Unit) {
    OnboardingScaffold(
        icon = Icons.Filled.GppGood,
        title = stringRes(R.string.onboarding_privacy_title),
        body = stringRes(R.string.onboarding_privacy_body),
        primaryLabel = stringRes(R.string.onboarding_next),
        onPrimary = onNext
    )
}

@Composable
fun OnboardingAnalyzeScreen(onAnalyze: () -> Unit) {
    OnboardingScaffold(
        icon = Icons.Filled.Insights,
        title = stringRes(R.string.onboarding_analyze_button),
        body = stringRes(R.string.disclaimer),
        primaryLabel = stringRes(R.string.onboarding_analyze_button),
        onPrimary = onAnalyze
    )
}

@Composable
private fun OnboardingScaffold(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    primaryLabel: String,
    onPrimary: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(96.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.height(72.dp)
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = body,
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(36.dp))
        NeonButton(text = primaryLabel, onClick = onPrimary)
    }
}

@Composable
private fun stringRes(id: Int): String = androidx.compose.ui.res.stringResource(id = id)
