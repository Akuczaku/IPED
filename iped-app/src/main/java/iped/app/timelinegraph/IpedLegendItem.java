package iped.app.timelinegraph;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.util.Map;

import org.jfree.chart.LegendItem;
import org.jfree.chart.title.LegendTitle;

public class IpedLegendItem extends LegendItem {
    IpedCombinedDomainXYPlot plot;
    private static String polishDisplayLegendItem(String key) {
        if (key == null) return null;
        switch (key) {
            case "created": return "Utworzono";
            case "modified": return "Zmodyfikowano";
            case "accessed": return "Ostatni dost\u0119p";
            case "communication:date": return "Data komunikacji";
            default: return key;
        }
    }

    private static String plDisplayLabel(String key) {
        if (key == null) return null;
        switch (key) {
            case "created": return iped.app.ui.Messages.getString("TimeLineGraph.createdLabel");
            case "modified": return iped.app.ui.Messages.getString("TimeLineGraph.modifiedLabel");
            case "accessed": return iped.app.ui.Messages.getString("TimeLineGraph.accessedLabel");
            case "communication:date": return iped.app.ui.Messages.getString("TimeLineGraph.communicationLabel");
            default: return key;
        }
    }


    public IpedLegendItem(String seriesKey, Paint paint, IpedCombinedDomainXYPlot plot) {
        super(polishDisplayLegendItem(seriesKey), paint);
        this.plot = plot;
    }

    @Override
    public Font getLabelFont() {
        if (plot.getIpedChartsPanel().getChartPanel().getExcludedEvents().contains(this.getSeriesKey())) {
            Font f = LegendTitle.DEFAULT_ITEM_FONT;
            Map attributes = f.getAttributes();
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            return f.deriveFont(attributes);
        }
        return LegendTitle.DEFAULT_ITEM_FONT;
    }

    @Override
    public Shape getShape() {
        return super.getShape();
    }

}
