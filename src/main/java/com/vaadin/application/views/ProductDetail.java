package com.vaadin.application.views;

import com.vaadin.application.model.Product;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class ProductDetail extends FormLayout {

    TextField productName = new TextField("Product Name");
    TextField productCode = new TextField("Product Code");
    TextField description = new TextField("Product Description");
    TextField releaseDate = new TextField("Product Release Date");
    TextField price = new TextField("Product Price");
    TextField starRating = new TextField("Product Rating");
    TextField imageUrl = new TextField("Product Image URL");

    Button saveBtn = new Button("Save");
    Button deleteBtn = new Button("Delete");
    Button cancelBtn = new Button("Cancel");

    Binder<Product> binderProduct = new BeanValidationBinder<>(Product.class);
    private Product product;

    public ProductDetail(List<Product> products) {
        addClassName("contact-form");
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
        btnLayout.add(getDeleteBtn());
        btnLayout.add(getCancelBtn());
        verticalLayout1.add(btnLayout);

        // Layout for parent layout to contain text fields by horizontal
        HorizontalLayout horizontalLayoutParent = new HorizontalLayout();
        horizontalLayoutParent.add(verticalLayout1);
        horizontalLayoutParent.add(verticalLayout2);

        // add layout to class and set alignment
        add(horizontalLayoutParent);
    }

    /**
     * Reading product detail or create product
     *
     * @param product
     */
    public void setProduct(Product product) {
        this.product = product;
        if (product != null && product.getProductId() != null) {
            binderProduct.readBean(product);
        } else {
            binderProduct.readBean(new Product(null, "",
                    "", "", "", 0F, 0F, ""));
        }
    }

    /**
     * Button Save Product
     *
     * @return saveBtn
     */
    public Button getSaveBtn() {
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addClickShortcut(Key.ENTER);
        saveBtn.addClickListener(click -> validateAndSave());
        binderProduct.addStatusChangeListener(evt -> saveBtn.setEnabled(binderProduct.isValid()));
        return saveBtn;
    }

    /**
     * Validate product and save
     *
     * @author tailam
     */
    private void validateAndSave() {
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
     * Button Delete
     *
     * @return deleteBtn
     */
    public Button getDeleteBtn() {
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteBtn.addClickShortcut(Key.DELETE);
        deleteBtn.addClickListener(click -> fireEvent(new DeleteEvent(this, product)));
        return deleteBtn;
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
    public static abstract class ProductDetailForm extends ComponentEvent<ProductDetail> {
        private final Product product;

        protected ProductDetailForm(ProductDetail source, Product product) {
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
    public static class SaveEvent extends ProductDetailForm {
        SaveEvent(ProductDetail source, Product product) {
            super(source, product);
        }
    }

    /**
     * Delete Event
     *
     * @author tailam
     */
    public static class DeleteEvent extends ProductDetailForm {
        DeleteEvent(ProductDetail source, Product product) {
            super(source, product);
        }

    }

    /**
     * Close Event
     *
     * @author tailam
     */
    public static class CloseEvent extends ProductDetailForm {
        CloseEvent(ProductDetail source) {
            super(source, null);
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
