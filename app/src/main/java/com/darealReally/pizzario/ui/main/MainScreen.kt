package com.darealReally.pizzario.ui.main

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darealReally.pizzario.R
import com.darealReally.pizzario.data.AnimationProps
import com.darealReally.pizzario.data.Location
import com.darealReally.pizzario.data.Status
import com.darealReally.pizzario.ui.theme.*
import com.google.accompanist.insets.LocalWindowInsets
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import android.graphics.Path as NativePath
import com.darealReally.pizzario.testData.locations as locationsSample

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    // State
    val scope = rememberCoroutineScope()
    val sheetScaffoldState = rememberBottomSheetScaffoldState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locationsSample[0].coordinates, 13.8F)
    }
    val setCameraPositionState: (coordinates: LatLng) -> Unit = {
        scope.launch {
            // collapse sheet before changing position
            if (sheetScaffoldState.bottomSheetState.isExpanded) {
                sheetScaffoldState.bottomSheetState.collapse()
            }
            // zoom into location
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(it, 15F)),
                400
            )
        }
    }

    // Props
    val topInset = LocalWindowInsets.current.systemBars.top
    val bottomInset = LocalWindowInsets.current.systemBars.bottom
    val topInsetDp = with(LocalDensity.current) { topInset.toDp().value }
    val bottomInsetDp = with(LocalDensity.current) { bottomInset.toDp().value }

    val screenHeightDp = LocalConfiguration.current.screenHeightDp + topInsetDp + bottomInsetDp
    val navBarHeight = 51
    val sheetHeightDp = screenHeightDp - (topInsetDp + navBarHeight + 12) // 12: spacing

    // UI
    BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
        sheetContent = {
            SheetContent(
                height = sheetHeightDp.roundToInt(),
                setCameraPositionState = setCameraPositionState
            )
       },
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetPeekHeight = (screenHeightDp * 0.6).dp,
        sheetShape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp
        ),
        content = {
            BackContent(
                isPreview = false,
                paddingValues = it,
                cameraPositionState = cameraPositionState
            )
        }
     )
}


@Composable
fun BackContent(
    isPreview: Boolean = true,
    paddingValues: PaddingValues,
    cameraPositionState: CameraPositionState
) {
    // Props
    val context = LocalContext.current
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    val properties by remember {
        mutableStateOf(
            MapProperties(mapStyleOptions = MapStyleOptions
                .loadRawResourceStyle(context, R.raw.style_json)
            )
        )
    }
    val bottomPadding = paddingValues.calculateBottomPadding()

    // UI
    Box(
        modifier = Modifier
            .padding(bottom = bottomPadding - 28.dp)
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {

        // Layer 1: MAP
        if (!isPreview) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = properties
            ) {
                locationsSample.forEach {
                    PizzaMarker(location = it)
                }
            }
        }

        // Layer 2: NAVIGATION
        NavigationBar()

    }
}


@Composable
fun NavigationBar(
    onClick: () -> Unit = {}
) {
    // Props
    val top = LocalWindowInsets.current.systemBars.top
    val topInsetDp = with(LocalDensity.current) {
        top.toDp().value
    }

    // UI
    Row(
        modifier = Modifier
            .padding(top = topInsetDp.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Col 1: BACK
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = MaterialTheme.colors.background,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .fillMaxSize(0.4F)
                    .padding(end = 2.dp),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = MaterialTheme.colors.primary
            )
        }

        // Col 2: TITLE
        Text(
            text = "Pizzario",
            color = MaterialTheme.colors.primary,
            fontFamily = Helvetica,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

    }
}


