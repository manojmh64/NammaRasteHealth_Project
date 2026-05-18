package com.nammaraste.health.domain.usecase

import com.nammaraste.health.domain.model.ClassificationResult
import com.nammaraste.health.domain.model.IssueType
import com.nammaraste.health.domain.model.Severity
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Mock AI use case for classifying road damage from an image.
 *
 * In a production app, this would call a TensorFlow Lite model or
 * a REST API (e.g. Vertex AI Vision or Firebase ML Kit).
 *
 * This implementation uses:
 * 1. File size heuristics (larger images tend to capture more severe damage)
 * 2. Weighted random sampling biased by file size bands
 * 3. A rule-based reasoning string
 *
 * Returns a [ClassificationResult] with issue type, severity, confidence, and reasoning.
 */
@Singleton
class ClassifyDamageUseCase @Inject constructor() {

    /**
     * Classifies damage from the provided image file (or returns a default if null).
     *
     * @param imageFile The captured/picked photo. Can be null.
     * @return [ClassificationResult] with AI prediction.
     */
    suspend operator fun invoke(imageFile: File?): ClassificationResult {
        // Simulate network/ML processing delay
        kotlinx.coroutines.delay(800)

        val fileSizeKb = imageFile?.length()?.div(1024) ?: 500L

        return when {
            fileSizeKb < 100 -> classifySmallImage()
            fileSizeKb < 500 -> classifyMediumImage()
            fileSizeKb < 1500 -> classifyLargeImage()
            else -> classifyVeryLargeImage()
        }
    }

    /**
     * Classifies from a text description (used when no image is available).
     */
    suspend fun fromDescription(description: String): ClassificationResult {
        kotlinx.coroutines.delay(400)
        val lower = description.lowercase()

        return when {
            lower.contains("water") || lower.contains("flood") || lower.contains("log") ->
                ClassificationResult(
                    issueType = IssueType.WATERLOGGING,
                    severity = Severity.HIGH,
                    confidence = 0.82f,
                    reasoning = "Keywords 'water/flood/log' detected → Classified as Waterlogging"
                )
            lower.contains("pothole") || lower.contains("hole") || lower.contains("ditch") ->
                ClassificationResult(
                    issueType = IssueType.POTHOLE,
                    severity = Severity.HIGH,
                    confidence = 0.91f,
                    reasoning = "Keywords 'pothole/hole/ditch' detected → Classified as Pothole"
                )
            lower.contains("crack") || lower.contains("break") || lower.contains("split") ->
                ClassificationResult(
                    issueType = IssueType.CRACK,
                    severity = Severity.MEDIUM,
                    confidence = 0.78f,
                    reasoning = "Keywords 'crack/break/split' detected → Classified as Road Crack"
                )
            lower.contains("debris") || lower.contains("stone") || lower.contains("mud") ->
                ClassificationResult(
                    issueType = IssueType.DEBRIS,
                    severity = Severity.LOW,
                    confidence = 0.70f,
                    reasoning = "Keywords 'debris/stone/mud' detected → Classified as Debris"
                )
            else -> randomClassification()
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private fun classifySmallImage(): ClassificationResult {
        // Small photo → likely minor issue
        val issueType = weightedRandom(
            listOf(IssueType.CRACK, IssueType.DEBRIS, IssueType.POTHOLE, IssueType.WATERLOGGING),
            listOf(0.45f, 0.30f, 0.20f, 0.05f)
        )
        val severity = weightedRandom(
            listOf(Severity.LOW, Severity.MEDIUM, Severity.HIGH, Severity.CRITICAL),
            listOf(0.50f, 0.35f, 0.12f, 0.03f)
        )
        return ClassificationResult(
            issueType = issueType,
            severity = severity,
            confidence = (0.65f + Random.nextFloat() * 0.15f),
            reasoning = "Low-resolution image suggests minor surface damage. Detected ${issueType.displayName} with ${severity.displayName} severity."
        )
    }

    private fun classifyMediumImage(): ClassificationResult {
        val issueType = weightedRandom(
            listOf(IssueType.POTHOLE, IssueType.CRACK, IssueType.WATERLOGGING, IssueType.DEBRIS),
            listOf(0.40f, 0.35f, 0.15f, 0.10f)
        )
        val severity = weightedRandom(
            listOf(Severity.LOW, Severity.MEDIUM, Severity.HIGH, Severity.CRITICAL),
            listOf(0.20f, 0.45f, 0.28f, 0.07f)
        )
        return ClassificationResult(
            issueType = issueType,
            severity = severity,
            confidence = (0.72f + Random.nextFloat() * 0.15f),
            reasoning = "Medium-detail image captured. Road surface analysis indicates ${issueType.displayName}. Structural degradation level: ${severity.displayName}."
        )
    }

    private fun classifyLargeImage(): ClassificationResult {
        val issueType = weightedRandom(
            listOf(IssueType.POTHOLE, IssueType.WATERLOGGING, IssueType.CRACK, IssueType.DEBRIS),
            listOf(0.45f, 0.25f, 0.20f, 0.10f)
        )
        val severity = weightedRandom(
            listOf(Severity.LOW, Severity.MEDIUM, Severity.HIGH, Severity.CRITICAL),
            listOf(0.10f, 0.30f, 0.40f, 0.20f)
        )
        return ClassificationResult(
            issueType = issueType,
            severity = severity,
            confidence = (0.80f + Random.nextFloat() * 0.12f),
            reasoning = "High-resolution image detected. AI identified visible road distress patterns. Issue: ${issueType.displayName}, Urgency: ${severity.displayName}."
        )
    }

    private fun classifyVeryLargeImage(): ClassificationResult {
        val issueType = weightedRandom(
            listOf(IssueType.POTHOLE, IssueType.WATERLOGGING, IssueType.CRACK, IssueType.DEBRIS),
            listOf(0.50f, 0.25f, 0.15f, 0.10f)
        )
        val severity = weightedRandom(
            listOf(Severity.MEDIUM, Severity.HIGH, Severity.CRITICAL),
            listOf(0.20f, 0.45f, 0.35f)
        )
        return ClassificationResult(
            issueType = issueType,
            severity = severity,
            confidence = (0.85f + Random.nextFloat() * 0.12f),
            reasoning = "Full-detail image processed. Severe road damage pattern detected. Primary issue: ${issueType.displayName}. Immediate attention required — severity: ${severity.displayName}."
        )
    }

    private fun randomClassification(): ClassificationResult {
        val types = IssueType.entries.filter { it != IssueType.UNKNOWN }
        val severities = Severity.entries
        return ClassificationResult(
            issueType = types.random(),
            severity = severities.random(),
            confidence = 0.55f + Random.nextFloat() * 0.2f,
            reasoning = "Insufficient data for precise classification. Default analysis applied."
        )
    }

    private fun <T> weightedRandom(items: List<T>, weights: List<Float>): T {
        val total = weights.sum()
        var r = Random.nextFloat() * total
        for (i in items.indices) {
            r -= weights[i]
            if (r <= 0) return items[i]
        }
        return items.last()
    }
}
