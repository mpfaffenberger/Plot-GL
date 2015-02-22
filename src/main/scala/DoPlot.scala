package pfaff.plot

import org.apache.commons.math3.random.EmpiricalDistribution
import org.apache.commons.math3.stat.StatUtils
import org.jcolorbrewer.ColorBrewer
import scala.collection.JavaConversions._

case class Bin(min: Double, max: Double) {
  def contains(num: Double) = {
    (num >= min && num < max)
  }
}
case class BinCollection(bins: Seq[Bin]) {
  def getBin(num: Double) = {
    bins.indexOf(bins.filter(_.contains(num)).head)
  }
}

object DoPlot {

  def normalize(data: Array[Array[Double]]) ={
    val normalizedX = StatUtils.normalize(data.map(_(0)).toArray)
    val normalizedY = StatUtils.normalize(data.map(_(1)).toArray)
    val normalizedZ = StatUtils.normalize(data.map(_(2)).toArray)
    val normalData = normalizedX.zip(normalizedY).zip(normalizedZ).map(i => {
      val result = Array(i._1._1, i._1._2, i._2)
      result
    })
    normalData
  }

  def main(args: Array[String]) = {
    val data = scala.io.Source.fromFile(args(0)).getLines.map(_.split(",")).toList.drop(1)
    val colorBy = args(1).toInt
    val numeric = args(2).toBoolean
    val palette = if (numeric) {
      ColorBrewer.getSequentialColorPalettes(false)(17).getColorPalette(20)
    } else {
      ColorBrewer.getQualitativeColorPalettes(false)(5).getColorPalette(data.map(_(3)).distinct.size)
    }

    val coloredData = if (numeric) {

      val colorField = data.map(_(colorBy).toDouble)
      val max = colorField.max
      val min = colorField.min
      val step = Math.ceil((max - min) / 20.0)
      var currentMin = min

      val normalData = normalize(data.toArray.map(_.map(_.toDouble)))
        .zip(StatUtils.normalize(data.map(_(3).toDouble).toArray))
        .map(i => Array(i._1(0), i._1(1), i._1(2), i._2))


      val ed = new EmpiricalDistribution(20)
      ed.load(normalData.map(_(3)).toArray)

      val mins = ed.getBinStats().map(bin => {
        println(bin.getMin + ", " + bin.getMax)
        bin.getMin()
      })

      val bins = BinCollection((0 until mins.size - 1).map(i => {
        Bin(mins(i), mins(i + 1))
      }) ++ Seq(Bin(mins(mins.size - 1), Double.MaxValue)))

      bins.bins.foreach(println(_))

      val coloredData = normalData.map(i => {
        val colorAttribute = i(colorBy)
        val color = palette.reverse(bins.getBin(colorAttribute))
        Array(i(0).toFloat * 100f,
          i(1).toFloat * 100f,
          i(2).toFloat * 100f,
          color.getRed() / 256.0f,
          color.getGreen() / 256.0f,
          color.getBlue() / 256.0f)
      }).toArray
      coloredData
    } else {

      val normalData = normalize(data.toArray.map(i => Array(i(0).toDouble, i(1).toDouble, i(2).toDouble)))
      val colors = data.map(_(colorBy)).distinct.zipWithIndex.toMap
      val coloredData = normalData.zip(data.map(_(colorBy))).map(i => {
        val color = palette(colors(i._2))
        Array(i._1(0).toFloat * 100f,
          i._1(1).toFloat * 100f,
          i._1(2).toFloat * 100f,
          color.getRed() / 256.0f,
          color.getGreen() / 256.0f,
          color.getBlue() / 256.0f)
      }).toArray
      coloredData
    }
    PlotGL.createPlot(coloredData)
  }
}