package com.vaadin.application.views;

import com.vaadin.application.model.Product;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

/**
 * Product Detail Form
 *
 * @author tailam
 */
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
        setProductDetailFormLayout();
    }

    /**
     * Set layout for Product Detail Form
     *
     * @author tailam
     */
    public void setProductDetailFormLayout(){
        productName.setPlaceholder("Product Name...");
        productCode.setPlaceholder("Product Code...");
        description.setPlaceholder("Product Description...");
        releaseDate.setPlaceholder("Format M/dd/yyyy...");
        price.setPlaceholder("Product Price...");
        starRating.setPlaceholder("Product Rating...");
        imageUrl.setPlaceholder("Product Image URL...");

        price.setPrefixComponent(new Span("$"));

        // Layout for text field productName --> productReleaseDate by vertical
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.add(productName);
        verticalLayout1.add(productCode);
        verticalLayout1.add(description);
        verticalLayout1.add(releaseDate);
        verticalLayout1.setWidth(50, Unit.PERCENTAGE);

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
        horizontalLayoutParent.setSpacing(false);
        horizontalLayoutParent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);

        // add layout to class and set alignment
        add(horizontalLayoutParent);
    }

    /**
     * Validating fields in Product Detail Form
     *
     * @author tailam
     */
    private void validateProductDetailForm() {
        NumberFormat number = new DecimalFormat("##,###.00");
        binderProduct.forField(productName).asRequired("*Required")
                .withValidator(
                        productName -> !productName.isBlank() && !productName.isEmpty(),
                        "*Must have content")
                .withValidator(
                        name -> name.length() >= 3,
                        "*Name must contain at least 3 characters")
                .bind(Product::getProductName, Product::setProductName);
        binderProduct.forField(productCode).asRequired("*Required")
                .withValidator(
                        productCode -> !productCode.isBlank() && !productCode.isEmpty(),
                        "*Must have content")
                .withValidator(
                        productCode -> productCode.length() >= 6 && productCode.contains("-"),
                        "*Code must contain at least 6 characters with correction format \n" +
                                "*Example: ABC-1234"
                )
                .bind(Product::getProductCode, Product::setProductCode);
        binderProduct.forField(description).asRequired("*Required")
                .withValidator(
                        description -> !description.isBlank() && !description.isEmpty(),
                        "*Must have content")
                .withValidator(
                        description -> description.length() <= 50,
                        "*Description have maximum with 50 characters"
                )
                .bind(Product::getDescription, Product::setDescription);
        binderProduct.forField(releaseDate).asRequired("*Please choose a date by button")
                .withConverter(new LocalDateToDateConverter())
                .withValidator(
                        releaseDate -> !releaseDate.toString().isBlank() && !releaseDate.toString().isEmpty(),
                        "*Must have content"
                )
                .bind(Product::getReleaseDate, Product::setReleaseDate);
        binderProduct.forField(price).asRequired("*Required")
                .withValidator(
                        price -> !price.isBlank() && !price.isEmpty(),
                        "*Must have content by number"
                )
                .withValidator(
                        price -> Float.parseFloat(price.replace(",", "")) >= 1.00F &&
                                Float.parseFloat(price.replace(",", "")) <= 10000.00F,
                        "*Price must be between 1 and 10000 \n with correction format: ##,###.##"
                )
                .withConverter(new StringToFloatConverter("Must be a number"))
                .bind(Product::getPrice, Product::setPrice);
        binderProduct.forField(starRating).asRequired("*Required")
                .withValidator(
                        starRating -> !starRating.isBlank() && !starRating.isEmpty(),
                        "*Must have content with number"
                )
                .withValidator(
                        starRating -> Float.parseFloat(starRating) >= 1.00F &&
                                Float.parseFloat(starRating) <= 5.00F,
                        "*Star Rating must be between 1 and 5 \n with correction format: #.##"
                )
                .withConverter(new StringToFloatConverter("Must be a number"))
                .bind(Product::getStarRating, Product::setStarRating);
        binderProduct.forField(imageUrl).asRequired("*Required")
                .withValidator(
                        imageUrl -> !imageUrl.isBlank() && !imageUrl.isEmpty(),
                        "*Must have content")
                .withValidator(
                        imageUrl -> imageUrl.contains(".png"),
                        "*Must be .PNG file"
                )
                .bind(Product::getImageUrl, Product::setImageUrl);
    }

    /**
     * Reading product detail or create product
     *
     * @author tailam
     */
    public void setProduct(Product product) {
        this.product = product;
        if (product != null && product.getProductId() != null) {
            binderProduct.readBean(product);
        } else {
            binderProduct.readBean(new Product(null, "",
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
    public static abstract class ProductDetailFormEvent extends ComponentEvent<ProductDetailForm> {
        private final Product product;

        protected ProductDetailFormEvent(ProductDetailForm source, Product product) {
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
    public static class SaveEvent extends ProductDetailFormEvent {
        SaveEvent(ProductDetailForm source, Product product) {
            super(source, product);
        }
    }

    /**
     * Delete Event
     *
     * @author tailam
     */
    public static class DeleteEvent extends ProductDetailFormEvent {
        DeleteEvent(ProductDetailForm source, Product product) {
            super(source, product);
        }

    }

    /**
     * Close Event
     *
     * @author tailam
     */
    public static class CloseEvent extends ProductDetailFormEvent {
        CloseEvent(ProductDetailForm source) {
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
