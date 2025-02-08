package com.example.eventy.solutions;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventy.R;
import com.example.eventy.common.PictureHelperService;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.custom.ValidOkDialog;
import com.example.eventy.databinding.FragmentSolutionDetailsBinding;
import com.example.eventy.model.enums.ReservationConfirmationType;
import com.example.eventy.solutions.enums.SolutionType;
import com.example.eventy.solutions.model.SolutionCard;
import com.example.eventy.solutions.model.SolutionDetails;
import com.example.eventy.users.services.LoggedInHelperService;
import com.example.eventy.utils.ClientUtils;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolutionDetailsFragment extends Fragment {

    private SolutionDetails solution;
    private FragmentSolutionDetailsBinding binding;

    public SolutionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolutionDetailsBinding.inflate(inflater, container, false);

        Long id = -1L;
        if (getArguments() != null) {
            id = getArguments().getLong("solutionId");
        }

        binding.toggleVisibilityButton.setOnClickListener(v -> {
            toggleVisibility();
        });

        binding.toggleAvailabilityButton.setOnClickListener(v -> {
            toggleAvailability();
        });

        final Long finalId = id;
        binding.editButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("id", finalId);

            NavController navController = Navigation.findNavController(getView());
            navController.popBackStack();
            navController.navigate(R.id.nav_manipulate_service, args);
        });

        binding.deleteButton.setOnClickListener(v -> {
            handleDelete();
        });

        if(LoggedInHelperService.getRole() != null && LoggedInHelperService.getRole().equals("ROLE_Organizer"))
        {
            binding.chatButton.setVisibility(View.VISIBLE);
            binding.purchaseButton.setVisibility(View.VISIBLE);
        }

        if (id == -1L) {
            ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Solution could not be found.");
            errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorOkDialog.show();
        } else {
            Call<SolutionDetails> call = ClientUtils.solutionService.getSolutionDetails(id);
            call.enqueue(new Callback<SolutionDetails>() {
                @Override
                public void onResponse(Call<SolutionDetails> call, Response<SolutionDetails> response) {
                    if (response.isSuccessful()) {
                        solution = response.body();
                        loadPage();
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Solution could not be loaded.");
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
        }

        return binding.getRoot();
    }

    private void loadPage() {

        if (Objects.equals(LoggedInHelperService.getId(), solution.getProviderId())) {
            binding.toggleAvailabilityButton.setVisibility(View.VISIBLE);
            binding.toggleVisibilityButton.setVisibility(View.VISIBLE);
            binding.editButton.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.VISIBLE);

            setUpAvailabilityButton();
            setUpVisibilityButton();
        }



        binding.title.setText(solution.getName() + " - " + solution.getCategoryName());

        if (solution.getImages() != null) {
            Drawable picture = PictureHelperService.getPicture(solution.getImages().get(0), getContext());
            if (picture != null) {
                binding.image.setBackground(picture);
            }
        }

        binding.description.setText(solution.getDescription());

        binding.eventTypes.setText("Suitable for: " + TextUtils.join(", ", solution.getEventTypeNames()));

        // TO-DO: maybe needs altering if there is no discount
        String currentPriceString = String.valueOf(solution.getPrice());
        binding.beforePrice.setText(currentPriceString);
        binding.crossedOutPrice.setText(currentPriceString);

        double discountedPrice = solution.getPrice() - solution.getPrice() * solution.getDiscount() / 100;
        String discountedPriceString = String.format("%.2f", discountedPrice);
        binding.currentPrice.setText(discountedPriceString);

        Drawable providerPicture = PictureHelperService.getPicture(solution.getProviderImageUrl(), getContext());
        if (providerPicture != null) {
            binding.providerImage.setBackground(providerPicture);
            binding.providerImage.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                Bundle args = new Bundle();
                args.putLong("userId", solution.getProviderId());
                navController.popBackStack();
                navController.navigate(R.id.nav_other_user_profile_page, args);
            });
        }

        binding.providerName.setText(solution.getProviderName());
        binding.providerName.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            Bundle args = new Bundle();
            args.putLong("userId", solution.getProviderId());
            navController.popBackStack();
            navController.navigate(R.id.nav_other_user_profile_page, args);
        });

        if(solution.getAvailable()) {
            binding.purchaseButton.setText("Buy product");
        } else {
            binding.purchaseButton.setText("Product unavailable");
            binding.purchaseButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.button_color));
            binding.purchaseButton.setEnabled(false);
        }

        if (solution.getType().equals(SolutionType.SERVICE)) {
            binding.specifics.setText(solution.getSpecifics());
            binding.specifics.setVisibility(View.VISIBLE);

            StringBuilder lengthBuilder = new StringBuilder("Service length: ");
            lengthBuilder.append(solution.getMinReservationTime());
            if (solution.getMaxReservationTime().intValue() != solution.getMinReservationTime().intValue()) {
                lengthBuilder.append(" - ");
                lengthBuilder.append(solution.getMaxReservationTime());
            }
            binding.serviceLength.setText(lengthBuilder.toString());
            binding.serviceLength.setVisibility(View.VISIBLE);

            SpannableStringBuilder deadlineBuilder = new SpannableStringBuilder();
            SpannableString part1 = new SpannableString("Contact the provider at least ");
            SpannableString part2 = new SpannableString(solution.getReservationDeadline() + " days");
            SpannableString part3 = new SpannableString(" before the day you want this service to be held to reserve it, and at least ");
            SpannableString part4 = new SpannableString(solution.getCancellationDeadline() + " days");
            SpannableString part5 = new SpannableString(" before the service is held to cancel it.");
            part2.setSpan(new StyleSpan(Typeface.BOLD), 0, part2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            part4.setSpan(new StyleSpan(Typeface.BOLD), 0, part4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            deadlineBuilder.append(part1).append(part2).append(part3).append(part4).append(part5);
            binding.deadlines.setText(deadlineBuilder);
            binding.deadlines.setVisibility(View.VISIBLE);

            binding.acceptanceType.setText(solution.getReservationType().equals(ReservationConfirmationType.AUTOMATIC) ? "Your reservation will be immediately accepted." : "Your reservation will be reviewed by the provider before being accepted or rejected.");
            binding.acceptanceType.setVisibility(View.VISIBLE);

            if(solution.getAvailable()) {
                binding.purchaseButton.setText("Reserve service");
            } else {
                binding.purchaseButton.setText("Service unavailable");
                binding.purchaseButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.button_color));
                binding.purchaseButton.setEnabled(false);
            }
        }

        if (solution.isFavorite()) {
            binding.favoriteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_favorite_smaller_white));
        }

        binding.favoriteButton.setOnClickListener(v -> {
            Call<Boolean> call2 = ClientUtils.solutionService.toggleFavorite(solution.getSolutionId());
            call2.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        solution.setFavorite(!solution.isFavorite());

                        if (solution.isFavorite()) {
                            binding.favoriteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_favorite_smaller_white));
                        } else {
                            binding.favoriteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_favorite_smaller));
                        }

                        Toast.makeText(getContext(), (solution.isFavorite() ? "Favorite: " : "Removed Favorite: ") + solution.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Please log in to make this your favorite solution.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Please log in to make this your favorite solution.");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        });
    }

    private void toggleVisibility() {
        Call<Void> call = ClientUtils.solutionService.toggleVisibility(solution.getSolutionId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    solution.setVisible(!solution.getVisible());
                    setUpVisibilityButton();
                    ValidOkDialog validOkDialog = new ValidOkDialog(getActivity(), "Visibility changed", "You have set the solution to " + (solution.getVisible() ? "visible!" : "invisible!"));
                    validOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    validOkDialog.show();
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error toggling visibility for solution!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }

    private void toggleAvailability() {
        Call<Void> call = ClientUtils.solutionService.toggleAvailability(solution.getSolutionId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    solution.setAvailable(!solution.getAvailable());
                    setUpAvailabilityButton();
                    ValidOkDialog validOkDialog = new ValidOkDialog(getActivity(), "Availability changed", "You have made the solution " + (solution.getAvailable() ? "available!" : "unavailable!"));
                    validOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    validOkDialog.show();
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error toggling availability for solution!");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }

    private void setUpVisibilityButton() {
        binding.toggleVisibilityButton.setText(solution.getVisible() ? "Make invisible" : "Make visible");
        binding.toggleVisibilityButton.setBackgroundTintList(solution.getVisible() ? ContextCompat.getColorStateList(getContext(), R.color.red_255_button) : ContextCompat.getColorStateList(getContext(), R.color.green_button));
    }

    private void setUpAvailabilityButton() {
        binding.toggleAvailabilityButton.setText(solution.getAvailable() ? "Make unavailable" : "Make available");
        binding.toggleAvailabilityButton.setBackgroundTintList(solution.getAvailable() ? ContextCompat.getColorStateList(getContext(), R.color.red_255_button) : ContextCompat.getColorStateList(getContext(), R.color.green_button));
    }

    private void handleDelete() {

        new AlertDialog.Builder(getContext())
                .setMessage("Are you sure you want to delete the solution " + solution.getName() + "? This action can not be reversed.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Call<Void> call = ClientUtils.solutionService.delete(solution.getSolutionId());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    NavController navController = Navigation.findNavController(getView());
                                    navController.popBackStack();
                                    navController.navigate(R.id.nav_home);
                                } else {
                                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error deleting the solution!");
                                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    errorOkDialog.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Network error!");
                                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                errorOkDialog.show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }
}