@Composable
fun SheetContent(
    locations: List<Location> = locationsSample,
    height: Int? = null,
    setCameraPositionState: (coordinates: LatLng) -> Unit = { _ -> }
) {
    // Props
    val modifier =
        if (height == null) Modifier.wrapContentHeight()
        else Modifier.height(height.dp)
    val bottomInset = LocalWindowInsets.current.systemBars.bottom
    val bottomInsetDp = with(LocalDensity.current) { bottomInset.toDp().value }

    // UI
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {

        // Layer 1:
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Row 1: HANDLE
            Spacer(Modifier.height(10.dp))

            Box(
                Modifier
                    .height(4.5.dp)
                    .width(32.dp)
                    .background(
                        color = MaterialTheme.colors.primary.copy(0.2F),
                        shape = RoundedCornerShape(50)
                    ),
            )

            // Row 2: HEADER
            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Stores",
                    color = MaterialTheme.colors.primary,
                    fontFamily = Helvetica,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 0.sp
                )

                Text(
                    modifier = Modifier.offset(y = (-4).dp), // reduces font padding
                    text = "Found ${locations.size}",
                    color = MaterialTheme.colors.primary.copy(alpha = 0.5F),
                    fontFamily = Helvetica,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp
                )
            }

            // Row 3: LOCATIONS
            LazyColumn(
                modifier = Modifier.offset(y = (-5).dp)
            ) {
                items(locations) { location ->
                    LocationRow(location) {
                        setCameraPositionState(location.coordinates)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(bottomInsetDp.dp))
                }
            }
        }

        // Layer 2:
        BackgroundIllustration(modifier = Modifier.padding(top = 10.dp))
    }

}


@Composable
fun LocationRow(
    location: Location = Location(),
    onClick: () -> Unit = {}
) {
    // Props
    val (branchName, address1, address2, distance, status) = location
    val statusColor =
        when (status) {
            Status.Open -> MaterialTheme.colors.secondary
            Status.Close -> MaterialTheme.colors.error
        }

    // UI
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(top = 13.dp)
            .fillMaxWidth()
    ) {

        // Row 1:
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {

            // Col 1: LEFT
            Column(
                modifier = Modifier
                    .weight(0.6F),
                horizontalAlignment = Alignment.Start
            ) {

                // Row 1: BRANCH NAME
                Text(
                    text = branchName,
                    color = MaterialTheme.colors.primary,
                    fontFamily = Helvetica,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp
                )

                // Row 2: ADDRESS 1 & 2
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    // Col 1: ADDRESS 1
                    Text(
                        modifier = Modifier.offset(y = (-3).dp), // reduces font padding
                        text = address1,
                        color = MaterialTheme.colors.primary,
                        fontFamily = Helvetica,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )

                    // Col 2: DIVIDER
                    Text(
                        modifier = Modifier.offset(y = (-3).dp), // reduces font padding
                        text = "|",
                        color = MaterialTheme.colors.primary.copy(alpha = 0.2F),
                        fontFamily = Helvetica,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    // Col 3: ADDRESS 2
                    Text(
                        modifier = Modifier.offset(y = (-3).dp), // reduces font padding
                        text = address2,
                        color = MaterialTheme.colors.primary,
                        fontFamily = Helvetica,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )

                }

                // Row 3: DISTANCE
                Text(
                    modifier = Modifier.offset(y = (-3).dp), // reduces font padding
                    text = "$distance km",
                    color = MaterialTheme.colors.primary,
                    fontFamily = Helvetica,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )

                // Row 4: SEAT AVAILABILITY PERCENTAGE
                Spacer(Modifier.height((16-4).dp))

                SeatAvailabilityLevel(location = location)
            }

            // Col 2: RIGHT
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Row 1: STATUS
                Text(
                    text = status.name.uppercase(),
                    color = statusColor,
                    style = MaterialTheme.typography.subtitle1.copy(
                        shadow = Shadow(
                            color = statusColor.copy(alpha = 0.3F),
                            offset = Offset(0F, 1F),
                            blurRadius = 10F
                        )
                    )
                )

                // Row 2: CALL BUTTON
                Spacer(Modifier.height(23.dp))

                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .width(43.dp)
                        .clip(CircleShape)
                        .clickable(onClick = {}),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Row 1: CALL ICON
                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = "Call",
                        tint = MaterialTheme.colors.primary
                    )

                    // Row 2: CALL TITLE
                    Text(
                        text = "Call",
                        color = MaterialTheme.colors.primary,
                        fontFamily = Helvetica,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )

                }

            }


        }

        // Row 2: DIVIDER
        Spacer(
            Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(1.2.dp)
                .background(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.2F),
                )
        )
    }

}


