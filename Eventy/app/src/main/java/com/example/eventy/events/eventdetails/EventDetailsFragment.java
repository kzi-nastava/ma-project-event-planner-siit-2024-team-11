package com.example.eventy.events.eventdetails;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventy.R;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentEventDetailsBinding;
import com.example.eventy.events.model.CreateActivity;
import com.example.eventy.events.organizeevent.ActivityTableAdapter;
import com.example.eventy.users.model.EventDetails;
import com.example.eventy.utils.ClientUtils;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {

    private FragmentEventDetailsBinding binding;

    private EventDetails event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Long typeId = getArguments().getLong("EventID");

        Call<EventDetails> call = ClientUtils.eventService.getEvent(typeId);
        call.enqueue(new Callback<EventDetails>() {
            @Override
            public void onResponse(Call<EventDetails> call, Response<EventDetails> response) {
                if(response.isSuccessful() && response.body() != null) {
                    event = response.body();

                    binding.eventNameText.setText(event.getName());
                    binding.eventDescriptionText.setText(event.getDescription());
                    binding.eventOrganizerText.setText("Organizer: " + event.getOrganizerName());
                    binding.eventDateText.setText("Date: " + event.getDate().toString());
                    binding.eventLocationText.setText("Location: " + event.getLocation().getAddress());

                    OnlineTileSourceBase cartoTileSource = new OnlineTileSourceBase(
                            "CartoDB",
                            0,
                            19,
                            256,
                            ".png",
                            new String[]{
                                    "https://a.basemaps.cartocdn.com/light_all/",
                                    "https://b.basemaps.cartocdn.com/light_all/",
                                    "https://c.basemaps.cartocdn.com/light_all/",
                                    "https://d.basemaps.cartocdn.com/light_all/"
                            }) {

                        @Override
                        public String getTileURLString(long pMapTileIndex) {
                            int zoomLevel = MapTileIndex.getZoom(pMapTileIndex);
                            int xTile = MapTileIndex.getX(pMapTileIndex);
                            int yTile = MapTileIndex.getY(pMapTileIndex);

                            String[] subdomains = {"a", "b", "c", "d"};
                            String subdomain = subdomains[(int) (Math.random() * subdomains.length)];

                            return String.format("https://%s.basemaps.cartocdn.com/light_all/%d/%d/%d.png", subdomain, zoomLevel, xTile, yTile);
                        }
                    };

                    binding.mapview.setTileSource(cartoTileSource);
                    binding.mapview.invalidate(); // Refresh the map to load tiles
                    binding.mapview.setMultiTouchControls(true);


                    IMapController mapController = binding.mapview.getController();
                    mapController.setCenter(new GeoPoint(event.getLocation().getLatitude(), event.getLocation().getLongitude()));
                    mapController.setZoom(15.0);

                    if (event.getIsFavorite()) {
                        binding.favoriteButton.setBackgroundColor(Color.parseColor("#929AB7"));
                        binding.favoriteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_favorite_smaller_white));
                    }

                    binding.favoriteButton.setOnClickListener(v -> toggleFavButton());
                    binding.downloadEventDetailsButton.setOnClickListener(v -> downloadEventDetails());
                    binding.downloadGuestListButton.setOnClickListener(v -> downloadGuestList());

                    RecyclerView recyclerView = binding.recyclerView;
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                    ActivityTableAdapter adapter = new ActivityTableAdapter(event.getAgenda().stream().map(CreateActivity::new).collect(Collectors.toList()));
                    recyclerView.setAdapter(adapter);
                }
                else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading the event.");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<EventDetails> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading the event.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void toggleFavButton() {
        Call<Void> call2 = ClientUtils.eventService.toggleFavoriteEvent(event.getId());
        call2.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    event.setIsFavorite(!event.getIsFavorite());

                    if (event.getIsFavorite()) {
                        binding.favoriteButton.setBackgroundColor(Color.parseColor("#929AB7"));
                        binding.favoriteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_favorite_smaller_white));
                    } else {
                        binding.favoriteButton.setBackgroundColor(Color.parseColor("#ffffff"));
                        binding.favoriteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_favorite_smaller));
                    }

                    Toast.makeText(getContext(), (event.getIsFavorite() ? "Favorite: " : "Remove Favorite: ") + event.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Please log in to make this your favorite event.");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Please log in to make this your favorite event.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }

    private byte[] pdfData; // Store PDF data
    private String pdfFileName; // Store file name

    private void downloadEventDetails() {
        Call<byte[]> call = ClientUtils.eventService.triggerEventDetailsPDFDownload(event.getId());
        call.enqueue(new Callback<byte[]>() {
            @Override
            public void onResponse(Call<byte[]> call, Response<byte[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pdfData = response.body();
                    pdfFileName = event.getName() + "EventDetails.pdf";

                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    else {
                        savePDFFile(pdfData, pdfFileName);
                    }
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error while downloading", "Error while downloading event details. Please try again later.");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<byte[]> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error while downloading", "Error while downloading event details. Please try again later.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }

    private void downloadGuestList() {
        Call<byte[]> call = ClientUtils.eventService.triggerEventGuestListPDFDownload(event.getId());
        call.enqueue(new Callback<byte[]>() {
            @Override
            public void onResponse(Call<byte[]> call, Response<byte[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pdfData = response.body();
                    pdfFileName = event.getName() + "GuestList.pdf";

                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    else {
                        savePDFFile(pdfData, pdfFileName);
                    }
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error while downloading", "Error while downloading event details. Please try again later.");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<byte[]> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error while downloading", "Error while downloading event details. Please try again later.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }

    private void savePDFFile(byte[] pdfData, String fileName) {
        try {
            // Get external files directory
            File pdfFile = new File(requireContext().getExternalFilesDir(null), fileName);

            // Write byte array to the file
            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write(pdfData);
            fos.close();

            // Optionally, open the file
            openPDF(pdfFile);
        } catch (IOException ignored) {

        }
    }

    private void openPDF(File pdfFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No PDF viewer installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    savePDFFile(pdfData, pdfFileName);
                }
            });
}