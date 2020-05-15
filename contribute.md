# gradient filters for the circles

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
