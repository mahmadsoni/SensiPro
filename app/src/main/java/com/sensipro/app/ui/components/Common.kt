package com.sensipro.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sensipro.app.ui.theme.BackgroundDeep
import com.sensipro.app.ui.theme.NeonCyan
import com.sensipro.app.ui.theme.NeonViolet
import com.sensipro.app.ui.theme.Outline
import com.sensipro.app.ui.theme.SurfaceGlass
import com.sensipro.app.ui.theme.SurfaceGlassElevated
import com.sensipro.app.ui.theme.TextPrimary
import com.sensipro.app.ui.theme.TextSecondary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(listOf(SurfaceGlassElevated, SurfaceGlass)),
                shape = RoundedCornerShape(20.dp)
            )
            .border(1.dp, Outline, RoundedCornerShape(20.dp))
            .padding(18.dp),
        content = content
    )
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonCyan,
            contentColor = BackgroundDeep,
            disabledContainerColor = SurfaceGlassElevated,
            disabledContentColor = TextSecondary
        ),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceGlassElevated,
            contentColor = TextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Outline),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(text, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = modifier.padding(bottom = 10.dp)
    )
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 14.sp)
        Text(value, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun SensitivityValueChip(label: String, value: Int) {
    Box(
        modifier = Modifier
            .background(
                Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.18f), NeonViolet.copy(alpha = 0.18f))),
                RoundedCornerShape(14.dp)
            )
            .border(1.dp, Outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column {
            Text(label, color = TextSecondary, fontSize = 12.sp)
            Text("$value", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}
