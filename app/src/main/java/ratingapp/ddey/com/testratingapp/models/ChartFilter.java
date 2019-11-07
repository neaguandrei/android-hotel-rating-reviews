package ratingapp.ddey.com.testratingapp.models;

public class ChartFilter {
    private String chartType;
    private String chartName;

    public ChartFilter() {
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public ChartFilter(String chartType, String chartName) {

        this.chartType = chartType;
        this.chartName = chartName;
    }
}
