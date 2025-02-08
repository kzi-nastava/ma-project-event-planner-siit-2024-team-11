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
import com.example.eventy.databinding.FragmentProductUpdateBinding;
import com.example.eventy.events.model.EventType;
import com.example.eventy.events.model.EventTypeCard;
import com.example.eventy.model.enums.Status;
import com.example.eventy.products.model.CreateProduct;
import com.example.eventy.products.model.Product;
import com.example.eventy.solutions.model.Category;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.solutions.model.SolutionDetails;
import com.example.eventy.users.register.CarouselAdapter;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductUpdateFragment extends Fragment {

    private CarouselAdapter carouselAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private List<String> images = new ArrayList<>();
    private FragmentProductUpdateBinding binding;
    private List<EventTypeCard> eventTypes;
    private Long id = -1L;

    public ProductUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductUpdateBinding.inflate(inflater, container, false);

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

        binding.addProductPhotosButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // Allow multiple selection
            imagePickerLauncher.launch(intent);
        });

        ChipGroup chipGroup = binding.chipGroup;
        Call<EventTypeCard[]> call2 = ClientUtils.eventTypeService.getActiveEventTypes();
        call2.enqueue(new Callback<EventTypeCard[]>() {
            @Override
            public void onResponse(Call<EventTypeCard[]> call, Response<EventTypeCard[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes = new ArrayList<>();
                    List<EventTypeCard> chips = Arrays.asList(response.body());

                    id = getArguments().getLong("id");
                    Call<SolutionDetails> call3 = ClientUtils.solutionService.getSolutionDetails(id);
                    call3.enqueue(new Callback<SolutionDetails>() {
                        @Override
                        public void onResponse(Call<SolutionDetails> call, Response<SolutionDetails> response) {
                            if (response.isSuccessful()) {
                                images = response.body().getImages();
                                carouselAdapter.updateImages(images);
                                binding.productNameInput.setText(response.body().getName());
                                binding.productDescriptionInput.setText(response.body().getDescription());
                                binding.productPriceInput.setText(response.body().getPrice().toString());
                                binding.productDiscountInput.setText(response.body().getDiscount().toString());
                                binding.categoryAutoCompleteTextView.setText(response.body().getCategoryName());
                                binding.isAvailable.setChecked(response.body().getAvailable());
                                binding.isVisible.setChecked(response.body().getVisible());

                                for (EventTypeCard et : chips) {
                                    Chip chip = new Chip(getContext());
                                    chip.setText(et.getName()); // text
                                    chip.setCheckable(true);
                                    chip.setTag(et.getId());    // value;
                                    chip.setChecked(response.body().getEventTypeNames().contains(et.getName()));
                                    chipGroup.addView(chip);
                                    eventTypes.add(et);
                                }
                            } else {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "The solution could not be found.");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SolutionDetails> call, Throwable t) {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    });
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

        if(binding.productNameInputLayout.getError() != null || binding.productDescriptionInputLayout.getError() != null ||
                binding.productPriceInputLayout.getError() != null || binding.productDiscountInputLayout.getError() != null ||
                images == null || images.isEmpty() || id == null || id == -1L) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Validation failed! Check your input fields again!");
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();

            return;
        }

        Product newProduct = new Product();

        newProduct.setId(id);
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
        newProduct.setRelatedEventTypes(selectedEventTypeIds.stream().map(typeId -> {
            EventType et = new EventType();
            et.setId(typeId);
            return et;
        }).collect(Collectors.toList()));
        newProduct.setImages(images);

        Call<Product> call = ClientUtils.productService.update(id, newProduct);
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