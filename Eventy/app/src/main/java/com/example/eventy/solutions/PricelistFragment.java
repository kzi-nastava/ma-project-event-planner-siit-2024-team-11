package com.example.eventy.solutions;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.eventy.R;
import com.example.eventy.adapters.solutions.PricelistAdapter;
import com.example.eventy.common.PagedResponse;
import com.example.eventy.custom.ErrorOkDialog;
import com.example.eventy.databinding.FragmentCategoryHomeBinding;
import com.example.eventy.databinding.FragmentPricelistBinding;
import com.example.eventy.solutions.model.PricelistItem;
import com.example.eventy.utils.ClientUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricelistFragment extends Fragment {

    private FragmentPricelistBinding binding;
    private int pageIndex = 0;
    private int pageSize = 5;
    private int totalPages = 99;
    private boolean isLoading = false;
    private List<PricelistItem> paginatedPricelistItems = new ArrayList<>();
    private PricelistAdapter adapter;


    public PricelistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPricelistBinding.inflate(inflater, container, false);

        adapter = new PricelistAdapter(requireContext(), paginatedPricelistItems);

        setupPaginationControls();
        fetchItems();

        binding.pricelistItemsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.pricelistItemsRecycler.setAdapter(adapter);

        binding.pricelistDownloadButton.setOnClickListener(v -> {
            Call<ResponseBody> call = ClientUtils.solutionService.getPricelistPdf();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null && savePDFToDownloads(response.body(), "pricelist.pdf")) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Pricelist PDF is downloading")
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
                    ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Connection error!", "Connection error. Please try again later.");
                    errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    errorOkDialog.show();
                }
            });
        });

        return binding.getRoot();
    }

    private void fetchItems() {
        if (isLoading) return;
        isLoading = true;
        Call<PagedResponse<PricelistItem>> call = ClientUtils.solutionService.getPricelist(pageIndex, pageSize);
        call.enqueue(new Callback<PagedResponse<PricelistItem>>() {
            @Override
            public void onResponse(Call<PagedResponse<PricelistItem>> call, Response<PagedResponse<PricelistItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<PricelistItem> pagedResponse = response.body();

                    paginatedPricelistItems.clear();
                    paginatedPricelistItems.addAll(pagedResponse.getContent());
                    adapter.notifyDataSetChanged();

                    totalPages = pagedResponse.getTotalPages();
                    updatePaginationControls();
                    isLoading = false;
                } else {
                    if (getActivity() != null) {
                        ErrorOkDialog errorOkDialog = new ErrorOkDialog(getActivity(), "Error", "Error while loading pricelist! Try again.");
                        errorOkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        errorOkDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<PricelistItem>> call, Throwable t) {

            }
        });
    }

    private void updatePaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setEnabled(pageIndex > 0);
        binding.btnNext.setEnabled(pageIndex < totalPages - 1);

        binding.tvPageInfo.setText(String.format("Page %d of %d", pageIndex + 1, totalPages));
    }

    private void setupPaginationControls() {
        if (!isAdded()) {
            return;
        }

        binding.btnPrevious.setOnClickListener(v -> {
            if (pageIndex > 0) {
                pageIndex--;
                fetchItems();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (pageIndex < totalPages - 1) {
                pageIndex++;
                fetchItems();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.page_size_options,
                R.layout.custom_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPageSize.setAdapter(adapter);
        binding.spinnerPageSize.setSelection(2);

        binding.spinnerPageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(position).toString());
                pageIndex = 0;
                fetchItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
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