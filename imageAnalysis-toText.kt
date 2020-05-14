package examples

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.extensions.Screenshots
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import org.openrndr.text.Cursor
import org.openrndr.text.writer
import tools.dynamicText
import tools.statistics

fun main() = application {
    configure {
        width = 1280
        height = 800
    }
    program {
        val image = loadImage("screenshots/poster5.png")
        val statistics = image.statistics()

        val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 60.0)
        val smallFont = loadFont("data/fonts/IBMPlexMono-Medium.ttf", 32.0)
        // -- filter nice colors, with enough saturation and brightness
        val niceColors = statistics.histogram.colors().filter {
            val hsv = it.first.toHSVa()
            hsv.s > 0.2 && hsv.v > 0.7
        }
        extend(Screenshots())
        extend {
            drawer.imageFit(image, width/2.0 + 20.0, 20.0, width/2.0-40.0, height-40.0)
            // -- pick the most dominant and 'nice' color
            drawer.fill = niceColors.first().first ?: ColorRGBa.YELLOW
            drawer.fontMap = font

            writer {
                box = Rectangle(20.0, 20.0, width/2 - 40.0, height - 40.0)
                gaplessNewLine()
                text("Image Analysis" )

                drawer!!.fontMap = smallFont
                newLine()
                var index = 0
                dynamicText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. ") {
                    drawer!!.fill = niceColors[index % niceColors.size].first
                    index++
                }

            }
        }
    }
}