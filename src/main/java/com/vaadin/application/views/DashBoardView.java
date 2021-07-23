package com.vaadin.application.views;

import com.vaadin.application.service.ProductService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Map;

@PageTitle("DashBoard")
@Route(value = "dashboard", layout = MainLayout.class)
public class DashBoardView extends VerticalLayout {

    private final ProductService productService;

    public DashBoardView(ProductService productService) {
        this.productService = productService;

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(
                getProductStats(),
                getProductsChart()
        );
    }

    private Span getProductStats() {
        Span stats = new Span(productService.count() + " products");
        stats.addClassName("contact-stats");

        return stats;
    }

    private Component getProductsChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        Map<String, Integer> stats = productService.getStats();
        stats.forEach((name, number) ->
                dataSeries.add(new DataSeriesItem(name, number)));

        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }
}
