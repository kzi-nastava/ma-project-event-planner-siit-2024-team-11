package com.example.eventy.products;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentProductCreationBinding;
import com.example.eventy.events.model.EventTypeCard;
import com.example.eventy.products.model.CreateProduct;
import com.example.eventy.products.model.Product;
import com.example.eventy.solutions.model.Category;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.model.enums.Status;
import com.example.eventy.users.register.CarouselAdapter;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCreationFragment extends Fragment {
    private CarouselAdapter carouselAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ArrayList<String> images = new ArrayList<>();
    private FragmentProductCreationBinding binding;
    private Long selectedCategoryId;
    private List<CategoryWithID> categories;
    private List<EventTypeCard> eventTypes;

    public ProductCreationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductCreationBinding.inflate(inflater, container, false);

        ViewPager2 viewPager = binding.productPhotos;

        carouselAdapter = new CarouselAdapter(images);
        viewPager.setAdapter(carouselAdapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    ArrayList<String> newImages = new ArrayList<>();
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            ClipData clipData = result.getData().getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                try {
                                    Uri imageUri = clipData.getItemAt(i).getUri();
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                            getActivity().getContentResolver(), imageUri);
                                    newImages.add(PictureHelperService.bitmapToBase64(bitmap));
                                }
                                catch (Exception e) {
                                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while selecting the picture!");
                                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    errorOkDialog.show();
                                }
                            }
                        } else if (result.getData().getData() != null) {
                            try {
                                Uri imageUri = result.getData().getData();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        getActivity().getContentResolver(), imageUri);
                                newImages.add(PictureHelperService.bitmapToBase64(bitmap));
                            }
                            catch (Exception e) {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while selecting the picture!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        }

                        carouselAdapter.updateImages(newImages);
                        images = newImages;
                    }
                }
        );

        addValidation(binding.productNameInputLayout, binding.productNameInput, this::validateRequired);
        addValidation(binding.productDescriptionInputLayout, binding.productDescriptionInput, this::validateRequired);
        addValidation(binding.productPriceInputLayout, binding.productPriceInput, this::validateRequired);
        addValidation(binding.productDiscountInputLayout, binding.productDiscountInput, this::validateRequired);
        addValidation(binding.productNewCategoryNameInputLayout, binding.productNewCategoryNameInput, this::validateRequired);
        addValidation(binding.productNewCategoryDescriptionInputLayout, binding.productNewCategoryDescriptionInput, this::validateRequired);

        binding.addProductPhotosButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // Allow multiple selection
            imagePickerLauncher.launch(intent);
        });

        Call<List<CategoryWithID>> call = ClientUtils.categoryService.getActiveCategories();
        call.enqueue(new Callback<List<CategoryWithID>>() {
            @Override
            public void onResponse(Call<List<CategoryWithID>> call, Response<List<CategoryWithID>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    MaterialAutoCompleteTextView categoryAutoCompleteTextView = binding.categoryAutoCompleteTextView;

                    CategoryWithID stubNewCategory = new CategoryWithID(-1337L, "New Category", "New Category", Status.ACCEPTED);
                    List<CategoryWithID> allSolutionCategories = new ArrayList<>();
                    allSolutionCategories.add(stubNewCategory);
                    allSolutionCategories.addAll(response.body());
                    ArrayAdapter<CategoryWithID> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, allSolutionCategories);

                    categoryAutoCompleteTextView.setAdapter(adapter);

                    categoryAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                        CategoryWithID selectedCard = (CategoryWithID) parent.getItemAtPosition(position);
                        selectedCategoryId = selectedCard.getId();

                        if (selectedCategoryId == -1337L) {
                            binding.productNewCategoryNameInputLayout.setVisibility(View.VISIBLE);
                            binding.productNewCategoryDescriptionInputLayout.setVisibility(View.VISIBLE);
                        } else {
                            binding.productNewCategoryNameInputLayout.setVisibility(View.GONE);
                            binding.productNewCategoryDescriptionInputLayout.setVisibility(View.GONE);
                        }
                    });

                    categoryAutoCompleteTextView.setOnClickListener(v -> {
                        if (!categoryAutoCompleteTextView.isPopupShowing()) {
                            categoryAutoCompleteTextView.showDropDown();
                        }
                    });

                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error loading event types!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryWithID>> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });

        ChipGroup chipGroup = binding.chipGroup;
        Call<EventTypeCard[]> call2 = ClientUtils.eventTypeService.getActiveEventTypes();
        call2.enqueue(new Callback<EventTypeCard[]>() {
            @Override
            public void onResponse(Call<EventTypeCard[]> call, Response<EventTypeCard[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes.clear();
                    for (EventTypeCard et : response.body()) {
                        Chip chip = new Chip(getContext());
                        chip.setText(et.getName()); // text
                        chip.setCheckable(true);
                        chip.setTag(et.getId());    // value
                        chipGroup.addView(chip);
                        eventTypes.add(et);
                    }
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error loading event types!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<EventTypeCard[]> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });

        binding.submitButton.setOnClickListener(v -> {
            submit();
        });

        return binding.getRoot();
    }

    private void submit() {
        binding.productNameInput.setText(binding.productNameInput.getText());
        binding.productDescriptionInput.setText(binding.productDescriptionInput.getText());
        binding.productPriceInput.setText(binding.productPriceInput.getText());
        binding.productDiscountInput.setText(binding.productDiscountInput.getText());

        if (selectedCategoryId == -1337L) {
            binding.productNewCategoryNameInput.setText(binding.productNewCategoryNameInput.getText());
            binding.productNewCategoryDescriptionInput.setText(binding.productNewCategoryDescriptionInput.getText());
        }

        if(binding.productNameInputLayout.getError() != null || binding.productDescriptionInputLayout.getError() != null ||
        binding.productPriceInputLayout.getError() != null || binding.productDiscountInputLayout.getError() != null ||
                selectedCategoryId == null || images == null || images.isEmpty() ||
                (selectedCategoryId == -1337L && (binding.productNewCategoryNameInputLayout.getError() != null || binding.productNewCategoryDescriptionInputLayout.getError() != null))) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Validation failed! Check your input fields again!");
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();

            return;
        }

        CreateProduct newProduct = new CreateProduct();

        newProduct.setName(binding.productNameInput.getText().toString());
        newProduct.setDescription(binding.productDescriptionInput.getText().toString());
        newProduct.setPrice(Double.parseDouble(binding.productPriceInput.getText().toString()));
        newProduct.setDiscount(Integer.parseInt(binding.productDiscountInput.getText().toString()));
        newProduct.setIsAvailable(binding.isAvailable.isChecked());
        newProduct.setIsVisible(binding.isVisible.isChecked());

        List<Long> selectedEventTypeIds = new ArrayList<>();
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                selectedEventTypeIds.add((Long) chip.getTag());
            }
        }
        newProduct.setRelatedEventTypes(selectedEventTypeIds.stream().map(typeId -> eventTypes.stream().filter(type -> type.getId().equals(typeId)).findFirst().orElse(null)).collect(Collectors.toList()));
        newProduct.setImageUrls(images);

        if (selectedCategoryId == -1337L) {
            Category newCategory = new Category(binding.productNewCategoryNameInput.getText().toString(), binding.productNewCategoryDescriptionInput.getText().toString(), Status.PENDING);
            Call<CategoryWithID> call = ClientUtils.categoryService.createCategory(newCategory);
            call.enqueue(new Callback<CategoryWithID>() {
                @Override
                public void onResponse(Call<CategoryWithID> call, Response<CategoryWithID> response) {
                    newProduct.setCategory(response.body());

                    Call<Product> call2 = ClientUtils.productService.create(newProduct);
                    call2.enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            if (response.isSuccessful()) {
                                Bundle args = new Bundle();
                                args.putLong("solutionId", response.body().getId());

                                NavController navController = Navigation.findNavController(getView());
                                navController.popBackStack();
                                navController.navigate(R.id.nav_solution_details, args);
                            } else {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Could not create the product!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<CategoryWithID> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error! Couldn't create the category!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        }
        else {
            newProduct.setCategory(categories.stream()
                    .filter(category -> category.getId().equals(selectedCategoryId))
                    .findFirst().orElse(null));

            Call<Product> call = ClientUtils.productService.create(newProduct);
            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful()) {
                        Bundle args = new Bundle();
                        args.putLong("solutionId", response.body().getId());

                        NavController navController = Navigation.findNavController(getView());
                        navController.popBackStack();
                        navController.navigate(R.id.nav_solution_details, args);
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Could not create the product!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        }
    }

    private void addValidation(TextInputLayout textInputLayout, TextInputEditText textInputEditText, BiConsumer<String, TextInputLayout> action) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                action.accept(s.toString(), textInputLayout);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        textInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            action.accept(String.valueOf(textInputEditText.getText()), textInputLayout);
        });
    }

    private void validateRequired(String inputText, TextInputLayout textInputLayout) {
        if (inputText.trim().isEmpty()) {
            textInputLayout.setError("This field is required");
        } else {
            textInputLayout.setError(null);
        }
    }
}