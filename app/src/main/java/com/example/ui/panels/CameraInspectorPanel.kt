package com.example.ui.panels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.ui.CinemaCameraRig
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isPreviewing by viewModel.isPreviewingCamera.collectAsState()
    val isPreviewComplete by viewModel.isCameraPreviewComplete.collectAsState()
    val isRecordingCamera by viewModel.isRecordingCamera.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val isCameraPathVisible by viewModel.isCameraPathVisible.collectAsState()

    val progress by viewModel.exportProgress.collectAsState()
    val exportedPath by viewModel.exportedFilePath.collectAsState()
    val selectedFormat by viewModel.exportFormat.collectAsState()
    val selectedRes by viewModel.exportResolution.collectAsState()

    // Interactive Multi-Motion Camera Recorder State
    val motionClips by viewModel.recordedMotionClips.collectAsState()
    val isRecordingTouchMotion by viewModel.isRecordingTouchMotion.collectAsState()
    val touchRecordingDuration by viewModel.touchRecordingDuration.collectAsState()
    val touchRecordingFrames by viewModel.touchRecordingFrames.collectAsState()
    val activePreviewClipId by viewModel.activePreviewClipId.collectAsState()

    // Cinema Multi-Camera Rigs & Director State
    val cinemaCameras by viewModel.cinemaCameras.collectAsState()
    val activeCinemaCameraId by viewModel.activeCinemaCameraId.collectAsState()
    val areCameraRigsVisible by viewModel.areCameraRigsVisible.collectAsState()

    var selectedPresetToAdd by remember { mutableStateOf("zoomIn") }
    var selectedPresetDuration by remember { mutableStateOf(4.0f) }

    val presetNames = remember {
        mapOf(
            "zoomIn" to Pair("اقتراب من النص (Zoom In)", "Zoom In Towards Text"),
            "zoomOut" to Pair("ابتعاد للخلف (Zoom Out)", "Zoom Out Away From Text"),
            "orbit360" to Pair("دوران 360° حول المشهد", "360° Orbit Around Scene"),
            "top" to Pair("كاميرا علوية (Top Angle)", "Top High Angle Shot"),
            "bottom" to Pair("كاميرا سفلية (Low Angle)", "Bottom Low Angle Shot"),
            "close_up" to Pair("لقطة قريبة (Close-up)", "Close-up Focus Shot"),
            "hero_wide" to Pair("لقطة واسعة (Hero Wide)", "Hero Wide Establishing Shot"),
            "droneFlyby" to Pair("تحليق طائرة درون", "Cinematic Drone Flyby"),
            "craneReveal" to Pair("كشف رافعة Crane", "Vertical Crane Reveal"),
            "left_side" to Pair("جانبية يسار (Left Angle)", "Left Side Angle"),
            "right_side" to Pair("جانبية يمين (Right Angle)", "Right Side Angle")
        )
    }

    val totalDurationSec = motionClips.sumOf { it.durationSec.toDouble() }.toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isArabic) "مسجل ومخرج حركات الكاميرات 3D" else "Cinematic 3D Camera Motion Studio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = if (isArabic) "سجّل حركات الكاميرا بأصبعك وعاينها متسلسلة ثم صدّر الفيديو"
                    else "Record camera paths with finger touch, preview sequence, and export video",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // 2. Interactive Finger Motion Recorder Hub (قسم مسجل حركات الأصبع)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isRecordingTouchMotion) Color(0xFF2A1215) else Color(0xFF1E1E24)
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isRecordingTouchMotion) 2.dp else 1.dp,
                    color = if (isRecordingTouchMotion) Color(0xFFEF4444) else Color(0xFFFFD700).copy(alpha = 0.4f),
                    shape = RoundedCornerShape(14.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isRecordingTouchMotion) Color(0xFFEF4444) else Color(0xFFFFD700))
                        )
                        Text(
                            text = if (isArabic) "مسجل حركات الكاميرا بالأصبع" else "Finger Touch Camera Recorder",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }

                    if (isRecordingTouchMotion) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDC2626)
                        ) {
                            Text(
                                text = if (isArabic) "🔴 جاري التسجيل بالأصبع..." else "🔴 Recording Finger...",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (isRecordingTouchMotion) {
                    // Active Recording Live Status Display
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3F1D24), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isArabic) "👉 حرك الكاميرا بأصبعك الآن على شاشة العرض ثلاثية الأبعاد!"
                            else "👉 Move camera with your finger now on the 3D viewport!",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCA5A5)
                        )
                        Text(
                            text = if (isArabic) "مدة الحركة: ${"%.1f".format(touchRecordingDuration)} ثانية | تم التقاط $touchRecordingFrames إطار"
                            else "Duration: ${"%.1f".format(touchRecordingDuration)}s | Captured $touchRecordingFrames frames",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }

                    // Big Stop & Save Motion Button
                    Button(
                        onClick = { viewModel.stopTouchMotionRecording() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_stop_touch_recording")
                    ) {
                        Icon(Icons.Default.StopCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "⏹ إنهاء وحفظ الحركة كـ (حركة ${motionClips.size + 1})"
                            else "⏹ Stop & Save as (Motion ${motionClips.size + 1})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    // Recording Action Buttons (Touch Record + Capture Pose)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Start Finger Touch Recording Button
                        Button(
                            onClick = { viewModel.startTouchMotionRecording() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("btn_start_touch_recording"),
                            enabled = !isPreviewing && !isExporting
                        ) {
                            Icon(Icons.Default.RadioButtonChecked, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "🔴 تسجيل حركة بأصبعك" else "🔴 Record Finger Motion",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        // Capture Current Pose Button
                        OutlinedButton(
                            onClick = { viewModel.captureCurrentPoseAsMotion() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD700)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFF59E0B)))),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_capture_pose"),
                            enabled = !isPreviewing && !isExporting
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "📸 التقاط الزاوية" else "📸 Snap Pose",
                                color = Color(0xFFFFD700),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Text(
                        text = if (isArabic) "💡 مثال: اضغط تسجيل وحرك أصبعك نحو النص لحفظ حركة 1، ثم ابتعد عنه وسجل حركة 2، لتحصل على تسلسل احترافي متكامل."
                        else "💡 Tip: Tap record and swipe towards text for Motion 1, then swipe away for Motion 2 to create a multi-shot sequence.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // 3. Sequential Preview & Video Export Hub (المعاينة المتسلسلة + تصدير الفيديو)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27272A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isArabic) "المعاينة المتسلسلة وتصدير الفيديو" else "Sequential Preview & Video Export",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = if (isArabic) "إجمالي زمن الحركات: ${"%.1f".format(totalDurationSec)}ث (${motionClips.size} حركات) | $selectedRes $selectedFormat"
                            else "Total: ${"%.1f".format(totalDurationSec)}s (${motionClips.size} motions) | $selectedRes $selectedFormat",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    if (isPreviewing) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF0369A1)
                        ) {
                            Text(
                                text = if (isArabic) "جاري المعاينة..." else "Previewing...",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Action Buttons: 1. Preview Sequential Motions | 2. Export Video
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isPreviewing) {
                        Button(
                            onClick = { viewModel.stopCameraPreview() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_stop_camera_preview")
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "⏹ إيقاف فوري الآن" else "⏹ Instant Stop",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.playAllMotionsSequentially() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F3F46)),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(46.dp)
                                .testTag("btn_preview_camera"),
                            enabled = !isRecordingTouchMotion && !isExporting && motionClips.isNotEmpty()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "1. 🎬 معاينة الحركات متسلسلة" else "1. Preview All Motions",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Step 2: Export Video Button (فقط في نافذة الكاميرا)
                    Button(
                        onClick = { viewModel.exportAllMotionsVideo() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPreviewComplete) Color(0xFF059669) else Color(0xFFDC2626)
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp)
                            .testTag("btn_record_camera_export"),
                        enabled = !isRecordingTouchMotion && !isExporting && !isPreviewing && motionClips.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "2. 🎥 تصدير الفيديو" else "2. Export Video",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Preview Complete Success Banner
                if (isPreviewComplete && !isPreviewing && !isExporting) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF064E3B).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "✅ تمت معاينة الحركات المتسلسلة بنجاح فائق! يمكنك الآن تصدير الفيديو إلى المعرض."
                            else "Sequence preview complete with zero stutter! You can now export video to gallery.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF34D399)
                        )
                    }
                }

                // Export Progress Display
                if (isExporting) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "جاري رندر وتصدير الفيديو: $progress%" else "Rendering Video: $progress%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFFD700)
                            )
                            Button(
                                onClick = { viewModel.emergencyStopAll() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "إلغاء فوري" else "Cancel",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = progress / 100f,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFD700),
                            trackColor = Color(0xFF3F3F46)
                        )
                    }
                }

                // Export Complete Saved Banner
                if (!exportedPath.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF132A1C), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isArabic) "تم تصدير الفيديو بنجاح وحفظه في المعرض!" else "Video exported and saved to Gallery!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = exportedPath.orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // 4. Recorded Camera Motions Pipeline (قائمة حركات الكاميرا المسجلة)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "حركات الكاميرا المسجلة في المشهد (${motionClips.size})"
                else "Recorded Camera Movements (${motionClips.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (motionClips.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearAllRecordedMotions() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Icon(Icons.Default.ClearAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isArabic) "تفريغ الكل" else "Clear All", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (motionClips.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) "لا توجد حركات مسجلة حالياً. اضغط 'تسجيل حركة بأصبعك' أو اختر قالباً جاهزاً بالأسفل."
                        else "No motions recorded yet. Tap 'Record Finger Motion' above or pick a preset below.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Horizontal Chips of Recorded Motions with Individual Preview and Delete
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(motionClips) { index, clip ->
                    val isCurrentPreviewing = activePreviewClipId == clip.id && isPreviewing

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentPreviewing) Color(0xFF1E3A5F) else Color(0xFF18181B)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = if (isCurrentPreviewing) Color(0xFF38BDF8) else Color(0xFFFFD700).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(
                                        imageVector = when (clip.type) {
                                            "touch" -> Icons.Default.TouchApp
                                            "pose" -> Icons.Default.CameraAlt
                                            else -> Icons.Default.Movie
                                        },
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = clip.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "${clip.durationSec}ث ${if (clip.frameCount > 0) "• ${clip.frameCount} إطار" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            // Single Motion Preview Button
                            IconButton(
                                onClick = { viewModel.previewSingleMotionClip(clip.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCurrentPreviewing) Icons.Default.Stop else Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = if (isCurrentPreviewing) Color(0xFFEF4444) else Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Delete Motion Button
                            IconButton(
                                onClick = { viewModel.deleteRecordedMotionClip(clip.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Divider(color = Color(0xFF27272A))

        // 5. Preset Cinematic Motions Library (قوالب سينمائية جاهزة للإضافة السريعة)
        Text(
            text = if (isArabic) "إضافة قوالب حركات سينمائية جاهزة:" else "Add Preset Motion Template:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presetNames.toList()) { p ->
                val isSel = selectedPresetToAdd == p.first
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) Color(0xFFFFD700) else Color(0xFF18181B))
                        .border(1.dp, if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46), RoundedCornerShape(6.dp))
                        .clickable { selectedPresetToAdd = p.first }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("preset_motion_${p.first}")
                ) {
                    Text(
                        text = if (isArabic) p.second.first else p.second.second,
                        color = if (isSel) Color.Black else Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Duration selector & Add Preset Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isArabic) "المدة:" else "Dur:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(listOf(2.0f, 3.0f, 4.0f, 5.0f, 8.0f, 10.0f)) { dur ->
                        val isSel = selectedPresetDuration == dur
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFFFFD700) else Color(0xFF27272A))
                                .clickable { selectedPresetDuration = dur }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${dur.toInt()}ث",
                                color = if (isSel) Color.Black else Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.addPresetMotionClip(selectedPresetToAdd, selectedPresetDuration) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_add_preset_motion")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isArabic) "إضافة القالب" else "Add Template",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // 6. 3D Path Curve Visibility Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Timeline, contentDescription = null, tint = Color(0xFF38BDF8))
                Column {
                    Text(
                        text = if (isArabic) "إظهار مسار حركات الكاميرا ثلاثي الأبعاد (3D Path Curve)" else "Draw Visual 3D Camera Path",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isArabic) "يتم إخفاء المسار واللمبة تلقائياً عند تسجيل وتصدير الفيديو" else "Auto-hidden during video export",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Switch(
                checked = isCameraPathVisible,
                onCheckedChange = { viewModel.toggleCameraPathCurve(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF38BDF8),
                    checkedTrackColor = Color(0xFF3F3F46)
                ),
                modifier = Modifier.testTag("switch_camera_path")
            )
        }

        Divider(color = Color(0xFF27272A), thickness = 1.dp)

        // 7. Cinema Multi-Camera Rigs & Director System (كاميرات الاستوديو السينمائية والتنقل الدقيق)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141720)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header + Badges Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF38BDF8))
                        Column {
                            Text(
                                text = if (isArabic) "كاميرات الاستوديو في المشهد (Multi-Camera Director)" else "3D Cinema Camera Rigs & Director",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = if (isArabic) "مجسمات كاميرات هوليوود 3D مع شارة CAM ورقمها والتنقل السينمائي الفائق"
                                else "Physical 3D cinema camera rigs, badges, coordinates, and precision cuts",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0284C7)
                    ) {
                        Text(
                            text = "${cinemaCameras.size} CAM",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Add Camera At Current Spot & Visibility Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Place Camera Here
                    Button(
                        onClick = { viewModel.addCinemaCameraAtCurrentView() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("btn_add_cinema_camera"),
                        enabled = !isPreviewing && !isExporting
                    ) {
                        Icon(Icons.Default.AddLocationAlt, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "+ وضع كاميرا في هذا المكان" else "+ Place Camera Rig Here",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    // Toggle Rigs Visibility
                    OutlinedButton(
                        onClick = { viewModel.toggleCinemaCameraRigsVisibility(!areCameraRigsVisible) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (areCameraRigsVisible) Color(0xFF38BDF8) else Color.Gray
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    if (areCameraRigsVisible) Color(0xFF38BDF8) else Color.Gray,
                                    if (areCameraRigsVisible) Color(0xFF0284C7) else Color.DarkGray
                                )
                            )
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_toggle_camera_rigs")
                    ) {
                        Icon(
                            imageVector = if (areCameraRigsVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = if (areCameraRigsVisible) Color(0xFF38BDF8) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (areCameraRigsVisible) {
                                if (isArabic) "إخفاء المجسمات" else "Hide Rigs"
                            } else {
                                if (isArabic) "إظهار المجسمات" else "Show Rigs"
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Camera Rigs Quick-Switch Bar (CAM 1, CAM 2, CAM 3...)
                Text(
                    text = if (isArabic) "الانتقال المباشر بين الكاميرات (Live Camera Switcher):"
                    else "Live Camera Switcher (Instant Cuts):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cinemaCameras) { cam ->
                        val isActive = activeCinemaCameraId == cam.id
                        Button(
                            onClick = { viewModel.switchToCinemaCamera(cam.id, smooth = true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isActive) Color(0xFFFFD700) else Color(0xFF1E293B)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("switch_cam_${cam.id}")
                        ) {
                            Icon(
                                Icons.Default.Videocam,
                                contentDescription = null,
                                tint = if (isActive) Color.Black else Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "CAM ${cam.index}",
                                color = if (isActive) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Individual Camera Cards (Detailed Position, Transition Type, Align & Delete)
                cinemaCameras.forEach { cam ->
                    val isActive = activeCinemaCameraId == cam.id
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) Color(0xFF1E293B) else Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isActive) 1.5.dp else 1.dp,
                                color = if (isActive) Color(0xFFFFD700) else Color(0xFF334155),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Top Row: CAM Badge + Name + Coordinates + Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isActive) Color(0xFFFFD700) else Color(0xFF0284C7)
                                    ) {
                                        Text(
                                            text = "CAM ${cam.index}",
                                            color = if (isActive) Color.Black else Color.White,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = cam.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "موقع الكاميرا: (X: ${"%.1f".format(cam.posX)}, Y: ${"%.1f".format(cam.posY)}, Z: ${"%.1f".format(cam.posZ)})",
                                            color = Color(0xFF94A3B8),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    // Align to current view
                                    IconButton(
                                        onClick = { viewModel.alignCinemaCameraToCurrentView(cam.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CameraAlt,
                                            contentDescription = if (isArabic) "محاذاة للرؤية الحالية" else "Align to View",
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Delete camera (if > 1)
                                    if (cinemaCameras.size > 1) {
                                        IconButton(
                                            onClick = { viewModel.deleteCinemaCamera(cam.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = if (isArabic) "حذف الكاميرا" else "Delete Camera",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Bottom Row: Transition Selector + Look Through Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Transition Selector: Cut vs Glide vs Arc
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (isArabic) "الانتقال:" else "Trans:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF94A3B8)
                                    )

                                    val transTypes = listOf(
                                        Triple("glide", "انسيابي Glide", "Glide"),
                                        Triple("cut", "قطع Cut", "Cut"),
                                        Triple("arc", "قوسي Arc", "Arc")
                                    )

                                    transTypes.forEach { (typeKey, labelAr, labelEn) ->
                                        val isSelectedType = cam.transitionType == typeKey
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isSelectedType) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                                .clickable {
                                                    val dur = if (typeKey == "cut") 0f else 2.5f
                                                    viewModel.updateCinemaCameraTransition(cam.id, typeKey, dur)
                                                }
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isArabic) labelAr else labelEn,
                                                color = if (isSelectedType) Color.Black else Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelectedType) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }

                                // Switch to View Button
                                Button(
                                    onClick = { viewModel.switchToCinemaCamera(cam.id, smooth = true) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isActive) Color(0xFFFFD700) else Color(0xFF334155)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(
                                        Icons.Default.RemoveRedEye,
                                        contentDescription = null,
                                        tint = if (isActive) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "رؤية" else "View",
                                        color = if (isActive) Color.Black else Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Director Mode Master Sequence & Movie Render
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isArabic) "🎬 وضع المخرج السينمائي (Director Master Sequence):"
                            else "🎬 Director Master Sequence Control:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = if (isArabic) "يتم تشغيل الكاميرات بالتسلسل (CAM 1 ➔ CAM 2 ➔ CAM 3...) بالقطع أو التحليق الانسيابي بدقة فائقة مع إخفاء مجسمات الكاميرات تلقائياً أثناء الفيديو."
                            else "Automated director sequence transitioning across all cameras with seamless cuts and glides. Rigs auto-hidden in export.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Play Director Sequence
                            Button(
                                onClick = { viewModel.playMultiCameraDirectorSequence() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
                                    .testTag("btn_play_director_sequence"),
                                enabled = !isPreviewing && !isExporting && cinemaCameras.isNotEmpty()
                            ) {
                                Icon(Icons.Default.MovieFilter, contentDescription = null, tint = Color(0xFFFFD700))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "معاينة المخرج" else "Director Preview",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            // Export Director Movie
                            Button(
                                onClick = { viewModel.exportMultiCameraDirectorVideo() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(44.dp)
                                    .testTag("btn_export_director_movie"),
                                enabled = !isPreviewing && !isExporting && cinemaCameras.isNotEmpty()
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "🎥 تصدير فيلم المخرج" else "🎥 Export Director Movie",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

