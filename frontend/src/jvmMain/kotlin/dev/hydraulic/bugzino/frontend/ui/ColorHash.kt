import androidx.compose.ui.graphics.Color


/**
 * Returns a color hash of the given object as a hue, saturation, lightness triple.
 *
 * To convert to RGB use a color math library. If the object is an integer it's hashed with [fastSmallIntegerHash] and used, otherwise
 * the `hashCode` of the object is used.
 *
 * @param obj The object to hash, can be anything as the hashCode is used.
 * @param slOptions An array of positions on the saturation/lightness spectrum that the object is hashed onto (i.e. picked at random).
 */
fun colorHash(obj: Any, slOptions: FloatArray = floatArrayOf(0.25f, 0.3f, 0.5f)): Color {
    val hash = if (obj is Int) fastSmallIntegerHash(obj) else obj.hashCode()
    val hue = hash.mod(359).toFloat()
    val saturation = slOptions[(hash / 360).mod(slOptions.size)]
    val lightness = slOptions[(hash / 360 / slOptions.size).mod(slOptions.size)]
    return Color.hsl(hue, saturation, lightness).copy(alpha = 0.8f)
}

/**
 * Hashes an integer to another integer, using [a trivial algorithm that nonetheless has good properties](https://stackoverflow.com/a/12996028/2248578).
 */
fun fastSmallIntegerHash(x: Int): Int {
    val x1 = x.shr(16).xor(x) * 0x45d9f3b
    val x2 = x1.shr(16).xor(x1) * 0x45d9f3b
    val x3 = x2.shr(16).xor(x2)
    return x3
}
