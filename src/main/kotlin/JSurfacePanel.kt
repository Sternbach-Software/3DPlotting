import net.ericaro.surfaceplotter.DefaultSurfaceModel
import net.ericaro.surfaceplotter.Mapper
import net.ericaro.surfaceplotter.beans.JGridBagScrollPane
import net.ericaro.surfaceplotter.surface.AbstractSurfaceModel
import net.ericaro.surfaceplotter.surface.JSurface
import net.ericaro.surfaceplotter.surface.SurfaceModel
import net.ericaro.surfaceplotter.surface.VerticalConfigurationPanel
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*

class JSurfacePanel constructor(
    model: SurfaceModel? = createDefaultSurfaceModel(),
    val xLabel: String,
    val yLabel: String
) :
    JPanel(BorderLayout()) {
    private var title: JLabel? = null
    var surface: JSurface? = null
        private set
    private var scrollpane: JGridBagScrollPane? = null
    private var configurationPanel: VerticalConfigurationPanel? = null
    private var configurationToggler: AbstractAction? = null

    init {
        initComponents()
        val name = configurationToggler!!.getValue("Name") as String
        actionMap.put(name, configurationToggler)
        this.getInputMap(1).put(KeyStroke.getKeyStroke(113, 0), name)
        setModel(model)
    }

    fun setModel(model: SurfaceModel?) {
        if (model is AbstractSurfaceModel) {
            configurationPanel!!.setModel(model as AbstractSurfaceModel?)
        } else {
            scrollpane!!.isVisible = false
            configurationPanel!!.setModel(null as AbstractSurfaceModel?)
        }
        surface!!.model = model
    }

    var titleFont: Font?
        get() = title!!.font
        set(font) {
            title!!.font = font
        }
    var titleIcon: Icon?
        get() = title!!.icon
        set(icon) {
            title!!.icon = icon
        }
    var titleText: String?
        get() = title!!.text
        set(text) {
            title!!.text = text
        }
    var isTitleVisible: Boolean
        get() = title!!.isVisible
        set(aFlag) {
            title!!.isVisible = aFlag
        }
    var isConfigurationVisible: Boolean
        get() = scrollpane!!.isVisible
        set(aFlag) {
            scrollpane!!.isVisible = aFlag
            invalidate()
            revalidate()
        }

    private fun toggleConfiguration() {
        isConfigurationVisible = !isConfigurationVisible
        if (!isConfigurationVisible) {
            surface!!.requestFocusInWindow()
        }
    }

    private fun mousePressed() {
        surface!!.requestFocusInWindow()
    }

    private fun surfaceMouseClicked(e: MouseEvent) {
        if (e.clickCount >= 2) {
            toggleConfiguration()
        }
    }

    private fun initComponents() {
        val bundle = ResourceBundle.getBundle("net.ericaro.surfaceplotter.JSurfacePanel")
        title = JLabel()
        surface = JSurface().also {
            it.xLabel = xLabel
            it.yLabel = yLabel
        }
        scrollpane = JGridBagScrollPane()
        configurationPanel = VerticalConfigurationPanel()
        configurationToggler = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                toggleConfiguration()
            }
        }
        name = "this"
        this.layout = GridBagLayout()
        (this.layout as GridBagLayout).columnWidths = intArrayOf(0, 0, 0)
        (this.layout as GridBagLayout).rowHeights = intArrayOf(0, 0, 0)
        (this.layout as GridBagLayout).columnWeights = doubleArrayOf(1.0, 0.0, 1.0E-4)
        (this.layout as GridBagLayout).rowWeights = doubleArrayOf(0.0, 1.0, 1.0E-4)
        title!!.text = bundle.getString("title.text")
        title!!.horizontalTextPosition = 0
        title!!.horizontalAlignment = 0
        title!!.background = Color.white
        title!!.isOpaque = true
        title!!.font = title!!.font.deriveFont(title!!.font.size.toFloat() + 4.0f)
        title!!.name = "title"
        this.add(title, GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 1, Insets(0, 0, 0, 0), 0, 0))
        surface!!.toolTipText = bundle.getString("surface.toolTipText")
        surface!!.inheritsPopupMenu = true
        surface!!.name = "surface"
        surface!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                surfaceMouseClicked(e)
            }

            override fun mousePressed(e: MouseEvent) {
                this@JSurfacePanel.mousePressed()
            }
        })
        this.add(surface, GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 10, 1, Insets(0, 0, 0, 0), 0, 0))
        scrollpane!!.isWidthFixed = true
        scrollpane!!.name = "scrollpane"
        configurationPanel!!.nextFocusableComponent = this
        configurationPanel!!.name = "configurationPanel"
        scrollpane!!.setViewportView(configurationPanel)
        this.add(scrollpane, GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, 10, 1, Insets(0, 0, 0, 0), 0, 0))
        (configurationToggler as AbstractAction).putValue("Name", bundle.getString("configurationToggler.Name"))
    }

    companion object {
        private fun createDefaultSurfaceModel(): SurfaceModel {
            val sm = DefaultSurfaceModel()
            sm.isPlotFunction2 = false
            sm.calcDivisions = 50
            sm.dispDivisions = 50
            sm.contourLines = 10
            sm.xMin = -3.0f
            sm.xMax = 3.0f
            sm.yMin = -3.0f
            sm.yMax = 3.0f
            sm.isBoxed = false
            sm.isDisplayXY = false
            sm.isExpectDelay = false
            sm.isAutoScaleZ = true
            sm.isDisplayZ = false
            sm.isMesh = false
            sm.plotType = SurfaceModel.PlotType.SURFACE
            sm.isFirstFunctionOnly = true
            sm.plotColor = SurfaceModel.PlotColor.SPECTRUM
            sm.mapper = object : Mapper {
                override fun f1(x: Float, y: Float): Float {
                    val r = x * x + y * y
                    return if (r == 0.0f) 1.0f else (Math.sin(r.toDouble()) / r.toDouble()).toFloat()
                }

                override fun f2(x: Float, y: Float): Float {
                    return Math.sin((x * y).toDouble()).toFloat()
                }
            }
            sm.plot().execute()
            return sm
        }
    }
}