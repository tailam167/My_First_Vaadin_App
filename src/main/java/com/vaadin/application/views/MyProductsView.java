package com.vaadin.application.views;

import com.vaadin.application.cache.ProductCacheService;
import com.vaadin.application.cache.SortDataValue;
import com.vaadin.application.model.Product;
import com.vaadin.application.service.ProductService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Product List View
 *
 * @author tailam
 */
@PageTitle("My Products")
@Route(value = "list", layout = MainLayout.class)
public class MyProductsView extends VerticalLayout {

    private final ProductCacheService productCacheService;
    private final ProductDetailForm productDetailForm;
    private final ProductService productService;
    Grid<Product> grid = new Grid<>();
    TextField filterText = new TextField();
    List<Product> sortList;
    ListDataProvider<Product> listDataProvider;

    /**
     * Constructor for MyProductsView class
     *
     * @author tailam
     */
    public MyProductsView(ProductCacheService productCacheService, ProductService productService) {
        this.productCacheService = productCacheService;
        this.productService = productService;
        sortList = productCacheService.findAllProductCache();
        sortList.sort(new SortDataValue());
        listDataProvider = DataProvider.ofCollection(sortList);

        addClassName("my-products-view");
        setSizeFull();
        configureGrid();

        productDetailForm = new ProductDetailForm(productService.findAllProduct());
        productDetailForm.addListener(ProductDetailForm.SaveEvent.class, this::updateProduct);
        productDetailForm.addListener(ProductDetailForm.DeleteEvent.class, this::deleteProduct);
        productDetailForm.addListener(ProductDetailForm.CloseEvent.class, e -> closeEditor());

        CreateProductForm createProductForm = new CreateProductForm(productService);
        createProductForm.addListener(CreateProductForm.SaveEvent.class, this::saveNewProduct);

        Div content = new Div(grid, productDetailForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getConfigFilter(), content);
        closeEditor();
    }

    /**
     * Get grid to contain product list
     *
     * @author tailam
     */
    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.setItems(sortList);
        grid.addColumn(Product::getProductName, "productName").setHeader("Product Name");
        grid.addColumn(
                productCode -> productCode.getProductCode().toUpperCase()
                , "productCode").setHeader("Product Code");
        grid.addColumn(
                releaseDate -> {
                    LocalDate localDate = LocalDate.ofInstant(releaseDate.getReleaseDate().toInstant(),
                            ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    return localDate.format(formatter);
                }, "releaseDate").setHeader("Release Date");
        grid.addColumn(new NumberRenderer<>(Product::getPrice,
                "$%(,.2f",
                Locale.US, "$0.00"), "price").setHeader("Price");
        grid.addColumn(new NumberRenderer<>(Product::getStarRating,
                new DecimalFormat("#.##")), "starRating").setHeader("Rating");
        grid.addColumn(Product::getImageUrl, "imageUrl").setHeader("Image");
        grid.setColumnReorderingAllowed(true);
        grid.setDataProvider(listDataProvider);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
//        grid.addItemDoubleClickListener(event -> editProduct(event.getItem()));
        grid.asSingleSelect().addValueChangeListener(evt -> editProduct(evt.getValue()));
    }

    /**
     * Get filter to search in product list
     *
     * @return toolbar
     */
    private HorizontalLayout getConfigFilter() {
        filterText.setPlaceholder("Looking for...");
        filterText.setClearButtonVisible(true);
        Icon icon = VaadinIcon.SEARCH.create();
        filterText.setPrefixComponent(icon);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> filterList());

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    /**
     * Edit product in Product Detail Form
     *
     * @author tailam
     */
    public void editProduct(Product product) {
        if (product == null) {
            closeEditor();
        } else {
            productDetailForm.setProduct(product);
            productDetailForm.setVisible(true);
            addClassName("editing");
        }
    }

    /**
     * Delete Product in Product Detail Form
     *
     * @author tailam
     */
    private void deleteProduct(ProductDetailForm.DeleteEvent evt) {
        productService.deleteProduct(evt.getProduct());
        productCacheService.deleteProductCache(evt.getProduct());
        listDataProvider.getItems().remove(evt.getProduct());
        closeEditor();
    }

    /**
     * Saving Product Detail Form
     *
     * @author tailam
     */
    private void updateProduct(ProductDetailForm.SaveEvent evt) {
        productService.updateProduct(evt.getProduct());
        productCacheService.updateProductCache(evt.getProduct());
        listDataProvider.refreshItem(evt.getProduct());
        closeEditor();
    }

    /**
     * Close Product Detail Form
     *
     * @author tailam
     */
    public void closeEditor() {
        productDetailForm.setProduct(null);
        productDetailForm.setVisible(false);
        removeClassName("editing");
        grid.getDataProvider().refreshAll();
    }

    /**
     * Save New Product
     *
     * @author tailam
     */
    private void saveNewProduct(CreateProductForm.SaveEvent evt) {
        productService.save(evt.getProduct());
        listDataProvider.refreshAll();
    }

    /**
     * Searching Product in Product List
     *
     * @author tailam
     */
    public void filterList() {
        if (filterText.getValue() != null) {
            listDataProvider.setFilter(product ->
                    product.getProductName().toLowerCase().contains(filterText.getValue().toLowerCase()) ||
                            product.getProductCode().toLowerCase().contains(filterText.getValue().toLowerCase()) ||
                            product.getDescription().toLowerCase().contains(filterText.getValue().toLowerCase()) ||
                            product.getReleaseDate().toString().contains(filterText.getValue()) ||
                            product.getPrice().toString().contains(filterText.getValue()) ||
                            product.getStarRating().toString().contains(filterText.getValue()) ||
                            product.getImageUrl().toLowerCase().contains(filterText.getValue().toLowerCase()));
        } else {
            listDataProvider.clearFilters();
        }
    }
}
