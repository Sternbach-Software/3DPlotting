import net.ericaro.surfaceplotter.DefaultSurfaceModel
import net.ericaro.surfaceplotter.JSurfacePanel
import net.ericaro.surfaceplotter.Mapper
import net.ericaro.surfaceplotter.surface.SurfaceModel
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotColor
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType
import java.awt.*
import javax.swing.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    fun createDefaultSurfaceModel(): SurfaceModel {
        val sm = DefaultSurfaceModel()
        sm.isPlotFunction2 = true
        sm.isFirstFunctionOnly = true

        sm.calcDivisions = 50
        sm.dispDivisions = 50
        sm.contourLines = 10
        sm.xMin = -500F
        sm.xMax = 500F
        sm.yMin = -500F
        sm.yMax = 500F
        sm.isBoxed = false
        sm.isDisplayXY = true
        sm.isExpectDelay = false
        sm.isAutoScaleZ = true
        sm.isDisplayZ = false
        sm.isMesh = true
        sm.plotType = PlotType.SURFACE
        sm.plotColor = PlotColor.SPECTRUM
        sm.mapper = object : Mapper {
            override fun f1(x: Float, y: Float): Float {
                return x.getNextStep(y, 100.toDouble()) // to do csv or plot specific coordinates, just load them into a map, and query the map based on the x-y pair
            }

            override fun f2(x: Float, y: Float): Float {
                return x.getNextStep(100F, y.toDouble())//(x,y).toFloat()
            }

            fun Float.getNextStep(
                delta: Float,
                step: Double
            ): Float {
                return try {
                    if(step == 0.0) return this
                    var initialRate =
                        (this * 100.0).roundToInt() / 100.0 //not exactly sure what the goal is. To remove the decimal?
                    initialRate = if (delta > 0)
                        floor((initialRate + 0.005) / step.coerceAtLeast(1.toDouble())) * step //not sure the significance of the number 0.005, happens to be tenth of speed step, but not sure if that is an actual relationship, or 0.005 is just half of 0.01. Not really sure how this math works, just copied from VLC
                    else
                        ceil((initialRate - 0.005) / step.coerceAtLeast(1.toDouble())) * step
                    ((initialRate + delta) * 100F).roundToInt() / 100F
                } catch (t:Throwable) {
                    println("Failed on $this.getNextStep(\n" +
                            "                delta=$delta,\n" +
                            "                step=$step\n" +
                            "            )")
                    t.printStackTrace()
                    0F
                }
            }
        }
        sm.plot().execute()
        return sm
    }
    EventQueue.invokeLater {
        JFrame().apply {
            add(JSurfacePanel(createDefaultSurfaceModel(), "value", "delta"))
            extendedState = JFrame.MAXIMIZED_BOTH
            isVisible = true
        }
    }
    //
    // Source code recreated from a .class file by IntelliJ IDEA
    // (powered by FernFlower decompiler)
    //

}