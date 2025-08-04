package com.example.eventy.services;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentServiceManipulationBinding;
import com.example.eventy.events.model.EventType;
import com.example.eventy.events.model.EventTypeCard;
import com.example.eventy.model.enums.Status;
import com.example.eventy.model.solution.Service;
import com.example.eventy.services.model.CreateService;
import com.example.eventy.services.model.UpdateService;
import com.example.eventy.solutions.model.Category;
import com.example.eventy.solutions.model.CategoryWithID;
import com.example.eventy.users.register.CarouselAdapter;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceManipulationFragment extends Fragment {

    private CarouselAdapter carouselAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ArrayList<String> images = new ArrayList<>();
    private FragmentServiceManipulationBinding binding;
    private Service service = null;
    private Long selectedCategoryId;

    public ServiceManipulationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceManipulationBinding.inflate(inflater, container, false);

        binding.fixedDurationRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show fixed duration input and hide variable duration inputs
                binding.fixedDurationInputLayout.setVisibility(View.VISIBLE);
                binding.minimumDurationInputLayout.setVisibility(View.GONE);
                binding.maximumDurationInputLayout.setVisibility(View.GONE);
            }
        });

        binding.variableDurationRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show variable duration inputs and hide fixed duration input
                binding.fixedDurationInputLayout.setVisibility(View.GONE);
                binding.minimumDurationInputLayout.setVisibility(View.VISIBLE);
                binding.maximumDurationInputLayout.setVisibility(View.VISIBLE);
            }
        });

        ViewPager2 viewPager = binding.servicePhotos;

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

        binding.addServicePhotosButton.setOnClickListener(v -> {
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
                            binding.serviceNewCategoryNameInputLayout.setVisibility(View.VISIBLE);
                            binding.serviceNewCategoryDescriptionInputLayout.setVisibility(View.VISIBLE);
                        } else {
                            binding.serviceNewCategoryNameInputLayout.setVisibility(View.GONE);
                            binding.serviceNewCategoryDescriptionInputLayout.setVisibility(View.GONE);
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
                    for (EventTypeCard et : response.body()) {
                        Chip chip = new Chip(getContext());
                        chip.setText(et.getName()); // text
                        chip.setCheckable(true);
                        chip.setTag(et.getId());    // value
                        chipGroup.addView(chip);
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

        if (getArguments() != null) {
            Long id = getArguments().getLong("id");
            Call<Service> call3 = ClientUtils.serviceService.getService(id);
            call3.enqueue(new Callback<Service>() {
                @Override
                public void onResponse(Call<Service> call, Response<Service> response) {
                    if (response.isSuccessful()) {
                        service = response.body();
                        loadData();
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "The solution could not be found.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<Service> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        }

        setupValidation();
        return binding.getRoot();
    }

    private void loadData() {
        binding.title.setText("Edit service - " + service.getName());
        binding.serviceNameInput.setText(service.getName());
        binding.serviceDescriptionInput.setText(service.getDescription());
        binding.serviceSpecificsInput.setText(service.getSpecifics());
        binding.serviceCategoryUponEditLayout.setVisibility(View.VISIBLE);
        binding.serviceCategoryUponEditInput.setText(service.getCategory().getName());

        binding.serviceCategoryInputLayout.setVisibility(View.GONE);

        Integer maxTime = service.getMaxReservationTime();
        if (maxTime.intValue() == service.getMinReservationTime().intValue()) {
            binding.fixedDurationRadioButton.setChecked(true);
            binding.fixedDurationInputLayout.setVisibility(View.VISIBLE);
            binding.minimumDurationInputLayout.setVisibility(View.GONE);
            binding.maximumDurationInputLayout.setVisibility(View.GONE);
            binding.fixedDurationInput.setText(String.valueOf(service.getMinReservationTime()));
        } else {
            binding.variableDurationRadioButton.setChecked(true);
            binding.fixedDurationInputLayout.setVisibility(View.GONE);
            binding.minimumDurationInputLayout.setVisibility(View.VISIBLE);
            binding.maximumDurationInputLayout.setVisibility(View.VISIBLE);
            binding.minimumDurationInput.setText(String.valueOf(service.getMinReservationTime()));
            binding.maximumDurationInput.setText(String.valueOf(service.getMaxReservationTime()));
        }

        binding.servicePriceInput.setText(String.valueOf(service.getPrice()));
        binding.serviceDiscountInput.setText(String.valueOf(service.getDiscount()));

        binding.serviceDaysNoticeReservationInput.setText(String.valueOf(service.getReservationDeadline()));
        binding.serviceDaysNoticeCancellationInput.setText(String.valueOf(service.getCancellationDeadline()));

        binding.checkbox.setChecked(service.getAutomaticReservationAcceptance());

        if (service.getRelatedEventTypes() != null && service.getRelatedEventTypes().size() > 0) {
            for (EventType eventType : service.getRelatedEventTypes()) {
                for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                    if (chip.getTag().equals(eventType.getId())) {
                        chip.setChecked(true);
                        break;
                    }
                }
            }
        }

        List<String> imageUrls = service.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            images.clear();
            images.addAll(imageUrls);
            carouselAdapter.updateImages(images);
        }

        binding.submitButton.setText("Confirm changes");
    }

    private void submit() {
        if (isValid()) {
            if (service == null) {
                if (selectedCategoryId == -1337L) {
                    Category newCategory = new Category(binding.serviceNewCategoryNameInput.getText().toString(), binding.serviceNewCategoryDescriptionInput.getText().toString(), Status.PENDING);
                    Call<CategoryWithID> call = ClientUtils.categoryService.createCategory(newCategory);
                    call.enqueue(new Callback<CategoryWithID>() {
                        @Override
                        public void onResponse(Call<CategoryWithID> call, Response<CategoryWithID> response) {
                            if (response.isSuccessful()) {
                                CreateService newService = new CreateService();

                                newService.setName(binding.serviceNameInput.getText().toString());
                                newService.setDescription(binding.serviceDescriptionInput.getText().toString());
                                newService.setSpecifics(binding.serviceSpecificsInput.getText().toString());
                                newService.setCategoryId(response.body().getId());
                                newService.setProviderId(LoggedInHelperService.getId());
                                newService.setAutomaticReservationAcceptance(binding.checkbox.isChecked());
                                newService.setPrice(Double.parseDouble(binding.servicePriceInput.getText().toString()));
                                newService.setDiscount(Double.parseDouble(binding.serviceDiscountInput.getText().toString()));
                                newService.setReservationDeadline(Integer.parseInt(binding.serviceDaysNoticeReservationInput.getText().toString()));
                                newService.setCancellationDeadline(Integer.parseInt(binding.serviceDaysNoticeCancellationInput.getText().toString()));
                                if (binding.fixedDurationRadioButton.isChecked()) {
                                    newService.setMinReservationTime(Integer.parseInt(binding.fixedDurationInput.getText().toString()));
                                    newService.setMaxReservationTime(Integer.parseInt(binding.fixedDurationInput.getText().toString()));
                                } else {
                                    newService.setMinReservationTime(Integer.parseInt(binding.minimumDurationInput.getText().toString()));
                                    newService.setMaxReservationTime(Integer.parseInt(binding.maximumDurationInput.getText().toString()));
                                }

                                List<Long> selectedEventTypeIds = new ArrayList<>();
                                for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                                    Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                                    if (chip.isChecked()) {
                                        selectedEventTypeIds.add((Long) chip.getTag());
                                    }
                                }
                                newService.setRelatedEventTypeIds(selectedEventTypeIds);
                                newService.setImageUrls(images);

                                Call<Service> call2 = ClientUtils.serviceService.createService(newService);
                                call2.enqueue(new Callback<Service>() {
                                    @Override
                                    public void onResponse(Call<Service> call, Response<Service> response) {
                                        if (response.isSuccessful()) {
                                            Bundle args = new Bundle();
                                            args.putLong("solutionId", response.body().getId());

                                            NavController navController = Navigation.findNavController(getView());
                                            navController.popBackStack();
                                            navController.navigate(R.id.nav_solution_details, args);
                                        } else {
                                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Could not create the service!");
                                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            errorOkDialog.show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Service> call, Throwable t) {
                                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        errorOkDialog.show();
                                    }
                                });
                            } else {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error creating a new category!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryWithID> call, Throwable t) {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    });
                } else {
                    CreateService newService = new CreateService();

                    newService.setName(binding.serviceNameInput.getText().toString());
                    newService.setDescription(binding.serviceDescriptionInput.getText().toString());
                    newService.setSpecifics(binding.serviceSpecificsInput.getText().toString());
                    newService.setCategoryId(selectedCategoryId);
                    newService.setProviderId(LoggedInHelperService.getId());
                    newService.setAutomaticReservationAcceptance(binding.checkbox.isChecked());
                    newService.setPrice(Double.parseDouble(binding.servicePriceInput.getText().toString()));
                    newService.setDiscount(Double.parseDouble(binding.serviceDiscountInput.getText().toString()));
                    newService.setReservationDeadline(Integer.parseInt(binding.serviceDaysNoticeReservationInput.getText().toString()));
                    newService.setCancellationDeadline(Integer.parseInt(binding.serviceDaysNoticeCancellationInput.getText().toString()));
                    if (binding.fixedDurationRadioButton.isChecked()) {
                        newService.setMinReservationTime(Integer.parseInt(binding.fixedDurationInput.getText().toString()));
                        newService.setMaxReservationTime(Integer.parseInt(binding.fixedDurationInput.getText().toString()));
                    } else {
                        newService.setMinReservationTime(Integer.parseInt(binding.minimumDurationInput.getText().toString()));
                        newService.setMaxReservationTime(Integer.parseInt(binding.maximumDurationInput.getText().toString()));
                    }

                    List<Long> selectedEventTypeIds = new ArrayList<>();
                    for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                        Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                        if (chip.isChecked()) {
                            selectedEventTypeIds.add((Long) chip.getTag());
                        }
                    }
                    newService.setRelatedEventTypeIds(selectedEventTypeIds);
                    newService.setImageUrls(images);

                    Call<Service> call = ClientUtils.serviceService.createService(newService);
                    call.enqueue(new Callback<Service>() {
                        @Override
                        public void onResponse(Call<Service> call, Response<Service> response) {
                            if (response.isSuccessful()) {
                                Bundle args = new Bundle();
                                args.putLong("solutionId", response.body().getId());

                                NavController navController = Navigation.findNavController(getView());
                                navController.popBackStack();
                                navController.navigate(R.id.nav_solution_details, args);
                            } else {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Could not create the service!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Service> call, Throwable t) {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    });
                }
            } else {
                UpdateService updateService = new UpdateService();

                updateService.setId(service.getId());
                updateService.setName(binding.serviceNameInput.getText().toString());
                updateService.setDescription(binding.serviceDescriptionInput.getText().toString());
                updateService.setSpecifics(binding.serviceSpecificsInput.getText().toString());
                updateService.setVisible(service.getVisible());
                updateService.setAvailable(service.getAvailable());
                updateService.setAutomaticReservationAcceptance(binding.checkbox.isChecked());
                updateService.setPrice(Double.parseDouble(binding.servicePriceInput.getText().toString()));
                updateService.setDiscount(Integer.parseInt(binding.serviceDiscountInput.getText().toString()));
                updateService.setReservationDeadline(Integer.parseInt(binding.serviceDaysNoticeReservationInput.getText().toString()));
                updateService.setCancellationDeadline(Integer.parseInt(binding.serviceDaysNoticeCancellationInput.getText().toString()));
                if (binding.fixedDurationRadioButton.isChecked()) {
                    updateService.setMinReservationTime(Integer.parseInt(binding.fixedDurationInput.getText().toString()));
                    updateService.setMaxReservationTime(Integer.parseInt(binding.fixedDurationInput.getText().toString()));
                } else {
                    updateService.setMinReservationTime(Integer.parseInt(binding.minimumDurationInput.getText().toString()));
                    updateService.setMaxReservationTime(Integer.parseInt(binding.maximumDurationInput.getText().toString()));
                }

                List<Long> selectedEventTypeIds = new ArrayList<>();
                for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                    if (chip.isChecked()) {
                        selectedEventTypeIds.add((Long) chip.getTag());
                    }
                }
                updateService.setRelatedEventTypeIds(selectedEventTypeIds);
                updateService.setImageUrls(images);

                Call<Service> call = ClientUtils.serviceService.updateService(updateService);
                call.enqueue(new Callback<Service>() {
                    @Override
                    public void onResponse(Call<Service> call, Response<Service> response) {
                        if (response.isSuccessful()) {
                            Bundle args = new Bundle();
                            args.putLong("solutionId", response.body().getId());

                            NavController navController = Navigation.findNavController(getView());
                            navController.popBackStack();
                            navController.navigate(R.id.nav_solution_details, args);
                        } else {
                            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Could not create the service!");
                            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            errorOkDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Service> call, Throwable t) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                });
            }
        } else {
            new AlertDialog.Builder(getContext())
                    .setMessage("Not all fields are valid!")
                    .setCancelable(true)
                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                    .show();
        }
    }

    private void setupValidation() {
        binding.serviceNameInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isTextValid(binding.serviceNameInputLayout, binding.serviceNameInput, "Name");
            }
        });
        binding.serviceDescriptionInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isTextValid(binding.serviceDescriptionInputLayout, binding.serviceDescriptionInput, "Description");
            }
        });
        binding.serviceSpecificsInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isTextValid(binding.serviceSpecificsInputLayout, binding.serviceSpecificsInput, "Specifics");
            }
        });
        binding.serviceNewCategoryNameInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isTextValid(binding.serviceNewCategoryNameInputLayout, binding.serviceNewCategoryNameInput, "Category name");
            }
        });
        binding.serviceNewCategoryDescriptionInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isTextValid(binding.serviceNewCategoryDescriptionInputLayout, binding.serviceNewCategoryDescriptionInput, "Category description");
            }
        });
        binding.fixedDurationInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isNumberValid(binding.fixedDurationInputLayout, binding.fixedDurationInput, "Fixed duration", 0, null);
            }
        });
        binding.minimumDurationInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isNumberValid(binding.minimumDurationInputLayout, binding.minimumDurationInput, "Minimum duration", 0, null);
            }
        });
        binding.maximumDurationInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isNumberValid(binding.maximumDurationInputLayout, binding.maximumDurationInput, "Maximum duration", 0, null);
            }
        });
        binding.categoryAutoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isCategoryValid();
            }
        });
        binding.serviceDaysNoticeReservationInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isNumberValid(binding.serviceDaysNoticeReservationInputLayout, binding.serviceDaysNoticeReservationInput, "Reservation deadline", 0, null);
            }
        });
        binding.serviceDaysNoticeCancellationInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isNumberValid(binding.serviceDaysNoticeCancellationInputLayout, binding.serviceDaysNoticeCancellationInput, "Cancellation deadline", 0, null);
            }
        });
        binding.servicePriceInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isNumberValid(binding.servicePriceInputLayout, binding.servicePriceInput, "Price", 0, null);
            }
        });
        binding.serviceDiscountInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                isNumberValid(binding.serviceDiscountInputLayout, binding.serviceDiscountInput, "Discount", 0, 100);
            }
        });
    }

    private boolean isValid() {
        boolean valid = isTextValid(binding.serviceNameInputLayout, binding.serviceNameInput, "Name");
        valid = isTextValid(binding.serviceDescriptionInputLayout, binding.serviceDescriptionInput, "Description") && valid;
        valid = isTextValid(binding.serviceSpecificsInputLayout, binding.serviceSpecificsInput, "Specifics") && valid;
        if (service == null) {
            valid = isCategoryValid() && valid;
            if (selectedCategoryId != null && selectedCategoryId == -1337L) {
                valid = isTextValid(binding.serviceNewCategoryNameInputLayout, binding.serviceNewCategoryNameInput, "Category name") && valid;
                valid = isTextValid(binding.serviceNewCategoryDescriptionInputLayout, binding.serviceNewCategoryDescriptionInput, "Category description") && valid;
            }
        }
        if (binding.fixedDurationRadioButton.isChecked()) {
            isNumberValid(binding.fixedDurationInputLayout, binding.fixedDurationInput, "Fixed duration", 0, null);
        } else if (binding.variableDurationRadioButton.isChecked()) {
            boolean minValid = isNumberValid(binding.minimumDurationInputLayout, binding.minimumDurationInput, "Minimum duration", 0, null);
            boolean maxValid = isNumberValid(binding.maximumDurationInputLayout, binding.maximumDurationInput, "Maximum duration", 0, null);
            valid = minValid && valid;
            valid = maxValid && valid;
            if (minValid && maxValid) {
                valid = isDurationValid() && valid;
            }
        } else {
            valid = false;
        }
        valid = isEventTypesValid() && valid;
        valid = isNumberValid(binding.serviceDaysNoticeReservationInputLayout, binding.serviceDaysNoticeReservationInput, "Reservation deadline", 0, null) && valid;
        valid = isNumberValid(binding.serviceDaysNoticeCancellationInputLayout, binding.serviceDaysNoticeCancellationInput, "Cancellation deadline", 0, null) && valid;
        valid = isNumberValid(binding.servicePriceInputLayout, binding.servicePriceInput, "Price", 0, null) && valid;
        valid = isNumberValid(binding.serviceDiscountInputLayout, binding.serviceDiscountInput, "Discount", 0, 100) && valid;
        valid = isPhotoValid() && valid;

        return valid;
    }

    private boolean isTextValid(TextInputLayout layout, TextInputEditText editText, String field) {
        if (String.valueOf(editText.getText()).isEmpty()) {
            layout.setError(field + " is required");
            layout.setErrorEnabled(true);
            return false;
        } else {
            layout.setError(null);
            layout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isNumberValid(TextInputLayout layout, TextInputEditText editText, String field, Integer lowerLimit, Integer upperLimit) {
        if (String.valueOf(editText.getText()).isEmpty()) {
            layout.setError(field + " is required");
            layout.setErrorEnabled(true);
            return false;
        }

        try {
            Double numberValue = Double.parseDouble(editText.getText().toString());
            if (lowerLimit != null && numberValue <= lowerLimit) {
                layout.setError(field + " must be greater than " + lowerLimit);
                layout.setErrorEnabled(true);
                return false;
            } else if (upperLimit != null && numberValue > upperLimit) {
                layout.setError(field + " must not be greater than " + upperLimit);
                layout.setErrorEnabled(true);
                return false;
            }
            else {
                layout.setError(null);
                layout.setErrorEnabled(false);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            layout.setError(field + " must be a number");
            layout.setErrorEnabled(true);
            return false;
        }
    }

    private boolean isEventTypesValid() {
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private boolean isCategoryValid() {
        if (selectedCategoryId == null) {
            binding.serviceCategoryInputLayout.setError("Category is required");
            binding.serviceCategoryInputLayout.setErrorEnabled(true);
            return false;
        } else {
            binding.serviceCategoryInputLayout.setError(null);
            binding.serviceCategoryInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isDurationValid() {
        int minValue = Integer.parseInt(binding.minimumDurationInput.getText().toString());
        int maxValue = Integer.parseInt(binding.maximumDurationInput.getText().toString());
        if (maxValue >= minValue) {
            binding.minimumDurationInputLayout.setError(null);
            binding.minimumDurationInputLayout.setErrorEnabled(false);
            binding.maximumDurationInputLayout.setError(null);
            binding.maximumDurationInputLayout.setErrorEnabled(false);
            return true;
        } else {
            binding.minimumDurationInputLayout.setError("Minimum duration be smaller than maximum duration");
            binding.minimumDurationInputLayout.setErrorEnabled(true);
            binding.maximumDurationInputLayout.setError("Maximum duration must be bigger than minimum duration");
            binding.maximumDurationInputLayout.setErrorEnabled(true);
            return false;
        }
    }

    private boolean isPhotoValid() {
        return !images.isEmpty();
    }
}