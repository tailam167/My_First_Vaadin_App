package com.vaadin.application.views;

import com.vaadin.application.model.Product;
import com.vaadin.application.service.ProductService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.List;

@PageTitle("New Product")
@Route(value = "create", layout = MainLayout.class)
public class CreateProductForm extends FormLayout {

    ProductService productService;
    TextField productName = new TextField("Product Name");
    TextField productCode = new TextField("Product Code");
    TextField description = new TextField("Product Description");
    TextField releaseDate = new TextField("Product Release Date");
    TextField price = new TextField("Product Price");
    TextField starRating = new TextField("Product Rating");
    TextField imageUrl = new TextField("Product Image URL");

    Button saveNewBtn = new Button("Save");
    Button cancelBtn = new Button("Cancel");

    Binder<Product> binderProduct = new BeanValidationBinder<>(Product.class);
    private Product product;
    boolean isValid = false;

    public CreateProductForm(List<Product> products, ProductService productService) {
        this.productService = productService;
        addClassName("create-product-view");
        binderProduct.forField(starRating).withConverter(new StringToFloatConverter("Must enter float number"))
                .bind(Product::getStarRating, Product::setStarRating);
        binderProduct.forField(price).withConverter(new StringToFloatConverter("Must enter float number"))
                .bind(Product::getPrice, Product::setPrice);
        binderProduct.bindInstanceFields(this);

        // Layout for text field productName --> productReleaseDate by vertical
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.add(productName);
        verticalLayout1.add(productCode);
        verticalLayout1.add(description);
        verticalLayout1.add(releaseDate);

        // Layout for text field productPrice --> imageUrl by vertical
        VerticalLayout verticalLayout2 = new VerticalLayout();
        verticalLayout2.add(price);
        verticalLayout2.add(starRating);
        verticalLayout2.add(imageUrl);

        // Layout for button Save and Cancel by horizontal
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.add(getSaveBtn());
        btnLayout.add(getCancelBtn());
        verticalLayout1.add(btnLayout);

        // Layout for parent layout to contain text fields by horizontal
        HorizontalLayout horizontalLayoutParent = new HorizontalLayout();
        horizontalLayoutParent.add(verticalLayout1);
        horizontalLayoutParent.add(verticalLayout2);

        // add layout to class and set alignment
        add(horizontalLayoutParent);

        this.addListener(SaveEvent.class, this::saveNewProduct);
    }

    /**
     * Set New Product
     *
     * @param product
     */
    public void setProduct(Product product) {
        this.product = product;
        binderProduct.readBean(product);
    }

    /**
     * Button Save Product
     *
     * @return saveBtn
     */
    public Button getSaveBtn() {
        saveNewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveNewBtn.addClickShortcut(Key.ENTER);
        saveNewBtn.addClickListener(click -> validateAndSave());
        binderProduct.addStatusChangeListener(evt -> saveNewBtn.setEnabled(binderProduct.getBean() == null));
        return saveNewBtn;
    }

    /**
     * Validate product and save
     *
     * @author tailam
     */
    private void validateAndSave() {
        addNewProduct(new Product());
        if (binderProduct.isValid()) {
            try {
                binderProduct.writeBean(product);
                fireEvent(new SaveEvent(this, product));
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add New Product
     *
     * @author tailam
     */
    private void addNewProduct(Product product) {
        if (product == null) {
            Notification.show("Your new product is invalid !");
        } else {
            product.setProductId(null);
            product.setProductName(productName.getValue());
            product.setProductCode(productCode.getValue());
            product.setDescription(description.getValue());
            product.setReleaseDate(releaseDate.getValue());
            product.setPrice(Float.valueOf(price.getValue()));
            product.setStarRating(Float.valueOf(starRating.getValue()));
            product.setImageUrl(imageUrl.getValue());
            this.setProduct(product);
            this.setVisible(true);
            addClassName("create");
        }
    }

    /**
     * Save New Product
     *
     * @author tailam
     */
    private void saveNewProduct(SaveEvent evt) {
        try {
            productService.save(evt.getProduct());
            isValid = true;
        } catch (Exception exception){
            exception.printStackTrace();
            isValid = false;
        }
        if (isValid) {
            productName.clear();
            productCode.clear();
            description.clear();
            releaseDate.clear();
            price.clear();
            starRating.clear();
            imageUrl.clear();
            Notification.show("Your new product is created !");
        } else {
            Notification.show("Your new product is invalid !");
        }
    }

    /**
     * Button Cancel
     *
     * @return cancelBtn
     */
    public Button getCancelBtn() {
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        cancelBtn.addClickShortcut(Key.ESCAPE);
        cancelBtn.addClickListener(e -> fireEvent(new CloseEvent(this)));
        return cancelBtn;
    }

    /**
     * Event Component
     *
     * @author tailam
     */
    public static abstract class CreateProductFormEvent extends ComponentEvent<CreateProductForm> {
        private final Product product;

        protected CreateProductFormEvent(CreateProductForm source, Product product) {
            super(source, false);
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }
    }

    /**
     * Save Event
     *
     * @author tailam
     */
    public static class SaveEvent extends CreateProductFormEvent {
        SaveEvent(CreateProductForm source, Product product) {
            super(source, product);
        }
    }

    /**
     * Close Event
     *
     * @author tailam
     */
    public class CloseEvent extends CreateProductFormEvent {
        CloseEvent(CreateProductForm source) {
            super(source, null);
            getUI().ifPresent(ui -> ui.navigate("list"));
        }
    }

    /**
     * Register Event
     *
     * @return event
     */
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
