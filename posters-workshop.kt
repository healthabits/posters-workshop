// LocalArchiveSkeleton
package skeletons
import archives.LoadedArticle
import archives.localArchive
import blockGradient

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
import org.openrndr.extra.shadestyles.linearGradient
import org.openrndr.extra.shadestyles.radialGradient
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import org.openrndr.text.writer
import tools.dynamicText

fun main() = application {
    class Entry(var smoking: String, var age: Int, var bp: Int, var chole: Int, var risk_factor: Int)
    val table = listOf(
        Entry("NON SMOKER",58,140,7,1),
        Entry("SMOKER",50,180,7,3),
        Entry("SMOKER",86,160,7,13),
        Entry("NON SMOKER",89,180,8,12),
        Entry("SMOKER",72,160,8,16),
        Entry("SMOKER",75,180,7,19),
        Entry("NON SMOKER",84,160,8,8),
        Entry("NON SMOKER",60,120,4,1),
        Entry("SMOKER",72,180,8,22),
        Entry("NON SMOKER",54,120,4,0)
    )
    configure {

        title = "health risk"
        width = 600
        height = 800

    }
    program {

        val archive = localArchive("archives/health-risk/health-risk").iterator()
        var article = archive.next()
        var table_counter= 0
        val highlight = loadFont("data/fonts/IBMPlexMono-BoldItalic.ttf", 24.0)

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
                    drawer.rectangle( 0.0, 20.0, width/6.0, 35.0)
                    drawer.rectangle( 0.0, 220.0, width/4.0, 35.0)
                    drawer.rectangle( 0.0, 420.0, width/2.0-80.0, 35.0)
                    drawer.rectangle( 0.0, 620.0, width/4.0+20.0, 35.0)
                    drawer.rectangle( 450.0, 20.0, width/4.0, 35.0)
                    drawer.rectangle( 470.0, 220.0, width/4.0, 35.0)
                    drawer.rectangle( 470.0, 420.0, width/4.0, 35.0)
                    drawer.rectangle( 470.0, 620.0, width/4.0  , 35.0)

                    var smoke = table[settings.tableIndex].smoking
                    var age = table[settings.tableIndex].age.toString()
                    var bp = table[settings.tableIndex].bp.toString()
                    var chole = table[settings.tableIndex].chole.toString()
                    val specialWords = setOf("NON","SMOKER", age, bp,chole, "mm", "Hg", "y", "mmol/L")
                    val otherWords = setOf(table[settings.tableIndex].risk_factor.toString())
                    val color = listOf(ColorRGBa.GREEN,ColorRGBa.YELLOW,ColorRGBa.BLUE)
                    writer {
                        box = Rectangle(10.0, 23.0, width / 2.0 + 90.0, 25.0)
                        //drawer.fill = ColorRGBa.WHITE
                        newLine()
                        dynamicText("YOU ARE A ") {
                            if (it in specialWords) {
                                drawer.fontMap = highlight
                                drawer.fill = color[1]

                            } else {
                                drawer.fontMap = font
                                drawer.fill = ColorRGBa.PINK

                            }
                        }
                    }
                    writer {
                        box = Rectangle(10.0, 230.0, width / 4.0, 25.0)
                        newLine()

                        dynamicText("AT THE AGE OF") {
                            if (it in specialWords) {
                                drawer.fontMap = highlight
                                drawer.fill = color[0]

                            } else  {
                                drawer.fontMap = font
                                drawer.fill = ColorRGBa.PINK

                            }
                        }
                    }
                    writer {
                        box = Rectangle(10.0, 430.0, width / 2.0-80.0, 25.0)
                        newLine()

                        dynamicText("WITH SYSTOLIC PRESSURE AT") {
                            if (it in specialWords) {
                                drawer.fontMap = highlight
                                drawer.fill = color[2]

                            } else  {
                                drawer.fontMap = font
                                drawer.fill = ColorRGBa.PINK

                            }
                        }
                    }
                    writer{
                        box = Rectangle(10.0, 630.0, width / 4.0 +20.0, 25.0)
                        newLine()

                        dynamicText("AND CHOLESTEROL AT") {
                            if (it in specialWords) {
                                drawer.fontMap = highlight
                                drawer.fill = color[0]

                            } else {
                                drawer.fontMap = font
                                drawer.fill = ColorRGBa.PINK

                            }
                        }

                    }
                    writer {
                        box = Rectangle(460.0, 23.0, width / 4.0, 25.0)
                        newLine()
                        dynamicText(smoke) {
                            if (it in specialWords) {
                                drawer.fontMap = highlight
                                drawer.fill = color[2]

                            } else if (it in otherWords) {
                                drawer.fontMap = font
                                drawer.fill = ColorRGBa.PINK

                            }
                        }
                    }
                    writer {
                        box = Rectangle(480.0, 230.0, width / 4.0, 25.0)
                        newLine()
                        dynamicText(age + " y") {
                            if (it in specialWords) {
                                drawer.fontMap = highlight
                                drawer.fill = color[0]

                            } else if (it in otherWords) {
                                drawer.fontMap = font
                                drawer.fill = ColorRGBa.PINK

                            }
                        }
                    }
                    writer {3
                        box = Rectangle(480.0, 430.0, width / 4.0, 25.0)
                        newLine()
                        dynamicText(bp + " mm Hg") {
                            if (it in specialWords) {
                                drawer.fontMap = highlight
                                drawer.fill = color[1]

                            } else if (it in otherWords) {
                                drawer.fontMap = font
                                drawer.fill = ColorRGBa.PINK

                            }
                        }
                    }
                    writer{
                        box = Rectangle(480.0, 630.0, width / 4.0, 25.0)
                        newLine()
                        dynamicText(chole + " mmol/L") {
                            if (it in specialWords) {
                                drawer.fontMap = highlight
                                drawer.fill = color[0]

                            } else if (it in otherWords) {
                                drawer.fontMap = font
                                drawer.fill = ColorRGBa.PINK

                            }
                        }
                    }
                    //var riskCounter = 0

                    drawer.fill = null
                    drawer.stroke = null
                    drawer.rectangle( 190.0, 165.0, width/2.0 - 80.0, height/2.0 - 30.0)
                    writer {
                        box = Rectangle(190.0, 165.0, width/2.0 - 80.0, height/2.0 - 30.0)
                        /*for (j in 0 until 10) {
                            for (i in 0 until 10) {

                                if(value > riskCounter ) {
                                    drawer.fill = ColorRGBa.RED
                                }
                                if(value < riskCounter ) {
                                    drawer.fill = ColorRGBa.GREEN
                                }
                                drawer.circle(i*23.0 + 300.0, j*23.0+150.0, 10.0)
                                riskCounter = riskCounter + 1

                                /*while(value > 0) {

                                        drawer.fill = ColorRGBa.RED
                                        drawer.circle(value * 23.0 + 300.0,value * 23.0 + 150.0,10.0)
                                        value =  value - 1

                                }*/
                            }*/

                        val gradient = radialGradient(ColorRGBa.PINK,ColorRGBa.WHITE)
                        gui.add(gradient)
                        var circleCounter = 0
                        val colors = mutableListOf<ColorRGBa>()
                        var value = table[settings.tableIndex].risk_factor
                        val otherWords = setOf(value.toString())
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
                                drawer.shadeStyle=gradient
                                // -- rotate

                                drawer.circle(i * 23.0 + 195.0,j * 63.0 + 40.0,(simplex(i*50 + j*63, seconds*0.5) + 1.0) * 20.0 )
                                circleCounter++
                            }
                        }

                    }
                    val fonts = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 25.0)
                    drawer.fontMap = fonts
                    drawer.shadeStyle = null
                    drawer.fill = ColorRGBa.PINK.shade(0.1)
                    drawer.stroke = ColorRGBa.PINK
                    drawer.rectangle( 0.0, 680.0, width*1.0, 50.0)
                    writer{

                        box = Rectangle(165.0, 690.0, width/2.0 - 10.0, 40.0)
                        drawer.fill = ColorRGBa.PINK
                        newLine()
                        text("YOUR CURRENT RISK IS ")
                        drawer.fill = ColorRGBa.RED
                        text(table[settings.tableIndex].risk_factor.toString() +" %")
                    }
                }
            }


        }
        onNextArticle.trigger(article)
        gui.add(settings)
        extend(gui)

        extend(Screenshots())     
		extend {
            //drawer.clear(rgb(Math.random(), Math.random(), Math.random()).shade(0.100))
            composite.draw(drawer)

        }

    }

}
