package com.vaadin.application.views;

import com.vaadin.application.model.Product;
import com.vaadin.application.service.ProductService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.converter.StringToFloatConverter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Create Product Form
 *
 * @author tailam
 */
@PageTitle("New Product")
@Route(value = "create", layout = MainLayout.class)
public class CreateProductForm extends FormLayout {

    ProductService productService;
    TextField productName = new TextField("Product Name");
    TextField productCode = new TextField("Product Code");
    TextArea description = new TextArea("Product Description");
    DatePicker releaseDate = new DatePicker("Product Release Date");
    TextField price = new TextField("Product Price");
    TextField starRating = new TextField("Product Rating");
    TextField imageUrl = new TextField("Product Image URL");

    Button saveNewBtn = new Button("Save");
    Button cancelBtn = new Button("Cancel");

    Binder<Product> binderProduct = new BeanValidationBinder<>(Product.class);
    private Product product;
    boolean isValid = false;

    /**
     * Constructor for CreateProductForm class
     *
     * @author tailam
     */
    public CreateProductForm(ProductService productService) {
        this.productService = productService;
        addClassName("create-product-view");
        validateCreateProductForm();
        binderProduct.bindInstanceFields(this);
        setCreateProductFormLayout();
        this.addListener(SaveEvent.class, this::saveNewProduct);
    }

    /**
     * Set Layout for Create Product Form
     *
     * @author tailam
     */
    public void setCreateProductFormLayout(){
        productName.setPlaceholder("Product Name...");
        productCode.setPlaceholder("Product Code...");
        description.setPlaceholder("Product Description...");
        releaseDate.setPlaceholder("Format MM/dd/yyyy...");
        price.setPlaceholder("Product Price...");
        starRating.setPlaceholder("Product Rating...");
        imageUrl.setPlaceholder("Product Image URL...");

        productName.setWidthFull();
        productCode.setWidthFull();
        description.setWidthFull();
        releaseDate.setWidthFull();
        price.setWidthFull();
        starRating.setWidthFull();
        imageUrl.setWidthFull();

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
    }

    /**
     * Validating fields in Create Product Form
     *
     * @author tailam
     */
    private void validateCreateProductForm() {
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
                .withValidator(
                        releaseDate -> {
                            LocalDate localDate = LocalDate.ofInstant(releaseDate.toInstant(),
                                    ZoneId.systemDefault());
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
                            return localDate.format(formatter).length() == 9;
                        },
                        "*Date must be valid, please choose date by button"
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
     * Button Save Product
     *
     * @return saveBtn
     */
    public Button getSaveBtn() {
        saveNewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveNewBtn.addClickShortcut(Key.ENTER);
        saveNewBtn.setIcon(new Icon(VaadinIcon.CLIPBOARD_CHECK));
        saveNewBtn.addClickListener(click -> validateAndSave());
        saveNewBtn.setEnabled(false);
        binderProduct.addStatusChangeListener(evt -> saveNewBtn.setEnabled(binderProduct.isValid()));
        return saveNewBtn;
    }

    /**
     * Set New Product
     *
     * @author tailam
     */
    public void setProduct(Product product) {
        this.product = product;
        binderProduct.readBean(product);
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
            product.setReleaseDate(Date.valueOf(releaseDate.getValue()));
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
        } catch (Exception exception) {
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
        getUI().ifPresent(ui -> ui.navigate("list"));
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
            removeClassName("create");
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
