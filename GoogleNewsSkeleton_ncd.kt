package skeletons

import archives.GoogleNewsEndPoint
import archives.LoadedArticle
import archives.googleNewsSequence
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.duckko.duckDuckGoSequence
import org.openrndr.events.Event
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.shadow.DropShadow
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.launch
import org.openrndr.shape.Rectangle
import org.openrndr.text.writer
import kotlin.math.round
import kotlin.math.sqrt

/*
    Before you can use this you have to request an API key from  https://newsapi.org/s/google-news-api
    You will receive a key within minutes. That key should be placed in src/main/resources/googlenews.properties
 */

fun main() = application {
    class Entry(var country: String, var risk_factor: Int, var a0: Int, var b0: Int, var c0: Int)
    val table = listOf(
            Entry("Hungary",33, 6, 5, 3),
            Entry("the Netherlands",24, 5, 4, 4),
            Entry("Greece" ,20, 4, 5, 0),
            Entry("Bangladesh",51, 7, 7, 2),
            Entry("Italy",16, 4, 4, 0) ,
            Entry("the United Arab Emirates",76, 9, 8, 4),
            Entry("India",56, 8, 7, 0),
            Entry("Egypt",53, 7, 7, 4),
            Entry("the USA",33, 6, 5, 3)
            )
    configure {
        width = 600
        height = 800
    }
    program {
        var ti=3
        // -- per country
        //val archive = googleNewsSequence(GoogleNewsEndPoint.TopHeadlines, country = "it").iterator()
        val archive = duckDuckGoSequence("nature beauty of " + table[ti].country).iterator()

        // -- per query
        //val archive = googleNewsSequence(GoogleNewsEndPoint.Everything, query= "health", language = "hu").iterator()
        var article = archive.next().load()
        val gui = GUI()

        val onNextArticle = Event<LoadedArticle>()
        val settings = @Description("Settings") object {
            @ActionParameter("Next article")
            fun nextArticle() {
                val next = GlobalScope.async {
                    archive.next()
                }

                launch {
                    var newArticle = next.await().load()
                    article.destroy()
                    article = newArticle
                    onNextArticle.trigger(newArticle)
                }
            }
        }

        val composite = compose {
            // -- image layer
            layer {
                draw {
                    if (article.images.isNotEmpty()) {
                        drawer.imageFit(article.images[0], 0.0, 0.0, width * 1.0, height * 1.0)
                    }
                }
            }
            layer {
                draw {
                    drawer.fill = ColorRGBa.RED
                    var p = table[ti].risk_factor
                    var a1 = table[ti].a0
                    var b1 = table[ti].b0 // round(p/a1)
                    var c1 = table[ti].c0 // p - (a1*b1)
                    var ratio=(580.0-p)/(b1+1)

                    for (j in 0 until a1) {
                        for (i in 0 until b1) {
                            drawer.circle((i * ratio)+ratio, (j * ratio)+250.0, (simplex(i*50 + j*63, seconds*0.1) + 1.0) * p*1.0 )
                        }
                    }
                    for (k in 0 until c1) {
                            drawer.circle((b1 * ratio)+ratio, (k * ratio)+250.0, (simplex(b1*50 + k*63, seconds*0.1) + 1.0) * p*1.0 )
                    }
                }
            }
            // -- text layer
            layer {
                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 50.0)
                val font2 = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 80.0)
                //val longText = "The probability of premature death in "+ table[ti].country + " is " + table[ti].risk_factor.toString() +"%"
                draw {
                    if (article.texts.isNotEmpty()) {
                        drawer.fontMap = font
                        writer {
                            box = Rectangle(50.0, 100.0, 620.0, 400.0)
                            drawer.fill = ColorRGBa.WHITE

                            newLine()
                            text("The probability of premature death in")
                            newLine()
                            text(""+ table[ti].country + " is " )
                            newLine()
                        }
                        writer {
                            box = Rectangle(250.0, 500.0, 100.0, 100.0)
                            drawer.fill = ColorRGBa.PINK
                            drawer.fontMap = font2
                            text(table[ti].risk_factor.toString() +"%" )
                        }
                    }
                }
                //post(DropShadow()).addTo(gui, "2. Drop shadow")
            }
        }
        onNextArticle.trigger(article)


        gui.add(settings)
        extend(gui)
        extend(Screenshots())
        extend {
            composite.draw(drawer)
        }
    }

}