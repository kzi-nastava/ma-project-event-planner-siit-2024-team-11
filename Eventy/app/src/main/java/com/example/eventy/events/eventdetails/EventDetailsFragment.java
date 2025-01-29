package com.example.eventy.events.eventdetails;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy HH:mm:ss");
                    binding.eventDateText.setText("Date: " + event.getDate().format(formatter));
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
                        binding.favoriteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_favorite_smaller_white));
                    } else {
                        binding.favoriteButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.icon_favorite_smaller));
                    }

                    Toast.makeText(getContext(), (event.getIsFavorite() ? "Favorite: " : "Removed Favorite: ") + event.getName(), Toast.LENGTH_SHORT).show();
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

    private void downloadEventDetails() {
        Call<ResponseBody> call = ClientUtils.eventService.triggerEventDetailsPDFDownload(event.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null && savePDFToDownloads(response.body(), event.getName() + "EventDetails.pdf")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Event Details PDF is downloading")
                            .setMessage("Please check your downloads folder!")
                            .setIcon(R.drawable.icon_success_png)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }})
                            .show();
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error while downloading", "Error while downloading event details. Please try again later.");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error while downloading", "Error while downloading event details. Please try again later.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }

    private void downloadGuestList() {
        Call<ResponseBody> call = ClientUtils.eventService.triggerEventGuestListPDFDownload(event.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null && savePDFToDownloads(response.body(), event.getName() + "GuestList.pdf")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Guest List PDF is downloading")
                            .setMessage("Please check your downloads folder!")
                            .setIcon(R.drawable.icon_success_png)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }})
                            .show();
                } else {
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error while downloading", "Error while downloading event details. Please try again later.");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error while downloading", "Error while downloading event details. Please try again later.");
                errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                errorOkDialog.show();
            }
        });
    }

    private boolean savePDFToDownloads(ResponseBody body, String fileName) {
        try {
            Context context = requireContext();
            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                outputStream = context.getContentResolver().openOutputStream(
                        context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                );
            } else {
                // For Android 9 and below, use External Storage (Requires Permission)
                File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                outputStream = new FileOutputStream(pdfFile);
            }

            if (outputStream != null) {
                outputStream.write(body.bytes());
                outputStream.close();
                Log.d("PDF", "File saved successfully!");
                return true;
            }
        } catch (Exception e) {
            Log.e("PDF", "Error saving PDF: " + e.getMessage());
        }
        return false;
    }
}