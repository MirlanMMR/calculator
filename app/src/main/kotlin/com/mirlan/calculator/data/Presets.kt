package com.mirlan.calculator.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object Presets {

    val classic = LayoutPreset(
        id = "classic",
        name = "Classic",
        cols = 4,
        grid = listOf(
            listOf(KeyCell(CalcKey.Clear), KeyCell(CalcKey.Backspace), KeyCell(CalcKey.Percent), KeyCell(CalcKey.Divide)),
            listOf(KeyCell(CalcKey.N7), KeyCell(CalcKey.N8), KeyCell(CalcKey.N9), KeyCell(CalcKey.Multiply)),
            listOf(KeyCell(CalcKey.N4), KeyCell(CalcKey.N5), KeyCell(CalcKey.N6), KeyCell(CalcKey.Minus)),
            listOf(KeyCell(CalcKey.N1), KeyCell(CalcKey.N2), KeyCell(CalcKey.N3), KeyCell(CalcKey.Plus)),
            listOf(KeyCell(CalcKey.N0, colSpan = 2), KeyCell(CalcKey.Decimal), KeyCell(CalcKey.Equals))
        ),
        theme = CalcTheme(
            background    = Color(0xFF0D0D0D),
            numBg         = Color(0xFF1C1C1E), numText  = Color.White,
            opBg          = Color(0xFFFF9F0A), opText   = Color(0xFF0D0D0D),
            fnBg          = Color(0xFF2C2C2E), fnText   = Color.White,
            eqBg          = Color(0xFFFF9F0A), eqText   = Color(0xFF0D0D0D),
            activeOpBg    = Color.White,       activeOpText = Color(0xFFFF9F0A),
            displayText   = Color.White,
            expressionText = Color(0xFF888888),
            buttonShape   = ButtonShape.Circle
        )
    )

    val material = LayoutPreset(
        id = "material",
        name = "Material",
        cols = 4,
        grid = listOf(
            listOf(KeyCell(CalcKey.Clear), KeyCell(CalcKey.Backspace), KeyCell(CalcKey.Percent), KeyCell(CalcKey.Divide)),
            listOf(KeyCell(CalcKey.N7), KeyCell(CalcKey.N8), KeyCell(CalcKey.N9), KeyCell(CalcKey.Multiply)),
            listOf(KeyCell(CalcKey.N4), KeyCell(CalcKey.N5), KeyCell(CalcKey.N6), KeyCell(CalcKey.Minus)),
            listOf(KeyCell(CalcKey.N1), KeyCell(CalcKey.N2), KeyCell(CalcKey.N3), KeyCell(CalcKey.Plus)),
            listOf(KeyCell(CalcKey.N0, colSpan = 2), KeyCell(CalcKey.Decimal), KeyCell(CalcKey.Equals))
        ),
        theme = CalcTheme(
            background    = Color(0xFF1C1B1F),
            numBg         = Color(0xFF2B2930), numText  = Color(0xFFE6E1E5),
            opBg          = Color(0xFF6750A4), opText   = Color.White,
            fnBg          = Color(0xFF3B383F), fnText   = Color(0xFFCAC4D0),
            eqBg          = Color(0xFF6750A4), eqText   = Color.White,
            activeOpBg    = Color(0xFFD0BCFF), activeOpText = Color(0xFF21005D),
            displayText   = Color(0xFFE6E1E5),
            expressionText = Color(0xFF938F99),
            buttonShape   = ButtonShape.RoundedRect,
            cornerRadius  = 28.dp,
            buttonGap     = 10.dp
        )
    )

    val system = LayoutPreset(
        id = "system",
        name = "System",
        cols = 4,
        grid = listOf(
            listOf(KeyCell(CalcKey.MemClear), KeyCell(CalcKey.MemRecall), KeyCell(CalcKey.MemPlus), KeyCell(CalcKey.MemMinus)),
            listOf(KeyCell(CalcKey.Clear), KeyCell(CalcKey.Backspace), KeyCell(CalcKey.Percent), KeyCell(CalcKey.Divide)),
            listOf(KeyCell(CalcKey.N7), KeyCell(CalcKey.N8), KeyCell(CalcKey.N9), KeyCell(CalcKey.Multiply)),
            listOf(KeyCell(CalcKey.N4), KeyCell(CalcKey.N5), KeyCell(CalcKey.N6), KeyCell(CalcKey.Minus)),
            listOf(KeyCell(CalcKey.N1), KeyCell(CalcKey.N2), KeyCell(CalcKey.N3), KeyCell(CalcKey.Plus)),
            listOf(KeyCell(CalcKey.Negate), KeyCell(CalcKey.N0), KeyCell(CalcKey.Decimal), KeyCell(CalcKey.Equals))
        ),
        theme = CalcTheme(
            background    = Color(0xFF0D1117),
            numBg         = Color(0xFF161B22), numText  = Color(0xFFC9D1D9),
            opBg          = Color(0xFF1565C0), opText   = Color.White,
            fnBg          = Color(0xFF21262D), fnText   = Color(0xFF8B949E),
            eqBg          = Color(0xFF1565C0), eqText   = Color.White,
            activeOpBg    = Color(0xFF388BFD), activeOpText = Color.White,
            displayText   = Color(0xFFC9D1D9),
            expressionText = Color(0xFF6E7681),
            buttonShape   = ButtonShape.RoundedRect,
            cornerRadius  = 8.dp,
            buttonGap     = 8.dp
        )
    )

    val retro = LayoutPreset(
        id = "retro",
        name = "Retro",
        cols = 4,
        grid = listOf(
            listOf(KeyCell(CalcKey.MemClear), KeyCell(CalcKey.MemRecall), KeyCell(CalcKey.MemPlus), KeyCell(CalcKey.MemMinus)),
            listOf(KeyCell(CalcKey.Clear), KeyCell(CalcKey.Backspace), KeyCell(CalcKey.Percent), KeyCell(CalcKey.Divide)),
            listOf(KeyCell(CalcKey.N7), KeyCell(CalcKey.N8), KeyCell(CalcKey.N9), KeyCell(CalcKey.Multiply)),
            listOf(KeyCell(CalcKey.N4), KeyCell(CalcKey.N5), KeyCell(CalcKey.N6), KeyCell(CalcKey.Minus)),
            listOf(KeyCell(CalcKey.N1), KeyCell(CalcKey.N2), KeyCell(CalcKey.N3), KeyCell(CalcKey.Plus)),
            listOf(KeyCell(CalcKey.N0, colSpan = 2), KeyCell(CalcKey.Decimal), KeyCell(CalcKey.Equals))
        ),
        theme = CalcTheme(
            background    = Color(0xFFECECEC),
            numBg         = Color(0xFFDCDCDC), numText  = Color(0xFF1A1A1A),
            opBg          = Color(0xFFB0B0B0), opText   = Color(0xFF1A1A1A),
            fnBg          = Color(0xFFC8C8C8), fnText   = Color(0xFF1A1A1A),
            eqBg          = Color(0xFFD32F2F), eqText   = Color.White,
            activeOpBg    = Color(0xFF1A1A1A), activeOpText = Color.White,
            displayText   = Color(0xFF1A1A1A),
            expressionText = Color(0xFF888888),
            buttonShape   = ButtonShape.Square,
            buttonGap     = 4.dp
        )
    )

    val neon = LayoutPreset(
        id = "neon",
        name = "Neon",
        cols = 4,
        grid = listOf(
            listOf(KeyCell(CalcKey.Clear), KeyCell(CalcKey.Backspace), KeyCell(CalcKey.Percent), KeyCell(CalcKey.Divide)),
            listOf(KeyCell(CalcKey.N7), KeyCell(CalcKey.N8), KeyCell(CalcKey.N9), KeyCell(CalcKey.Multiply)),
            listOf(KeyCell(CalcKey.N4), KeyCell(CalcKey.N5), KeyCell(CalcKey.N6), KeyCell(CalcKey.Minus)),
            listOf(KeyCell(CalcKey.N1), KeyCell(CalcKey.N2), KeyCell(CalcKey.N3), KeyCell(CalcKey.Plus)),
            listOf(KeyCell(CalcKey.N0, colSpan = 2), KeyCell(CalcKey.Decimal), KeyCell(CalcKey.Equals))
        ),
        theme = CalcTheme(
            background    = Color(0xFF000000),
            numBg         = Color(0xFF0D0D0D), numText  = Color(0xFFE0FFE0),
            opBg          = Color(0xFF003300), opText   = Color(0xFF00FF88),
            fnBg          = Color(0xFF111111), fnText   = Color(0xFF00CC66),
            eqBg          = Color(0xFF00FF88), eqText   = Color(0xFF000000),
            activeOpBg    = Color(0xFF00FF88), activeOpText = Color(0xFF000000),
            displayText   = Color(0xFF00FF88),
            expressionText = Color(0xFF006633),
            buttonShape   = ButtonShape.RoundedRect,
            cornerRadius  = 12.dp,
            buttonGap     = 10.dp
        )
    )

    val nordic = LayoutPreset(
        id = "nordic",
        name = "Nordic",
        cols = 4,
        grid = listOf(
            listOf(KeyCell(CalcKey.Clear), KeyCell(CalcKey.Backspace), KeyCell(CalcKey.Percent), KeyCell(CalcKey.Divide)),
            listOf(KeyCell(CalcKey.N7), KeyCell(CalcKey.N8), KeyCell(CalcKey.N9), KeyCell(CalcKey.Multiply)),
            listOf(KeyCell(CalcKey.N4), KeyCell(CalcKey.N5), KeyCell(CalcKey.N6), KeyCell(CalcKey.Minus)),
            listOf(KeyCell(CalcKey.N1), KeyCell(CalcKey.N2), KeyCell(CalcKey.N3), KeyCell(CalcKey.Plus)),
            listOf(KeyCell(CalcKey.N0, colSpan = 2), KeyCell(CalcKey.Decimal), KeyCell(CalcKey.Equals))
        ),
        theme = CalcTheme(
            background    = Color(0xFF2E3440),
            numBg         = Color(0xFF3B4252), numText  = Color(0xFFECEFF4),
            opBg          = Color(0xFF81A1C1), opText   = Color(0xFF2E3440),
            fnBg          = Color(0xFF434C5E), fnText   = Color(0xFF88C0D0),
            eqBg          = Color(0xFF5E81AC), eqText   = Color(0xFFECEFF4),
            activeOpBg    = Color(0xFF88C0D0), activeOpText = Color(0xFF2E3440),
            displayText   = Color(0xFFECEFF4),
            expressionText = Color(0xFF4C566A),
            buttonShape   = ButtonShape.RoundedRect,
            cornerRadius  = 16.dp,
            buttonGap     = 10.dp
        )
    )

    val dracula = LayoutPreset(
        id = "dracula",
        name = "Dracula",
        cols = 4,
        grid = listOf(
            listOf(KeyCell(CalcKey.Clear), KeyCell(CalcKey.Backspace), KeyCell(CalcKey.Percent), KeyCell(CalcKey.Divide)),
            listOf(KeyCell(CalcKey.N7), KeyCell(CalcKey.N8), KeyCell(CalcKey.N9), KeyCell(CalcKey.Multiply)),
            listOf(KeyCell(CalcKey.N4), KeyCell(CalcKey.N5), KeyCell(CalcKey.N6), KeyCell(CalcKey.Minus)),
            listOf(KeyCell(CalcKey.N1), KeyCell(CalcKey.N2), KeyCell(CalcKey.N3), KeyCell(CalcKey.Plus)),
            listOf(KeyCell(CalcKey.N0, colSpan = 2), KeyCell(CalcKey.Decimal), KeyCell(CalcKey.Equals))
        ),
        theme = CalcTheme(
            background    = Color(0xFF282A36),
            numBg         = Color(0xFF383A4A), numText  = Color(0xFFF8F8F2),
            opBg          = Color(0xFFFF79C6), opText   = Color(0xFF282A36),
            fnBg          = Color(0xFF44475A), fnText   = Color(0xFF6272A4),
            eqBg          = Color(0xFFBD93F9), eqText   = Color(0xFF282A36),
            activeOpBg    = Color(0xFFFF79C6), activeOpText = Color(0xFF282A36),
            displayText   = Color(0xFFF8F8F2),
            expressionText = Color(0xFF6272A4),
            buttonShape   = ButtonShape.RoundedRect,
            cornerRadius  = 20.dp,
            buttonGap     = 10.dp
        )
    )

    val light = LayoutPreset(
        id = "light",
        name = "Light",
        cols = 4,
        grid = listOf(
            listOf(KeyCell(CalcKey.Clear), KeyCell(CalcKey.Backspace), KeyCell(CalcKey.Percent), KeyCell(CalcKey.Divide)),
            listOf(KeyCell(CalcKey.N7), KeyCell(CalcKey.N8), KeyCell(CalcKey.N9), KeyCell(CalcKey.Multiply)),
            listOf(KeyCell(CalcKey.N4), KeyCell(CalcKey.N5), KeyCell(CalcKey.N6), KeyCell(CalcKey.Minus)),
            listOf(KeyCell(CalcKey.N1), KeyCell(CalcKey.N2), KeyCell(CalcKey.N3), KeyCell(CalcKey.Plus)),
            listOf(KeyCell(CalcKey.N0, colSpan = 2), KeyCell(CalcKey.Decimal), KeyCell(CalcKey.Equals))
        ),
        theme = CalcTheme(
            background    = Color(0xFFF5F5F5),
            numBg         = Color(0xFFFFFFFF), numText  = Color(0xFF1A1A1A),
            opBg          = Color(0xFF1976D2), opText   = Color.White,
            fnBg          = Color(0xFFE8E8E8), fnText   = Color(0xFF555555),
            eqBg          = Color(0xFF1565C0), eqText   = Color.White,
            activeOpBg    = Color(0xFF42A5F5), activeOpText = Color.White,
            displayText   = Color(0xFF1A1A1A),
            expressionText = Color(0xFF999999),
            buttonShape   = ButtonShape.RoundedRect,
            cornerRadius  = 14.dp,
            buttonGap     = 10.dp
        )
    )

    private fun themed(id: String, name: String, theme: CalcTheme) =
        LayoutPreset(id = id, name = name, cols = 4, grid = classic.grid, theme = theme)

    val sand = themed("sand", "Sand", CalcTheme(
        background = Color(0xFFF4EBDD),
        numBg = Color(0xFFFFFFFF), numText = Color(0xFF3B2A1A),
        opBg = Color(0xFFC8702A), opText = Color.White,
        fnBg = Color(0xFFE8DCC8), fnText = Color(0xFF5A4630),
        eqBg = Color(0xFF8C4A1B), eqText = Color.White,
        activeOpBg = Color(0xFFE59A56), activeOpText = Color.White,
        displayText = Color(0xFF3B2A1A), expressionText = Color(0xFF9A8670),
        buttonShape = ButtonShape.RoundedRect, cornerRadius = 14.dp, buttonGap = 10.dp
    ))

    val mint = themed("mint", "Mint", CalcTheme(
        background = Color(0xFFE6F4EF),
        numBg = Color(0xFFFFFFFF), numText = Color(0xFF12332A),
        opBg = Color(0xFF1FA37A), opText = Color.White,
        fnBg = Color(0xFFCFE8DF), fnText = Color(0xFF2D5A4B),
        eqBg = Color(0xFF137A5A), eqText = Color.White,
        activeOpBg = Color(0xFF5BC4A2), activeOpText = Color.White,
        displayText = Color(0xFF12332A), expressionText = Color(0xFF6E9487),
        buttonShape = ButtonShape.RoundedRect, cornerRadius = 14.dp, buttonGap = 10.dp
    ))

    val sunset = themed("sunset", "Sunset", CalcTheme(
        background = Color(0xFF1A1423),
        numBg = Color(0xFF2A2036), numText = Color(0xFFF5E9F0),
        opBg = Color(0xFFFF6B5A), opText = Color(0xFF1A1423),
        fnBg = Color(0xFF3A2D48), fnText = Color(0xFFE2C9E8),
        eqBg = Color(0xFFFFA25C), eqText = Color(0xFF1A1423),
        activeOpBg = Color(0xFFFFFFFF), activeOpText = Color(0xFFFF6B5A),
        displayText = Color(0xFFF5E9F0), expressionText = Color(0xFF8E7A9C),
        buttonShape = ButtonShape.RoundedRect, cornerRadius = 14.dp, buttonGap = 10.dp
    ))

    val ocean = themed("ocean", "Ocean", CalcTheme(
        background = Color(0xFF06232B),
        numBg = Color(0xFF0D3A46), numText = Color(0xFFE3F7F8),
        opBg = Color(0xFF19C3C9), opText = Color(0xFF06232B),
        fnBg = Color(0xFF114B59), fnText = Color(0xFF9FE3E6),
        eqBg = Color(0xFF19C3C9), eqText = Color(0xFF06232B),
        activeOpBg = Color(0xFFFFFFFF), activeOpText = Color(0xFF0D3A46),
        displayText = Color(0xFFE3F7F8), expressionText = Color(0xFF4F8A93),
        buttonShape = ButtonShape.RoundedRect, cornerRadius = 14.dp, buttonGap = 10.dp
    ))

    val lavender = themed("lavender", "Lavender", CalcTheme(
        background = Color(0xFFEEE9F7),
        numBg = Color(0xFFFFFFFF), numText = Color(0xFF2A2140),
        opBg = Color(0xFF7C5CD6), opText = Color.White,
        fnBg = Color(0xFFDDD3F0), fnText = Color(0xFF4B3A78),
        eqBg = Color(0xFF5B3FB0), eqText = Color.White,
        activeOpBg = Color(0xFFA38AE8), activeOpText = Color.White,
        displayText = Color(0xFF2A2140), expressionText = Color(0xFF8E82AB),
        buttonShape = ButtonShape.RoundedRect, cornerRadius = 14.dp, buttonGap = 10.dp
    ))

    val rose = themed("rose", "Rose", CalcTheme(
        background = Color(0xFFFBEAEE),
        numBg = Color(0xFFFFFFFF), numText = Color(0xFF3D1F29),
        opBg = Color(0xFFE0527A), opText = Color.White,
        fnBg = Color(0xFFF4D3DC), fnText = Color(0xFF7A3D50),
        eqBg = Color(0xFFB8325A), eqText = Color.White,
        activeOpBg = Color(0xFFEE86A3), activeOpText = Color.White,
        displayText = Color(0xFF3D1F29), expressionText = Color(0xFFA98390),
        buttonShape = ButtonShape.RoundedRect, cornerRadius = 14.dp, buttonGap = 10.dp
    ))

    val sky = themed("sky", "Sky", CalcTheme(
        background = Color(0xFFE8F2FB),
        numBg = Color(0xFFFFFFFF), numText = Color(0xFF14283D),
        opBg = Color(0xFF2F8FE0), opText = Color.White,
        fnBg = Color(0xFFD2E4F5), fnText = Color(0xFF35577A),
        eqBg = Color(0xFF1B6BB8), eqText = Color.White,
        activeOpBg = Color(0xFF6DB4F0), activeOpText = Color.White,
        displayText = Color(0xFF14283D), expressionText = Color(0xFF7C94AB),
        buttonShape = ButtonShape.RoundedRect, cornerRadius = 14.dp, buttonGap = 10.dp
    ))

    val lemon = themed("lemon", "Lemon", CalcTheme(
        background = Color(0xFFFBF7DC),
        numBg = Color(0xFFFFFFFF), numText = Color(0xFF33300F),
        opBg = Color(0xFFE0B400), opText = Color(0xFF33300F),
        fnBg = Color(0xFFF1EBBB), fnText = Color(0xFF6A6320),
        eqBg = Color(0xFFB38F00), eqText = Color.White,
        activeOpBg = Color(0xFFF2CF3A), activeOpText = Color(0xFF33300F),
        displayText = Color(0xFF33300F), expressionText = Color(0xFFA39D66),
        buttonShape = ButtonShape.RoundedRect, cornerRadius = 14.dp, buttonGap = 10.dp
    ))

    val all = listOf(
        classic, material, system, neon, nordic, dracula, sunset, ocean,
        retro, light, sand, mint, lavender, rose, sky, lemon
    )
}
