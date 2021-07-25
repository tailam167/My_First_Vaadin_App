package com.vaadin.application.views;

import com.vaadin.application.model.Product;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.shared.Registration;

import java.util.Date;
import java.util.List;

public class ProductDetailForm extends FormLayout {

    TextField productName = new TextField("Product Name");
    TextField productCode = new TextField("Product Code");
    TextArea description = new TextArea("Product Description");
    DatePicker releaseDate = new DatePicker("Product Release Date");
    TextField price = new TextField("Product Price");
    TextField starRating = new TextField("Product Rating");
    TextField imageUrl = new TextField("Product Image URL");

    Button saveBtn = new Button("Save");
    Button deleteBtn = new Button("Delete");
    Button cancelBtn = new Button("Cancel");

    Binder<Product> binderProduct = new BeanValidationBinder<>(Product.class);
    private Product product;

    /**
     * Constructor for ProductDetailForm class
     *
     * @author tailam
     */
    public ProductDetailForm(List<Product> products) {
        addClassName("contact-form");
        validateProductDetailForm();
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
     * Validating fields in Product Detail Form
     *
     * @author tailam
     */
    private void validateProductDetailForm() {
        binderProduct.forField(productName).asRequired("*Required")
                .withValidator(
                        productName -> !productName.isBlank() && !productName.isEmpty(),
                        "*Must have content")
                .withValidator(
                        name -> name.length() >= 3,
                        "*Name must contain at least 3 characters")
                .bind(com.vaadin.application.model.Product::getProductName, com.vaadin.application.model.Product::setProductName);
        binderProduct.forField(productCode).asRequired("*Required")
                .withValidator(
                        productCode -> !productCode.isBlank() && !productCode.isEmpty(),
                        "*Must have content")
                .withValidator(
                        code -> code.length() >= 6 && code.contains("-"),
                        "*Code must contain at least 6 characters with format (characters-number)"
                )
                .bind(com.vaadin.application.model.Product::getProductCode, com.vaadin.application.model.Product::setProductCode);
        binderProduct.forField(description).asRequired("*Required")
                .withValidator(
                        description -> !description.isBlank() && !description.isEmpty(),
                        "*Must have content")
                .withValidator(
                        description -> description.length() <= 50,
                        "*Description have maximum with 50 characters"
                )
                .bind(com.vaadin.application.model.Product::getDescription, com.vaadin.application.model.Product::setDescription);
        binderProduct.forField(releaseDate).asRequired("*Please choose a date by button")
                .withConverter(new LocalDateToDateConverter())
                .withValidator(
                        releaseDate -> !releaseDate.toString().isBlank() && !releaseDate.toString().isEmpty(),
                        "*Must have content"
                )
                .bind(com.vaadin.application.model.Product::getReleaseDate, com.vaadin.application.model.Product::setReleaseDate);
        binderProduct.forField(price).asRequired("*Required")
                .withValidator(
                        price -> !price.isBlank() && !price.isEmpty(),
                        "*Must have content by number"
                )
                .withValidator(
                        price -> Float.parseFloat(price) >= 1F && Float.parseFloat(price) <= 10000F,
                        "*Price must be between 1 and 10000"
                )
                .withConverter(new StringToFloatConverter("Must be a number"))
                .bind(com.vaadin.application.model.Product::getPrice, com.vaadin.application.model.Product::setPrice);
        binderProduct.forField(starRating).asRequired("*Required")
                .withValidator(
                        starRating -> !starRating.isBlank() && !starRating.isEmpty(),
                        "*Must have content with number"
                )
                .withValidator(
                        starRating -> Float.parseFloat(starRating) >= 1F && Float.parseFloat(starRating) <= 5F,
                        "*Star Rating must be between 1 and 5"
                )
                .withConverter(new StringToFloatConverter("Must be a number"))
                .bind(com.vaadin.application.model.Product::getStarRating, com.vaadin.application.model.Product::setStarRating);
        binderProduct.forField(imageUrl).asRequired("*Required")
                .withValidator(
                        imageUrl -> !imageUrl.isBlank() && !imageUrl.isEmpty(),
                        "*Must have content")
                .withValidator(
                        imageUrl -> imageUrl.contains(".png"),
                        "*Must be .PNG file"
                )
                .bind(com.vaadin.application.model.Product::getImageUrl, com.vaadin.application.model.Product::setImageUrl);
    }

    /**
     * Reading product detail or create product
     *
     * @author tailam
     */
    public void setProduct(com.vaadin.application.model.Product product) {
        this.product = product;
        if (product != null && product.getProductId() != null) {
            binderProduct.readBean(product);
        } else {
            binderProduct.readBean(new com.vaadin.application.model.Product(null, "",
                    "", new Date(), "", 0F, 0F, ""));
        }
    }

    /**
     * Button Save Product
     *
     * @return saveBtn
     */
    public Button getSaveBtn() {
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.setIcon(new Icon(VaadinIcon.CLIPBOARD_CHECK));
        saveBtn.addClickShortcut(Key.ENTER);
        saveBtn.addClickListener(click -> {
            validateAndSave();
            binderProduct.validate();
        });
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
        deleteBtn.setIcon(new Icon(VaadinIcon.TRASH));
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
        cancelBtn.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
        cancelBtn.addClickShortcut(Key.ESCAPE);
        cancelBtn.addClickListener(e -> fireEvent(new CloseEvent(this)));
        return cancelBtn;
    }

    /**
     * Event Component
     *
     * @author tailam
     */
    public static abstract class ProductDetailFormEvent extends ComponentEvent<com.vaadin.application.views.ProductDetailForm> {
        private final com.vaadin.application.model.Product product;

        protected ProductDetailFormEvent(com.vaadin.application.views.ProductDetailForm source, com.vaadin.application.model.Product product) {
            super(source, false);
            this.product = product;
        }

        public com.vaadin.application.model.Product getProduct() {
            return product;
        }
    }

    /**
     * Save Event
     *
     * @author tailam
     */
    public static class SaveEvent extends ProductDetailFormEvent {
        SaveEvent(com.vaadin.application.views.ProductDetailForm source, com.vaadin.application.model.Product product) {
            super(source, product);
        }
    }

    /**
     * Delete Event
     *
     * @author tailam
     */
    public static class DeleteEvent extends ProductDetailFormEvent {
        DeleteEvent(com.vaadin.application.views.ProductDetailForm source, com.vaadin.application.model.Product product) {
            super(source, product);
        }

    }

    /**
     * Close Event
     *
     * @author tailam
     */
    public static class CloseEvent extends ProductDetailFormEvent {
        CloseEvent(com.vaadin.application.views.ProductDetailForm source) {
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