@Composable
fun SeatAvailabilityLevel(
    location: Location = Location(seatsPercentage = 0.3F)
) {
    // Props
    val percentage = remember {
        Animatable(initialValue = 0F)
    }
    val descText =
        if (percentage.value == 0F) "NA"
        else "${(percentage.value*100).roundToInt()}% Seats Available"

    // Side Effect
    // triggers the animation from initial to target state
    LaunchedEffect(Unit) { // starts to animate when this composable enters the tree
        percentage.animateTo(
            targetValue = location.seatsPercentage,
            animationSpec = tween(
                durationMillis = 2000,
                easing = LinearOutSlowInEasing,
                delayMillis = 1000
            )
        )
    }

    // UI
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {

        // Row 1: PERCENTAGE BAR
        Box(
            modifier = Modifier
                .width(131.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(
                    color = MaterialTheme.colors.primary.copy(0.2F)
                )
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage.value)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colors.primary)
            )
        }

        // Row 2: DESCRIPTION
        Text(
            text = descText,
            color = MaterialTheme.colors.primary.copy(alpha = 0.5F),
            fontFamily = Helvetica,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )

    }
}


fun locationPin(
    size: Size = AnimationProps().pizzaSize,
    shadowOffset: Float = -5F
): ImageBitmap {
    val (imageWidth, imageHeight) = size
    val baseBitmap = Bitmap.createBitmap(
        imageWidth.toInt(),
        imageHeight.toInt(),
        Bitmap.Config.ARGB_8888
    )

    NativeCanvas(baseBitmap).apply {

        drawColor(android.graphics.Color.TRANSPARENT)

        // Circle Pin
        val radius = 30 / 2F
        val cx = width / 2F
        val cy = height - radius - 2

        // fill
        drawCircle(
            cx,
            cy,
            radius - 1F,
            Paint().apply {
                this.color = Blue700.toArgb()
            }
        )

        // outline
        drawCircle(
            cx,
            cy,
            radius,
            Paint().apply {
                this.style = Paint.Style.STROKE
                this.strokeWidth = 4F
                this.color = Black800.toArgb()
            }
        )

        // Pizza Shape
        // w: 80 px h: 100 px
        val strokeWidth = 12F
        val trianglePath = NativePath().apply {
            val pHeight = (100F / 120F) * height
            val pWidth = (80F / 120F) * width
            val y1 = strokeWidth + 3F
            val x1 = (width - pWidth) / 2
            this.moveTo(x1, y1)
            this.lineTo(width - x1, y1)
            this.lineTo(width / 2F, pHeight)
            this.lineTo(x1, y1)
        }

        drawPath(
            trianglePath,
            Paint().apply {
                this.setShadowLayer(
                    10F,
                    shadowOffset,
                    shadowOffset,
                    Orange300.copy(alpha = 0.25F).toArgb()
                )
                this.style = Paint.Style.STROKE
                this.strokeCap = Paint.Cap.ROUND
                this.strokeJoin = Paint.Join.ROUND
                this.strokeWidth = strokeWidth
                this.color = Orange300.toArgb()
            }
        )

    }

    return baseBitmap.asImageBitmap()
}


@Composable
fun PizzaMarker(
    location: Location = locationsSample[0]
) {
    // Props
    val (branchName, _, _, _, _, _, coordinates, animationProps) = location
    val (delay, degreesToMove, pizzaSize) = animationProps

    // State
    // holds one or more animations
    val infiniteTransition = rememberInfiniteTransition()

    // tilting pizza animation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = degreesToMove,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearOutSlowInEasing,
                delayMillis = delay
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    // glow animation
    val glow by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = -5F,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = LinearOutSlowInEasing,
                delayMillis = delay
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val locationPinImage = locationPin(
        size = pizzaSize,
        shadowOffset = glow
    ).asAndroidBitmap()

    Marker(
        state = MarkerState(position = coordinates),
        title = branchName,
        snippet = "",
        icon = BitmapDescriptorFactory.fromBitmap(locationPinImage),
        rotation = rotation
    )
}


