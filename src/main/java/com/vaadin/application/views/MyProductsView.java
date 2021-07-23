package com.vaadin.application.views;

import com.vaadin.application.model.Product;
import com.vaadin.application.model.SortDataValue;
import com.vaadin.application.service.ProductService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;
import java.util.List;

@PageTitle("My Products")
@Route(value = "list", layout = MainLayout.class)
public class MyProductsView extends VerticalLayout {

    private final ProductDetail productDetailForm;
    private final CreateProductForm createProductForm;
    private final ProductService productService;
    Grid<Product> grid = new Grid<>(Product.class);
    TextField filterText = new TextField();

    /**
     * Constructor for MyProductsView
     */
    public MyProductsView(ProductService productService) {
        this.productService = productService;
        addClassName("my-products-view");
        setSizeFull();
        configureGrid();

        productDetailForm = new ProductDetail(productService.findAllProduct());
        productDetailForm.addListener(ProductDetail.SaveEvent.class, this::saveProduct);
        productDetailForm.addListener(ProductDetail.DeleteEvent.class, this::deleteProduct);
        productDetailForm.addListener(ProductDetail.CloseEvent.class, e -> closeEditor());

        createProductForm = new CreateProductForm(productService.findAllProduct(), productService);
        createProductForm.addListener(CreateProductForm.SaveEvent.class, this::saveNewProduct);

        Div content = new Div(grid, productDetailForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getConfigFilter(), content);
        updateList();
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
        grid.removeColumnByKey("productId");
        grid.setColumns("productName", "productCode", "releaseDate",
                "price", "starRating", "imageUrl");
        grid.setSizeFull();
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
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
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> filterList());
        Button searchBtn = new Button("Search", buttonClickEvent -> filterList());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, searchBtn);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    /**
     * Add new Product in Product List
     *
     * @author tailam
     */
    private void addProduct() {
        grid.asSingleSelect().clear();
        editProduct(new Product());
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
    private void deleteProduct(ProductDetail.DeleteEvent evt) {
        productService.deleteProduct(evt.getProduct());
        updateList();
        closeEditor();
    }

    /**
     * Saving Product Detail Form
     *
     * @author tailam
     */
    private void saveProduct(ProductDetail.SaveEvent evt) {
        productService.save(evt.getProduct());
        updateList();
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
    }

    /**
     * Save New Product
     *
     * @author tailam
     */
    private void saveNewProduct(CreateProductForm.SaveEvent evt) {
        productService.save(evt.getProduct());
        updateList();
    }

    /**
     * Update List Product
     *
     * @author tailam
     */
    public void updateList() {
        List<Product> sortList = productService.findAllProduct();
        Collections.sort(sortList, new SortDataValue());
        grid.setItems(sortList);
    }

    /**
     * Searching Product in Product List
     *
     * @author tailam
     */
    public void filterList() {
        grid.setItems(productService.findFilterProduct(filterText.getValue()));
    }
}
