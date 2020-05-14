package examples

import org.openrndr.application
import org.openrndr.draw.loadImage
import org.openrndr.extensions.Screenshots
import tools.statistics

fun main() = application {
    configure {
        width = 720
        height = 578
    }
    program {
        val image = loadImage("screenshots/poster5.png")
        val statistics = image.statistics()
        extend(Screenshots())
        extend {
            drawer.image(image)
            for (color in statistics.histogram.colors()) {
                drawer.fill = color.first
                drawer.rectangle(620.0, 0.0, 100.0, 10.0)
                drawer.translate(0.0, 10.0)

            }

        }
    }
}