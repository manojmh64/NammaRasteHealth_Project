package com.nammaraste.health.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.nammaraste.health.domain.model.*
import com.nammaraste.health.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ─── Health Badge ─────────────────────────────────────────────────────────────

/**
 * Colored badge showing road health status.
 */
@Composable
fun HealthBadge(health: RoadHealth, modifier: Modifier = Modifier) {
    val (color, icon) = when (health) {
        RoadHealth.EXCELLENT -> HealthExcellent to Icons.Filled.CheckCircle
        RoadHealth.GOOD      -> HealthGood      to Icons.Filled.ThumbUp
        RoadHealth.FAIR      -> HealthFair      to Icons.Filled.Warning
        RoadHealth.POOR      -> HealthPoor      to Icons.Filled.Error
        RoadHealth.CRITICAL  -> HealthCritical  to Icons.Filled.Dangerous
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.18f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = health.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Severity Badge ───────────────────────────────────────────────────────────

@Composable
fun SeverityBadge(severity: Severity, modifier: Modifier = Modifier) {
    val color = when (severity) {
        Severity.LOW      -> SeverityLow
        Severity.MEDIUM   -> SeverityMedium
        Severity.HIGH     -> SeverityHigh
        Severity.CRITICAL -> SeverityCritical
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.18f),
        modifier = modifier
    ) {
        Text(
            text = severity.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

// ─── Health Score Ring ────────────────────────────────────────────────────────

/**
 * Circular progress ring showing health score 0–100.
 */
@Composable
fun HealthScoreRing(
    score: Double,
    health: RoadHealth,
    size: Dp = 100.dp,
    strokeWidth: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    val color = healthColor(health)
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "healthScore"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Track
            drawCircle(
                color = color.copy(alpha = 0.15f),
                radius = (this.size.minDimension / 2) - strokeWidth.toPx() / 2,
                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth.toPx())
            )
            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = (animatedScore / 100f) * 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${animatedScore.toInt()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = "/100",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

// ─── Road Health Card ─────────────────────────────────────────────────────────

@Composable
fun RoadHealthCard(
    info: RoadHealthInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val healthCol = healthColor(info.healthStatus)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, DarkDivider)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Health indicator bar on left
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(healthCol)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = info.roadName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${info.taluka} · ${info.lengthKm} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HealthBadge(info.healthStatus)
                    Text(
                        text = "${info.complaintCount} complaints",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }

            // Score
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${info.healthScore.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = healthCol,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "score",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }
    }
}

// ─── Stat Card ────────────────────────────────────────────────────────────────

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape)
                    .padding(8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Black
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = Brand300,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = modifier
    )
}

// ─── Gradient Background ──────────────────────────────────────────────────────

@Composable
fun GradientHeader(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Brand700, DarkBackground),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        content = content
    )
}

// ─── Timestamp Formatter ──────────────────────────────────────────────────────

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toShortDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

// ─── Health color helper ──────────────────────────────────────────────────────

fun healthColor(health: RoadHealth): Color = when (health) {
    RoadHealth.EXCELLENT -> HealthExcellent
    RoadHealth.GOOD      -> HealthGood
    RoadHealth.FAIR      -> HealthFair
    RoadHealth.POOR      -> HealthPoor
    RoadHealth.CRITICAL  -> HealthCritical
}
