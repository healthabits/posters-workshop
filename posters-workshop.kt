package skeletons
import archives.LoadedArticle
import archives.localArchive

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgb
import org.openrndr.color.rgba
import org.openrndr.draw.loadFont
import org.openrndr.events.Event
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.compositor.compose
import org.openrndr.extra.compositor.layer
import org.openrndr.extra.compositor.draw
import org.openrndr.extra.compositor.post
import org.openrndr.extra.fx.distort.Perturb
import org.openrndr.extra.fx.shadow.DropShadow
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.gui.addTo
import org.openrndr.extra.noise.random
import org.openrndr.extra.noise.simplex
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import org.openrndr.text.writer
fun main() = application {
    class Entry(var smoking: String, var age: Int, var bp: Int, var chole: Int, var risk_factor: Int)
    val table = listOf(
            Entry("non smoker",58,140,7,1),
            Entry("smoker",50,180,7,3),
            Entry("smoker",86,160,7,13),
            Entry("son smoker",89,180,8,12),
            Entry("smoker",72,160,8,16),
            Entry("smoker",75,180,7,19),
            Entry("non smoker",84,160,8,8),
            Entry("non smoker",60,120,4,1),
            Entry("smoker",72,180,8,22),
            Entry("non smoker",54,120,4,0)
    )
    configure {

        title = "health risk"
        width = 600
        height = 500

    }
    program {

        val archive = localArchive("archives/health-risk/health-risk").iterator()
        var article = archive.next()
        var table_counter= 0
        val gui = GUI()
        val onNextArticle = Event<LoadedArticle>()
        val settings = @Description("Next post") object {
            @ActionParameter("Next")
            fun nextArticle() {
                article = archive.next()
                onNextArticle.trigger(article)
                table_counter++
                if (table_counter>=table.size){
                    table_counter =0
                }
            }
        }
        val composite = compose {
            var background = ColorRGBa.PINK.shade(0.100)
            onNextArticle.listen {
                background = rgb(Math.random(), Math.random(), Math.random()).shade(0.100)
            }
            layer {
                var imageX = 0.0
                var imageY = 0.0
                onNextArticle.listen{

                    imageX = random(0.0,width*1.0)
                    imageY = random(0.0,width*1.0)

                    imageX = article.texts[0].length * 1.0
                }
                draw {
                    if (article.images.isNotEmpty()) {
                        drawer.imageFit(article.images[0], 0.0, 0.0, width * 1.0, height * 1.0)
                    }

                    for (image in article.images){
                        drawer.imageFit(image,0.0,0.0,width*1.0,height*1.0)
                        drawer.translate(40.0,40.0)
                    }

                }
                post(Perturb()){
                    gain = table[table_counter].risk_factor* 1.0
                }.addTo(gui,"Perturbation")

            }

            layer {
                val settings = object {
                    @IntParameter("entry", 0, 9)
                    var tableIndex = 0
                }.addTo(gui,"Change Input")
                draw {
                    val font = loadFont("data/fonts/IBMPlexMono-Regular.ttf", 17.0)
                    drawer.fontMap = font
                    // here we visualize the box
                    drawer.fill = ColorRGBa.PINK.shade(0.1)
                    drawer.stroke = ColorRGBa.PINK.shade(0.1)
                    drawer.rectangle( 0.0, 20.0, 600.0, 85.0)
                    writer {
                        box = Rectangle(160.0, 23.0, 390.0, 85.0)
                        drawer.fill = ColorRGBa.WHITE

                        newLine()
                        text("WE HAVE A "+ table[settings.tableIndex].smoking)

                        newLine()
                        text("AT THE AGE OF "+ table[settings.tableIndex].age.toString()+ " YEARS OLD")
                        newLine()
                        text("WITH SYSTOLIC PRESSURE AT  "+ table[settings.tableIndex].bp.toString() + " mm Hg")
                        newLine()
                        text("AND CHOLESTEROL AT "+ table[settings.tableIndex].chole.toString()+ " mmol/L")
                    }
                    var value = table[settings.tableIndex].risk_factor
                    //var riskCounter = 0
                    var circleCounter = 0
                    val colors = mutableListOf<ColorRGBa>()
                    drawer.fill = ColorRGBa.PINK.shade(0.05)
                    drawer.stroke = ColorRGBa.PINK.shade(0.05)
                    drawer.rectangle( 190.0, 165.0, 220.0, 220.0)
                    writer {
                        box = Rectangle(190.0, 165.0, 220.0, 220.0)

                        for (i in 0 until value) {
                            colors.add(ColorRGBa.RED)
                        }
                        for (i in value until 100) {
                            colors.add(ColorRGBa.GREEN)
                        }
                        colors.shuffle(kotlin.random.Random(0))
                        for (j in 0 until 10) {
                            for (i in 0 until 10) {
                                drawer.fill = colors[circleCounter]

                                // -- rotate

                                drawer.circle(i * 23.0 + 195.0,j * 23.0 + 170.0,10.0)
                                circleCounter++
                            }
                        }

                    }
                    val fonts = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 25.0)
                    drawer.fontMap = fonts

                    drawer.fill = ColorRGBa.PINK.shade(0.1)
                    drawer.stroke = ColorRGBa.PINK
                    drawer.rectangle( 0.0, 450.0, 600.0, 40.0)
                    writer{

                        box = Rectangle(165.0, 453.0, 280.0, 40.0)
                        drawer.fill = ColorRGBa.WHITE
                        newLine()
                        text("CURRENT RISK IS AT "+ table[settings.tableIndex].risk_factor.toString() +" %")
                    }
                }
            }


        }
        onNextArticle.trigger(article)
        gui.add(settings)
        extend(gui)
        extend(Screenshots())
        extend {
            drawer.clear(rgb(Math.random(), Math.random(), Math.random()).shade(0.100))
            composite.draw(drawer)

        }

    }

}