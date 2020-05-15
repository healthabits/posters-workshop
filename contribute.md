# gradient filters for the circles

```
layer {
     val gradient = radialGradient(ColorRGBa.PINK,ColorRGBa.WHITE)
     gui.add(gradient)
     draw {
         drawer.fill = ColorRGBa.RED
         drawer.shadeStyle=gradient
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
```
# text highlighting based on image analysis

```
program {
        val gui = GUI()
        var ti = 3
        // -- per country
        //val archive = googleNewsSequence(GoogleNewsEndPoint.TopHeadlines, country = "it").iterator()
        val archive = duckDuckGoSequence("nature beauty of " + table[ti].country).iterator()

        // -- per query
        //val archive = googleNewsSequence(GoogleNewsEndPoint.Everything, query= "health", language = "hu").iterator()
        var article = archive.next().load()

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

        //val image = loadImage("screenshots/poster.png")
        val settings2 = object {
            @IntParameter("entry", 0, 8)
            var ti = 0

        }.addTo(gui,"Change Input")
        val gradient = radialGradient(ColorRGBa.PINK,ColorRGBa.WHITE)
        gui.add(gradient)
        onNextArticle.trigger(article)


        gui.add(settings)
        extend(gui)
        extend(Screenshots())
        extend {

            if (article.images.isNotEmpty()) {
                val statistics = article.images[0].statistics()

                val font = loadFont("data/fonts/IBMPlexMono-Bold.ttf", 30.0)
                val bigFont = loadFont("data/fonts/IBMPlexMono-Medium.ttf", 70.0)
                // -- filter nice colors, with enough saturation and brightness
                val niceColors = statistics.histogram.colors().filter {
                    val hsv = it.first.toHSVa()
                    hsv.s > 0.2 && hsv.v > 0.7
                }
                drawer.imageFit(article.images[0], width / 2.0 + 20.0, 20.0, width / 2.0 - 40.0, height - 40.0)
                // -- pick the most dominant and 'nice' color
                drawer.fill = niceColors.first().first ?: ColorRGBa.YELLOW
                drawer.fontMap = font
                if (article.texts.isNotEmpty()) {

                    writer {
                        box = Rectangle(20.0, 20.0, width / 2 - 40.0, height - 40.0)
                        var index = 0
                        drawer!!.fontMap = font
                        newLine()
                        dynamicText("The probability of premature death in") {
                            drawer!!.fill = niceColors[index % niceColors.size].first
                            index++
                        }
                        newLine()
                        dynamicText(""+ table[settings2.ti].country + " is ") {
                            drawer!!.fill = niceColors[index % niceColors.size].first
                            index++
                        }
                        newLine()
                    }



                        drawer.fill = ColorRGBa.RED
                        drawer.shadeStyle=gradient
                        var p = table[settings2.ti].risk_factor
                        var a1 = table[settings2.ti].a0
                        var b1 = table[settings2.ti].b0 // round(p/a1)
                        var c1 = table[settings2.ti].c0 // p - (a1*b1)
                        var ratio=(580.0-p)/(b1+1)

                        for (j in 0 until a1) {
                            for (i in 0 until b1) {
                                drawer.circle((i * ratio)+ratio, (j * ratio)+200.0, (simplex(i*10 + j*24, seconds*0.1) + 1.0) * p*1.0 )
                            }
                        }
                        for (k in 0 until c1) {
                            drawer.circle((b1 * ratio)+ratio, (k * ratio)+200.0, (simplex(b1*10 + k*24, seconds*0.1) + 1.0) * p*1.0 )
                        }

                    writer {
                        box = Rectangle(420.0, 500.0, width / 2 - 40.0, height - 40.0)
                        var index = 0
                        drawer!!.fontMap = bigFont
                        drawer.shadeStyle=null
                        dynamicText(table[settings2.ti].risk_factor.toString() +" %") {
                            drawer!!.fill = niceColors[index % niceColors.size].first
                            index++
                        }
                    }
                }

            }
        }
    }
```