@Composable
fun BackgroundIllustration(
    modifier: Modifier = Modifier
) {

    // UI
    Canvas(
        modifier = modifier.size(97.2.dp, 73.2.dp)
    ) {
        val meatBorderWidth = 4F.dp.toPx()
        val primaryColor = Color.White
        val alpha = 0.1F

        // Meat
        // dp in wireframe to px
        val length = 15F.dp.toPx() - meatBorderWidth // converted to outer stroke
        val cornerRadius = 2F.dp.toPx()
        val startY = 29.dp.toPx()
        val startX = (5.68 + 1.32).dp.toPx()

        rotate(
            degrees = 22F,
            pivot = Offset(length * 2, length * 2)
        ) {
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(startX, startY),
                size = Size(length, length),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Stroke(width = meatBorderWidth),
                alpha = alpha
            )

        }

        // Pepperoni
        val pepBorderWidth = 4.5F.dp.toPx()
        val radius = (17F.dp.toPx() - pepBorderWidth) / 2  // converted to outer stroke
        val centerX = 27.dp.toPx() + radius
        val centerY = radius + 2.dp.toPx() // with top padding

        drawCircle(
            color = primaryColor,
            radius = radius,
            center = Offset(centerX, centerY),
            alpha = alpha,
            style = Stroke(width = pepBorderWidth)
        )

        // Pizza
        val pizzaBorderWidth = 5.98.dp.toPx()
        val width = 40.65.dp.toPx() - pizzaBorderWidth
        val height = 52.58.dp.toPx() - pizzaBorderWidth
        val y1 = 3.dp.toPx() // with top padding
        val x1 = 60.97.dp.toPx()

        val trianglePath = Path().apply {
            moveTo(x1, y1)
            lineTo(x1 + width, y1)
            moveTo(x1 + width, y1)
            lineTo(x1 + (width / 2F), height)
            lineTo(x1, y1)
        }
        rotate(
            degrees = 45F,
            pivot = Offset(140F, 20F)
        ) {
            drawPath(
                path = trianglePath,
                color = primaryColor,
                alpha = alpha,
                style = Stroke(
                    width = pizzaBorderWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.cornerPathEffect(radius = 30F)
                )
            )
        }

        // Meat
        val meatX1 = 76.dp.toPx()
        val meatX2 = 62.92.dp.toPx() - meatBorderWidth
        rotate(
            degrees = 45F,
            pivot = Offset(250F, 220F)
        ) {
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(meatX1, meatX2),
                size = Size(length, length),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Stroke(width = meatBorderWidth),
                alpha = alpha
            )
        }
    }
}


/**
 * Preview Section
 */
@Preview
@Composable
fun BackContentPreview() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locationsSample[0].coordinates, 13.5F)
    }

    PizzarioTheme {
        BackContent(
            paddingValues = PaddingValues(bottom = 500.dp),
            cameraPositionState = cameraPositionState
        )
    }
}

@Preview(name = "Sheet Content")
@Composable
fun SheetContentPreview() {
    PizzarioTheme {
        SheetContent()
    }
}

@Preview(name = "Navigation Bar")
@Composable
fun NavBarPreview() {
    PizzarioTheme {
        NavigationBar()
    }
}

@Preview(name = "Location Row")
@Composable
fun LocationRowPreview() {
    PizzarioTheme {
        LocationRow()
    }
}

@Preview(name = "Pizza Marker")
@Composable
fun PizzaMarkerPreview() {
    Image(
        bitmap = locationPin(),
        contentDescription = ""
    )
}

@Preview(name = "Background Illustration")
@Composable
fun PizzaIllustrationPreview() {
    BackgroundIllustration()
}